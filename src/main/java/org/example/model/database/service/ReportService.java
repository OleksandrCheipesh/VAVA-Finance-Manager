package org.example.model.database.service;

import org.example.logging.AppLog;
import org.example.model.database.ConnectionProvider;
import org.example.model.database.entity.Employee;
import org.example.model.reports.ExpenseCategoryDTO;
import org.example.model.reports.IncomeBreakdownDTO;
import org.example.model.reports.MonthlySnapshotDTO;
import org.example.model.reports.ProjectSummaryDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    public List<ProjectSummaryDTO> getProjectSummaries(int companyId, LocalDate dateFrom, LocalDate dateTo, Integer projectId, String projectStatus) throws SQLException {
        var logger = AppLog.getLogger(ReportService.class);
        LocalDate prevFrom = dateFrom.minusMonths(1);
        LocalDate prevTo = dateTo.minusMonths(1);

        String sql = "WITH params AS (" +
                " SELECT ?::int AS company_id, ?::date AS date_from, ?::date AS date_to, ?::date AS prev_from, ?::date AS prev_to, ?::int AS project_id, ?::text AS project_status) " +
                " , t_filtered AS (" +
                "   SELECT t.* FROM transactions t, params p WHERE t.company_id = p.company_id AND (t.date BETWEEN p.date_from AND p.date_to OR t.date BETWEEN p.prev_from AND p.prev_to) AND (p.project_id IS NULL OR t.project_id = p.project_id)" +
                " )" +
                " SELECT p.id AS project_id, p.name AS project_name, " +
                " COALESCE(SUM(CASE WHEN tf.type = 'SALE' AND tf.date BETWEEN params.date_from AND params.date_to THEN tf.amount ELSE 0 END), 0) AS total_income, " +
                " COALESCE(SUM(CASE WHEN tf.type = 'PURCHASE' AND tf.date BETWEEN params.date_from AND params.date_to THEN tf.amount ELSE 0 END), 0) AS total_expense, " +
                " COALESCE(SUM(CASE WHEN tf.type = 'SALE' AND tf.date BETWEEN params.prev_from AND params.prev_to THEN tf.amount ELSE 0 END), 0) AS prev_income, " +
                " COALESCE(SUM(CASE WHEN tf.type = 'PURCHASE' AND tf.date BETWEEN params.prev_from AND params.prev_to THEN tf.amount ELSE 0 END), 0) AS prev_expense " +
                " FROM projects p CROSS JOIN params LEFT JOIN t_filtered tf ON p.id = tf.project_id " +
                " WHERE p.company_id = params.company_id AND (params.project_id IS NULL OR p.id = params.project_id) AND (params.project_status IS NULL OR (params.project_status = 'Active' AND p.is_active = true) OR (params.project_status = 'Inactive' AND p.is_active = false)) " +
                " GROUP BY p.id, p.name " +
                " ORDER BY (COALESCE(SUM(CASE WHEN tf.type = 'SALE' AND tf.date BETWEEN params.date_from AND params.date_to THEN tf.amount ELSE 0 END),0) - COALESCE(SUM(CASE WHEN tf.type = 'PURCHASE' AND tf.date BETWEEN params.date_from AND params.date_to THEN tf.amount ELSE 0 END),0)) DESC";

        List<ProjectSummaryDTO> list = new ArrayList<>();
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, companyId);
            ps.setDate(2, Date.valueOf(dateFrom));
            ps.setDate(3, Date.valueOf(dateTo));
            ps.setDate(4, Date.valueOf(prevFrom));
            ps.setDate(5, Date.valueOf(prevTo));
            if (projectId != null) ps.setInt(6, projectId); else ps.setNull(6, Types.INTEGER);
            if (projectStatus != null) ps.setString(7, projectStatus); else ps.setNull(7, Types.VARCHAR);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProjectSummaryRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to load project summaries for companyId={} dateFrom={} dateTo={} projectId={}", companyId, dateFrom, dateTo, projectId, e);
            throw e;
        }
        return list;
    }

    public List<MonthlySnapshotDTO> getMonthlySnapshots(int companyId, LocalDate dateFrom, LocalDate dateTo, Integer projectId, String projectStatus) throws SQLException {
        var logger = AppLog.getLogger(ReportService.class);

        String sql = "WITH params AS (SELECT ?::int AS company_id, ?::date AS date_from, ?::date AS date_to, ?::int AS project_id, ?::text AS project_status), " +
                "t_filtered AS (SELECT t.* FROM transactions t, params p WHERE t.company_id = p.company_id AND t.date BETWEEN p.date_from AND p.date_to AND (p.project_id IS NULL OR t.project_id = p.project_id)) " +
                "SELECT DATE_TRUNC('month', tf.date)::date AS period, " +
                "COALESCE(SUM(CASE WHEN tf.type = 'SALE' THEN tf.amount ELSE 0 END),0) AS income, " +
                "COALESCE(SUM(CASE WHEN tf.type = 'PURCHASE' THEN tf.amount ELSE 0 END),0) AS expense " +
                "FROM t_filtered tf CROSS JOIN params LEFT JOIN projects pr ON pr.id = tf.project_id " +
                "WHERE (params.project_status IS NULL OR (params.project_status = 'Active' AND pr.is_active = true) OR (params.project_status = 'Inactive' AND pr.is_active = false)) " +
                "GROUP BY period ORDER BY period ASC";

        List<MonthlySnapshotDTO> list = new ArrayList<>();
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            ps.setDate(2, Date.valueOf(dateFrom));
            ps.setDate(3, Date.valueOf(dateTo));
            if (projectId != null) ps.setInt(4, projectId); else ps.setNull(4, Types.INTEGER);
            if (projectStatus != null) ps.setString(5, projectStatus); else ps.setNull(5, Types.VARCHAR);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMonthlySnapshotRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to load monthly snapshots for companyId={} dateFrom={} dateTo={} projectId={}", companyId, dateFrom, dateTo, projectId, e);
            throw e;
        }
        return list;
    }

    public List<ExpenseCategoryDTO> getExpenseBreakdown(int companyId, LocalDate dateFrom, LocalDate dateTo, Integer projectId) throws SQLException {
        var logger = AppLog.getLogger(ReportService.class);

        String sql = "WITH params AS (SELECT ?::int AS company_id, ?::date AS date_from, ?::date AS date_to, ?::int AS project_id), " +
                "t_filtered AS (SELECT t.* FROM transactions t, params p WHERE t.company_id = p.company_id AND t.type = 'PURCHASE' AND t.date BETWEEN p.date_from AND p.date_to AND (p.project_id IS NULL OR t.project_id = p.project_id)) " +
                "SELECT COALESCE(NULLIF(TRIM(t_filtered.description), ''), 'Uncategorized') AS category, SUM(t_filtered.amount) AS amount, SUM(SUM(t_filtered.amount)) OVER () AS total " +
                "FROM t_filtered GROUP BY category ORDER BY amount DESC";

        List<ExpenseCategoryDTO> list = new ArrayList<>();
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            ps.setDate(2, Date.valueOf(dateFrom));
            ps.setDate(3, Date.valueOf(dateTo));
            if (projectId != null) ps.setInt(4, projectId); else ps.setNull(4, Types.INTEGER);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BigDecimal amount = rs.getBigDecimal("amount");
                    BigDecimal total = rs.getBigDecimal("total");
                    double pct = 0.0;
                    if (total != null && total.compareTo(BigDecimal.ZERO) > 0) {
                        pct = amount.divide(total, 6, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue();
                    }
                    list.add(new ExpenseCategoryDTO(rs.getString("category"), amount, pct));
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to load expense breakdown for companyId={} dateFrom={} dateTo={} projectId={}", companyId, dateFrom, dateTo, projectId, e);
            throw e;
        }
        return list;
    }

    public List<IncomeBreakdownDTO> getIncomeBreakdown(int companyId, LocalDate dateFrom, LocalDate dateTo, Integer projectId, String projectStatus) throws SQLException {
        var logger = AppLog.getLogger(ReportService.class);

        String sql = "WITH params AS (SELECT ?::int AS company_id, ?::date AS date_from, ?::date AS date_to, ?::int AS project_id, ?::text AS project_status), " +
                "t_filtered AS (SELECT t.* FROM transactions t, params p WHERE t.company_id = p.company_id AND t.type = 'SALE' AND t.date BETWEEN p.date_from AND p.date_to AND (p.project_id IS NULL OR t.project_id = p.project_id)) " +
                "SELECT COALESCE(p.name, 'Unassigned') AS project_name, SUM(t_filtered.amount) AS amount, SUM(SUM(t_filtered.amount)) OVER () AS total " +
                "FROM t_filtered CROSS JOIN params LEFT JOIN projects p ON p.id = t_filtered.project_id " +
                "WHERE (params.project_status IS NULL OR (params.project_status = 'Active' AND p.is_active = true) OR (params.project_status = 'Inactive' AND p.is_active = false)) " +
                "GROUP BY COALESCE(p.name, 'Unassigned') ORDER BY amount DESC";

        List<IncomeBreakdownDTO> list = new ArrayList<>();
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            ps.setDate(2, Date.valueOf(dateFrom));
            ps.setDate(3, Date.valueOf(dateTo));
            if (projectId != null) ps.setInt(4, projectId); else ps.setNull(4, Types.INTEGER);
            if (projectStatus != null) ps.setString(5, projectStatus); else ps.setNull(5, Types.VARCHAR);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BigDecimal amount = rs.getBigDecimal("amount");
                    BigDecimal total = rs.getBigDecimal("total");
                    double pct = 0.0;
                    if (total != null && total.compareTo(BigDecimal.ZERO) > 0) {
                        pct = amount.divide(total, 6, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue();
                    }
                    list.add(new IncomeBreakdownDTO(rs.getString("project_name"), amount, pct));
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to load income breakdown for companyId={} dateFrom={} dateTo={} projectId={}", companyId, dateFrom, dateTo, projectId, e);
            throw e;
        }
        return list;
    }

    public List<Employee> getEmployees(int companyId, Integer projectId) throws SQLException {
        var logger = AppLog.getLogger(ReportService.class);
        List<Employee> list = new ArrayList<>();

        // Detect join table existence (employee_projects)
        boolean hasJoin = false;
        String checkSql = "SELECT to_regclass('public.employee_projects')";
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement checkPs = connection.prepareStatement(checkSql);
             ResultSet checkRs = checkPs.executeQuery()) {
            if (checkRs.next()) hasJoin = checkRs.getString(1) != null;
        }

        String sql;
        if (projectId != null && hasJoin) {
            sql = "SELECT e.* FROM employees e JOIN employee_projects ep ON e.id = ep.employee_id WHERE e.company_id = ? AND ep.project_id = ? ORDER BY e.surname, e.name";
        } else {
            sql = "SELECT * FROM employees WHERE company_id = ? ORDER BY surname, name";
        }

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            if (projectId != null && hasJoin) ps.setInt(2, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEmployeeRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Failed to load employees for companyId={} projectId={}", companyId, projectId, e);
            throw e;
        }
        return list;
    }

    private ProjectSummaryDTO mapProjectSummaryRow(ResultSet rs) throws SQLException {
        int projectId = rs.getInt("project_id");
        String projectName = rs.getString("project_name");
        BigDecimal income = rs.getBigDecimal("total_income");
        BigDecimal expense = rs.getBigDecimal("total_expense");
        BigDecimal prevIncome = rs.getBigDecimal("prev_income");
        BigDecimal prevExpense = rs.getBigDecimal("prev_expense");

        if (income == null) income = BigDecimal.ZERO;
        if (expense == null) expense = BigDecimal.ZERO;
        if (prevIncome == null) prevIncome = BigDecimal.ZERO;
        if (prevExpense == null) prevExpense = BigDecimal.ZERO;

        BigDecimal net = income.subtract(expense);
        BigDecimal prevNet = prevIncome.subtract(prevExpense);

        Double mom = null;
        if (prevNet.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal diff = net.subtract(prevNet);
            mom = diff.divide(prevNet, 6, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue();
        }

        return new ProjectSummaryDTO(projectId, projectName, income, expense, net, mom);
    }

    private MonthlySnapshotDTO mapMonthlySnapshotRow(ResultSet rs) throws SQLException {
        Date periodDate = rs.getDate("period");
        YearMonth ym = YearMonth.from(periodDate.toLocalDate());
        BigDecimal income = rs.getBigDecimal("income");
        BigDecimal expense = rs.getBigDecimal("expense");
        if (income == null) income = BigDecimal.ZERO;
        if (expense == null) expense = BigDecimal.ZERO;
        BigDecimal net = income.subtract(expense);
        return new MonthlySnapshotDTO(ym, income, expense, net);
    }

    private Employee mapEmployeeRow(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setId(rs.getInt("id"));
        e.setCompanyId(rs.getInt("company_id"));
        e.setName(rs.getString("name"));
        e.setSurname(rs.getString("surname"));
        e.setEmail(rs.getString("email"));
        int age = rs.getInt("age");
        e.setAge(rs.wasNull() ? null : age);
        e.setSalary(rs.getBigDecimal("salary"));
        e.setPosition(rs.getString("position"));
        e.setHiredAt(rs.getObject("hired_at", java.time.OffsetDateTime.class));
        e.setStatus(rs.getString("status"));
        return e;
    }
}

