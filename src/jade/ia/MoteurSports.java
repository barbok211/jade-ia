package jade.ia;

import jade.metier.ControleurFactory.TypeEntite;


/**
 * Moteur spécialisé pour la recherche des sports
 */
public class MoteurSports extends Moteur {

    /**
     * Nom de la table liée à cette entité
     * @return Nom de la table
     */
    @Override
    public TypeEntite getTypeEntite() {
        return TypeEntite.SPORT;
    }

    
}
