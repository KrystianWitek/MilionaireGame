package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;

class MilionaireServer {

    private ServerSocket serverSocket;
    private volatile boolean isServerWorking = true;

    MilionaireServer() {
        setServerSettings();
        getCurrentCalendarDate();
        startServer();
    }
    private void getCurrentCalendarDate(){
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd.MM.yyyy 'o godzinie' hh:mm");
        System.out.println("Serwer został uruchomiony w: " + dateFormat.format(currentDate.getTime()));
    }

    private void setServerSettings(){
        final int port = 9877;
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException ex) {
            System.out.println("Nie można utworzyć serwera na porcie " + port);
            System.exit(-1);
        }
    }

    private void startServer() {
        while(isServerWorking){
            try{
                ServerController.handleServerSocket(serverSocket);
            } catch(SocketException ex){
                System.out.println("Jeden z graczy się rozlaczyl, usuwanie gracza z kolejki...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closeServer();
    }

    private void closeServer(){
        try{
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Napotkano problem podczas wyłączania serwera");
        }
    }
}
