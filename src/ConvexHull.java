package Project1;

import java.awt.Dimension;
import java.awt.*;
import static java.awt.Color.BLACK;
import static java.awt.Color.DARK_GRAY;
import static java.awt.Color.CYAN;
import static java.awt.Color.BLUE;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.ORANGE;
import static java.awt.Color.PINK;
import static java.awt.Color.GREEN;
import static java.awt.Color.LIGHT_GRAY;
import static java.awt.Color.RED;
import static java.awt.Color.YELLOW;
import static java.awt.Color.WHITE;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Thread.sleep;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Goheung Choi
 */
public class ConvexHull extends javax.swing.JFrame 
{
    private String fileName = "./src/data.xml";
    private final Color DEFAULT_COLOR = new java.awt.Color(51, 51, 51);
    private final int WAIT = 100;
    private boolean trace;
    private boolean init;
    private final int LEAST_WPOINTS = 11;
    private final int MOST_WPOINTS = 500;
    //position of the mouse
    private int xpos;
    private int ypos;
    //set the current color of points
    private final Color[] ColorOfPoints 
            = { BLUE, ORANGE, MAGENTA, PINK, GREEN, YELLOW, RED };
    //DecimalFormat to print out double values of points
    DecimalFormat format = new DecimalFormat("##0.00");
    //Graphics object for drawing on graphicJPanel
    private Graphics g;
    //array list to contain points in the convex hull
    private ArrayList<Point> convexHull = new ArrayList<>();
    //array list to contain points
    private ArrayList<Point> points = new ArrayList<>();
    //width of the graphicJPanel
    private final int WIDHT_G;
    //height of the graphicJPanel
    private final int HEIGHT_G;
    //center of the graphicJPanel in x direction
    private final int CENTER_W;
    //center of the graphicJPanel in y direction
    private final int CENTER_H;
    //ratio of Height divided by Width
    private final double RATIO;
    /* data members needed to be updated every time */
    //the number of points in the horizontal direction
    private int numWPoint;
    //the number of points in the vertical direction
    private int numHPoint;
    //the pixel scale of horizontal direction
    private int wScale;
    //the pixel scale of vertical direction
    private int hScale;
    
    /**
     * Creates new form ConvexHull
     */
    public ConvexHull() {
        initComponents();
        
        this.setLocationRelativeTo(null);   //set location
        this.getRootPane().setDefaultButton(displayJButton);    //set default button
        this.setIconImage(Toolkit.getDefaultToolkit().
                getImage("./src/GeometriesIcon.jpg"));   //set icon image
        
        this.g = graphicJPanel.getGraphics();
        this.WIDHT_G = graphicJPanel.getWidth();
        this.HEIGHT_G = graphicJPanel.getHeight();
        this.CENTER_W = WIDHT_G / 2;
        this.CENTER_H = HEIGHT_G / 2;
        this.RATIO = (double) HEIGHT_G / WIDHT_G;
        numWPoint = 11;
        numHPoint = (int) (numWPoint * RATIO);
        wScale = getWScale();
        hScale = getHScale();
    }
    
    private void update()
    {
        g.setColor(DEFAULT_COLOR);
        g.fillRect(0, 0, WIDHT_G, HEIGHT_G);
        numHPoint = (int) (numWPoint * RATIO);
        wScale = getWScale();
        hScale = getHScale();
                                
        drawOrigin();
        drawScale();
        
        for (Point point: points)
            drawPoint(point, CYAN);
    }
    
    /**
     * returns the number of pixels which is a horizontal 
     * distance between two point.
     * @return Integer -- the number of pixels on width
     */
    private int getWScale()
    {
        boolean odd = (numWPoint % 2) != 0;
        if (odd)
            return round((double)CENTER_W / (numWPoint / 2));
        else
            return round((double)WIDHT_G / (numWPoint - 1));
    }
    /**
     * returns the number of pixels which is a vertical
     * distance between two point.
     * @return Integer -- the number of pixels on height
     */
    private int getHScale()
    {
        boolean odd = (numHPoint % 2) != 0;
        if (odd)
            return round((double)CENTER_H / (numHPoint / 2));
        else
            return round((double)HEIGHT_G / (numHPoint - 1));
    }
    
    private int round(double x)
    {
        int intX = (int) x;
        if ((x - intX) >= 0 && (x - intX) < 5)
            return intX;
        else
            return intX + 1;
    }
    
    private void drawOrigin()
    {
        //draw x-axis
        g.setColor(BLACK);
        g.drawLine(0, CENTER_H, WIDHT_G, CENTER_H);
        //draw y-axis
        g.setColor(BLACK);
        g.drawLine(CENTER_W, 0, CENTER_W, HEIGHT_G);
        //draw the origin
        g.setColor(BLACK);
        g.drawString(String.valueOf(0), CENTER_W + 5, CENTER_H + 10);
    }
    
    private void drawScale()
    {
        int scale;
        /*over -5 ~ +5: delta X = 1*/
        if (numWPoint > 10 && numWPoint <= 20)
        {
            //draw lines in y direction
            for (int i = -((numWPoint + 10) / 2); i <= (numWPoint + 10) / 2; i++)
            {
                if ( i != 0)
                {
                    scale = CENTER_W + wScale * i;
                    g.setColor(DARK_GRAY);
                    g.drawLine(scale, 0, scale, HEIGHT_G);
                    g.setColor(BLACK);
                    g.drawString(String.valueOf(i), scale + 5, CENTER_H + 10);
                }
            }
            //draw lines in x direction
            for (int i = -((numWPoint + 10) / 2); i <= (numWPoint + 10) / 2; i++)
            {
                if ( i != 0)
                {
                    scale = CENTER_H - hScale * i;
                    g.setColor(DARK_GRAY);
                    g.drawLine(0, scale, WIDHT_G, scale);
                    g.setColor(BLACK);
                    g.drawString(String.valueOf(i), CENTER_W - 10, scale);
                }
            }
        }
        /*over -10 ~ +10: delta X = of 2*/
        else if (numWPoint > 20 && numWPoint <=36)
        {
            //draw lines in y direction
            for (int i = -((numWPoint + 20) / 2); i <= (numWPoint + 20) / 2; i += 2)
            {
                if ( i != 0)
                {
                    scale = CENTER_W + wScale * i;
                    g.setColor(DARK_GRAY);
                    g.drawLine(scale, 0, scale, HEIGHT_G);
                    g.setColor(BLACK);
                    g.drawString(String.valueOf(i), scale + 5, CENTER_H + 10);
                }
            }
            //draw lines in x direction
            for (int i = -((numWPoint + 20) / 2); i <= (numWPoint + 20) / 2; i += 2)
            {
                if ( i != 0)
                {
                    scale = CENTER_H - hScale * i;
                    g.setColor(DARK_GRAY);
                    g.drawLine(0, scale, WIDHT_G, scale);
                    g.setColor(BLACK);
                    g.drawString(String.valueOf(i), CENTER_W - 10, scale);
                }
            }
        }
        /*over -18 ~ +18: delta X = of 5*/
        else if (numWPoint > 36 && numWPoint <=90)
        {
            //draw lines in y direction
            for (int i = -((numWPoint + 50) / 2); i <= (numWPoint + 50) / 2; i += 5)
            {
                if ( i != 0)
                {
                    scale = CENTER_W + wScale * i;
                    g.setColor(DARK_GRAY);
                    g.drawLine(scale, 0, scale, HEIGHT_G);
                    g.setColor(BLACK);
                    g.drawString(String.valueOf(i), scale + 5, CENTER_H + 10);
                }
            }
            //draw lines in x direction
            for (int i = -((numWPoint + 50) / 2); i <= (numWPoint + 50) / 2; i += 5)
            {
                if ( i != 0)
                {
                    scale = CENTER_H - hScale * i;
                    g.setColor(DARK_GRAY);
                    g.drawLine(0, scale, WIDHT_G, scale);
                    g.setColor(BLACK);
                    g.drawString(String.valueOf(i), CENTER_W - 10, scale);
                }
            }
        }
        /*over -45 ~ +45: delta X = of 10*/
        else if (numWPoint > 90 && numWPoint <=200)
        {
            //draw lines in y direction
            for (int i = -((numWPoint + 100) / 2); i <= (numWPoint + 100) / 2; i += 10)
            {
                if ( i != 0)
                {
                    scale = CENTER_W + wScale * i;
                    g.setColor(DARK_GRAY);
                    g.drawLine(scale, 0, scale, HEIGHT_G);
                    g.setColor(BLACK);
                    g.drawString(String.valueOf(i), scale + 5, CENTER_H + 10);
                }
            }
            //draw lines in x direction
            for (int i = -((numWPoint + 100) / 2); i <= (numWPoint + 100) / 2; i += 10)
            {
                if ( i != 0)
                {
                    scale = CENTER_H - hScale * i;
                    g.setColor(DARK_GRAY);
                    g.drawLine(0, scale, WIDHT_G, scale);
                    g.setColor(BLACK);
                    g.drawString(String.valueOf(i), CENTER_W - 10, scale);
                }
            }
        }
        /*over -100 ~ +100: delta X = of 20*/
        else if (numWPoint > 200 && numWPoint <=400)
        {
            //draw lines in y direction
            for (int i = -((numWPoint + 200) / 2); i <= (numWPoint + 200) / 2; i += 20)
            {
                if ( i != 0)
                {
                    scale = CENTER_W + wScale * i;
                    g.setColor(DARK_GRAY);
                    g.drawLine(scale, 0, scale, HEIGHT_G);
                    g.setColor(BLACK);
                    g.drawString(String.valueOf(i), scale + 5, CENTER_H + 10);
                }
            }
            //draw lines in x direction
            for (int i = -((numWPoint + 200) / 2); i <= (numWPoint + 200) / 2; i += 20)
            {
                if ( i != 0)
                {
                    scale = CENTER_H - hScale * i;
                    g.setColor(DARK_GRAY);
                    g.drawLine(0, scale, WIDHT_G, scale);
                    g.setColor(BLACK);
                    g.drawString(String.valueOf(i), CENTER_W - 10, scale);
                }
            }
        }
        /*over -200 ~ +200: scale of 50*/
        else if (numWPoint > 400)
        {
            //draw lines in y direction
            for (int i = -((numWPoint + 1000) / 2); i <= (numWPoint + 1000) / 2; i += 50)
            {
                if ( i != 0)
                {
                    scale = CENTER_W + wScale * i;
                    g.setColor(DARK_GRAY);
                    g.drawLine(scale, 0, scale, HEIGHT_G);
                    g.setColor(BLACK);
                    g.drawString(String.valueOf(i), scale + 5, CENTER_H + 10);
                }
            }
            //draw lines in x direction
            for (int i = -((numWPoint + 1000) / 2); i <= (numWPoint + 1000) / 2; i += 50)
            {
                if ( i != 0)
                {
                    scale = CENTER_H - hScale * i;
                    g.setColor(DARK_GRAY);
                    g.drawLine(0, scale, WIDHT_G, scale);
                    g.setColor(BLACK);
                    g.drawString(String.valueOf(i), CENTER_W - 10, scale);
                }
            }
        }
    }

