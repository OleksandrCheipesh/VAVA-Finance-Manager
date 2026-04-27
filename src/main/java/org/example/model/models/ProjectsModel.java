package org.example.model.models;

import org.example.SessionManager;
import org.example.model.database.entity.Position;

public class ProjectsModel {

    public boolean hasAccess() {
        return SessionManager.getInstance().getPosition() == Position.Director;
    }
}