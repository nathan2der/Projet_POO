public class Terrain{
    protected String type;
    protected int bonusDeplacement;
    protected int bonusAttaque;

    public Terrain(String type, int bonusDeplacement, int bonusAttaque) {
        this.type = type;
        this.bonusDeplacement = bonusDeplacement;
        this.bonusAttaque = bonusAttaque;
    }
}
