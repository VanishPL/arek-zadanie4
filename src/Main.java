import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        JFrame f=new JFrame();//creating instance of JFrame




        f.setSize(800,600);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
        JTextArea n = new JTextArea();
        n.setBounds(100,0,699,599);
        f.add(n);

    }
}
