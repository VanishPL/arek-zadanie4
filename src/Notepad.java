

import com.sun.org.apache.xml.internal.serializer.utils.Utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.xml.soap.Text;

public class Notepad extends JFrame {

    private static final long serialVersionUID = 1L;
    JFrame frame;
    JPanel statusBar;
    JMenuBar menuBar;
    JMenu file;
    JMenu edit;
    JMenuItem open, newFile,save, exit;
    JMenuItem undo,paste, selectAll, find;
    JMenu format;
    JMenuItem font,colorChange;
    JMenu help;
    JFileChooser fileChooser;
    JTextArea textArea;
    JScrollPane scrollArea;
    BorderLayout ns;
    Font fontS;
    JTextField statusBarText;
    final Clipboard clipboard =
            Toolkit.getDefaultToolkit().getSystemClipboard();

    Notepad() {
        statusBar = new JPanel(new GridLayout(1,1));

        statusBarText = new JTextField();
        statusBar.add(statusBarText);
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
        font = new JMenuItem("Change font");
        find = new JMenuItem("Find      Ctrl+F");
        colorChange = new JMenuItem("Change color of area");

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        scrollArea = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fileChooser = new JFileChooser();
        menuBar = new JMenuBar();
        ns = new BorderLayout();


        frame.setLayout(ns);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(scrollArea);
        frame.add(statusBar, BorderLayout.PAGE_END);

        file.add(open); //zdefiniowanie JMenu'sów
        file.add(newFile);
        file.add(save);
        file.add(exit);
        edit.add(undo);
        edit.add(paste);
        edit.add(selectAll);
        edit.add(find);
        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(format);
        menuBar.add(help);
        format.add(font);
        format.add(colorChange);

        frame.setJMenuBar(menuBar); //zdefiniowanie JMenuBar
        fileChooser.addChoosableFileFilter(new TextFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        OpenListener openL = new OpenListener(); // zdefiniowanie i ustawienie nasluchiwaczy
        NewListener NewL = new NewListener();
        SaveListener saveL = new SaveListener();
        ExitListener exitL = new ExitListener();
        ChangeFont changeL = new ChangeFont();
        ChangeColor changeC = new ChangeColor();
        FindWord findL = new FindWord();
        SelectAllListener selectAllL = new SelectAllListener();
        PasteListener pasteL = new PasteListener(); // ustawienie nasluchiwacza wklejenia z clipboard

        open.addActionListener(openL);
        newFile.addActionListener(NewL);
        save.addActionListener(saveL);
        exit.addActionListener(exitL);
        font.addActionListener(changeL);
        colorChange.addActionListener(changeC);
        find.addActionListener(findL);
        selectAll.addActionListener(selectAllL);
        paste.addActionListener(pasteL);
        //UndoListener UndoL = new UndoListener();

        //EditListener EditL = new EditListener();
        //SelectListener SelectL = new SelectListener();
        //undo.addActionListener(UndoL);
        //paste.addActionListener(EditL);
        statusBarText.setText("New Notepad");
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    class SelectAllListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            textArea.setSelectionStart(0);
            textArea.setSelectionEnd(textArea.getText().length());
            statusBarText.setText("Selected all");
        }
    }


