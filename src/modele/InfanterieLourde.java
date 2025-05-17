package modele;

import controleur.Jeu;

/**
 * InfanterieLourde est la classe représentant une unité d'Infanterie Lourde. Ses caractéristiques sont :
 * <ul>
 * <li>Attaque : 10</li>
 * <li>Défense : 10</li>
 * <li>PV : 38</li>
 * <li>Déplacement : 4</li>
 * <li>Vision : 3</li>
 * <li>Portée : 1</li>
 * </ul>
 * @author Nathan
 * @see Unite
 */
public class InfanterieLourde extends Unite {
    /**
     * Valeur de l'attaque de l'Infanterie Lourde.
     */
    private static final int ATTAQUE = 10;
    /**
     * Valeur de la défense de l'Infanterie Lourde.
     */
    private static final int DEFENSE = 10;
    /**
     * Valeur des PV de l'Infanterie Lourde.
     */
    private static final int PV = 38;
    /**
     * Valeur du déplacement de l'Infanterie Lourde.
     */
    private static final int DEPLACEMENT = 4;
    /**
     * Valeur de la vision de l'Infanterie Lourde.
     */
    private static final int VISION = 3;
    /**
     * Valeur de la portée de l'Infanterie Lourde.
     */
    private static final int PORTEE = 1;

    /**
     * Constructeur Infanterie Lourde avec ses constantes.
     * @param equipe Le numéro du joueur associé à cette unité.
     * @param x Le numéro de ligne de l'unité.
     * @param y Le numéro de colonne de l'unité.
     */
    public InfanterieLourde(final int equipe, final int x, final int y) {
        super(Jeu.INFANTERIE_LOURDE, ATTAQUE, DEFENSE, PV, DEPLACEMENT, VISION, PORTEE, x, y, equipe);
    }
} 