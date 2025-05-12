package wargame.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import wargame.game.Game;
import wargame.map.GameMap;
import wargame.player.Player;
import wargame.terrain.TerrainType;
import wargame.unit.Unit;
import wargame.unit.UnitType;

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

        JLabel titleLabel = new JLabel("WARGAME");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(darkMode ? Color.WHITE : Color.BLACK);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(50));

        JButton newGameButton = createMenuButton("New Game");
        JButton loadGameButton = createMenuButton("Load Game");
        JButton rulesButton = createMenuButton("Game Rules");
        JButton settingsButton = createMenuButton("Settings");
        JButton exitButton = createMenuButton("Exit");

        newGameButton.addActionListener(e -> startNewGame());
        loadGameButton.addActionListener(e -> loadGame());
        rulesButton.addActionListener(e -> showRules());
        settingsButton.addActionListener(e -> showSettings());
        exitButton.addActionListener(e -> System.exit(0));

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

        if (darkMode) {
            button.setBackground(new Color(60, 60, 60));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        }

        return button;
    }

    private void startNewGame() {
        String[] playerOptions = {"2 Players", "3 Players"};
        int mode = JOptionPane.showOptionDialog(this, "How many players?", "Player Count",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, playerOptions, playerOptions[0]);

        if (mode == JOptionPane.CLOSED_OPTION) return;

        int playerCount = (mode == 0) ? 2 : 3;

        List<Player> players = new ArrayList<>();

        for (int i = 1; i <= playerCount; i++) {
            String name = JOptionPane.showInputDialog(this, "Enter Player " + i + " name:", "Player " + i,
                    JOptionPane.QUESTION_MESSAGE);
            if (name == null || name.trim().isEmpty()) name = "Player " + i;
            players.add(new Player(name));
        }

        GameMap map = new GameMap(20, 15);
        for (int i = 0; i < players.size(); i++) {
            generateRandomArmy(players.get(i), map, i);
        }

        TerrainType[] terrainTypes = TerrainType.values();
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getTile(x, y).getUnit() != null) continue;

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

        Game game = new Game(map, players);
        GameWindow gameWindow = new GameWindow(game);
        gameWindow.setVisible(true);
        dispose();
    }

    private void generateRandomArmy(Player player, GameMap map, int playerIndex) {
        final int TOTAL_SPACE = 100;
        int remainingSpace = TOTAL_SPACE;
        int startX = playerIndex == 0 ? 0 : (playerIndex == 1 ? 17 : 10);
        int currentX = startX;
        int currentY = 5 + playerIndex;

        while (remainingSpace > 0) {
            List<UnitType> possibleTypes = new ArrayList<>();
            if (remainingSpace >= 7) possibleTypes.add(UnitType.HEAVY_INFANTRY);
            if (remainingSpace >= 5) possibleTypes.add(UnitType.CAVALRY);
            if (remainingSpace >= 2) {
                possibleTypes.add(UnitType.INFANTRY);
                possibleTypes.add(UnitType.MAGE);
            }
            if (remainingSpace >= 1) possibleTypes.add(UnitType.ARCHER);

            if (possibleTypes.isEmpty()) possibleTypes.add(UnitType.ARCHER);

            UnitType selectedType = possibleTypes.get((int) (Math.random() * possibleTypes.size()));
            Unit unit = new Unit(selectedType, player);
            map.placeUnit(unit, currentX, currentY);

            remainingSpace -= selectedType.getSpaceCost();
            currentX++;
            if (currentX > startX + 2) {
                currentX = startX;
                currentY++;
            }
        }
    }

    private void loadGame() {
        // Code inchangé
    }

    private void showRules() {
        JDialog rulesDialog = new JDialog(this, "Game Rules", true);
        rulesDialog.setLayout(new BorderLayout());
        
        // Create a text area with the rules
        JTextArea rulesText = new JTextArea();
        rulesText.setEditable(false);
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        rulesText.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Set rules content
        String rules = """
            WARGAME RULES
            
            1. Game Overview:
            - Turn-based strategy game on a hexagonal grid
            - Each player controls an army of different unit types
            - Goal: Eliminate all enemy units
            
            2. Unit Types:
            - Infantry: Basic unit, balanced stats
            - Heavy Infantry: Strong defense, slow movement
            - Cavalry: Fast movement, good attack
            - Archer: Ranged attack, weak defense
            - Mage: Powerful attack, very weak defense
            
            3. Movement:
            - Each unit has movement points
            - Different terrain types have different movement costs
            - Units can't move through occupied tiles
            
            4. Combat:
            - Units can attack adjacent enemies
            - Archers and Mages can attack from range
            - Combat is resolved automatically based on unit stats
            
            5. Terrain Types:
            - Plains: Normal movement cost
            - Forest: Increased movement cost
            - Mountain: High movement cost
            - Water: Impassable
            - Village: Provides healing
            
            6. Game Flow:
            - Players take turns moving and attacking
            - Units regain movement points at the start of their turn
            - Game ends when one player loses all units
            """;
        
        rulesText.setText(rules);
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(rulesText);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> rulesDialog.dispose());
        
        // Apply theme
        if (darkMode) {
            rulesDialog.getContentPane().setBackground(new Color(43, 43, 43));
            rulesText.setBackground(new Color(60, 60, 60));
            rulesText.setForeground(Color.WHITE);
            closeButton.setBackground(new Color(60, 60, 60));
            closeButton.setForeground(Color.WHITE);
        }
        
        // Add components to dialog
        rulesDialog.add(scrollPane, BorderLayout.CENTER);
        rulesDialog.add(closeButton, BorderLayout.SOUTH);
        
        // Show dialog
        rulesDialog.pack();
        rulesDialog.setLocationRelativeTo(this);
        rulesDialog.setVisible(true);
    }

    private void showSettings() {
        JDialog settingsDialog = new JDialog(this, "Settings", true);
        settingsDialog.setLayout(new BorderLayout());
        
        // Create settings panel
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Dark mode toggle
        JCheckBox darkModeCheckBox = new JCheckBox("Dark Mode");
        darkModeCheckBox.setSelected(darkMode);
        darkModeCheckBox.addActionListener(e -> {
            darkMode = darkModeCheckBox.isSelected();
            applyTheme();
            settingsDialog.dispose();
        });
        
        // Apply theme to settings dialog
        if (darkMode) {
            settingsDialog.getContentPane().setBackground(new Color(43, 43, 43));
            settingsPanel.setBackground(new Color(43, 43, 43));
            darkModeCheckBox.setBackground(new Color(43, 43, 43));
            darkModeCheckBox.setForeground(Color.WHITE);
        }
        
        // Add components
        settingsPanel.add(darkModeCheckBox);
        settingsDialog.add(settingsPanel, BorderLayout.CENTER);
        
        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> settingsDialog.dispose());
        if (darkMode) {
            closeButton.setBackground(new Color(60, 60, 60));
            closeButton.setForeground(Color.WHITE);
        }
        settingsDialog.add(closeButton, BorderLayout.SOUTH);
        
        // Show dialog
        settingsDialog.pack();
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setVisible(true);
    }

    private void applyTheme() {
        // Apply dark mode to main window
        if (darkMode) {
            getContentPane().setBackground(new Color(43, 43, 43));
            setBackground(new Color(43, 43, 43));
        } else {
            getContentPane().setBackground(Color.WHITE);
            setBackground(Color.WHITE);
        }
        
        // Update all components
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setBackground(darkMode ? new Color(43, 43, 43) : Color.WHITE);
                
                // Update all components in the panel
                for (Component panelComp : panel.getComponents()) {
                    if (panelComp instanceof JLabel) {
                        JLabel label = (JLabel) panelComp;
                        label.setForeground(darkMode ? Color.WHITE : Color.BLACK);
                    } else if (panelComp instanceof JButton) {
                        JButton button = (JButton) panelComp;
                        button.setBackground(darkMode ? new Color(60, 60, 60) : null);
                        button.setForeground(darkMode ? Color.WHITE : null);
                        button.setBorder(BorderFactory.createLineBorder(darkMode ? new Color(100, 100, 100) : null));
                    }
                }
            }
        }
        
        // Repaint the window
        repaint();
    }

    public static boolean isDarkMode() {
        return darkMode;
    }
} 

