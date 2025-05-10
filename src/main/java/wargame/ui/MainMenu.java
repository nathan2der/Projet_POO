package wargame.ui;

import wargame.game.Game;
import wargame.map.GameMap;
import wargame.player.Player;
import wargame.player.AIPlayer;
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
    private static boolean darkMode = false;

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
        applyTheme();
    }

    private void createMenuPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        mainPanel.setBackground(darkMode ? new Color(43, 43, 43) : Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("WARGAME");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(darkMode ? Color.WHITE : Color.BLACK);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(50));

        // Buttons
        JButton newGameButton = createMenuButton("New Game");
        JButton loadGameButton = createMenuButton("Load Game");
        JButton rulesButton = createMenuButton("Game Rules");
        JButton settingsButton = createMenuButton("Settings");
        JButton exitButton = createMenuButton("Exit");

        // Add action listeners
        newGameButton.addActionListener(e -> startNewGame());
        loadGameButton.addActionListener(e -> loadGame());
        rulesButton.addActionListener(e -> showRules());
        settingsButton.addActionListener(e -> showSettings());
        exitButton.addActionListener(e -> System.exit(0));

        // Add buttons to panel
        mainPanel.add(newGameButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(loadGameButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(rulesButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(settingsButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(exitButton);

        add(mainPanel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 24));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 50));
        
        // Apply dark mode styling
        if (darkMode) {
            button.setBackground(new Color(60, 60, 60));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        }
        
        return button;
    }

    private void showSettings() {
        JDialog settingsDialog = new JDialog(this, "Settings", true);
        settingsDialog.setLayout(new BorderLayout());
        settingsDialog.setSize(400, 300);
        settingsDialog.setLocationRelativeTo(this);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        settingsPanel.setBackground(darkMode ? new Color(43, 43, 43) : Color.WHITE);

        // Dark Mode Toggle
        JCheckBox darkModeCheckBox = new JCheckBox("Dark Mode");
        darkModeCheckBox.setSelected(darkMode);
        darkModeCheckBox.setForeground(darkMode ? Color.WHITE : Color.BLACK);
        darkModeCheckBox.addActionListener(e -> {
            darkMode = darkModeCheckBox.isSelected();
            applyTheme();
            settingsDialog.dispose();
            dispose();
            new MainMenu().setVisible(true);
        });

        settingsPanel.add(darkModeCheckBox);
        settingsPanel.add(Box.createVerticalStrut(20));

        // Add OK button
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> settingsDialog.dispose());
        if (darkMode) {
            okButton.setBackground(new Color(60, 60, 60));
            okButton.setForeground(Color.WHITE);
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(darkMode ? new Color(43, 43, 43) : Color.WHITE);
        buttonPanel.add(okButton);

        settingsDialog.add(settingsPanel, BorderLayout.CENTER);
        settingsDialog.add(buttonPanel, BorderLayout.SOUTH);
        settingsDialog.setVisible(true);
    }

    private void applyTheme() {
        if (darkMode) {
            getContentPane().setBackground(new Color(43, 43, 43));
            setBackground(new Color(43, 43, 43));
        } else {
            getContentPane().setBackground(Color.WHITE);
            setBackground(Color.WHITE);
        }
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    private void startNewGame() {
        // Create game mode selection dialog
        String[] options = {"Play against another player", "Play against CPU"};
        int gameMode = JOptionPane.showOptionDialog(
            this,
            "Select game mode:",
            "Game Mode",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (gameMode == JOptionPane.CLOSED_OPTION) {
            return;
        }

        // Get player names
        String player1Name = JOptionPane.showInputDialog(
            this,
            "Enter Player 1 name:",
            "Player 1",
            JOptionPane.QUESTION_MESSAGE
        );

        if (player1Name == null || player1Name.trim().isEmpty()) {
            player1Name = "Player 1";
        }

        String player2Name;
        if (gameMode == 0) { // Local multiplayer
            player2Name = JOptionPane.showInputDialog(
                this,
                "Enter Player 2 name:",
                "Player 2",
                JOptionPane.QUESTION_MESSAGE
            );
            if (player2Name == null || player2Name.trim().isEmpty()) {
                player2Name = "Player 2";
            }
        } else { // CPU opponent
            player2Name = "CPU";
        }

        // Create new game map
        GameMap map = new GameMap(20, 15);
        
        // Create players
        Player player1 = new Player(player1Name);
        Player player2 = gameMode == 0 ? new Player(player2Name) : new AIPlayer(player2Name);
        
        /* Previous version with fixed units
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
        */
        
        // Generate random armies for both players
        generateRandomArmy(player1, map, true);  // true for left side
        generateRandomArmy(player2, map, false); // false for right side
        
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
        System.out.println("  " + player1Name + ": " + player1.getUnits().size() + " units");
        System.out.println("  " + player2Name + ": " + player2.getUnits().size() + " units");
        
        // Create and show game window
        GameWindow gameWindow = new GameWindow(game);
        gameWindow.setVisible(true);
        
        // Close main menu
        dispose();
    }

    private void generateRandomArmy(Player player, GameMap map, boolean isLeftSide) {
        final int TOTAL_SPACE = 100;
        int remainingSpace = TOTAL_SPACE;
        int startX = isLeftSide ? 0 : 17;
        int currentX = startX;
        int currentY = 6;
        
        while (remainingSpace > 0) {
            // Create a list of possible unit types that fit in remaining space
            List<UnitType> possibleTypes = new ArrayList<>();
            if (remainingSpace >= 7) possibleTypes.add(UnitType.HEAVY_INFANTRY);
            if (remainingSpace >= 5) possibleTypes.add(UnitType.CAVALRY);
            if (remainingSpace >= 2) {
                possibleTypes.add(UnitType.INFANTRY);
                possibleTypes.add(UnitType.MAGE);
            }
            if (remainingSpace >= 1) possibleTypes.add(UnitType.ARCHER);
            
            // If no units can fit, use the smallest unit (Archer)
            if (possibleTypes.isEmpty()) {
                possibleTypes.add(UnitType.ARCHER);
            }
            
            // Randomly select a unit type
            UnitType selectedType = possibleTypes.get((int)(Math.random() * possibleTypes.size()));
            
            // Create and place the unit
            Unit unit = new Unit(selectedType, player);
            map.placeUnit(unit, currentX, currentY);
            
            // Update remaining space
            remainingSpace -= selectedType.getSpaceCost();
            
            // Update position for next unit
            currentX++;
            if (currentX > (isLeftSide ? 2 : 19)) {
                currentX = startX;
                currentY++;
            }
        }
        
        // Debug output
        System.out.println(player.getName() + "'s army composition:");
        for (UnitType type : UnitType.values()) {
            long count = player.getUnits().stream()
                .filter(u -> u.getType() == type)
                .count();
            System.out.println("  " + type + ": " + count + " units");
        }
        System.out.println("Total space used: " + (TOTAL_SPACE - remainingSpace));
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