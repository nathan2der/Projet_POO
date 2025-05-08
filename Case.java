
public class Case {
    private Terrain terrain;
    private Unite unite;

    public Case(Terrain terrain) {
        this.terrain = terrain;
        this.unite = null;
    }

    public void placerUnite(Unite u) {
        this.unite = u;
    }

    public void retirerUnite() {
        this.unite = null;
    }

    public Unite getUnite() {
        return unite;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    @Override
    public String toString() {
        return "Terrain: " + terrain.getType() + ", Unite: " + (unite != null ? unite.getNom() : "aucune");
    }
}

