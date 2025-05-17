package modele;

import controleur.Jeu;

/**
 * Forteresse est la classe représentant un type d'Hexagone. Ses caractéristiques sont :
 * <ul>
 * <li>Points de déplacement : 1</li>
 * <li>Bonus de défense : 60 %</li>
 * </ul>
 * @author Nathan
 * @see Hexagone
 */
public class Forteresse extends Hexagone {
    /**
     * Valeur des points de déplacement de la Forteresse.
     */
    private static final int POINTDEPLACEMENT = 1;
    /**
     * Valeur du bonus de défense de la Forteresse.
     */
    private static final double BONUSDEFENSE = 0.6;

    /**
     * Constructeur Forteresse.
     * @param x Numéro de ligne sur le plateau.
     * @param y Numéro de colonne sur le plateau.
     */
    public Forteresse(final int x, final int y) {
        super(Jeu.FORTERESSE, BONUSDEFENSE, POINTDEPLACEMENT, x, y);
    }
} 