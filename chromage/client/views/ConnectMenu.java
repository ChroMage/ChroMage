package chromage.client.views;

import chromage.client.util.Configuration;
import chromage.client.MenuStyles;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A menu to connect to a server
 */
public class ConnectMenu extends JPanel implements AncestorListener {

    Delegate delegate;

    /**
     * Creates the connect menu and all its components
     * @param delegate
     */
    public ConnectMenu(Delegate delegate) {
        MenuStyles.styleMainPanel(this);
        this.delegate = delegate;
        System.out.println("Opening");
        addAncestorListener(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Dimension textFieldSize = new Dimension(300, 80);

        final JTextField nameField = MenuStyles.createTextField();
        nameField.setText(Configuration.PLAYER_NAME.get());
        nameField.setPreferredSize(textFieldSize);
        nameField.setMinimumSize(textFieldSize);
        nameField.setMaximumSize(textFieldSize);

        final JTextField ipField = MenuStyles.createTextField();
        ipField.setText(Configuration.SERVER_IP.get());
        ipField.setPreferredSize(textFieldSize);
        ipField.setMinimumSize(textFieldSize);
        ipField.setMaximumSize(textFieldSize);

        final JTextField portField = MenuStyles.createTextField();
        portField.setText(Integer.toString(Configuration.SERVER_PORT.get()));
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
                    Configuration.SERVER_IP.set(ip);
                    Configuration.SERVER_PORT.set(port);
                    Configuration.PLAYER_NAME.set(nameField.getText());
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

    public static interface Delegate {
        /**
         * Called when the user presses the connect button to connect to a sever
         * @param port          the port entered by the user
         * @param ipAddress     the ip address entered by the user
         * @param playerName    the player name entered by the user
         */
        public void initiateConnection(int port, String ipAddress, String playerName);
    }
}
