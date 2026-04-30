package org.example.viewModel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.SessionManager;
import org.example.logging.AppLog;
import org.example.model.database.entity.Client;
import org.example.model.database.entity.Position;
import org.example.model.database.service.ClientService;
import org.example.model.validation.ClientValidator;

import java.sql.SQLException;
import java.util.List;

public class ClientViewModel {

    private final ObservableList<Client> clients = FXCollections.observableArrayList();
    private final ClientService clientService = new ClientService();

    private final BooleanProperty hasAccess = new SimpleBooleanProperty(
            SessionManager.getInstance().getPosition() == Position.Director ||
            SessionManager.getInstance().getPosition() == Position.Analyst
    );

    public ClientViewModel() {
        reload();
    }

    public ObservableList<Client> getClients() {
        return clients;
    }

    public BooleanProperty hasAccessProperty() {
        return hasAccess;
    }

    public void reload() {
        var logger = AppLog.getLogger(ClientViewModel.class);
        int companyId = SessionManager.getInstance().getCurrentCompanyId();
        try {
            List<Client> list = clientService.getClientsByCompanyId(companyId);
            clients.setAll(list);
        } catch (SQLException e) {
            logger.error("Failed to load clients for companyId={}", companyId, e);
        }
    }

    public void addClient(Client client) throws IllegalArgumentException, SQLException {
        ClientValidator.validate(client);
        clientService.addClient(client);
        clients.add(client);
    }

    public void updateClient(Client client) throws IllegalArgumentException, SQLException {
        ClientValidator.validate(client);
        clientService.updateClient(client);
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getId() == client.getId()) {
                clients.set(i, client);
                break;
            }
        }
    }

    public void deleteClient(int clientId) throws SQLException {
        clientService.deleteClient(clientId);
        clients.removeIf(c -> c.getId() == clientId);
    }
}