package modele;

import controleur.Jeu;


public class Riviere extends Hexagone {
    private static final int POINTDEPLACEMENT = 2;
    private static final double BONUSDEFENSE = 0.1;
    public Riviere(final int x, final int y) {
        super(Jeu.RIVIERE, BONUSDEFENSE, POINTDEPLACEMENT, x, y);
    }

}
