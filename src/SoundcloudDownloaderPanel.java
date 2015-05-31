import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Dominik on 31.05.2015.
 */
public class SoundcloudDownloaderPanel extends JPanel {
    private SoundcloudDownloader scDownloader;
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
    private JList listLinksGUI;

    private String listTitle;

    public SoundcloudDownloaderPanel(){
        settingsManager = new SettingsManager();
        initComponents();
        initFileChooser();
        initActionListeners();
    }

    private void initActionListeners() {
        btnAddToList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // determine if real fb link
                if(txtURL.getText().contains("soundcloud")) {
                    listModel.addElement(txtURL.getText());
                }
                else {
                    JOptionPane.showMessageDialog(null, "No valid soundcloud link", "SoundCloudDownloader - Not a valid link", JOptionPane.ERROR_MESSAGE);
                }
                txtURL.setText("");
            }
        });
        btnRemoveFromList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = listLinksGUI.getSelectedIndex();
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
                    JOptionPane.showMessageDialog(null, "Please select a download path", "SoundCloudDownloader - Select a valid path", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(listModel.size() <= 0) {
                    JOptionPane.showMessageDialog(null,
                            "List is empty. Please add a soundcloud link in order to start the download process",
                            "SoundCloudDownloader - List is empty", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                txtPath.setEditable(false);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < listModel.size(); i++) {
                            String url = listModel.get(i).toString();
                            scDownloader = new SoundcloudDownloader(url, txtPath.getText());
                            String toDL = scDownloader.getAudioURL();
                            long size = scDownloader.getDownloadSize(toDL);
                            listTitle = "Size: " + (size / 1024) + "KB | " + listModel.get(i).toString();
                            scDownloader.DownloadFile(toDL, (int)size, i, SoundcloudDownloaderPanel.this);
                        }

                        JOptionPane.showMessageDialog(null, "Downloaded all audio files to: " + txtPath.getText(), "SoundCloudDownloader - Job finished", JOptionPane.INFORMATION_MESSAGE);
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
        JLabel lblURL = new JLabel("SoundCloud-Link:");
        txtURL = new JTextField("");
        btnAddToList = new JButton("Add to list");
        btnRemoveFromList = new JButton("Remove selected link");

        panelTop.add(lblURL);
        panelTop.add(txtURL);
        panelTop.add(btnAddToList);
        panelTop.add(btnRemoveFromList);

        scrollBar = new JScrollPane();
        listModel = new DefaultListModel();
        listLinksGUI = new JList(listModel);
        scrollBar.setViewportView(listLinksGUI);

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
