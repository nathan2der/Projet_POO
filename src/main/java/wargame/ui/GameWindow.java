package wargame.ui;

import wargame.game.Game;
import wargame.map.GameMap;
import wargame.player.Player;
import wargame.unit.Unit;
import wargame.unit.UnitType;
import wargame.terrain.TerrainType;
import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * Main game window that contains all UI components.
 */
public class GameWindow extends JFrame {
    private final MapPanel mapPanel;
    private final InfoPanel infoPanel;
    private final JToolBar toolBar;
    private final JLabel statusLabel;
    private final Game game;

    public GameWindow(Game game) {
        this.game = game;
        
        // Setup window
        setTitle("Wargame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create menu bar
        createMenuBar();
        
        // Create toolbar
        toolBar = createToolBar();
        add(toolBar, BorderLayout.NORTH);
        
        // Create panels
        mapPanel = new MapPanel(game);
        infoPanel = new InfoPanel(game);
        
        // Create a split pane to allow resizing
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapPanel, infoPanel);
        splitPane.setResizeWeight(0.85); // Give more space to mapPanel
        
        // Add panels to window
        add(splitPane, BorderLayout.CENTER);
        
        // Create status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(statusLabel, BorderLayout.SOUTH);
        
        // Calculate optimal window size based on map dimensions
        GameMap map = game.getGameMap();
        double hexSize = 40; // Base hex size
        
        // Calculate total width needed for the map
        double mapWidth = map.getWidth() * hexSize * Math.sqrt(3) * 0.75;
        // Add space for info panel and some padding
        double totalWidth = mapWidth + 300; // 250px for info panel + 50px padding
        
        // Calculate total height needed
        double mapHeight = map.getHeight() * hexSize * 1.5;
        // Add space for toolbar, menu, status bar, and padding
        double totalHeight = mapHeight + 100; // Extra space for UI elements
        
        // Set window size (ensure minimum size and maximum based on screen)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) Math.min(Math.max(totalWidth, 800), screenSize.width * 0.9);
        int height = (int) Math.min(Math.max(totalHeight, 600), screenSize.height * 0.9);
        setSize(width, height);
        
        // Set split pane divider location
        splitPane.setDividerLocation((int)(width * 0.85));
        
        // Center on screen
        setLocationRelativeTo(null);
        
        // Make window visible
        setVisible(true);
        
        // Add component listener to handle resizing
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                splitPane.setDividerLocation((int)(getWidth() * 0.85));
                mapPanel.repaint();
            }
        });
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem saveGameItem = new JMenuItem("Save Game");
        JMenuItem loadGameItem = new JMenuItem("Load Game");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        fileMenu.add(newGameItem);
        fileMenu.add(saveGameItem);
        fileMenu.add(loadGameItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Game menu
        JMenu gameMenu = new JMenu("Game");
        JMenuItem endTurnItem = new JMenuItem("End Turn");
        JMenuItem surrenderItem = new JMenuItem("Surrender");
        
        gameMenu.add(endTurnItem);
        gameMenu.add(surrenderItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem rulesItem = new JMenuItem("Game Rules");
        JMenuItem aboutItem = new JMenuItem("About");
        
        helpMenu.add(rulesItem);
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(gameMenu);
        menuBar.add(helpMenu);
        
        // Add action listeners
        newGameItem.addActionListener(e -> startNewGame());
        saveGameItem.addActionListener(e -> saveGame());
        loadGameItem.addActionListener(e -> loadGame());
        exitItem.addActionListener(e -> System.exit(0));
        endTurnItem.addActionListener(e -> endTurn());
        surrenderItem.addActionListener(e -> surrender());
        rulesItem.addActionListener(e -> showRules());
        aboutItem.addActionListener(e -> showAbout());
        
        setJMenuBar(menuBar);
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Add buttons
        JButton endTurnButton = new JButton("End Turn");
        JButton surrenderButton = new JButton("Surrender");
        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load");
        
        // Add action listeners
        endTurnButton.addActionListener(e -> endTurn());
        surrenderButton.addActionListener(e -> surrender());
        saveButton.addActionListener(e -> saveGame());
        loadButton.addActionListener(e -> loadGame());
        
        // Add buttons to toolbar
        toolBar.add(endTurnButton);
        toolBar.add(surrenderButton);
        toolBar.addSeparator();
        toolBar.add(saveButton);
        toolBar.add(loadButton);
        
        return toolBar;
    }

    private void startNewGame() {
        int response = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to start a new game?",
            "New Game",
            JOptionPane.YES_NO_OPTION
        );
        
        if (response == JOptionPane.YES_OPTION) {
            // Create new game map
            GameMap map = new GameMap(10, 10);
            
            // Create players
            Player player1 = new Player("Player 1");
            Player player2 = new Player("Player 2");
            
            // Create game with players
            List<Player> players = new ArrayList<>();
            players.add(player1);
            players.add(player2);
            Game newGame = new Game(map, players);
            
            // Create and place units for player 1
            Unit infantry1 = new Unit(UnitType.INFANTRY, player1);
            Unit archer1 = new Unit(UnitType.ARCHER, player1);
            Unit cavalry1 = new Unit(UnitType.CAVALRY, player1);
            
            // Place player 1 units on the left side
            newGame.placeUnit(infantry1, 0, 0);
            newGame.placeUnit(archer1, 1, 0);
            newGame.placeUnit(cavalry1, 2, 0);
            
            // Create and place units for player 2
            Unit infantry2 = new Unit(UnitType.INFANTRY, player2);
            Unit heavyInfantry2 = new Unit(UnitType.HEAVY_INFANTRY, player2);
            Unit mage2 = new Unit(UnitType.MAGE, player2);
            
            // Place player 2 units on the right side
            newGame.placeUnit(infantry2, 7, 9);
            newGame.placeUnit(heavyInfantry2, 8, 9);
            newGame.placeUnit(mage2, 9, 9);
            
            // Add some terrain variety
            for (int x = 3; x < 7; x++) {
                for (int y = 3; y < 7; y++) {
                    map.getTile(x, y).setTerrainType(TerrainType.FOREST);
                }
            }
            
            // Create new window with the new game
            GameWindow newWindow = new GameWindow(newGame);
            newWindow.setVisible(true);
            this.dispose();
        }
    }

    private void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Wargame Save Files", "wgs"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getPath();
                if (!filePath.toLowerCase().endsWith(".wgs")) {
                    filePath += ".wgs";
                }
                
                // Save game state
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                    oos.writeObject(game);
                    updateStatus("Game saved successfully to " + filePath);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error saving game: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
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
                    
                    // Create new window with the loaded game
                    GameWindow newWindow = new GameWindow(loadedGame);
                    newWindow.setVisible(true);
                    this.dispose();
                }
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error loading game: " + e.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void endTurn() {
        game.endTurn();
        updateStatus("Turn ended - " + game.getCurrentPlayer().getName() + "'s turn");
        update();
    }

    private void surrender() {
        int response = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to surrender?",
            "Surrender",
            JOptionPane.YES_NO_OPTION
        );
        
        if (response == JOptionPane.YES_OPTION) {
            Player currentPlayer = game.getCurrentPlayer();
            
            // Remove all units of the surrendering player
            currentPlayer.getUnits().forEach(unit -> {
                if (unit.getTile() != null) {
                    unit.getTile().setUnit(null);
                }
            });
            currentPlayer.getUnits().clear();
            
            // Show victory message
            JOptionPane.showMessageDialog(
                this,
                "Game Over - " + currentPlayer.getName() + " has surrendered.",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Update UI
            update();
            updateStatus("Game Over - " + currentPlayer.getName() + " has surrendered!");
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

    private void showAbout() {
        JOptionPane.showMessageDialog(
            this,
            "Wargame v1.0\n" +
            "A turn-based strategy game with hexagonal grid\n" +
            "Created as a POO project",
            "About",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    /**
     * Updates all UI components
     */
    public void update() {
        mapPanel.repaint();
        infoPanel.update();
        updateStatus(game.getCurrentPlayer().getName() + "'s turn");
    }
} 