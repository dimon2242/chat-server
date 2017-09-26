import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String args[]) {
        List<Socket> sockets = new ArrayList<>();
        List<MessageChecker> clients = new ArrayList<>();
        ConnectorThread cT = new ConnectorThread(sockets, clients);
        ServerControl serverControl = new ServerControl(clients, sockets, cT);
    }
}
