/*
 Keith Fosmire
CSC375 Concurrent Programming Fall 2016
Prject3
DETAILS:
This project uses the concept of parallel programming by using ConcuurentRecursiveAction and fork() Join().
The game looks for possible moves throught the sweeper class until it finds no more possible moves
 */
package project4;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author keith
 */
public class GameDisplay extends JPanel implements ActionListener {

    private static final int SML_SIDE = 3;
    private static final int SIDE = SML_SIDE * SML_SIDE;
    private static final int GAP = 3;
    private static final Color BG = Color.BLACK;
    private static final Dimension BTN_PREF_SIZE = new Dimension(80, 80);
    private JButton[] buttons = new JButton[81];
    private boolean firstMove = true;
    private JButton buttonA;
    private JButton buttonB;
    private JButton leaped;
    private JButton[] playButtons = new JButton[45];
    private int numButtons = 0;
    private Thread player;
    private Thread sweeper;
    private int score;
    private Thread t;
    private boolean auto = true;
    private JPanel panel = new JPanel(new GridLayout(9, 9));
    private ForkJoinPool pool;
    private Sweep sweep;
    private boolean stop = true;

    public GameDisplay() {

        setBackground(BG);
        setLayout(new GridLayout(1, 1, GAP, GAP));
        setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        add(panel);
        for (int i = 0; i < buttons.length; i++) {

            buttons[i] = new JButton();
            if (i == 0) {
                buttons[i].setName("play");
            } else {
                buttons[i].setName("x");
            }

            buttons[i].setBackground(Color.BLUE);
            buttons[i].setPreferredSize(BTN_PREF_SIZE);
            buttons[i].addActionListener(this);
            panel.add(buttons[i]);

        }
        setGamePieces();

    }

