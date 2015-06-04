import javax.swing.*;

/**
 * Creation time: 03:05
 * Created by Dominik on 22.04.2015.
 */
public class ImagePreviewWindow extends JFrame {
    private ImagePreviewPanel imgPrePanel;

    public ImagePreviewWindow(String previewURL, String descr){
        if(!previewURL.contains("http"))
            return;

        setTitle("MediaDownloader - Image Preview Window : " + descr + " : " + previewURL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        imgPrePanel = new ImagePreviewPanel(previewURL);
        getContentPane().add(imgPrePanel);
        setSize(imgPrePanel.getWidth(), imgPrePanel.getHeight());
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
