package org.example.view.mainStages;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import org.example.view.templates.BaseView;

public class ReportsView extends BaseView {

    @Override
    protected void setContent() {
        BorderPane root = new BorderPane();
        buildSidebar(root);
        root.setCenter(buildContentTitle("Reports"));
        scene = new Scene(root);
        buildContentTitle("Reports");
        stage.setTitle("App Manager - Reports");
    }

    @Override
    protected void setStyle() {

    }

    @Override
    protected void setLogic() {

    }
}
