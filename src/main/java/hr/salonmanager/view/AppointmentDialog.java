package hr.salonmanager.view;

import hr.salonmanager.model.Appointment;
import hr.salonmanager.model.AppointmentStatus;
import hr.salonmanager.model.Client;
import hr.salonmanager.model.Employee;
import hr.salonmanager.model.PaymentType;
import hr.salonmanager.model.Service;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/** Forma za kreiranje i uređivanje termina. */
public class AppointmentDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final JComboBox<Client> clientCombo;
    private final JComboBox<Employee> employeeCombo;
    private final JComboBox<Service> serviceCombo;
    private final JTextField dateField = new JTextField(16);
    private final JTextField timeField = new JTextField(16);
    private final JComboBox<PaymentType> paymentCombo = new JComboBox<>(PaymentType.values());
    private final JLabel endTimeLabel = new JLabel("-");
    private final Appointment initial;
    private Appointment value;
    private boolean saved;

    public AppointmentDialog(Frame owner, Appointment initial, List<Client> clients,
                             List<Employee> employees, List<Service> services) {
        super(owner, initial == null ? "Novi termin" : "Uredi termin", true);
        this.initial = initial;
        clientCombo = new JComboBox<>(clients.toArray(Client[]::new));
        employeeCombo = new JComboBox<>(employees.toArray(Employee[]::new));
        serviceCombo = new JComboBox<>(services.toArray(Service[]::new));
        if (initial != null) {
            selectById(clientCombo, initial.getClient().getId());
            selectById(employeeCombo, initial.getEmployee().getId());
            selectById(serviceCombo, initial.getService().getId());
            dateField.setText(initial.getAppointmentDate().format(DateTimeFormats.DATE));
            timeField.setText(initial.getStartTime().format(DateTimeFormats.TIME));
            paymentCombo.setSelectedItem(initial.getPaymentType());
        } else {
            dateField.setText(LocalDate.now().format(DateTimeFormats.DATE));
            timeField.setText("08:00");
        }

        serviceCombo.addActionListener(event -> updateEndTime());
        timeField.getDocument().addDocumentListener(new SimpleDocumentListener(this::updateEndTime));
        JPanel form = DialogSupport.formPanel();
        DialogSupport.addRow(form, 0, "Klijent:", clientCombo);
        DialogSupport.addRow(form, 1, "Djelatnik:", employeeCombo);
        DialogSupport.addRow(form, 2, "Usluga:", serviceCombo);
        DialogSupport.addRow(form, 3, "Datum (DD.MM.GGGG.):", dateField);
        DialogSupport.addRow(form, 4, "Vrijeme (HH:MM):", timeField);
        DialogSupport.addRow(form, 5, "Očekivani završetak:", endTimeLabel);
        DialogSupport.addRow(form, 6, "Plaćanje:", paymentCombo);
        JButton save = new JButton("Spremi");
        JButton cancel = new JButton("Odustani");
        save.addActionListener(event -> save());
        cancel.addActionListener(event -> dispose());
        add(form, BorderLayout.CENTER);
        add(DialogSupport.buttons(save, cancel), BorderLayout.SOUTH);
        updateEndTime();
        pack();
        setLocationRelativeTo(owner);
    }

    private void save() {
        try {
            Client client = (Client) clientCombo.getSelectedItem();
            Employee employee = (Employee) employeeCombo.getSelectedItem();
            Service service = (Service) serviceCombo.getSelectedItem();
            LocalDate date = DateTimeFormats.parseDate(dateField.getText());
            LocalTime time = DateTimeFormats.parseTime(timeField.getText());
            PaymentType payment = (PaymentType) paymentCombo.getSelectedItem();
            AppointmentStatus status = initial == null ? AppointmentStatus.ZAKAZAN : initial.getStatus();
            double price = initial == null ? service.getPrice() : initial.getPrice();
            value = new Appointment(initial == null ? 0 : initial.getId(), client, employee, service,
                    date, time, price, status, payment);
            saved = true;
            dispose();
        } catch (DateTimeParseException | NullPointerException exception) {
            DialogSupport.showError(this, new IllegalArgumentException("Datum i vrijeme moraju biti u ispravnom formatu."));
        }
    }

    private void updateEndTime() {
        try {
            Service service = (Service) serviceCombo.getSelectedItem();
            LocalTime time = DateTimeFormats.parseTime(timeField.getText());
            endTimeLabel.setText(service == null ? "-" : time.plusMinutes(service.getDurationMinutes())
                    .format(DateTimeFormats.TIME));
        } catch (DateTimeParseException exception) {
            endTimeLabel.setText("-");
        }
    }

    private <T> void selectById(JComboBox<T> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof Client client && client.getId() == id
                    || item instanceof Employee employee && employee.getId() == id
                    || item instanceof Service service && service.getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    public Appointment getValue() { return value; }
    public boolean isSaved() { return saved; }

    @FunctionalInterface
    private interface ChangeAction { void run(); }

    private static class SimpleDocumentListener implements javax.swing.event.DocumentListener {
        private final ChangeAction action;

        private SimpleDocumentListener(ChangeAction action) { this.action = action; }
        @Override public void insertUpdate(javax.swing.event.DocumentEvent event) { action.run(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent event) { action.run(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent event) { action.run(); }
    }
}
