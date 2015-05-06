package vn.tienbm;

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
import vn.tienbm.utils.DateTimeExtractor;
import vn.tienbm.utils.WebpageClient;
import vn.tienbm.webpage.extractor.Config;

import java.text.SimpleDateFormat;


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
            long time = DateTimeExtractor.getInstance(config).getDatetimeByStandardRegex(url, html);
            System.out.println(time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");
            System.out.println(simpleDateFormat.format(new java.sql.Timestamp(time)));

            String text = ArticleExtractor.INSTANCE.getText(html);
//String text = KeepEverythingExtractor.INSTANCE.getText(html);
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
