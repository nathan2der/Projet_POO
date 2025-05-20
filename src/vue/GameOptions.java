package vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import controleur.Jeu;

public class GameOptions {
    private JFrame frame;
    private JComboBox<Integer> totalPlayersCombo;
    private JComboBox<Integer> aiPlayersCombo;
    private JFrame parentFrame;

    public GameOptions(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Options de jeu");
        frame.setBounds(100, 30, 800, 500);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.getContentPane().setBackground(Color.BLACK);

        // Load background image
        Image imagefond = null;
        try {
            imagefond = ImageIO.read(new File("images/war.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        CustomPanel panel = new CustomPanel(imagefond);
        SpringLayout sl_panel = new SpringLayout();
        panel.setLayout(sl_panel);

        // Create options panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Options de jeu");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionsPanel.add(titleLabel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Total players selection
        JPanel totalPlayersPanel = new JPanel();
        totalPlayersPanel.setOpaque(false);
        JLabel totalPlayersLabel = new JLabel("Nombre total de joueurs: ");
        totalPlayersLabel.setForeground(Color.WHITE);
        totalPlayersLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        totalPlayersCombo = new JComboBox<>(new Integer[]{2, 3});
        totalPlayersCombo.setPreferredSize(new Dimension(90, 40));
        totalPlayersCombo.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        totalPlayersPanel.add(totalPlayersLabel);
        totalPlayersPanel.add(totalPlayersCombo);
        optionsPanel.add(totalPlayersPanel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // AI players selection
        JPanel aiPlayersPanel = new JPanel();
        aiPlayersPanel.setOpaque(false);
        JLabel aiPlayersLabel = new JLabel("Nombre de joueurs IA: ");
        aiPlayersLabel.setForeground(Color.WHITE);
        aiPlayersLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        aiPlayersCombo = new JComboBox<>(new Integer[]{0, 1});
        aiPlayersCombo.setPreferredSize(new Dimension(90, 40));
        aiPlayersCombo.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        aiPlayersPanel.add(aiPlayersLabel);
        aiPlayersPanel.add(aiPlayersCombo);
        optionsPanel.add(aiPlayersPanel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Update AI players options when total players changes
        totalPlayersCombo.addActionListener(e -> {
            int totalPlayers = (Integer) totalPlayersCombo.getSelectedItem();
            aiPlayersCombo.removeAllItems();
            for (int i = 0; i <= totalPlayers - 1; i++) {
                aiPlayersCombo.addItem(i);
            }
        });

        // Start button
        JButton startButton = new JButton("COMMENCER");
        startButton.setPreferredSize(new Dimension(250, 50));
        startButton.setMaximumSize(new Dimension(250, 50));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setBackground(new Color(51, 102, 0));
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        startButton.setFocusPainted(false);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int totalPlayers = (Integer) totalPlayersCombo.getSelectedItem();
                int aiPlayers = (Integer) aiPlayersCombo.getSelectedItem();
                parentFrame.dispose();
                frame.dispose();
                Jeu.start(totalPlayers - aiPlayers, aiPlayers);
                Jeu.setStarted(true);
            }
        });

        optionsPanel.add(startButton);

        // Center the options panel
        sl_panel.putConstraint(SpringLayout.HORIZONTAL_CENTER, optionsPanel, 0, SpringLayout.HORIZONTAL_CENTER, panel);
        sl_panel.putConstraint(SpringLayout.VERTICAL_CENTER, optionsPanel, 0, SpringLayout.VERTICAL_CENTER, panel);
        panel.add(optionsPanel);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
} 