package org.example.view.templates;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.SessionManager;

import java.util.Map;

public class I18n {

    public static final StringProperty language = new SimpleStringProperty(
            SessionManager.getInstance().getLanguage()
    );

    private static final Map<String, String> SLOVAK = Map.ofEntries(
        // ── Sidebar ──────────────────────────────────────────────────────────
        Map.entry("Dashboard",     "Prehľad"),
        Map.entry("Transactions",  "Transakcie"),
        Map.entry("Employees",     "Zamestnanci"),
        Map.entry("Clients",       "Klienti"),
        Map.entry("Projects",      "Projekty"),
        Map.entry("Budget",        "Rozpočet"),
        Map.entry("Reports",       "Správy"),
        Map.entry("Settings",      "Nastavenia"),
        Map.entry("Logout",        "Odhlásiť sa"),

        // ── Common ────────────────────────────────────────────────────────────
        Map.entry("Clear",               "Vymazať"),
        Map.entry("Search by name...",   "Hľadať podľa názvu..."),
        Map.entry("DATE",                "DÁTUM"),
        Map.entry("AMOUNT",              "SUMA"),
        Map.entry("NAME",                "MENO"),
        Map.entry("EMAIL",               "EMAIL"),
        Map.entry("ACTION",              "AKCIA"),
        Map.entry("PROJECT",             "PROJEKT"),
        Map.entry("CLIENT",              "KLIENT"),
        Map.entry("DESCRIPTION",         "POPIS"),
        Map.entry("TYPE",                "TYP"),
        Map.entry("STATUS",              "STAV"),
        Map.entry("Active",              "Aktívny"),
        Map.entry("Sale",                "Predaj"),
        Map.entry("Buy",                 "Nákup"),

        // ── Dashboard ─────────────────────────────────────────────────────────
        Map.entry("Overview of your key performance indicators.",
                  "Prehľad kľúčových výkonnostných ukazovateľov."),
        Map.entry("TOTAL REVENUE",       "CELKOVÉ PRÍJMY"),
        Map.entry("ACTIVE PROJECTS",     "AKTÍVNE PROJEKTY"),
        Map.entry("Revenue vs Projections",              "Príjmy vs. Predpoklady"),
        Map.entry("Quarterly financial performance analysis",
                  "Štvrťročná finančná analýza"),
        Map.entry("Top Projects",        "Najlepšie projekty"),
        Map.entry("Recent Transactions", "Posledné transakcie"),
        Map.entry("View All",            "Zobraziť všetko"),

        // ── Transactions ──────────────────────────────────────────────────────
        Map.entry("Monitor your income, expenses, and financial records.",
                  "Sledujte príjmy, výdavky a finančné záznamy."),
        Map.entry("All",             "Všetky"),
        Map.entry("Sales",           "Predaje"),
        Map.entry("Purchase",        "Nákup"),
        Map.entry("+ Add Transaction",   "+ Pridať transakciu"),
        Map.entry("INCOME",          "PRÍJMY"),
        Map.entry("EXPENSES",        "VÝDAVKY"),
        Map.entry("NET BALANCE",     "ČISTÝ ZOSTATOK"),
        Map.entry("LARGEST",         "NAJVYŠŠIA"),
        Map.entry("No Transactions Yet",               "Žiadne transakcie"),
        Map.entry("Register your first sale or purchase",
                  "Zaregistrujte prvý predaj alebo nákup"),
        Map.entry("Project",         "Projekt"),
        Map.entry("Type",            "Typ"),

        // ── Employees ─────────────────────────────────────────────────────────
        Map.entry("Manage your team members and their roles.",
                  "Spravujte členov tímu a ich úlohy."),
        Map.entry("All Staff",       "Všetci zamestnanci"),
        Map.entry("Inactive",        "Neaktívni"),
        Map.entry("Contractors",     "Dodávatelia"),
        Map.entry("+ Add Employee",  "+ Pridať zamestnanca"),
        Map.entry("TOTAL EMPLOYEES", "ZAMESTNANCOV CELKOM"),
        Map.entry("ACTIVE",          "AKTÍVNI"),
        Map.entry("ONBOARDING",      "NÁSTUP"),
        Map.entry("No Employees Yet",                  "Žiadni zamestnanci"),
        Map.entry("Register your first employee",      "Zaregistrujte prvého zamestnanca"),
        Map.entry("ROLE",            "ROLA"),
        Map.entry("DEPARTMENT",      "ODDELENIE"),
        Map.entry("Search by name, role...",           "Hľadať podľa mena, roly..."),
        Map.entry("Department",      "Oddelenie"),
        Map.entry("Filter Date",     "Filtrovať dátum"),

        // ── Clients ───────────────────────────────────────────────────────────
        Map.entry("Manage your clients and their profiles.",
                  "Spravujte svojich klientov a ich profily."),
        Map.entry("+ Add Client",        "+ Pridať klienta"),
        Map.entry("No Clients Yet",      "Žiadni klienti"),
        Map.entry("Register your first client",        "Zaregistrujte prvého klienta"),
        Map.entry("MONTHLY INCOME",      "MESAČNÝ PRÍJEM"),
        Map.entry("MEMBER SINCE",        "ČLEN OD"),
        Map.entry("TOTAL CLIENTS",       "KLIENTOV CELKOM"),
        Map.entry("registered clients",  "registrovaných klientov"),
        Map.entry("TOTAL MONTHLY INCOME",    "CELKOVÝ MESAČNÝ PRÍJEM"),
        Map.entry("combined monthly",        "mesačne spolu"),
        Map.entry("AVERAGE MONTHLY INCOME",  "PRIEMERNÝ MESAČNÝ PRÍJEM"),
        Map.entry("per client avg.",         "priemer na klienta"),
        Map.entry("PHONE",                "TELEFÓN"),
        Map.entry("Search by name, email, phone...",
                  "Hľadať podľa mena, e-mailu, telefónu..."),
        Map.entry("Income (default)", "Príjem (predvolený)"),
        Map.entry("Income ↑",         "Príjem ↑"),
        Map.entry("Income ↓",         "Príjem ↓"),
        Map.entry("Date (default)",   "Dátum (predvolený)"),
        Map.entry("Newest first",     "Najnovšie"),
        Map.entry("Oldest first",     "Najstaršie"),

        // ── Projects ──────────────────────────────────────────────────────────
        Map.entry("Monitoring capital allocation and architectural milestones.",
                  "Sledovanie rozdelenia kapitálu a míľnikov."),
        Map.entry("+ Add New Project", "+ Pridať nový projekt"),
        Map.entry("Active Projects",   "Aktívne projekty"),
        Map.entry("TOTAL BUDGET",      "CELKOVÝ ROZPOČET"),
        Map.entry("ACTIVE SPRINT",     "AKTÍVNY ŠPRINT"),

        // ── Budget ────────────────────────────────────────────────────────────
        Map.entry("Manage and track your company spending.",
                  "Spravujte a sledujte výdavky spoločnosti."),
        Map.entry("TOTAL ASSETS",      "CELKOVÉ AKTÍVA"),
        Map.entry("TOTAL LIABILITIES", "CELKOVÉ PASÍVA"),
        Map.entry("NET POSITION",      "ČISTÁ POZÍCIA"),
        Map.entry("Accounts Overview", "Prehľad účtov"),
        Map.entry("Manage your operational and reserve funds",
                  "Spravujte prevádzkové a rezervné fondy"),
        Map.entry("Search accounts...", "Hľadať účty..."),
        Map.entry("+ Add New Account",  "+ Pridať nový účet"),
        Map.entry("No accounts found",  "Žiadne účty"),
        Map.entry("Available for allocation", "Dostupné na alokáciu"),
        Map.entry("Deficit",           "Deficit"),

        // ── Reports ───────────────────────────────────────────────────────────
        Map.entry("Financial overview · FY 2024",  "Finančný prehľad · FY 2024"),
        Map.entry("PROJECT PROFIT DISTRIBUTION",   "ROZDELENIE ZISKU PROJEKTOV"),
        Map.entry("INCOME VS EXPENSES",            "PRÍJMY VS VÝDAVKY"),
        Map.entry("NET PROFIT TREND",              "TREND ČISTÉHO ZISKU"),
        Map.entry("GROSS INCOME",                  "HRUBÝ PRÍJEM"),
        Map.entry("NET EXPENSE",                   "ČISTÉ NÁKLADY"),

        // ── Settings ─────────────────────────────────────────────────────────
        Map.entry("Configure your organization's parameters and customize your portal experience.",
                  "Nakonfigurujte parametre organizácie a prispôsobte si portál."),
        Map.entry("Company Profile",   "Profil spoločnosti"),
        Map.entry("Save Changes",      "Uložiť zmeny"),
        Map.entry("COMPANY NAME",      "NÁZOV SPOLOČNOSTI"),
        Map.entry("COUNTRY",           "KRAJINA"),
        Map.entry("CURRENCY",          "MENA"),
        Map.entry("INDUSTRY",          "ODVETVIE"),
        Map.entry("Preferences",       "Predvoľby"),
        Map.entry("LANGUAGE",          "JAZYK"),
        Map.entry("English",           "Angličtina"),
        Map.entry("Slovak",            "Slovenčina"),
        Map.entry("DATE FORMAT",       "FORMÁT DÁTUMU"),
        Map.entry("Apply Settings",    "Použiť nastavenia"),
        Map.entry("User Management",   "Správa používateľov"),
        Map.entry("+ Add User",        "+ Pridať používateľa"),
        Map.entry("SURNAME",           "PRIEZVISKO"),
        Map.entry("POSITION",          "POZÍCIA"),
        Map.entry("Protected",         "Chránené"),
        Map.entry("Security",          "Bezpečnosť"),
        Map.entry("CURRENT PASSWORD",  "AKTUÁLNE HESLO"),
        Map.entry("NEW PASSWORD",      "NOVÉ HESLO"),
        Map.entry("CONFIRM PASSWORD",  "POTVRDIŤ HESLO"),
        Map.entry("Save New Password", "Uložiť nové heslo")
    );

    public static String t(String key) {
        if ("Slovak".equals(language.get())) {
            return SLOVAK.getOrDefault(key, key);
        }
        return key;
    }

    /** Updates both SessionManager and all live listeners. */
    public static void setLanguage(String lang) {
        SessionManager.getInstance().setLanguage(lang);
        language.set(lang);
    }
}
