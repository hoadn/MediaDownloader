import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Dominik on 22.04.2015.
 */
public class FacebookDownloaderPanel extends JPanel {
    private FacebookDownloader fbDownloader;
    private SettingsManager settingsManager;

    private JTextField txtURL;
    private JTextField txtPath;
    private JFileChooser dirChooser;

    private JButton btnAddToList;
    private JButton btnRemoveFromList;
    private JButton btnSelectPath;
    private JButton btnStartDownload;

    private JScrollPane scrollBar;
    private DefaultListModel listModel;
    private JList listYTLinksGUI;

    private String listTitle;

    public FacebookDownloaderPanel(){
        settingsManager = new SettingsManager();
        initComponents();
        initFileChooser();
        initActionListeners();
    }

    private void initActionListeners() {
        btnAddToList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtURL.setText(txtURL.getText().replace("photos_stream?tab=photos_stream", ""));
                txtURL.setText(txtURL.getText().replace("photos_stream?tab=photos_albums", ""));
                txtURL.setText(txtURL.getText().replace("photos_stream?tab=photos", ""));


                // determine if real fb link
                if(txtURL.getText().contains("/?type") && txtURL.getText().contains(("&theater"))){
                    fbDownloader = new FacebookDownloader(txtURL.getText());
                    String[] urls = fbDownloader.GetDownloadLinks();
                    for (int i = 0; i < urls.length; i++) {
                        listModel.addElement(urls[i]);
                    }
                }
                else if(txtURL.getText().contains("facebook")) {
                    fbDownloader = new FacebookDownloader(txtURL.getText());
                    String[] pic = fbDownloader.GetDownloadLinks();
                    //fbDownloader = new FacebookDownloader(txtURL.getText());
                    //String[] vid = fbDownloader.GetDownloadLinks();

                    for (int i = 0; i < pic.length; i++) {
                        listModel.addElement(pic[i]);
                    }

                    //if(vid != null) {
                   //     for (int i = 0; i < vid.length; i++) {
                    //        listModel.addElement(vid[i]);
                    //    }
                   // }
                }
                else {
                    JOptionPane.showMessageDialog(null, "No facebook link", "FacebookDownloader - Not a valid link", JOptionPane.ERROR_MESSAGE);
                }
                txtURL.setText("");
            }
        });
        btnRemoveFromList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = listYTLinksGUI.getSelectedIndex();
                if(index > -1)
                    listModel.removeElementAt(index);
            }
        });
        btnSelectPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = "";

                if (dirChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                    path = dirChooser.getSelectedFile().getAbsolutePath();

                if(System.getProperty("os.name").contains("Windows"))
                    path = path.replace("\\", "\\\\");

                txtPath.setText(path);
            }
        });
        btnStartDownload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(txtPath.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "Please select a download path", "FacebookDownloader - Select a valid path", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(listModel.size() <= 0) {
                    JOptionPane.showMessageDialog(null,
                            "List is empty. Please add a facebook link in order to start the download process",
                            "FacebookDownloader - List is empty", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                txtPath.setEditable(false);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < listModel.size(); i++) {
                            String url = listModel.get(i).toString();
                            fbDownloader = new FacebookDownloader();
                            long size = fbDownloader.getDownloadSize(url);
                            listTitle = "Size: " + (size / 1024) + "KB | " + listModel.get(i).toString();
                            fbDownloader.DownloadFile(url, size, i, FacebookDownloaderPanel.this, txtPath.getText());
                        }

                        JOptionPane.showMessageDialog(null, "Downloaded all media files to: " + txtPath.getText(), "FacebookDownloader - Job finished", JOptionPane.INFORMATION_MESSAGE);
                        listModel.clear();
                        txtPath.setEditable(true);
                    }
                });
                t.start();
            }
        });
    }

    private void initFileChooser() {
        //
        // Standard File Chooser settings
        //
        dirChooser = new JFileChooser();
        dirChooser.setDialogTitle("Choose a path to save ...");
        dirChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.dir")));
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setAcceptAllFileFilterUsed(false);
    }

    private void initComponents() {
        JPanel panelTop = new JPanel(new GridLayout(0,4));
        JLabel lblURL = new JLabel("Facebook-Link:");
        txtURL = new JTextField("");
        btnAddToList = new JButton("Add to list");
        btnRemoveFromList = new JButton("Remove selected link");

        panelTop.add(lblURL);
        panelTop.add(txtURL);
        panelTop.add(btnAddToList);
        panelTop.add(btnRemoveFromList);

        scrollBar = new JScrollPane();
        listModel = new DefaultListModel();
        listYTLinksGUI = new JList(listModel);
        scrollBar.setViewportView(listYTLinksGUI);

        JPanel panelBottom = new JPanel(new GridLayout(0,3));
        txtPath = new JTextField(settingsManager.GetStandardSavePath());
        btnSelectPath = new JButton("Select Path");
        btnStartDownload = new JButton("Download all!");

        panelBottom.add(txtPath);
        panelBottom.add(btnSelectPath);
        panelBottom.add(btnStartDownload);

        // add all elements to gui
        this.setLayout(new BorderLayout());
        this.add(panelTop, BorderLayout.NORTH);
        this.add(scrollBar, BorderLayout.CENTER);
        this.add(panelBottom, BorderLayout.SOUTH);
    }

    public void setElementPercentage(String s, int element) {
        listModel.setElementAt(s + " | " + listTitle, element);
    }
}
