package hr.salonmanager.repository.jdbc;

import hr.salonmanager.model.Appointment;
import hr.salonmanager.model.AppointmentStatus;
import hr.salonmanager.model.Client;
import hr.salonmanager.model.Employee;
import hr.salonmanager.model.PaymentType;
import hr.salonmanager.model.Service;
import hr.salonmanager.repository.AppointmentRepository;
import hr.salonmanager.repository.Database;
import hr.salonmanager.repository.RepositoryException;
import hr.salonmanager.repository.TransactionCallback;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementacija AppointmentRepository sučelja. */
public class JdbcAppointmentRepository implements AppointmentRepository {
    private static final String SELECT = """
            SELECT a.id, a.appointment_date, a.start_time, a.price, a.status, a.payment_type,
                   c.id AS client_id, c.name AS client_name, c.phone AS client_phone, c.email AS client_email,
                   e.id AS employee_id, e.name AS employee_name, e.phone AS employee_phone, e.email AS employee_email,
                   s.id AS service_id, s.name AS service_name, s.price AS service_price,
                   s.duration_minutes AS service_duration
            FROM appointments a
            JOIN clients c ON c.id = a.client_id
            JOIN employees e ON e.id = a.employee_id
            JOIN services s ON s.id = a.service_id
            """;

    private final Database database;

    public JdbcAppointmentRepository(Database database) {
        this.database = database;
    }

    @Override
    public Appointment save(Appointment appointment) {
        return database.withConnection(connection -> {
            String sql = """
                    INSERT INTO appointments
                    (client_id, employee_id, service_id, appointment_date, start_time, price, status, payment_type)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                bind(statement, appointment);
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) appointment.setId(keys.getInt(1));
                }
                return appointment;
            }
        });
    }

    @Override
    public Appointment update(Appointment appointment) {
        return database.withConnection(connection -> {
            String sql = """
                    UPDATE appointments SET client_id = ?, employee_id = ?, service_id = ?,
                    appointment_date = ?, start_time = ?, price = ?, status = ?, payment_type = ?
                    WHERE id = ?
                    """;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                bind(statement, appointment);
                statement.setInt(9, appointment.getId());
                statement.executeUpdate();
                return appointment;
            }
        });
    }

    @Override
    public Optional<Appointment> findById(int id) {
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    SELECT + " WHERE a.id = ?")) {
                statement.setInt(1, id);
                try (ResultSet result = statement.executeQuery()) {
                    return result.next() ? Optional.of(map(result)) : Optional.empty();
                }
            }
        });
    }

    @Override
    public List<Appointment> findAll() {
        return database.withConnection(connection -> query(connection, SELECT
                + " ORDER BY a.appointment_date, a.start_time"));
    }

    @Override
    public List<Appointment> findByEmployeeAndDate(int employeeId, LocalDate date) {
        return database.withConnection(connection -> {
            String sql = SELECT + " WHERE a.employee_id = ? AND a.appointment_date = ?"
                    + " ORDER BY a.start_time";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, employeeId);
                statement.setDate(2, Date.valueOf(date));
                try (ResultSet result = statement.executeQuery()) {
                    List<Appointment> appointments = new ArrayList<>();
                    while (result.next()) appointments.add(map(result));
                    return appointments;
                }
            }
        });
    }

    @Override
    public <T> T inTransaction(TransactionCallback<T> callback) {
        return database.inTransaction(callback);
    }

    private List<Appointment> query(java.sql.Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<Appointment> appointments = new ArrayList<>();
            while (result.next()) appointments.add(map(result));
            return appointments;
        }
    }

    private void bind(PreparedStatement statement, Appointment appointment) throws SQLException {
        statement.setInt(1, appointment.getClient().getId());
        statement.setInt(2, appointment.getEmployee().getId());
        statement.setInt(3, appointment.getService().getId());
        statement.setDate(4, Date.valueOf(appointment.getAppointmentDate()));
        statement.setTime(5, Time.valueOf(appointment.getStartTime()));
        statement.setDouble(6, appointment.getPrice());
        statement.setString(7, appointment.getStatus().name());
        statement.setString(8, appointment.getPaymentType().name());
    }

    private Appointment map(ResultSet result) throws SQLException {
        Client client = new Client(result.getInt("client_id"), result.getString("client_name"),
                result.getString("client_phone"), result.getString("client_email"));
        Employee employee = new Employee(result.getInt("employee_id"), result.getString("employee_name"),
                result.getString("employee_phone"), result.getString("employee_email"));
        Service service = new Service(result.getInt("service_id"), result.getString("service_name"),
                result.getDouble("service_price"), result.getInt("service_duration"));
        return new Appointment(result.getInt("id"), client, employee, service,
                result.getDate("appointment_date").toLocalDate(),
                result.getTime("start_time").toLocalTime(), result.getDouble("price"),
                AppointmentStatus.valueOf(result.getString("status")),
                PaymentType.valueOf(result.getString("payment_type")));
    }
}
