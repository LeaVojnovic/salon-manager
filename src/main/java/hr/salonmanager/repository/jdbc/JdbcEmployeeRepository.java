package hr.salonmanager.repository.jdbc;

import hr.salonmanager.model.Employee;
import hr.salonmanager.repository.Database;
import hr.salonmanager.repository.EmployeeRepository;
import hr.salonmanager.repository.RepositoryException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementacija EmployeeRepository sučelja. */
public class JdbcEmployeeRepository implements EmployeeRepository {
    private final Database database;

    public JdbcEmployeeRepository(Database database) {
        this.database = database;
    }

    @Override
    public Employee save(Employee employee) {
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO employee (name, phone, email) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, employee.getName());
                statement.setString(2, employee.getPhone());
                statement.setString(3, employee.getEmail());
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) employee.setId(keys.getInt(1));
                }
                return employee;
            }
        });
    }

    @Override
    public Employee update(Employee employee) {
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE employee SET name = ?, phone = ?, email = ? WHERE id = ?")) {
                statement.setString(1, employee.getName());
                statement.setString(2, employee.getPhone());
                statement.setString(3, employee.getEmail());
                statement.setInt(4, employee.getId());
                statement.executeUpdate();
                return employee;
            }
        });
    }

    @Override
    public Optional<Employee> findById(int id) {
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, name, phone, email FROM employee WHERE id = ?")) {
                statement.setInt(1, id);
                try (ResultSet result = statement.executeQuery()) {
                    return result.next() ? Optional.of(map(result)) : Optional.empty();
                }
            }
        });
    }

    @Override
    public List<Employee> findAll() {
        return database.withConnection(connection -> {
            List<Employee> employees = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, name, phone, email FROM employee ORDER BY name")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) employees.add(map(result));
                }
            }
            return employees;
        });
    }

    @Override
    public void delete(int id) {
        database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM employee WHERE id = ?")) {
                statement.setInt(1, id);
                statement.executeUpdate();
                return null;
            } catch (SQLException exception) {
                throw new RepositoryException("Djelatnik se ne može obrisati jer postoji u terminu.", exception);
            }
        });
    }

    private Employee map(ResultSet result) throws SQLException {
        return new Employee(result.getInt("id"), result.getString("name"),
                result.getString("phone"), result.getString("email"));
    }
}
