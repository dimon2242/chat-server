import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class ConnectorThread implements Runnable {
    Thread thread;
    List<Socket> socketList;
    List<MessageChecker> clients;
    ServerSocket serverSocket;
    volatile boolean isRun;
    Socket commonSocket;

    public ConnectorThread(List<Socket> socketList, List<MessageChecker> clients) {
        this.socketList = socketList;
        this.clients = clients;
        try {
            serverSocket = new ServerSocket(Consts.ConnectionConfig.PORT);
        } catch(IOException e) {
            e.printStackTrace();
        }
        isRun = true;
        thread = new Thread(this, "Connector thread");
        thread.start();
    }

    public void interrupt() {
        isRun = false;
        for(MessageChecker client : clients)
            client.interrupt();
        try {
            serverSocket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Starting server...");
        while(isRun) {
            try {
                commonSocket = serverSocket.accept();

                socketList.add(commonSocket);
                clients.add(new MessageChecker(commonSocket, socketList.size(), socketList, clients));
                System.out.println("Connected! " + Integer.toString(socketList.size()));
            } catch(SocketException e) {

            } catch(IOException e) {
                e.printStackTrace();
            }
        }

    }
}
