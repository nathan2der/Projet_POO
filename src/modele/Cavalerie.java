package modele;

import controleur.Jeu;

/**
 * Cavalerie est la classe représentant une unité de Cavalerie. Ses caractéristiques sont :
 * <ul>
 * <li>Attaque : 8</li>
 * <li>Défense : 4</li>
 * <li>PV : 38</li>
 * <li>Déplacement : 8</li>
 * <li>Vision : 6</li>
 * <li>Portée : 1</li>
 * </ul>
 * @author Nathan
 * @see Unite
 */
public class Cavalerie extends Unite {
    /**
     * Valeur de l'attaque de la Cavalerie.
     */
    private static final int ATTAQUE = 8;
    /**
     * Valeur de la défense de la Cavalerie.
     */
    private static final int DEFENSE = 4;
    /**
     * Valeur des PV de la Cavalerie.
     */
    private static final int PV = 38;
    /**
     * Valeur du déplacement de la Cavalerie.
     */
    private static final int DEPLACEMENT = 8;
    /**
     * Valeur de la vision de la Cavalerie.
     */
    private static final int VISION = 6;
    /**
     * Valeur de la portée de la Cavalerie.
     */
    private static final int PORTEE = 1;

    /**
     * Constructeur Cavalerie avec ses constantes.
     * @param equipe Le numéro du joueur associé à cette unité.
     * @param x Le numéro de ligne de l'unité.
     * @param y Le numéro de colonne de l'unité.
     */
    public Cavalerie(final int equipe, final int x, final int y) {
        super(Jeu.CAVALERIE, ATTAQUE, DEFENSE, PV, DEPLACEMENT, VISION, PORTEE, x, y, equipe);
    }
}
