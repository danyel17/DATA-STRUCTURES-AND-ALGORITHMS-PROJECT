import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class ExitPointGame extends JFrame {
    private static final Random random = new Random();
    
    
    private int currentRoom = 0;
    private int moves = 0;
    private boolean hasKey = false;
    private boolean hasFlashlight = false;
    private boolean gameOver = false;
    
    
    private Timer gameTimer;
    private int secondsRemaining = 40;
    private JLabel timerLabel;
    
    
    private final String[] rooms = {
        "Entrance Pipe", "Main Junction", "East Tunnel", "Filtration Chamber", 
        "Flooded Section", "Maintenance Shaft", "Discharge Point", "Exit Duct"
    };
    
    private final String[] descriptions = {
        "A narrow concrete pipe with slimy walls. Water trickles past your feet.",
        "A four-way junction where several large pipes converge. Echoes bounce off the walls.",
        "A horizontal pipe with rusted metal rungs embedded in the side. The air is damp.",
        "A wide chamber with mesh screens and broken filtering equipment.",
        "A partially submerged section with murky water reaching your knees.",
        "A vertical shaft with a metal ladder leading upward. Old tools lie scattered.",
        "A noisy chamber with massive water pumps. One is malfunctioning, creating a rhythmic banging.",
        "A narrow air duct that slopes upward toward that looks like daylight, I think this is the way to exit. yay Im finally free."
    };

    // Room connections - defines which rooms are connected to the current one
    private final int[][] roomConnections = {
        {1}, // From Entrance Pipe -> Main Junction
        {0, 2, 3, 4}, // From Main Junction -> Entrance, East Tunnel, Filtration, Flooded
        {1}, // From East Tunnel -> Main Junction
        {1, 4}, // From Filtration Chamber -> Main Junction, Flooded Section
        {1, 3, 5}, // From Flooded Section -> Main Junction, Filtration, Maintenance Shaft
        {4, 6}, // From Maintenance Shaft -> Flooded Section, Discharge Point
        {5, 7}, // From Pump Station -> Maintenance Shaft, Exit Duct
        {6} // From Exit Duct -> Discharge Point
    };

    // Room layouts (0=wall, 1=path, 2=water, 3=ladder, 4=key, 5=flashlight, 6=exit)
    private final int[][][] roomLayouts = {
        {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        },
        {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 5, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        },
        {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        },
        {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 1, 2, 2, 2, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 2, 4, 2, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0}
        },
        {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 2, 2, 3, 2, 2, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        },
        {
            {0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        },
        {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        },
        {
            {0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        }
    };

    
    private int playerX = 7;
    private int playerY = 6;
    private boolean playerFacingRight = true;
    
   
    private JTextArea gameLog;
    private JButton[] choiceButtons;
    private DrainagePanel drainagePanel;
    private JLabel inventoryLabel;
    private JLabel locationLabel;
    private JButton retryButton; 

    
    private Image[] playerSprites;
    private Image wallTile, pathTile, waterTile, ladderTile, keyTile, flashlightTile, exitTile;
    
    public ExitPointGame() {
        setTitle("Exit Point Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        loadImages();
        setupUI();
        
        
        appendToLog("ESCAPE THE DRAINAGE");
        appendToLog("=========================");
        appendToLog("You wake up in a dark drainage system with no memory of how you got here.");
        appendToLog("The pipes echo with strange sounds and your only goal is to find a way out...");
        appendToLog("WARNING: You have only 40 seconds to escape before mommy long legs find you!");
        appendToLog("");
        
        updateRoomInfo();
        startTimer();
    }
    
    private void loadImages() {
        
        playerSprites = new Image[2];
        playerSprites[0] = createPlayerImage(true);  // Facing right
        playerSprites[1] = createPlayerImage(false); // Facing left
        
        wallTile = createTileImage(new Color(80, 80, 80));
        pathTile = createTileImage(new Color(40, 40, 40));
        waterTile = createTileImage(new Color(20, 40, 120));
        ladderTile = createLadderImage();
        keyTile = createKeyImage();
        flashlightTile = createFlashlightImage();
        exitTile = createExitImage();
    }
    
    private Image createPlayerImage(boolean facingRight) {
        BufferedImage img = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        
        g.setColor(new Color(200, 200, 200));
        g.fillRect(10, 10, 10, 15);
        
        
        g.setColor(new Color(255, 220, 175));
        g.fillOval(8, 2, 14, 14);
        
        
        g.setColor(Color.BLACK);
        if (facingRight) {
            g.fillRect(18, 6, 2, 2);
        } else {
            g.fillRect(10, 6, 2, 2);
        }
        
        
        g.setColor(new Color(150, 150, 150));
        
        if (facingRight) {
            g.drawLine(20, 12, 25, 10);
        } else {
            g.drawLine(10, 12, 5, 10);
        }
        
        g.drawLine(12, 25, 10, 30);
        g.drawLine(18, 25, 20, 30);
        
        g.dispose();
        return img;
    }

    private Image createTileImage(Color color) {
        BufferedImage img = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, 30, 30);
        g.setColor(new Color(0, 0, 0, 50));
        g.drawRect(0, 0, 29, 29);
        g.dispose();
        return img;
    }
    
    private Image createLadderImage() {
        BufferedImage img = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(40, 40, 40));
        g.fillRect(0, 0, 30, 30);
        g.setColor(new Color(150, 100, 50));
        g.fillRect(5, 0, 3, 30);
        g.fillRect(22, 0, 3, 30);
        for (int y = 5; y < 30; y += 7) {
            g.fillRect(5, y, 20, 2);
        }
        g.dispose();
        return img;
    }
    
    private Image createKeyImage() {
        BufferedImage img = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(40, 40, 40));
        g.fillRect(0, 0, 30, 30);
        g.setColor(new Color(200, 180, 0));
        g.fillOval(10, 7, 10, 10);
        g.fillRect(14, 15, 2, 10);
        g.fillRect(12, 20, 6, 2);
        g.fillRect(12, 23, 6, 2);
        g.dispose();
        return img;
    }
    
    private Image createFlashlightImage() {
        BufferedImage img = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(40, 40, 40));
        g.fillRect(0, 0, 30, 30);
        g.setColor(new Color(50, 50, 50));
        g.fillRect(8, 10, 14, 6);
        g.setColor(new Color(200, 200, 0));
        g.fillOval(18, 11, 4, 4);
        g.setColor(new Color(255, 255, 100, 100));
        g.fillPolygon(
            new int[] {22, 30, 28, 20}, 
            new int[] {11, 5, 17, 15}, 4
        );
        g.dispose();
        return img;
    }
    
    private Image createExitImage() {
        BufferedImage img = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, 30, 30);
        g.setColor(new Color(100, 150, 255));
        g.fillOval(5, 5, 20, 20);
        g.setColor(new Color(200, 200, 255));
        g.fillOval(10, 10, 5, 5);
        g.dispose();
        return img;
    }
    
    private void setupUI() {
        
        setLayout(new BorderLayout());
        
        
        gameLog = new JTextArea();
        gameLog.setEditable(false);
        gameLog.setLineWrap(true);
        gameLog.setWrapStyleWord(true);
        gameLog.setFont(new Font("Monospaced", Font.PLAIN, 14));
        gameLog.setBackground(new Color(20, 20, 20));
        gameLog.setForeground(new Color(0, 230, 0));
        gameLog.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(gameLog);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        add(scrollPane, BorderLayout.EAST);
        
        
        JPanel statusPanel = new JPanel(new GridLayout(1, 3));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.setBackground(new Color(40, 40, 40));
        
        locationLabel = new JLabel("Location: " + rooms[currentRoom]);
        locationLabel.setForeground(Color.WHITE);
        locationLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        inventoryLabel = new JLabel("Inventory: Empty");
        inventoryLabel.setForeground(Color.WHITE);
        
        timerLabel = new JLabel("Time: 40");
        timerLabel.setForeground(Color.RED);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        statusPanel.add(locationLabel);
        statusPanel.add(inventoryLabel);
        statusPanel.add(timerLabel);
        
        add(statusPanel, BorderLayout.NORTH);
        
        
        drainagePanel = new DrainagePanel();
        add(drainagePanel, BorderLayout.CENTER);
        
       
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // Changed to 4 rows to accommodate retry button
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(40, 40, 40));
        
        choiceButtons = new JButton[3];
        for (int i = 0; i < 3; i++) {
            choiceButtons[i] = new JButton("Choice " + (i+1));
            final int choice = i;
            choiceButtons[i].addActionListener(e -> processChoice(choice));
            choiceButtons[i].setFont(new Font("SansSerif", Font.BOLD, 14));
            choiceButtons[i].setBackground(new Color(80, 80, 80));
            choiceButtons[i].setForeground(Color.WHITE);
            choiceButtons[i].setFocusPainted(false);
            buttonPanel.add(choiceButtons[i]);
        }

          
          retryButton = new JButton("If you had fun please play the game again :>");
          retryButton.setFont(new Font("SansSerif", Font.BOLD, 14));
          retryButton.setBackground(new Color(80, 80, 80));
          retryButton.setForeground(Color.WHITE);
          retryButton.setFocusPainted(false);
          retryButton.addActionListener(e -> resetGame());
          retryButton.setVisible(false);
          buttonPanel.add(retryButton);
          
          add(buttonPanel, BorderLayout.SOUTH);
          
          
          updatePlayerPosition();
          updateChoices();
      }
  
      private void resetGame() {
          currentRoom = 0;
          moves = 0;
          hasKey = false;
          hasFlashlight = false;
          gameOver = false;
          secondsRemaining = 40; // Reset timer
          timerLabel.setText("Time: 40");
          timerLabel.setForeground(Color.RED);

        // If you Reload the game :)
        gameLog.setText("");
        appendToLog("ESCAPE THE DRAINAGE");
        appendToLog("=========================");
        appendToLog("You wake up in a dark drainage system with no memory of how you got here.");
        appendToLog("The pipes echo with strange sounds and your only goal is to find a way out...");
        appendToLog("WARNING: You have only 40 seconds to escape before mommy long legs find you!");
        appendToLog("");

        
        updateRoomInfo();
        startTimer();
        retryButton.setVisible(false);
        updateChoices(); 
        drainagePanel.repaint(); 
    }

    private void startTimer() {
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (gameOver) {
                    this.cancel();
                    return;
                }
                
                secondsRemaining--;
                SwingUtilities.invokeLater(() -> {
                    int minutes = secondsRemaining / 60;
                    int seconds = secondsRemaining % 60;
                    timerLabel.setText(String.format("Time: %d:%02d", minutes, seconds));
                    
                    
                    if (secondsRemaining <= 0) {
                        gameOver = true;
                        gameOver(false);
                    }
                    
                    
                    if (secondsRemaining <= 30) {
                        timerLabel.setForeground(new Color(255, 0, 0));
                    }
                });
            }
        }, 1000, 1000);
    }
    
    private void updatePlayerPosition() {
        
        switch (currentRoom) {
            case 0: // Entrance Pipe
                playerX = 7; playerY = 6;
                playerFacingRight = true;
                break;
            case 1: // Main Junction
                playerX = 6; playerY = 5;
                playerFacingRight = true;
                break;
            case 2: // East Tunnel
                playerX = 7; playerY = 5;
                playerFacingRight = true;
                break;
            case 3: // Filtration Chamber
                playerX = 6; playerY = 5;
                playerFacingRight = false;
                break;
            case 4: // Flooded Section
                playerX = 6; playerY = 5;
                playerFacingRight = true;
                break;
            case 5: // Maintenance Shaft
                playerX = 6; playerY = 3;
                playerFacingRight = true;
                break;
            case 6: // Discharge Point
                playerX = 6; playerY = 4;
                playerFacingRight = false;
                break;
            case 7: // Exit Duct
                playerX = 6; playerY = 4;
                playerFacingRight = true;
                break;
        }
    }
    
   
    private class DrainagePanel extends JPanel {
        private static final int TILE_SIZE = 30;
        
        public DrainagePanel() {
            setBackground(Color.BLACK);
            setPreferredSize(new Dimension(450, 300));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawDrainageSystem(g);
        }
        
        private void drawDrainageSystem(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int mapWidth = roomLayouts[currentRoom][0].length * TILE_SIZE;
            int mapHeight = roomLayouts[currentRoom].length * TILE_SIZE;
            
            int offsetX = (panelWidth - mapWidth) / 2;
            int offsetY = (panelHeight - mapHeight) / 2;
            
            
            int[][] currentLayout = roomLayouts[currentRoom];
            for (int y = 0; y < currentLayout.length; y++) {
                for (int x = 0; x < currentLayout[y].length; x++) {
                    int tileX = offsetX + x * TILE_SIZE;
                    int tileY = offsetY + y * TILE_SIZE;
                    
                    
                    switch (currentLayout[y][x]) {
                        case 0: // Wall
                            g2d.drawImage(wallTile, tileX, tileY, null);
                            break;
                        case 1: // Path
                            g2d.drawImage(pathTile, tileX, tileY, null);
                            break;
                        case 2: // Water
                            g2d.drawImage(waterTile, tileX, tileY, null);
                            break;
                        case 3: // Ladder
                            g2d.drawImage(ladderTile, tileX, tileY, null);
                            break;
                        case 4: // Key
                            if (!hasKey) {
                                g2d.drawImage(pathTile, tileX, tileY, null);
                                g2d.drawImage(keyTile, tileX, tileY, null);
                            } else {
                                g2d.drawImage(pathTile, tileX, tileY, null);
                            }
                            break;
                        case 5: // Flashlight
                            if (!hasFlashlight) {
                                g2d.drawImage(pathTile, tileX, tileY, null);
                                g2d.drawImage(flashlightTile, tileX, tileY, null);
                            } else {
                                g2d.drawImage(pathTile, tileX, tileY, null);
                            }
                            break;
                        case 6: // Exit
                            if (gameOver) {
                                g2d.drawImage(pathTile, tileX, tileY, null);
                                g2d.drawImage(exitTile, tileX, tileY, null);
                            } else {
                                g2d.drawImage(pathTile, tileX, tileY, null);
                            }
                            break;
                    }
                }
            }
            
            g2d.drawImage(playerSprites[playerFacingRight ? 0 : 1], offsetX + playerX * TILE_SIZE, offsetY + playerY * TILE_SIZE, null);
        }
    }
    
    private void processChoice(int choice) {
        if (gameOver) return;

        
        int[] connections = roomConnections[currentRoom];
        if (choice < connections.length) {
            currentRoom = connections[choice];
            moves++; 

            
            updatePlayerPosition();
            updateChoices();

           
            checkForItems();

            
            updateRoomInfo();
            drainagePanel.repaint(); 
        }
    }

    private void checkForItems() {
        if (currentRoom == 4 && !hasKey) { 
            hasKey = true;
            appendToLog("You found a key!");
            updateInventory();
        } else if (currentRoom == 5 && !hasFlashlight) { 
            hasFlashlight = true;
            appendToLog("You found a flashlight!");
            updateInventory();
        } else if (currentRoom == 7) { // Exit Duct
            if (hasKey) {
                gameOver = true;
                gameOver(true);
            } else {
                appendToLog("The exit is locked! You need a key to escape.");
            }
        }
    }

    private void updateChoices() {
        int[] connections = roomConnections[currentRoom];
        for (int i = 0; i < choiceButtons.length; i++) {
            if (i < connections.length) {
                choiceButtons[i].setText("Go to " + rooms[connections[i]]);
            } else {
                choiceButtons[i].setText("No Exit Here");
            }
        }
    }

    private void updateRoomInfo() {
        locationLabel.setText("Location: " + rooms[currentRoom]);
        updateInventory();
        appendToLog(descriptions[currentRoom]);
    }

    private void updateInventory() {
        StringBuilder inventory = new StringBuilder("Inventory: ");
        if (hasKey) inventory.append("Key ");
        if (hasFlashlight) inventory.append("Flashlight ");
        if (inventory.length() == "Inventory: ".length()) inventory.append("Empty");
        inventoryLabel.setText(inventory.toString());
    }

    private void gameOver(boolean success) {
        gameTimer.cancel();
        retryButton.setVisible(true); 
        if (success) {
            appendToLog("Congratulations! You've successfully escaped the drainage system!");
        } else {
            appendToLog("Time's up! You have been caught.");
        }

        for (JButton button : choiceButtons) {
            button.setEnabled(true); 
        }
    }

    private void appendToLog(String message) {
        gameLog.append(message + "\n");
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExitPointGame game = new ExitPointGame();
            game.setVisible(true);
        });
    }
}