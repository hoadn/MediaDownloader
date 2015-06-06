import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;

/**
 * Created by Dominik on 05.06.2015.
 */
public class SharedSXDownloaderPanel extends JPanel {
    private JButton btnDownload;
    private JButton btnChoosePath;
    private JTextField txtSharedSXURL;
    private JTextField txtSavePath;
    private JProgressBar progressBar;

    private JFileChooser dirChooser;
    private NowVideoDownloader nwDownloader;
    private SettingsManager settingsManager;

    public SharedSXDownloaderPanel(){
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

            if (System.getProperty("os.name").contains("Windows"))
                path = path.replace("\\", "\\\\");

            txtSavePath.setText(path);
        });
        btnDownload.addActionListener(e -> {
            if (txtSharedSXURL.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter a valid Shared.SX URL",
                        "SharedSXDownloader - Enter a valid URL", JOptionPane.ERROR_MESSAGE);
                return;
            }
            txtSavePath.setEditable(false);
            btnChoosePath.setEnabled(false);
            btnDownload.setEnabled(false);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    SharedSXDownloader sxDownloader = new SharedSXDownloader(txtSharedSXURL.getText());
                    String filename = "";
                    if(System.getProperty("os.name").contains("Windows"))
                        filename = txtSavePath.getText() + "\\" + sxDownloader.getFilename();
                    else
                        filename = txtSavePath.getText() + "/" + sxDownloader.getFilename();
                    String dlUrl = sxDownloader.getStreamURL();

                    sxDownloader.DownloadFile(dlUrl, filename, SharedSXDownloaderPanel.this,
                            sxDownloader.getDownloadSize(dlUrl));

                    JOptionPane.showMessageDialog(null, "Download finished!",
                            "SharedSXDownloader - Job finished", JOptionPane.INFORMATION_MESSAGE);

                    txtSavePath.setEditable(true);
                    btnChoosePath.setEnabled(true);
                    btnDownload.setEnabled(true);
                    progressBar.setValue(0);
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
        txtSharedSXURL = new JTextField();
        txtSavePath = new JTextField(settingsManager.GetStandardSavePath());

        JPanel panelProgress = new JPanel();
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        panelProgress.add(progressBar);

        panel.add(new JLabel("Save path:"));
        panel.add(txtSavePath);
        panel.add(btnChoosePath);
        panel.add(new JLabel("Shared.SX-URL:"));
        panel.add(txtSharedSXURL);
        panel.add(btnDownload);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
        this.add(panelProgress, BorderLayout.SOUTH);
    }

    public void setProgressBarPercentage(int value){
        progressBar.setValue(value);
    }
}
