import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Dominik on 03.06.2015.
 */
public class ConsoleManager {
    private String[] params;
    private boolean noOutput;
    private BufferedReader readCmd;
    private String command = "";

    private String hoster = "";
    private String dlUrl = "";
    private String filePath = "";
    private boolean convertToMp3 = false;
    private boolean deleteMp4 = false;
    private boolean crawlProfile = false;

    public ConsoleManager(String[] params, boolean noOutput) throws IOException {
        this.params = params;
        this.noOutput = noOutput;

        if(!this.noOutput)
            printGreeting();
        else{ // create string cmd
            for (int i = 0; i < params.length; i++) {
                command += params[i] + " ";
            }
        }
    }

    public void printGreeting() throws IOException {
        System.out.println("####################################");
        System.out.println("# ConsoleMediaDownloader - Welcome #");
        System.out.println("#                                  #");
        System.out.println("# Usage:                           #");
        System.out.println("#       -h Define a supported host #");
        System.out.println("#       Shortcuts like:            #");
        System.out.println("#       yt  = YouTube              #");
        System.out.println("#       sc  = SoundCloud           #");
        System.out.println("#       ig  = Instagram            #");
        System.out.println("#       fb  = Facebook             #");
        System.out.println("#       vim = Vimeo                #");
        System.out.println("#       are supported              #");
        System.out.println("#                                  #");
        System.out.println("#       -l Enter download url      #");
        System.out.println("#       -p Define a save path      #");
        System.out.println("#       -c Convert to mp3          #");
        System.out.println("#       -d Delete mp4 (-c needed)  #");
        System.out.println("#       Using ffmpeg               #");
        System.out.println("#       -cp Crawl profile          #");
        System.out.println("####################################");
        System.out.println("");
        System.out.println("Please enter your cmd to execute:");

        readCmd = new BufferedReader(new InputStreamReader(System.in));
        command = readCmd.readLine();
    }

    public void run() {
        System.out.println("Your command: " + command);
        String[] cmdParams = command.split(" ");

        for (int i = 0; i < cmdParams.length; i++) {
            if(cmdParams[i].contains("-h"))
                hoster = cmdParams[i+1];
            if(cmdParams[i].contains("-l"))
                dlUrl = cmdParams[i+1];
            if(cmdParams[i].contains("-p"))
                filePath = cmdParams[i+1];
            if(cmdParams[i].contains("-c"))
                convertToMp3 = true;
            if(cmdParams[i].contains("-d"))
                deleteMp4 = true;
            if(cmdParams[i].contains("-cp"))
                crawlProfile = true;
        }

        // force turn off if convert mp3 is not given
        if(!convertToMp3)
            deleteMp4 = false;

        if(hoster.toLowerCase().contains("yt") || hoster.toLowerCase().contains("youtube"))
            StartYouTube();
        if(hoster.toLowerCase().contains("sc") || hoster.toLowerCase().contains("soundcloud"))
            StartSoundCloud();
        if(hoster.toLowerCase().contains("ig") || hoster.toLowerCase().contains("instagram"))
            StartInstagram();
        if(hoster.toLowerCase().contains("fb") || hoster.toLowerCase().contains("facebook"))
            StartFacebook();
        if(hoster.toLowerCase().contains("vim") || hoster.toLowerCase().contains("vimeo"))
            StartVimeo();
    }

    private void StartVimeo() {

    }

    private void StartFacebook() {

    }

    private void StartInstagram() {

    }

    private void StartSoundCloud() {

    }

    private void StartYouTube() {
        YouTubeDownloader ytDl = new YouTubeDownloader(dlUrl, filePath, true);
        String vidUrl = ytDl.getVideoURL();
        int size = ytDl.getDownloadSize(vidUrl);
        System.out.println("Download Size: " + size);
        System.out.println("Downloading...");
        ytDl.DownloadFile(vidUrl, size, 0, null);
        TaskCompleted();
    }

    private void TaskCompleted(){
        System.out.println("Task completed - exiting ...");
    }
}
