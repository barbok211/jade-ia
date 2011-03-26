/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jade.metier;

import Utils.JadeException;
import jade.ia.Moteur.Reponse;
import jade.model.Container;
import jade.model.Entite;
import jade.model.QuestionEntite;
import jade.sql.ConnexionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Accès aux information enregistrées en base de donnée
 */
public class Controleur {

    private String table;           // Table Entité
    private String tableQuestions;  // Table Question
    private Class classEntite;      // Classe Entité
    private Class classQuestion;    // Classe


    public Controleur(String table, String tableQuestions, Class entite, Class question)
    {
        this.table = table;
        this.tableQuestions = tableQuestions;

        this.classEntite = entite;
        this.classQuestion = question;
        
    }

    /**
     * Récupère la question racine dans la BDD
     * @return Première question à poser
     */
    public QuestionEntite fetchFirstQuestion()
    {
        QuestionEntite qe = null;

        try {
            Statement stmnt = ConnexionManager.getCurrentConnexion().createStatement();

            ResultSet res = stmnt.executeQuery("SELECT * FROM "+tableQuestions+" WHERE PARENT is NULL");
            if (!res.next()) throw new JadeException("Impossible de récupérer la question racine");

            qe = (QuestionEntite) classQuestion.newInstance();
            qe.init(res);
            
            stmnt.close();
            
        } catch (Exception ex) {
            Logger.getLogger(Controleur.class.getName()).log(Level.SEVERE, null, ex);
        }

        return qe;

    }

    /**
     * Récupère une question à partir de son ID
     * @param ouiQuestion ID de la question à récupérer
     * @param augmenterStats Si vrai, incrémente la stastistique d'utilisation de cette question
     * @return Object Question rempli
     */
    public QuestionEntite fetchQuestionById(int ouiQuestion, boolean augmenterStats) {
        QuestionEntite qe = null;

        try {

            PreparedStatement stmnt = ConnexionManager.getCurrentConnexion().prepareStatement(
                    "SELECT * FROM "+tableQuestions+" WHERE id = ?"
             );
            stmnt.setInt(1, ouiQuestion);

            ResultSet res = stmnt.executeQuery();
            if (!res.next()) throw new JadeException("Impossible de trouver la question numéro "+ouiQuestion);

            qe = (QuestionEntite) classQuestion.newInstance();
            qe.init(res);
            stmnt.close();

            if (augmenterStats)
            {
                stmnt = ConnexionManager.getCurrentConnexion().prepareStatement(
                    "UPDATE "+tableQuestions+" SET NB_JOUE=NB_JOUE+1 WHERE ID = ?"
                 );
                stmnt.setInt(1, ouiQuestion);
                stmnt.executeUpdate();
                stmnt.close();
            }

        } catch (Exception ex) {
            Logger.getLogger(Controleur.class.getName()).log(Level.SEVERE, null, ex);
        }

        return qe;
    }

