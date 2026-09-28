package hr.salonmanager.repository;

import hr.salonmanager.model.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    /** Sprema termin sa snapshotom cijene. */
    Appointment save(Appointment appointment);
    /** Ažurira postojeći termin. */
    Appointment update(Appointment appointment);
    /** Dohvaća termin s povezanim klijentom, djelatnikom i uslugom. */
    Optional<Appointment> findById(int id);
    /** Dohvaća sve termine kronološkim redom. */
    List<Appointment> findAll();
    /** Dohvaća termine djelatnika za određeni datum. */
    List<Appointment> findByEmployeeAndDate(int employeeId, LocalDate date);
    /** Izvršava callback unutar jedne JDBC transakcije. */
    <T> T inTransaction(TransactionCallback<T> callback);
}
