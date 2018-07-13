package client;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.ServerException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

class ClientGame {
    private static int roundCounter = 1;
    private static Socket socket = null;
    private static BufferedReader in = null;
    private static PrintWriter out = null;
    private static boolean exitFromLoop = false;
    private final static String host = "localhost";
    private final static int port = 9877;
    private static Scanner scanner = new Scanner(System.in);
    private static String playerNick;

    static void runClientGame(){

        System.out.println("Podaj swój nick");
        playerNick = getLineFromScanner();
        while(!exitFromLoop){
            runClientMenuHandler();
        }
    }

    private static void runClientMenuHandler(){
        showMenu();
        System.out.println("Wybierz opcję: ");
        String option = getLineFromScanner();
        switch (option){
            case "1":
                startPlayerGame();
                showMessageAfterEndGame();
                break;
            case "2":
                showInformationsAboutPlayerScores();
                break;
            case "3":
                showAuthorsInformations();
                break;
            case "0":
                endClientGame();
                break;
            default:
                System.out.println("Wybrałeś nieistniejącą opcję, spróbuj ponownie!");
        }
    }

    private static void startPlayerGame(){
        try {
            prepareConnectionSettings();
            managementMessagesFromServer();
        } catch (ServerException ex){
            System.out.println("Stracono połączenie z serwerem");
        }catch (UnknownHostException ex){
            System.out.println("Nieznany host");
        } catch (ConnectException ex){
            System.out.println("Błąd łączenia z serwerem");
        } catch (IOException e) {
            System.out.println("Nie można wysłać wiadomości, serwer nie odpowiada");
        }
    }

    private static void managementMessagesFromServer() throws IOException {
        boolean gameOver = false;
        do{
            String message = checkMessageCorrection(in.readLine());
            switch (message) {
                case "PLAYER_DISCONNECTED": {
                    System.out.println("\n=======================================");
                    System.out.println("Przeciwnik sie rozłączył, wygrałeś grę :D");
                    break;
                }
                case "START_NICK": {
                    receiveMessageFromServer();
                    sendMessageToServer(playerNick);
                    break;
                }
                case "END_NICK": {
                    receiveMessageFromServer();
                    System.out.println("Oczekiwanie na drugiego gracza...");
                    break;
                }
                case "CONNECTION": {
                    receiveMessageFromServer();
                    sendMessageToServer("CONNECTION_OK");
                    break;
                }
                case "SCORE": {
                    readMessagesFromServer("END_SCORE");
                    break;
                }
                case "GAME_OVER": {
                    gameOver = true;
                    System.out.println("Gra zostala zakonczona");
                    System.out.println("===========================================");
                    closeSocketConnections();
                    break;
                }
                case "START_OF_QUESTION": {
                    System.out.println("Runda " + roundCounter++);
                    readMessagesFromServer("END_OF_QUESTION");
                    break;
                }
                case "GIVE_ME_ANSWER": {
                    receiveMessageFromServer();
                    handlePlayerAnswer();
                    break;
                }
                case "ASKED": {
                    readMessagesFromServer("END_ASKED");
                    internalTimeCounter(1);
                    break;
                }
                case "NEXT_QUESTION": {
                    readMessagesFromServer("END_NEXT_QUESTION");
                    internalTimeCounter(3);
                    break;
                }
                default: {
                    System.out.println("Otrzymano zły komunikat ze strony serwera!");
                    break;
                }
            }
        } while (!gameOver || !socket.isClosed());
    }

    private static void readMessagesFromServer(String endOfMessage) throws IOException {
        boolean isTrue = false;
        while(!isTrue) {
            String message = in.readLine();
            if(message.equals(endOfMessage))
                isTrue = true;
            else
                System.out.println(message);
        }
    }

    private static void internalTimeCounter(int seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            System.out.println("Problem z odliczaniem czasu");
        }
    }

    private static String checkMessageCorrection(String messageFromServer){
        String msgBeggining;
        if(messageFromServer.startsWith("+-+")){
            msgBeggining = messageFromServer.substring(3,messageFromServer.length());
            String correctMessage = "";
            if(msgBeggining.endsWith("-+-")){
                correctMessage = msgBeggining.substring(0,msgBeggining.length()-3);
            }
            return correctMessage;
        }
        return "BAD_MESSAGE";
    }

    private static void prepareConnectionSettings() throws IOException {
        socket = new Socket(host, port);
        System.out.println("Polaczono z serwerem.");
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private static void receiveMessageFromServer() throws IOException {
        String message = in.readLine();
        System.out.println(message);
    }
    private static void sendMessageToServer(String message){
        out.println(message);
        out.flush();
    }

    private static void showInformationsAboutPlayerScores(){
        System.out.println("=======================================");
        System.out.println("Wyniki");
        System.out.println("=======================================");
    }

    private static void showAuthorsInformations(){
        System.out.println("=======================================");
        System.out.println("Krystian Witek");
        System.out.println("=======================================");
    }

    private static void showMessageAfterEndGame(){
        System.out.println("Za kilka sekund nastąpi powrót do Menu Głównego...");
        internalTimeCounter(5);
        System.out.println("\n\n\n\n");
    }

    private static void endClientGame(){
        System.out.println("=======================================");
        exitFromLoop = true;
        System.out.println("Koniec gry");
    }

    private static void handlePlayerAnswer(){
        String answer = getLineFromScanner();
        System.out.println("Odpowiedziałeś " + answer + ", poczekaj na drugiego gracza.");
        out.println("+-+");
        sendMessageToServer(answer.toLowerCase());
    }

    private static void closeSocketConnections() throws IOException {
        in.close();
        out.close();
        socket.close();
        System.out.println("Połączenia zostały zamknięte pomyślnie");
    }

    private static String getLineFromScanner(){
        return scanner.nextLine();
    }

    private static void showMenu(){
        System.out.println("\nWitaj w Milionerach");
        System.out.println("1. Graj przez internet z drugim graczem");
        System.out.println("2. Pokaż najlepsze wyniki");
        System.out.println("3. Informacje o autorach");
        System.out.println("0. Wyjdź z gry\n");
    }
}
