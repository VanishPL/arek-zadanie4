import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.*;

public class Notepad extends JFrame {

    private static final long serialVersionUID = 1L;
    JFrame frame;
    JMenuBar menuBar;
    JMenu file;
    JMenu edit;
    JMenuItem open, newFile,save, exit;
    JMenuItem undo,paste, selectAll ;
    JMenu format;
    JMenu help;
    JFileChooser fileChooser;
    JTextArea textArea;
    JScrollPane scrollArea;
    Clipboard clip ;
    BorderLayout ns;

    Notepad() {
        frame = new JFrame("Notepad Application");
        file = new JMenu("File");
        edit = new JMenu("Edit");
        format = new JMenu("Format");
        help = new JMenu("Help");

        newFile = new JMenuItem("New");
        open = new JMenuItem("Open");
        save = new JMenuItem("Save");
        exit = new JMenuItem("Exit");
        undo = new  JMenuItem("Undo                 Ctrl+Z");
        paste = new JMenuItem("Paste                Ctrl+V");
        selectAll = new JMenuItem("Select All       Ctrl+A ");
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        scrollArea = new JScrollPane(textArea);
        fileChooser = new JFileChooser();
        menuBar = new JMenuBar();
        ns = new BorderLayout();



        frame.setLayout(ns);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(scrollArea);

        file.add(open); //zdefiniowanie JMenu'sów
        file.add(newFile);
        file.add(save);
        file.add(exit);
        edit.add(undo);
        edit.add(paste);
        edit.add(selectAll);
        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(format);
        menuBar.add(help);

        frame.setJMenuBar(menuBar); //zdefiniowanie JMenuBar

        OpenListener openL = new OpenListener(); // zdefiniowanie i ustawienie nasluchiwaczy
        NewListener NewL = new NewListener();
        SaveListener saveL = new SaveListener();
        ExitListener exitL = new ExitListener();
        open.addActionListener(openL);
        newFile.addActionListener(NewL);
        save.addActionListener(saveL);
        exit.addActionListener(exitL);
        //UndoListener UndoL = new UndoListener();
        PasteListener pasteL = new PasteListener(); // ustawienie nasluchiwacza wklejenia z clipboard
        //EditListener EditL = new EditListener();
        //SelectListener SelectL = new SelectListener();
        //undo.addActionListener(UndoL);
        //paste.addActionListener(EditL);
        //selectAll.addActionListener(SelectL);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    class OpenListener implements ActionListener { // nasluchwiacz otwarcia
        public void actionPerformed(ActionEvent e) {
            if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(frame)) {
                File file = fileChooser.getSelectedFile();
                if(file.getName().endsWith(".txt")) { //sprawdzenie czy jest rozszerzenie txt
                    textArea.setText("");
                    Scanner in = null;
                    try {
                        in = new Scanner(file);
                        while (in.hasNext()) {
                            String line = in.nextLine();
                            textArea.append(line + "\n"); //wczytanie calego pliku do JTextArea
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        in.close();
                    }
                }
            }
        }
    }

    class SaveListener implements ActionListener { // nasluchiwacz zapisu
        public void actionPerformed(ActionEvent e) {
            if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(frame)) {
                File file = fileChooser.getSelectedFile();
                PrintWriter out = null;
                if(!file.getName().endsWith(".txt")) file //sprawdzenie czy jest rozszerzenie txt
                try {
                    out = new PrintWriter(file);
                    String output = textArea.getText();
                    System.out.println(output);
                    out.println(output);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        out.flush();
                    } catch(Exception ex1)
                    {

                    }
                    try {
                        out.close();
                    } catch(Exception ex1) {

                    }
                }
            }
        }
    }

    class NewListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            textArea.setText("");
            //frame.add(newFile);
            //textArea.(newFile+"\n");



        }
    }
    class ExitListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }



    class PasteListener implements ActionListener { // nasluchiwacz wklejania
        public void actionPerformed(ActionEvent e) {
            Transferable cliptran = clip.getContents(Notepad.this);
            try
            {
                String sel = (String) cliptran.getTransferData(DataFlavor.stringFlavor); //program posiada kopie clipboard
                textArea.replaceRange(sel,textArea.getSelectionStart(),textArea.getSelectionEnd()); //program zamienia zaznaczony tekst na kopie z clipboard
            }
            catch(Exception exc)
            {
                System.out.println("not string flavour");
            }

        }
    }


    public static void main(String args[]) {
        Notepad n = new Notepad();
    }
}