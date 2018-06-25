package server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionPool {

    private Question question = null;
    private List<Question> listOfQuestions = new ArrayList<>();

    public List<Question> getListOfQuestions() {
        return listOfQuestions;
    }

    public void prepareQuestions(){
        generateQuestions(1);
        generateQuestions(2);
        generateQuestions(3);
    }

    public void generateQuestions(int questionLevel){
        // ustawiam wszystkie dane łącząc się z bazą
        createQuestions();
        //showQuestions();
    }

    private void createQuestions() {
        // zaczynam od 3 i koncze na 6  bo w bazie mam od 3 indexu ( jeszcze nie zmienione )
        int counter = 6; // 6
        Question question;

        try{
            ConnectionManager connectionManager = new ConnectionManager();
            for(int i = 3; i < counter; i++){
                question = connectionManager.getQuestion(i);
                listOfQuestions.add(question);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Pytanie zostało wczytane poprawnie.");
    }

    public void showQuestions(){
        for(Question q : listOfQuestions){
            q.showAllData();
        }
    }
}