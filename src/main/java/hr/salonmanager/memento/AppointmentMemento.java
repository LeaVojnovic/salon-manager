package hr.salonmanager.memento;

import hr.salonmanager.model.AppointmentStatus;
import hr.salonmanager.model.PaymentType;

import java.time.LocalDate;
import java.time.LocalTime;

/** Spremljeno prethodno stanje termina za Command/Memento undo funkcionalnost. */
public final class AppointmentMemento {
    private final LocalDate appointmentDate;
    private final LocalTime startTime;
    private final double price;
    private final AppointmentStatus status;
    private final PaymentType paymentType;

    public AppointmentMemento(LocalDate appointmentDate, LocalTime startTime, double price,
                              AppointmentStatus status, PaymentType paymentType) {
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.price = price;
        this.status = status;
        this.paymentType = paymentType;
    }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public LocalTime getStartTime() { return startTime; }
    public double getPrice() { return price; }
    public AppointmentStatus getStatus() { return status; }
    public PaymentType getPaymentType() { return paymentType; }
}
