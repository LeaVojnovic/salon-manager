package hr.salonmanager.repository.jdbc;

import hr.salonmanager.model.SalonSettings;
import hr.salonmanager.repository.Database;
import hr.salonmanager.repository.RepositoryException;
import hr.salonmanager.repository.SettingsRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Optional;

/** JDBC implementacija SettingsRepository sučelja. */
public class JdbcSettingsRepository implements SettingsRepository {
    private final Database database;

    public JdbcSettingsRepository(Database database) {
        this.database = database;
    }

    @Override
    public SalonSettings save(SalonSettings settings) {
        return database.withConnection(connection -> {
            String update = """
                    UPDATE salon_settings SET salon_name = ?, address = ?, phone = ?, email = ?,
                    opening_time = ?, closing_time = ? WHERE id = ?
                    """;
            try (PreparedStatement statement = connection.prepareStatement(update)) {
                bind(statement, settings);
                int count = statement.executeUpdate();
                if (count == 0) {
                    String insert = """
                            INSERT INTO salon_settings
                            (id, salon_name, address, phone, email, opening_time, closing_time)
                            VALUES (?, ?, ?, ?, ?, ?, ?)
                            """;
                    try (PreparedStatement insertStatement = connection.prepareStatement(insert)) {
                        insertStatement.setInt(1, settings.getId());
                        insertStatement.setString(2, settings.getSalonName());
                        insertStatement.setString(3, settings.getAddress());
                        insertStatement.setString(4, settings.getPhone());
                        insertStatement.setString(5, settings.getEmail());
                        insertStatement.setTime(6, Time.valueOf(settings.getOpeningTime()));
                        insertStatement.setTime(7, Time.valueOf(settings.getClosingTime()));
                        insertStatement.executeUpdate();
                    }
                }
                return settings;
            }
        });
    }

    @Override
    public Optional<SalonSettings> findById(int id) {
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, salon_name, address, phone, email, opening_time, closing_time "
                            + "FROM salon_settings WHERE id = ?")) {
                statement.setInt(1, id);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) return Optional.empty();
                    return Optional.of(new SalonSettings(result.getInt("id"), result.getString("salon_name"),
                            result.getString("address"), result.getString("phone"), result.getString("email"),
                            result.getTime("opening_time").toLocalTime(),
                            result.getTime("closing_time").toLocalTime()));
                }
            }
        });
    }

    private void bind(PreparedStatement statement, SalonSettings settings) throws SQLException {
        statement.setString(1, settings.getSalonName());
        statement.setString(2, settings.getAddress());
        statement.setString(3, settings.getPhone());
        statement.setString(4, settings.getEmail());
        statement.setTime(5, Time.valueOf(settings.getOpeningTime()));
        statement.setTime(6, Time.valueOf(settings.getClosingTime()));
        statement.setInt(7, settings.getId());
    }
}
