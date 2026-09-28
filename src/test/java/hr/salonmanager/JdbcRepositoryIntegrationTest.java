package hr.salonmanager;

import hr.salonmanager.model.Appointment;
import hr.salonmanager.model.AppointmentStatus;
import hr.salonmanager.model.Client;
import hr.salonmanager.model.Employee;
import hr.salonmanager.model.PaymentType;
import hr.salonmanager.model.SalonSettings;
import hr.salonmanager.model.Service;
import hr.salonmanager.repository.Database;
import hr.salonmanager.repository.DatabaseConfig;
import hr.salonmanager.repository.RepositoryException;
import hr.salonmanager.repository.jdbc.JdbcAppointmentRepository;
import hr.salonmanager.repository.jdbc.JdbcClientRepository;
import hr.salonmanager.repository.jdbc.JdbcEmployeeRepository;
import hr.salonmanager.repository.jdbc.JdbcServiceRepository;
import hr.salonmanager.repository.jdbc.JdbcSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Integracijski testovi JDBC Repository sloja nad privremenom H2 bazom. */
class JdbcRepositoryIntegrationTest {
    private JdbcClientRepository clients;
    private JdbcEmployeeRepository employees;
    private JdbcServiceRepository services;
    private JdbcAppointmentRepository appointments;
    private JdbcSettingsRepository settings;

    @BeforeEach
    void setUp() {
        Database database = new Database(new DatabaseConfig(
                "jdbc:h2:mem:repository_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1", "sa", ""));
        database.initializeSchema();
        clients = new JdbcClientRepository(database);
        employees = new JdbcEmployeeRepository(database);
        services = new JdbcServiceRepository(database);
        appointments = new JdbcAppointmentRepository(database);
        settings = new JdbcSettingsRepository(database);
    }

    @Test
    void persistsAndReadsEntitiesThroughJdbcRepositories() {
        Client client = clients.save(new Client("Ana Horvat", "091111111", "ana@example.com"));
        Employee employee = employees.save(new Employee("Marija Kovač", "092222222", "marija@example.com"));
        Service service = services.save(new Service("Šišanje", 20.00, 60));
        Appointment appointment = appointments.save(new Appointment(
                0, client, employee, service, LocalDate.of(2026, 10, 10), LocalTime.of(10, 0),
                20.00, AppointmentStatus.ZAKAZAN, PaymentType.CASH));

        Appointment loaded = appointments.findById(appointment.getId()).orElseThrow();

        assertNotNull(client.getId());
        assertEquals(1, clients.findAll().size());
        assertEquals("Marija Kovač", loaded.getEmployee().getName());
        assertEquals("Šišanje", loaded.getService().getName());
        assertEquals(20.00, loaded.getPrice());
        assertEquals(1, appointments.findByEmployeeAndDate(
                employee.getId(), LocalDate.of(2026, 10, 10)).size());
    }

    @Test
    void keepsSalonSettingsAcrossSaveAndRead() {
        SalonSettings original = new SalonSettings(1, "Salon Lea", "Ulica 1", "093333333",
                "salon@example.com", LocalTime.of(8, 0), LocalTime.of(20, 0));
        settings.save(original);
        original.setClosingTime(LocalTime.of(21, 0));
        settings.save(original);

        SalonSettings loaded = settings.findById(1).orElseThrow();

        assertEquals("Salon Lea", loaded.getSalonName());
        assertEquals(LocalTime.of(21, 0), loaded.getClosingTime());
    }

    @Test
    void preventsPhysicalDeletionOfRecordsReferencedByAppointment() {
        Client client = clients.save(new Client("Ana", "091", "ana@example.com"));
        Employee employee = employees.save(new Employee("Marija", "092", "marija@example.com"));
        Service service = services.save(new Service("Šišanje", 20.00, 60));
        appointments.save(new Appointment(0, client, employee, service,
                LocalDate.of(2026, 10, 11), LocalTime.of(10, 0), 20.00,
                AppointmentStatus.ZAKAZAN, PaymentType.CASH));

        assertThrows(RepositoryException.class, () -> clients.delete(client.getId()));
        assertThrows(RepositoryException.class, () -> employees.delete(employee.getId()));
        assertThrows(RepositoryException.class, () -> services.delete(service.getId()));
    }
}
