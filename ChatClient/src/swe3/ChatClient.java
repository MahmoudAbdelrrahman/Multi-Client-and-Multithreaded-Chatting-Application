package swe3;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public  class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;
    private ArrayList<UserListener> userListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String args[]) throws IOException {
        ChatClient client = new ChatClient("localhost", 8815);
        client.addUserListener(new UserListener() {
            @Override
            public void online(String login) {
                System.out.println("User online: " +login);
            }

            @Override
            public void offline(String login) {
                System.out.println("User offline: "+login);
            }
        });
        client.addMessage(new MessageListener() {
            @Override
            public void onMessage(String from, String body) {
                System.out.println("You have a message from "+from+" And it says: "+body);
            }
        });
        if (!client.connect()) {
            System.err.println("Connection failed");
        }
        else {
            System.out.println("Connection succeeded");

            if (client.login("karim", "karim")) {
                System.out.println("Logged in Successfully!");
                client.message("7oda","Hello mahmoud");
            } else {
                System.out.println("Incorrect Username or Password.");
            }
            //client.logout();
        }
    }

    private void message(String sendTo, String messageBody) throws IOException {
        String message= "message" + sendTo + " " + messageBody+"\n";
        serverOut.write(message.getBytes());
    }

    private boolean login(String username, String password) throws IOException {
        String signIn = "Login" + username + " " + password;
        serverOut.write(signIn.getBytes());
        String response = bufferedIn.readLine();
        System.out.println("Response: " + response);
        if ("Logged in Successfully!".equalsIgnoreCase(response)) {
            startMessageReader();
            return true;
        } else {
            return false;

        }

    }

    private void logout() throws IOException {
        String cmd ="logout\n";
        serverOut.write(cmd.getBytes());
    }

    private void startMessageReader(){
        Thread k=new Thread(){
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        k.start();
    }
    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if("online".equalsIgnoreCase(cmd)){
                        handleOnline(tokens);
                    }else if("offline".equalsIgnoreCase(cmd)){
                        handleOffline(tokens);
                    }else if("Message".equalsIgnoreCase(cmd)){
                        String[] msgToken = StringUtils.split(line, null,3);
                        handleMessage(msgToken);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try{
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }

    private void handleMessage(String[] msgToken) {
        String login=msgToken[1];
        String body=msgToken[2];
        for(MessageListener messageListener:messageListeners){
            messageListener.onMessage(login,body);
        }
    }

    private void handleOffline(String[] tokens) {
        String login=tokens[1];
        for(UserListener user:userListeners){
            user.offline(login);
        }
    }
    private void handleOnline(String[] tokens) {
        String login=tokens[1];
        for(UserListener user:userListeners){
            user.online(login);
        }
    }

    private boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is: " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void addUserListener(UserListener user){
        userListeners.add(user);
    }
    public void removeUserListener(UserListener user){
        userListeners.remove(user);
    }
    public void addMessage(MessageListener messageListener){
        messageListeners.add(messageListener);
    }
    public void removeMessage(MessageListener messageListener){
        messageListeners.remove(messageListener);
    }
}