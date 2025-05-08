
import java.util.ArrayList;

public class Plateau {
    private ArrayList<Case> plateau;

    public Plateau() {
        this.plateau = new ArrayList<>();
    }

    public void ajouterCase(Case c) {
        plateau.add(c);
    }

    public Case getCase(int index) {
        if (index >= 0 && index < plateau.size()) {
            return plateau.get(index);
        }
        return null;
    }

    public void afficherPlateau() {
        for (int i = 0; i < plateau.size(); i++) {
            System.out.println("Case " + i + ": " + plateau.get(i));
        }
    }
}

