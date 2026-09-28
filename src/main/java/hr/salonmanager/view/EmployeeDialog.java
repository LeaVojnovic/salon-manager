package hr.salonmanager.view;

import hr.salonmanager.model.Employee;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Frame;

/** Forma za dodavanje i uređivanje djelatnika. */
public class EmployeeDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final JTextField nameField = new JTextField(24);
    private final JTextField phoneField = new JTextField(24);
    private final JTextField emailField = new JTextField(24);
    private Employee value;
    private boolean saved;

    public EmployeeDialog(Frame owner, Employee initial) {
        super(owner, initial == null ? "Novi djelatnik" : "Uredi djelatnika", true);
        value = initial == null ? null : new Employee(initial.getId(), initial.getName(), initial.getPhone(), initial.getEmail());
        if (initial != null) {
            nameField.setText(initial.getName());
            phoneField.setText(initial.getPhone());
            emailField.setText(initial.getEmail());
        }
        JPanel form = DialogSupport.formPanel();
        DialogSupport.addRow(form, 0, "Ime:", nameField);
        DialogSupport.addRow(form, 1, "Broj:", phoneField);
        DialogSupport.addRow(form, 2, "Email:", emailField);
        JButton save = new JButton("Spremi");
        JButton cancel = new JButton("Odustani");
        save.addActionListener(event -> save());
        cancel.addActionListener(event -> dispose());
        add(form, BorderLayout.CENTER);
        add(DialogSupport.buttons(save, cancel), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }

    private void save() {
        if (nameField.getText().isBlank()) {
            DialogSupport.showError(this, new IllegalArgumentException("Ime djelatnika je obavezno."));
            return;
        }
        value = new Employee(value == null ? 0 : value.getId(), nameField.getText().trim(),
                phoneField.getText().trim(), emailField.getText().trim());
        saved = true;
        dispose();
    }

    public Employee getValue() { return value; }
    public boolean isSaved() { return saved; }
}
