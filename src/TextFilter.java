import javax.swing.filechooser.FileFilter;
import java.io.File;

public class TextFilter extends FileFilter {
    //akceptuje wszystkie pliki txt
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.txt)) return true; //jezeli nazwa pliku ma na koncu .txt to sie wyswietla jesli nie to nie
        }

        return false;
    }

    //The description of this filter
    public String getDescription() { return "txt (.txt)"; }
}
