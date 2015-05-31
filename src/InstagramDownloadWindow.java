import javax.swing.*;
import java.awt.*;

/**
 * Created by Dominik on 30.05.2015.
 */
public class InstagramDownloadWindow extends JFrame {
    private JScrollPane scrollBar;
    private DefaultListModel listModel;
    private JList listMediaGUI;

    public InstagramDownloadWindow(String[] urls){
        setTitle("InstagramDownloader - Download Progress");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        AddComponents();

        for (int i = 0; i < urls.length; i++) {
            listModel.addElement(urls[i]);
        }

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void AddComponents() {
        listModel = new DefaultListModel();
        listMediaGUI = new JList(listModel);
        scrollBar = new JScrollPane(listMediaGUI);

        getContentPane().add(scrollBar, BorderLayout.CENTER);
    }

    public void setElementPercentage(String s, int element) {
        String model = listModel.getElementAt(element).toString();
        String nListString;

        if(model.contains("|")) {
            String[] arr = listModel.getElementAt(element).toString().split("\\|");
            nListString = arr[arr.length - 1].trim();
        }else
            nListString = model;

        listModel.setElementAt(s + " | " + nListString, element);
    }

    public void RemoveElementAt(int i){
        listModel.removeElementAt(i);
    }

    public void SetOverallProgress(String s){
        String rootTitle = " - InstagramDownloader - Download Progress";
        setTitle(s + rootTitle);
    }
}
