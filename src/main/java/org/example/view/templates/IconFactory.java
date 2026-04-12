package org.example.view.templates;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconFactory {

    // Loads the standard black PNG icon
    public static ImageView getIcon(String iconName, double size) {
        // Clean the string just in case .svg or .png was passed in the name
        String cleanName = iconName.replace(".svg", "").replace(".png", "");

        ImageView imageView = new ImageView();
        try {
            Image img = new Image(IconFactory.class.getResourceAsStream("/icons/" + cleanName + ".png"));
            imageView.setImage(img);
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
        } catch (Exception e) {
            System.err.println("Warning: Could not load PNG icon: /icons/" + cleanName + ".png");
        }
        return imageView;
    }

    // Helper method: Loads the PNG and uses an effect to turn it completely white
    // Useful for primary buttons or active sidebar items!
    public static ImageView getWhiteIcon(String iconName, double size) {
        ImageView imageView = getIcon(iconName, size);
        ColorAdjust makeWhite = new ColorAdjust();
        makeWhite.setBrightness(1.0); // 1.0 turns black pixels into pure white
        imageView.setEffect(makeWhite);
        return imageView;
    }
}