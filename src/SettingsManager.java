import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 * Created by Dominik on 28.04.2015.
 */
public class SettingsManager {
    private String osName;
    private String settingsFilePath;
    private String settingsFile;

    public SettingsManager(){
        settingsFilePath = System.getProperty("user.dir");
        osName = System.getProperty("os.name");

        settingsFile = settingsFilePath;
        if(osName.contains("Windows"))
            settingsFile += "\\\\settings.ini";
        else if (osName.contains("nux"))
            settingsFile += "/settings.ini";
    }

    public String GetStandardSavePath(){
        try{
            File settings = new File(settingsFile);
            if(!settings.exists())
                return "";

            // now read first line
            BufferedReader br = new BufferedReader(new FileReader(settings));
            String line;
            while((line = br.readLine()) != null){
                if(line.contains("path"))
                    return line.replace("savepath:", "");
            }

            throw new IndexOutOfBoundsException("Found EOF and still no standard save path pls contact: admin@r3d-soft.de");
        }catch (Exception ex){
            ex.printStackTrace();
            if(osName.contains("Windows"))
                return "C:\\\\";
            else if(osName.contains("nux"))
                return "/home/";
            else
                return "/";
        }
    }

    public boolean GetConvertToMP3(){
        try{
            File settings = new File(settingsFile);
            if(!settings.exists())
                return true;

            // now read first line
            BufferedReader br = new BufferedReader(new FileReader(settings));
            String line;
            while((line = br.readLine()) != null){
                if(line.contains("converttomp3"))
                    return Boolean.valueOf(line.replace("converttomp3:", ""));
            }

            throw new IndexOutOfBoundsException("ERROR in settings.ini pls contact: admin@r3d-soft.de");
        }catch (Exception ex){
            return true;
        }
    }

    public boolean GetRemoveGEMA(){
        try{
            File settings = new File(settingsFile);
            if(!settings.exists())
                return true;

            // now read first line
            BufferedReader br = new BufferedReader(new FileReader(settings));
            String line;
            while((line = br.readLine()) != null){
                if(line.contains("removegema"))
                    return Boolean.valueOf(line.replace("removegema:", ""));
            }

            throw new IndexOutOfBoundsException("ERROR in settings.ini pls contact: admin@r3d-soft.de");
        }catch (Exception ex){
            ex.printStackTrace();
            return true;
        }
    }

    public String GetSettingsFile(){
        return settingsFile;
    }

    public void ShowSettingsWindow() {
        new SettingsManagerWindow(this);
    }

    public String GetFFMPEGDir() {
        try{
            if(osName.contains("nux"))
                return "Not needed under Linux";

            File settings = new File(settingsFile);
            if(!settings.exists())
                return "";

            // now read first line
            BufferedReader br = new BufferedReader(new FileReader(settings));
            String line;
            while((line = br.readLine()) != null){
                if(line.contains("ffmpeg"))
                    return line.replace("ffmpeg:", "");
            }

            throw new IndexOutOfBoundsException("ERROR in settings.ini contact me pls: admin@r3d-soft.de");
        }catch (Exception ex){
            ex.printStackTrace();
            return "{wd}\\bin\\";
        }
    }

