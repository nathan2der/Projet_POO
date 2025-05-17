package modele;

import controleur.Jeu;

/**
 * Colline est la classe représentant un type d'Hexagone. Ses caractéristiques sont :
 * <ul>
 * <li>Points de déplacement : 2</li>
 * <li>Bonus de défense : 40 %</li>
 * </ul>
 * @author Nathan
 * @see Hexagone
 */
public class Colline extends Hexagone {
    /**
     * Valeur des points de déplacement de la Colline.
     */
    private static final int POINTDEPLACEMENT = 2;
    /**
     * Valeur du bonus de défense de la Colline.
     */
    private static final double BONUSDEFENSE = 0.4;

    /**
     * Constructeur Colline.
     * @param x Numéro de ligne sur le plateau.
     * @param y Numéro de colonne sur le plateau.
     */
    public Colline(final int x, final int y) {
        super(Jeu.COLLINE, BONUSDEFENSE, POINTDEPLACEMENT, x, y);
    }
} 