package org.example.view.mainStages;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import org.example.view.templates.BaseView;

public class EmployeesView extends BaseView {
    @Override
    protected void setContent() {
        BorderPane root = new BorderPane();
        buildSidebar(root);
        root.setCenter(buildContentTitle("Employees"));
        scene = new Scene(root);
        buildContentTitle("Employees");
        stage.setTitle("App Manager - Employees");
    }

    @Override
    protected void setStyle() {

    }

    @Override
    protected void setLogic() {

    }
}
