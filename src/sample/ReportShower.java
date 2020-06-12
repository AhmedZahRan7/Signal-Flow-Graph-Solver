package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;


public class ReportShower{
    @FXML private javafx.scene.control.TextArea Area;
    @FXML private Button showButton;

    public void printReport(){
        List<String> lines = null;
        try {
            URL url = getClass().getResource("Report.txt");
            File file = new File(url.getPath());
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert lines != null;
        for (String line : lines) Area.appendText(line+"\n");
    }
    public void handelOpen(){
        showButton.setVisible(false);
        printReport();
    }
}
