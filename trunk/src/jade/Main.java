package jade;

import jade.ui.Accueil;
import javax.swing.UIManager;
import org.pushingpixels.substance.api.skin.SubstanceMarinerLookAndFeel;

/**
 * Classe Lancement du jeu 
 */
public class Main {

    private static Accueil fenAccueil;
    
    /**
     * Lance la fenêtre principale du jeu
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(new SubstanceMarinerLookAndFeel()); //new SubstanceGraphiteLookAndFeel()
                    fenAccueil = new Accueil();
                    fenAccueil.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



    }

    /**
     * Retourne l'instance de la fenêtre d'accueil
     * @return Fenêtre principale
     */
    public static Accueil getFenAccueil() {
        return fenAccueil;
    }

    

}
