package server.questions;

import java.util.ArrayList;

public class Question {

    private int ID;
    private int level;
    private String content;
    private String firstAnswer;
    private String secondAnswer;
    private String thirdAnswer;
    private String rightAnswer;

    private ArrayList<String> questionList = new ArrayList<>();

    public Question(){}

    public Question(int ID, int level, String content, String firstAnswer, String secondAnswer, String thirdAnswer, String rightAnswer) {
        this.ID = ID;
        this.level = level;
        this.content = content;
        this.firstAnswer = firstAnswer;
        this.secondAnswer = secondAnswer;
        this.thirdAnswer = thirdAnswer;
        this.rightAnswer = rightAnswer;
    }

    public void addToList(String s){
        questionList.add(s);
    }

    public void setAllData() {
        int counter = 1;
        for(String s : questionList) {
            switch (counter){
                case 1:
                    setID(Integer.parseInt(s));
                    counter++;
                    break;
//                case 2:
//                    setLevel(Integer.parseInt(s));
//                    counter++;
//                    break;
                case 2:
                    setContent(s);
                    counter++;
                    break;
                case 3:
                    setFirstAnswer(s);
                    counter++;
                    break;
                case 4:
                    setSecondAnswer(s);
                    counter++;
                    break;
                case 5:
                    setThirdAnswer(s);
                    counter++;
                    break;
                case 6:
                    setRightAnswer(s);
                    break;
            }
        }
    }

    public int getID() {
        return ID;
    }

    private void setID(int ID) {
        this.ID = ID;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getContent() {
        return content;
    }

    private void setContent(String content) {
        this.content = content;
    }

    public String getFirstAnswer() {
        return firstAnswer;
    }

    private void setFirstAnswer(String firstAnswer) {
        this.firstAnswer = firstAnswer;
    }

    public String getSecondAnswer() {
        return secondAnswer;
    }

    private void setSecondAnswer(String secondAnswer) {
        this.secondAnswer = secondAnswer;
    }

    public String getThirdAnswer() {
        return thirdAnswer;
    }

    private void setThirdAnswer(String thirdAnswer) {
        this.thirdAnswer = thirdAnswer;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    private void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    void showAllData(){
        System.out.println(content);
        System.out.println(firstAnswer);
        System.out.println(secondAnswer);
        System.out.println(thirdAnswer);
        System.out.println("Poprawna odp = " + rightAnswer);
    }
}
