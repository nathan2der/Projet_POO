package vue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import controleur.Jeu;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * MainJFrame est la fenêtre du jeu.
 * @author Stefano
 *
 */
public class MainJFrame {
    /**
     * Fenetre principale du jeu.
     */
    private JFrame frame;

    /**
     * Coordonnées de la souris.
     */
    private Point mouse = new Point(-1, -1);
    /**
     * Caractéristiques de l'unité séléctionée.
     */
    private ArrayList<JLabel> listeCaractAffichage = new ArrayList<JLabel>();
    /**
     * String pour représenter le séparateur de fichier que ce soit sur Linux ou
     * Windows.
     */
    private String separateur = System.getProperty("file.separator");
    /**
     * Image de l'unité sélectionnée.
     */
    private PaintImage image = new PaintImage("images/orc_inconnu.png");

    /**
     * Dernière attaque.
     */
    private JLabel labelLastAtt;

    /**
     * Crée l'application.
     */
    public MainJFrame() {
        initialize();
    }

    /**
     * Initialise le contenu de la fenêtre.
     */ 
  
    private void initialize() {
        frame = new JFrame();
        // Récupérer la taille de l'écran
        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int h=screenSize.height;
        int w=screenSize.width; 
        */
        frame.setBounds(330, 5 , 997, 780);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        Color color = new Color(139,0,0); 
        frame.getContentPane().setBackground(color);

        Jeu.setPlateau(new Affplateau());
        Jeu.getPlateau().setBounds(0, 21, 997, 550); 
        Jeu.getPlateau().addMouseListener(new MouseListener() {

            public void mouseClicked(final MouseEvent e) {
            }

            public void mousePressed(final MouseEvent e) {
                mouse = e.getPoint();
                Jeu.setClicFlag(true);
                System.out.println();
                ArrayList<String> caracteristiquesUnite = Jeu.getCaractUniteEnMouvement(Jeu.getCoordHexaClicked());
                if (!caracteristiquesUnite.isEmpty()) {
                    listeCaractAffichage.get(0).setText(caracteristiquesUnite.get(0));
                    listeCaractAffichage.get(1).setText(caracteristiquesUnite.get(1));
                    listeCaractAffichage.get(2).setText(caracteristiquesUnite.get(2));
                    listeCaractAffichage.get(3).setText(caracteristiquesUnite.get(3));
                    listeCaractAffichage.get(4).setText(caracteristiquesUnite.get(4));
                    listeCaractAffichage.get(5).setText(caracteristiquesUnite.get(5));

                    switch (Integer.parseInt(caracteristiquesUnite.get(6))) {
                    case Jeu.ARCHER:
                        image = new PaintImage("images/GrandArcher.png");
                        
                        break;
                    case Jeu.CHEVALIER:
                        image = new PaintImage("images/GrandChevalier.png");
                        break;
                    case Jeu.GUERRIER:
                        image = new PaintImage("images/GrandGuerrier.png");
                        break;
                    case Jeu.PRETRE:
                        image = new PaintImage("images/GrandPretre.png");
                        break;
                    case Jeu.MAGE:
                        image = new PaintImage("images/GrandMage.png");
                        break;
                    default:
                        break;
                    }
                }
                labelLastAtt.setText(Jeu.getLastAttaque());
            }

            public void mouseReleased(final MouseEvent e) {
            }

            public void mouseEntered(final MouseEvent e) {
            }

            public void mouseExited(final MouseEvent e) {
            }

        });
        frame.getContentPane().add(Jeu.getPlateau());

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 1300, 21);
        frame.getContentPane().add(menuBar);

        JMenu mnFichier = new JMenu("Fichier");

        menuBar.add(mnFichier);

