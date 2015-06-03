import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Created by Dominik on 30.05.2015.
 */
public class InstagramDownloadWindow extends JFrame {
    private JScrollPane scrollBar;
    private DefaultListModel listModel;
    private JList listMediaGUI;
    private boolean isClosed = false;

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


        // implementation for abort download when user closes this window
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if(listModel.get(listModel.size() -1 ).toString().contains("100%"))
                    return;

                int confirm = JOptionPane.showOptionDialog(InstagramDownloadWindow.this,
                        "Are you sure you want to abort the download (this may corrupt your files)?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == JOptionPane.YES_OPTION) {
                    isClosed = true;
                    InstagramDownloadWindow.this.dispose();
                }
            }
        };

        addWindowListener(exitListener);
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

    public boolean isClosed(){
        return isClosed;
    }
}
