package hr.salonmanager.repository.jdbc;

import hr.salonmanager.model.Client;
import hr.salonmanager.repository.ClientRepository;
import hr.salonmanager.repository.Database;
import hr.salonmanager.repository.RepositoryException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementacija ClientRepository sučelja. */
public class JdbcClientRepository implements ClientRepository {
    private final Database database;

    public JdbcClientRepository(Database database) {
        this.database = database;
    }

    @Override
    public Client save(Client client) {
        return database.withConnection(connection -> {
            String sql = "INSERT INTO client (name, phone, email) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, client.getName());
                statement.setString(2, client.getPhone());
                statement.setString(3, client.getEmail());
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) client.setId(keys.getInt(1));
                }
                return client;
            }
        });
    }

    @Override
    public Client update(Client client) {
        return database.withConnection(connection -> {
            String sql = "UPDATE client SET name = ?, phone = ?, email = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, client.getName());
                statement.setString(2, client.getPhone());
                statement.setString(3, client.getEmail());
                statement.setInt(4, client.getId());
                statement.executeUpdate();
                return client;
            }
        });
    }

    @Override
    public Optional<Client> findById(int id) {
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, name, phone, email FROM client WHERE id = ?")) {
                statement.setInt(1, id);
                try (ResultSet result = statement.executeQuery()) {
                    return result.next() ? Optional.of(map(result)) : Optional.empty();
                }
            }
        });
    }

    @Override
    public List<Client> findAll() {
        return database.withConnection(connection -> {
            List<Client> clients = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, name, phone, email FROM client ORDER BY name")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) clients.add(map(result));
                }
            }
            return clients;
        });
    }

    @Override
    public void delete(int id) {
        database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM client WHERE id = ?")) {
                statement.setInt(1, id);
                statement.executeUpdate();
                return null;
            } catch (SQLException exception) {
                throw new RepositoryException("Klijent se ne može obrisati jer postoji u terminu.", exception);
            }
        });
    }

    private Client map(ResultSet result) throws SQLException {
        return new Client(result.getInt("id"), result.getString("name"),
                result.getString("phone"), result.getString("email"));
    }
}
