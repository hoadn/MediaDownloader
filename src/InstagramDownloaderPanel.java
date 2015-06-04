import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Creation time: 03:05
 * Created by Dominik on 22.04.2015.
 */
public class InstagramDownloaderPanel extends JPanel {
    private JButton btnDownload;
    private JButton btnChoosePath;
    private JButton btnGetAllFromProfileAndDownload;
    private JTextField txtInstaProfile;
    private JTextField txtInstaLink;
    private JTextField txtSavePath;
    private JCheckBox checkPreview;
    private JCheckBox skipExistingFiles;

    private JFileChooser dirChooser;

    private InstagramDownloader igDownloader;
    private SettingsManager settingsManager;

    private InstagramDownloadWindow dlWindow;

    public InstagramDownloaderPanel(){
        settingsManager = new SettingsManager();

        initComponents();
        initFileChooser();
        initActionListener();
    }

    private void initActionListener() {
        btnChoosePath.addActionListener(e -> {
            String path = "";

            if (dirChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                path = dirChooser.getSelectedFile().getAbsolutePath();

            if(System.getProperty("os.name").contains("Windows"))
                path = path.replace("\\", "\\\\");

            txtSavePath.setText(path);
        });
        btnDownload.addActionListener(e -> {
            if(txtInstaLink.getText().trim().equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid instagram link",
                        "InstagramDownloader - Enter a valid URL", JOptionPane.ERROR_MESSAGE);
                return;
            }
            txtSavePath.setEditable(false);
            btnGetAllFromProfileAndDownload.setEnabled(false);
            btnDownload.setEnabled(false);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        igDownloader = new InstagramDownloader(txtInstaLink.getText(), txtSavePath.getText(), checkPreview.isSelected());
                        String url = igDownloader.GetURLsAndPreview();
                        igDownloader.DownloadFile(url, 0, 0);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                    txtSavePath.setEditable(true);
                    btnGetAllFromProfileAndDownload.setEnabled(true);
                    btnDownload.setEnabled(true);
                }
            });
            t.start();
        });
        btnGetAllFromProfileAndDownload.addActionListener(e -> {
            String check = txtInstaProfile.getText().replace("user:", "").replace("users:", "");
            if(check.trim().equals("")) {
                JOptionPane.showMessageDialog(null,
                        "Please enter a valid username to start the crawling process",
                        "InstagramDownloader - No user entered", JOptionPane.ERROR_MESSAGE);
                return;
            }
            txtSavePath.setEditable(false);
            btnDownload.setEnabled(false);
            btnGetAllFromProfileAndDownload.setEnabled(false);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    igDownloader = new InstagramDownloader(txtInstaProfile.getText(), txtSavePath.getText());
                    igDownloader.setSkipFiles(skipExistingFiles.isSelected());
                    String userID = igDownloader.fetchUserID("https://api.instagram.com/v1/users/search?q={user}&client_id=21ae9c8b9ebd4183adf0d0602ead7f05");
                    System.out.println("Found userID: " + userID);
                    igDownloader.setSavePath(txtSavePath.getText() + "/" + userID);
                    String[] urls = igDownloader.fetchAllImageURLs(userID, "");
                    System.out.println("Found: " + urls.length + " media files");
                    dlWindow = new InstagramDownloadWindow(urls);
                    igDownloader.setDownloadWindow(dlWindow);
                    igDownloader.DownloadFile(urls);
                    txtSavePath.setEditable(true);
                    btnDownload.setEnabled(true);
                    btnGetAllFromProfileAndDownload.setEnabled(true);
                    // this hasn't to be here!
                    // JOptionPane.showMessageDialog(null, "Downloaded all media files to: " + txtSavePath.getText(), "InstagramDownloader - Job finished", JOptionPane.INFORMATION_MESSAGE);
                    dlWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                }
            });
            t.start();
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
        JPanel panel = new JPanel(new GridLayout(0,3));
        btnDownload = new JButton("Download");
        btnChoosePath = new JButton("Choose path ...");
        txtInstaLink = new JTextField();
        txtSavePath = new JTextField(settingsManager.GetStandardSavePath());
        checkPreview = new JCheckBox("Show preview picture (only single link)");
        checkPreview.setSelected(true);
        btnGetAllFromProfileAndDownload = new JButton("Crawl profile");
        txtInstaProfile = new JTextField("user:");
        skipExistingFiles = new JCheckBox("Skip existing files (crawler only)");
        skipExistingFiles.setSelected(true);

        panel.add(new JLabel("Save path:"));
        panel.add(txtSavePath);
        panel.add(btnChoosePath);
        panel.add(new JLabel("Instagram-Link:"));
        panel.add(txtInstaLink);
        panel.add(btnDownload);
        panel.add(new JLabel("Instagram-Profile Crawler"));
        panel.add(txtInstaProfile);
        panel.add(btnGetAllFromProfileAndDownload);
        panel.add(checkPreview);
        panel.add(skipExistingFiles);

        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }
}