package wargame.ui;

import wargame.game.Game;
import wargame.unit.Unit;
import wargame.unit.UnitType;
import wargame.player.Player;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Panel that displays game information and unit details.
 */
public class InfoPanel extends JPanel {
    private final Game game;
    private final JLabel turnLabel;
    private final JLabel playerLabel;
    private final JLabel unitInfoLabel;
    private final JLabel playerStatsLabel;
    private final JTextArea gameLog;
    private final JScrollPane gameLogScroll;
    private final JPanel gameLogPanel;
    private final JPanel unitInfoPanel;
    private final JTextArea unitInfo;

    public InfoPanel(Game game) {
        this.game = game;
        this.unitInfo = new JTextArea();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 0));
        
        // Apply dark mode if enabled
        if (MainMenu.isDarkMode()) {
            setBackground(new Color(43, 43, 43));
        }
        
        // Create labels with custom fonts
        Font headerFont = new Font("Arial", Font.BOLD, 14);
        Font normalFont = new Font("Arial", Font.PLAIN, 12);

        // Game state section
        JPanel gameStatePanel = new JPanel();
        gameStatePanel.setLayout(new BoxLayout(gameStatePanel, BoxLayout.Y_AXIS));
        gameStatePanel.setBorder(BorderFactory.createTitledBorder("Game State"));

        turnLabel = new JLabel("Turn: 1");
        turnLabel.setFont(headerFont);
        playerLabel = new JLabel("Current Player: Player 1");
        playerLabel.setFont(headerFont);

        gameStatePanel.add(turnLabel);
        gameStatePanel.add(Box.createVerticalStrut(5));
        gameStatePanel.add(playerLabel);

        // Player stats section
        JPanel playerStatsPanel = new JPanel();
        playerStatsPanel.setLayout(new BoxLayout(playerStatsPanel, BoxLayout.Y_AXIS));
        playerStatsPanel.setBorder(BorderFactory.createTitledBorder("Player Stats"));

        playerStatsLabel = new JLabel();
        playerStatsLabel.setFont(normalFont);
        playerStatsPanel.add(playerStatsLabel);

        // Unit info section
        unitInfoPanel = new JPanel();
        unitInfoPanel.setLayout(new BorderLayout());
        unitInfoPanel.setBorder(BorderFactory.createTitledBorder("Selected Unit"));
        if (MainMenu.isDarkMode()) {
            unitInfoPanel.setBackground(new Color(43, 43, 43));
            unitInfoPanel.setForeground(Color.WHITE);
            ((TitledBorder)unitInfoPanel.getBorder()).setTitleColor(Color.WHITE);
        }

        unitInfoLabel = new JLabel("No unit selected");
        unitInfoLabel.setFont(normalFont);
        unitInfoPanel.add(unitInfoLabel, BorderLayout.CENTER);

        // Game log section
        gameLogPanel = new JPanel();
        gameLogPanel.setLayout(new BorderLayout());
        gameLogPanel.setBorder(BorderFactory.createTitledBorder("Game Log"));
        if (MainMenu.isDarkMode()) {
            gameLogPanel.setBackground(new Color(43, 43, 43));
            gameLogPanel.setForeground(Color.WHITE);
            ((TitledBorder)gameLogPanel.getBorder()).setTitleColor(Color.WHITE);
        }
        
        gameLog = new JTextArea();
        gameLog.setEditable(false);
        gameLog.setFont(normalFont);
        gameLog.setLineWrap(true);
        gameLog.setWrapStyleWord(true);
        if (MainMenu.isDarkMode()) {
            gameLog.setBackground(new Color(60, 60, 60));
            gameLog.setForeground(Color.WHITE);
            gameLog.setCaretColor(Color.WHITE);
        }
        
        gameLogScroll = new JScrollPane(gameLog);
        if (MainMenu.isDarkMode()) {
            gameLogScroll.getViewport().setBackground(new Color(60, 60, 60));
        }
        gameLogPanel.add(gameLogScroll, BorderLayout.CENTER);

        // Create a split pane to allow resizing
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gameLogPanel, unitInfoPanel);
        splitPane.setResizeWeight(0.7); // Give more space to game log
        
        // Add split pane to panel
        add(splitPane, BorderLayout.CENTER);
    }

    public void update() {
        // Update game state
        turnLabel.setText("Turn: " + game.getTurnNumber());
        playerLabel.setText("Current Player: " + game.getCurrentPlayer().getName());
        
        // Update player stats
        Player currentPlayer = game.getCurrentPlayer();
        StringBuilder stats = new StringBuilder();
        stats.append("<html>");
        stats.append("Units: ").append(currentPlayer.getUnits().size()).append("<br>");
        stats.append("Alive Units: ").append(currentPlayer.getUnits().stream()
            .filter(Unit::isAlive)
            .count()).append("<br>");
        stats.append("</html>");
        playerStatsLabel.setText(stats.toString());
        
        // Update unit info
        Unit selectedUnit = game.getSelectedUnit();
        if (selectedUnit != null) {
            StringBuilder unitInfo = new StringBuilder();
            unitInfo.append("<html>");
            unitInfo.append("Type: ").append(selectedUnit.getType()).append("<br>");
            
            // Health with color indication
            int healthPercentage = selectedUnit.getCurrentHealth() * 100 / selectedUnit.getType().getHealth();
            String healthColor = healthPercentage > 66 ? "green" : (healthPercentage > 33 ? "orange" : "red");
            unitInfo.append("Health: <font color='").append(healthColor).append("'>")
                   .append(selectedUnit.getCurrentHealth())
                   .append("/").append(selectedUnit.getType().getHealth())
                   .append("</font><br>");
            
            // Movement with status indication
            String moveStatus = selectedUnit.hasMoved() ? " (Moved)" : " (Ready)";
            unitInfo.append("Movement: <b>").append(selectedUnit.getRemainingMovement())
                   .append("/").append(selectedUnit.getType().getMovement())
                   .append("</b>").append(moveStatus).append("<br>");
            
            // Terrain info if on a tile
            if (selectedUnit.getTile() != null) {
                unitInfo.append("Terrain: ").append(selectedUnit.getTile().getTerrainType())
                       .append(" (Def: +").append(selectedUnit.getTile().getTerrainType().getDefenseBonus())
                       .append(", Move: ").append(selectedUnit.getTile().getTerrainType().getMovementCost())
                       .append(")<br>");
            }
            
            // Combat stats
            unitInfo.append("Attack: ").append(selectedUnit.getType().getAttack()).append("<br>");
            unitInfo.append("Defense: ").append(selectedUnit.getType().getDefense()).append("<br>");
            unitInfo.append("Range: ").append(selectedUnit.getType().getRange()).append("<br>");
            
            // Attack status
            unitInfo.append("Status: ").append(selectedUnit.hasAttacked() ? 
                           "<font color='red'>Has attacked</font>" : 
                           "<font color='green'>Can attack</font>").append("<br>");
            
            unitInfo.append("</html>");
            unitInfoLabel.setText(unitInfo.toString());
        } else {
            unitInfoLabel.setText("No unit selected");
        }
    }

    /**
     * Adds a message to the game log.
     * @param message The message to add
     */
    public void addToGameLog(String message) {
        gameLog.append(message + "\n");
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }
    
    /**
     * Adds a unit action to the game log with proper formatting.
     * @param unit The unit performing the action
     * @param action The action being performed
     * @param details Additional details about the action
     */
    public void addUnitActionToLog(Unit unit, String action, String details) {
        String playerName = unit.getOwner().getName();
        String unitType = unit.getType().toString();
        String message = playerName + "'s " + unitType + " " + action + " " + details;
        addToGameLog(message);
    }
    
    /**
     * Clears the game log.
     */
    public void clearGameLog() {
        gameLog.setText("");
    }

    public void updateUnitInfo(Unit unit) {
        if (unit == null) {
            unitInfo.setText("");
            return;
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Type: ").append(unit.getType()).append("\n");
        info.append("Health: ").append(unit.getHealth()).append("/").append(unit.getMaxHealth()).append("\n");
        info.append("Movement: ").append(unit.getMovementPoints()).append("\n");
        info.append("Owner: ").append(unit.getOwner().getName()).append("\n");
        
        unitInfo.setText(info.toString());
    }
} 