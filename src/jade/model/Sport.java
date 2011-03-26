package jade.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Entité de type Sport
 */
public class Sport extends Entite {

    public Sport() {}

    /**
     * Initialise un objet Sport à partir d'un ResultSet
     * @param ligne
     * @throws SQLException
     */
    public void init (ResultSet ligne) throws SQLException
    {
        this.setId(ligne.getInt("ID"));
        this.setNom(ligne.getString("NOM"));
        this.setNbJoues(ligne.getInt("NB_JOUE"));
        this.setNbTrouves(ligne.getInt("NB_TROUVE"));
    }

    
    
}
