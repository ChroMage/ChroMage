package chromage.client.views;

import chromage.client.util.Configuration;
import chromage.shared.utils.GameInfo;
import chromage.client.MenuStyles;
import chromage.shared.MageType;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
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
    GridBagLayout layout;

    public LobbyMenu(ILobbyMenuDelegate delegate) {
        System.out.println("Joining lobby");
        games = new ArrayList<GameInfo>();
        this.delegate = delegate;
        addAncestorListener(this);

        setLayout(layout = new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        MenuStyles.styleMainPanel(this);
        gameList = MenuStyles.createGameList();

        JButton newGameButton = MenuStyles.createButton("Create");
        JButton joinGameButton = MenuStyles.createButton("Join");
        JButton refreshGameButton = MenuStyles.createButton("Refresh");
        JComponent gameSizeLabel = MenuStyles.createLabel("Game Size", 15);
        gameSizeField = MenuStyles.createTextField();
        gameSizeField.setText(Integer.toString(Configuration.GAME_SIZE.get()));
        JComponent gameNameLabel = MenuStyles.createLabel("Game Name", 15);
        gameNameField = MenuStyles.createTextField();
        gameNameField.setText(Configuration.GAME_NAME.get());

        orange = MenuStyles.createRadio("Red + Yellow");
        green = MenuStyles.createRadio("Blue + Yellow");
        purple = MenuStyles.createRadio("Red + Blue");
        typeRadio = new ButtonGroup();
        typeRadio.add(orange);
        typeRadio.add(green);
        typeRadio.add(purple);

        c.fill = GridBagConstraints.BOTH;

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        add(MenuStyles.createLabel("Select a game.", 15), c);

        c.weightx = c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 3;
        c.gridwidth = 2;

        JPanel p = new JPanel();
        p.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        p.add(gameList);
        p.setOpaque(false);
        JScrollPane listScrollPanel = new JScrollPane(p);
        MenuStyles.styleMainPanel(listScrollPanel.getViewport());
        listScrollPanel.setBorder(BorderFactory.createEmptyBorder());
        listScrollPanel.setPreferredSize(new Dimension(350, 50));
        add(listScrollPanel, c);

        c.weightx = c.weighty = 0.0;
        c.gridx = 2;
        c.gridy = 2;
        c.gridheight = 1;
        c.gridwidth = 1;
        add(refreshGameButton, c);

        c.gridx = 2;
        c.gridy = 3;
        c.gridheight = 1;
        c.gridwidth = 1;
        add(joinGameButton, c);

        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        c.gridwidth = 1;
        c.weighty = 2.0;

        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
        colorPanel.setOpaque(false);
        colorPanel.add(orange);
        colorPanel.add(green);
        colorPanel.add(purple);
        add(colorPanel, c);

        c.weighty = 0.0;

        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 4;
        c.gridheight = 1;
        c.gridwidth = 1;
        add(gameNameLabel, c);
        c.gridx = 0;
        c.gridy = 5;
        c.gridheight = 1;
        c.gridwidth = 1;
        add(gameNameField, c);


        c.weightx = 0.2;
        c.gridx = 1;
        c.gridy = 4;
        c.gridheight = 1;
        c.gridwidth = 1;
        add(gameSizeLabel, c);
        c.gridx = 1;
        c.gridy = 5;
        c.gridheight = 1;
        c.gridwidth = 1;
        add(gameSizeField, c);

        c.gridx = 2;
        c.gridy = 4;
        c.gridheight = 2;
        c.gridwidth = 1;
        add(newGameButton, c);

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDefaults();
                int players = getGameSize();
                if (players == -1) return;
                LobbyMenu.this.delegate.createGame(
                        players,
                        getGameName(),
                        getMageType());
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

    public void saveDefaults() {
        Configuration.GAME_NAME.set(getGameName());
        Configuration.GAME_SIZE.set(getGameSize());
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
        try {
            return Integer.parseInt(gameSizeField.getText());
        } catch (NumberFormatException e) {
            gameSizeField.setText("2");
            return -1;
        }
    }

    public void ancestorMoved(AncestorEvent e) {}
    public void ancestorRemoved(AncestorEvent e) {}
    public void ancestorAdded(AncestorEvent e) {
        System.out.println("Opened lobby menu");
        games = delegate.getGameList();
        gameList.setListData(games.toArray());
    }
}
