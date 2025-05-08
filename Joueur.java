import java.util.List;

public class Joueur {
    private String nom;
    private List<Unite> unites;

    public Joueur(String nom, List<Unite> unites) {
        this.nom = nom;
        this.unites = unites;
    }

    public void choisirUnite(Unite u) {
        System.out.println(nom + " a choisi " + u.getNom());
    }

    public void terminerTour() {
        System.out.println(nom + " termine son tour.");
    }

    public String getNom() {
        return nom;
    }

    public List<Unite> getUnites() {
        return unites;
    }
}

