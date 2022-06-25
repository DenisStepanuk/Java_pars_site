package myefe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Separation {
    int countWord;          // колличество строк
    int step;               // один шаг
    static int currentStep; // текущий шаг
    Connection connection;
    Statement statement;
    public Separation(int step) throws SQLException {
        this.step = step;
        // Подключаемчя к БД
        connection = DriverManager.getConnection("jdbc:mysql://localhost/english", "root", "");
        statement = connection.createStatement();
        // SQL запрос
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM word_eng");
        resultSet.next();
        countWord = resultSet.getInt(1); // Получаем количество строк в таблице
        currentStep = 8200;
    }

    // Выдаем порцию данных
    synchronized public List<Word> getPortion() throws SQLException {
        if(currentStep>countWord) return null;
        List<Word> words = new ArrayList<>();
        // SQL запрос
        ResultSet resultSet = statement.executeQuery("SELECT id, word FROM word_eng " +
                "ORDER BY word_eng.id ASC LIMIT " + currentStep + ", " + step);
        System.out.println(currentStep + " | " + step);
        while (resultSet.next()) {
            words.add(new Word(resultSet.getInt(1), resultSet.getString(2)));
        }
        currentStep += step;
        return words;
    }
}
class Word{
    public int id;
    public String word;
    Word(int id, String word){
        this.id = id;
        this.word = word;
    }
}
