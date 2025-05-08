public class Terrain {
    private String type;
    private int bonusDeplacement;
    private int bonusAttaque;
    private int bonusDefense;

    public Terrain(String type, int bonusDeplacement, int bonusAttaque, int bonusDefense) {
        this.type = type;
        this.bonusDeplacement = bonusDeplacement;
        this.bonusAttaque = bonusAttaque;
        this.bonusDefense = bonusDefense;
    }

    public String getType() {
        return type;
    }

    public int getBonusDeplacement() {
        return bonusDeplacement;
    }

    public int getBonusAttaque() {
        return bonusAttaque;
    }

    public int getBonusDefense() {
        return bonusDefense;
    }
}

