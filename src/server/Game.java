package server;
import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

class Game {

    private boolean gameExit = false;
    private int gameID;
    private Player p1 = null;
    private Player p2 = null;
    private QuestionPool questionPool = new QuestionPool();
    private List<Player> players = new ArrayList<>();

    Game(Player p1, Player p2, int gameID){
        this.p1 = p1;
        this.p2 = p2;
        this.gameID = gameID;

        // w konstruktorze od razu generuję pytania dla graczy

        questionPool.generateQuestions(0);
        players.add(p1);
        players.add(p2);
    }

    private void showPlayers(){
        System.out.println("=============================================================");
        System.out.println("Gra nr: " + gameID);
        System.out.println("Player1 = " + p1.getNick() + "\nPlayer2 = " + p2.getNick());
        System.out.println("=============================================================");
    }

    void startGame(){
        showPlayers();
        System.out.println("Rozpoczynam quiz...");
        for(Question q : questionPool.getListOfQuestions()){

            // uruchamiam funkcję obsługi pojedyńczego pytania, tj. pojedyńcza runda
            if(!gameExit)
                askPlayers(q);
        }
        // porównanie wyników
        comparePlayersScores();

        endGame();
    }

    private void askPlayers(Question question) {
        BufferedReader in;
        PrintWriter out;

        // wysyłam pytania do klientów
        try {
            for (Player p : players) {
                out = new PrintWriter(new OutputStreamWriter(p.getSocket().getOutputStream()));
                sendQuestion(out, question);
                }
            } catch (IOException ex) {
            System.out.println("Błąd podczas wysyłania pytania do gracza");
        }

        int answers = 0;
        int askForAnswer = 0;
        String msg;

        //  pętla obsługuje wczytywanie odpowiedzi od klientów

        try {
            while (answers != 2) {
                for (Player p : players) {
                    in = new BufferedReader(new InputStreamReader(p.getSocket().getInputStream()));
                    out = new PrintWriter(new OutputStreamWriter(p.getSocket().getOutputStream()));

                    // wysyłam do gracza prośbę o uzyskanine odpowiedzi

                    if (askForAnswer != 2) {
                        out.println("+-+");
                        out.println("GIVE_ME_ANSWER");
                        out.println("Podaj odpowiedż");
                        out.flush();
                        askForAnswer++;
                    }

                    // jeśli obaj gracze dostali powiadomienie to zaczynam pobierać od nich odpowiedzi

                    if(askForAnswer == 2 && answers != 2){
                        if (in.readLine().equals("+-+")) {
                            msg = in.readLine();
                            System.out.println("Player " + p.getNick() + " odpowiedział: " + msg);
                            if (msg.equals(question.getRightAnswer())) {
                                correctAnswer(out, p);
                            } else {
                                wrongAnswer(out, p);
                            }
                            answers++;
                        }
                        else{
                            System.out.println("Zły komunikat od klienta!");
                        }
                    }
                }
            }
            notifyPlayers();

        } catch (SocketException ex) {
            System.out.println("Jeden z graczy się rozłączył");
            whenPlayerDisconnected();
//            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Nie mozna powiadomic gracza");
        }
    }

    private void endGame(){
        try {
            // pętla powiadamia graczy o ich wyniku i o tym kto wygrał mecz

            for(Player p : players) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(p.getSocket().getOutputStream()));
                out.println("+-+");
                out.println("SCORE");
                out.println("Twój wynik = " + p.getScore());

                out.println(findWinner(p));
                out.flush();

                out.println("END_SCORE");

                sendEndGame(out);
            }
            System.out.println("Gracze otrzymali swoje wyniki --- KONIEC GRY");

        } catch (IOException ex) {
            System.out.println("Nie mozna powiadomic gracza");}
    }

    private void sendEndGame(PrintWriter out) throws IOException{
        out.println("+-+");
        out.println("GAME_OVER");
        out.flush();
    }

    private void whenPlayerDisconnected(){
        try {
            for (Player p : players) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(p.getSocket().getOutputStream()));

                gameExit = true;
                out.println("+-+");
                out.println("PLAYER_DISCONNECTED");
                out.flush();

                sendEndGame(out);
            }
        } catch (IOException e) {
            System.out.println("Powiadamianie gracza nie powiodło się!");
//            e.printStackTrace();
        }
    }

    private void sendQuestion(PrintWriter out, Question question){

        // funkcja wysyła pytanie do gracza

        out.println("+-+");
        out.println("START_OF_QUESTION");
        out.println("================================================================");
        out.println(question.getContent());
        out.println(question.getFirstAnswer());
        out.println(question.getSecondAnswer());
        out.println(question.getThirdAnswer());
        out.flush();

        out.println("END_OF_QUESTION");
        out.flush();
    }

    private void correctAnswer(PrintWriter out, Player p){

        // funkcja wysyła wiadomość do gracza o poprawnej odpowiedzi

        out.println("+-+");
        out.println("ASKED");
        out.println("Poprawna odpowiedź");
        p.setScore(p.getScore() + 1);
        out.println("================================================================");
        out.println("END_ASKED");
        out.flush();
    }

    private void wrongAnswer(PrintWriter out, Player p){

        // funkcja wysyła wiadomość do gracza o złej odpowiedzi

        out.println("+-+");
        out.println("ASKED");
        out.println("Zła odpowiedź");
        out.println("================================================================");
        out.println("END_ASKED");
        out.flush();
    }

    private String currentScores(){
        return "Gracz " + p1.getNick() +" = "+ p1.getScore() +" || "+ "Gracz " + p2.getNick() +" = "+ p2.getScore();
    }

    private void notifyPlayers(){

        // funckja powiadamia graczy o obecnym stanie gry

        try{
            for(Player p : players){
                PrintWriter out = new PrintWriter(new OutputStreamWriter(p.getSocket().getOutputStream()));
                out.println("+-+");
                out.println("NEXT_QUESTION");
                out.println(currentScores());
                out.println("\nPobieranie kolejnego pytania...\n");
                out.println("END_NEXT_QUESTION");
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("NR GRY: "+ gameID + " GRACZE: "+ p1.getNick() +" "+ p1.getScore() +" || "+ p2.getNick() +" "+ p2.getScore());
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    private void comparePlayersScores(){

        // funckja ustala jakim wynikiem zakończył się mecz i który z graczy wygrał

        if(p1.getScore() > p2.getScore())
            p1.setWon(1);
        else if(p2.getScore() > p1.getScore())
            p2.setWon(1);
        else if(p1.getScore() == p2.getScore()){
            p1.setWon(2);
            p2.setWon(2);
        }
    }

    private String findWinner(Player p){

        // funckja zwraca komunikat, który jest wysyłany do gracza

        if(p.getWon() == 1)
            return "Wygrales!!!";
        else if(p.getWon() == 2)
            return "Remis :/";
        else
            return "Przegrales :(";
    }
}