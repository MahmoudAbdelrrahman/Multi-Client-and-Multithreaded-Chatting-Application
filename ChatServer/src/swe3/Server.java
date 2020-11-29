package swe3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
    private final int serverPort;
    private ArrayList<ServerManager> managerList=new ArrayList<>();
    public Server(int serverPort){
        this.serverPort= serverPort;
    }

    public List<ServerManager> getManagerList() {
        return managerList;
    }

    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while(true) {
                System.out.println("Client Connection Acceptance processing...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client Connection from"+clientSocket+" Accepted.");
                ServerManager manager= new ServerManager(this,clientSocket);
                managerList.add(manager);
                manager.start();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void removeManager(ServerManager serverManager) {
        managerList.remove(serverManager);
    }
}
