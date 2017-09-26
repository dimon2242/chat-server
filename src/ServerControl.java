import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

public class ServerControl implements Runnable {
    Thread thread;
    List<MessageChecker> clients;
    List<Socket> sockets;
    volatile boolean isRun;
    String cmd;
    ConnectorThread connector;
    public ServerControl(List<MessageChecker> clients, List<Socket> sockets, ConnectorThread connector) {
        thread = new Thread(this, "ServerControl Thread");
        this.sockets = sockets;
        this.clients = clients;
        isRun = true;
        this.connector = connector;
        thread.start();
    }

    public void interrupt() {
        isRun = false;

    }

    public void run() {
        while(isRun) {
            try {
                cmd = new BufferedReader(new InputStreamReader(System.in)).readLine();
                if(cmd.length() != 0 && cmd.charAt(0) == '/') {
                    if(cmd.equals("/shutdown")) {
                        connector.interrupt();
                        this.interrupt();
                        System.out.println("Shutdown...");
                    } else
                        System.out.println(cmd + " is wrong command!");
                } else
                    System.out.println(cmd + " is wrong command!");
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
