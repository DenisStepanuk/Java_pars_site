package myefe;

import BD.BDMyefe;
import XML.MyefeWord;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreadMyefe extends Thread {
    BDMyefe bdMyefe;
    Separation separation;
    public ThreadMyefe(String name, Separation separation) throws SQLException {
        super(name);
        this.bdMyefe = new BDMyefe();
        this.separation = separation;
    }
    @Override
    public void run() { // Поток
        // получаем партию слов
        try {
            System.out.println(Thread.currentThread().getName() + " запущен");
            List<Word> words;
            while ((words = separation.getPortion()).size() > 0) {
                // закидываем в цикл
                for (int i = 0; i < words.size(); i++) {
                    saveWord(words.get(i).id, words.get(i).word, bdMyefe);
                }
            }
            System.out.println("Поток " + Thread.currentThread().getName() + " ЗАКРЫТ.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void saveWord(int id, String word, BDMyefe bdMyefe){
        try {
            Document doc = Jsoup.connect("https://myefe.ru/anglijskaya-transkriptsiya/" + word).get();  // Ищем xml
            final String regex = "var MLSW7_APP_DATA = (.+\"locale\":\"ru_RU\"\\})";    // Форматируем полученые данные
            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(doc.toString());
            if (matcher.find()) {
                String json = matcher.group(1);
                JSONObject obj = new JSONObject(json);
                GsonBuilder gsonBuilder = new GsonBuilder();
                MyefeWord myefeWord = gsonBuilder.create().fromJson(json, MyefeWord.class);
                myefeWord.setIDWord(id);
                myefeWord.setJson(json);
                myefeWord.saveToBD(bdMyefe);
                //myefeWord.show();
                FileWriter writer = new FileWriter("D:/myefe/json/"+ word +".json", false); // записываем в файл
                // запись всей строки
                writer.write(obj.toString());
                writer.flush();
            }
            else {
                //System.out.println("Объект regex слова \""+word+"\" не найден...");
            }
        } catch (JsonSyntaxException jse){
            //System.out.println("Json не найден: " + word);
        } catch (HttpStatusException httpError){
            //System.out.println(httpError);
            //System.out.println("Нет его: " + word);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
// myefe

class IDCityList{
    // Дописать реализацию записи
    ArrayList<Integer> ids;
    IDCityList(){
        //ids = BD.getAllIDCity();
    }
    synchronized public boolean contains(Integer integer){
        return ids.contains(integer);
    }
    synchronized public void setId(Integer integer){
        ids.add(integer);
    }
}
