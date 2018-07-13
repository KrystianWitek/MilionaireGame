package server.gameManagement;
import server.players.Player;
import server.questions.QuestionPool;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private boolean gameExit = false;
    private int gameID;
    private Player player1 = null;
    private Player player2 = null;
    private QuestionPool questionPool = new QuestionPool();
    private List<Player> players = new ArrayList<>();

    public Game(Player player1, Player player2, int gameID){
        this.player1 = player1;
        this.player2 = player2;
        this.gameID = gameID;
        questionPool.generateQuestions();
        players.add(player1);
        players.add(player2);
    }

    public int getGameID() {
        return gameID;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public QuestionPool getQuestionPool() {
        return questionPool;
    }

    public List<Player> getListOfPlayers() {
        return players;
    }

    public boolean isExit(){
        return gameExit;
    }

    public void setGameExit(){
        gameExit = true;
    }

    public void comparePlayersScores(){
        if(player1.getScore() > player2.getScore())
            player1.setWon(1);
        else if(player2.getScore() > player1.getScore())
            player2.setWon(1);
        else if(player1.getScore() == player2.getScore()){
            player1.setWon(2);
            player2.setWon(2);
        }
    }

    public String sayToPlayerWhoIsWinner(Player p){
        if(p.getWon() == 1)
            return "Wygrales!!!";
        else if(p.getWon() == 2)
            return "Remis :/";
        else
            return "Przegrales :(";
    }
}