import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class MessageChecker implements Runnable {
    Thread thread;
    BufferedReader br;
    BufferedWriter bw;
    Socket socket;
    int counter;
    String msg;
    String username;
    List<Socket> sockets;
    List<MessageChecker> clients;
    String castMsg;
    volatile boolean isRun;

    public MessageChecker(Socket socket, int counter, List<Socket> sockets, List<MessageChecker> clients) {
        thread = new Thread(this, "Message Checker Thread");
        this.counter = counter;
        this.socket = socket;
        this.sockets = sockets;
        this.clients = clients;
        isRun = true;
        msg = null;
        castMsg = null;
        username = Integer.toString(counter);
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch(IOException e) {
            e.printStackTrace();
        }
        thread.start();
    }

    public void interrupt() {
        isRun = false;
        try {
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void wrongCommand(String str) {
        castMsg = str + " is not command!";
        sendCastMsg();
        System.out.println(castMsg);
    }

    private void sendCastMsg() {
        synchronized (sockets) {
            try {
                for (Socket socket : sockets) {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bw.write(castMsg + '\n');
                    bw.flush();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPrivateMsg(Socket target) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(target.getOutputStream()));
            bw.write(castMsg + '\n');
            bw.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (isRun) {
            try {
                msg = br.readLine();
                if(!socket.getInetAddress().isReachable(5000)) {
                    castMsg = username + " is disconnected!";
                    System.out.println(castMsg);
                    synchronized (sockets) {
                        sockets.remove(socket);
                    }
                    synchronized (clients) {
                        clients.remove(this);
                    }
                    break;
                } else
                if(msg.charAt(0) == ('/')) {
                    if(msg.equals("/exit")) {
                        castMsg = username + " is exit!";

                        System.out.println(castMsg);
                        sendCastMsg();
                        synchronized (sockets) {
                            sockets.remove(socket);
                        }
                        synchronized (clients) {
                            clients.remove(this);
                        }
                        break;
                    } else if(msg.contains("/uname ")) {
                        String newUserame = msg.substring(msg.indexOf(" ") + 1, msg.length());
                        if(newUserame.equals(""))
                            wrongCommand(newUserame);
                        else {
                            castMsg = username + " has changing name to " + newUserame;
                            System.out.println(castMsg);
                            sendCastMsg();
                            username = newUserame;
                        }

                    } else if(msg.equals("/ucount")) {
                        castMsg = "Users: " + Integer.toString(sockets.size());
                        sendPrivateMsg(socket);
                        System.out.println(castMsg);
                    } else
                        wrongCommand(msg);
                } else {
                    castMsg = username + ": " + msg;
                    sendCastMsg();
                    System.out.println(castMsg);
                }


            } catch(SocketException e) {

            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
