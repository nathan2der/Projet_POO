package wargame.ui;

import wargame.player.Player;
import javax.swing.*;
import java.awt.*;

/**
 * A standalone dialog that shows the victory screen.
 * This class doesn't rely on MapPanel or AssetManager.
 */
public class VictoryDialog {
    
    /**
     * Shows a victory dialog for the winning player.
     * 
     * @param parent The parent component
     * @param winner The winning player
     */
    public static void show(JFrame parent, Player winner) {
        System.out.println("Showing victory dialog for " + winner.getName());
        
        // Create custom dialog for victory announcement
        final JDialog victoryDialog = new JDialog(parent, "Game Over", true);
        victoryDialog.setLayout(new BorderLayout());
        victoryDialog.setSize(400, 250);
        victoryDialog.setLocationRelativeTo(parent);
        victoryDialog.setAlwaysOnTop(true);
        victoryDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
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
            
            // Create and show the main menu
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
            
            // Close the parent game window
            parent.dispose();
        });
        
        // Add button to panel
        buttonPanel.add(menuButton);
        
        // Add panels to dialog
        victoryDialog.add(messagePanel, BorderLayout.CENTER);
        victoryDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show the dialog on the Event Dispatch Thread to ensure proper display
        SwingUtilities.invokeLater(() -> {
            try {
                victoryDialog.setVisible(true);
            } catch (Exception e) {
                System.err.println("Error showing victory dialog: " + e.getMessage());
                e.printStackTrace();
                
                // Fallback to simple message dialog if custom dialog fails
                JOptionPane.showMessageDialog(
                    parent,
                    winner.getName() + " has won the game!\nAll enemy units have been defeated.",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
    }
} 