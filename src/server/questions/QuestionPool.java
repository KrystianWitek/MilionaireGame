package server.questions;

import server.repository.ConnectionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionPool {

    // zaczynam od 3 i koncze na 6  bo w bazie mam od 3 indexu ( jeszcze nie zmienione )
    private static final int QUESTIONS_NUMBER = 6;
    private List<Question> listOfQuestions = new ArrayList<>();

    public List<Question> getListOfQuestions() {
        return listOfQuestions;
    }

    public void generateQuestions(){
        createQuestions();
//        showQuestions();
    }

    private void createQuestions() {
        try{
            ConnectionManager connectionManager = new ConnectionManager();
            for(int i = 3; i < QUESTIONS_NUMBER; i++){
                Question question = connectionManager.getQuestionByID(i);
                listOfQuestions.add(question);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Pytanie zostało wczytane poprawnie.");
    }

    private void showQuestions(){
        System.out.println("Wszystkie pytania:");
        for(Question q : listOfQuestions){
            q.showAllData();
        }
    }
}