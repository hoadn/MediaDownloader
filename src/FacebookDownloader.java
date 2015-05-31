import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Dominik on 23.04.2015.
 */
public class FacebookDownloader {
    private String fbLink;
    private String fbID;
    private boolean isAlbum;
    private boolean isSingleURL;
    private String url;

    public FacebookDownloader(String fbLink){
        // determine if link is a album link
        // or complete profile (like: facebook.com/whatsapp)

        if(fbLink.contains("/media/")) {
            // id needed for processing json
            String[] URLArr = fbLink.split("/");
            String[] IDArr = URLArr[URLArr.length - 1].split("\\.");

            this.fbID = (IDArr[1].split("&"))[0];
            isAlbum = true;
        }
        else if(fbLink.contains("/?type") && fbLink.contains(("&theater"))){
            // single url
            this.isSingleURL = true;
            this.url = fbLink;
        }
        else {
            String[] URLArr = fbLink.split("/");

            this.fbID = URLArr[URLArr.length - 1];
            isAlbum = false;
        }

        this.fbLink = fbLink;
    }

    public FacebookDownloader(){

    }

    public void DownloadFile(String urls, long fileSize, int element, FacebookDownloaderPanel guiElements, String savePath){
        try {
            savePath = CheckSavePath(savePath);

            URL url = new URL(urls);
            String[] URL_split = urls.split("/");

            InputStream in = new BufferedInputStream(url.openStream());

            OutputStream out;
            if(!URL_split[URL_split.length -1].contains("?"))
                out = new BufferedOutputStream(new FileOutputStream(savePath + URL_split[URL_split.length - 1]));
            else {
                String[] URL_further = (URL_split[URL_split.length - 1]).split("\\?");
                out = new BufferedOutputStream(new FileOutputStream(savePath + URL_further[0]));
            }
            double sum = 0;
            int count;
            byte data[] = new byte[1024];
            // added a quick fix for downloading >= 0 instead of != -1
            while ((count = in.read(data, 0, 1024)) >= 0) {
                out.write(data, 0, count);
                sum += count;

                if (fileSize > 0) {
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

    public String[] GetDownloadLinks(){
        if(isSingleURL){
            JSoupAnalyze analyze = new JSoupAnalyze(url);
            Elements el = analyze.AnalyzeWithTag("img[class=fbPhotoImage img]");
            String[] urls = new String[el.size()];
            for (int i = 0; i < el.size(); i++) {
                urls[i] = el.get(i).attr("src");
            }
            return urls;
        }

        /*if(isAlbum && isVideo){
            return GetLinksFromAlbum("https://graph.facebook.com/v1.0/" + fbID + "/videos?fields=source");
        }
        else */if(isAlbum){
            String[] allPictures = GetLinksFromAlbum("https://graph.facebook.com/v1.0/" + fbID + "/photos?access_token=CAAT0ftuZAxBABAINQnxJoqFmvzuMlpUmfKZB0dawalmb3f5XmL9U2zmi6LeIZB1x822JLs4Fq7BuX7B8RRghQMr9ZAElGAaQg27OPUFmiTDVVFzjpRNuKWM49QNWWxZCZAr76ljf2Okix74LU9YMQxMZC9b0uz6JdliBlFRkVKmQcM0RkODPETRbI8BbQILiQ4SkT7MPZBbciZB50VjaCefDr&fields=source");
            try {
                String[] videos = GetLinksFromAlbum("https://graph.facebook.com/v1.0/" + fbID + "/videos?access_token=CAAT0ftuZAxBABAINQnxJoqFmvzuMlpUmfKZB0dawalmb3f5XmL9U2zmi6LeIZB1x822JLs4Fq7BuX7B8RRghQMr9ZAElGAaQg27OPUFmiTDVVFzjpRNuKWM49QNWWxZCZAr76ljf2Okix74LU9YMQxMZC9b0uz6JdliBlFRkVKmQcM0RkODPETRbI8BbQILiQ4SkT7MPZBbciZB50VjaCefDr&fields=source");
                return Stream.concat(Arrays.stream(allPictures), Arrays.stream(videos))
                        .toArray(String[]::new);
            }catch (Exception ex){
                return allPictures;
            }
        }
        else if(!isAlbum){
            String[] album_id = GetAlbumsFromProfile("https://graph.facebook.com/" + fbID + "/albums?access_token=CAAT0ftuZAxBABAINQnxJoqFmvzuMlpUmfKZB0dawalmb3f5XmL9U2zmi6LeIZB1x822JLs4Fq7BuX7B8RRghQMr9ZAElGAaQg27OPUFmiTDVVFzjpRNuKWM49QNWWxZCZAr76ljf2Okix74LU9YMQxMZC9b0uz6JdliBlFRkVKmQcM0RkODPETRbI8BbQILiQ4SkT7MPZBbciZB50VjaCefDr&fields=id");
            List<String> links = new ArrayList<>();

            for (int i = 0; i < album_id.length; i++) {
                String[] tmp = GetLinksFromAlbum("https://graph.facebook.com/v1.0/" + album_id[i] + "/photos?access_token=CAAT0ftuZAxBABAINQnxJoqFmvzuMlpUmfKZB0dawalmb3f5XmL9U2zmi6LeIZB1x822JLs4Fq7BuX7B8RRghQMr9ZAElGAaQg27OPUFmiTDVVFzjpRNuKWM49QNWWxZCZAr76ljf2Okix74LU9YMQxMZC9b0uz6JdliBlFRkVKmQcM0RkODPETRbI8BbQILiQ4SkT7MPZBbciZB50VjaCefDr&fields=source");

                for (int j = 0; j < tmp.length; j++) {
                    links.add(tmp[j]);
                }
            }

            String[] allPictures = new String[links.size()];
            for (int i = 0; i < links.size(); i++) {
                allPictures[i] = links.get(i);
            }

            // if isVideo == true select all videos
            try {
                String[] videos = GetLinksFromAlbum("https://graph.facebook.com/v1.0/" + fbID + "/videos?access_token=" +
                        "CAAT0ftuZAxBABAINQnxJoqFmvzuMlpUmfKZB0dawalmb3f5XmL9U2zmi6LeIZB1x822JLs4Fq7BuX7B8RRghQMr9ZAElGAaQg27OPUFmiTDVVFzjpRNuKWM49QNWWxZCZAr76ljf2Okix74LU9YMQxMZC9b0uz6JdliBlFRkVKmQcM0RkODPETRbI8BbQILiQ4SkT7MPZBbciZB50VjaCefDr" +
                        "&fields=source");
                return Stream.concat(Arrays.stream(allPictures), Arrays.stream(videos))
                        .toArray(String[]::new);
            }catch (Exception ex){
                return allPictures;
            }
        }
        else
            return null;
    }

    private String[] GetAlbumsFromProfile(String url){
        try {
            JSONObject obj = readJsonFromUrl(url);
            String nextPage;
            try {
                nextPage = obj.getJSONObject("paging").getString("next");
            }catch (Exception ex){
                nextPage = "";
            }

            JSONArray arr = obj.getJSONArray("data");
            List<String> itemlist = new ArrayList<String>();

            for (int i = 0; i < arr.length(); i++) {
                try {
                    itemlist.add(arr.getJSONObject(i).getString("id"));
                }
                catch (Exception ex){
                    // ignore because if no video id is found move on
                }
            }

            String[] items = new String[itemlist.size()];
            for (int i = 0; i < itemlist.size(); i++) {
                items[i] = itemlist.get(i);
            }

            if(nextPage.equals(""))
                return items;
            else
                return Stream.concat(Arrays.stream(items), Arrays.stream(GetLinksFromAlbum(nextPage)))
                        .toArray(String[]::new);
        }catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    private String[] GetLinksFromAlbum(String url){
        String requestURL = url;

        try {
            JSONObject obj = readJsonFromUrl(requestURL);
            String nextPage;
            try {
                nextPage = obj.getJSONObject("paging").getString("next");
            }catch (Exception ex){
                nextPage = "";
            }

            JSONArray arr = obj.getJSONArray("data");
            List<String> itemlist = new ArrayList<String>();

            for (int i = 0; i < arr.length(); i++) {
                try {
                    itemlist.add(arr.getJSONObject(i).getString("source"));
                }
                catch (Exception ex){
                    // ignore because if no video id is found move on
                }
            }

            String[] items = new String[itemlist.size()];
            for (int i = 0; i < itemlist.size(); i++) {
                items[i] = itemlist.get(i);
            }

            if(nextPage.equals(""))
                return items;
            else
                return Stream.concat(Arrays.stream(items), Arrays.stream(GetLinksFromAlbum(nextPage)))
                        .toArray(String[]::new);
        }catch (IOException ex){
            System.err.println("No links found or private album!");
            return null;
        }
    }

    public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is;
        try {
            is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } catch (Exception ex){
            System.err.println("No vid found on this node");
            return null;
        }
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private String CheckSavePath(String pathToCheck) {
        if(System.getProperty("os.name").contains("Windows")) {
            if (!pathToCheck.endsWith("\\")) {
                pathToCheck = pathToCheck + "\\";
            }

            if (!Files.isDirectory(Paths.get(pathToCheck))) {
                try {
                    Files.createDirectory(Paths.get(pathToCheck));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return pathToCheck;
        }
        else if(System.getProperty("os.name").contains("nux")){
            if(!pathToCheck.endsWith("/"))
                pathToCheck = pathToCheck + "/";

            if(!Files.isDirectory(Paths.get(pathToCheck))){
                try{
                    Files.createDirectory(Paths.get(pathToCheck));
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            return pathToCheck;
        }
        else
            return pathToCheck;
    }

    public Long getDownloadSize(String urls){
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(urls).openConnection();
            conn.connect();
            return Long.parseLong(conn.getHeaderField("Content-Length"));
        }catch (Exception ex){
            System.err.println("No content-length given... continue");
            //ex.printStackTrace();
            return (long)0;
        }
    }
}
