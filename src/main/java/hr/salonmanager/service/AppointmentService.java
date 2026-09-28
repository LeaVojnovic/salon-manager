package hr.salonmanager.service;

import hr.salonmanager.memento.AppointmentMemento;
import hr.salonmanager.model.Appointment;
import hr.salonmanager.model.AppointmentStatus;
import hr.salonmanager.model.Client;
import hr.salonmanager.model.Employee;
import hr.salonmanager.model.PaymentType;
import hr.salonmanager.model.SalonSettings;
import hr.salonmanager.model.Service;
import hr.salonmanager.repository.AppointmentRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** Poslovna logika termina, uključujući dostupnost, radno vrijeme i statuse. */
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final ServiceCatalogService serviceCatalogService;
    private final SalonSettingsService settingsService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              ClientService clientService,
                              EmployeeService employeeService,
                              ServiceCatalogService serviceCatalogService,
                              SalonSettingsService settingsService) {
        this.appointmentRepository = appointmentRepository;
        this.clientService = clientService;
        this.employeeService = employeeService;
        this.serviceCatalogService = serviceCatalogService;
        this.settingsService = settingsService;
    }

    /** Kreira zakazani termin i provjeru dostupnosti sprema u istoj transakciji. */
    /**
     * Kreira termin iz već dohvaćenih domenskih objekata.
     *
     * @param client klijent termina
     * @param employee djelatnik termina
     * @param service odabrana usluga
     * @param date datum termina
     * @param startTime početno vrijeme
     * @param paymentType način plaćanja
     * @return spremljeni termin
     */
    public Appointment createAppointment(Client client, Employee employee, Service service,
                                         LocalDate date, LocalTime startTime, PaymentType paymentType) {
        validateAppointmentData(client, employee, service, date, startTime, paymentType);
        Appointment appointment = new Appointment(0, client, employee, service, date, startTime,
                service.getPrice(), AppointmentStatus.ZAKAZAN, paymentType);
        validateWorkingHours(appointment);
        return appointmentRepository.inTransaction(() -> {
            ensureAvailable(appointment, 0);
            return appointmentRepository.save(appointment);
        });
    }

    /** Ažurira termin uz provjeru statusa, radnog vremena i preklapanja. */
    /** Ažurira termin uz provjeru dostupnosti i dopuštenog prijelaza statusa. */
    public Appointment updateAppointment(Appointment updated) {
        validateAppointmentData(updated.getClient(), updated.getEmployee(), updated.getService(),
                updated.getAppointmentDate(), updated.getStartTime(), updated.getPaymentType());
        return appointmentRepository.inTransaction(() -> {
            Appointment current = findRequired(updated.getId());
            validateStatusTransition(current.getStatus(), updated.getStatus());
            if (current.getService().getId() == updated.getService().getId()) {
                updated.setPrice(current.getPrice());
            } else {
                updated.setPrice(updated.getService().getPrice());
            }
            validateWorkingHours(updated);
            ensureAvailable(updated, updated.getId());
            return appointmentRepository.update(updated);
        });
    }

    /** Otkazuje zakazani termin. */
    public Appointment cancelAppointment(int id) {
        return changeToFinalStatus(id, AppointmentStatus.OTKAZAN);
    }

    /** Označava zakazani termin završenim. */
    public Appointment completeAppointment(int id) {
        return changeToFinalStatus(id, AppointmentStatus.ZAVRSEN);
    }

    /** Vraća prethodno stanje iz Memento objekta i ponovno ga sprema u bazu. */
    /** Vraća prethodno stanje termina spremljeno u Memento objektu. */
    public Appointment restoreState(int id, AppointmentMemento memento) {
        return appointmentRepository.inTransaction(() -> {
            Appointment current = findRequired(id);
            current.restore(memento);
            validateWorkingHours(current);
            if (current.getStatus() != AppointmentStatus.OTKAZAN) {
                ensureAvailable(current, id);
            }
            return appointmentRepository.update(current);
        });
    }

    public Appointment findById(int id) {
        return findRequired(id);
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    /** Provjera dostupnosti bez spremanja termina. */
    public boolean isAvailable(Appointment candidate) {
        return appointmentRepository.findByEmployeeAndDate(
                        candidate.getEmployee().getId(), candidate.getAppointmentDate())
                .stream()
                .filter(existing -> existing.getId() != candidate.getId())
                .noneMatch(candidate::overlaps);
    }

    private Appointment changeToFinalStatus(int id, AppointmentStatus status) {
        return appointmentRepository.inTransaction(() -> {
            Appointment appointment = findRequired(id);
            appointment.changeStatus(status);
            return appointmentRepository.update(appointment);
        });
    }

    private void ensureAvailable(Appointment candidate, int excludedId) {
        boolean available = appointmentRepository.findByEmployeeAndDate(
                        candidate.getEmployee().getId(), candidate.getAppointmentDate())
                .stream()
                .filter(existing -> existing.getId() != excludedId)
                .noneMatch(candidate::overlaps);
        if (!available) {
            throw new ValidationException("Djelatnik već ima termin u odabranom vremenu.");
        }
    }

    private Appointment findRequired(int id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Termin ne postoji."));
    }

    private void validateAppointmentData(Client client, Employee employee, Service service,
                                         LocalDate date, LocalTime startTime, PaymentType paymentType) {
        if (client == null || client.getId() == 0) throw new ValidationException("Odaberite klijenta.");
        if (employee == null || employee.getId() == 0) throw new ValidationException("Odaberite djelatnika.");
        if (service == null || service.getId() == 0) throw new ValidationException("Odaberite uslugu.");
        if (date == null) throw new ValidationException("Datum termina je obavezan.");
        if (startTime == null) throw new ValidationException("Početno vrijeme je obavezno.");
        if (paymentType == null) throw new ValidationException("Odaberite način plaćanja.");
    }

    private void validateWorkingHours(Appointment appointment) {
        SalonSettings settings = settingsService.getSettings();
        LocalTime end = appointment.getEndTime();
        if (end == null || appointment.getStartTime().isBefore(settings.getOpeningTime())
                || end.isAfter(settings.getClosingTime())) {
            throw new ValidationException("Termin mora biti unutar radnog vremena salona.");
        }
    }

    private void validateStatusTransition(AppointmentStatus current, AppointmentStatus next) {
        if (current == next) return;
        if (current != AppointmentStatus.ZAKAZAN
                || (next != AppointmentStatus.OTKAZAN && next != AppointmentStatus.ZAVRSEN)) {
            throw new ValidationException("Nedopušten prijelaz statusa termina.");
        }
    }
}
