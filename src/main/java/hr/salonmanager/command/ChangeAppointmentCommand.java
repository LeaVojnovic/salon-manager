package hr.salonmanager.command;

import hr.salonmanager.memento.AppointmentMemento;
import hr.salonmanager.model.Appointment;
import hr.salonmanager.service.AppointmentService;

/** Command za promjenu termina uz Memento prethodnog stanja. */
public class ChangeAppointmentCommand implements Command {
    private final AppointmentService appointmentService;
    private final Appointment updatedAppointment;
    private AppointmentMemento previousState;

    public ChangeAppointmentCommand(AppointmentService appointmentService, Appointment updatedAppointment) {
        this.appointmentService = appointmentService;
        this.updatedAppointment = updatedAppointment;
    }

    @Override
    public void execute() {
        Appointment current = appointmentService.findById(updatedAppointment.getId());
        previousState = current.saveState();
        appointmentService.updateAppointment(updatedAppointment);
    }

    @Override
    public void undo() {
        appointmentService.restoreState(updatedAppointment.getId(), previousState);
    }
}
