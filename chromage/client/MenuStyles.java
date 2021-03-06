package chromage.client;

import chromage.shared.utils.GameInfo;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Manages the creation and styling of elements for menus
 */
public class MenuStyles {


    private static final Color TINT_COLOR = new Color(0, 88, 128);
    private static final Color FOREGROUND_COLOR = Color.WHITE;
    private static final Color BACKGROUND_COLOR = new Color(0, 22, 32);

    /**
     * Set up the style of the container panel
     * @param panel
     */
    public static void styleMainPanel(JComponent panel) {
        panel.setBackground(BACKGROUND_COLOR);
    }

    /**
     * Create a button with the given text.
     * @param text the button text
     * @return
     */
    public static JButton createButton(String text) {
        JButton b = new JButton(text) {

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(150, 80);
            }
            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();

            }
            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public void paintComponent(Graphics graphics) {
                // blindly casting is probably safe
                Graphics2D g = (Graphics2D)graphics;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g.setColor(TINT_COLOR.darker().darker());
                } else {
                    g.setColor(TINT_COLOR);
                }
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(TINT_COLOR.darker().darker());
                g.drawRect(0, 0, getWidth(), getHeight());

                g.setColor(FOREGROUND_COLOR);
                g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), 25));
                Rectangle2D stringBounds =  g.getFontMetrics().getStringBounds(getText(), g);
                double startX = getWidth() / 2 - stringBounds.getWidth() / 2;
                double startY = getHeight() / 2 + stringBounds.getHeight() / 3;
                g.drawString(getText(), (int)startX, (int)startY);
            }
        };
        return b;
    }

    /**
     * Create a text field
     * @return the text field created
     */
    public static JTextField createTextField() {
        JTextField t = new JTextField();

        t.setFont(new Font(t.getFont().getName(), t.getFont().getStyle(), 25));
        t.setBackground(TINT_COLOR.darker().darker());
        t.setForeground(FOREGROUND_COLOR);
        t.setCaretColor(FOREGROUND_COLOR.darker());
        t.setBorder(new CompoundBorder(new LineBorder(TINT_COLOR, 1), new EmptyBorder(15, 15, 15, 15)));
        return t;
    }

    /**
     * Create a label with default font size and padding.
     * @param text  the text of the label
     * @return
     */
    public static JLabel createLabel(String text) {
        return createLabel(text, 25);
    }

    /**
     * Create a label with default padding
     * @param text      the text of the label
     * @param fontSize  the font size to use, in points
     * @return
     */
    public static JLabel createLabel(String text, int fontSize) {
        return createLabel(text, fontSize, 5);
    }

    /**
     * Create a label with custom padding and font size
     * @param text          the label text
     * @param fontSize      the font size, in points
     * @param padding       the padding to use, in pixels
     * @return
     */
    public static JLabel createLabel(String text, int fontSize, int padding) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(l.getFont().getName(), l.getFont().getStyle(), fontSize));
        l.setForeground(FOREGROUND_COLOR);
        l.setBorder(new EmptyBorder(padding, padding, padding, padding));
        return l;
    }

    /**
     * Creates a game list, which expects its data elements to be objects of type GameInfo
     * @return
     */
    public static JList createGameList() {
        JList l = new JList();
        l.setForeground(FOREGROUND_COLOR);
        l.setBackground(TINT_COLOR.darker().darker());
        l.setCellRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                GameInfo game = (GameInfo)value;
                JPanel item = new JPanel();
                item.setOpaque(isSelected);
                item.setBackground(TINT_COLOR);
                item.setPreferredSize(new Dimension(300, 50));
                item.setLayout(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                c.fill = GridBagConstraints.HORIZONTAL;
                c.weightx = 1.0;
                c.gridx = 0;
                c.gridy = 0;
                c.gridheight = 1;
                c.gridwidth = 1;
                item.add(createLabel(game.getGameName(), 20, 10), c);
                c.weightx = 0.0;
                c.gridx = 1;
                c.gridy = 0;
                c.gridheight = 1;
                c.gridwidth = 1;
                c.anchor = GridBagConstraints.EAST;
                item.add(createLabel(game.getNumberOfPlayers() + " of " + game.getExpectedNumberOfPlayers(), 20, 10), c);
                return item;
            }
        });
        return l;
    }

    /**
     * Create a radio button
     * @param text  the radio button text
     * @return
     */
    public static JRadioButton createRadio(String text) {
        JRadioButton b = new JRadioButton(text);
        b.setFont(new Font(b.getFont().getName(), b.getFont().getStyle(), 18));
        b.setForeground(FOREGROUND_COLOR);
        return b;
    }

}
