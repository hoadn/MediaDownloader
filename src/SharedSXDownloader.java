import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by Dominik on 05.06.2015.
 */
public class SharedSXDownloader extends Downloader {
    private String hash = "";
    private String expires = "";
    private String timestamp = "";
    private String streamURL = "";
    private String dataname = "";
    private Map<String, String> cookies;

    public SharedSXDownloader(String sharedURL){

        // fill with input type to send a post request
        try {
            Connection.Response prep = Jsoup.connect(sharedURL).timeout(0)
                    .userAgent("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0").execute();
            Document preparePost = prep.parse();
            cookies = prep.cookies();



            Elements inputhidden = preparePost.select("input[type=hidden]");
            for (int i = 0; i < inputhidden.size(); i++) {
                if(inputhidden.get(i).attr("name").equals("hash"))
                    hash = inputhidden.get(i).attr("value");
                if(inputhidden.get(i).attr("name").equals("expires"))
                    expires = inputhidden.get(i).attr("value");
                if(inputhidden.get(i).attr("name").equals("timestamp"))
                    timestamp = inputhidden.get(i).attr("value");
            }

            //Document getStream = Jsoup.connect(sharedURL)
            //        .userAgent("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0")
            //        .data("hash", hash, "expires", expires, "timestamp", timestamp).post();

            Connection.Response cook = Jsoup.connect(sharedURL)
                    .userAgent("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0")
                    .data("hash", hash, "expires", expires, "timestamp", timestamp)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true).execute();
            //cookies = cook.cookies();

            Document getStream = cook.parse();

            streamURL = (getStream.select("div[class=stream-content]")).attr("data-url");
            dataname = (getStream.select("div[class=stream-content]")).attr("data-name");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getStreamURL(){
        return this.streamURL;
    }

    public String getFilename(){
        return this.dataname;
    }

    public void DownloadFile(String dlUrl, String filename,
                             SharedSXDownloaderPanel sharedSXDownloaderPanel, int fileSize) {
        try {
            String urls = "";
            String[] splitted = dlUrl.split("&");
            for (int i = 0; i < splitted.length; i++) {
                if(!splitted[i].startsWith("m=") && !urls.equals(""))
                    urls += "?" + splitted[i];
                else if(urls.equals(""))
                    urls = splitted[i];
                else
                    urls += "?m=audio/mp4";
            }

            URL url = new URL(urls);
            URLConnection hc = url.openConnection();

            hc.setReadTimeout((100*1000));
            hc.setReadTimeout((100*1000));
            hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

            InputStream in = new BufferedInputStream(hc.getInputStream());
            OutputStream out = new BufferedOutputStream(new FileOutputStream(filename));

            double sum = 0;
            int count;
            byte data[] = new byte[1024];
            // added a quick fix for downloading >= 0 instead of != -1
            while ((count = in.read(data, 0, 1024)) >= 0) {
                out.write(data, 0, count);
                sum += count;

                if (fileSize > 0 && sharedSXDownloaderPanel != null) {
                    sharedSXDownloaderPanel.setProgressBarPercentage(((int)(sum / fileSize * 100)));
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
