package vue;

import controleur.Jeu;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Affplateau est la classe fille de JPanel, elle affiche le plateau de jeu et
 * son contenu.
 * 
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
     * Variables pour le panoramique de la carte
     */
    private int decalageX = 0; // Décalage horizontal pour le panoramique
    private int decalageY = 0; // Décalage vertical pour le panoramique
    private int minDecalageX = 0; // Limite minimale du décalage horizontal
    private int maxDecalageX = 0; // Limite maximale du décalage horizontal
    private int minDecalageY = 0; // Limite minimale du décalage vertical
    private int maxDecalageY = 0; // Limite maximale du décalage vertical
    private Point pointDepart; // Point de départ pour le panoramique
    private boolean enDeplacement = false; // Indique si la carte est en cours de déplacement
    private int largeurTotale = 0; // Largeur totale de la carte
    private int hauteurTotale = 0; // Hauteur totale de la carte

    /**
     * Constructeur de la classe Affplateau.
     * Initialise les écouteurs de souris pour le panoramique.
     */
    public Affplateau() {
        // Ajouter les écouteurs de souris pour le panoramique
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Vérifier si c'est un clic gauche
                if (e.getButton() == MouseEvent.BUTTON1) {
                    pointDepart = e.getPoint();
                    enDeplacement = true;
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                // Fin du panoramique
                if (e.getButton() == MouseEvent.BUTTON1) {
                    enDeplacement = false;
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });
        
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Déplacement de la carte si le panoramique est actif
                if (enDeplacement) {
                    int dx = e.getX() - pointDepart.x;
                    int dy = e.getY() - pointDepart.y;
                    
                    // Calcul des nouveaux offsets en respectant les limites
                    int nouveauDecalageX = decalageX + dx;
                    int nouveauDecalageY = decalageY + dy;
                    
                    // Mise à jour des limites du panoramique
                    mettreAJourLimitesPanoramique();
                    
                    // Application des limites
                    if (nouveauDecalageX < minDecalageX) nouveauDecalageX = minDecalageX;
                    if (nouveauDecalageX > maxDecalageX) nouveauDecalageX = maxDecalageX;
                    if (nouveauDecalageY < minDecalageY) nouveauDecalageY = minDecalageY;
                    if (nouveauDecalageY > maxDecalageY) nouveauDecalageY = maxDecalageY;
                    
                    decalageX = nouveauDecalageX;
                    decalageY = nouveauDecalageY;
                    
                    pointDepart = e.getPoint();
                    repaint();
                }
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                // Non utilisé
            }
        });
    }
    
    /**
     * Met à jour les limites du panoramique en fonction de la taille de la carte et de la fenêtre.
     * Cette méthode calcule les valeurs minimales et maximales pour les décalages X et Y
     * afin d'empêcher l'utilisateur de naviguer en dehors des limites de la carte.
     */
    private void mettreAJourLimitesPanoramique() {
        // Calculer la taille totale de la carte
        Polygon hexagone = getPolygon(0, 0, COTE);
        Rectangle r = hexagone.getBounds();
        
        // Calculer la largeur et la hauteur totales de la carte
        largeurTotale = Jeu.MAPCOLONNE * r.width + r.width / 2;
        hauteurTotale = (int) (Jeu.MAPLIGNE * COTE * 1.5 + COTE);
        
        // Définir les limites du panoramique
        // La limite minimale (valeur négative la plus grande en valeur absolue) permet de déplacer la carte vers la droite/bas
        // La limite maximale (0) empêche de déplacer la carte trop loin vers la gauche/haut
        minDecalageX = Math.min(0, getWidth() - largeurTotale);
        minDecalageY = Math.min(0, getHeight() - hauteurTotale);
        maxDecalageX = 0;
        maxDecalageY = 0;
    }
    
    /**
     * Retourne le décalage horizontal actuel du panoramique.
     * @return le décalage horizontal
     */
    public int getDecalageX() {
        return decalageX;
    }
    
    /**
     * Retourne le décalage vertical actuel du panoramique.
     * @return le décalage vertical
     */
    public int getDecalageY() {
        return decalageY;
    }
    
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
        
        // Mise à jour des limites du panoramique avant de dessiner
        mettreAJourLimitesPanoramique();
        
        // Application de la translation pour le panoramique
        g2d.translate(decalageX, decalageY);
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
            
            // Charger l'image du brouillard
            BufferedImage brouillardImg = null;
            try {
                brouillardImg = ImageIO.read(new File("images/brouillard.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            
            for (ArrayList<Integer> brouillard : Jeu.getBrouillard()) {
                Polygon fogPoly;
                int drawX, drawY;
                if (brouillard.get(0) % 2 == 0) {
                    drawX = brouillard.get(1) * r.width;
                    drawY = (int) (brouillard.get(0) * COTE * 1.5);
                    fogPoly = getPolygon(drawX, drawY, COTE);
                } else {
                    drawX = brouillard.get(1) * r.width + r.width / 2;
                    drawY = (int) (brouillard.get(0) * COTE * 1.5 + 0.5);
                    fogPoly = getPolygon(drawX, drawY, COTE);
                }
                if (brouillardImg != null) {
                    g2d.setClip(fogPoly);
                    g2d.drawImage(brouillardImg, drawX, drawY, r.width, r.height, null);
                    g2d.setClip(null);
                } else {
                    g2d.setColor(new Color(80, 80, 80));
                    g2d.fill(fogPoly);
                }
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
