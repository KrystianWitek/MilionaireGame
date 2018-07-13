package server.gameManagement;

import server.players.Player;
import server.questions.Question;
import java.io.*;
import java.net.SocketException;
import java.util.List;

public class GameController {

    private Game game;
    private SecureMessage secMsg = new SecureMessage();
    private String rightAnswer;

    public GameController(Game game){
        this.game = game;
    }

    private void showPlayersInfo(){
        System.out.println("=============================================================");
        System.out.println("Gra nr: " + game.getGameID());
        System.out.println("Player1 = " + game.getPlayer1().getNick() + "\nPlayer2 = " + game.getPlayer2().getNick());
        System.out.println("=============================================================");
    }

    public void runGame(){
        showPlayersInfo();
        startGame();
        comparePlayersScores();
        endGame();
    }

    private List<Question> getGameQuestionsList(){
        return game.getQuestionPool().getListOfQuestions();
    }

    private void startGame(){
        System.out.println("Rozpoczynam quiz...");
        for(Question q: getGameQuestionsList()){
            if(!game.isExit()){
                setRightAnswer(q);
                questionHandler(q);
                answerHandler();
                notifyPlayersAboutScores();
            }
        }
    }

    private void setRightAnswer(Question question){
        rightAnswer = question.getRightAnswer();
    }

    private void questionHandler(Question question){
        try {
            for (Player p : game.getListOfPlayers()) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(p.getSocket().getOutputStream()));
                sendQuestionToPlayer(question, out);
            }
        } catch (IOException ex) {
            System.out.println("Błąd w trakcie wysyłania pytania do gracza");
        }
    }

    private void sendQuestionToPlayer(Question question, PrintWriter out){
        out.println(secMsg.sendSecuredMessageToServer("START_OF_QUESTION"));
        out.println("================================================================");
        out.println(question.getContent());
        out.println(question.getFirstAnswer());
        out.println(question.getSecondAnswer());
        out.println(question.getThirdAnswer());
        out.flush();
        out.println("END_OF_QUESTION");
        out.flush();
    }

    private void answerHandler(){
        askPlayersForAnswer();
        getAnswersFromPlayers();
    }

    private void askPlayersForAnswer(){
        int askForAnswer = 0;
        PrintWriter out;

        for(Player p : game.getListOfPlayers()) {
            try {
                out = new PrintWriter(new OutputStreamWriter(p.getSocket().getOutputStream()));
                if (askForAnswer != 2) {
                    out.println(secMsg.sendSecuredMessageToServer("GIVE_ME_ANSWER"));
                    out.println("Podaj odpowiedż");
                    out.flush();
                    askForAnswer++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getAnswersFromPlayers(){
        int answers = 0;
        String msg;
        PrintWriter out;
        BufferedReader in;
        try{
            while(answers != 2){
                for(Player player : game.getListOfPlayers()){
                    in = new BufferedReader(new InputStreamReader(player.getSocket().getInputStream()));
                    out = new PrintWriter(new OutputStreamWriter(player.getSocket().getOutputStream()));
                    if(answers != 2){
                        if (in.readLine().equals("+-+")) {
                            msg = in.readLine();
                            System.out.println("Player " + player.getNick() + " odpowiedział: " + msg);
                            if (msg.equals(rightAnswer)) {
                                notifyPlayerAboutAnswerCorrection(player, true, out);
                            } else {
                                notifyPlayerAboutAnswerCorrection(player, false, out);
                            }
                            answers++;
                        }
                        else{
                            System.out.println("Zły komunikat od klienta!");
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            System.out.println("Drugi gracz się rozłączył");
            whenPlayerDisconnected();
//            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Nie mozna powiadomic gracza");
        }
    }

    private void notifyPlayerAboutAnswerCorrection(Player p, boolean isCorrect, PrintWriter out){
        out.println(secMsg.sendSecuredMessageToServer("ASKED"));
        if(isCorrect){
            out.println("Poprawna odpowiedź");
            p.setScore(p.getScore() + 1);
        }
        else{
            out.println("Zła odpowiedź");
        }
        out.println("================================================================");
        out.println("END_ASKED");
        out.flush();
    }

    private void notifyPlayersAboutScores() {
        try{
            for(Player p : game.getListOfPlayers()){
                PrintWriter out = new PrintWriter(new OutputStreamWriter(p.getSocket().getOutputStream()));
                out.println(secMsg.sendSecuredMessageToServer("NEXT_QUESTION"));
                out.println(currentScores());
                out.println("\nPobieranie kolejnego pytania...\n");
                out.println("END_NEXT_QUESTION");
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("NR GRY: "+ game.getGameID() + " GRACZE: "+ game.getPlayer1().getNick() +" "+ game.getPlayer1().getScore() +" || "+ game.getPlayer2().getNick() +" "+ game.getPlayer2().getScore());
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    private String currentScores(){
        return "Gracz " + game.getPlayer1().getNick() +" = "+ game.getPlayer1().getScore() +" || "+ "Gracz " + game.getPlayer2().getNick() +" = "+ game.getPlayer2().getScore();
    }

    private void endGame() {
        try {
            for(Player player : game.getListOfPlayers()){
                PrintWriter out = new PrintWriter(new OutputStreamWriter(player.getSocket().getOutputStream()));
                out.println(secMsg.sendSecuredMessageToServer("SCORE"));
                out.println("Twój wynik = " + player.getScore());

                out.println(game.sayToPlayerWhoIsWinner(player));
                out.flush();

                out.println("END_SCORE");
                sendEndGame(out);
            }
            System.out.println("Gracze otrzymali swoje wyniki --- KONIEC GRY");

        } catch (IOException e) {
            System.out.println("Nie mozna powiadomic gracza");}
    }

    private void whenPlayerDisconnected() {
        game.setGameExit();
        try {
            for (Player p : game.getListOfPlayers()) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(p.getSocket().getOutputStream()));

                out.println(secMsg.sendSecuredMessageToServer("PLAYER_DISCONNECTED"));
                out.flush();
                sendEndGame(out);
            }
        } catch (IOException e) {
            System.out.println("Powiadamianie gracza nie powiodło się!");
//            e.printStackTrace();
        }
    }

    private void sendEndGame(PrintWriter out) throws IOException{
        out.println(secMsg.sendSecuredMessageToServer("GAME_OVER"));
        out.flush();
    }

    private void comparePlayersScores(){
        game.comparePlayersScores();
    }
}
