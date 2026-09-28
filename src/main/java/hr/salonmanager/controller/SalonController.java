package hr.salonmanager.controller;

import hr.salonmanager.command.CancelAppointmentCommand;
import hr.salonmanager.command.ChangeAppointmentCommand;
import hr.salonmanager.command.CommandInvoker;
import hr.salonmanager.command.CompleteAppointmentCommand;
import hr.salonmanager.model.Appointment;
import hr.salonmanager.model.Client;
import hr.salonmanager.model.Employee;
import hr.salonmanager.model.PaymentType;
import hr.salonmanager.model.Service;
import hr.salonmanager.service.AppointmentService;
import hr.salonmanager.service.ClientService;
import hr.salonmanager.service.EmployeeService;
import hr.salonmanager.service.ServiceCatalogService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** Controller sloj koji obrađuje akcije iz Swing View sloja. */
public class SalonController {
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final ServiceCatalogService serviceCatalogService;
    private final AppointmentService appointmentService;
    private final CommandInvoker commandInvoker = new CommandInvoker();

    public SalonController(ClientService clientService, EmployeeService employeeService,
                           ServiceCatalogService serviceCatalogService, AppointmentService appointmentService) {
        this.clientService = clientService;
        this.employeeService = employeeService;
        this.serviceCatalogService = serviceCatalogService;
        this.appointmentService = appointmentService;
    }

    public List<Client> findClients() { return clientService.findAll(); }
    public Client saveClient(Client client) { return clientService.save(client); }
    public void deleteClient(int id) { clientService.delete(id); }

    public List<Employee> findEmployees() { return employeeService.findAll(); }
    public Employee saveEmployee(Employee employee) { return employeeService.save(employee); }
    public void deleteEmployee(int id) { employeeService.delete(id); }

    public List<Service> findServices() { return serviceCatalogService.findAll(); }
    public Service saveService(Service service) { return serviceCatalogService.save(service); }
    public void deleteService(int id) { serviceCatalogService.delete(id); }

    public List<Appointment> findAppointments() { return appointmentService.findAll(); }

    public Appointment createAppointment(Client client, Employee employee, Service service,
                                         LocalDate date, LocalTime startTime, PaymentType paymentType) {
        return appointmentService.createAppointment(client, employee, service, date, startTime, paymentType);
    }

    public void changeAppointment(Appointment appointment) {
        commandInvoker.executeCommand(new ChangeAppointmentCommand(appointmentService, appointment));
    }

    public void cancelAppointment(int appointmentId) {
        commandInvoker.executeCommand(new CancelAppointmentCommand(appointmentService, appointmentId));
    }

    public void completeAppointment(int appointmentId) {
        commandInvoker.executeCommand(new CompleteAppointmentCommand(appointmentService, appointmentId));
    }

    public void undoLastChange() { commandInvoker.undoLast(); }
    public boolean canUndo() { return commandInvoker.canUndo(); }
}
