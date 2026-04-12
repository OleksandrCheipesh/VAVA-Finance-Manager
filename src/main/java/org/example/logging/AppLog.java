package org.example.logging;

import org.example.SessionManager.SessionStatus;

import java.lang.reflect.Method;
import java.util.UUID;

public final class AppLog {

    public interface SimpleLogger {
        void info(String msg, Object... args);
        void warn(String msg, Object... args);
        void error(String msg, Object... args);
        void debug(String msg, Object... args);
    }

    private AppLog() {}

    public static SimpleLogger getLogger(Class<?> clazz) {
        try {
            Class<?> loggerFactory = Class.forName("org.slf4j.LoggerFactory");
            Method getLogger = loggerFactory.getMethod("getLogger", Class.class);

            Object logger = getLogger.invoke(null, clazz);
            return new ReflectionLogger(logger);
        } catch (Exception e) {
            return new ConsoleLogger(clazz.getSimpleName());
        }
    }

    public static void pushSession(SessionStatus status) {
        if (status == null) return;
        try {
            Class<?> mdc = Class.forName("org.slf4j.MDC");
            Method put = mdc.getMethod("put", String.class, String.class);

            if (status.userId() > 0) put.invoke(null, "userId", String.valueOf(status.userId()));
            if (status.companyId() != null) put.invoke(null, "companyId", String.valueOf(status.companyId()));

            put.invoke(null, "sessionId", UUID.randomUUID().toString());
        } catch (Exception ignored) {}
    }

    public static void clearSession() {
        try {
            Class<?> mdc = Class.forName("org.slf4j.MDC");
            Method remove = mdc.getMethod("remove", String.class);

            remove.invoke(null, "userId");
            remove.invoke(null, "companyId");
            remove.invoke(null, "sessionId");
        } catch (Exception ignored) {}
    }

    private static final class ConsoleLogger implements SimpleLogger {
        private final String name;

        ConsoleLogger(String name) {
            this.name = name;
        }

        public void info(String msg, Object... args) {
            System.out.println(format("INFO", msg, args));
        }

        public void warn(String msg, Object... args) {
            System.out.println(format("WARN", msg, args));
        }

        public void error(String msg, Object... args) {
            System.err.println(format("ERROR", msg, args));
        }

        public void debug(String msg, Object... args) {
            System.out.println(format("DEBUG", msg, args));
        }

        private String format(String level, String msg, Object... args) {
            String formatted = msg;
            if (args != null && args.length > 0) {
                try {
                    formatted = String.format(msg.replace("{}", "%s"), args);
                } catch (Exception ignored) {}
            }
            return String.format("%s [%s] %s - %s", level, Thread.currentThread().getName(), name, formatted);
        }
    }

    private static final class ReflectionLogger implements SimpleLogger {
        private final Object logger;
        private final Method info;
        private final Method warn;
        private final Method error;
        private final Method debug;

        ReflectionLogger(Object logger) throws Exception {
            this.logger = logger;
            Class<?> loggerClass = logger.getClass();

            this.info = findMethod(loggerClass, "info");
            this.warn = findMethod(loggerClass, "warn");
            this.error = findMethod(loggerClass, "error");
            this.debug = findMethod(loggerClass, "debug");
        }

        private Method findMethod(Class<?> cls, String name) throws NoSuchMethodException {
                    try {
                        return cls.getMethod(name, String.class, Object.class, Object.class);
                    } catch (NoSuchMethodException _) {}

                    try {
                        return cls.getMethod(name, String.class, Object.class, Throwable.class);
                    } catch (NoSuchMethodException _) {}

                    try {
                        return cls.getMethod(name, String.class, Object.class);
                    } catch (NoSuchMethodException _) {}

                    try {
                        return cls.getMethod(name, String.class, Throwable.class);
                    } catch (NoSuchMethodException _) {}

                    return cls.getMethod(name, String.class, Object[].class);
        }

        public void info(String msg, Object... args) {
            invoke(info, msg, args);
        }

        public void warn(String msg, Object... args) {
            invoke(warn, msg, args);
        }

        public void error(String msg, Object... args) {
            invoke(error, msg, args);
        }

        public void debug(String msg, Object... args) {
            invoke(debug, msg, args);
        }

        private void invoke(Method m, String msg, Object... args) {
            try {
                int params = m.getParameterCount();
                Class<?>[] types = m.getParameterTypes();
                if (params == 3) {
                    if (types[2] == Throwable.class && args.length >= 2 && args[args.length-1] instanceof Throwable) {
                        Object[] p = new Object[] { msg, args[0], args[args.length-1] };
                        m.invoke(logger, p);
                    } else if (types[1] == Object.class && args.length >= 2) {
                        Object[] p = new Object[] { msg, args[0], args[1] };
                        m.invoke(logger, p);
                    } else {
                        m.invoke(logger, new Object[] { msg, args });
                    }

                } else if (params == 2) {
                    if (types[1] == Object.class && args.length >= 1) {
                        m.invoke(logger, msg, args[0]);
                    } else if (types[1] == Throwable.class && args.length >= 1 && args[args.length-1] instanceof Throwable) {
                        m.invoke(logger, msg, args[args.length-1]);
                    } else if (types[1] == Object[].class) {
                        m.invoke(logger, new Object[] { msg, args });
                    } else {
                        m.invoke(logger, new Object[] { msg, args });
                    }
                } else {
                    m.invoke(logger, new Object[] { msg, args });
                }
            } catch (Exception ignored) {}
        }
    }
}


