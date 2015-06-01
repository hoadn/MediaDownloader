import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Dominik on 22.04.2015.
 */
public class InstagramDownloader extends Downloader{
    private String igLink;
    private String savePath;
    private String img_Title;
    private String img_URL;
    private String usrID;

    private boolean isPreviewWanted;

    private ImagePreviewWindow imgPreview;
    private JSoupAnalyze webObj;
    private InstagramDownloadWindow downloadWindow;

    public InstagramDownloader(String igLink, String savePath, boolean isPreviewWanted){
        super();
        this.igLink = igLink;
        this.savePath = savePath;
        this.isPreviewWanted = isPreviewWanted;
    }

    // constructor for crawling instagram profile
    public InstagramDownloader(String igLink, String savePath){
        super();
        if(igLink.contains("userID:"))
            this.usrID = igLink.replace("userID:", "");
        else
            this.igLink = igLink.replace("user:", "");

        this.savePath = savePath;
    }

    public String GetURLsAndPreview(){
        try {
            webObj = new JSoupAnalyze(igLink);
            Element videos = webObj.AnalyzeWithTag("meta[property=og:video]").first();
            Element images = webObj.AnalyzeWithTag("meta[property=og:image]").first();
            Element descr = webObj.AnalyzeWithTag("meta[property=og:description]").first();

            if (videos != null) { // found videos
                if (isPreviewWanted) {
                    imgPreview = new ImagePreviewWindow(images.attr("content"), descr.attr("content"));
                    return videos.attr("content");
                }
                else
                    return videos.attr("content");

            } else if (images != null) { // found images
                if(isPreviewWanted){
                    imgPreview = new ImagePreviewWindow(images.attr("content"), descr.attr("content"));
                    return images.attr("content");
                }
                else
                    return images.attr("content");
            } else
                return "";
        }
        catch (Exception ex){
            ex.printStackTrace();
            return "";
        }
    }

    public void DownloadFile(String urls, int element, int fileSize){
        boolean showProgress = true;

        if(urls.equals(""))
            return;

        if(downloadWindow == null)
            showProgress = false;

        try {
            savePath = CheckSavePath(savePath);

            URL url = new URL(urls);
            String[] URL_split = urls.split("/");

            InputStream in = new BufferedInputStream(url.openStream());

            OutputStream out;
            out = new BufferedOutputStream(new FileOutputStream(savePath + URL_split[URL_split.length - 1]));

            double sum = 0;
            int count;
            byte data[] = new byte[1024];
            // added a quick fix for downloading >= 0 instead of != -1
            while ((count = in.read(data, 0, 1024)) >= 0) {
                out.write(data, 0, count);
                sum += count;

                if (fileSize > 0 && showProgress) {
                    downloadWindow.setElementPercentage(((int)(sum / fileSize * 100)) + "%", element);
                }
            }


            in.close();
            out.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void DownloadFile(String[] urls){
        for (int i = 0; i < urls.length; i++) {
            DownloadFile(urls[i], i, getDownloadSize(urls[i]));
            if(downloadWindow != null)
                downloadWindow.SetOverallProgress("[" + (i+1) + "/" + urls.length + "]");
        }
    }

    public String fetchUserID(String requestURL) {
        if(usrID != null)
            return usrID;

        try {
            requestURL = requestURL.replace("{user}", igLink);
            JSONObject obj = readJsonFromUrl(requestURL);
            String userID = obj.getJSONArray("data").getJSONObject(0).getString("id").toString();
            return userID;
        }catch (Exception ex){
            ex.printStackTrace();
            return "Error occured while crawling";
        }
    }

    public String[] fetchAllImageURLs(String userID, String newURL) {
        if(newURL.equals(""))
            newURL = "https://api.instagram.com/v1/users/" + userID + "/media/recent?client_id=21ae9c8b9ebd4183adf0d0602ead7f05";
        try{
            JSONObject obj = readJsonFromUrl(newURL);
            String nextPage = "";
            try{
                nextPage = obj.getJSONObject("pagination").getString("next_url");
            }catch (Exception ex){
                System.err.println("No next page");
            }

            JSONArray arr = obj.getJSONArray("data");
            List<String> itemlist = new ArrayList<String>();

            for (int i = 0; i < arr.length(); i++) {
                try {
                    itemlist.add(arr.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution").getString("url"));
                    itemlist.add(arr.getJSONObject(i).getJSONObject("videos").getJSONObject("standard_resolution").getString("url"));
                }
                catch (Exception ex){
                    // ignore this case
                }
            }

            String[] items = new String[itemlist.size()];
            for (int i = 0; i < itemlist.size(); i++) {
                items[i] = itemlist.get(i);
            }

            if(nextPage.equals(""))
                return items;
            else
                return Stream.concat(Arrays.stream(items), Arrays.stream(fetchAllImageURLs(userID, nextPage)))
                        .toArray(String[]::new);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public void setDownloadWindow(InstagramDownloadWindow downloadWindow) {
        this.downloadWindow = downloadWindow;
    }
}
