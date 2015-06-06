import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Creation time: 16:09
 * Created by Dominik on 04.06.2015.
 */
public class NowVideoDownloaderPanel extends JPanel{
    private JButton btnDownload;
    private JButton btnChoosePath;
    private JTextField txtNVLink;
    private JTextField txtSavePath;

    private JFileChooser dirChooser;
    private NowVideoDownloader nwDownloader;
    private SettingsManager settingsManager;

    public NowVideoDownloaderPanel(){
        settingsManager = new SettingsManager();

        initComponents();
        initFileChooser();
        initActionListener();
    }

    private void initActionListener() {
        btnChoosePath.addActionListener(e -> {
            String path = "";
            File fileToSave = null;
            int userSelection = dirChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                 fileToSave = dirChooser.getSelectedFile();
            }

            if(System.getProperty("os.name").contains("Windows"))
                path = path.replace("\\", "\\\\");

            if(fileToSave != null) {
                if(fileToSave.toString().endsWith(".flv"))
                    txtSavePath.setText(fileToSave.getAbsolutePath());
                else
                    txtSavePath.setText(fileToSave.getAbsolutePath() + ".flv");
            }
        });
        btnDownload.addActionListener(e -> {
            if(txtNVLink.getText().trim().equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid nowvideo link",
                        "NowVideoDownloader - Enter a valid URL", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    nwDownloader = new NowVideoDownloader(txtNVLink.getText(), txtSavePath.getText());
                    String url = nwDownloader.getVideoURL();
                    nwDownloader.getDownloadSize(url);
                    nwDownloader.DownloadFile(url);

                    JOptionPane.showMessageDialog(null, "Download finished!",
                            "NowVideoDownloader - Job finished", JOptionPane.INFORMATION_MESSAGE);
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
        dirChooser.setDialogTitle("Choose a file to save ...");
        //dirChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Flash Video", "flv");
        dirChooser.setFileFilter(filter);
        dirChooser.setAcceptAllFileFilterUsed(false);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(0,3));
        btnDownload = new JButton("Download");
        btnChoosePath = new JButton("Choose path ...");
        txtNVLink = new JTextField();
        txtSavePath = new JTextField(settingsManager.GetStandardSavePath());

        panel.add(new JLabel("Save path:"));
        panel.add(txtSavePath);
        panel.add(btnChoosePath);
        panel.add(new JLabel("NowVideo-Link:"));
        panel.add(txtNVLink);
        panel.add(btnDownload);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }
}
