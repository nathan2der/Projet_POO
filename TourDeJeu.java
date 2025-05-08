public class TourDeJeu {
    private int numeroTour;

    public TourDeJeu() {
        this.numeroTour = 1;
    }

    public void initierTour() {
        System.out.println("Début du tour " + numeroTour);
    }

    public void finTour() {
        System.out.println("Fin du tour " + numeroTour);
        numeroTour++;
    }

    public int getNumeroTour() {
        return numeroTour;
    }
}

