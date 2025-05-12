package modele;

import controleur.Jeu;
public class Archer extends Unite {
    private static final int ATTAQUE = 14;
    private static final int DEFENSE = 6;
    private static final int PV = 38;
    private static final int DEPLACEMENT = 4;
    private static final int VISION = 4;
    private static final int PORTEE = 3;
    public Archer(final int equipe, final int x, final int y) {
        super(Jeu.ARCHER, ATTAQUE, DEFENSE, PV, DEPLACEMENT, VISION, PORTEE, x, y, equipe);
    }

}
