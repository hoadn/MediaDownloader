import org.json.JSONException;
import org.json.JSONObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Creation time: 03:04
 * Created by Dominik on 01.06.2015.
 */
public abstract class Downloader {

    // Methods any Downloader need
    public boolean isFileExisting(File fileToCheck){
        try {
            if (fileToCheck.exists())
                return true;
            else
                return false;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public int getDownloadSize(String urls){
        URLConnection hUrl;
        try {
            hUrl = new URL(urls).openConnection();
            int size = hUrl.getContentLength();
            return size;

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String CheckSavePath(String pathToCheck) {
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

    public String validateFileName(String name){
        if(name.contains("|"))
            name = name.replace("|", "_");

        if(name.contains(">"))
            name = name.replace(">", "_");

        if(name.contains("<"))
            name = name.replace("<", "_");

        if(name.contains("\""))
            name = name.replace("\"", "_");

        if(name.contains("?"))
            name = name.replace("?", "_");

        if(name.contains("*"))
            name = name.replace("*", "_");

        if(name.contains(":"))
            name = name.replace(":", "_");

        if(name.contains("\\\\"))
            name = name.replace("\\\\", "_");

        if(name.contains("/"))
            name = name.replace("/", "_");

        return name;
    }

    public String decodeJScriptURL(String toDecode){
        try {
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("JavaScript");
            return (String) engine.eval("unescape('" + toDecode + "')");
        } catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject readJsonFromUrl(String url) throws JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }catch (Exception ex){
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
}
