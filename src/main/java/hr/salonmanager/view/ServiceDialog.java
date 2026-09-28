package hr.salonmanager.view;

import hr.salonmanager.model.Service;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Frame;

/** Forma za dodavanje i uređivanje usluge. */
public class ServiceDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final JTextField nameField = new JTextField(24);
    private final JTextField priceField = new JTextField(24);
    private final JTextField durationField = new JTextField(24);
    private Service value;
    private boolean saved;

    public ServiceDialog(Frame owner, Service initial) {
        super(owner, initial == null ? "Nova usluga" : "Uredi uslugu", true);
        value = initial == null ? null : new Service(initial.getId(), initial.getName(), initial.getPrice(), initial.getDurationMinutes());
        if (initial != null) {
            nameField.setText(initial.getName());
            priceField.setText(String.valueOf(initial.getPrice()));
            durationField.setText(String.valueOf(initial.getDurationMinutes()));
        }
        JPanel form = DialogSupport.formPanel();
        DialogSupport.addRow(form, 0, "Naziv:", nameField);
        DialogSupport.addRow(form, 1, "Cijena:", priceField);
        DialogSupport.addRow(form, 2, "Trajanje (min):", durationField);
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
        try {
            if (nameField.getText().isBlank()) throw new IllegalArgumentException("Naziv usluge je obavezan.");
            double price = Double.parseDouble(priceField.getText().trim().replace(',', '.'));
            int duration = Integer.parseInt(durationField.getText().trim());
            if (price < 0 || duration <= 0) throw new IllegalArgumentException("Cijena i trajanje moraju biti ispravni.");
            value = new Service(value == null ? 0 : value.getId(), nameField.getText().trim(), price, duration);
            saved = true;
            dispose();
        } catch (NumberFormatException exception) {
            DialogSupport.showError(this, new IllegalArgumentException("Cijena i trajanje moraju biti brojevi."));
        } catch (IllegalArgumentException exception) {
            DialogSupport.showError(this, exception);
        }
    }

    public Service getValue() { return value; }
    public boolean isSaved() { return saved; }
}
