package chromage.client;

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
        this.delegate = delegate;
        System.out.println("Opening");
        addAncestorListener(this);
        setLayout(new FlowLayout());
        final JTextField ipField = new JTextField("127.0.0.1");
        final JTextField portField = new JTextField("9877");
        final JButton connectButton = new JButton("Connect");

        add(ipField);
        add(portField);
        add(connectButton);

        ipField.setVisible(true);
        portField.setVisible(true);
        connectButton.setVisible(true);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int port = Integer.parseInt(portField.getText());
                    String ip = ipField.getText();
                    ConnectMenu.this.delegate.initiateConnection(port, ip);
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