    private void drawPoint(Point p, Color color)
    {
        g.setColor(color);
        g.fillOval(getWPixels(p) - 2, getHPixels(p) - 2, 5, 5);
    }
    private void drawLine(Point p1, Point p2, Color color) 
    {
        g.setColor(color);
        g.drawLine(getWPixels(p1), getHPixels(p1), getWPixels(p2), getHPixels(p2));
    }

    private boolean pointExist(Point p)
    {
        boolean exist = false;
        for (Point point : points)
            if(point.equals(p))
                exist =  true;
        return exist;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgressBar1 = new javax.swing.JProgressBar();
        buttonGroup = new javax.swing.ButtonGroup();
        menuButtonGroup = new javax.swing.ButtonGroup();
        chJPanel = new javax.swing.JPanel();
        graphicJPanel = new javax.swing.JPanel();
        statsJPanel = new javax.swing.JPanel();
        algorithmJPanel = new javax.swing.JPanel();
        gwJCheckBox = new javax.swing.JCheckBox();
        gsJCheckBox = new javax.swing.JCheckBox();
        chJCheckBox = new javax.swing.JCheckBox();
        ksJCheckBox = new javax.swing.JCheckBox();
        displayJButton = new javax.swing.JButton();
        promptPointJLabel = new javax.swing.JLabel();
        tpJLabel = new javax.swing.JLabel();
        tGrahamsJLabel = new javax.swing.JLabel();
        pinJLabel = new javax.swing.JLabel();
        pointInCHJLabel = new javax.swing.JLabel();
        pinsideJLabel = new javax.swing.JLabel();
        pointInsideCHLabel = new javax.swing.JLabel();
        pJLabel = new javax.swing.JLabel();
        percentJLabel = new javax.swing.JLabel();
        statisticsJLabel = new javax.swing.JLabel();
        startJButton = new javax.swing.JButton();
        clearJButton = new javax.swing.JButton();
        gwJLabel = new javax.swing.JLabel();
        gsJLabel = new javax.swing.JLabel();
        caJLabel = new javax.swing.JLabel();
        ksJLabel = new javax.swing.JLabel();
        totalPointsJLabel = new javax.swing.JLabel();
        tGiftWrappingJLabel = new javax.swing.JLabel();
        tChansJLabel = new javax.swing.JLabel();
        tKirkSeidJLabel = new javax.swing.JLabel();
        comparisonJLabel = new javax.swing.JLabel();
        addJTextField = new javax.swing.JTextField();
        promptJLabel = new javax.swing.JLabel();
        addJButton = new javax.swing.JButton();
        addJButton1 = new javax.swing.JButton();
        chJMenuBar = new javax.swing.JMenuBar();
        fileJMenu = new javax.swing.JMenu();
        clearJMenuItem = new javax.swing.JMenuItem();
        openJMenuItem = new javax.swing.JMenuItem();
        printJMenuItem = new javax.swing.JMenuItem();
        printConvexHullJMenuItem = new javax.swing.JMenuItem();
        saveJMenuItem = new javax.swing.JMenuItem();
        fileJSeparator = new javax.swing.JPopupMenu.Separator();
        quitJMenuItem = new javax.swing.JMenuItem();
        methodJMenu = new javax.swing.JMenu();
        gwJCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        gsJCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        chJCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        ksJCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        HelpJMenu = new javax.swing.JMenu();
        aboutJMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        chJPanel.setBackground(new java.awt.Color(255, 255, 255));

        graphicJPanel.setBackground(new java.awt.Color(51, 51, 51));
        graphicJPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        graphicJPanel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        graphicJPanel.setPreferredSize(new java.awt.Dimension(1100, 800));
        graphicJPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                graphicJPanelMouseMoved(evt);
            }
        });
        graphicJPanel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                graphicJPanelMouseWheelMoved(evt);
            }
        });
        graphicJPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                graphicJPanelMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                graphicJPanelMouseExited(evt);
            }
        });

        javax.swing.GroupLayout graphicJPanelLayout = new javax.swing.GroupLayout(graphicJPanel);
        graphicJPanel.setLayout(graphicJPanelLayout);
        graphicJPanelLayout.setHorizontalGroup(
            graphicJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1098, Short.MAX_VALUE)
        );
        graphicJPanelLayout.setVerticalGroup(
            graphicJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );

        statsJPanel.setBackground(new java.awt.Color(0, 0, 0));
        statsJPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(null, javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0))));
        statsJPanel.setForeground(new java.awt.Color(51, 255, 255));

        algorithmJPanel.setBackground(new java.awt.Color(0, 0, 0));
        algorithmJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Algorithms", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("굴림", 0, 12), new java.awt.Color(204, 255, 255))); // NOI18N
        algorithmJPanel.setOpaque(false);

        gwJCheckBox.setBackground(new java.awt.Color(0, 0, 0));
        buttonGroup.add(gwJCheckBox);
        gwJCheckBox.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        gwJCheckBox.setForeground(new java.awt.Color(51, 255, 204));
        gwJCheckBox.setSelected(true);
        gwJCheckBox.setText("Gift-Wrapping(Jarvis March)");
        gwJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gwJCheckBoxActionPerformed(evt);
            }
        });

        gsJCheckBox.setBackground(new java.awt.Color(0, 0, 0));
        buttonGroup.add(gsJCheckBox);
        gsJCheckBox.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        gsJCheckBox.setForeground(new java.awt.Color(51, 255, 204));
        gsJCheckBox.setText("Graham's scan");
        gsJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsJCheckBoxActionPerformed(evt);
            }
        });

        chJCheckBox.setBackground(new java.awt.Color(0, 0, 0));
        buttonGroup.add(chJCheckBox);
        chJCheckBox.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        chJCheckBox.setForeground(new java.awt.Color(51, 255, 204));
        chJCheckBox.setText("Chan's");
        chJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chJCheckBoxActionPerformed(evt);
            }
        });

        ksJCheckBox.setBackground(new java.awt.Color(0, 0, 0));
        buttonGroup.add(ksJCheckBox);
        ksJCheckBox.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        ksJCheckBox.setForeground(new java.awt.Color(51, 255, 204));
        ksJCheckBox.setText("Kirkpatrick–Seidel");
        ksJCheckBox.setEnabled(false);
        ksJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ksJCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout algorithmJPanelLayout = new javax.swing.GroupLayout(algorithmJPanel);
        algorithmJPanel.setLayout(algorithmJPanelLayout);
        algorithmJPanelLayout.setHorizontalGroup(
            algorithmJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(algorithmJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(algorithmJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gwJCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(gsJCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chJCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ksJCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        algorithmJPanelLayout.setVerticalGroup(
            algorithmJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(algorithmJPanelLayout.createSequentialGroup()
                .addComponent(gwJCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(gsJCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chJCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ksJCheckBox))
        );

        displayJButton.setBackground(new java.awt.Color(153, 153, 153));
        displayJButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        displayJButton.setText("Display Convex Hull");
        displayJButton.setToolTipText("Display the convex hull without animation");
        displayJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayJButtonActionPerformed(evt);
            }
        });

        promptPointJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        promptPointJLabel.setForeground(new java.awt.Color(255, 255, 255));
        promptPointJLabel.setText("<html>Click your mouse or press 'P' <br> for generating points.<br> Click 'Start' button to show the convex hull<br> or 'Display' button to show <br> how an specific algorithm works</html>");

        tpJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tpJLabel.setForeground(new java.awt.Color(51, 255, 255));
        tpJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tpJLabel.setText("<html>Total points: </html>");

        tGrahamsJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tGrahamsJLabel.setForeground(new java.awt.Color(0, 255, 0));
        tGrahamsJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tGrahamsJLabel.setText("0");

        pinJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        pinJLabel.setForeground(new java.awt.Color(51, 255, 255));
        pinJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        pinJLabel.setText("<html>Points in convex hull: </html>");

        pointInCHJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        pointInCHJLabel.setForeground(new java.awt.Color(51, 255, 255));
        pointInCHJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        pointInCHJLabel.setText("0");

        pinsideJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        pinsideJLabel.setForeground(new java.awt.Color(51, 255, 255));
        pinsideJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        pinsideJLabel.setText("<html>Points inside of convex hull: </html>");

        pointInsideCHLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        pointInsideCHLabel.setForeground(new java.awt.Color(51, 255, 255));
        pointInsideCHLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        pointInsideCHLabel.setText("0");

        pJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        pJLabel.setForeground(new java.awt.Color(51, 255, 255));
        pJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        pJLabel.setText("<html>Percent of points in CH: </html>");

        percentJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        percentJLabel.setForeground(new java.awt.Color(51, 255, 255));
        percentJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        percentJLabel.setText("0");

        statisticsJLabel.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        statisticsJLabel.setForeground(new java.awt.Color(204, 255, 255));
        statisticsJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        statisticsJLabel.setText("<html>* Statistics *</html>");

        startJButton.setBackground(new java.awt.Color(153, 153, 153));
        startJButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        startJButton.setText("Start Simulation");
        startJButton.setToolTipText("Trace the process of checked algorithm");
        startJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startJButtonActionPerformed(evt);
            }
        });

        clearJButton.setBackground(new java.awt.Color(153, 153, 153));
        clearJButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        clearJButton.setText("Clear");
        clearJButton.setToolTipText("Clear every information");
        clearJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearJButtonActionPerformed(evt);
            }
        });

        gwJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        gwJLabel.setForeground(new java.awt.Color(0, 255, 0));
        gwJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gwJLabel.setText("Gift-Wrapping(Jarvis March): ");

        gsJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        gsJLabel.setForeground(new java.awt.Color(0, 255, 0));
        gsJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gsJLabel.setText("Graham's scan: ");

        caJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        caJLabel.setForeground(new java.awt.Color(0, 255, 0));
        caJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        caJLabel.setText("Chan's algorithm: ");

        ksJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        ksJLabel.setForeground(new java.awt.Color(0, 255, 0));
        ksJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ksJLabel.setText("Kirkpatrick–Seidel: ");

        totalPointsJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        totalPointsJLabel.setForeground(new java.awt.Color(51, 255, 255));
        totalPointsJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        totalPointsJLabel.setText("0");

        tGiftWrappingJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tGiftWrappingJLabel.setForeground(new java.awt.Color(0, 255, 0));
        tGiftWrappingJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tGiftWrappingJLabel.setText("0");

        tChansJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tChansJLabel.setForeground(new java.awt.Color(0, 255, 0));
        tChansJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tChansJLabel.setText("0");

        tKirkSeidJLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tKirkSeidJLabel.setForeground(new java.awt.Color(0, 255, 0));
        tKirkSeidJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tKirkSeidJLabel.setText("N/A");
        tKirkSeidJLabel.setToolTipText("Add a point to the graphic panel");

        comparisonJLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        comparisonJLabel.setForeground(new java.awt.Color(255, 255, 255));
        comparisonJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        comparisonJLabel.setText("* Comparison (in nanosecond) *");

        addJTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        addJTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addJTextFieldMouseClicked(evt);
            }
        });
        addJTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                addJTextFieldKeyPressed(evt);
            }
        });

        promptJLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        promptJLabel.setForeground(new java.awt.Color(255, 255, 0));
        promptJLabel.setText("<html>-Type a point in format of \"(a, b)\"<br> without quotation mark.</html>");

        addJButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        addJButton.setText("Add");
        addJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addJButtonActionPerformed(evt);
            }
        });

        addJButton1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        addJButton1.setText("Show Points");
        addJButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addJButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout statsJPanelLayout = new javax.swing.GroupLayout(statsJPanel);
        statsJPanel.setLayout(statsJPanelLayout);
        statsJPanelLayout.setHorizontalGroup(
            statsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(algorithmJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(displayJButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(statsJPanelLayout.createSequentialGroup()
                .addComponent(startJButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(clearJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(statsJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(promptJLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(statisticsJLabel)
                    .addComponent(tGrahamsJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tGiftWrappingJLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tChansJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tKirkSeidJLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comparisonJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(statsJPanelLayout.createSequentialGroup()
                        .addComponent(addJTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(statsJPanelLayout.createSequentialGroup()
                        .addGroup(statsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pinJLabel)
                            .addComponent(pJLabel)
                            .addComponent(pinsideJLabel)
                            .addComponent(tpJLabel, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGroup(statsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statsJPanelLayout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(totalPointsJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(statsJPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(statsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(pointInCHJLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pointInsideCHLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(percentJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(statsJPanelLayout.createSequentialGroup()
                        .addGroup(statsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(statsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(ksJLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(caJLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(gsJLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(gwJLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(promptPointJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(statsJPanelLayout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(addJButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        statsJPanelLayout.setVerticalGroup(
            statsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statsJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(promptPointJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(statisticsJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(statsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tpJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalPointsJLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(statsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pinJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pointInCHJLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(statsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pinsideJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pointInsideCHLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(statsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percentJLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(comparisonJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(gwJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tGiftWrappingJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gsJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tGrahamsJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(caJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tChansJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ksJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tKirkSeidJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(addJButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(promptJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(statsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addJButton)
                    .addGroup(statsJPanelLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(addJTextField)))
                .addGap(18, 18, 18)
                .addComponent(algorithmJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(displayJButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(statsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startJButton)
                    .addComponent(clearJButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout chJPanelLayout = new javax.swing.GroupLayout(chJPanel);
        chJPanel.setLayout(chJPanelLayout);
        chJPanelLayout.setHorizontalGroup(
            chJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chJPanelLayout.createSequentialGroup()
                .addComponent(graphicJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statsJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        chJPanelLayout.setVerticalGroup(
            chJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chJPanelLayout.createSequentialGroup()
                .addGroup(chJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(graphicJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 858, Short.MAX_VALUE)
                    .addComponent(statsJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        fileJMenu.setText("File");

        clearJMenuItem.setMnemonic('C');
        clearJMenuItem.setText("Clear");
        clearJMenuItem.setToolTipText("Clear every information");
        clearJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(clearJMenuItem);

        openJMenuItem.setMnemonic('O');
        openJMenuItem.setText("Open");
        openJMenuItem.setToolTipText("open a new xml file");
        openJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(openJMenuItem);

        printJMenuItem.setMnemonic('P');
        printJMenuItem.setText("Print");
        printJMenuItem.setToolTipText("print this GUI image");
        printJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(printJMenuItem);

        printConvexHullJMenuItem.setMnemonic('P');
        printConvexHullJMenuItem.setText("PrintCH");
        printConvexHullJMenuItem.setToolTipText("print this GUI image");
        printConvexHullJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printConvexHullJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(printConvexHullJMenuItem);

        saveJMenuItem.setMnemonic('S');
        saveJMenuItem.setText("Save");
        saveJMenuItem.setToolTipText("Save every input points");
        saveJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(saveJMenuItem);
        fileJMenu.add(fileJSeparator);

        quitJMenuItem.setMnemonic('Q');
        quitJMenuItem.setText("Quit");
        quitJMenuItem.setToolTipText("quit");
        quitJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(quitJMenuItem);

        chJMenuBar.add(fileJMenu);

        methodJMenu.setText("Method");

        menuButtonGroup.add(gwJCheckBoxMenuItem);
        gwJCheckBoxMenuItem.setMnemonic('G');
        gwJCheckBoxMenuItem.setSelected(true);
        gwJCheckBoxMenuItem.setText("Gift-Wrapping(Jarvis March)");
        gwJCheckBoxMenuItem.setToolTipText("");
        gwJCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gwJCheckBoxMenuItemActionPerformed(evt);
            }
        });
        methodJMenu.add(gwJCheckBoxMenuItem);

        menuButtonGroup.add(gsJCheckBoxMenuItem);
        gsJCheckBoxMenuItem.setMnemonic('s');
        gsJCheckBoxMenuItem.setText("Graham's scan");
        gsJCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsJCheckBoxMenuItemActionPerformed(evt);
            }
        });
        methodJMenu.add(gsJCheckBoxMenuItem);

        menuButtonGroup.add(chJCheckBoxMenuItem);
        chJCheckBoxMenuItem.setMnemonic('C');
        chJCheckBoxMenuItem.setText("Chan's");
        chJCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chJCheckBoxMenuItemActionPerformed(evt);
            }
        });
        methodJMenu.add(chJCheckBoxMenuItem);

        menuButtonGroup.add(ksJCheckBoxMenuItem);
        ksJCheckBoxMenuItem.setText("Kirkpatrick–Seidel");
        ksJCheckBoxMenuItem.setEnabled(false);
        ksJCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ksJCheckBoxMenuItemActionPerformed(evt);
            }
        });
        methodJMenu.add(ksJCheckBoxMenuItem);

        chJMenuBar.add(methodJMenu);

        HelpJMenu.setText("Help");

        aboutJMenuItem.setMnemonic('b');
        aboutJMenuItem.setText("About");
        aboutJMenuItem.setToolTipText("display the information about this GUI");
        aboutJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutJMenuItemActionPerformed(evt);
            }
        });
        HelpJMenu.add(aboutJMenuItem);

        chJMenuBar.add(HelpJMenu);

        setJMenuBar(chJMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(chJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void graphicJPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_graphicJPanelMouseExited
        addJTextField.setText("(0, 0)");
    }//GEN-LAST:event_graphicJPanelMouseExited

    private void graphicJPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_graphicJPanelMouseClicked
        if (evt.getButton() == evt.BUTTON1)
        {
            Point point = getPointInput();
            if(!pointExist(point))
            {
                points.add(point);
                update();
            }
            else
            {
                points.remove(point);
                update();
            }
        }
    }//GEN-LAST:event_graphicJPanelMouseClicked

    private void graphicJPanelMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_graphicJPanelMouseWheelMoved
        if (evt.getWheelRotation() > 0)
        {
            if(numWPoint < this.MOST_WPOINTS)
            {
                numWPoint++;
                update();
            }
            else
                JOptionPane.showMessageDialog(null, 
                    "Cannot minimize this screen anymore.",
                    "Screen Error", JOptionPane.ERROR_MESSAGE);
        }
        else if (evt.getWheelRotation() < 0)
        {
            if(numWPoint > this.LEAST_WPOINTS)
            {
                numWPoint--;
                update();
            }
            else
                JOptionPane.showMessageDialog(null, 
                    "Cannot enlarge this screen anymore.",
                    "Screen Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_graphicJPanelMouseWheelMoved

    private void displayJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayJButtonActionPerformed
        long startTime, stopTime, elapsedTime; 
        
        update();
        if (points.size() > 2)
        {
            try {
                //gw
                startTime = System.nanoTime();
                convexHull = giftWrapping(points);
                stopTime = System.nanoTime();
                elapsedTime = stopTime - startTime;
                tGiftWrappingJLabel.setText(String.valueOf(elapsedTime));
                //gs
                startTime = System.nanoTime();
                grahams(points);
                stopTime = System.nanoTime();
                elapsedTime = stopTime - startTime;
                tGrahamsJLabel.setText(String.valueOf(elapsedTime));
                //ch
                startTime = System.nanoTime();
                try{
                    chan(points);
                } catch (Exception ex)
                {
                }
                stopTime = System.nanoTime();
                elapsedTime = stopTime - startTime;
                tChansJLabel.setText(String.valueOf(elapsedTime));
                //js
//                startTime = System.nanoTime();
//                convexHull = js(points);
//                stopTime = System.nanoTime();
//                elapsedTime = stopTime - startTime;
//                tGiftWrappingJLabel.setText(String.valueOf(elapsedTime));
                totalPointsJLabel.setText(String.valueOf(points.size()));
                pointInCHJLabel.setText(String.valueOf(convexHull.size()));
                pointInsideCHLabel.setText(
                        String.valueOf(points.size() - convexHull.size()));
                percentJLabel.setText(
                        String.valueOf((float)((
                                (points.size() - convexHull.size())
                                        / (double)points.size()) * 100.00))
                );
                for (int i = 0; i < convexHull.size(); i++)
                {
                    if (i != convexHull.size() - 1)
                    {
                        drawPoint(convexHull.get(i), ORANGE);
                        drawLine(convexHull.get(i), convexHull.get(i + 1), MAGENTA);
                    }
                    else
                    {
                        drawPoint(convexHull.get(i), ORANGE);
                        drawLine(convexHull.get(i), convexHull.get(0), MAGENTA);
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ConvexHull.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, 
                    "Need more points to get a convex hull.",
                    "Too Less Points", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_displayJButtonActionPerformed

    private void graphicJPanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_graphicJPanelMouseMoved
        xpos = evt.getX();
        ypos = evt.getY();
        
        addJTextField.setText("(" + format.format(getXCoordinate(xpos)) + ", "
                + format.format(getYCoordinate(ypos)) + ")");
    }//GEN-LAST:event_graphicJPanelMouseMoved

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        if(!init)
        {
            drawOrigin();
            drawScale();
        }
    }//GEN-LAST:event_formWindowActivated

    private void addJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addJButtonActionPerformed
        Point point = getPointInput();
        if(!pointExist(point))
        {
            points.add(point);
            update();
        }
        else
        {
            points.remove(point);
            update();
        }
    }//GEN-LAST:event_addJButtonActionPerformed

    private void startJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startJButtonActionPerformed
        update();

        convexHull.clear();
        Thread t = new Thread()
        {
            public void run() {
                try {
                    trace = true;

                    if(gwJCheckBox.isSelected())
                        convexHull = giftWrapping(points);
                    if(gsJCheckBox.isSelected())
                        convexHull = grahams(points);
                    if(chJCheckBox.isSelected())
                        try{
                            convexHull = chan(points);
                        } catch (Exception ex)
                        {
                        }
                    if(ksJCheckBox.isSelected())
                        convexHull = giftWrapping(points);

                    trace = false;
                    update();
                    convexHull.clear();
                    convexHull = giftWrapping(points);
                    for (int i = 0; i < convexHull.size(); i++)
                    {
                        if (i != convexHull.size() - 1)
                        {
                            drawPoint(convexHull.get(i), ORANGE);
                            drawLine(convexHull.get(i), convexHull.get(i + 1), MAGENTA);
                        }
                        else
                        {
                            drawPoint(convexHull.get(i), ORANGE);
                            drawLine(convexHull.get(i), convexHull.get(0), MAGENTA);
                        }
                    }
                    totalPointsJLabel.setText(String.valueOf(points.size()));
                    pointInCHJLabel.setText(String.valueOf(convexHull.size()));
                    pointInsideCHLabel.setText(
                            String.valueOf(points.size() - convexHull.size()));
                    percentJLabel.setText(format.format(
                            String.valueOf(((
                                    (points.size() - convexHull.size())
                                            / (double)points.size()) * 100))) );
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConvexHull.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        t.start();
    }//GEN-LAST:event_startJButtonActionPerformed

    private void addJTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_addJTextFieldKeyPressed
        Random rnd = new Random();
        if (evt.getKeyChar() == 'p')
        {
            addJTextField.setEditable(false);
            for (int i = 0; i < 15; i++)
                points.add(new Point(rnd.nextFloat() * numWPoint - (numWPoint / 2) - (float) 0.5,
                rnd.nextFloat() * numHPoint - (numHPoint / 2) - (float) 0.5));
        }
        update();
    }//GEN-LAST:event_addJTextFieldKeyPressed

    private void addJTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addJTextFieldMouseClicked
        addJTextField.setEditable(true);
    }//GEN-LAST:event_addJTextFieldMouseClicked

    private void clearJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearJButtonActionPerformed
        numWPoint = 11;
        points.clear();
        convexHull.clear();
        update();
        totalPointsJLabel.setText("0");
        pointInCHJLabel.setText("0");
        pointInsideCHLabel.setText("0");
        percentJLabel.setText("0");
        tGiftWrappingJLabel.setText("0");
        tGrahamsJLabel.setText("0");
        tChansJLabel.setText("0");
        tKirkSeidJLabel.setText("0");
        gwJCheckBox.setSelected(true);
    }//GEN-LAST:event_clearJButtonActionPerformed

    private void clearJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearJMenuItemActionPerformed
        clearJButton.doClick();
    }//GEN-LAST:event_clearJMenuItemActionPerformed

    private void openJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openJMenuItemActionPerformed
        JFileChooser chooser = new JFileChooser("./src/");
        // Calls the JFileChooser
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Text Files", "xml");
        chooser.setFileFilter(filter);
        int choice = chooser.showOpenDialog(null);
        
        // When choice is "yes"
        if(choice == JFileChooser.APPROVE_OPTION) {
            // reset the parcels ArrayList
            points.clear();
            // get the name of the chosen file
            File chosenfile = chooser.getSelectedFile();
            fileName = "./src/" + chosenfile.getName();
            // set the direction of XML factory
            points = XMLFactory.fileReader(fileName);
            
            update();
        }
        else {
            JOptionPane.showMessageDialog(null, "Unable to open file", 
                    "File Input Error", JOptionPane.PLAIN_MESSAGE);
        }
    }//GEN-LAST:event_openJMenuItemActionPerformed

    private void saveJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveJMenuItemActionPerformed
        XMLFactory.fileBuilder(points, fileName);
    }//GEN-LAST:event_saveJMenuItemActionPerformed

    private void printJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printJMenuItemActionPerformed
        PrintUtilities.printComponent(this);
    }//GEN-LAST:event_printJMenuItemActionPerformed

    private void quitJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitJMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_quitJMenuItemActionPerformed

    private void aboutJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutJMenuItemActionPerformed
        About myAbout = new About(this, true);
        myAbout.setVisible(true);
    }//GEN-LAST:event_aboutJMenuItemActionPerformed

    private void gwJCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gwJCheckBoxMenuItemActionPerformed
        gwJCheckBox.setSelected(true);
        update();
    }//GEN-LAST:event_gwJCheckBoxMenuItemActionPerformed

    private void gsJCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsJCheckBoxMenuItemActionPerformed
        gsJCheckBox.setSelected(true);
        update();
    }//GEN-LAST:event_gsJCheckBoxMenuItemActionPerformed

    private void chJCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chJCheckBoxMenuItemActionPerformed
        chJCheckBox.setSelected(true);
        update();
    }//GEN-LAST:event_chJCheckBoxMenuItemActionPerformed

    private void ksJCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ksJCheckBoxMenuItemActionPerformed
        ksJCheckBox.setSelected(true);
        update();
    }//GEN-LAST:event_ksJCheckBoxMenuItemActionPerformed

    private void gwJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gwJCheckBoxActionPerformed
        gwJCheckBoxMenuItem.setSelected(true);
    }//GEN-LAST:event_gwJCheckBoxActionPerformed

    private void gsJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsJCheckBoxActionPerformed
        gsJCheckBoxMenuItem.setSelected(true);
    }//GEN-LAST:event_gsJCheckBoxActionPerformed

    private void chJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chJCheckBoxActionPerformed
        chJCheckBoxMenuItem.setSelected(true);
    }//GEN-LAST:event_chJCheckBoxActionPerformed

    private void ksJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ksJCheckBoxActionPerformed
        ksJCheckBoxMenuItem.setSelected(true);
    }//GEN-LAST:event_ksJCheckBoxActionPerformed

    private void addJButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addJButton1ActionPerformed
        ShowPoints myShow = new ShowPoints(points);
        myShow.setVisible(true);
    }//GEN-LAST:event_addJButton1ActionPerformed

    private void printConvexHullJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printConvexHullJMenuItemActionPerformed
        JTextArea printJTextArea = new JTextArea();
        StringBuffer str = new StringBuffer();
        try{
            for (int i = 0; i < points.size(); i++)
            str.append("(")
                    .append(format.format(points.get(i).getX()))
                    .append(", ")
                    .append(format.format(points.get(i).getY()))
                    .append(")\n");
            printJTextArea.setText(str.toString());
            printJTextArea.print();
        } catch (PrinterException ex)
        {
            Logger.getLogger(ConvexHull.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_printConvexHullJMenuItemActionPerformed
    
    private Point getPointInput()
    {
        ArrayList<Float> list = new ArrayList<>();
        if (!isPoint(addJTextField.getText()))
        {
            addJTextField.setText(" ");
            JOptionPane.showMessageDialog(null, 
                    "Enter valid inputs",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            update();
            return null;
        }
        else
        {
            String expression = insertBlanks(addJTextField.getText());
            String[] tokens = expression.split(" ");
            for(String token: tokens)
            {
                if (token.length() == 0 ||
                        token.trim().charAt(0) == '(' ||
                        token.trim().charAt(0) == ')' ||
                        token.charAt(0) == ',' )     //blank spaces
                    continue;
                else
                {
                    try
                    {
                    list.add(Float.parseFloat(token));
                    }
                    catch (NumberFormatException ex)
                    {
                        addJTextField.setText(" ");
                        JOptionPane.showMessageDialog(null, 
                        "Enter valid inputs",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                        update();
                        return null;
                    }
                }
            }
            return new Point(list.get(0), list.get(1));
        }
    }
    
    public static boolean isPoint(String fieldValue) {
        int parenthesis = 0;
        boolean firstnum = false, secondnum = false;
        
        Pattern pat = Pattern.compile("^[\\d\\s\\(\\)\\,\\.]+$");
        Matcher mat = pat.matcher(fieldValue);
        
        
        fieldValue = insertBlanks(fieldValue);
        String[] tokens = fieldValue.split(" ");
        
        for(String token : tokens) {
            if(token.length() == 0)     //blank spaces
                continue;
            else if(!firstnum && isNumeric(token))
                firstnum = true;
            else if(!secondnum && isNumeric(token))
                secondnum = true;
            else if(parenthesis < 0)     //if ')' comes first -> not valid
                return false;
            else if(token.trim().charAt(0) == '(')
                parenthesis++;
            else if(token.trim().charAt(0) == ')')
                parenthesis--;
        }
        
        if(!(firstnum && secondnum))
            return false;
        
        return mat.matches() || parenthesis == 0;
    }
    
    private static String insertBlanks(String str) {
        String result = "";
        for(int i = 0; i < str.length(); i++) {
            if(str.charAt(i) == '(' || str.charAt(i) == ')' ||
                    str.charAt(i) == ',')
                result += " " + str.charAt(i) + " ";
            else
                result += str.charAt(i);
        }
        return result;
    }
    public static boolean isNumeric(String var) {
        try {
            double n = Double.parseDouble(var);
        }
        catch(NumberFormatException nfe) {
            return false;
        }
    return true;
}
    /**
     * Returns the x value of horizontal pixels on graphicJPanel when the center
     * of graphicJPanel is the origin (0, 0).
     * @param wPixel a value in horizontal pixel on graphicJPanel
     * @return Double -- x coordinate
     */
    private double getXCoordinate(int wPixel)
    {
        return (wPixel - CENTER_W) / (double) wScale;
    }
    /**
     * Returns the y value of vertical pixels on graphicJPanel when the center
     * of graphicJPanel is the origin (0, 0).
     * @param hPixel a value in vertical pixel on graphicJPanel
     * @return Double -- y coordinate
     */
    private double getYCoordinate(int hPixel)
    {
        return (CENTER_H - hPixel) / (double) hScale;
    }
    /**
     * Returns the number of horizontal pixels needed to draw a x coordinate 
     * of a point
     * @param point a point for getting x coordinate to convert into pixels
     * on graphicJPanel
     * @return Integer -- horizontal pixels of the certain x coordinate 
     * of the point
     */
    private int getWPixels(Point point)
    {
        return CENTER_W + (int) (point.getX() * wScale);
    }
    /**
     * Returns the number of vertical pixels needed to draw a x coordinate 
     * of a point
     * @param point a point for getting y coordinate to convert into pixels
     * on graphicJPanel
     * @return Integer -- vertical pixels of the certain x coordinate 
     * of the point
     */
    private int getHPixels(Point point)
    {
        return CENTER_H - (int) (point.getY() * hScale);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ConvexHull.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConvexHull.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConvexHull.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConvexHull.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConvexHull().setVisible(true);
            }
        });
    }
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu HelpJMenu;
    private javax.swing.JMenuItem aboutJMenuItem;
    private javax.swing.JButton addJButton;
    private javax.swing.JButton addJButton1;
    private javax.swing.JTextField addJTextField;
    private javax.swing.JPanel algorithmJPanel;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JLabel caJLabel;
    private javax.swing.JCheckBox chJCheckBox;
    private javax.swing.JCheckBoxMenuItem chJCheckBoxMenuItem;
    private javax.swing.JMenuBar chJMenuBar;
    private javax.swing.JPanel chJPanel;
    private javax.swing.JButton clearJButton;
    private javax.swing.JMenuItem clearJMenuItem;
    private javax.swing.JLabel comparisonJLabel;
    private javax.swing.JButton displayJButton;
    private javax.swing.JMenu fileJMenu;
    private javax.swing.JPopupMenu.Separator fileJSeparator;
    private javax.swing.JPanel graphicJPanel;
    private javax.swing.JCheckBox gsJCheckBox;
    private javax.swing.JCheckBoxMenuItem gsJCheckBoxMenuItem;
    private javax.swing.JLabel gsJLabel;
    private javax.swing.JCheckBox gwJCheckBox;
    private javax.swing.JCheckBoxMenuItem gwJCheckBoxMenuItem;
    private javax.swing.JLabel gwJLabel;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JCheckBox ksJCheckBox;
    private javax.swing.JCheckBoxMenuItem ksJCheckBoxMenuItem;
    private javax.swing.JLabel ksJLabel;
    private javax.swing.ButtonGroup menuButtonGroup;
    private javax.swing.JMenu methodJMenu;
    private javax.swing.JMenuItem openJMenuItem;
    private javax.swing.JLabel pJLabel;
    private javax.swing.JLabel percentJLabel;
    private javax.swing.JLabel pinJLabel;
    private javax.swing.JLabel pinsideJLabel;
    private javax.swing.JLabel pointInCHJLabel;
    private javax.swing.JLabel pointInsideCHLabel;
    private javax.swing.JMenuItem printConvexHullJMenuItem;
    private javax.swing.JMenuItem printJMenuItem;
    private javax.swing.JLabel promptJLabel;
    private javax.swing.JLabel promptPointJLabel;
    private javax.swing.JMenuItem quitJMenuItem;
    private javax.swing.JMenuItem saveJMenuItem;
    private javax.swing.JButton startJButton;
    private javax.swing.JLabel statisticsJLabel;
    private javax.swing.JPanel statsJPanel;
    private javax.swing.JLabel tChansJLabel;
    private javax.swing.JLabel tGiftWrappingJLabel;
    private javax.swing.JLabel tGrahamsJLabel;
    private javax.swing.JLabel tKirkSeidJLabel;
    private javax.swing.JLabel totalPointsJLabel;
    private javax.swing.JLabel tpJLabel;
    // End of variables declaration//GEN-END:variables
    /**
     * Returns ArrayList of a convex hull among input points by Gift-Wrapping 
     * (also, called Jarvis march) algorithm.<br>Also, it helps tracing this
     * algorithm stem by stem.
     * This algorithm has O(hn) of efficiency.
     * @param points An ArrayList which contains points for getting a convex hull
     * @return ArrayList that contains the points on the convex hull
     */
    private ArrayList<Point> giftWrapping(ArrayList<Point> points) throws InterruptedException
    {
        boolean foundAll = false;
        //create an arraylist which is going to contain points on a convex hull.
        ArrayList<Point> convexHull = new ArrayList<>();
        //copy the input points into another array
        ArrayList<Point> copyPoints = new ArrayList<>();
        copyPoints.addAll(points);
        //Step1: find the rightmost lowest point
        Point h = findH(copyPoints);
        //assign the rightmost lowest point to t0.
        Point t0 = h;
        if (trace)
        {
            sleep(WAIT);
            drawPoint(t0, PINK);
        }
        //create t1.
        Point t1;
        //create a test point.
        Point testP;
        //start do-while loop -- loop through all points and find convexHull
        do
        {
            //add t0 into the ArrayList named 'convexHull'.
            convexHull.add(t0);
            //assign the first point into t1 for comparison with respect to t0
            t1 = copyPoints.get(0);
            if (trace)
            {
                sleep(WAIT);
                drawPoint(t1, PINK);
                drawLine(t0, t1, RED);
            }
            //start for loop -- find the next t1.
            for (int i = 0; i < copyPoints.size(); i++)
            {
                if (trace)
                {
                    for (int k = 0; k < convexHull.size(); k++)
                    {
                        drawPoint(convexHull.get(k), ORANGE);
                        if (convexHull.size() > 1 && k < convexHull.size() - 1)
                            drawLine(convexHull.get(k), convexHull.get(k + 1), MAGENTA);
                    }
                }
                testP = copyPoints.get(i);
                //draw a line from t0 to t1
                if (trace)
                {
                    sleep(WAIT);
                    drawPoint(testP, BLUE);
                    drawLine(t0, testP, LIGHT_GRAY);
                }
                //if the test point is located on the right side than t1.
                if (direction(t0, t1, testP) > 0)
                {
                    // *set testP to be t1*
                    //draw a line form t1 to testP
                    if (trace)
                    {
                        sleep(WAIT);
                        drawPoint(t1, CYAN);
                        drawPoint(testP, CYAN);
                        drawLine(t0, t1, DEFAULT_COLOR);
                    }
                    t1 = testP;
                    if (trace)
                    {
                        drawPoint(t1, PINK);
                        drawLine(t0, t1, RED);
                    }
                }
                //if the test point is located on the same line on which t0 and t1 is
                else if (direction(t0, t1, testP) == 0)
                {
                    //and if the test point is further to the t0 than t1 is.
                    /*(This if statement also prevents t1 from being the same point)
                        case 1: i = copyPoints.get(0) and t1 = copyPoints.get(0)
                        case 2: t0 = t1 = testP = h                           */
                    if (distance(t0, t1) < distance(t0, testP))
                    {
                        // *set testP to be t1*
                        if (trace)
                        {
                            sleep(WAIT);
                            drawPoint(t1, CYAN);
                            drawPoint(testP, CYAN);
                            drawLine(t0, t1, DEFAULT_COLOR);
                        }
                        t1 = testP;
                        if (trace)
                        {
                            drawPoint(t1, PINK);
                            drawLine(t0, t1, RED);
                        }
                    }
                    else
                    {
                        //refresh the point and line
                        if (trace)
                        {
                            sleep(WAIT);
                            drawPoint(testP, CYAN);
                            drawLine(t0, testP, DEFAULT_COLOR);
                        }
                    }
                }
                else
                {
                    //refresh the point and line
                    if (trace)
                    {
                        sleep(WAIT);
                        drawPoint(testP, CYAN);
                        drawLine(t0, testP, DEFAULT_COLOR);
                    }
                }
            }
            //draw unfinished convex 
            if (trace)
            {
                sleep(WAIT);
                for (int i = 0; i < convexHull.size(); i++)
                {
                    drawPoint(convexHull.get(i), ORANGE);
                    if (convexHull.size() > 1 && i < convexHull.size() - 1)
                            drawLine(convexHull.get(i), convexHull.get(i + 1), MAGENTA);
                }
            }
            //reset t0 to be t1 for finding a new point of convex hull
            t0 = t1;
//            //if the found t1 already exists in the convexHull.
//            if (convexHull.get(convexHull.size() - 1) == t1)
//                //exit this do-while loop
//                foundAll = true;
            //if the found t1 is h point.
            if (h == t0)
            {
                for (int i = 0; i < convexHull.size(); i++)
                {
                    if (i != convexHull.size() - 1)
                    {
                        drawPoint(convexHull.get(i), ORANGE);
                        drawLine(convexHull.get(i), convexHull.get(i + 1), MAGENTA);
                    }
                    else
                    {
                        drawPoint(convexHull.get(i), ORANGE);
                        drawLine(convexHull.get(i), convexHull.get(0), MAGENTA);
                    }
                }
                //exit this do-while loop
                foundAll = true;
            }
        } while(!foundAll);
        //end loop
        return convexHull;
    }
    /**
     * Returns ArrayList of a convex hull among input points by Graham's 
     * algorithm.<br>
     * This algorithm has O(n) of efficiency.
     * @param points An ArrayList which contains points for getting a convex hull
     * @return ArrayList that contains the points on the convex hull
     */
    private ArrayList<Point> grahams(ArrayList<Point> points) throws InterruptedException
    {
        //create an stack which is going to contain points on a convex hull.
        Stack<Point> convexHull = new Stack<>();
        //copy the input points into another array
        ArrayList<Point> copyPoints = new ArrayList<>();
        copyPoints.addAll(points);
        //Step1: find the rightmost lowest point
        Point h = findH(copyPoints);
        //sort ArrayList<Point> copyPoints
        sortPoints(copyPoints, h);
        //Push p0, p1, and p2 into stack convexHull
        convexHull.push(copyPoints.get(0));
        if (trace)
        {
            sleep(WAIT);
            drawPoint(copyPoints.get(0), ORANGE);
        }
        convexHull.push(copyPoints.get(1));
        if (trace)
        {
            sleep(WAIT);
            drawPoint(copyPoints.get(1), ORANGE);
            drawLine(copyPoints.get(0), copyPoints.get(1), MAGENTA);
        }
        convexHull.push(copyPoints.get(2));
        if (trace)
        {
            sleep(WAIT);
            drawPoint(copyPoints.get(2), PINK);
            drawLine(copyPoints.get(1), copyPoints.get(2), RED);
        }
        //get points in a convex hull
        int i = 3;
        while (i < copyPoints.size())
        {
            Point t1 = convexHull.peek_first();
            Point t2 = convexHull.peek_second();
            
            if (trace)
            {
                sleep(WAIT);
                drawPoint(copyPoints.get(i), BLUE);
                drawLine(t1, copyPoints.get(i), LIGHT_GRAY);
            }
            //if pi is on the left side of the direct line from t2 to t1
            if (direction(t2, t1, copyPoints.get(i)) < 0)
            {
                if (trace)
                {
                    sleep(WAIT);
                    drawLine(t1, t2, MAGENTA);
                }
                if (trace)
                {
                    sleep(WAIT);
                    drawPoint(copyPoints.get(i), ORANGE);
                    drawLine(t1, copyPoints.get(i), RED);
                }
                //push pi to convexHull
                convexHull.push(copyPoints.get(i));
                //consider the next point in copyPoints
                i++;
            }
            else
            {
                if (trace)
                {
                    sleep(WAIT);

                    drawLine(t1, copyPoints.get(i), DEFAULT_COLOR);
                    drawLine(t2, t1, DEFAULT_COLOR);
                    drawPoint(t1, CYAN);

                }
                //pop the top element off stack convexHull
                convexHull.pop();
            }
        }
        if (trace)
        {
            sleep(WAIT);
            for (int count = 0; count < convexHull.toArrayList().size(); count++)
            {
                if (count != convexHull.toArrayList().size() - 1)
                {
                    drawPoint(convexHull.toArrayList().get(count), ORANGE);
                    drawLine(convexHull.toArrayList().get(count), convexHull.toArrayList().get(count + 1), MAGENTA);
                }
                else
                {
                    drawPoint(convexHull.toArrayList().get(count), ORANGE);
                    drawLine(convexHull.toArrayList().get(count), convexHull.toArrayList().get(0), MAGENTA);
                }
            }
        }
        return convexHull.toArrayList();
    }
    /**
     * Returns ArrayList of a convex hull among input points by Graham's 
     * algorithm.<br>
     * This algorithm has O(nlogh) of efficiency.
     * @param points An ArrayList which contains points for getting a convex hull
     * @return ArrayList that contains the points on the convex hull
     */
    private ArrayList<Point> chan(ArrayList<Point> points) throws InterruptedException
    {
        //qSet = (a set of qi points)
        ArrayList<ArrayList<Point>> qSet = new ArrayList();
        //pSet = (a set of pi points)
        ArrayList<Point> pSet = new ArrayList();
        //Q = (a set of Qk, subsets of P of m elemets each)
        Stack<ArrayList<Point>> Q = new Stack<>();
        //S = (a set of convex hull of subset Qk)
        ArrayList<ArrayList<Point>> S = new ArrayList<>();
        //n = (the number of points in Set P)
        int n = points.size();
        //m = (a parameter for iteration of squaring scheme)
        int m;
        //P = (a Set of points)
        ArrayList<Point> P = new ArrayList<>();
        //add every point into ArrayList, P
        P.addAll(points);
        //C = (an empty list to store the points in the convex hull of P)
        ArrayList<Point> C = new ArrayList<>();
        //p1 = (the rightmost left point)
        Point p1 = findH(P);
        //p0 is used in the Jarvis march part of this Chan's algorithm
        //p0 is a point located on (-oo, 0) coordinate
        Point p0 = new Point((float)Double.NEGATIVE_INFINITY, 0);
        for (int t = 1; t <= logbi(logbi(n)); t++)
        {
            //add p0 and p1 into ArrayList, pSet
            pSet.add(p0);
            pSet.add(p1);
            //m = 2^(2^t)
            m = (int) pow(2, pow(2, t));
            //add p1 into set, C
            C.add(p1);
            //split set of points into K = n/m subsets of roughly m elements each
            double K = n / m;
            for (int k = 1; k <= K + 1; k++)
            {
                //if the last index is greater than the size of the List, P
                // -> if the last points are less than m points.
                if (k == (int) K + 1)
                {
                    //form a smaller subset Qk
                    Q.push(new ArrayList(P.subList((k - 1) * m, P.size())));
                    if (trace)
                    {
                        if ( color > ColorOfPoints.length - 1)
                            color = 0;
                        for (int i = 0; i < Q.peek().size(); i++)
                        {
                            sleep(WAIT);
                            drawPoint(Q.peek().get(i), ColorOfPoints[color]);
                        }
                        color++;
                    }
                }
                else
                {
                    //form a subset, Qk, which contains m elements
                    Q.push(new ArrayList(P.subList((k - 1) * m, (k - 1) * m + m)));
                    if (trace)
                    {
                        if ( color > ColorOfPoints.length - 1)
                            color= 0;
                        for (int i = 0; i < Q.peek().size(); i++)
                        {
                            sleep(WAIT);
                            drawPoint(Q.peek().get(i), ColorOfPoints[color]);
                        }
                        color++;
                    }
                }
            }
            if (trace)
                color = 0;
            //push every convex hull of subsets into the stack, S
            for (int k = 1; k <= K + 1; k++)
            {
                S.add(graham_scan(Q.pop()));
                color++;
            }
            //use the Jarvis march algorithm to compute the convex hull of P
            for (int i = 1; i <= m; i++)
            {
                //Once a new i value is updated, increase the size of qSet
                qSet.add(new ArrayList<>());
                //Finds the point d in Ck such that the angle p&(i-1)p&(i)d is maximized.
                for (int k = 1; k <= K + 1; k++)
                {
                    qSet.get(i - 1).add(jarvis_binary_search(pSet.get(i - 1), pSet.get(i), S.get(k - 1)));
                }
                //Chooses the point, z in qSet, which maximizes the angle p&(i-1)p&(i)z
                pSet.add(jarvis_next_ch_point(pSet.get(i - 1), pSet.get(i), qSet.get(i - 1)));
                /*Jarvis march terminates when the next selected point on the 
                  convex hull, p(i+1), is the initial point, p1 */
                if (pSet.get(i + 1) == pSet.get(1))
                    //Return the convex hull of P which contains i = h points
                    return C;
                else
                {
                    C.add(pSet.get(i + 1));
                    if (trace)
                        for (int v = 0; v < C.size(); v++)
                        {
                            if (v > convexHull.size() - 1)
                            {
                                drawPoint(convexHull.get(v), ORANGE);
                                drawLine(convexHull.get(v), convexHull.get(v + 1), MAGENTA);
                            }
                            else
                            {
                                drawPoint(convexHull.get(v), ORANGE);
                                drawLine(convexHull.get(v), convexHull.get(0), MAGENTA);
                            }
                        }
                }
                /*if after m iterations a point p(i+1) has not been found so that
                 * p(i+1) == p1, then m < h,
                 * so we need to start over with a higher value for m */
            }
            /*if Convex hull is not found even after the whole loop is executed,
             * it resets every set used in the loop of t */
            qSet.clear();pSet.clear();Q.clear();S.clear();C.clear();
        }
        return null;
    }
    /**
     * Returns ArrayList of a convex hull among input points by Graham's 
     * algorithm.<br>
     * This algorithm has O(n) of efficiency.
     * @param points An ArrayList which contains points for getting a convex hull
     * @return ArrayList that contains the points on the convex hull
     */
    int color = 0;
    private ArrayList<Point> graham_scan(ArrayList<Point> points) throws InterruptedException
    {
        if (points.size() < 3)
        {
            if (trace)
            {
                sleep(WAIT);
                if (points.size() > 1)
                    drawLine(points.get(0), points.get(1), ColorOfPoints[color]);
            }
            return points;
        }
        //create an stack which is going to contain points on a convex hull.
        Stack<Point> convexHull = new Stack<>();
        //copy the input points into another array
        ArrayList<Point> copyPoints = new ArrayList<>();
        copyPoints.addAll(points);
        //Step1: find the rightmost lowest point
        Point h = findH(copyPoints);
        //sort ArrayList<Point> copyPoints
        sortPoints(copyPoints, h);
        //Push p0, p1, and p2 into stack convexHull
        convexHull.push(copyPoints.get(0));
        if (trace)
        {
            sleep(WAIT);
            drawPoint(copyPoints.get(0), ColorOfPoints[color]);
        }
        convexHull.push(copyPoints.get(1));
        if (trace)
        {
            sleep(WAIT);
            drawPoint(copyPoints.get(1), ColorOfPoints[color]);
            drawLine(copyPoints.get(0), copyPoints.get(1), ColorOfPoints[color]);
        }
        convexHull.push(copyPoints.get(2));
        if (trace)
        {
            sleep(WAIT);
            drawPoint(copyPoints.get(2), ColorOfPoints[color]);
            drawLine(copyPoints.get(1), copyPoints.get(2), RED);
        }
        //get points in a convex hull
        int i = 3;
        while (i < copyPoints.size())
        {
            Point t1 = convexHull.peek_first();
            Point t2 = convexHull.peek_second();
            if (trace)
            {
                sleep(WAIT);
                drawPoint(copyPoints.get(i), ColorOfPoints[color]);
                drawLine(t1, copyPoints.get(i), LIGHT_GRAY);
            }
            //if pi is on the left side of the direct line from t2 to t1
            if (direction(t2, t1, copyPoints.get(i)) < 0)
            {
                if (trace)
                {
                    sleep(WAIT);
                    drawLine(t1, t2, ColorOfPoints[color]);
                }
                if (trace)
                {
                    sleep(WAIT);
                    drawPoint(copyPoints.get(i), ColorOfPoints[color]);
                    drawLine(t1, copyPoints.get(i), ColorOfPoints[color]);
                }
                //push pi to convexHull
                convexHull.push(copyPoints.get(i));
                //consider the next point in copyPoints
                i++;
            }
            else
            {
                if (trace)
                {
                    sleep(WAIT);

                    drawLine(t1, copyPoints.get(i), DEFAULT_COLOR);
                    drawLine(t2, t1, DEFAULT_COLOR);
                    drawPoint(t1, CYAN);

                }
                //pop the top element off stack convexHull
                convexHull.pop();
            }
        }
        if (trace)
        {
            sleep(WAIT);
            for (int count = 0; count < convexHull.toArrayList().size(); count++)
            {
                if (count != convexHull.toArrayList().size() - 1)
                {
                    drawPoint(convexHull.toArrayList().get(count), ORANGE);
                    drawLine(convexHull.toArrayList().get(count), convexHull.toArrayList().get(count + 1), MAGENTA);
                }
                else
                {
                    drawPoint(convexHull.toArrayList().get(count), ORANGE);
                    drawLine(convexHull.toArrayList().get(count), convexHull.toArrayList().get(0), MAGENTA);
                }
            }
        }
        return convexHull.toArrayList();
    }
    /**
     * Finds the point d in Ck such that the angle p&(i-1)p&(i)d is maximized.
     * @param p0 p sub (i-1)
     * @param p1 p sub i
     * @param Sk a set of convex hull of subset Qk
     * @return Point, d, which maximizes the angle between p1p0 and p1d
     */
    private Point jarvis_binary_search(Point p0, Point p1, ArrayList<Point> Sk) throws InterruptedException
    {
        //set d to be the first point in a set, Sk
        Point d = Sk.get(0);
        //create a test point.
        Point testP;
        //finds the point such that the angle p(i-1)p(i)d is maximized
        for (int i = 1; i < Sk.size(); i++)
        {
            if (trace)
            {
                drawPoint(d, WHITE);
            }
            testP = Sk.get(i);
            if (trace)
            {
                sleep(WAIT);
                drawPoint(testP, LIGHT_GRAY);
                drawLine(d, testP, LIGHT_GRAY);
            }
            //if the test point is located on the right side than t1.
            if (direction(p1, p0, testP) > 0)
            {
                if (trace)
                {
                    sleep(WAIT);
                    drawPoint(d, ColorOfPoints[color]);
                    if (i > 1)
                        drawLine(d, Sk.get(i), DEFAULT_COLOR);
                    drawLine(d, testP, WHITE);
                }
                // *set testP to be t1*
                d = testP;
            }
            //if the test point is located on the same line on which t0 and t1 is
            else if (direction(p1, p0, testP) == 0)
            {
                //and if the test point is further to the t0 than t1 is.
                /*(This if statement also prevents t1 from being the same point)
                    case 1: i = copyPoints.get(0) and t1 = copyPoints.get(0)
                    case 2: t0 = t1 = testP = h                           */
                if (distance(p1, p0) < distance(p1, testP))
                {
                    if (trace)
                    {
                        sleep(WAIT);
                        drawPoint(d, ColorOfPoints[color]);
                        if (i > 1)
                            drawLine(d, Sk.get(i), DEFAULT_COLOR);
                        drawLine(d, testP, WHITE);
                    }
                    // *set testP to be t1*
                    d = testP;
                }
                else
                {
                    if (trace)
                    {
                        sleep(WAIT);
                        drawPoint(testP, ColorOfPoints[color]);
                        drawLine(d, testP, DEFAULT_COLOR);
                    }
                }
            }
            else
            {
                if (trace)
                {
                    sleep(WAIT);
                    drawPoint(testP, ColorOfPoints[color]);
                    drawLine(d, testP, DEFAULT_COLOR);
                }
            }
        }
        //end loop
        return d;
    }
    /**
     * Chooses the point, z in qSet, which maximizes the angle p&(i-1)p&(i)z
     * to be the next point on the convex hull of P.
     * @param p0 p sub (i-1)
     * @param p1 p sub i
     * @param qSet a set of qi points
     */
    private Point jarvis_next_ch_point(Point p0, Point p1, ArrayList<Point> qSet) throws InterruptedException
    {
        //set z to be the first point in a set, Sk
        Point z = qSet.get(0);
        //create a test point.
        Point testP;
        //finds the point such that the angle p(i-1)p(i)d is maximized
        for (int i = 1; i < qSet.size(); i++)
        {
            if (trace)
            {
                sleep(WAIT);
                drawPoint(z, ORANGE);
            }
            testP = qSet.get(i);
            if (trace)
            {
                sleep(WAIT);
                drawPoint(testP, YELLOW);
                drawLine(z, testP, RED);
            }
            //if the test point is located on the right side than t1.
            if (direction(p1, p0, testP) > 0)
            {
                if (trace)
                {
                    sleep(WAIT);
                    drawPoint(testP, ORANGE);
                    drawLine(z, testP, MAGENTA);
                }
                // *set testP to be t1*
                z = testP;
            }
            //if the test point is located on the same line on which t0 and t1 is
            else if (direction(p1, p0, testP) == 0)
            {
                //and if the test point is further to the t0 than t1 is.
                /*(This if statement also prevents t1 from being the same point)
                    case 1: i = qSet.get(0) and t1 = qSet.get(0)
                    case 2: t0 = t1 = testP = h                           */
                if (distance(p1, p0) < distance(p1, testP))
                {
                    if (trace)
                    {
                        sleep(WAIT);
                        drawPoint(testP, ORANGE);
                        drawLine(z, testP, MAGENTA);
                    }
                    // *set testP to be t1*
                    z = testP;
                }
                else
                {
                    if (trace)
                    {
                        sleep(WAIT);
                        drawPoint(testP, ColorOfPoints[color]);
                        drawLine(z, testP, DEFAULT_COLOR);
                    }
                }
            }
            else
            {
                if (trace)
                {
                    sleep(WAIT);
                    drawPoint(testP, ColorOfPoints[color]);
                    drawLine(z, testP, DEFAULT_COLOR);
                }
            }
        }
        //end loop
        return z;
    }
    public static <E extends Comparable<E>> void heapSort(ArrayList<E> points, E h)
    {
        //Create a heap of whatever type E
        Heap<E> myHeap = new Heap<>();
        
        //Add elements to the heap
        for (int i = 0; i < points.size(); i++)
            myHeap.add(points.get(i));
        
        //Remove elements from the heap
        for (int i = points.size() - 1; i >= 0; i--)
            points.set(i, myHeap.remove());
    }
    
    /**
     * Sorts ArrayList of Point in an ascending order with respect to the angle 
     * between x-axis <br> and a line passing a point 
     * and the rightmost lowest point.
     * @param points ArrayList to be sorted
     * @param h the rightmost lowest point
     */
    private void sortPoints(ArrayList<Point> points, Point h) throws InterruptedException
    {
        int i, j;
        for(i = 0; i < points.size(); i++)
        {
            Point a = points.get(i);
            j = i - 1;
//            if (angle(points.get(j), h) == angle(a, h))
//                points.remove( (distance(points.get(j), h) > distance(a, h)) ?
//                        a : points.get(j) );
            while ( j >= 0 &&
                  (angle(points.get(j), h) > angle(a, h)) )
            {
                points.set(j + 1, points.get(j));
                j--;
            }
            
            points.set(j + 1, a);
        }
        if (trace)
        {
            int count = 0;
            for (Point point : points)
            {
                sleep(WAIT);
                if (count >= ColorOfPoints.length)
                    count = 0;
                drawPoint(point, ColorOfPoints[count++]);
                drawLine(h, point, YELLOW);
            }
            sleep(WAIT);
            for (Point point : points)
            {
                drawPoint(point, CYAN);
                drawLine(h, point, DEFAULT_COLOR);
            }
            drawPoint(h, PINK);
        }
    }
    /**
     * Returns the degree of an angle between x-axis and a line which passes 
     * through Point a and Point h.
     * @param a point to test
     * @param h the rightmost lowest point
     * @return double--the angle between y = h.getY() and a line passing Point a
     * and Point h
     */
    private double angle(Point a, Point h)
    {
        //in case, the slope of a line is not defined
        if (a.getY() != h.getY() && a.getX() == h.getX())
        {
            return Math.PI / 2.0;
        }
        else if (a.getY() == h.getY() && a.getX() == h.getX())
            return 0;
        else
        {
            //get the slope of the line from h to a
            double slope = (a.getY() - h.getY()) / (a.getX() - h.getX());
            //if the slope is greater than 0
            if (slope > 0)
                return Math.atan(slope);
            /* Since h is the rightmost lowest point, once the slope is 0,
             * the point a is always on the left side of h.
             * Thus, the degree between x-axis and the line h to a is always PI.
             * Also, the less the negetive slope is, the less the value of
             * arctan(slope) we get, so we add this up with PI value.
             */
            else
                return Math.PI + Math.atan(slope);
        }
    }
    /**
     * Returns a point which is the rightmost lowest point.
     * @param points a set of points
     * @return Point object that is the rightmost lowest point.
     */
    private Point findH(ArrayList<Point> points) throws InterruptedException
    {
        Point h = points.get(0);
        if (trace)
            drawPoint(h, PINK);
        for (Point point : points) {
            
            if (trace)
            {
                sleep(WAIT);
                drawPoint(point, BLUE);
            }
            if ( point.getY() < h.getY())
            {
                if (trace)
                    drawPoint(h, CYAN);
                h = point;
                if (trace)
                    drawPoint(h, PINK);
            }
            else if ( point.getY() == h.getY())
            {
                if (point.getX() > h.getX())
                {
                    if (trace)
                        drawPoint(h, CYAN);
                    h = point;
                    if (trace)
                        drawPoint(h, PINK);
                }
                else
                {
                    if (trace)
                    {
                        sleep(WAIT);
                        drawPoint(point, CYAN);
                    }
                }
            }
            else
            {
                if (trace)
                {
                    sleep(WAIT);
                    drawPoint(point, CYAN);
                }
            }
        }
        sleep(WAIT);
        if (trace)
        {
            drawPoint(h, PINK);
        }
        return h;
    }
    /**
     * Returns a numeric value of how much a point is winding from a line starting<br>
     * at a and ending at b.
     * @param a Point which is the starting point on the line
     * @param b Point which is the ending point on the line
     * @param p Point to test
     * @return double that represents the degree between the line <br>
     * from a to b and the line from a to p.
     */
    private double direction(Point a, Point b, Point p)
    {
        return (p.getX() - a.getX()) * (b.getY() - a.getY())
                - (p.getY() - a.getY()) * (b.getX() - a.getX());
    }
    /**
     * Returns a distance between two points.
     * @param p1 Point1 on (x, y) coordinate
     * @param p2 Point2 on (x, y) coordinate
     * @return double that represents the value of a distance.
     */
    private double distance(Point p1, Point p2)
    {
        double a = Math.pow((p2.getX() - p1.getX()), 2) 
                   + Math.pow((p2.getY() - p1.getY()), 2);
        return Math.sqrt(a);
    }
    /**
     * Returns the binary logarithm (base <i>2</i>) of a {@code double}
     * value.  Special cases:
     * <ul><li>If the argument is NaN or less than zero, then the result
     * is NaN.
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     * <li>If the argument is positive zero or negative zero, then the
     * result is negative infinity.</ul>
     *
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     *
     * @param n a value
     * @return  the value log&nbsp;{@code n}, the binary logarithm of
     *          {@code n}.
     */
    private double logbi(double n)
    {
        if (n < 0 || Double.isNaN(n))
            return Double.NaN;
        if (n >= Double.POSITIVE_INFINITY)
            return Double.POSITIVE_INFINITY;
        if (n == 0.0)
            return Double.NEGATIVE_INFINITY;
        return log(n) / log(2);
    }

}
