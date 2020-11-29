package swe3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MessagePanel extends JPanel implements MessageListener{

    private final ChatClient client;
    private final String login;
private DefaultListModel<String>listModel=new DefaultListModel<>();
    private JList<String>messagelist=new JList<>(listModel);
    private JTextField inputfield =new JTextField();
    public MessagePanel(ChatClient client, String login) {
        this.client=client;
        this.login=login;
        client.addMessage(this);
setLayout(new BorderLayout());
add(new JScrollPane(messagelist),BorderLayout.CENTER);
add(inputfield,BorderLayout.SOUTH);
inputfield.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String text=inputfield.getText();
            client.message(login,text);
            listModel.addElement("You: "+text);
            inputfield.setText("");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
});
    }

    @Override
    public void onMessage(String from, String body) {
        if (login.equalsIgnoreCase(from)) {


            String line = from + ": " + body;
            listModel.addElement(line);
        }
    }
}
