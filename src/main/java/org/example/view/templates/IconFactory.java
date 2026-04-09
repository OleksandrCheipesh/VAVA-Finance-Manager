package org.example.view.templates;

import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class IconFactory {

    public static SVGPath getIcon(String iconName, String colorHex, double size) {
        SVGPath icon = new SVGPath();

        // These specific styling rules make standard modern SVGs render perfectly in JavaFX
        icon.setFill(Color.TRANSPARENT);
        icon.setStroke(Color.web(colorHex));
        icon.setStrokeWidth(2);
        icon.setStrokeLineCap(StrokeLineCap.ROUND);
        icon.setStrokeLineJoin(StrokeLineJoin.ROUND);

        // Mathematical paths extracted from your .svg files
        String path = switch (iconName) {
            case "layout-dashboard" -> "M3 3h7v7H3z M14 3h7v7h-7z M14 14h7v7h-7z M3 14h7v7H3z";
            case "receipt" -> "M4 2v20l2-1 2 1 2-1 2 1 2-1 2 1 2-1 2 1V2H4z M16 8H8 M16 12H8 M10 16H8";
            case "users-round" -> "M18 21a8 8 0 0 0-16 0 M22 21a10 10 0 0 0-7.3-9.15 M10 13a5 5 0 1 0 0-10 5 5 0 0 0 0 10z M22 13a4 4 0 1 0 0-8 4 4 0 0 0 0 8z";
            case "folder" -> "M4 20h16a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2h-7.93a2 2 0 0 1-1.66-.9l-.82-1.2A2 2 0 0 0 7.93 3H4a2 2 0 0 0-2 2v13c0 1.1.9 2 2 2Z";
            case "wallet" -> "M21 12V7H5a2 2 0 0 1 0-4h14v4 M3 5v14a2 2 0 0 0 2 2h16v-5 M18 12a2 2 0 0 0 0 4h4v-4Z";
            case "chart-column-big" -> "M3 3v18h18 M18 17V9 M13 17V5 M8 17v-3";
            case "settings" -> "M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6z";
            case "square-pen" -> "M12 3H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7 M18.375 2.625a2.121 2.121 0 1 1 3 3L12 15l-4 1 1-4Z";
            case "trash" -> "M3 6h18 M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6 M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2 M10 11v6 M14 11v6";
            case "calendar" -> "M8 2v4 M16 2v4 M3 10h18 M5 4h14c1.1 0 2 .9 2 2v14c0 1.1-.9 2-2 2H5c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z";
            case "circle-check" -> "M22 11.08V12a10 10 0 1 1-5.93-9.14 M22 4L12 14.01l-3-3";
            case "circle-x" -> "M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10z M15 9l-6 6 M9 9l6 6";
            default -> "";
        };

        icon.setContent(path);

        // Standard SVGs are drawn on a 24x24 canvas. We scale them to your requested size.
        double scale = size / 24.0;
        icon.setScaleX(scale);
        icon.setScaleY(scale);

        return icon;
    }
}