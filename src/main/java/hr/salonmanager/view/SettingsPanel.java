package hr.salonmanager.view;

import hr.salonmanager.model.SalonSettings;
import hr.salonmanager.service.SalonSettingsService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/** Prikaz i uređivanje osnovnih postavki salona. */
public class SettingsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final SalonSettingsService settingsService;
    private final JTextField nameField = new JTextField(28);
    private final JTextField addressField = new JTextField(28);
    private final JTextField phoneField = new JTextField(28);
    private final JTextField emailField = new JTextField(28);
    private final JTextField openingField = new JTextField(28);
    private final JTextField closingField = new JTextField(28);

    public SettingsPanel(SalonSettingsService settingsService) {
        super(new BorderLayout(12, 12));
        this.settingsService = settingsService;
        JPanel form = new JPanel(new GridBagLayout());
        addRow(form, 0, "Naziv salona:", nameField);
        addRow(form, 1, "Adresa:", addressField);
        addRow(form, 2, "Telefon:", phoneField);
        addRow(form, 3, "Email:", emailField);
        addRow(form, 4, "Otvaranje (HH:MM):", openingField);
        addRow(form, 5, "Zatvaranje (HH:MM):", closingField);
        JButton save = new JButton("Spremi");
        JButton refresh = new JButton("Osvježi");
        save.addActionListener(event -> save());
        refresh.addActionListener(event -> refresh());
        JPanel buttons = new JPanel();
        buttons.add(save);
        buttons.add(refresh);
        add(form, BorderLayout.NORTH);
        add(buttons, BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        SalonSettings settings = settingsService.getSettings();
        nameField.setText(settings.getSalonName());
        addressField.setText(settings.getAddress());
        phoneField.setText(settings.getPhone());
        emailField.setText(settings.getEmail());
        openingField.setText(settings.getOpeningTime().toString());
        closingField.setText(settings.getClosingTime().toString());
    }

    private void save() {
        try {
            SalonSettings current = settingsService.getSettings();
            current.setSalonName(nameField.getText().trim());
            current.setAddress(addressField.getText().trim());
            current.setPhone(phoneField.getText().trim());
            current.setEmail(emailField.getText().trim());
            current.setOpeningTime(LocalTime.parse(openingField.getText().trim()));
            current.setClosingTime(LocalTime.parse(closingField.getText().trim()));
            settingsService.save(current);
            javax.swing.JOptionPane.showMessageDialog(this, "Postavke su spremljene.");
        } catch (DateTimeParseException exception) {
            DialogSupport.showError(this, new IllegalArgumentException("Vrijeme mora biti u formatu HH:MM."));
        } catch (RuntimeException exception) {
            DialogSupport.showError(this, exception);
        }
    }

    private void addRow(JPanel panel, int row, String label, JTextField field) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(6, 6, 6, 12);
        panel.add(new JLabel(label), labelConstraints);
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.insets = new Insets(6, 0, 6, 6);
        panel.add(field, fieldConstraints);
    }
}
