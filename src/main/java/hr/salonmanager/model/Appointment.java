package hr.salonmanager.model;

import hr.salonmanager.memento.AppointmentMemento;

import java.time.LocalDate;
import java.time.LocalTime;

/** Termin koji povezuje klijenta, djelatnika i uslugu. */
public class Appointment {
    private int id;
    private Client client;
    private Employee employee;
    private Service service;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private double price;
    private AppointmentStatus status;
    private PaymentType paymentType;

    public Appointment() {
    }

    public Appointment(int id, Client client, Employee employee, Service service,
                       LocalDate appointmentDate, LocalTime startTime, double price,
                       AppointmentStatus status, PaymentType paymentType) {
        this.id = id;
        this.client = client;
        this.employee = employee;
        this.service = service;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.price = price;
        this.status = status;
        this.paymentType = paymentType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public AppointmentStatus getStatus() { return status; }

    public void setStatus(AppointmentStatus status) {
        changeStatus(status);
    }

    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }

    /** Vraća očekivano vrijeme završetka prema trajanju odabrane usluge. */
    public LocalTime getEndTime() {
        if (service == null || startTime == null) {
            return null;
        }
        return startTime.plusMinutes(service.getDurationMinutes());
    }

    /** Sprema stanje koje se može vratiti undo operacijom. */
    public AppointmentMemento saveState() {
        return new AppointmentMemento(appointmentDate, startTime, price, status, paymentType);
    }

    /** Vraća prethodno spremljeno stanje termina. */
    public void restore(AppointmentMemento memento) {
        this.appointmentDate = memento.getAppointmentDate();
        this.startTime = memento.getStartTime();
        this.price = memento.getPrice();
        this.status = memento.getStatus();
        this.paymentType = memento.getPaymentType();
    }

    /** Provjerava i provodi dopušteni prijelaz statusa. */
    public void changeStatus(AppointmentStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status termina nije zadan.");
        }
        if (status == null || status == newStatus) {
            status = newStatus;
            return;
        }
        boolean allowed = status == AppointmentStatus.ZAKAZAN
                && (newStatus == AppointmentStatus.OTKAZAN || newStatus == AppointmentStatus.ZAVRSEN);
        if (!allowed) {
            throw new IllegalStateException("Nedopušten prijelaz statusa: " + status + " -> " + newStatus);
        }
        status = newStatus;
    }

    /** Provjerava preklapanje s drugim aktivnim terminom istog djelatnika. */
    public boolean overlaps(Appointment other) {
        if (other == null || employee == null || other.employee == null
                || employee.getId() != other.employee.getId()
                || !appointmentDate.equals(other.appointmentDate)
                || status == AppointmentStatus.OTKAZAN
                || other.status == AppointmentStatus.OTKAZAN) {
            return false;
        }
        return startTime.isBefore(other.getEndTime()) && other.startTime.isBefore(getEndTime());
    }

    @Override
    public String toString() {
        return appointmentDate + " " + startTime + " - " + (client == null ? "" : client.getName());
    }
}
