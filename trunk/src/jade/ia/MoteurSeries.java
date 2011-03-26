
package jade.ia;

import jade.metier.ControleurFactory.TypeEntite;

/**
 * Moteur spécialisé pour la recherche des séries
 */
public class MoteurSeries extends Moteur {

    /**
     * Nom de la table liée à cette entité
     * @return Nom de la table
     */
    @Override
    public TypeEntite getTypeEntite() {
        return TypeEntite.SERIE;
    }
}
