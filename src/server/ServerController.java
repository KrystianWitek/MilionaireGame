package server;

import server.gameManagement.Game;
import server.gameManagement.GameController;
import server.gameManagement.RequestHandler;
import server.players.Player;
import server.players.PlayerCreator;
import server.players.PlayersCheckConnection;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ServerController {
    private static int gameID = 1;
    private static Queue<Player> playerQueue = new LinkedList<>();

    public static void handleServerSocket(ServerSocket serverSocket) throws IOException {
        handleSocketConnectionForPlayer(serverSocket);
        if (playerQueue.size() == 2) {
            twoPlayersAreReadyInQueue();
        }
        else System.out.println("Oczekiwanie na drugiego gracza...");
    }

    private static void handleSocketConnectionForPlayer(ServerSocket serverSocket) throws IOException {
        Socket socket = serverSocket.accept();
        socket.setSoTimeout(30000);
        Player currentPlayer = PlayerCreator.createPlayerBySocket(socket);
        addPlayerToQueue(currentPlayer);
    }

    private static void twoPlayersAreReadyInQueue() throws IOException {
        if (playersAreStillConnected()) {
            createGameFor2Players();
        } else
            System.out.println("Nastąpił błąd podczas tworzenia rozgrywki");
    }

    private static boolean playersAreStillConnected() throws IOException {
        return PlayersCheckConnection.stillConnected(playerQueue);
    }

    private static void createGameFor2Players(){
        Player p1 = playerQueue.remove();
        Player p2 = playerQueue.remove();
        Game game = new Game(p1, p2, gameID++);
        GameController gameController = new GameController(game);
        new RequestHandler(gameController).start();
    }

    private static void addPlayerToQueue(Player p){
        playerQueue.add(p);
    }
}