    private static void createAndShowGui() {
        GameDisplay mainPanel = new GameDisplay();
        JFrame frame = new JFrame("Peg Solitaire");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()
                -> {
            createAndShowGui();
        });
    }

    public void setGamePieces() {
        Image imgB;
        Image imgM;
        sweep = new Sweep(buttons, 0, 81, panel);
        pool = new ForkJoinPool();
        try {

            imgB = ImageIO.read(getClass().getResource("empty.png"));
            imgM = ImageIO.read(getClass().getResource("marble.png"));
            int i = 3;
            while (i < 6) {
                buttons[i].setBackground(Color.WHITE);
                buttons[i].setIcon(new ImageIcon(imgM));
                buttons[i].setName("m");
                ++i;
            }
            i = 12;
            while (i < 15) {
                buttons[i].setBackground(Color.WHITE);
                buttons[i].setIcon(new ImageIcon(imgM));
                buttons[i].setName("m");
                ++i;
            }
            i = 21;
            while (i < 24) {
                buttons[i].setBackground(Color.WHITE);
                buttons[i].setIcon(new ImageIcon(imgM));
                buttons[i].setName("m");
                ++i;
            }
            i = 27;
            while (i < 54) {
                if (i == 40) {
                    buttons[i].setBackground(Color.WHITE);
                    buttons[i].setIcon(new ImageIcon(imgB));
                    buttons[i].setName("b");
                } else {
                    buttons[i].setBackground(Color.WHITE);
                    buttons[i].setIcon(new ImageIcon(imgM));
                    buttons[i].setName("m");
                }
                ++i;
            }
            i = 57;
            while (i < 60) {
                buttons[i].setBackground(Color.WHITE);
                buttons[i].setIcon(new ImageIcon(imgM));
                buttons[i].setName("m");
                ++i;
            }
            i = 66;
            while (i < 69) {
                buttons[i].setBackground(Color.WHITE);
                buttons[i].setIcon(new ImageIcon(imgM));
                buttons[i].setName("m");
                ++i;
            }
            i = 75;
            while (i < 78) {
                buttons[i].setBackground(Color.WHITE);
                buttons[i].setIcon(new ImageIcon(imgM));
                buttons[i].setName("m");
                ++i;
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setBackground(BG);
        JButton temp = (JButton) e.getSource();
        if (temp.getName().equals("play")) {
            autoPlay();
            sweeper();
            stop = true;
        } else {

            if (temp.getBackground().equals(Color.BLUE)) {
            } else if (temp.getIcon() != null && firstMove) {

                firstMove = false;
                buttonA = temp;
            } else if (temp.getIcon() != null && !firstMove) {
                buttonB = temp;
                firstMove = true;
                try {
                    Image imgB = ImageIO.read(getClass().getResource("empty.png"));
                    Image imgM = ImageIO.read(getClass().getResource("marble.png"));
                    //temp.setIcon((new ImageIcon()));
                    if (buttonB.getName().equals("b") && buttonA.getName().equals("m")) {
                        int xA, xB, yA, yB;
                        xA = buttonA.getX();
                        yA = buttonA.getY();
                        xB = buttonB.getX();
                        yB = buttonB.getY();
                        if (xA == xB && yA != yB) {
                            if (yA > yB) {
                                if (yA - yB == 160) {
                                    Point p = new Point();
                                    p.setLocation(xB, yB + 80);
                                    temp = (JButton) panel.getComponentAt(p);
                                    if (temp.getName().equals("m")) {
                                        temp.setIcon(new ImageIcon(imgB));
                                        temp.setName("b");
                                        buttonA.setIcon(new ImageIcon(imgB));
                                        buttonA.setName("b");
                                        buttonB.setIcon(new ImageIcon(imgM));
                                        buttonB.setName("m");
                                        buttonA = null;
                                        buttonB = null;
                                    } else {
                                        buttonA = null;
                                        buttonB = null;
                                    }
                                }
                            } else {
                                if (yB - yA == 160) {
                                    Point p = new Point();
                                    p.setLocation(xB, yA + 80);
                                    temp = (JButton) panel.getComponentAt(p);
                                    if (temp.getName().equals("m")) {
                                        temp.setIcon(new ImageIcon(imgB));
                                        temp.setName("b");
                                        buttonA.setIcon(new ImageIcon(imgB));
                                        buttonA.setName("b");
                                        buttonB.setIcon(new ImageIcon(imgM));
                                        buttonB.setName("m");
                                        buttonA = null;
                                        buttonB = null;
                                    } else {
                                        buttonA = null;
                                        buttonB = null;
                                    }
                                }

                            }
                        } else if (yA == yB) {
                            if (xA > xB) {
                                if (xA - xB == 160) {
                                    Point p = new Point();
                                    p.setLocation(xB + 80, yB);
                                    temp = (JButton) panel.getComponentAt(p);
                                    if (temp.getName().equals("m")) {
                                        temp.setIcon(new ImageIcon(imgB));
                                        temp.setName("b");
                                        buttonA.setIcon(new ImageIcon(imgB));
                                        buttonA.setName("b");
                                        buttonB.setIcon(new ImageIcon(imgM));
                                        buttonB.setName("m");
                                        buttonA = null;
                                        buttonB = null;
                                    } else {
                                        buttonA = null;
                                        buttonB = null;
                                    }
                                }
                            } else {
                                if (xB - xA == 160) {
                                    Point p = new Point();
                                    p.setLocation(xA + 80, yA);
                                    temp = (JButton) panel.getComponentAt(p);
                                    if (temp.getName().equals("m")) {
                                        temp.setIcon(new ImageIcon(imgB));
                                        temp.setName("b");
                                        buttonA.setIcon(new ImageIcon(imgB));
                                        buttonA.setName("b");
                                        buttonB.setIcon(new ImageIcon(imgM));
                                        buttonB.setName("m");
                                        buttonA = null;
                                        buttonB = null;
                                    } else {
                                        buttonA = null;
                                        buttonB = null;
                                    }
                                }

                            }
                        } else {
                            buttonA = null;
                            buttonB = null;
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(GameDisplay.class.getName()).log(Level.SEVERE, null, ex);
                }
                //checkScore();
            }
        }

    }

    public void checkScore() {

        return;
    }

    public void autoPlay() {
        player = new Thread() {
            public void run() {
                while (stop) {
                    Random ran = new Random();
                    int rN = ran.nextInt(80);
                    JButton a = buttons[rN];
                    if (a.getName().equals("m")) {
                        int x = a.getX();
                        int y = a.getY();
                        JButton leaped;
                        JButton land;
                        if (!(x + 160 >= 640)) {

                            leaped = (JButton) panel.getComponentAt(x + 80, y);
                            land = (JButton) panel.getComponentAt(x + 160, y);
                            if (land.getName().equals("b") && leaped.getName().equals("m")) {
                                a.doClick();
                                land.doClick();
                            }
                        }
                        if ((x - 160 >= 0)) {

                            leaped = (JButton) panel.getComponentAt(x - 80, y);
                            land = (JButton) panel.getComponentAt(x - 160, y);
                            if (land.getName().equals("b") && leaped.getName().equals("m")) {
                                a.doClick();
                                land.doClick();
                            }
                        }
                        if (!(y + 160 >= 640)) {

                            leaped = (JButton) panel.getComponentAt(x, y + 80);
                            land = (JButton) panel.getComponentAt(x, y + 160);
                            if (land.getName().equals("b") && leaped.getName().equals("m")) {
                                a.doClick();
                                land.doClick();
                            }
                        }
                        if ((y - 160 >0)) {

                            leaped = (JButton) panel.getComponentAt(x, y - 80);
                            land = (JButton) panel.getComponentAt(x, y - 160);
                            if (land.getName().equals("b") && leaped.getName().equals("m")) {
                                a.doClick();
                                land.doClick();
                            }
                        }
                    }
                }
            }
        };
        player.start();
    }

    public void sweeper() {
        
        sweeper = new Thread() {
            public void run() {
               
                while (stop) {
                   
                    pool.invoke(sweep);
                    while (!sweep.isDone()) {
                    }
                    //System.out.println(sweep.getResult());
                    

                }
            }
        };
        sweeper.start();
    }
    
   public void youLose()
   {
       setBackground(Color.RED);
       JOptionPane.showMessageDialog(this,
               "YOU LOST! CLICK TO TRY AGAIN",
               "LOSER",
               JOptionPane.QUESTION_MESSAGE);
       
   }
}