        JMenuItem mntmEnregistrer = new JMenuItem("Enregistrer");
        mntmEnregistrer.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
                JFileChooser choix = new JFileChooser();
                choix.setCurrentDirectory(new File(
                        "." + System.getProperty("file.separator") + "saves" + System.getProperty("file.separator")));
                int retour = choix.showSaveDialog(frame);
                if (retour == JFileChooser.APPROVE_OPTION) {
                    Jeu.sauvegarderPartie("." + System.getProperty("file.separator") + "saves"
                            + System.getProperty("file.separator") + choix.getSelectedFile().getName());
                    JOptionPane.showMessageDialog(null, "Partie enregistrée", "Sauvegarde",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        mnFichier.add(mntmEnregistrer);

        JMenuItem mntmRetourMenu = new JMenuItem("Retour Menu");
        mntmRetourMenu.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
                Jeu.kill();
            }
        });
        mnFichier.add(mntmRetourMenu);

        JPanel panel = new JPanel();
        panel.setBounds(200, 270, 4000, 5910);
        frame.getContentPane().add(panel);
        Color color1 = new Color(139,0,0); 
        panel.setBackground(color1);

        JButton btnNewButton = new JButton("SKIP");
        btnNewButton.setBounds(540, 350, 200, 50);
        btnNewButton.setFont(new Font("ARIAL", Font.BOLD, 20)); 
        Color color_fin_tour = new Color(51,102,0); 
        btnNewButton.setBackground(color_fin_tour);
        btnNewButton.setForeground(Color.WHITE);
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
                Jeu.setSkipFlag(true);
            }
        });
        panel.setLayout(null); 
        panel.add(btnNewButton);
 
        
        
        JLabel lblNewLabel = new JLabel("PA -> ");
        lblNewLabel.setBounds(10, 379, 91, 14);
        lblNewLabel.setForeground(new Color(255, 255, 255));
        panel.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("P_Def-> ");
        lblNewLabel_1.setBounds(10, 354, 129, 14);
        lblNewLabel_1.setForeground(new Color(255, 255, 255));
        panel.add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("Vision -> ");
        lblNewLabel_2.setBounds(10, 404, 91, 14);
        lblNewLabel_2.setForeground(new Color(255, 255, 255));
        panel.add(lblNewLabel_2);

        JLabel lblPv = new JLabel("PV max -> ");
        lblPv.setBounds(10, 329, 91, 14);
        lblPv.setForeground(new Color(255, 255, 255));
        panel.add(lblPv);

        JLabel lblPointDeVie = new JLabel("PV restant -> ");
        lblPointDeVie.setBounds(10, 304, 116, 20);
        lblPointDeVie.setForeground(new Color(255, 255, 255));
        panel.add(lblPointDeVie);

        JLabel lblPointDeDeplacement = new JLabel("PM ->  ");
        lblPointDeDeplacement.setBounds(10, 435, 158, 14);
        lblPointDeDeplacement.setForeground(new Color(255, 255, 255));
        panel.add(lblPointDeDeplacement);

        JLabel lblNewLabelAffichagePvRest = new JLabel("?");
        lblNewLabelAffichagePvRest.setBounds(94, 304, 46, 14);
        lblNewLabelAffichagePvRest.setForeground(new Color(255, 255, 255));
        panel.add(lblNewLabelAffichagePvRest);
        listeCaractAffichage.add(lblNewLabelAffichagePvRest);

        JLabel labelAffichagePvMax = new JLabel("?");
        labelAffichagePvMax.setBounds(93, 329, 46, 14);
        labelAffichagePvMax.setForeground(new Color(255, 255, 255));
        panel.add(labelAffichagePvMax);
        listeCaractAffichage.add(labelAffichagePvMax);

        JLabel labelAffichageDefense = new JLabel("?");
        labelAffichageDefense.setBounds(94, 354, 46, 14);
        labelAffichageDefense.setForeground(new Color(255, 255, 255));
        panel.add(labelAffichageDefense);
        listeCaractAffichage.add(labelAffichageDefense);

        JLabel labelAffichageAttaque = new JLabel("?");
        labelAffichageAttaque.setBounds(93, 379, 46, 14);
        labelAffichageAttaque.setForeground(new Color(255, 255, 255));
        panel.add(labelAffichageAttaque);
        listeCaractAffichage.add(labelAffichageAttaque);

        JLabel labelVision = new JLabel("?");
        labelVision.setBounds(94, 404, 46, 14);
        labelVision.setForeground(new Color(255, 255, 255));
        panel.add(labelVision);
        listeCaractAffichage.add(labelVision);

        JLabel labelAffichagePtDeDeplacement = new JLabel("?");
        labelAffichagePtDeDeplacement.setBounds(93, 435, 46, 14);
        labelAffichagePtDeDeplacement.setForeground(new Color(255, 255, 255));
        panel.add(labelAffichagePtDeDeplacement);
        listeCaractAffichage.add(labelAffichagePtDeDeplacement);

        //image.setBounds(150, 250, 316, 1000);
        image.setBounds(200, 315, 216, 150);
        panel.add(image);

        labelLastAtt = new JLabel("");
        labelLastAtt.setBounds(10, 520, 216, 60);
        panel.add(labelLastAtt);
        frame.setResizable(false);

    }

    /**
     * Renvoie la fenêtre principale.
     * @return La fenêtre principale.
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Renvoie la position du clic.
     * @return la position du clic.
     */
    public Point getClicPos() {
        return mouse;
    }
}
