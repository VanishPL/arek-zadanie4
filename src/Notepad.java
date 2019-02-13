

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
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
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
    JTextField statusBarText, countWords;
    final Clipboard clipboard =
            Toolkit.getDefaultToolkit().getSystemClipboard();

    Notepad() {
        statusBar = new JPanel(new GridLayout(1,2));
        countWords = new JTextField();
        statusBarText = new JTextField();
        countWords.setText("Symbols: ");
        statusBar.add(statusBarText);
        statusBar.add(countWords);
        frame = new JFrame("Notepad Application");
        file = new JMenu("File");//tworzenie kategori w menu
        edit = new JMenu("Edit");
        format = new JMenu("Format");
        help = new JMenu("Help");

        newFile = new JMenuItem("New"); //tworzenie pokoleii przyciskow.
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
        textArea.setLineWrap(true); // zawijanie wierszy
        scrollArea = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //ustawinie scrolla z boku tekstu. Ma sie zawsze wyswietlac vertykalnie i nigdy na szerokosc
        fileChooser = new JFileChooser(); // nowy obiekt do wyboru plikow
        menuBar = new JMenuBar();
        ns = new BorderLayout();


        frame.setLayout(ns);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(scrollArea);
        frame.add(statusBar, BorderLayout.PAGE_END);

        file.add(open); //zdefiniowanie JMenu'sów
        file.add(newFile); //dodanie do menu wszystkich przyciskow
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

        frame.setJMenuBar(menuBar); //dodanie menu do okienka
        fileChooser.addChoosableFileFilter(new TextFilter());//filter do rodzajów plików akceptowalnych przez dialog otwarcia i zapisania pliku (pasek na dole z txt(.txt) )
        fileChooser.setAcceptAllFileFilterUsed(false);//czy na tym pasku ma sie pojawiac opcja wszystkich plikow (false - nie)

        OpenListener openL = new OpenListener(); // zdefiniowanie i ustawienie nasluchiwaczy
        NewListener NewL = new NewListener();
        SaveListener saveL = new SaveListener();
        ExitListener exitL = new ExitListener();
        ChangeFont changeL = new ChangeFont();
        ChangeColor changeC = new ChangeColor();
        FindWord findL = new FindWord();
        SelectAllListener selectAllL = new SelectAllListener();
        PasteListener pasteL = new PasteListener(); // ustawienie nasluchiwacza wklejenia z clipboard
        CountWords countWordsL = new CountWords();

        open.addActionListener(openL);
        newFile.addActionListener(NewL);
        save.addActionListener(saveL);
        exit.addActionListener(exitL);
        font.addActionListener(changeL);
        colorChange.addActionListener(changeC);
        find.addActionListener(findL);
        selectAll.addActionListener(selectAllL);
        paste.addActionListener(pasteL);
        textArea.addCaretListener(countWordsL);
        //UndoListener UndoL = new UndoListener();

        //EditListener EditL = new EditListener();
        //SelectListener SelectL = new SelectListener();
        //undo.addActionListener(UndoL);
        //paste.addActionListener(EditL);
        statusBarText.setText("New Notepad");
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    class CountWords implements CaretListener { //klasa do nasłuchu gdy w polu text.area pojawi sie jakiś znak

        @Override
        public void caretUpdate(CaretEvent e) {
            countWords.setText("Words: " + String.valueOf(textArea.getText().length())); //ustawienie countera na ilość obecnych słów
        }
    }

    class SelectAllListener implements ActionListener { // klasa do nasłuchu przycisku select all
        public void actionPerformed(ActionEvent e){
            textArea.setSelectionStart(0);
            textArea.setSelectionEnd(textArea.getText().length());
            statusBarText.setText("Selected all");
        }
    }


    class OpenListener implements ActionListener { // klasa do nasluchu przycisku otwarcia pliku
        public void actionPerformed(ActionEvent e) {
            if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(frame)) {
                File file = fileChooser.getSelectedFile(); //utworzenie obiektu i przypisanie mu wybranego pliku
                    textArea.setText("");
                    Scanner in = null; //zdeklarowanie obiektu scanner który posłuży do odczytania plikiu
                    try {
                        in = new Scanner(file);//utworzenie obiektu który wczytuje plik zawarty w obiekcie file
                        while (in.hasNext()) { //dopóki file ma nastepny znak
                            String line = in.nextLine(); //wczytanie linijki z file
                            textArea.append(line + "\n"); //wczytanie calego pliku do JTextArea
                        }
                        statusBarText.setText("Opened file"); //ustawienie statusu
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        in.close();
                    }

            }
        }
    }

    class SaveListener implements ActionListener { //klasa do nasluchu przycisku zapisania
        public void actionPerformed(ActionEvent e) {
            if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(frame)) { //wczytanie okienka dialogowego z wszystkimi warunkami ktore ma spełnic (wyswietlac tylko pliki txt)
                File file = fileChooser.getSelectedFile();
                PrintWriter out = null; //zdeklarowanie obiektu podobnego do scanner tyle ze dzialajacego w druga strone (zapisującego)
                try { //obsluga wyjatkow
                    if(!fileChooser.accept(file)) out = new PrintWriter(file + ".txt");  //sprawdzenie czy użytkownik dodał rozszerzenie .txt na koncu pliku jeśli nie to je dodajemy
                    else out = new PrintWriter(file); //zawsze zwracaj plik z rozszerzeniem txt
                    String output = textArea.getText();
                    out.println(output); //wypisanie do wybranego pliku tekstu z pola
                    statusBarText.setText("Directory of saved file:  " + file.getAbsolutePath()); //ustawienie statusu
                } catch (Exception ex) { //lap wyjatki
                    ex.printStackTrace();
                } finally {
                    try {
                        out.flush(); // wyczysc buffor
                    } catch(Exception ex1)
                    {

                    }
                    try {
                        out.close(); // zamkniecie i zapisanie pliku
                    } catch(Exception ex1) {

                    }
                }
            }
        }

    }

    class NewListener implements ActionListener { //nasluchiwacz do przycisku nowy
        public void actionPerformed(ActionEvent e) {
            textArea.setText(""); //ustawienie pola na pusty
            //frame.add(newFile);
            //textArea.(newFile+"\n");
            statusBarText.setText("Created new file"); // zmiana statusu


        }
    }
    class ExitListener implements ActionListener { //klasa do nasluchwiania przycisu exit
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        } // wyjscie z programu
    }



    class PasteListener implements ActionListener { //klasa do nasluchu przycisku paste
        public void actionPerformed(ActionEvent e) {
            Transferable cliptran = clipboard.getContents(clipboard); //obiekt przechowujacy aktualna zawartość schowka systemowego
            try
            {
                String sel = (String) cliptran.getTransferData(DataFlavor.stringFlavor); //kopiujemy do stringa zawartość schowka
                textArea.replaceRange(sel,textArea.getSelectionStart(),textArea.getSelectionEnd()); //zmiana zaznaczanego tekstu na zawartosc schowka
                statusBarText.setText("Pasted clipboard to file"); //ustawienie statusu
            }
            catch(Exception exc)
            {
                System.out.println("Nie jest to tekst");
            }

        }
    }

    class ChangeFont implements ActionListener {//klasa do nasluchu przycisku change font
        JButton colorButton;
        JComboBox<String> nes;
        JFrame chan;
        String fonts[] =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(); //pobranie nazw wszystkich dostępnych czcionek z komputera do tablicy stringow
        SpinnerNumberModel model1 = new SpinnerNumberModel(12, 0, 60, 1); //ustawienie modelu(zawartosci) spinner boxa. podstawowa wartość - 12, min - 0, max - 60, krok - 1
        JSpinner size;
        JRadioButton radioButton;
        Color newColor;
        int fontStyle, fontIndex;
        public void actionPerformed(ActionEvent e){
            GridLayout gird = new GridLayout(2,2); //Nowy obiekt grid layout dzielący okienko na siatke. W tym przypadku na 2 kolumny i 2 rzędy

            chan = new JFrame("CHANGE FONT"); //Obiekt nowego okienka
            colorButton = new JButton("Change color");
            nes = new JComboBox<>(fonts); //nowy combo box z wszystkimi czcionkami z fonts
            size = new JSpinner(model1); // dodanie modelu spinera do jego reprezentacji graficznej
            radioButton = new JRadioButton("BOLD"); //nowy radio button


            frame.setEnabled(false);//zablokowanie głównego okna


            chan.setLocation(getX()+100, getY()+100); // ustawianie kraftowego okna dialogowego i jego rozmieszczenia. Miejsce to położenie głównego okna + 100px
            chan.setSize(400, 200); //rozmiar okienka do zmianny czcionki
            chan.setVisible(true); // ustawienie okienka jako widzialnego
            chan.setLayout(gird); // nadanie okienku obiektu siatki
            chan.addWindowListener(new WA()); //obsluga zdarzenia zamkniecia okna


            chan.add(nes); // dodanie do okienka wszystkich przyciskow typu Bold czy tez zmianny rozmiaru czcionki
            chan.add(colorButton);
            chan.add(size);
            chan.add(radioButton);

            Bold bold = new Bold(); //nowy obiekt bold ktory zostanie uzyty do naskluchu
            TextColorChange textColorChange = new TextColorChange(); //to samo co wyzej

            radioButton.addActionListener(bold); //ustawienie nasluchiwania zmian
            colorButton.addActionListener(textColorChange);

        }

        class WA extends WindowAdapter {//klasa do nasluchu przycisku X w gornym prawym rogu okienka zostal klikniety na okienku
            public void windowClosing(WindowEvent e){
                setVisible(false); // ustawienie niewidzialnosci okienka
                fontS = new Font(fonts[nes.getSelectedIndex()], fontStyle, model1.getNumber().intValue()); //utworzenie nowego obiektu czcionki (nazwa czcionki, czy ma byc gruba, jej rozmiar)
                statusBarText.setText("Changed font");
                textArea.setFont(fontS);//ustawienie w textArea tej nowej czcionki
                textArea.setForeground(newColor);//utawienie koloru czcionki wybranego wczesniej w change color
                frame.setEnabled(true);
            }
        }

        class Bold implements ActionListener { //klasa do nasluchiwania czy radiobutton bold zostal wcisniety
            public void actionPerformed(ActionEvent e){
                if(radioButton.isSelected()){
                    fontStyle = Font.BOLD;
                }else{
                    fontStyle = Font.PLAIN;
                }
            }
        }

        class TextColorChange implements ActionListener {//klasa do nasluchiwania przycisku text color change
            public void actionPerformed(ActionEvent e){
                newColor = JColorChooser.showDialog(textArea, "Choose Background", textArea.getBackground()); // wywolanie dialogu kolorow ktory jest zawarty w swingu
            }
        }

    }

    class ChangeColor implements ActionListener {//klasa do nasluchu zmiany koloru tla
        public void actionPerformed(ActionEvent e){
            textArea.setBackground(JColorChooser.showDialog(textArea, "Choose Background", textArea.getBackground())); //wywolanie dialou kolorow i ustwienie koloru tla
            statusBarText.setText("Changed color of background");
        }
    }

    class FindWord implements ActionListener { //klasa nasluchiwacz do przycisku find
        JFrame chan;
        JPanel comboPane, accCan;
        JTextArea textField;
        JButton acceptButton, cancelButton;
        Highlighter h; //klasa do poswietlenia textu
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

            gird2.setVgap(20); // przerwa pomiedzy dwoma obiektami siatki
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

            chan.add(comboPane);//podzielenie okienka na dwa rzedy w ktorych znajduja sie kolejne obiekty
            // 1. siatka jedno rzedowa i kolumnowa z polem tekstowym w srodku
            // 2. siatka 2 kolumnowa w ktorej znajduja sie 2 przyciski accept i cancel
            chan.add(accCan);

            acceptButton.addActionListener(new Accept());
            cancelButton.addActionListener(new Cancel());
        }

        class WA extends WindowAdapter {//klasa do nasluchu przycisku X w gornym prawym rogu okienka zostal klikniety na okienku
            public void windowClosing(WindowEvent e){
                chan.setVisible(false);
                h.removeAllHighlights(); //przy zamknieciu usun wszystkie podswietlenia
            }
        }

        class Accept implements ActionListener {
            public void actionPerformed(ActionEvent e){
                String temp = textField.getText();
                h = textArea.getHighlighter();
                String searchArea[] = textArea.getText().split(" ");//rozdzielenie slow w tekscie (slowo konczy sie znakiem spacji)
                h.removeAllHighlights(); // usun wszystkie podswietlenia
                for(String n : searchArea){ //dla kazdego slowa w tekscie

                    if(n.contains(temp)){//jesli slowo zawiera ciag znaków taki jak w find
                        try {
                            h.addHighlight(
                                    textArea.getText().indexOf(n)+n.indexOf(temp),  //ustawienie poczatku podwietlenia na poczatek slowa ktore aktualnie przegladamy + poczatek gdzie wystepuje ciag
                                    textArea.getText().indexOf(n)+n.indexOf(temp)+temp.length(), // dodanie do poczatku podswietlenia dlugosci ciagu znakow
                                    new DefaultHighlighter.DefaultHighlightPainter(new Color(31, 233, 238))); //nowy podstawowy graficzny malarz stylu do podswietlenia. W srodku ustawiamy kolor r, g, b
                            statusBarText.setText("Found text in area"); //ustawienie stanu
                        }catch(Exception es){
                            es.printStackTrace();
                        }
                    }
                }

            }
        }

        class Cancel implements ActionListener {//klasa do nasluchu przycisku cancel
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