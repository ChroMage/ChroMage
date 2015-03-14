package chromage.client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyFrame extends JPanel implements KeyListener {

  private Point startPoint = new Point(0, 0);

  private Point endPoint = new Point(0, 0);

  public MyFrame() {
    addKeyListener(this);
  }

  public void keyPressed(KeyEvent evt) {
  }

  public void keyReleased(KeyEvent evt) {
	  int keyCode = evt.getKeyChar();
	    System.out.println(keyCode + " released");
  }

  public void keyTyped(KeyEvent evt) {
	  int keyCode = evt.getKeyChar();
	  int d;
	  int cap = 'A' - 'a';
	    if (evt.isShiftDown())
	      d = 20;
	    else
	      d = 5;
	    if (keyCode == 'a')
	      add(-d, 0);
	    else if (keyCode == 'd')
	      add(d, 0);
	    else if (keyCode == 'w')
	      add(0, -d);
	    else if (keyCode == 's')
	      add(0, d);
	    else if (keyCode == 'A')
		  add(-d, 0);
	    else if (keyCode == 'D')
	      add(d, 0);
	    else if (keyCode == 'W')
	      add(0, -d);
	    else if (keyCode == 'S')
	      add(0, d);
	    System.out.println(keyCode + " w,a,s,d: " + (int)'w' + " " + (int)'a' + " " + (int)'s' + " " + (int)'d');
  }

  public boolean isFocusable() {
    return true;
  }

  public void add(int dx, int dy) {
    endPoint.x += dx;
    endPoint.y += dy;
    Graphics g = getGraphics();
    g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
    g.dispose();
    startPoint.x = endPoint.x;
    startPoint.y = endPoint.y;
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setTitle("Sketch");
    frame.setSize(300, 200);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Container contentPane = frame.getContentPane();
    contentPane.add(new MyFrame());

    frame.setVisible(true);
  }
}