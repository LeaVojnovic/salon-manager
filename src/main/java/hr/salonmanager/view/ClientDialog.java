package hr.salonmanager.view;

import hr.salonmanager.model.Client;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

/** Forma za dodavanje i uređivanje klijenta. */
public class ClientDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final JTextField nameField = new JTextField(24);
    private final JTextField phoneField = new JTextField(24);
    private final JTextField emailField = new JTextField(24);
    private Client value;
    private boolean saved;

    public ClientDialog(Frame owner, Client initial) {
        super(owner, initial == null ? "Novi klijent" : "Uredi klijenta", true);
        value = initial == null ? null : new Client(initial.getId(), initial.getName(), initial.getPhone(), initial.getEmail());
        if (initial != null) {
            nameField.setText(initial.getName());
            phoneField.setText(initial.getPhone());
            emailField.setText(initial.getEmail());
        }
        JPanel form = DialogSupport.formPanel();
        DialogSupport.addRow(form, 0, "Ime:", nameField);
        DialogSupport.addRow(form, 1, "Telefon:", phoneField);
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
            DialogSupport.showError(this, new IllegalArgumentException("Ime klijenta je obavezno."));
            return;
        }
        value = new Client(value == null ? 0 : value.getId(), nameField.getText().trim(),
                phoneField.getText().trim(), emailField.getText().trim());
        saved = true;
        dispose();
    }

    public Client getValue() { return value; }
    public boolean isSaved() { return saved; }
}
