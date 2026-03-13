package org.example.view.templates;

public class Themes {
    // Extract all colors and style parameters from this class into static constant !!!

    // Brand Colors
    public static final String PRIMARY = "#2BD2C6";
    public static final String PRIMARY_DARK = "#16B8AD";
    public static final String PRIMARY_OVERLAY = "rgba(22, 184, 173, 0.85)";
    public static final String PRIMARY_GRADIENT = "linear-gradient(to bottom right, rgba(86, 212, 208, 0.85) 0%, rgba(7, 155, 151, 0.85) 93%)";
    public static final String LOGO_CIRCLE = "#0E9B92";

    // Background Colors
    public static final String BG_RIGHT = "#F4FAFA"; // Login / Register right panel
    public static final String BG_FIELD = "#DDF0EF"; // Login / Register input fields
    public static final String BG_FIELD_LARGE = "#D4EDEC"; // CreateCompany input fields
    public static final String BG_FIELD_HOVER = "#C4E5E3"; // CreateCompany field hover

    // Border Colors
    public static final String BORDER_FIELD = "#B0D6D3"; // Login / Register field border
    public static final String BORDER_LARGE = "#6B8A89";  // CreateCompany field border

    // Text Colors
    public static final String TEXT_PRIMARY = "#6B8A89"; // Main text / labels (CreateCompany)
    public static final String TEXT_SUBTITLE = "#7f9e9c"; // Subtitle under form title
    public static final String TEXT_SUCCESS = "#10B981"; // Success message
    public static final String TEXT_ERROR = "#E74C3C"; // Error message / border

    // Button Colors
    public static final String BTN_PRIMARY = "#56D4D0"; // Large register button (CreateCompany)
    public static final String BTN_PRIMARY_HOVER = "#4CB8B5"; // Hover state

    // Layout
    public static final double PANEL_WIDTH = 400;
    public static final double FORM_MAX_WIDTH = 300;

    // Font Sizes
    public static final int FONT_FIELD = 16;
    public static final int FONT_SECTION_TITLE = 20;
    public static final int FONT_LABEL = 14;
    public static final int FONT_LOGO_LABEL = 16;
    public static final int FONT_BTN_LARGE = 20;
    public static final int FONT_ERROR_LARGE = 14;

    // Large Field Sizing (CreateCompany)
    public static final int    FIELD_PADDING    = 10;
    public static final double FIELD_HEIGHT     = 48;
    public static final double FIELD_SPLIT_W    = 300;

    public static final String BTN_SECONDARY = "#B0B7C3";
    public static final String BTN_SECONDARY_HOVER = "#8F98A8";
    public static final String BTN_DANGER = "#E74C3C";
    public static final String BTN_DANGER_HOVER = "#C0392B";

    public static final String STATE_FOCUS_RING = "#56D4D0";
    public static final String STATE_ERROR_RING = "#E74C3C";
    public static final String STATE_DISABLED_OPACITY = "0.5";

    public static final String BG_DASHBOARD = "#F4F7F6"; // The light background behind everything
    public static final String BG_CARD = "#FFFFFF"; // White table/modal background
    public static final String BORDER_LIGHT = "#E5E7EB"; // Thin borders for tables and inputs
    public static final String TEXT_MUTED = "#9CA3AF"; // Gray text for placeholders and headers
    public static final String TEXT_DARK = "#111827"; // Dark text for data
}