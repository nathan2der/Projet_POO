public class Jeu {
    private Plateau plateau;
    private Joueur[] joueurs;
    private TourDeJeu tourEnCours;

    public void demarrerPartie() {}
    public void finPartie() {}
    public void passerTour() {}
}

public class IA {
    private Unite[] unites;

    public Unite calculerDeplacement() {
        return null;
    }

    public Unite calculerAttaque() {
        return null;
    }
}

public class Plateau {
    private Case[][] cases;

    public void afficherPlateau() {}

    public Case getCase(int x, int y) {
        return null;
    }
}

public class Case {
    private Terrain terrain;
    private Unite unite;

    public void placerUnite(Unite u) {
        this.unite = u;
    }

    public void retirerUnite() {
        this.unite = null;
    }
}

public class Terrain {
    private String type;
    private int bonusDeplacement;
    private int bonusAttaque;

    public int getBonusAttaque() {
        return bonusAttaque;
    }

    public int getBonusDeplacement() {
        return bonusDeplacement;
    }
}

public abstract class Unite {
    protected String nom;
    protected int pointsDeVie;
    protected int attaque;
    protected int defense;
    protected int deplacement;
    protected int champDeVision;

    public void deplacer(int x, int y) {}
    public void attaquer(Unite cible) {}
    public void recevoirDegats(int d) {
        pointsDeVie = Math.max(0, pointsDeVie - d);
    }
}

public class Infanterie extends Unite {}
public class InfanterieLourde extends Unite {}
public class Cavalerie extends Unite {}
public class Archer extends Unite {}
public class Mage extends Unite {}

public class Joueur {
    private String nom;
    private Unite[] unites;

    public Unite choisirUnite() {
        return null;
    }

    public void terminerTour() {}
}

public class TourDeJeu {
    private int numeroTour;

    public void initierTour() {}
    public void finTour() {}
}

