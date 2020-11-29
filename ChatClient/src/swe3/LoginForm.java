package swe3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginForm extends JFrame {
    private final ChatClient client;
    JTextField LArea =new JTextField();
JPasswordField PArea=new JPasswordField();
JButton Button =new JButton();
public LoginForm(){
    super("Login");
    this.client=new ChatClient("Local Host",8815);
    client.connect();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel y=new JPanel();
    y.setLayout(new BoxLayout(y,BoxLayout.Y_AXIS));
    y.add(LArea);
    y.add(PArea);
    y.add(Button);
    Button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Login();
        }
    });
     getContentPane().add(y, BorderLayout.CENTER);
     pack();
     setVisible(true);

}

    private void Login() {
    String login=LArea.getText();
    String password=PArea.getText();
        try {
            if(client.login(login,password)){
                UserPanel userPanel=new UserPanel(client);
                JFrame frame=new JFrame("UserPanel");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(500,700);
                frame.getContentPane().add(userPanel, BorderLayout.CENTER);
                frame.setVisible(true);

                setVisible(false);
            }else{
                JOptionPane.showMessageDialog(this,"Invalid!!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
    LoginForm loginform =new LoginForm();
}
}