    class OpenListener implements ActionListener { // nasluchwiacz otwarcia
        public void actionPerformed(ActionEvent e) {
            if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(frame)) {
                File file = fileChooser.getSelectedFile();
                //sprawdzenie czy jest rozszerzenie txt
                    textArea.setText("");
                    Scanner in = null;
                    try {
                        in = new Scanner(file);
                        while (in.hasNext()) {
                            String line = in.nextLine();
                            textArea.append(line + "\n"); //wczytanie calego pliku do JTextArea
                        }
                        statusBarText.setText("Opened file");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        in.close();
                    }

            }
        }
    }

    class SaveListener implements ActionListener { // nasluchiwacz zapisu
        public void actionPerformed(ActionEvent e) {
            if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(frame)) {
                File file = fileChooser.getSelectedFile();
                PrintWriter out = null;
                try {
                    if(!fileChooser.accept(file)) out = new PrintWriter(file + ".txt");
                    else out = new PrintWriter(file); //zawsze zwracaj plik z rozszerzeniem txt
                    String output = textArea.getText();
                    out.println(output);
                    statusBarText.setText("Directory of saved file:  " + file.getAbsolutePath());
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
            statusBarText.setText("Created new file");


        }
    }
    class ExitListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }



    class PasteListener implements ActionListener { // nasluchiwacz wklejania
        public void actionPerformed(ActionEvent e) {
            Transferable cliptran = clipboard.getContents(clipboard);
            try
            {
                String sel = (String) cliptran.getTransferData(DataFlavor.stringFlavor); //program posiada kopie clipboard
                textArea.replaceRange(sel,textArea.getSelectionStart(),textArea.getSelectionEnd()); //program zamienia zaznaczony tekst na kopie z clipboard
                statusBarText.setText("Pasted clipboard to file");
            }
            catch(Exception exc)
            {
                System.out.println("Nie jest to tekst");
            }

        }
    }

    class ChangeFont implements ActionListener {
        JButton colorButton;
        JComboBox<String> nes;
        JFrame chan;
        String fonts[] =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        SpinnerNumberModel model1 = new SpinnerNumberModel(12, 0, 60, 1);
        JSpinner size;
        JRadioButton radioButton;
        Color newColor;
        int fontStyle, fontIndex;
        public void actionPerformed(ActionEvent e){
            GridLayout gird = new GridLayout(2,2);

            chan = new JFrame("CHANGE FONT");
            colorButton = new JButton("Change color");
            nes = new JComboBox<>(fonts);
            size = new JSpinner(model1);
            radioButton = new JRadioButton("BOLD");


            frame.setEnabled(false);


            chan.setLocation(getX()+100, getY()+100); // ustawianie kraftowego okna dialogowego i jego rozmieszczenia
            chan.setSize(400, 200);
            chan.setVisible(true);
            chan.setLayout(gird);
            chan.addWindowListener(new WA()); //obsluga zdarzenia zamkniecia okna


            chan.add(nes);
            chan.add(colorButton);
            chan.add(size);
            chan.add(radioButton);

            Bold bold = new Bold();
            TextColorChange textColorChange = new TextColorChange();

            radioButton.addActionListener(bold);
            colorButton.addActionListener(textColorChange);

        }

        class WA extends WindowAdapter {
            public void windowClosing(WindowEvent e){
                setVisible(false);
                fontS = new Font(fonts[nes.getSelectedIndex()], fontStyle, model1.getNumber().intValue());
                statusBarText.setText("Changed font");
                textArea.setFont(fontS);
                textArea.setForeground(newColor);
                frame.setEnabled(true);
            }
        }

        class Bold implements ActionListener { //obsluga radio button - BOLD
            public void actionPerformed(ActionEvent e){
                if(radioButton.isSelected()){
                    fontStyle = Font.BOLD;
                }else{
                    fontStyle = Font.PLAIN;
                }
            }
        }

        class TextColorChange implements ActionListener {
            public void actionPerformed(ActionEvent e){
                newColor = JColorChooser.showDialog(textArea, "Choose Background", textArea.getBackground());
            }
        }

    }

    class ChangeColor implements ActionListener {
        public void actionPerformed(ActionEvent e){
            textArea.setBackground(JColorChooser.showDialog(textArea, "Choose Background", textArea.getBackground()));
            statusBarText.setText("Changed color of background");
        }
    }

    class FindWord implements ActionListener {
        JFrame chan;
        JPanel comboPane, accCan;
        JTextArea textField;
        JButton acceptButton, cancelButton;
        Highlighter h;
        public void actionPerformed(ActionEvent e){
            GridLayout gird = new GridLayout(2,1);
            GridLayout gird1 = new GridLayout(1,2);
            GridLayout gird2 = new GridLayout(1,1);
            chan = new JFrame("FIND WORD");
            comboPane = new JPanel();
            acceptButton = new JButton("Accept");
            cancelButton = new JButton("Cancel");
            textField = new JTextArea();
            textField.setLineWrap(true);

            gird2.setVgap(20);
            comboPane.setLayout(gird2);
            comboPane.add(textField);

            //gird.setVgap(20);
            accCan = new JPanel();
            accCan.setLayout(gird1);
            accCan.add(acceptButton);
            accCan.add(cancelButton);

            chan.setLocation(getX()+100, getY()+100); // ustawianie kraftowego okna dialogowego i jego rozmieszczenia
            chan.setSize(400, 200);
            chan.setVisible(true);
            chan.setLayout(gird);
            chan.addWindowListener(new WA()); //obsluga zdarzenia zamkniecia okna

            chan.add(comboPane);
            chan.add(accCan);

            acceptButton.addActionListener(new Accept());
            cancelButton.addActionListener(new Cancel());
        }

        class WA extends WindowAdapter {
            public void windowClosing(WindowEvent e){
                chan.setVisible(false);
                h.removeAllHighlights();
            }
        }

        class Accept implements ActionListener {
            public void actionPerformed(ActionEvent e){
                String temp = textField.getText();
                h = textArea.getHighlighter();
                String searchArea[] = textArea.getText().split(" ");
                h.removeAllHighlights();
                for(String n : searchArea){
                    System.out.println(n);
                    if(n.contains(temp)){
                         System.out.println(n.indexOf(temp));
                        try {
                            h.addHighlight(textArea.getText().indexOf(n)+n.indexOf(temp), textArea.getText().indexOf(n)+n.indexOf(temp)+temp.length(), new DefaultHighlighter.DefaultHighlightPainter(new Color(31, 233, 238))); // podswietlanie
                            statusBarText.setText("Found text in area");
                        }catch(Exception es){
                            es.printStackTrace();
                        }
                    }
                }

            }
        }

        class Cancel implements ActionListener {
            public void actionPerformed(ActionEvent e){
                h.removeAllHighlights();
                chan.setVisible(false);
            }
        }
    }

    public static void main(String args[]) {
        Notepad n = new Notepad();
    }
}