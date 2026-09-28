package hr.salonmanager.command;

import hr.salonmanager.memento.AppointmentMemento;
import hr.salonmanager.model.Appointment;
import hr.salonmanager.service.AppointmentService;

/** Command za otkazivanje termina uz mogućnost vraćanja prethodnog stanja. */
public class CancelAppointmentCommand implements Command {
    private final AppointmentService appointmentService;
    private final int appointmentId;
    private AppointmentMemento previousState;

    public CancelAppointmentCommand(AppointmentService appointmentService, int appointmentId) {
        this.appointmentService = appointmentService;
        this.appointmentId = appointmentId;
    }

    @Override
    public void execute() {
        Appointment current = appointmentService.findById(appointmentId);
        previousState = current.saveState();
        appointmentService.cancelAppointment(appointmentId);
    }

    @Override
    public void undo() {
        appointmentService.restoreState(appointmentId, previousState);
    }
}
