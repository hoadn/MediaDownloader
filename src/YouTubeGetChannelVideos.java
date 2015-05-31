import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Dominik on 19.04.2015.
 */
public class YouTubeGetChannelVideos {
    private String username;

    public YouTubeGetChannelVideos(String username){
        this.username = username;
    }

    private String GetChannelIDFromUsername(String username) {
        try{
            JSONObject obj = readJsonFromUrl("https://www.googleapis.com/youtube/v3/channels?part=id&forUsername=" +
                    username +
                    "&key=AIzaSyC7vBPNYtT_eNy90WBZ-uQ7KwZ-iqInXcE");
            JSONArray arr = obj.getJSONArray("items");
            String channelID = arr.getJSONObject(0).getString("id");
            return channelID;
        }catch (Exception ex) {
            //no user found
            return "";
        }
    }

    private String[] GetAllVideoIdsFromChannelID(String channelID, String nextPageToken){
        try {
            JSONObject obj = readJsonFromUrl("https://www.googleapis.com/youtube/v3/search?" +
                    "key=AIzaSyC7vBPNYtT_eNy90WBZ-uQ7KwZ-iqInXcE&channelId=" + channelID +
                    "&part=id&order=date&maxResults=50&nextPageToken&pageToken=" + nextPageToken);
            String token;
            try {
                token = obj.getString("nextPageToken");
            }catch (Exception ex){
                token = "";
            }

            JSONArray arr = obj.getJSONArray("items");
            List<String> itemlist = new ArrayList<String>();

            for (int i = 0; i < arr.length(); i++)
            {
                try {
                    itemlist.add(arr.getJSONObject(i).getJSONObject("id").getString("videoId"));
                }
                catch (Exception ex){
                    // ignore because if no video id is found move on
                }
            }

            String[] items = new String[itemlist.size()];
            for (int i = 0; i < itemlist.size(); i++) {
                items[i] = itemlist.get(i);
            }

            if(token.equals(""))
                return items;
            else
                return Stream.concat(Arrays.stream(items), Arrays.stream(GetAllVideoIdsFromChannelID(channelID, token)))
                        .toArray(String[]::new);
        }catch (IOException ex){
            System.err.println("IO Exception");
            return null;
        }
    }

    public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
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

    public String[] GetVideoList(){
        String channelID = GetChannelIDFromUsername(this.username);
        System.out.println("Channel ID is: " + channelID);

        if(channelID.equals(""))
            return new String[0];

        String[] allVids = GetAllVideoIdsFromChannelID(channelID, "");
        System.out.println("Number of videos found: " + allVids.length);
        return allVids;
    }
}
