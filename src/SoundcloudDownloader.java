import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.net.URL;

/**
 * Created by Dominik on 31.05.2015.
 */
public class SoundcloudDownloader extends Downloader{
    private JSoupAnalyze webObj;
    private SettingsManager settingsManager;

    private String baseURI = "https://api.soundcloud.com/tracks/";
    private String clientID = "b45b1aa10f1ac2941910a7f0d10f8e28";
    private String trackID;

    private String savePath;
    private String audioName;
    private String soundcloud_url;

    public SoundcloudDownloader(String soundcloud_url, String savePath){
        super();
        settingsManager = new SettingsManager();
        try{
            this.soundcloud_url = soundcloud_url;
            this.savePath = CheckSavePath(savePath);

            webObj = new JSoupAnalyze(this.soundcloud_url);
            this.audioName = validateFileName
                    (webObj.AnalyzeWithTag("meta[property=og:title]").get(0).attr("content"));
            String[] track = (webObj.AnalyzeWithTag("meta[property=twitter:app:url:googleplay]").
                    get(0).attr("content")).split(":");
            this.trackID = track[track.length - 1];
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public String getAudioURL(){
        try {
            Connection.Response res = Jsoup.
                    connect(baseURI + trackID + "/stream?client_id=" + clientID)
                    .ignoreContentType(true).followRedirects(false).execute();
            //cookies = res.cookies();

            return res.header("location");
        }catch (Exception ex){
            ex.printStackTrace();
            return "null";
        }
    }

    public void DownloadFile(String urls, int fileSize, int element, SoundcloudDownloaderPanel guiElements){
        try {
            URL url = new URL(urls);
            InputStream in = new BufferedInputStream(url.openStream());
            OutputStream out = new BufferedOutputStream(new FileOutputStream(savePath + this.audioName + ".mp3"));

            double sum = 0;
            int count;
            byte data[] = new byte[1024];
            // added a quick fix for downloading >= 0 instead of != -1
            while ((count = in.read(data, 0, 1024)) >= 0) {
                out.write(data, 0, count);
                sum += count;

                if (fileSize > 0 && guiElements != null) {
                    guiElements.setElementPercentage(((int)(sum / fileSize * 100)) + "%", element);
                }
            }


            in.close();
            out.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
