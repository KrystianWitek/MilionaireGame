package server.players;

import server.gameManagement.SecureMessage;
import java.io.*;
import java.util.Queue;

public class PlayersCheckConnection {

    public static boolean stillConnected(Queue<Player> playerQueue) throws IOException {
        BufferedReader in;
        PrintWriter out;
        SecureMessage secMsg = new SecureMessage();
        int checked = 0;

        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("Sprawdzanie połączenia z graczami...");

        for (Player p : playerQueue) {
            in = new BufferedReader(new InputStreamReader(p.getSocket().getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(p.getSocket().getOutputStream()));

            out.println(secMsg.sendSecuredMessageToServer("CONNECTION"));
            out.println("Serwer sprawdza, czy gracz jest nadal połączony...");
            out.flush();

            String msg = in.readLine();
            if(msg.equals("CONNECTION_OK")){
                checked++;
                System.out.println("Gracz " + p.getNick() + " jest połączony");
            }
            else System.out.println("Nie znaleziono gracza " + p.getNick() + "!");
        }

        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if(checked == 2)
            return true;
        return false;
    }
}
