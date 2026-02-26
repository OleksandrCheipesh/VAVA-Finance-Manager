package org.example.view.mainStages;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import org.example.view.templates.BaseView;

public class ManageCompanyView extends BaseView {
    @Override
    protected void setContent() {
        BorderPane root = new BorderPane();
        buildSidebar(root);
        root.setCenter(buildContentTitle("Manage Company"));
        scene = new Scene(root);
        buildContentTitle("Manage Company");
        stage.setTitle("App Manager - Manage Company");
    }

    @Override
    protected void setStyle() {

    }

    @Override
    protected void setLogic() {

    }
}
