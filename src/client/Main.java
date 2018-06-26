package client;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.ServerException;
import java.sql.Time;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    private static int roundCounter = 1;
    private static Socket socket = null;
    private static BufferedReader in = null;
    private static PrintWriter out = null;
    private static boolean exit = false;
    private final static String host = "localhost";
    private final static int port = 9877;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Podaj swój nick");

        String nick;
        Scanner sc = new Scanner(System.in);
        nick = sc.nextLine();

        while(!exit){
            showMenu();
            String option;
            System.out.println("Wybierz opcję: ");
            option = sc.nextLine();

            switch (option){
                case "1":
                    play(nick);

                    System.out.println("Za kilka sekund nastąpi powrót do Menu Głównego...");
                    TimeUnit.SECONDS.sleep(5);
                    System.out.println("\n\n\n\n");
                    break;
                case "2":
                    System.out.println("=======================================");
                    System.out.println("Wyniki");
                    System.out.println("=======================================");
                    break;
                case "3":
                    System.out.println("=======================================");
                    System.out.println("Autorino");
                    System.out.println("=======================================");

                    break;
                case "0":
                    System.out.println("=======================================");
                    exit = true;
                    System.out.println("Koniec");
                    break;
                default:
                    System.out.println("Wybrałeś nieistniejącą opcję, spróbuj ponownie!");
            }
        }
    }

    private static void play(String nick){
        try {
            boolean gameOver = false;
            socket = new Socket(host, port);
            System.out.println("Polaczono z serwerem, \nOczekiwanie na drugiego gracza");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            String message;
            do{
                if(in.readLine().equals("+-+")){
                    message = in.readLine();
                    switch (message) {
                        case "PLAYER_DISCONNECTED":{
                            System.out.println("\n=======================================");
                            System.out.println("Przeciwnik sie rozłączył, wygrałeś grę :D");
                            break;
                        }
                        case "START_NICK": {
                            message = in.readLine();
                            System.out.println(message);
                            out.println(nick);
                            out.flush();
                            break;
                        }
                        case "END_NICK": {
                            message = in.readLine();
                            System.out.println(message);
                            break;
                        }
                        case "CONNECTION": {
                            message = in.readLine();
                            System.out.println(message);

                            out.println("CONNECTION_OK");
                            out.flush();
                            break;
                        }
                        case "SCORE": {
                            boolean endScore = false;
                            readMessages(endScore, "END_SCORE");
                            break;
                        }
                        case "GAME_OVER":{
                            gameOver = true;
                            System.out.println("Gra zostala zakonczona");
                            System.out.println("===========================================");
                            socket.close();
                            break;
                        }
                        case "START_OF_QUESTION": {
                            System.out.println("Runda " + roundCounter++);
                            boolean endOfQuestion = false;
                            readMessages(endOfQuestion, "END_OF_QUESTION");
                            break;
                        }
                        case "GIVE_ME_ANSWER": {
                            message = in.readLine();
                            System.out.println(message);
                            Scanner scanner = new Scanner(System.in);
                            String answer = scanner.nextLine();

                            out.println("+-+");
                            out.println(answer.toLowerCase());
                            out.flush();
                            break;
                        }
                        case "ASKED": {
                            boolean asked = false;
                            readMessages(asked,"END_ASKED");
                            try{
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException ex){
                                System.out.println("Problem z odliczaniem czasu");
                            }
                            break;
                        }
                        case "NEXT_QUESTION": {
                            boolean nextQuestion = false;
                            readMessages(nextQuestion,"END_NEXT_QUESTION");
                            try{

                                TimeUnit.SECONDS.sleep(3);
                            } catch (InterruptedException ex){
                                System.out.println("Problem z odliczaniem czasu");
                            }
                            break;
                        }
                    }
                }
                else{
                    System.out.println("Otrzymano zły komunikat ze strony serwera!");
                }
            } while (!gameOver || !socket.isClosed());
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

    private static void readMessages(boolean isTrue, String end) throws IOException {
        while(!isTrue) {
            String msg = in.readLine();
            if(msg.equals(end))
                isTrue = true;
            else
                System.out.println(msg);
        }
    }

    private static void showMenu(){
        System.out.println("\nWitaj w Milionerach");
        System.out.println("1. Graj przez internet z drugim graczem");
        System.out.println("2. Pokaż najlepsze wyniki");
        System.out.println("3. Informacje o autorze");
        System.out.println("0. EXIT\n");
    }
}