import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominik on 20.04.2015.
 */
public class YouTubeDownloaderPanel extends JPanel {
    private YouTubeDownloader ytDownloader;
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

    private JCheckBox checkToMP3;
    private JCheckBox checkGEMAUnblock;
    private JCheckBox checkRemoveMp4FilesAfterDownload;

    private String listTitle;

    private List<String> currentMp4Files = new ArrayList<String>();

    public YouTubeDownloaderPanel(){
        settingsManager = new SettingsManager();
        initComponents();
        initFileChooser();
        initActionListeners();
    }

    private void initActionListeners() {
        btnAddToList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if not a correct youtube name assume that text is a youtube username
                if(txtURL.getText().contains("user:") || txtURL.getText().contains("users:") || txtURL.getText().contains("youtube.com/user/"))
                {
                    // now add all videos from a channel if it is a channel
                    String username = txtURL.getText().replace("user:", "").replace("users:", "")
                            .replace("https://wwww.", "").replace("http://wwww.", "")
                            .replace("youtube.com/user/", "");

                    YouTubeGetChannelVideos channelVideos = new YouTubeGetChannelVideos(username);
                    String[] list = channelVideos.GetVideoList();

                    if(list.length <= 0) {
                        JOptionPane.showMessageDialog(null, "No YouTube User matching your criteria",
                                "YouTubeDownloader - No user found!", JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        for (int i = 0; i < list.length; i++) {
                            listModel.addElement("https://youtube.com/watch?v=" + list[i]);
                        }
                        JOptionPane.showMessageDialog(null, "Added " + list.length + " videos recording to your download request: " +
                                username, "YouTubeDownloader - Added all channel videos", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else if(txtURL.getText().contains("list=")){
                    YouTubeRetrievePlaylist retrievePlaylist = new YouTubeRetrievePlaylist(txtURL.getText());
                    String[] elements = retrievePlaylist.getAllVideosFromPlaylist("");
                    for (int i = 0; i < elements.length; i++) {
                        listModel.addElement(elements[i]);
                    }
                    if(System.getProperty("os.name").contains("Windows"))
                        txtPath.setText(txtPath.getText() + "\\\\" + retrievePlaylist.getPlayListTitle());
                    else
                        txtPath.setText(txtPath.getText() + "/" + retrievePlaylist.getPlayListTitle());

                    JOptionPane.showMessageDialog(null, "Added " + elements.length + " links to the downloader"
                            , "YouTubeDownloader - Added all playlist videos", JOptionPane.INFORMATION_MESSAGE);
                }
                else if(txtURL.getText().contains("youtube.com/"))
                    listModel.addElement(txtURL.getText());
                else{
                    JOptionPane.showMessageDialog(null, "No youtube link", "YouTubeDownloader - Not a valid link", JOptionPane.ERROR_MESSAGE);
                }
                txtURL.setText("");
            }
        });
        btnRemoveFromList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = listYTLinksGUI.getSelectedIndex();
                if (index > -1)
                    listModel.removeElementAt(index);
            }
        });
        btnSelectPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = "";

                if (dirChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                    path = dirChooser.getSelectedFile().getAbsolutePath();

                if (System.getProperty("os.name").contains("Windows"))
                    path = path.replace("\\", "\\\\");

                txtPath.setText(path);
            }
        });
        btnStartDownload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtPath.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please select a download path",
                            "YouTubeDownloader - Select a valid path", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (listModel.size() <= 0) {
                    JOptionPane.showMessageDialog(null,
                            "List is empty. Please add a youtube link in order to start the download process",
                            "YouTubeDownloader - List is empty", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                txtPath.setEditable(false);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < listModel.size(); i++) {
                            ytDownloader = new YouTubeDownloader(listModel.get(i).toString(), txtPath.getText(),
                                    checkGEMAUnblock.isSelected());
                            String dlURL = ytDownloader.getVideoURL();
                            int size = ytDownloader.getDownloadSize(dlURL);
                            listTitle = "Size: " + (size / 1024) + "KB | " + listModel.get(i).toString();
                            ytDownloader.DownloadFile(dlURL, size, i, YouTubeDownloaderPanel.this);

                            if (checkToMP3.isSelected())
                                ytDownloader.StartConvert();
                        }

                        // remove mp4 files downloaded
                        boolean shallRemoved = checkRemoveMp4FilesAfterDownload.isSelected();
                        if (System.getProperty("os.name").contains("Windows")) {
                            if (shallRemoved) {
                                String line;
                                String pidInfo = "";
                                try {
                                    boolean isRunning = true;
                                    while(isRunning) {
                                        Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");

                                        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

                                        while ((line = input.readLine()) != null) {
                                            pidInfo += line;
                                        }

                                        input.close();

                                        if (!pidInfo.contains("ffmpeg")) {
                                            isRunning = false;
                                            // now remove all mp4 files contained
                                            for (int i = 0; i < currentMp4Files.size(); i++) {
                                                try {
                                                    File delFile = new File(currentMp4Files.get(i));
                                                    if (delFile.exists())
                                                        delFile.delete();

                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                            currentMp4Files.clear();
                                        }

                                        pidInfo = "";
                                        line = "";
                                    }
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null, "Error while checking for mp4 files. " +
                                                    "Please contact: admin@r3d-soft.de in order to fix this error!" +
                                                    " Error message: " + ex.getMessage(),
                                            "YouTubeDownloader - Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }else if(System.getProperty("os.name").contains("nux")){
                            if(shallRemoved) {
                                String lines = "";
                                try {
                                    boolean isRunning = true;
                                    while(isRunning) {
                                        // Execute command
                                        String command = "ps aux";
                                        Process child = Runtime.getRuntime().exec(command);

                                        // Get the input stream and read from it
                                        InputStream in = child.getInputStream();
                                        int c;
                                        while ((c = in.read()) != -1) {
                                            lines += c;
                                        }
                                        in.close();

                                        if (!lines.contains("ffmpeg")) {
                                            isRunning = false;
                                            // now remove all mp4 files contained
                                            for (int i = 0; i < currentMp4Files.size(); i++) {
                                                try {
                                                    File delFile = new File(currentMp4Files.get(i));
                                                    if (delFile.exists())
                                                        delFile.delete();

                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                            currentMp4Files.clear();
                                        }
                                    }

                                    } catch (IOException e) {
                                    JOptionPane.showMessageDialog(null, "Error while checking for mp4 files. " +
                                                    "Please contact: admin@r3d-soft.de in order to fix this error!" +
                                                    " Error message: " + e.getMessage(),
                                            "YouTubeDownloader - Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                        else {
                            //
                            JOptionPane.showMessageDialog(null, "Error determine OS in order to delete mp4 files." +
                                            "Please contact: admin@r3d-soft.de in order to fix this error!" +
                                            " Error message: Unkown OS!",
                                    "YouTubeDownloader - Error", JOptionPane.ERROR_MESSAGE);
                        }

                        JOptionPane.showMessageDialog(null, "Downloaded all videos to: " + txtPath.getText(),
                                "YouTubeDownloader - Job finished", JOptionPane.INFORMATION_MESSAGE);
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
        JLabel lblURL = new JLabel("YouTube-Link:");
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
        checkToMP3 = new JCheckBox("Convert to MP3", settingsManager.GetConvertToMP3());
        checkGEMAUnblock = new JCheckBox("Remove GEMA", settingsManager.GetRemoveGEMA());
        checkRemoveMp4FilesAfterDownload = new JCheckBox("Remove mp4 after download", settingsManager.GetRemoveGEMA());
        txtPath = new JTextField(settingsManager.GetStandardSavePath());
        btnSelectPath = new JButton("Select Path");
        btnStartDownload = new JButton("Download all!");

        panelBottom.add(checkToMP3);
        panelBottom.add(checkGEMAUnblock);
        panelBottom.add(checkRemoveMp4FilesAfterDownload);
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

    public void addCurrentMP4File(String file){
        currentMp4Files.add(file);
    }
}
