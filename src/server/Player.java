package server;

import java.net.Socket;

public class Player {

    private int playerID;
    private int score = 0;
    private String nick;
    private Socket socket;
    private int isWon = 0;

    public void setWon(int number){
        isWon = number;
    }

    public int getWon(){
        return isWon;
    }

    public Socket getSocket() {
        return socket;
    }

    public Player(String nick, Socket s) {
        this.nick = nick;
        this.socket = s;
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getNick() {
        return nick;
    }

    public String toString() {
        return "Graczu " + nick + ", Your score = " + score;
    }
}
