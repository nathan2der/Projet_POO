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
    final InfoPanel infoPanel;
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
        
        // Set parent window reference for logging
        mapPanel.setParentWindow(this);
        
        // Create a split pane to allow resizing
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapPanel, infoPanel);
        splitPane.setResizeWeight(0.85); // Give more space to mapPanel
        
        // Add panels to window
        add(splitPane, BorderLayout.CENTER);
        
        // Create status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(statusLabel, BorderLayout.SOUTH);
        
        // Add initial message to game log
        infoPanel.addToGameLog("Game started");
        infoPanel.addToGameLog("--- Turn 1 begins ---");
        infoPanel.addToGameLog(game.getCurrentPlayer().getName() + "'s turn");
        
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
        JButton debugButton = new JButton("Debug Units");
        
        // Add action listeners
        endTurnButton.addActionListener(e -> endTurn());
        surrenderButton.addActionListener(e -> surrender());
        saveButton.addActionListener(e -> saveGame());
        loadButton.addActionListener(e -> loadGame());
        debugButton.addActionListener(e -> debugUnits());
        
        // Add buttons to toolbar
        toolBar.add(endTurnButton);
        toolBar.add(surrenderButton);
        toolBar.addSeparator();
        toolBar.add(saveButton);
        toolBar.add(loadButton);
        toolBar.add(debugButton);
        
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
        System.out.println("\n========== END TURN BUTTON CLICKED ==========");
        Player previousPlayer = game.getCurrentPlayer();
        
        // Store references to previousPlayer's units before end turn
        List<Unit> previousPlayerUnits = new ArrayList<>(previousPlayer.getUnits());
        
        // Print detailed debug info before ending turn
        System.out.println("BEFORE TURN END - Details for " + previousPlayer.getName() + "'s units:");
        for (Unit unit : previousPlayerUnits) {
            System.out.println("  Unit " + unit.getType() + 
                          " has movement " + unit.getRemainingMovement() + "/" + unit.getType().getMovement() +
                          ", hasMoved=" + unit.hasMoved() +
                          ", hasAttacked=" + unit.hasAttacked() +
                          ", ID=" + System.identityHashCode(unit));
        }
        
        // Print memory details of previous player's units
        System.out.println("MEMORY DETAILS before turn end:");
        for (Unit unit : previousPlayerUnits) {
            System.out.println("  Unit " + unit.getType() + " at " + 
                          System.identityHashCode(unit) + ", owner=" + 
                          System.identityHashCode(unit.getOwner()));
        }
        
        // End the turn through the Game object
        boolean continueGame = game.endTurn();
        
        Player newPlayer = game.getCurrentPlayer();
        
        // Verify movement points after turn end
        System.out.println("\nAFTER TURN END - Detailed unit status for all players:");
        for (Player player : game.getPlayers()) {
            System.out.println("Player: " + player.getName() + " (Is current: " + (player == newPlayer) + ")");
            for (Unit unit : player.getUnits()) {
                System.out.println("  Unit " + unit.getType() + " owned by " + player.getName() + 
                              " has movement " + unit.getRemainingMovement() + "/" + unit.getType().getMovement() +
                              ", hasMoved=" + unit.hasMoved() +
                              ", hasAttacked=" + unit.hasAttacked() +
                              ", ID=" + System.identityHashCode(unit));
            }
        }
        
        // Final safety check - ensure previous player's units have their movement properly reset
        // This should rarely be needed if the TurnManager.endTurn is working properly
        boolean anyUnitNeededReset = false;
        for (Unit unit : previousPlayerUnits) {
            // Only reset if actually needed
            if (unit.getRemainingMovement() < unit.getType().getMovement() || unit.hasMoved() || unit.hasAttacked()) {
                anyUnitNeededReset = true;
                System.out.println("EMERGENCY RESET for unit " + unit.getType() + " owned by " + previousPlayer.getName() + 
                                   " (ID=" + System.identityHashCode(unit) + ")");
                
                // Force a complete reset of all movement-related state
                unit.resetMovementPoints(); // This now resets both movement points and flags
                
                // Force explicit state reset as a last resort
                unit.setRemainingMovement(unit.getType().getMovement());
                unit.setHasMoved(false);
                unit.setHasAttacked(false);
            }
        }
        
        // Only print verification if a reset was needed
        if (anyUnitNeededReset) {
            System.out.println("\nAFTER EMERGENCY RESET:");
            for (Unit unit : previousPlayerUnits) {
                System.out.println("  Unit " + unit.getType() + " owned by " + previousPlayer.getName() + 
                              " now has movement " + unit.getRemainingMovement() + "/" + 
                              unit.getType().getMovement() +
                              ", hasMoved=" + unit.hasMoved() + 
                              ", hasAttacked=" + unit.hasAttacked());
            }
        }
        
        System.out.println("========== END TURN COMPLETE ==========\n");
        
        // Check for victory conditions
        if (!continueGame) {
            checkVictoryCondition();
            return;
        }
        
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
            Player winner = null;
            
            // Find the other player (the winner)
            for (Player player : game.getPlayers()) {
                if (player != currentPlayer) {
                    winner = player;
                    break;
                }
            }
            
            // Remove all units of the surrendering player
            List<Unit> unitsToRemove = new ArrayList<>(currentPlayer.getUnits());
            for (Unit unit : unitsToRemove) {
                if (unit.getTile() != null) {
                    unit.getTile().setUnit(null);
                }
                currentPlayer.removeUnit(unit);
            }
            
            // Log the surrender
            infoPanel.addToGameLog(currentPlayer.getName() + " has surrendered");
            
            // Update UI
            update();
            updateStatus("Game Over - " + currentPlayer.getName() + " has surrendered!");
            
            // Show victory dialog
            checkVictoryCondition();
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
    
    /**
     * Logs a unit movement action
     * @param unit The unit that moved
     * @param fromX The starting X coordinate
     * @param fromY The starting Y coordinate
     * @param toX The destination X coordinate
     * @param toY The destination Y coordinate
     */
    public void logUnitMovement(Unit unit, int fromX, int fromY, int toX, int toY) {
        String details = "from (" + fromX + "," + fromY + ") to (" + toX + "," + toY + ")";
        infoPanel.addUnitActionToLog(unit, "moved", details);
    }
    
    /**
     * Logs a unit attack action
     * @param attacker The attacking unit
     * @param defender The defending unit
     * @param damage The damage dealt
     */
    public void logUnitAttack(Unit attacker, Unit defender, int damage) {
        String details = "attacked " + defender.getOwner().getName() + "'s " + 
                         defender.getType() + " for " + damage + " damage";
        infoPanel.addUnitActionToLog(attacker, "", details);
        
        // Check if the defender was destroyed
        if (defender.getCurrentHealth() <= 0) {
            infoPanel.addToGameLog(defender.getOwner().getName() + "'s " + 
                defender.getType() + " was destroyed!");
            
            // Check if this caused a victory condition
            checkVictoryCondition();
        }
    }

    /**
     * Debug method to print detailed information about all units' movement points
     */
    private void debugUnits() {
        infoPanel.addToGameLog("==== DEBUG: UNIT MOVEMENT INFO ====");
        
        // Print info for current player
        Player currentPlayer = game.getCurrentPlayer();
        infoPanel.addToGameLog("Current player: " + currentPlayer.getName());
        
        for (Unit unit : currentPlayer.getUnits()) {
            String info = String.format("Unit %s: remainingMovement=%d, maxMovement=%d, hasMoved=%b",
                unit.getType(),
                unit.getRemainingMovement(),
                unit.getType().getMovement(),
                unit.hasMoved());
            infoPanel.addToGameLog(info);
        }
        
        // Print info for other players too
        for (Player player : game.getPlayers()) {
            if (player != currentPlayer) {
                infoPanel.addToGameLog("Other player: " + player.getName());
                for (Unit unit : player.getUnits()) {
                    String info = String.format("Unit %s: remainingMovement=%d, maxMovement=%d, hasMoved=%b",
                        unit.getType(),
                        unit.getRemainingMovement(),
                        unit.getType().getMovement(),
                        unit.hasMoved());
                    infoPanel.addToGameLog(info);
                }
            }
        }
        
        infoPanel.addToGameLog("==== END DEBUG INFO ====");
    }

    /**
     * Checks if the game has ended and shows a victory dialog if needed.
     * This should be called after any action that might result in a player's defeat.
     */
    public void checkVictoryCondition() {
        System.out.println("Checking victory condition...");
        Player winner = game.getWinner();
        
        // Debug output to help diagnose issues
        System.out.println("Winner check result: " + (winner != null ? winner.getName() : "No winner yet"));
        for (Player player : game.getPlayers()) {
            System.out.println("  " + player.getName() + " has " + player.getUnits().size() + " units left");
        }
        
        if (winner != null) {
            // Log the victory
            infoPanel.addToGameLog("Game Over - " + winner.getName() + " has won!");
            System.out.println("VICTORY DETECTED: " + winner.getName() + " has won!");
            
            try {
                // Use the standalone VictoryDialog class
                VictoryDialog.show(this, winner);
            } catch (Exception e) {
                System.err.println("Error showing victory dialog: " + e.getMessage());
                e.printStackTrace();
                
                // Fallback to simple message dialog if custom dialog fails
                JOptionPane.showMessageDialog(
                    this,
                    winner.getName() + " has won the game!\nAll enemy units have been defeated.",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }
    
    /**
     * Shows a simple victory dialog for the winning player.
     * This is a simplified version that doesn't rely on the MapPanel or AssetManager.
     * 
     * @param winner The winning player
     */
    private void showSimpleVictoryDialog(Player winner) {
        // Create custom dialog for victory announcement
        final JDialog victoryDialog = new JDialog(this, "Game Over", true);
        victoryDialog.setLayout(new BorderLayout());
        victoryDialog.setSize(400, 250);
        victoryDialog.setLocationRelativeTo(this);
        
        // Create victory message panel
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add victory message
        JLabel victoryLabel = new JLabel(winner.getName() + " has won the game!");
        victoryLabel.setFont(new Font("Arial", Font.BOLD, 24));
        victoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel detailsLabel = new JLabel("All enemy units have been defeated.");
        detailsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        detailsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components to message panel
        messagePanel.add(victoryLabel);
        messagePanel.add(Box.createVerticalStrut(20));
        messagePanel.add(detailsLabel);
        messagePanel.add(Box.createVerticalStrut(30));
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        
        // Add return to main menu button
        JButton menuButton = new JButton("Return to Main Menu");
        menuButton.setFont(new Font("Arial", Font.PLAIN, 16));
        menuButton.addActionListener(e -> {
            victoryDialog.dispose();
            returnToMainMenu();
        });
        
        // Add button to panel
        buttonPanel.add(menuButton);
        
        // Add panels to dialog
        victoryDialog.add(messagePanel, BorderLayout.CENTER);
        victoryDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Ensure dialog is shown on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Show the dialog
            victoryDialog.setVisible(true);
        });
    }
    
    /**
     * Returns to the main menu by closing the current window and opening the main menu.
     */
    private void returnToMainMenu() {
        // Create and show the main menu
        MainMenu mainMenu = new MainMenu();
        mainMenu.setVisible(true);
        
        // Close the current game window
        this.dispose();
    }
} 