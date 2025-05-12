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
        // Code inchangé
    }

    private void showSettings() {
        // Code inchangé
    }

    private void applyTheme() {
        // Code inchangé
    }

    public static boolean isDarkMode() {
        return darkMode;
    }
} 

