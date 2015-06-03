import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Dominik on 03.06.2015.
 */
public class YouTubeRetrievePlaylist extends Downloader {
    private String baseRequest = "https://www.googleapis.com/youtube/v3/playlistItems";
    private String baseAddOn = "?part=contentDetails&maxResults=50&playlistId=";
    private String pageToken = "&pageToken=";
    private String key = "&key=AIzaSyC7vBPNYtT_eNy90WBZ-uQ7KwZ-iqInXcE";
    private String playlistId;
    private List<String> videoIdList = new ArrayList<String>();
    private String playListTitle = "";

    public YouTubeRetrievePlaylist(String playlistLink){
        if(playlistLink.contains("&list")) {
            String[] splitted = playlistLink.split("&");
            for (int i = 0; i < splitted.length; i++) {
                if (splitted[i].contains("list="))
                    playlistId = splitted[i].replace("list=", "");
            }
        }else if(playlistLink.contains("?list")){
            String[] splitted = playlistLink.split("\\?");
            for (int i = 0; i < splitted.length; i++) {
                if (splitted[i].contains("list="))
                    playlistId = splitted[i].replace("list=", "");
            }
        }else{
            System.err.println("No playlist found!");
        }
    }

    public String[] getAllVideosFromPlaylist(String token){
        String request;
        if(token.equals(""))
            request = baseRequest + baseAddOn + playlistId + key;
        else
            request = baseRequest + baseAddOn + playlistId + pageToken + token + key;
        try {
            JSONObject obj = readJsonFromUrl(request);
            String nextPage = "";
            try {
                nextPage = obj.getString("nextPageToken");
            } catch (Exception ex) {
                // no next page so its done
                System.err.println("No next page!");
            }

            try{
                playListTitle = readJsonFromUrl("https://www.googleapis.com/youtube/v3/playlists?part=snippet&id="
                        + playlistId + key)
                        .getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("title");
            }catch (Exception ex){
                System.err.println("No title!");
            }

            JSONArray array = obj.getJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                videoIdList.add("http://youtube.com/watch?v=" + array.getJSONObject(i).getJSONObject("contentDetails").getString("videoId"));
            }
            String[] items = new String[videoIdList.size()];
            for (int i = 0; i < videoIdList.size(); i++) {
                items[i] = videoIdList.get(i);
            }
            videoIdList.clear();

            if(nextPage.equals(""))
                return items;
            else
                return Stream.concat(Arrays.stream(items), Arrays.stream(getAllVideosFromPlaylist(nextPage)))
                        .toArray(String[]::new);

        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public String getPlayListTitle(){
        return validateFileName(playListTitle);
    }
}
