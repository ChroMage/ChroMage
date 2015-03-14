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
    JTextField gameSizeField;
    JTextField gameNameField;
    JRadioButton orange, green, purple;
    ButtonGroup typeRadio;
    ArrayList<GameInfo> games;

    public LobbyMenu(ILobbyMenuDelegate delegate) {
        games = new ArrayList<GameInfo>();
        this.delegate = delegate;
        addAncestorListener(this);

        gameList = new JList();

        JButton newGameButton = new JButton("Create");
        JButton joinGameButton = new JButton("Join");
        JButton refreshGameButton = new JButton("Refresh");
        JLabel gameSizeLabel = new JLabel("Game Size");
        gameSizeField = new JTextField("2");
        JLabel gameNameLabel = new JLabel("Game Name");
        gameNameField = new JTextField("ChroMage");

        orange = new JRadioButton("Red + Yellow");
        green = new JRadioButton("Blue + Yellow");
        purple = new JRadioButton("Red + Blue");
        typeRadio = new ButtonGroup();

        add(gameSizeLabel);
        add(gameSizeField);
        add(gameNameLabel);
        add(gameNameField);
        add(gameList);
        add(newGameButton);
        add(joinGameButton);
        add(refreshGameButton);
        typeRadio.add(orange);
        typeRadio.add(green);
        typeRadio.add(purple);
        add(orange);
        add(green);
        add(purple);

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LobbyMenu.this.delegate.createGame(
                        LobbyMenu.this.getGameSize(),
                        LobbyMenu.this.getGameName(),
                        LobbyMenu.this.getMageType());
            }
        });

        joinGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selected = LobbyMenu.this.gameList.getSelectedIndex();
                if (selected != -1) {
                    LobbyMenu.this.delegate.joinGame(LobbyMenu.this.games.get(selected).getUuid(),
                                                     LobbyMenu.this.getMageType());
                }
            }
        });

        refreshGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LobbyMenu.this.games = LobbyMenu.this.delegate.getGameList();
                LobbyMenu.this.gameList.setListData(LobbyMenu.this.games.toArray());
            }
        });

        gameList.setVisible(true);

    }

    public MageType getMageType() {
        if (orange.isSelected()) {
            return MageType.ORANGE;
        }
        else if (green.isSelected()) {
            return MageType.GREEN;
        }
        else if (purple.isSelected()) {
            return MageType.PURPLE;
        }
        return MageType.ORANGE;
    }

    public String getGameName() {
        return gameNameField.getText();
    }
    public int getGameSize() {
        return Integer.parseInt(gameSizeField.getText());
    }

    public void ancestorMoved(AncestorEvent e) {}
    public void ancestorRemoved(AncestorEvent e) {}
    public void ancestorAdded(AncestorEvent e) {
        System.out.println("Opened lobby menu");
        games = delegate.getGameList();
        gameList.setListData(games.toArray());
    }
}
