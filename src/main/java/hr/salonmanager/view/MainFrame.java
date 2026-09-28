package hr.salonmanager.view;

import hr.salonmanager.controller.SalonController;
import hr.salonmanager.model.Appointment;
import hr.salonmanager.model.AppointmentStatus;
import hr.salonmanager.model.Client;
import hr.salonmanager.model.Employee;
import hr.salonmanager.model.Service;
import hr.salonmanager.service.AppointmentService;
import hr.salonmanager.service.ClientService;
import hr.salonmanager.service.EmployeeService;
import hr.salonmanager.service.SalonSettingsService;
import hr.salonmanager.service.ServiceCatalogService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/** Glavni Swing prozor organiziran prema MVC konceptu. */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final ServiceCatalogService serviceCatalogService;
    private final AppointmentService appointmentService;
    private final SalonSettingsService settingsService;
    private final SalonController controller;
    private final DecimalFormat priceFormat = new DecimalFormat("0.00");

    private final DefaultTableModel appointmentsModel = nonEditableModel(
            "Klijent", "Djelatnik", "Usluga", "Datum", "Vrijeme", "Završetak", "Cijena", "Status", "Plaćanje");
    private final DefaultTableModel clientsModel = nonEditableModel("Ime", "Telefon", "Email");
    private final DefaultTableModel employeesModel = nonEditableModel("Ime", "Telefon", "Email");
    private final DefaultTableModel servicesModel = nonEditableModel("Naziv usluge", "Cijena", "Trajanje (min)");

    private final JTable appointmentsTable = new JTable(appointmentsModel);
    private final JTable clientsTable = new JTable(clientsModel);
    private final JTable employeesTable = new JTable(employeesModel);
    private final JTable servicesTable = new JTable(servicesModel);
    private final JButton undoButton = new JButton("Poništi");

    private List<Appointment> appointments = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();
    private List<Service> services = new ArrayList<>();
    private final SettingsPanel settingsPanel;

    public MainFrame(ClientService clientService, EmployeeService employeeService,
                     ServiceCatalogService serviceCatalogService, AppointmentService appointmentService,
                     SalonSettingsService settingsService) {
        super("SalonManager");
        this.clientService = clientService;
        this.employeeService = employeeService;
        this.serviceCatalogService = serviceCatalogService;
        this.appointmentService = appointmentService;
        this.settingsService = settingsService;
        this.controller = new SalonController(clientService, employeeService, serviceCatalogService, appointmentService);
        this.settingsPanel = new SettingsPanel(settingsService);
        configureWindow();
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Termini", buildAppointmentsTab());
        tabs.addTab("Klijenti", buildClientsTab());
        tabs.addTab("Djelatnici", buildEmployeesTab());
        tabs.addTab("Usluge", buildServicesTab());
        tabs.addTab("Postavke", settingsPanel);
        add(tabs, BorderLayout.CENTER);
        refreshAll();
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    private JPanel buildAppointmentsTab() {
        appointmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentsTable.setAutoCreateRowSorter(true);
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JScrollPane(appointmentsTable), BorderLayout.CENTER);
        JButton add = new JButton("Dodaj");
        JButton edit = new JButton("Uredi");
        JButton cancel = new JButton("Otkaži");
        JButton complete = new JButton("Završi");
        JButton refresh = new JButton("Osvježi");
        undoButton.setEnabled(false);
        add.addActionListener(event -> addAppointment());
        edit.addActionListener(event -> editAppointment());
        cancel.addActionListener(event -> cancelAppointment());
        complete.addActionListener(event -> completeAppointment());
        refresh.addActionListener(event -> refreshAppointments());
        undoButton.addActionListener(event -> undoLast());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(add);
        buttons.add(edit);
        buttons.add(cancel);
        buttons.add(complete);
        buttons.add(undoButton);
        buttons.add(refresh);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildClientsTab() {
        clientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel panel = entityPanel(clientsTable);
        JButton add = new JButton("Dodaj");
        JButton edit = new JButton("Uredi");
        JButton delete = new JButton("Obriši");
        JButton refresh = new JButton("Osvježi");
        add.addActionListener(event -> addClient());
        edit.addActionListener(event -> editClient());
        delete.addActionListener(event -> deleteClient());
        refresh.addActionListener(event -> refreshClients());
        addButtons(panel, add, edit, delete, refresh);
        return panel;
    }

    private JPanel buildEmployeesTab() {
        employeesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel panel = entityPanel(employeesTable);
        JButton add = new JButton("Dodaj");
        JButton edit = new JButton("Uredi");
        JButton delete = new JButton("Obriši");
        JButton refresh = new JButton("Osvježi");
        add.addActionListener(event -> addEmployee());
        edit.addActionListener(event -> editEmployee());
        delete.addActionListener(event -> deleteEmployee());
        refresh.addActionListener(event -> refreshEmployees());
        addButtons(panel, add, edit, delete, refresh);
        return panel;
    }

    private JPanel buildServicesTab() {
        servicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel panel = entityPanel(servicesTable);
        JButton add = new JButton("Dodaj");
        JButton edit = new JButton("Uredi");
        JButton delete = new JButton("Obriši");
        JButton refresh = new JButton("Osvježi");
        add.addActionListener(event -> addService());
        edit.addActionListener(event -> editService());
        delete.addActionListener(event -> deleteService());
        refresh.addActionListener(event -> refreshServices());
        addButtons(panel, add, edit, delete, refresh);
        return panel;
    }

    private JPanel entityPanel(JTable table) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void addButtons(JPanel panel, JButton... buttons) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton button : buttons) buttonPanel.add(button);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void refreshAll() {
        refreshClients();
        refreshEmployees();
        refreshServices();
        refreshAppointments();
        settingsPanel.refresh();
    }

    private void refreshAppointments() {
        appointments = controller.findAppointments();
        appointmentsModel.setRowCount(0);
        for (Appointment appointment : appointments) {
            appointmentsModel.addRow(new Object[]{
                    appointment.getClient().getName(), appointment.getEmployee().getName(),
                    appointment.getService().getName(), appointment.getAppointmentDate(),
                    appointment.getStartTime(), appointment.getEndTime(), priceFormat.format(appointment.getPrice()),
                    statusText(appointment.getStatus()), appointment.getPaymentType()});
        }
        undoButton.setEnabled(controller.canUndo());
    }

    private void refreshClients() {
        clients = controller.findClients();
        clientsModel.setRowCount(0);
        for (Client client : clients) clientsModel.addRow(new Object[]{client.getName(), client.getPhone(), client.getEmail()});
    }

    private void refreshEmployees() {
        employees = controller.findEmployees();
        employeesModel.setRowCount(0);
        for (Employee employee : employees) employeesModel.addRow(new Object[]{employee.getName(), employee.getPhone(), employee.getEmail()});
    }

    private void refreshServices() {
        services = controller.findServices();
        servicesModel.setRowCount(0);
        for (Service service : services) servicesModel.addRow(new Object[]{service.getName(), priceFormat.format(service.getPrice()), service.getDurationMinutes()});
    }

    private void addClient() {
        ClientDialog dialog = new ClientDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) run(() -> controller.saveClient(dialog.getValue()), this::refreshClients);
    }

    private void editClient() {
        Client selected = selected(clientsTable, clients);
        if (selected == null) return;
        ClientDialog dialog = new ClientDialog(this, selected);
        dialog.setVisible(true);
        if (dialog.isSaved()) run(() -> controller.saveClient(dialog.getValue()), this::refreshClients);
    }

    private void deleteClient() {
        Client selected = selected(clientsTable, clients);
        if (selected == null || !confirmDelete("klijenta")) return;
        run(() -> controller.deleteClient(selected.getId()), this::refreshAll);
    }

    private void addEmployee() {
        EmployeeDialog dialog = new EmployeeDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) run(() -> controller.saveEmployee(dialog.getValue()), this::refreshEmployees);
    }

    private void editEmployee() {
        Employee selected = selected(employeesTable, employees);
        if (selected == null) return;
        EmployeeDialog dialog = new EmployeeDialog(this, selected);
        dialog.setVisible(true);
        if (dialog.isSaved()) run(() -> controller.saveEmployee(dialog.getValue()), this::refreshEmployees);
    }

    private void deleteEmployee() {
        Employee selected = selected(employeesTable, employees);
        if (selected == null || !confirmDelete("djelatnika")) return;
        run(() -> controller.deleteEmployee(selected.getId()), this::refreshAll);
    }

    private void addService() {
        ServiceDialog dialog = new ServiceDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) run(() -> controller.saveService(dialog.getValue()), this::refreshServices);
    }

    private void editService() {
        Service selected = selected(servicesTable, services);
        if (selected == null) return;
        ServiceDialog dialog = new ServiceDialog(this, selected);
        dialog.setVisible(true);
        if (dialog.isSaved()) run(() -> controller.saveService(dialog.getValue()), this::refreshServices);
    }

    private void deleteService() {
        Service selected = selected(servicesTable, services);
        if (selected == null || !confirmDelete("uslugu")) return;
        run(() -> controller.deleteService(selected.getId()), this::refreshAll);
    }

    private void addAppointment() {
        if (clients.isEmpty() || employees.isEmpty() || services.isEmpty()) {
            showError(new IllegalStateException("Prije dodavanja termina unesite klijenta, djelatnika i uslugu."));
            return;
        }
        AppointmentDialog dialog = new AppointmentDialog(this, null, clients, employees, services);
        dialog.setVisible(true);
        if (dialog.isSaved()) run(() -> controller.createAppointment(
                dialog.getValue().getClient(), dialog.getValue().getEmployee(), dialog.getValue().getService(),
                dialog.getValue().getAppointmentDate(), dialog.getValue().getStartTime(), dialog.getValue().getPaymentType()),
                this::refreshAppointments);
    }

    private void editAppointment() {
        Appointment selected = selected(appointmentsTable, appointments);
        if (selected == null) return;
        AppointmentDialog dialog = new AppointmentDialog(this, selected, clients, employees, services);
        dialog.setVisible(true);
        if (dialog.isSaved()) run(() -> controller.changeAppointment(dialog.getValue()), this::refreshAppointments);
    }

    private void cancelAppointment() {
        Appointment selected = selected(appointmentsTable, appointments);
        if (selected == null || selected.getStatus() != AppointmentStatus.ZAKAZAN) return;
        run(() -> controller.cancelAppointment(selected.getId()),
                this::refreshAppointments);
    }

    private void completeAppointment() {
        Appointment selected = selected(appointmentsTable, appointments);
        if (selected == null || selected.getStatus() != AppointmentStatus.ZAKAZAN) return;
        run(() -> controller.completeAppointment(selected.getId()),
                this::refreshAppointments);
    }

    private void undoLast() {
        run(controller::undoLastChange, this::refreshAppointments);
    }

    private boolean confirmDelete(String entity) {
        return JOptionPane.showConfirmDialog(this, "Obrisati odabranog " + entity + "?", "Potvrda",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void showError(Exception exception) {
        DialogSupport.showError(this, exception);
    }

    private void run(Runnable action, Runnable refresh) {
        try {
            action.run();
            refresh.run();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private <T> T selected(JTable table, List<T> values) {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            showError(new IllegalStateException("Odaberite zapis u tablici."));
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        return modelRow < values.size() ? values.get(modelRow) : null;
    }

    private static String statusText(AppointmentStatus status) {
        return switch (status) {
            case ZAKAZAN -> "Zakazan";
            case OTKAZAN -> "Otkazan";
            case ZAVRSEN -> "Završen";
        };
    }

    private static DefaultTableModel nonEditableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }
}
