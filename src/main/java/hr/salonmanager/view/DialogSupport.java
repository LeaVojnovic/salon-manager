package hr.salonmanager.view;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Zajednički pomoćni elementi Swing formi. */
final class DialogSupport {
    private DialogSupport() {
    }

    static JPanel formPanel() {
        return new JPanel(new GridBagLayout());
    }

    static void addRow(JPanel panel, int row, String label, JComponent field) {
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

    static JPanel buttons(JButton save, JButton cancel) {
        JPanel panel = new JPanel();
        panel.add(save);
        panel.add(cancel);
        return panel;
    }

    static void showError(java.awt.Component parent, Exception exception) {
        String message = exception.getMessage() == null ? "Dogodila se greška." : exception.getMessage();
        JOptionPane.showMessageDialog(parent, message, "Greška", JOptionPane.ERROR_MESSAGE);
    }
}
