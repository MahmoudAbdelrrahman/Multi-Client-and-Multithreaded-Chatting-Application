package swe3;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class UserPanel extends JPanel implements UserListener{
    private final ChatClient client;
    private JList<String>UserListGUI;
    private DefaultListModel<String>UserModel;
    public UserPanel(ChatClient client) {
    this.client=client;
    this.client.addUserListener(this);
    UserModel=new DefaultListModel<>();
    UserListGUI=new JList<>(UserModel);
    setLayout(new BorderLayout());
    add(new JScrollPane(UserListGUI), BorderLayout.CENTER);
    UserListGUI.addMouseListener(new MouseAdapter(){
        @Override
                public void mouseClicked(MouseEvent e){
            if(e.getClickCount()>1){
               String login= UserListGUI.getSelectedValue();
               MessagePanel messagePanel=new MessagePanel(client,login);
               JFrame x=new JFrame("message"+login);
               x.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
               x.setSize(500,500);
               x.getContentPane().add(messagePanel,BorderLayout.CENTER);
               x.setVisible(true);
            }
            }
        });
    }

    public static void main(String args[]){
        ChatClient client = new ChatClient("LocalHost", 8815);
UserPanel userPanel=new UserPanel(client);
JFrame frame=new JFrame("UserPanel");
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.setSize(500,700);
frame.getContentPane().add(userPanel, BorderLayout.CENTER);
frame.setVisible(true);
if(client.connect()){
    try {
        client.login("guest","guest");
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    }
    @Override
    public void online(String login) {
UserModel.addElement(login);
    }

    @Override
    public void offline(String login) {
        UserModel.removeElement(login);

    }
}
