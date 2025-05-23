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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import controleur.Jeu;

/**
 * Menu est une classe sur laquelle on choisit les paramètres de jeu (JvJ, JvIA,
 * Règles, Charger une partie).
 * 
 * 
 *
 */
public class Menu {

    /**
     * Fenêtre du menu.
     */
    private JFrame frame;
    /**
     * Séparateur de fichier que ce soit sur Linux ou Windows.
     */
    private String separateur = System.getProperty("file.separator");
    private JComboBox<Integer> totalPlayersCombo;
    private JComboBox<Integer> aiPlayersCombo;

    /**
     * Crée l'application.
     */
    public Menu() {
        initialize();
    }

    /**
     * Initialise le contenu de la fenêtre.
     */
    private void initialize() {
        Dimension size = new Dimension(1500, 900);
        frame = new JFrame("Wargame");
        frame.setBounds(100, 30, 1300, 700);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(size);
        frame.setMaximumSize(size);
        frame.setMinimumSize(size);
        frame.getContentPane().setBackground(Color.BLACK);

        // Load background image
        Image imagefond = null;
        try {
            imagefond = ImageIO.read(new File("images/war.jpg"));
        } catch (IOException e) {e.printStackTrace();
            System.exit(-1);
        }

        CustomPanel panel = new CustomPanel(imagefond);
        SpringLayout sl_panel = new SpringLayout();
        panel.setLayout(sl_panel);

        // Create main menu panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("WARGAME");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 120));
        titleLabel.setForeground(Color.RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(titleLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 60)));

        // Player selection panel
        JPanel playerSelectionPanel = new JPanel();
        playerSelectionPanel.setLayout(new BoxLayout(playerSelectionPanel, BoxLayout.Y_AXIS));
        playerSelectionPanel.setOpaque(false);

        // Total players selection
        JPanel totalPlayersPanel = new JPanel();
        totalPlayersPanel.setOpaque(false);
        JLabel totalPlayersLabel = new JLabel("Nombre total de joueurs: ");
        totalPlayersLabel.setForeground(Color.WHITE);
        totalPlayersLabel.setFont(new Font("Arial", Font.BOLD, 50));
        totalPlayersCombo = new JComboBox<>(new Integer[]{2, 3});
        totalPlayersCombo.setPreferredSize(new Dimension(90, 50));
        totalPlayersPanel.add(totalPlayersLabel);
        totalPlayersPanel.add(totalPlayersCombo);

        // AI players selection
        JPanel aiPlayersPanel = new JPanel();
        aiPlayersPanel.setOpaque(false);
        JLabel aiPlayersLabel = new JLabel("Nombre de joueurs IA: ");
        aiPlayersLabel.setForeground(Color.WHITE);
        aiPlayersLabel.setFont(new Font("Arial", Font.BOLD, 50));
        aiPlayersCombo = new JComboBox<>(new Integer[]{0, 1});
        aiPlayersCombo.setPreferredSize(new Dimension(90, 50));
        aiPlayersPanel.add(aiPlayersLabel);
        aiPlayersPanel.add(aiPlayersCombo);

        // Update AI players options when total players changes
        totalPlayersCombo.addActionListener(e -> {
            int totalPlayers = (Integer) totalPlayersCombo.getSelectedItem();
            aiPlayersCombo.removeAllItems();
            for (int i = 0; i <= totalPlayers - 1; i++) {
                aiPlayersCombo.addItem(i);
            }
        });

        playerSelectionPanel.add(totalPlayersPanel);
        playerSelectionPanel.add(aiPlayersPanel);
        menuPanel.add(playerSelectionPanel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 80)));

        // Create buttons with consistent styling
        JButton playButton = createStyledButton("JOUER");
        JButton loadButton = createStyledButton("CHARGER PARTIE");
        JButton rulesButton = createStyledButton("RÈGLES DU JEU");

        // Play button action
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int totalPlayers = (Integer) totalPlayersCombo.getSelectedItem();
                int aiPlayers = (Integer) aiPlayersCombo.getSelectedItem();
                frame.dispose();
                Jeu.start(totalPlayers - aiPlayers, aiPlayers);
                Jeu.setStarted(true);
            }
        });

        // Load game button action
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("." + separateur + "saves" + separateur));
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    frame.dispose();
                    Jeu.chargerPartie("." + separateur + "saves" + separateur + fileChooser.getSelectedFile().getName());
                    Jeu.start();
                }
            }
        });

        // Rules button action
        rulesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Regles();
            }
        });

        menuPanel.add(playButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(loadButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(rulesButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add quit button
        JButton quitButton = createStyledButton("QUITTER");
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menuPanel.add(quitButton);

        // Center the menu panel
        sl_panel.putConstraint(SpringLayout.HORIZONTAL_CENTER, menuPanel, 0, SpringLayout.HORIZONTAL_CENTER, panel);
        sl_panel.putConstraint(SpringLayout.VERTICAL_CENTER, menuPanel, 0, SpringLayout.VERTICAL_CENTER, panel);
        panel.add(menuPanel);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(300, 50));
        button.setMaximumSize(new Dimension(300, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setFocusPainted(false);
        return button;
    }
}
