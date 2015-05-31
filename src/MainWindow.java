import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Dominik on 20.04.2015.
 */
public class MainWindow extends JFrame {
    private JMenuBar menuBar;
    private JMenu menuMenu;
    private JMenu menuSettings;
    private JMenu menuHelp;
    private JMenuItem menuItemExit;
    private JMenuItem menuItemSettingsWindow;
    private JMenuItem menuItemHelp;
    private JMenuItem menuItemAbout;

    private YouTubeDownloaderPanel ytPanel;
    private FacebookDownloaderPanel fbPanel;
    private InstagramDownloaderPanel igPanel;
    private SoundcloudDownloaderPanel scPanel;

    private SettingsManager settingsManager;

    public MainWindow(){
        settingsManager = new SettingsManager();
        initComponents();
        initActionListeners();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("MediaDownloader - (c) R3DST0RM 2015");
        setLocationRelativeTo(null);
        //TODO: Design a downloader icon and rename this one to downloader.png!
        setIconImage(new ImageIcon(System.getProperty("user.dir") + "\\downloader.png").getImage());
        setVisible(true);
    }

    private void initActionListeners() {
        menuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menuItemHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = "<html>This tool allows you to download various files from many social/video platforms (eg. YouTube)." +
                        "<br />For the YouTube Downloader following terms are supported:" +
                        "<ul>" +
                        "<li>user:<i>USERNAME</i> (Add all videos from a channel)</li>" +
                        "<li>https://wwww.youtube.com/watch?v=<i>VIDEOID</i> (Just adds this video to the download list)</li>" +
                        "<li>https://www.youtube.com/user/<i>USERNAME</i> (Also add all videos from a channel)</li>" +
                        "</ul>" +
                        "</html>";

                JOptionPane.showMessageDialog(null, new JLabel(msg), "Help", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menuItemAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = "<html>" +
                        "Thanks for using MediaDownloader - written by R3DST0RM.<br />" +
                        "This software uses ffmpeg as MP3 converter all licenses can be found here: bin/licenses/<br /><br />" +
                        "This software is free software (GNU General Public License v2) - Source Code available at request:<br /><br />" +
                        "E-Mail: <b>admin@r3d-soft.de</b><br />" +
                        "Website: <b>http://r3d-soft.de</b>" +
                        "</html>";

                JOptionPane.showMessageDialog(null, new JLabel(msg), "Help", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menuItemSettingsWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settingsManager.ShowSettingsWindow();
            }
        });
    }

    private void initComponents() {
        // add menubar
        menuBar = new JMenuBar();
        menuMenu = new JMenu("Menu");
        menuSettings = new JMenu("Settings");
        menuHelp = new JMenu("Help");

        menuItemExit = new JMenuItem("Exit");
        menuItemSettingsWindow = new JMenuItem("Settings");
        menuItemHelp = new JMenuItem("Help - Usage");
        menuItemAbout = new JMenuItem("? - About this tool");

        menuMenu.add(menuItemExit);
        menuSettings.add(menuItemSettingsWindow);
        menuHelp.add(menuItemHelp);
        menuHelp.add(menuItemAbout);

        menuBar.add(menuMenu);
        menuBar.add(menuSettings);
        menuBar.add(menuHelp);

        // add all panels
        JTabbedPane tabpane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );
        ytPanel = new YouTubeDownloaderPanel();
        fbPanel = new FacebookDownloaderPanel();
        igPanel = new InstagramDownloaderPanel();
        scPanel = new SoundcloudDownloaderPanel();

        tabpane.add("YouTube-Downloader",ytPanel);
        tabpane.add("Facebook-Downloader",fbPanel);
        tabpane.add("Instagram-Downloader",igPanel);
        tabpane.add("SoundCloud-Downloader", scPanel);

        getContentPane().add(menuBar, BorderLayout.NORTH);
        getContentPane().add(tabpane);
        pack();
    }

    // main method which starts the gui and the entry programm
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {
            // handle exception
        }
        catch (ClassNotFoundException e) {
            // handle exception
        }
        catch (InstantiationException e) {
            // handle exception
        }
        catch (IllegalAccessException e) {
            // handle exception
        }

        new MainWindow();
    }
}
