package jade.model;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * QuestionEntité de type Série
 */
public class QuestionSerie extends QuestionEntite {

    public QuestionSerie() {
    }

    /**
     * Initialise une entité QuestionSérie à partir d'un ResultSet
     * @param ligne
     * @throws SQLException
     */
    public void init(ResultSet ligne) throws SQLException
    {
        this.setId(ligne.getInt("ID"));
        this.setNbJoue(ligne.getInt("NB_JOUE"));
        this.setNonQuestion(ligne.getInt("NON_QUESTION"));
        this.setNonRep(ligne.getInt("NON_REP"));
        this.setOuiQuestion(ligne.getInt("OUI_QUESTION"));
        this.setOuiRep(ligne.getInt("OUI_REP"));
        this.setParent(ligne.getInt("PARENT"));
        this.setQuestion(ligne.getString("QUESTION"));
    }


}
