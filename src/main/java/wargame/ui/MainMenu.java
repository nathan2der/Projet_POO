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
        GameMap map = new GameMap(20, 15);
        
        // Create players
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        
        // Create and place units for player 1
        Unit infantry1 = new Unit(UnitType.INFANTRY, player1);
        Unit archer1 = new Unit(UnitType.ARCHER, player1);
        Unit cavalry1 = new Unit(UnitType.CAVALRY, player1);
        
        // Place player 1 units on the left side with better spacing
        map.placeUnit(infantry1, 0, 7);
        map.placeUnit(archer1, 1, 6);
        map.placeUnit(cavalry1, 2, 7);
        
        // Create and place units for player 2
        Unit infantry2 = new Unit(UnitType.INFANTRY, player2);
        Unit heavyInfantry2 = new Unit(UnitType.HEAVY_INFANTRY, player2);
        Unit mage2 = new Unit(UnitType.MAGE, player2);
        
        // Place player 2 units on the right side with better spacing
        map.placeUnit(infantry2, 17, 7);
        map.placeUnit(heavyInfantry2, 18, 6);
        map.placeUnit(mage2, 19, 7);
        
        // Generate random terrain
        TerrainType[] terrainTypes = TerrainType.values();
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                // Skip tiles with units
                if (map.getTile(x, y).getUnit() != null) {
                    continue;
                }
                
                // Generate random terrain
                double random = Math.random();
                if (random < 0.3) {
                    map.getTile(x, y).setTerrainType(TerrainType.PLAINS);
                } else if (random < 0.5) {
                    map.getTile(x, y).setTerrainType(TerrainType.FOREST);
                } else if (random < 0.7) {
                    map.getTile(x, y).setTerrainType(TerrainType.MOUNTAIN);
                } else if (random < 0.85) {
                    map.getTile(x, y).setTerrainType(TerrainType.WATER);
                } else {
                    map.getTile(x, y).setTerrainType(TerrainType.VILLAGE);
                }
            }
        }
        
        // Create new game with players
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        Game game = new Game(map, players);
        
        // Debug output to verify unit counts
        System.out.println("Starting new game with:");
        System.out.println("  Player 1: " + player1.getUnits().size() + " units");
        System.out.println("  Player 2: " + player2.getUnits().size() + " units");
        
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