package chromage.client;

import chromage.shared.MageType;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by ahruss on 3/14/15.
 */
public class LobbyMenu extends JPanel implements AncestorListener {

    ILobbyMenuDelegate delegate;
    JList gameList;

    ArrayList<GameInfo> games;

    public LobbyMenu(ILobbyMenuDelegate delegate) {
        games = new ArrayList<GameInfo>();
        this.delegate = delegate;
        addAncestorListener(this);

        JButton newGameButton = new JButton("Create");
        JButton joinGameButton = new JButton("Join");
        gameList = new JList();

        add(gameList);
        add(newGameButton);
        add(joinGameButton);

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LobbyMenu.this.delegate.createGame(3, "", MageType.TODO);
            }
        });

        joinGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selected = LobbyMenu.this.gameList.getSelectedIndex();
                if (selected != -1) {
                    LobbyMenu.this.delegate.joinGame(LobbyMenu.this.games.get(selected).getUuid(), MageType.TODO);
                }
            }
        });

        gameList.setVisible(true);

    }

    public void ancestorMoved(AncestorEvent e) {}
    public void ancestorRemoved(AncestorEvent e) {}
    public void ancestorAdded(AncestorEvent e) {
        System.out.println("Opened lobby menu");
        games = delegate.getGameList();
        gameList.setListData(games.toArray());
    }
}
