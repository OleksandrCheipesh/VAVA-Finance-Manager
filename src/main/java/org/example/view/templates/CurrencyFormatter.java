package org.example.view.templates;

import org.example.SessionManager;
import java.math.BigDecimal;

public class CurrencyFormatter {
    public static String symbol() {
        return SessionManager.getInstance().getCurrencySymbol();
    }

    public static String format(BigDecimal amount) {
        return symbol() + String.format("%,.2f", amount);
    }

    public static String formatCompact(double amount) {
        if (amount >= 1_000_000) return String.format("%s%.1fM", symbol(), amount / 1_000_000);
        if (amount >= 1_000) return String.format("%s%.1fK", symbol(), amount / 1_000);
        return String.format("%s%.2f", symbol(), amount);
    }
}