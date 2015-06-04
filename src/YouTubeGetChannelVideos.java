import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Creation time: 03:05
 * Created by Dominik on 19.04.2015.
 */
public class YouTubeGetChannelVideos extends Downloader {
    private String username;

    public YouTubeGetChannelVideos(String username){
        super();
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
            List<String> itemlist = new ArrayList<>();

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
        }catch (Exception ex){
            System.err.println("IO Exception");
            return null;
        }
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
