public abstract class Unite {
    protected String nom;
    protected int pointsDeVie;
    protected int attaque;
    protected int defense;
    protected int deplacement;
    protected int champDeVision;

    public Unite(String nom, int pointsDeVie, int attaque, int defense, int deplacement, int champDeVision) {
        this.nom = nom;
        this.pointsDeVie = pointsDeVie;
        this.attaque = attaque;
        this.defense = defense;
        this.deplacement = deplacement;
        this.champDeVision = champDeVision;
    }

    public void deplacer(int x, int y) {
        System.out.println(nom + " se déplace vers (" + x + ", " + y + ")");
    }

    public void attaquer(Unite cible) {
        System.out.println(nom + " attaque " + cible.getNom());
        int degats = Math.max(0, this.attaque - cible.getDefense());
        cible.recevoirDegats(degats);
    }

    public void recevoirDegats(int d) {
        this.pointsDeVie -= d;
        System.out.println(nom + " reçoit " + d + " dégâts. PV restants : " + this.pointsDeVie);
    }

    // Getters

