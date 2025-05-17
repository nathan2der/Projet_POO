package modele;

import controleur.Jeu;

/**
 * Infanterie est la classe représentant une unité d'Infanterie. Ses caractéristiques sont :
 * <ul>
 * <li>Attaque : 5</li>
 * <li>Défense : 3</li>
 * <li>PV : 28</li>
 * <li>Déplacement : 6</li>
 * <li>Vision : 4</li>
 * <li>Portée : 1</li>
 * </ul>
 * @author Nathan
 * @see Unite
 */
public class Infanterie extends Unite {
    /**
     * Valeur de l'attaque de l'Infanterie.
     */
    private static final int ATTAQUE = 5;
    /**
     * Valeur de la défense de l'Infanterie.
     */
    private static final int DEFENSE = 3;
    /**
     * Valeur des PV de l'Infanterie.
     */
    private static final int PV = 28;
    /**
     * Valeur du déplacement de l'Infanterie.
     */
    private static final int DEPLACEMENT = 6;
    /**
     * Valeur de la vision de l'Infanterie.
     */
    private static final int VISION = 4;
    /**
     * Valeur de la portée de l'Infanterie.
     */
    private static final int PORTEE = 1;

    /**
     * Constructeur Infanterie avec ses constantes.
     * @param equipe Le numéro du joueur associé à cette unité.
     * @param x Le numéro de ligne de l'unité.
     * @param y Le numéro de colonne de l'unité.
     */
    public Infanterie(final int equipe, final int x, final int y) {
        super(Jeu.INFANTERIE, ATTAQUE, DEFENSE, PV, DEPLACEMENT, VISION, PORTEE, x, y, equipe);
    }
}
