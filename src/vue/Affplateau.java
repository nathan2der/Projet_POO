package vue;

import controleur.Jeu;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Affplateau est la classe fille de JPanel, elle affiche le plateau de jeu et
 * son contenu.
 * @author Stefano
 *
 */
public class Affplateau extends JPanel {
    /**
     * Numéro de serial Version.
     */
    private static final long serialVersionUID = -2264167392249814615L;
    /**
     * Taille du côté d'un hexagone.
     */
    static final int COTE = 30;
    /**
     * Polygone de la forme d'une hexagone.
     */
    private Polygon pol;
    /**
     * Graphics2D dans lequel on dessine les différents éléments.
     */
    private Graphics2D g2d;
    /**
     * Buffered Image qui aura l'image chargée depuis un fichier png.
     */
    private BufferedImage bim;
    /**
     * Rectangle le plus petit dans un hexagone.
     */
    private Rectangle r;
    /**
     * Graphic pris en paramètre de le fonction paint.
     */
    private Graphics graph;
    /**
     * String pour représenter le séparateur de fichier que ce soit sur Linux ou
     * Windows.
     */
    private String separateur = System.getProperty("file.separator");

    /**
     * Retourne un hexagone.
     * @param x Coordonée x du point de départ.
     * @param y Coordonnée y du point de départ.
     * @param cote Longueur du côté de l'hexagone.
     * @return un hexagone
     */
    //affichage polygone 
    public static Polygon getPolygon(final int x, final int y, final int cote) {
        int haut = cote / 2;
        int larg = (int) (cote * (Math.sqrt(3) / 2));
        Polygon p = new Polygon();
        p.addPoint(x, y + haut);
        p.addPoint(x + larg, y);
        p.addPoint(x + 2 * larg, y + haut);
        p.addPoint(x + 2 * larg, y + (int) (1.5 * cote));
        p.addPoint(x + larg, y + 2 * cote);
        p.addPoint(x, y + (int) (1.5 * cote));
        return p;
    }

