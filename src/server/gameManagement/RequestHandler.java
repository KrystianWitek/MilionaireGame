package server.gameManagement;

public class RequestHandler extends Thread{

    private GameController gameController = null;
    public RequestHandler(GameController game){
        this.gameController = game;
    }

    public void run(){
        gameController.runGame();
    }
}