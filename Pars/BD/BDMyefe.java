package BD;

import XML.MyefeWord;
import com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException;

import java.sql.*;

public class BDMyefe {
    Connection conn;
    Statement statement;
    public BDMyefe() throws SQLException { // Подключение к БД
        conn = DriverManager.getConnection("jdbc:mysql://localhost/english", "root", "");
        statement = conn.createStatement();
    }
    public int getIDmyefe(int IDWord, String IDMyefe) throws SQLException {
        ResultSet resultSet = statement.executeQuery(
                "SELECT setIDMyefe(" + IDWord + ", " + IDMyefe + ") AS setIDMyefe;");
        resultSet.next();
        return  resultSet.getInt(1);
    }

    public void setTransc(int IDWord, String transc_gb, String sound_gb,
                          String transc_us, String sound_us) throws SQLException {
        statement.executeUpdate(
                "CALL `setTransc`('"+IDWord+"', '"
                        +transc_gb+"', '"+sound_gb+"', '"
                        +transc_us+"', '"+sound_us+"');");
    }

    public void setExamples(int IDWord, MyefeWord.Word_data.Mltsr.G_object.Examples.Example[] examples) throws SQLException {
        if(examples.length > 0) {
            for (int i = 0; i < examples.length; i++) {
                statement.executeUpdate("INSERT INTO `example` (`id_word`, `sentence`) " +
                        "VALUES ('"+IDWord+"','"+examples[i].text.replaceAll("'", "''")+"');");
            }
        }
    }

    public void set_rus_eng(int IDWord, MyefeWord.Word_data.Mltsr.G_object.Dict[] dicts) throws SQLException {
        for (int i = 0; i < dicts.length; i++) {
            String wordPOS = dicts[i].pos.replaceAll("'", "''"); // Глагол Существительное Прилагательное Местоимение Числительные Наречие
            MyefeWord.Word_data.Mltsr.G_object.Dict.Entry[] entries = dicts[i].entry;
            for (int i1 = 0; i1 < entries.length; i1++) {
                String wordRUS = entries[i1].word.replaceAll("'", "''");
                for (int i2 = 0; i2 < entries[i1].reverse_translation.length; i2++) {
                    String wordENG = entries[i1].reverse_translation[i2].replaceAll("'", "''");
                    statement.executeUpdate("CALL `set_rus_eng`('"+wordENG+"', '"+wordRUS+"', '"+wordPOS+"');");
                }
            }
        }
    }

    public void setDefinition(int IDWord, MyefeWord.Word_data.Mltsr.G_object.Definitions[] definitions) throws SQLException {
        for (int i = 0; i < definitions.length; i++) {
            String wordPOS = definitions[i].pos.replaceAll("'", "''"); //POS
            MyefeWord.Word_data.Mltsr.G_object.Definitions.Entry[] entries = definitions[i].entry;
            for (int i1 = 0; i1 < entries.length; i1++) {
                String gloss = entries[i1].gloss.replaceAll("'", "''");
                String exampleEntr;
                try {
                    exampleEntr = entries[i1].example.replaceAll("'", "''");
                } catch (NullPointerException NullEx){
                    exampleEntr = "";
                }
                try {
                    statement.executeUpdate(
                            "CALL `setDefinition`('" + IDWord + "', '" + wordPOS + "', '"
                                    + gloss + "', '" + exampleEntr + "');");
                    //CALL `setDefinition`(idWordENG, wordPOS, glossSentence, exampleSentence);
                }  catch (MySQLTransactionRollbackException deadlock){
                    //Дописать решение!!!!
                }
            }
        }
    }

    public void setJson(String json) throws SQLException {
        statement.executeUpdate("INSERT INTO jsons (myefejson) VALUES ('"+json.replaceAll("'","''")+"')");
    }

    public void getWords() {
    }
}