    @Override
    /**
     * Dessine dans le JPanel les éléments du jeu (plateau, unités,
     * déplacement possible, brouillard).
     * @param graph Graphic.
     */
    public void paint(final Graphics graph) {
        Polygon hexagone = getPolygon(0, 0, COTE); // Crée un hexagone
        r = hexagone.getBounds(); // Récupère le plus petit rectangle aux bords de la fenêtre dans lequel
                                  // l'hexagone peut s'inscrire
        graph.setColor(Color.BLACK);
        super.paint(graph);
        g2d = (Graphics2D) graph;
        this.graph = graph;
        bim = null;
        for (int l = 0; l < Jeu.MAPLIGNE; l = l + 2) { // Remarquer le "+2" car la grille est constituées de 2 sous
                                                      // grilles (les lignes impaires sont décallées)
            for (int c = 0; c < Jeu.MAPCOLONNE; c++) {
                Polygon poly = getPolygon(c * r.width, (int) (l * COTE * 1.5), COTE);

                try {
                    switch (Jeu.getMap()[l][c].getType()) {
                   /* case 10:
                        bim = ImageIO.read(new File("images" + separateur + "grass01.png"));
                        break;
                    case 11:
                        bim = ImageIO.read(new File("images" + separateur + "tree.png"));
                        break;
                    case 12:
                        bim = ImageIO.read(new File("images" + separateur + "hut.png"));
                        break;
                    case 13:
                        bim = ImageIO.read(new File("images" + separateur + "water00.png"));
                        break;
                    case 14:
                        bim = ImageIO.read(new File("images" + separateur + "earth.png"));
                        break;
                    case 15:
                        bim = ImageIO.read(new File("images" + separateur + "water00.png"));
                        break;
                    case 16:
                        bim = ImageIO.read(new File("images" + separateur + "tree.png"));*/
                    case 10:
                        bim = ImageIO.read(new File("images/plaine3.png"));
                        break;
                    case 11:
                        bim = ImageIO.read(new File("images/foret3.png"));
                        break;
                    case 12:
                        bim = ImageIO.read(new File("images/village3.png"));
                        break;
                    case 13:
                        bim = ImageIO.read(new File("images/riviere3.png"));
                        break;
                    case 14:
                        bim = ImageIO.read(new File("images/motagne3.png"));
                        break;
                    case 15:
                        bim = ImageIO.read(new File("images/mer3.png"));
                        break;
                    case 16:
                        bim = ImageIO.read(new File("images/desert3.png"));
                        break;
                    default:
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                g2d.setColor(new Color(53, 196, 39));
                g2d.draw(poly);
                g2d.drawImage(bim, c * r.width, (int) (l * COTE * 1.5), this);
                
                // Ajout de l'affichage des points de mouvement
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                String mvtPoints = String.valueOf(Jeu.getMap()[l][c].getCoutDeDeplacement());
                int textX = c * r.width + r.width/2 - 5;
                int textY = (int)(l * COTE * 1.5) + COTE;
                g2d.drawString(mvtPoints, textX, textY);
            }
        }
        for (int l = 1; l < Jeu.MAPLIGNE; l = l + 2) {
            for (int c = 0; c < Jeu.MAPCOLONNE; c++) {
                Polygon poly = getPolygon(c * r.width + r.width / 2, (int) (l * COTE * 1.5 + 0.5), COTE);
                try {
                    switch (Jeu.getMap()[l][c].getType()) {
                   /* case 10:
                        bim = ImageIO.read(new File("images" + separateur + "grass01.png"));
                        break;
                    case 11:
                        bim = ImageIO.read(new File("images" + separateur + "tree.png"));
                        break;
                    case 12:
                        bim = ImageIO.read(new File("images" + separateur + "hut.png"));
                        break;
                    case 13:
                        bim = ImageIO.read(new File("images" + separateur + "water00.png"));
                        break;
                    case 14:
                        bim = ImageIO.read(new File("images" + separateur + "earth.png"));
                        break;
                    case 15:
                        bim = ImageIO.read(new File("images" + separateur + "water00.png"));
                        break;
                    case 16:
                        bim = ImageIO.read(new File("images" + separateur + "tree.png"));*/
                    case 10:
                        bim = ImageIO.read(new File("images/plaine3.png"));
                        break;
                    case 11:
                        bim = ImageIO.read(new File("images/foret3.png"));
                        break;
                    case 12:
                        bim = ImageIO.read(new File("images/village3.png"));
                        break;
                    case 13:
                        bim = ImageIO.read(new File("images/riviere3.png"));
                        break;
                    case 14:
                        bim = ImageIO.read(new File("images/motagne3.png"));
                        break;
                    case 15:
                        bim = ImageIO.read(new File("images/mer3.png"));
                        break;
                    case 16:
                        bim = ImageIO.read(new File("images/desert3.png"));
                        break;
                    default:
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                g2d.setColor(new Color(53, 196, 39));
                g2d.draw(poly);
                g2d.drawImage(bim, c * r.width + r.width / 2, (int) (l * COTE * 1.5 + 0.5), this);
                
                // Ajout de l'affichage des points de mouvement pour les lignes impaires
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                String mvtPoints = String.valueOf(Jeu.getMap()[l][c].getCoutDeDeplacement());
                int textX = c * r.width + r.width - 5;
                int textY = (int)(l * COTE * 1.5) + COTE;
                g2d.drawString(mvtPoints, textX, textY);
            }
        }
        for (ArrayList<Integer> listeUnite : Jeu.getInfoUnite()) {
            try {
                switch (listeUnite.get(1)) {
                case 1:
                    bim = ImageIO.read(new File("images/guerrier" + listeUnite.get(0) + ".png"));
                    break;
                case 2:
                    bim = ImageIO.read(new File("images/mage" + listeUnite.get(0) + ".png"));
                    break;
                case 3:
                    bim = ImageIO.read(new File("images/archer" + listeUnite.get(0) + ".png"));
                    break;
                case 4:
                    bim = ImageIO.read(new File("images/pretre" + listeUnite.get(0) + ".png"));
                    break;
                case 5:
                    bim = ImageIO.read(new File("images/chevalier" + listeUnite.get(0) + ".png"));
                    break;
                default:
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bim != null) {
                g2d = (Graphics2D) graph;
                
                // Draw the unit
                if (listeUnite.get(2) % 2 == 0) {
                    if (listeUnite.get(1) == 2) {
                        g2d.drawImage(bim, listeUnite.get(3) * r.width + 5, (int) (listeUnite.get(2) * COTE * 1.5),
                                this);
                    } else {
                        g2d.drawImage(bim, listeUnite.get(3) * r.width + 5, (int) (listeUnite.get(2) * COTE * 1.5) + 7,
                                this);
                    }
                } else {
                    if (listeUnite.get(1) == 2) {
                        g2d.drawImage(bim, listeUnite.get(3) * r.width + r.width / 2 + 5,
                                (int) (listeUnite.get(2) * COTE * 1.5 + 0.5), this);
                    } else {
                        g2d.drawImage(bim, listeUnite.get(3) * r.width + r.width / 2 + 5,
                                (int) (listeUnite.get(2) * COTE * 1.5 + 0.5) + 7, this);
                    }
                }

                // Add highlight effect for current team's units
                if (listeUnite.get(0) == Jeu.getTurn()) {
                    Polygon highlightPoly;
                    if (listeUnite.get(2) % 2 == 0) {
                        highlightPoly = getPolygon(listeUnite.get(3) * r.width,
                                (int) (listeUnite.get(2) * COTE * 1.5), COTE);
                    } else {
                        highlightPoly = getPolygon(listeUnite.get(3) * r.width + r.width / 2,
                                (int) (listeUnite.get(2) * COTE * 1.5 + 0.5), COTE);
                    }
                    
                    // Save the current composite
                    AlphaComposite oldComposite = (AlphaComposite) g2d.getComposite();
                    
                    // Set semi-transparent yellow for highlight
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    g2d.setColor(Color.YELLOW);
                    g2d.fill(highlightPoly);
                    
                    // Restore the original composite
                    g2d.setComposite(oldComposite);
                }
                
                repaint();
            }
        }
        Polygon poly = null;

        // Draw fog of war
        if (Jeu.getBrouillard() != null && !Jeu.getBrouillard().isEmpty()) {
            // Save the current composite
            AlphaComposite oldComposite = (AlphaComposite) g2d.getComposite();
            
            // Set semi-transparent dark overlay for fog
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2d.setColor(new Color(0, 0, 0));
            
            for (ArrayList<Integer> brouillard : Jeu.getBrouillard()) {
                Polygon fogPoly;
                if (brouillard.get(0) % 2 == 0) {
                    fogPoly = getPolygon(brouillard.get(1) * r.width,
                            (int) (brouillard.get(0) * COTE * 1.5), COTE);
                } else {
                    fogPoly = getPolygon(brouillard.get(1) * r.width + r.width / 2,
                            (int) (brouillard.get(0) * COTE * 1.5 + 0.5), COTE);
                }
                g2d.fill(fogPoly);
            }
            
            // Restore the original composite
            g2d.setComposite(oldComposite);
        }

        if (Jeu.getDeplacementPossible() != null) {
            for (ArrayList<Integer> deplacementPossible : Jeu.getDeplacementPossible()) {
                if (deplacementPossible.get(0) % 2 == 0) {
                    poly = getPolygon(deplacementPossible.get(1) * r.width,
                            (int) (deplacementPossible.get(0) * COTE * 1.5), COTE);
                } else {
                    poly = getPolygon(deplacementPossible.get(1) * r.width + r.width / 2,
                            (int) (deplacementPossible.get(0) * COTE * 1.5 + 0.5), COTE);
                }
                if (poly != null) {
                    g2d.setColor(Color.ORANGE);
                    g2d.draw(poly);
                }
            }
        }
        if (Jeu.getActionPossible() != null) {
            for (ArrayList<Object> actionPossible : Jeu.getActionPossible()) {
                if (((int) actionPossible.get(0)) % 2 == 0) {
                    poly = getPolygon(((int) actionPossible.get(1)) * r.width,
                            (int) (((int) actionPossible.get(0)) * COTE * 1.5), COTE);
                } else {
                    poly = getPolygon(((int) actionPossible.get(1)) * r.width + r.width / 2,
                            (int) (((int) actionPossible.get(0)) * COTE * 1.5 + 0.5), COTE);
                }
                if (poly != null) {
                    if ((String) actionPossible.get(2) == "allie") {
                        g2d.setColor(Color.GREEN);
                        g2d.draw(poly);
                    } else {
                        g2d.setColor(Color.RED);
                        g2d.draw(poly);
                    }
                }
            }
        }
    }
}
