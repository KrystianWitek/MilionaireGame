package server;

public class RequestHandler extends Thread{

    private Game game = null;

    RequestHandler(Game game){
        this.game = game;
    }

    public void run(){
        try{

            // wątek obsługuje pojedyńczą rozgrywkę

            game.startGame();

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}