    /**
     * Récupère une entité à partir de son ID
     * @param ouiRep ID de l'entité à récupérer
     * @param augmenterStats Si vrai, incrémente la stastistique d'utilisation de cette question
     * @return entité
     */
    public Entite fetchEntiteById(int ouiRep, boolean augmenterStats) {
        Entite e = null;

        try {
            PreparedStatement stmnt = ConnexionManager.getCurrentConnexion().prepareStatement(
                    "SELECT * FROM "+table+" WHERE id = ?"
             );
            stmnt.setInt(1, ouiRep);

            ResultSet res = stmnt.executeQuery();
            if (!res.next()) throw new JadeException("Impossible de trouver l'entité numéro "+ouiRep);

            e = (Entite) classEntite.newInstance();
            e.init(res);
            stmnt.close();

            if (augmenterStats)
            {
                stmnt = ConnexionManager.getCurrentConnexion().prepareStatement(
                    "UPDATE "+table+" SET NB_JOUE=NB_JOUE+1 WHERE ID = ?"
                 );
                stmnt.setInt(1, ouiRep);
                stmnt.executeUpdate();
                stmnt.close();
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(Controleur.class.getName()).log(Level.SEVERE, null, ex);
        }

        return e;
    }

    /**
     * Récupère la réponse privilégiée pour une réponse donnée, en fonction des statistiques des précédentes parties
     * @param derniereQuestion : Id de la question
     * @return Container contenant la question ou la proposition à jouer
     */
    public Container fetchPrivilegiedEntity(QuestionEntite derniereQuestion) {

        Container c = new Container();

        // Infos sur la réponse OUI
        int nbOui;
        if (derniereQuestion.getOuiRep()>0)
            nbOui = this.fetchEntiteById(derniereQuestion.getOuiRep(), false).getNbJoues();
        else
            nbOui = this.fetchQuestionById(derniereQuestion.getOuiQuestion(), false).getNbJoue();

        // Infos sur la réponse NON
        int nbNon;
        if (derniereQuestion.getNonRep()>0)
            nbNon = this.fetchEntiteById(derniereQuestion.getNonRep(), false).getNbJoues();
        else
            nbNon = this.fetchQuestionById(derniereQuestion.getNonQuestion(), false).getNbJoue();

        // La réponse OUI gagne
        if (nbOui>nbNon) {
            if (derniereQuestion.getOuiRep()>0) c.setEntity(this.fetchEntiteById(derniereQuestion.getOuiRep(), false));
            else c.setQuestionEntity(this.fetchQuestionById(derniereQuestion.getOuiQuestion(), false));
            c.setDecision(Reponse.OUI);
        }
        // La réponse NON gagne
        else {
            if (derniereQuestion.getNonRep()>0) c.setEntity(this.fetchEntiteById(derniereQuestion.getNonRep(), false));
            else c.setQuestionEntity(this.fetchQuestionById(derniereQuestion.getNonQuestion(), false));
            c.setDecision(Reponse.NON);
        }

        return c;

    }

    /**
     * Augmenter les statistique d'une proposition
     * @param proposition
     */
    public void goodProposition(Entite proposition) {
        try {
            PreparedStatement stmnt = ConnexionManager.getCurrentConnexion().prepareStatement(
                    "UPDATE "+table+" SET NB_TROUVE = NB_TROUVE + 1 WHERE ID = ?"
             );
            stmnt.setInt(1, proposition.getId());

            stmnt.executeUpdate();
            stmnt.close();
            
        }
        catch (Exception ex)
        {
            Logger.getLogger(Controleur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Enregistrer une nouvelle question/réponse dans la base de donnée
     * @param derniereQuestion : Dernière question de l'inférence
     * @param decision : Dernière décision de l'utilisateur (OUI/NON)
     * @param proposition : Proposition qui a été jugée incorrecte
     * @param question : Nouvelle question
     * @param reponse : Nouvelle réponse
     * @param oui : La nouvelle réponse est-elle liée à la réponse OUI de la question ?
     * @return résultat de l'opération
     */
    public boolean formulerNouvelleQuestion(QuestionEntite derniereQuestion, Reponse decision, Entite proposition, String question, String reponse, Boolean oui)  {

        try {

            // Début de la transaction
            ConnexionManager.getCurrentConnexion().setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            // Insérer nouvelle entité
            String sqlNewE = "INSERT INTO "+table+" (NOM, NB_JOUE, NB_TROUVE) VALUES (?, 0, 0)";
            PreparedStatement stmntNewE = ConnexionManager.getCurrentConnexion().prepareCall(sqlNewE);
            stmntNewE.setString(1, reponse);
            stmntNewE.executeUpdate();
            stmntNewE.close();

            // Récupérer ID de l'entité insérer
            int idE = ConnexionManager.getInstance().getLastInsertedId(table);

            // Insérer nouvelle question
            String sqlNewQ = "INSERT INTO "+tableQuestions+" (QUESTION, PARENT, OUI_REP, NON_REP, NB_JOUE) VALUES (?,?,?,?,0)";

            PreparedStatement stmntNewQ = ConnexionManager.getCurrentConnexion().prepareStatement(sqlNewQ);
            stmntNewQ.setString(1, question);
            stmntNewQ.setInt(2, derniereQuestion.getId());
            if (oui)
            {
                stmntNewQ.setInt(3, idE);
                stmntNewQ.setInt(4, proposition.getId());
            } else {
                stmntNewQ.setInt(3, proposition.getId());
                stmntNewQ.setInt(4, idE);
            }
            stmntNewQ.executeUpdate();
            stmntNewQ.close();
            int idQ = ConnexionManager.getInstance().getLastInsertedId(tableQuestions);

            // Mettre à jour question parente
            String sqlUpdate = "UPDATE "+tableQuestions+" SET ";
            if (decision.equals(Reponse.OUI)) {
                sqlUpdate += "OUI_QUESTION = ?, OUI_REP = NULL ";
            } else {
                sqlUpdate += "NON_QUESTION = ?, NON_REP = NULL ";
            }
            sqlUpdate += "WHERE ID = ?";
            PreparedStatement stmntU = ConnexionManager.getCurrentConnexion().prepareStatement(sqlUpdate);
            stmntU.setInt(1, idQ);
            stmntU.setInt(2, derniereQuestion.getId());
            stmntU.executeUpdate();
            stmntU.close();
            
            // Fin de la transaction
            ConnexionManager.getCurrentConnexion().commit();
            
            return true;
        }
        catch (Exception e) {
            try {
                e.printStackTrace();
                ConnexionManager.getCurrentConnexion().rollback();
            } catch (Exception e2) {}
            finally {
                return false;
            }
        }
    }

    public Entite fetchEntiteByNom(String nom) {
        Entite e = null;

        try {
            PreparedStatement stmnt = ConnexionManager.getCurrentConnexion().prepareStatement(
                    "SELECT * FROM "+table+" WHERE NOM = ?"
             );
            stmnt.setString(1, nom);

            ResultSet res = stmnt.executeQuery();
            if (!res.next()) throw new JadeException("Impossible de trouver l'entité nommée "+nom);

            e = (Entite) classEntite.newInstance();
            e.init(res);
            stmnt.close();

        }
        catch (Exception ex)
        {
           ex.printStackTrace();
        }

        return e;
    }

    public QuestionEntite fetchQuestionByEntite(Entite entiteCorrespondante) {

        QuestionEntite qe = null;

        try {

            PreparedStatement stmnt = ConnexionManager.getCurrentConnexion().prepareStatement(
                    "SELECT * FROM "+tableQuestions+" WHERE OUI_REP = ? OR NON_REP = ?"
             );
            stmnt.setInt(1, entiteCorrespondante.getId());
            stmnt.setInt(2, entiteCorrespondante.getId());

            ResultSet res = stmnt.executeQuery();
            if (!res.next()) throw new JadeException("Impossible de trouver la question qui contient l'entite "+entiteCorrespondante.getNom());

            qe = (QuestionEntite) classQuestion.newInstance();
            qe.init(res);
            stmnt.close();

        } catch (Exception ex) {
            Logger.getLogger(Controleur.class.getName()).log(Level.SEVERE, null, ex);
        }

        return qe;
        
    }

    /**
     *
     * @param qA
     * @param qB
     * @return
     */
    public QuestionEntite fetchPremiereQuestionCommune(QuestionEntite qA, QuestionEntite qB) {

        Stack<Integer> parentsQuestionsA = new Stack<Integer>();
            parentsQuestionsA.add(qA.getId());
        Stack<Integer> parentsQuestionsB = new Stack<Integer>();
            parentsQuestionsB.add(qB.getId());

        int idQuestionRacine = this.fetchFirstQuestion().getId();

        QuestionEntite resultat = null;

        // On est déjà sur le noeud commun
        if (qA.getId() == qB.getId()) {
            resultat = qA;
        }

        while (resultat==null)
        {

            if (qA.getId()!=idQuestionRacine)
            {
                // Remonte la qA tant qu'on est pas à la racine
                qA = fetchQuestionById(qA.getParent(), false);
                parentsQuestionsA.add(qA.getId());

                // Vérifie
                if (parentsQuestionsB.contains(Integer.valueOf(qA.getId()))) {
                    resultat = qA;
                }

            }

            if (qB.getId()!=idQuestionRacine && resultat==null)
            {
                // Remonte la qB tant qu'on est pas à la racine
                qB = fetchQuestionById(qB.getParent(), false);
                parentsQuestionsB.add(qB.getId());

                // Vérifie
                if (parentsQuestionsA.contains(Integer.valueOf(qB.getId())))
                {
                    resultat = qB;
                }
            }
        }

        return resultat;

    }



}
