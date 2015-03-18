package chromage.client;

import chromage.shared.Constants;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ahruss on 3/14/15.
 */
public class ConnectMenu extends JPanel implements AncestorListener {

    IConnectMenuDelegate delegate;

    public ConnectMenu(IConnectMenuDelegate delegate) {
        MenuStyles.styleMainPanel(this);
        this.delegate = delegate;
        System.out.println("Opening");
        addAncestorListener(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        Dimension textFieldSize = new Dimension(300, 80);

        final JTextField nameField = MenuStyles.createTextField();
        nameField.setText(Constants.DEFAULT_NAME);
        nameField.setPreferredSize(textFieldSize);
        nameField.setMinimumSize(textFieldSize);
        nameField.setMaximumSize(textFieldSize);

        final JTextField ipField = MenuStyles.createTextField();
        ipField.setText(Constants.DEFAULT_IP);
        ipField.setPreferredSize(textFieldSize);
        ipField.setMinimumSize(textFieldSize);
        ipField.setMaximumSize(textFieldSize);

        final JTextField portField = MenuStyles.createTextField();
        portField.setText(Integer.toString(Constants.DEFAULT_PORT));
        portField.setPreferredSize(textFieldSize);
        portField.setMinimumSize(textFieldSize);
        portField.setMaximumSize(textFieldSize);

        final JButton connectButton = MenuStyles.createButton("Connect");

        add(Box.createVerticalGlue());
        add(Box.createRigidArea(new Dimension(20,20)));
        add(nameField);
        add(Box.createRigidArea(new Dimension(20,20)));
        add(ipField);
        add(Box.createRigidArea(new Dimension(20,20)));
        add(portField);
        add(Box.createRigidArea(new Dimension(20,20)));
        add(connectButton);
        add(Box.createVerticalGlue());

        ipField.setVisible(true);
        portField.setVisible(true);
        connectButton.setVisible(true);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int port = Integer.parseInt(portField.getText());
                    String ip = ipField.getText();
                    ConnectMenu.this.delegate.initiateConnection(port, ip, nameField.getText());
                } catch (NumberFormatException ex) {
                    // port wasn't a number
                    portField.setText("9877");
                }
            }
        });
    }

    public void ancestorMoved(AncestorEvent e) {}
    public void ancestorRemoved(AncestorEvent e) {}
    public void ancestorAdded(AncestorEvent e) {
        System.out.println("Opened connect menu");
    }
}
