package hr.salonmanager;

import hr.salonmanager.command.ChangeAppointmentCommand;
import hr.salonmanager.command.CommandInvoker;
import hr.salonmanager.command.CancelAppointmentCommand;
import hr.salonmanager.model.Appointment;
import hr.salonmanager.model.AppointmentStatus;
import hr.salonmanager.model.Client;
import hr.salonmanager.model.Employee;
import hr.salonmanager.model.PaymentType;
import hr.salonmanager.model.Service;
import hr.salonmanager.repository.Database;
import hr.salonmanager.repository.DatabaseConfig;
import hr.salonmanager.repository.jdbc.JdbcAppointmentRepository;
import hr.salonmanager.repository.jdbc.JdbcClientRepository;
import hr.salonmanager.repository.jdbc.JdbcEmployeeRepository;
import hr.salonmanager.repository.jdbc.JdbcServiceRepository;
import hr.salonmanager.repository.jdbc.JdbcSettingsRepository;
import hr.salonmanager.service.AppointmentService;
import hr.salonmanager.service.ClientService;
import hr.salonmanager.service.EmployeeService;
import hr.salonmanager.service.SalonSettingsService;
import hr.salonmanager.service.ServiceCatalogService;
import hr.salonmanager.service.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppointmentServiceTest {
    private AppointmentService appointmentService;
    private ServiceCatalogService services;
    private Client client;
    private Employee employee;
    private Service service;

    @BeforeEach
    void setUp() {
        Database database = new Database(new DatabaseConfig(
                "jdbc:h2:mem:test_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1", "sa", ""));
        database.initializeSchema();
        ClientService clients = new ClientService(new JdbcClientRepository(database));
        EmployeeService employees = new EmployeeService(new JdbcEmployeeRepository(database));
        services = new ServiceCatalogService(new JdbcServiceRepository(database));
        SalonSettingsService settings = new SalonSettingsService(new JdbcSettingsRepository(database));
        appointmentService = new AppointmentService(
                new JdbcAppointmentRepository(database), clients, employees, services, settings);
        client = clients.save(new Client("Ana Horvat", "091111111", "ana@example.com"));
        employee = employees.save(new Employee("Marija Kovač", "092222222", "marija@example.com"));
        service = services.save(new Service("Šišanje", 20.00, 60));
    }

    @Test
    void preventsOverlappingAppointmentsForSameEmployee() {
        LocalDate date = LocalDate.of(2026, 10, 1);
        appointmentService.createAppointment(client, employee, service, date,
                LocalTime.of(10, 0), PaymentType.CASH);

        assertThrows(ValidationException.class, () -> appointmentService.createAppointment(
                client, employee, service, date, LocalTime.of(10, 30), PaymentType.CARD));
    }

    @Test
    void enforcesWorkingHours() {
        assertThrows(ValidationException.class, () -> appointmentService.createAppointment(
                client, employee, service, LocalDate.of(2026, 10, 1),
                LocalTime.of(19, 30), PaymentType.CASH));
    }

    @Test
    void allowsOnlyValidStatusTransitions() {
        Appointment appointment = appointmentService.createAppointment(client, employee, service,
                LocalDate.of(2026, 10, 2), LocalTime.of(10, 0), PaymentType.CASH);
        appointmentService.completeAppointment(appointment.getId());
        assertEquals(AppointmentStatus.ZAVRSEN, appointmentService.findById(appointment.getId()).getStatus());
        assertThrows(IllegalStateException.class, () -> appointmentService.cancelAppointment(appointment.getId()));
    }

    @Test
    void storesPriceSnapshotWhenServicePriceChanges() {
        Appointment appointment = appointmentService.createAppointment(client, employee, service,
                LocalDate.of(2026, 10, 3), LocalTime.of(10, 0), PaymentType.CASH);
        service.setPrice(35.00);
        services.save(service);
        // Promjena cjenika ne smije promijeniti cijenu već spremljenog termina.
        assertEquals(20.00, appointmentService.findById(appointment.getId()).getPrice());
    }

    @Test
    void commandAndMementoUndoRestorePreviousAppointmentState() {
        Appointment appointment = appointmentService.createAppointment(client, employee, service,
                LocalDate.of(2026, 10, 4), LocalTime.of(10, 0), PaymentType.CASH);
        Appointment changed = new Appointment(appointment.getId(), client, employee, service,
                appointment.getAppointmentDate(), LocalTime.of(12, 0), appointment.getPrice(),
                appointment.getStatus(), appointment.getPaymentType());
        CommandInvoker invoker = new CommandInvoker();
        invoker.executeCommand(new ChangeAppointmentCommand(appointmentService, changed));
        assertEquals(LocalTime.of(12, 0), appointmentService.findById(appointment.getId()).getStartTime());

        invoker.undoLast();
        assertEquals(LocalTime.of(10, 0), appointmentService.findById(appointment.getId()).getStartTime());
    }

    @Test
    void cancellingAndUndoRestoreThePreviouslyScheduledState() {
        Appointment appointment = appointmentService.createAppointment(client, employee, service,
                LocalDate.of(2026, 10, 5), LocalTime.of(10, 0), PaymentType.CARD);
        CommandInvoker invoker = new CommandInvoker();
        invoker.executeCommand(new CancelAppointmentCommand(appointmentService, appointment.getId()));

        assertEquals(AppointmentStatus.OTKAZAN, appointmentService.findById(appointment.getId()).getStatus());
        invoker.undoLast();
        assertEquals(AppointmentStatus.ZAKAZAN, appointmentService.findById(appointment.getId()).getStatus());
    }
}
