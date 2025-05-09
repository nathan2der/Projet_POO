package wargame.ui;

import wargame.game.Game;
import wargame.unit.Unit;
import wargame.unit.UnitType;
import wargame.player.Player;

import javax.swing.*;
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

    public InfoPanel(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(250, 600));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
        JPanel unitInfoPanel = new JPanel();
        unitInfoPanel.setLayout(new BoxLayout(unitInfoPanel, BoxLayout.Y_AXIS));
        unitInfoPanel.setBorder(BorderFactory.createTitledBorder("Selected Unit"));

        unitInfoLabel = new JLabel("No unit selected");
        unitInfoLabel.setFont(normalFont);
        unitInfoPanel.add(unitInfoLabel);

        // Game log section
        JPanel gameLogPanel = new JPanel(new BorderLayout());
        gameLogPanel.setBorder(BorderFactory.createTitledBorder("Game Log"));
        
        gameLog = new JTextArea();
        gameLog.setEditable(false);
        gameLog.setFont(normalFont);
        gameLog.setLineWrap(true);
        gameLog.setWrapStyleWord(true);
        
        gameLogScroll = new JScrollPane(gameLog);
        gameLogScroll.setPreferredSize(new Dimension(230, 200));
        gameLogPanel.add(gameLogScroll, BorderLayout.CENTER);

        // Add all sections to main panel
        add(gameStatePanel);
        add(Box.createVerticalStrut(10));
        add(playerStatsPanel);
        add(Box.createVerticalStrut(10));
        add(unitInfoPanel);
        add(Box.createVerticalStrut(10));
        add(gameLogPanel);
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
            unitInfo.append("Health: ").append(selectedUnit.getCurrentHealth())
                   .append("/").append(selectedUnit.getType().getHealth()).append("<br>");
            unitInfo.append("Movement: ").append(selectedUnit.getRemainingMovement())
                   .append("/").append(selectedUnit.getType().getMovement()).append("<br>");
            unitInfo.append("Attack: ").append(selectedUnit.getType().getAttack()).append("<br>");
            unitInfo.append("Defense: ").append(selectedUnit.getType().getDefense()).append("<br>");
            unitInfo.append("Range: ").append(selectedUnit.getType().getRange()).append("<br>");
            unitInfo.append("</html>");
            unitInfoLabel.setText(unitInfo.toString());
        } else {
            unitInfoLabel.setText("No unit selected");
        }
    }

    public void addGameLogEntry(String entry) {
        gameLog.append(entry + "\n");
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }
} 