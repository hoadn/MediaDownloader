import javax.swing.*;
import java.awt.*;

/**
 * Created by Dominik on 06.06.2015.
 */
public class StreamCloudEUDownloaderPanel extends JPanel {
    private JButton btnDownload;
    private JButton btnChoosePath;
    private JTextField txtSCEURL;
    private JTextField txtSavePath;
    private JProgressBar progressBar;

    private JFileChooser dirChooser;
    private SettingsManager settingsManager;

    public StreamCloudEUDownloaderPanel(){
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
            if (txtSCEURL.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter a valid StreamCloud.EU URL",
                        "StreamCloudEUDownloader - Enter a valid URL", JOptionPane.ERROR_MESSAGE);
                return;
            }
            txtSavePath.setEditable(false);
            btnChoosePath.setEnabled(false);
            btnDownload.setEnabled(false);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    StreamCloudEUDownloader sceDownloader = new StreamCloudEUDownloader(txtSCEURL.getText());
                    String filename = "";
                    if(System.getProperty("os.name").contains("Windows"))
                        filename = txtSavePath.getText() + "\\" + sceDownloader.getFilename();
                    else
                        filename = txtSavePath.getText() + "/" + sceDownloader.getFilename();
                    String dlUrl = sceDownloader.getStreamURL();

                    sceDownloader.DownloadFile(dlUrl, filename, StreamCloudEUDownloaderPanel.this,
                            sceDownloader.getDownloadSize(dlUrl));

                    JOptionPane.showMessageDialog(null, "Download finished!",
                            "StreamCloudEUDownloader - Job finished", JOptionPane.INFORMATION_MESSAGE);

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
        txtSCEURL = new JTextField();
        txtSavePath = new JTextField(settingsManager.GetStandardSavePath());

        JPanel panelProgress = new JPanel();
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        panelProgress.add(progressBar);

        panel.add(new JLabel("Save path:"));
        panel.add(txtSavePath);
        panel.add(btnChoosePath);
        panel.add(new JLabel("StreamCloud.EU-URL:"));
        panel.add(txtSCEURL);
        panel.add(btnDownload);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
        this.add(panelProgress, BorderLayout.SOUTH);
    }

    public void setProgressBarPercentage(int value){
        progressBar.setValue(value);
    }
}
