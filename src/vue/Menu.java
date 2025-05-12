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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import controleur.Jeu;

/**
 * Menu est une classe sur laquelle on choisit les paramètres de jeu (JvJ, JvIA,
 * Règles, Charger une partie).
 * 
 * @author Stefano
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

        Dimension size = new Dimension(1300, 700);
        frame = new JFrame("Wargame");
        frame.setBounds(100, 30 , 1300, 700);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(size);
        frame.setMaximumSize(size);
        frame.setMinimumSize(size);
        frame.getContentPane().setBackground(Color.BLACK);

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

        Integer[] items = { 2 };
        JComboBox<Integer> liste_1 = new JComboBox<Integer>(items);
        liste_1.setPreferredSize(new Dimension(80, 40));
        liste_1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton jvj = new JButton("PLAY");
        jvj.setPreferredSize(new Dimension(300, 60));
        jvj.setAlignmentX(Component.RIGHT_ALIGNMENT);
        jvj.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
                Integer nbJoueur = (Integer) liste_1.getSelectedItem();
                frame.dispose();
                Jeu.start(nbJoueur, 0);
                Jeu.setStarted(true);
                
            }
        });
        panel.add(jvj);
        

        // Positionnement du bouton Play au milieu du panneau
        sl_panel.putConstraint(SpringLayout.HORIZONTAL_CENTER, jvj, 0, SpringLayout.HORIZONTAL_CENTER, panel);
        sl_panel.putConstraint(SpringLayout.VERTICAL_CENTER, jvj, 280, SpringLayout.VERTICAL_CENTER, panel);
        jvj.setBackground(Color.black);
        jvj.setForeground(Color.white);
        jvj.setFont(new Font("ARIAL", Font.BOLD, 20)); 
        
        frame.add(panel, BorderLayout.CENTER);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setVisible(true);
    }
}
