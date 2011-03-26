/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jade.metier;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Permet de récupérer le controleur associé à notre entité
 */
public  class ControleurFactory {

    private static Controleur cSport;
    private static Controleur cSeries;
    static {
        try {
            cSport  = new Controleur("SPORTS", "QUESTIONS_SPORT", Class.forName("jade.model.Sport"), Class.forName("jade.model.QuestionSport"));
            cSeries = new Controleur("SERIES", "QUESTIONS_SERIE", Class.forName("jade.model.Serie"), Class.forName("jade.model.QuestionSerie"));
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControleurFactory.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    
    public enum TypeEntite { SPORT, SERIE };

    public static Controleur create(TypeEntite type)
    {
        Controleur out = null;
        
        switch(type)
        {
            case SPORT:
                out = cSport;
            break;
            case SERIE:
                out = cSeries;
            break;
        }
        
        return out;
    }
}
