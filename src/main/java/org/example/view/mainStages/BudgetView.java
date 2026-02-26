package org.example.view.mainStages;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import org.example.view.templates.BaseView;

public class BudgetView extends BaseView {
    @Override
    protected void setContent() {
        BorderPane root = new BorderPane();
        buildSidebar(root);
        root.setCenter(buildContentTitle("Budget"));
        scene = new Scene(root);
        buildContentTitle("Budget");
        stage.setTitle("App Manager - Dashboard");
    }

    @Override
    protected void setStyle() {

    }

    @Override
    protected void setLogic() {

    }
}
