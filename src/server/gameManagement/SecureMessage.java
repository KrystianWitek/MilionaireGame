package server.gameManagement;

public class SecureMessage {
    private final static String msgBeggining = "+-+";
    private final static String msgEnding = "-+-";

    public String sendSecuredMessageToServer(String msg){
        return msgBeggining + msg + msgEnding;
    }
}
