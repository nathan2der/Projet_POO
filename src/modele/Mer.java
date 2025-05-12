package modele;

import controleur.Jeu;

public class Mer extends Hexagone {

    private static final int POINTDEPLACEMENT = 20;
    private static final double BONUSDEFENSE = 0;
    public Mer(final int x, final int y) {
        super(Jeu.MER, BONUSDEFENSE, POINTDEPLACEMENT, x, y);
    }

}
