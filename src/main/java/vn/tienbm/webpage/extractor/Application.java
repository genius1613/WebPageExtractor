package vn.tienbm.webpage.extractor;

/**
 * Created by tienbm on 25/04/2015.
 */

import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.Detector;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import vn.tienbm.utils.DateParser;
import vn.tienbm.utils.WebpageClient;


/**
 * Created by PCU02 on 3/25/2015.
 */

public class Application {

    public Application() {
    }

    public static void main(String[] args) throws Exception {
        Config config = new Config();
        String url = "http://www.24h.com.vn/bong-da/arsenal-chelsea-bua-tiec-thinh-soan-h1-c48a705146.html";
        String html = WebpageClient.getWebpageSource(url);
        DetectorFactory.loadProfile(config.getProfilePath());
        Detector detector = DetectorFactory.create();
        if (html != null) {
            Document doc = Jsoup.parse(html);
            System.out.println(html);
            System.out.println("Title: " + doc.getElementsByTag("title").text());
            long time = DateParser.getInstance(config).getDatetimeByStandardRegex(url, html);
            System.out.println(time);
            String text = ArticleExtractor.INSTANCE.getText(html);
            Elements newsHeadlines = doc.select("img");
            for (Element i : newsHeadlines) {
                System.out.println(i.toString());

            }

            System.out.println(text);
            detector.append(text);

            String lang = detector.detect();
            System.out.println("HumanLanguage: " + lang);
        }

    }


}
