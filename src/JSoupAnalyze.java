import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Dominik on 20.04.2015.
 */
public class JSoupAnalyze {
    private Document site;
    private boolean isFacebookLink;

    public JSoupAnalyze(String webURL){
        try {
            // set to infinite time out ... may be its better
            site = Jsoup.connect(webURL).timeout(0).get();

            if(!webURL.contains("facebook"))
                return;

            // Notify the user we got a facebook link here and need proceed a bit different
            // (debug purposes)

            //System.out.println("Facebook link found - need to proceed a bit different");
            isFacebookLink = true;
            site = Jsoup.parse(site.toString().replace("<!--", ""));
            site = Jsoup.parse(site.toString().replace("&lt;!--", ""));
            site = Jsoup.parse(site.toString().replace("-->", ""));
            site = Jsoup.parse(site.toString().replace("--&gt;", ""));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Elements AnalyzeWithTag(String analyzeTag){
        return site.select(analyzeTag);
    }

    public String GetCompleteBodyHTML(){
        return site.body().outerHtml();
    }

    public boolean GetIsFacebookLink(){
        return isFacebookLink;
    }

    public String GetAbsoluteHrefURL(Element elem){
        if(isFacebookLink)
            return "https://facebook.com" + elem.attr("href");
        else
            return elem.attr("abs:href");
    }
}
