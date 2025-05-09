package wargame;

import wargame.ui.MainMenu;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });
    }
}