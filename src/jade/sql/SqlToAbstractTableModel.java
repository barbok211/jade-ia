package jade.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

public class SqlToAbstractTableModel extends AbstractTableModel {

    ResultSet result = null;
    HashMap<Integer, Ligne> donnees = new HashMap<Integer, Ligne>();
    
    public SqlToAbstractTableModel(String query) {
        try {
            Statement stmnt = ConnexionManager.getCurrentConnexion().createStatement();
            result = stmnt.executeQuery(query);
            
            int i=0;
            while (result.next())
            {
                Ligne l = new Ligne();
                l.nom = result.getString("NOM");
                l.gagne = result.getInt("NB_TROUVE");
                l.perd = result.getInt("NB_PERD");
                donnees.put(i, l);
                i++;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(SqlToAbstractTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getColumnCount() {
        return 3;
    }

    public int getRowCount() {
        return donnees.size();
    }

    public String getColumnName(int c) {
        String s = "";
           switch(c) {
            case 0:
                s = "Entité";
                break;
            case 1:
                s = "Nombre de fois trouvé";
                break;
            case 2:
                s = "Nombre d'erreurs";
             break;
        }
        return s;
    }

    public Object getValueAt(int row, int column) {
        Object o = null;
        switch(column) {
            case 0:
                o = donnees.get(row).nom;
                break;
            case 1:
                o = donnees.get(row).gagne;
                break;
            case 2:
                o = donnees.get(row).perd;
             break;
        }
        return o;
    }
    private static Object[][] data;
    private static Object[] colname;

    class Ligne {
        public String nom;
        public int gagne;
        public int perd;
    }
}
