import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImagePreviewPanel extends JPanel{
    private BufferedImage image;

    public ImagePreviewPanel(String previewURL) {
        try {
            image = ImageIO.read(new URL(previewURL));
            setSize(image.getWidth(), image.getHeight());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Cannot open preview image - I/O-Exception", "MediaDownloader - Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters            
    }

}