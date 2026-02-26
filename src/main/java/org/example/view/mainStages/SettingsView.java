package org.example.view.mainStages;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import org.example.view.templates.BaseView;

public class SettingsView extends BaseView {

    @Override
    protected void setContent() {
        BorderPane root = new BorderPane();
        buildSidebar(root);
        root.setCenter(buildContentTitle("Settings"));
        scene = new Scene(root);
        buildContentTitle("Settings");
        stage.setTitle("App Manager - Settings");
    }

    @Override
    protected void setStyle() {

    }

    @Override
    protected void setLogic() {

    }
}