    public boolean GetRemoveVidFiles(){
        try{
            File settings = new File(settingsFile);
            if(!settings.exists())
                return true;

            // new read lines
            BufferedReader br = new BufferedReader(new FileReader(settings));
            String line;
            while((line = br.readLine()) != null){
                if(line.contains("removeMp4"))
                    return Boolean.valueOf(line.replace("removeMp4:", ""));
            }

            throw new IndexOutOfBoundsException("ERROR in settings.ini pls contact: admin@r3d-soft.de");
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}

class SettingsManagerWindow extends JDialog{
    private JLabel lblSavePath;
    private JLabel lblConvertToMp3;
    private JLabel lblRemoveGEMA;
    private JLabel lblRemoveMp4;
    private JLabel lblFFMPEGFile;
    private JTextField txtSavePath;
    private JTextField txtFFMPEG;
    private JCheckBox checkConvertToMp3;
    private JCheckBox checkRemoveGEMA;
    private JCheckBox checkRemoveMp4;
    private JButton btnSave;
    private JButton btnCancel;
    private String settingsFile;
    private JButton btnSelectFFMPEG;
    private JButton btnSelectStandardSave;
    private JFileChooser dirChooser;

    public SettingsManagerWindow(SettingsManager man){
        setTitle("Change settings");

        //lblSavePath = new JLabel("Standard save path:");
        btnSelectStandardSave = new JButton("Select standard save path:");
        btnSelectStandardSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = "";

                dirChooser = new JFileChooser();
                dirChooser.setDialogTitle("Select the path where files will be stored ...");
                dirChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.dir")));
                dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                dirChooser.setAcceptAllFileFilterUsed(false);

                if (dirChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                    path = dirChooser.getSelectedFile().getAbsolutePath();

                if (System.getProperty("os.name").contains("Windows"))
                    path = path.replace("\\", "\\\\");

                txtSavePath.setText(path);
            }
        });
        txtSavePath = new JTextField(man.GetStandardSavePath());
        lblConvertToMp3 = new JLabel("Convert to mp3");
        checkConvertToMp3 = new JCheckBox("", man.GetConvertToMP3());
        lblRemoveMp4 = new JLabel("Remove video files after mp3 created");
        checkRemoveMp4 = new JCheckBox("", man.GetRemoveVidFiles());
        lblRemoveGEMA = new JLabel("Remove GEMA");
        checkRemoveGEMA = new JCheckBox("", man.GetRemoveGEMA());
        //lblFFMPEGFile = new JLabel("FFMPEG-Directory");
        btnSelectFFMPEG = new JButton("Select FFMPEG-Directory");
        btnSelectFFMPEG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = "";

                dirChooser = new JFileChooser();
                dirChooser.setDialogTitle("Select the path where FFMPEG.exe is located");
                dirChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.dir")));
                dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                dirChooser.setAcceptAllFileFilterUsed(false);

                if (dirChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                    path = dirChooser.getSelectedFile().getAbsolutePath();

                if (System.getProperty("os.name").contains("Windows"))
                    path = path.replace("\\", "\\\\");

                txtFFMPEG.setText(path);
            }
        });
        txtFFMPEG = new JTextField(man.GetFFMPEGDir().replace("{wd}", System.getProperty("user.dir")));

        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                //vs setVisible(false);
            }
        });
        btnSave = new JButton("Save & Close");
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File settings = new File(settingsFile);
                    PrintWriter out = new PrintWriter(settings);
                    out.println("savepath:" + txtSavePath.getText());
                    out.println("converttomp3:" + checkConvertToMp3.isSelected());
                    out.println("removegema:" + checkRemoveGEMA.isSelected());
                    out.println("ffmpeg:" + txtFFMPEG.getText().replace(System.getProperty("user.dir"), "{wd}"));
                    out.println("removeMp4:" + checkRemoveMp4.isSelected());
                    out.close();
                    JOptionPane.showMessageDialog(null, "Successfully saved settings! Changes will apply on application restart...", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel panel = new JPanel(new GridLayout(0,2));
        //panel.add(lblSavePath);
        panel.add(btnSelectStandardSave);
        panel.add(txtSavePath);
        panel.add(lblConvertToMp3);
        panel.add(checkConvertToMp3);
        panel.add(lblRemoveMp4);
        panel.add(checkRemoveMp4);
        panel.add(lblRemoveGEMA);
        panel.add(checkRemoveGEMA);
        //panel.add(lblFFMPEGFile);
        panel.add(btnSelectFFMPEG);
        panel.add(txtFFMPEG);
        panel.add(btnCancel);
        panel.add(btnSave);

        getContentPane().add(panel, BorderLayout.CENTER);
        setVisible(true);
        pack();
        setLocationRelativeTo(null);
        settingsFile = man.GetSettingsFile();
    }


}
