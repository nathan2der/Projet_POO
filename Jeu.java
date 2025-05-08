import java.util.List;

public class Jeu {
    private Plateau plateau;
    private List<Joueur> joueurs;
    private TourDeJeu tourEnCours;

    public Jeu(Plateau plateau, List<Joueur> joueurs) {
        this.plateau = plateau;
        this.joueurs = joueurs;
        this.tourEnCours = new TourDeJeu();
    }

    public void demarrerPartie() {
        System.out.println("La partie commence !");
        tourEnCours.initierTour();
    }

    public void finPartie() {
        System.out.println("La partie est terminée !");
    }

    public void passerTour() {
        tourEnCours.finTour();
        tourEnCours.initierTour();
    }
}

