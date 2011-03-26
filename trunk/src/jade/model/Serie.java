package jade.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Entité de type Série
 */
public class Serie extends Entite {

    public Serie() {}

    /**
     * Initialise un objet Série à partir d'un ResultSet
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
