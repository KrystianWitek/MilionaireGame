package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MilionaireServer {

    private ServerSocket serverSocket;
    private volatile boolean isServerWorking = true;
    private static int gameID = 1;

    public MilionaireServer() {

        final int port = 9877;
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException ex) {
            System.out.println("Nie można utworzyć serwera na porcie " + port);
            System.exit(-1);
        }

        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd.MM.yyyy 'o godzinie' hh:mm");
        System.out.println("Serwer został uruchomiony w: " + dateFormat.format(currentDate.getTime()));

        serviceConnections();
    }

    private void serviceConnections() {
        Player p1,p2;
        Socket socket;
        Queue<Player> playerQueue = new LinkedList<>();

        while(isServerWorking){
            try{
                socket = serverSocket.accept();
                playerQueue.add(createPlayer(socket));

                // gdy serwer znajdzie 2 graczy uruchamia obsługę gry

                if(playerQueue.size() == 2){
                    if(checkPlayersConnection(playerQueue) == true){
                        p1 = playerQueue.remove();
                        p2 = playerQueue.remove();

                        Game game = new Game(p1,p2,gameID++);
                        new RequestHandler(game).start();
                    }
                    else
                        System.out.println("Nastąpił błąd podczas tworzenia rozgrywki");
                }
                else System.out.println("Oczekiwanie na drugiego gracza...");

            } catch(SocketException ex){
                System.out.println("Gracz " + playerQueue.remove().getNick() +" sie rozlaczyl, usuwanie gracza z kolejki...");
//                ex.printStackTrace();
            } catch (IOException e) {
                System.out.println("Problem z IO");
            }
        }
        try{
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Napotkano problem podczas wyłączania serwera");
        }
    }

    private boolean checkPlayersConnection(Queue<Player> playerQueue) throws IOException {
        BufferedReader in;
        PrintWriter out;
        int check = 0;

        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("Sprawdzanie połączenia z graczami...");

        for (Player p : playerQueue) {
            in = new BufferedReader(new InputStreamReader(p.getSocket().getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(p.getSocket().getOutputStream()));

            out.println("+-+");
            out.println("CONNECTION");
            out.println("Serwer sprawdza, czy gracz jest nadal połączony...");
            out.flush();

            String msg = in.readLine();
            if(msg.equals("CONNECTION_OK")){
                check++;
                System.out.println("Gracz " + p.getNick() + " jest połączony");
            }
            else System.out.println("Nie znaleziono gracza " + p.getNick() + "!");
        }

        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if(check == 2)
            return true;
        return false;
    }

    private Player createPlayer(Socket s){
        System.out.println("Zaakceptowano klienta --- " + s.getInetAddress().getHostName());
        String nick = "";
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out  = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));

            out.println("+-+");
            out.println("START_NICK");
            out.println("Serwer pobiera nick gracza...");
            out.flush();

            nick = in.readLine();
            System.out.println(nick);

            out.println("+-+");
            out.println("END_NICK");
            out.println("Nick został wczytany poprawnie przez serwer");
            out.flush();
        }
        catch (IOException ex) {
            System.out.println("Wystąpił błąd podczas pobierania nicku gracza");
        }
        return new Player(nick, s);
    }
}