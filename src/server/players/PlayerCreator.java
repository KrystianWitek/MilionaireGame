package server.players;

import server.gameManagement.SecureMessage;

import java.io.*;
import java.net.Socket;

public class PlayerCreator {

    public static Player createPlayerBySocket(Socket s){
        System.out.println("Zaakceptowano klienta --- " + s.getInetAddress().getHostName());
        String nick = "";
        SecureMessage secMsg = new SecureMessage();
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out  = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));

            out.println(secMsg.sendSecuredMessageToServer("START_NICK"));
            out.println("Serwer pobiera nick gracza...");
            out.flush();

            nick = in.readLine();
            System.out.println(nick);

            out.println(secMsg.sendSecuredMessageToServer("END_NICK"));
            out.println("Nick został wczytany poprawnie przez serwer");
            out.flush();
        }
        catch (IOException ex) {
            System.out.println("Wystąpił błąd podczas pobierania nicku gracza");
        }
        return new Player(nick, s);
    }
}
