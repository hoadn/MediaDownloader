import javax.swing.*;
import java.awt.*;

/**
 * Creation time: 16:00
 * Created by Dominik on 04.06.2015.
 */
public class NowVideoDownloadWindow extends JDialog {
    private JProgressBar progressBar;
    private String url;

    public NowVideoDownloadWindow(String url){
        this.url = url;
        setTitle("NowVideo - Download Progress");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        AddComponents();
        setSize(new Dimension(325, 75));
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void AddComponents() {
        progressBar = new JProgressBar(1, 100);
        progressBar.setStringPainted(true);

        //getContentPane().add(scrollBar, BorderLayout.CENTER);
        getContentPane().add(new JLabel("Download progress (" + url + "):"), BorderLayout.NORTH);
        getContentPane().add(progressBar, BorderLayout.CENTER);
    }

    public void setElementPercentage(int bytesLoaded) {
        progressBar.setValue(bytesLoaded);
        progressBar.setString(bytesLoaded + "%");
    }
}
