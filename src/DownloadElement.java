import javax.swing.*;
import java.awt.*;

/**
 * Created by Dominik on 18.05.2015.
 */
public class DownloadElement extends JList {

    public DownloadElement(Object[] listData){
        super(listData);

    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.drawRoundRect(5, 5, 50, 50 , 50, 50);

        revalidate();
        repaint();
    }
}
