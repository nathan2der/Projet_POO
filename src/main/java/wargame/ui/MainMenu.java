package wargame.ui;

import wargame.game.Game;
import wargame.map.GameMap;
import wargame.player.Player;
import wargame.unit.Unit;
import wargame.unit.UnitType;
import wargame.terrain.TerrainType;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends JFrame {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    public MainMenu() {
        setupWindow();
        createMenuPanel();
    }

    private void setupWindow() {
        setTitle("Wargame - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void createMenuPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Title
        JLabel titleLabel = new JLabel("WARGAME");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(50));

        // Buttons
        JButton newGameButton = createMenuButton("New Game");
        JButton loadGameButton = createMenuButton("Load Game");
        JButton rulesButton = createMenuButton("Game Rules");
        JButton exitButton = createMenuButton("Exit");

        // Add action listeners
        newGameButton.addActionListener(e -> startNewGame());
        loadGameButton.addActionListener(e -> loadGame());
        rulesButton.addActionListener(e -> showRules());
        exitButton.addActionListener(e -> System.exit(0));

        // Add buttons to panel
        mainPanel.add(newGameButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(loadGameButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(rulesButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(exitButton);

        add(mainPanel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 24));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 50));
        return button;
    }

    private void startNewGame() {
        // Create new game map
        GameMap map = new GameMap(10, 10);
        
        // Create players
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        
        // Create and place units for player 1
        Unit infantry1 = new Unit(UnitType.INFANTRY, player1);
        Unit archer1 = new Unit(UnitType.ARCHER, player1);
        Unit cavalry1 = new Unit(UnitType.CAVALRY, player1);
        
        map.placeUnit(infantry1, 0, 0);
        map.placeUnit(archer1, 1, 0);
        map.placeUnit(cavalry1, 2, 0);
        
        // Create and place units for player 2
        Unit infantry2 = new Unit(UnitType.INFANTRY, player2);
        Unit heavyInfantry2 = new Unit(UnitType.HEAVY_INFANTRY, player2);
        Unit mage2 = new Unit(UnitType.MAGE, player2);
        
        map.placeUnit(infantry2, 7, 9);
        map.placeUnit(heavyInfantry2, 8, 9);
        map.placeUnit(mage2, 9, 9);
        
        // Add some terrain variety
        for (int x = 3; x < 7; x++) {
            for (int y = 3; y < 7; y++) {
                map.getTile(x, y).setTerrainType(TerrainType.FOREST);
            }
        }
        
        // Create new game with players
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        Game game = new Game(map, players);
        
        // Create and show game window
        GameWindow gameWindow = new GameWindow(game);
        gameWindow.setVisible(true);
        
        // Close main menu
        dispose();
    }

    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Game");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Wargame Save Files", "wgs"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getPath();
                
                // Load game state
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
                    Game loadedGame = (Game) ois.readObject();
                    
                    // Create and show game window
                    GameWindow gameWindow = new GameWindow(loadedGame);
                    gameWindow.setVisible(true);
                    
                    // Close main menu
                    dispose();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error loading game: " + e.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void showRules() {
        JOptionPane.showMessageDialog(
            this,
            "Game Rules:\n" +
            "1. Each player takes turns moving and attacking with their units\n" +
            "2. Units can move up to their movement range\n" +
            "3. Units can attack enemy units within their attack range\n" +
            "4. Terrain affects movement and combat\n" +
            "5. The last player with units remaining wins",
            "Game Rules",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
} 