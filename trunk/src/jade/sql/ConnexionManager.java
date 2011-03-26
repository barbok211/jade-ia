package jade.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Gestion de la connexion à la base de donnée embarquée
 */
public class ConnexionManager {

    // ---------- Configuration -------- //
    private static final String BDD_DRIVER = "org.hsqldb.jdbcDriver";
    private static final String BDD_URI = "jdbc:hsqldb:file:db/jade;shutdown=true";
    private static final String BDD_LOGIN = "sa";
    private static final String BDD_PASS = "";
    // ----------/Configuration/-------- //
    

    private Connection connexion;

    /**
     * Initialise la connexion à la BDD
     */
    private ConnexionManager()
    {
        try {
            Class.forName(BDD_DRIVER).newInstance();
            connexion = DriverManager.getConnection(BDD_URI, BDD_LOGIN, BDD_PASS);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Impossible de charger le driver HSQLDB", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ConnexionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            JOptionPane.showMessageDialog(null, "Impossible de charger le driver HSQLDB", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ConnexionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            JOptionPane.showMessageDialog(null, "Impossible de charger le driver HSQLDB", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ConnexionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Impossible d'initialiser la connexion", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ConnexionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }

    /**
     * Retourne la connexion courante
     * @return Connexion ouverte
     */
    public Connection getConnexion()
    {
        return this.connexion;
    }

    /**
     * Retourne la connexion courante sans récupérer l'instance
     * @return Connexion ouverte
     */
    public static Connection getCurrentConnexion()
    {
        return getInstance().getConnexion();
    }


    /**
     * Retourne le dernier ID inséré sur la table donnée
     * @param table table à examiner
     * @return dernier ID inséré
     */
    public int getLastInsertedId(String table)
    {
        int id=-1;
        try {
            Statement stmnt = ConnexionManager.getCurrentConnexion().createStatement();
            ResultSet res = stmnt.executeQuery("SELECT ID FROM "+table+" ORDER BY ID DESC LIMIT 1");
            if (res!=null && res.next()) id = res.getInt("id");
        } catch(Exception e) {}

        return id;
    }


    // ---------- Singleton ----------- //
    private static ConnexionManager _instance = new ConnexionManager();

    public static ConnexionManager getInstance() {
        return _instance;
    }
    // ----------/Singleton/----------- //


}
