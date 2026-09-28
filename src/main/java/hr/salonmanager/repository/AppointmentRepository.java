package hr.salonmanager.repository;

import hr.salonmanager.model.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    Appointment save(Appointment appointment);
    Appointment update(Appointment appointment);
    Optional<Appointment> findById(int id);
    List<Appointment> findAll();
    List<Appointment> findByEmployeeAndDate(int employeeId, LocalDate date);
    <T> T inTransaction(TransactionCallback<T> callback);
}
