package swe3;




import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class ServerManager extends Thread{


    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> topicSet= new HashSet<>();

    public ServerManager(Server server, Socket clientSocket) {
        this.server=server;
        this.clientSocket = clientSocket;
    }
    public void run(){
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = reader.readLine()) != null){
            String[] tokens = StringUtils.split(line);
            if(tokens != null && tokens.length>0){
                String cmd = tokens[0];
                if ("logout".equalsIgnoreCase(cmd)||"quit".equalsIgnoreCase(cmd)) {
                    handleLogout();
                    break;
                } else if("login".equalsIgnoreCase(cmd)){
                    handleLogin(outputStream,tokens);
                }
                else if ("msg".equalsIgnoreCase(cmd)){
                    String[] msgToken = StringUtils.split(line, null,3);
                    handleMessage(msgToken);
                }
                else if("join".equalsIgnoreCase(cmd)){
                    handleJoin(tokens);
                }
                else if("leave".equalsIgnoreCase(cmd)){
                    handleLeave(tokens);
                }
                else{
                    String msg= "Unknown Command " + cmd+ "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }
        clientSocket.close();
    }

    private void handleLeave(String[] tokens) {
        if (tokens.length >1){
            String topic= tokens[1];
            topicSet.remove(topic);
        }
    }

    public boolean isMemberOfTopic(String topic){
        return topicSet.contains(topic);
    }

    private void handleJoin(String[] tokens) {
        if (tokens.length >1){
            String topic= tokens[1];
            topicSet.add(topic);
        }
    }

    private void handleMessage(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String msgBody = tokens[2];

        boolean isTopic= sendTo.charAt(0) =='#';
        List<ServerManager> managerList=server.getManagerList();
        for(ServerManager manager:managerList){
            if(isTopic){
                if(manager.isMemberOfTopic(sendTo)){
                    String outMsg = "msg "+ sendTo+ ":"+ login + " " + msgBody + "\n";
                    manager.send(outMsg);
                }
            } else {
                if (sendTo.equalsIgnoreCase(manager.getLogin())) {
                    String outMsg = "msg " + login + " " + msgBody + "\n";
                    manager.send(outMsg);
                }
            }
        }
    }

    // send other online users the Offline status
    private void handleLogout() throws IOException {
        server.removeManager(this);
        List<ServerManager>managerList= server.getManagerList();
        String onlineMsg = login +" is Now Offline.\n";
        for(ServerManager manager : managerList){
            if(!login.equals(manager.getLogin())) {
                manager.send(onlineMsg);
            }
        }
        clientSocket.close();
    }

    public String getLogin(){
        return login;
    }
    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if(tokens.length==3){
            String login=tokens[1];
            String password = tokens[2];

            if((login.equals("karim")&& password.equals("karim")) || (login.equals("7oda")&& password.equals("7oda"))){
                String msg= "Logged in Successfully!\n";
                outputStream.write(msg.getBytes());
                this.login= login;
                System.out.println("User Logged in Successfully: "+ login+"\n");
                List<ServerManager>managerList= server.getManagerList();
                // here we send current user all other online users
                for(ServerManager manager : managerList){
                        if(manager.getLogin() != null) {
                            if(!login.equals(manager.getLogin())) {
                                String msg2 = manager.getLogin()+ " is Now Online.\n";
                                send(msg2);
                            }
                        }
                }
                // send other online users status
                String onlineMsg = login + " is Now Online.\n";
                for(ServerManager manager : managerList){
                    if(!login.equals(manager.getLogin())) {
                        manager.send(onlineMsg);
                    }
                }
            }
            else{
                String msg= "Incorrect Username or Password.\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for user: "+login);
            }
        }
    }

    private void send(String msg) throws IOException {
        if(login != null) {
            outputStream.write(msg.getBytes());
        }
    }
}
