package hr.salonmanager;

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
import hr.salonmanager.view.MainFrame;

import javax.swing.SwingUtilities;

/** Pokretna točka desktop aplikacije SalonManager. */
public final class Main {
    private Main() {
    }

    /** Pokreće aplikaciju i inicijalizira bazu podataka. */
    public static void main(String[] args) {
        Database database = new Database(DatabaseConfig.fromEnvironment());
        database.initializeSchema();

        ClientService clientService = new ClientService(new JdbcClientRepository(database));
        EmployeeService employeeService = new EmployeeService(new JdbcEmployeeRepository(database));
        ServiceCatalogService serviceCatalogService =
                new ServiceCatalogService(new JdbcServiceRepository(database));
        JdbcAppointmentRepository appointmentRepository = new JdbcAppointmentRepository(database);
        SalonSettingsService settingsService = new SalonSettingsService(new JdbcSettingsRepository(database));
        AppointmentService appointmentService = new AppointmentService(
                appointmentRepository,
                clientService,
                employeeService,
                serviceCatalogService,
                settingsService);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(
                    clientService,
                    employeeService,
                    serviceCatalogService,
                    appointmentService,
                    settingsService);
            frame.setVisible(true);
        });
    }
}
