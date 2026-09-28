package hr.salonmanager.repository.jdbc;

import hr.salonmanager.model.Service;
import hr.salonmanager.repository.Database;
import hr.salonmanager.repository.RepositoryException;
import hr.salonmanager.repository.ServiceRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementacija ServiceRepository sučelja. */
public class JdbcServiceRepository implements ServiceRepository {
    private final Database database;

    public JdbcServiceRepository(Database database) {
        this.database = database;
    }

    @Override
    public Service save(Service service) {
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO service (name, price, duration_minutes) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, service.getName());
                statement.setDouble(2, service.getPrice());
                statement.setInt(3, service.getDurationMinutes());
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) service.setId(keys.getInt(1));
                }
                return service;
            }
        });
    }

    @Override
    public Service update(Service service) {
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE service SET name = ?, price = ?, duration_minutes = ? WHERE id = ?")) {
                statement.setString(1, service.getName());
                statement.setDouble(2, service.getPrice());
                statement.setInt(3, service.getDurationMinutes());
                statement.setInt(4, service.getId());
                statement.executeUpdate();
                return service;
            }
        });
    }

    @Override
    public Optional<Service> findById(int id) {
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, name, price, duration_minutes FROM service WHERE id = ?")) {
                statement.setInt(1, id);
                try (ResultSet result = statement.executeQuery()) {
                    return result.next() ? Optional.of(map(result)) : Optional.empty();
                }
            }
        });
    }

    @Override
    public List<Service> findAll() {
        return database.withConnection(connection -> {
            List<Service> services = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, name, price, duration_minutes FROM service ORDER BY name")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) services.add(map(result));
                }
            }
            return services;
        });
    }

    @Override
    public void delete(int id) {
        database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM service WHERE id = ?")) {
                statement.setInt(1, id);
                statement.executeUpdate();
                return null;
            } catch (SQLException exception) {
                throw new RepositoryException("Usluga se ne može obrisati jer postoji u terminu.", exception);
            }
        });
    }

    private Service map(ResultSet result) throws SQLException {
        return new Service(result.getInt("id"), result.getString("name"),
                result.getDouble("price"), result.getInt("duration_minutes"));
    }
}
