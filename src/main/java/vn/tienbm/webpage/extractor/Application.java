package vn.tienbm.webpage.extractor;

/**
 * Created by tienbm on 25/04/2015.
 */

import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.Detector;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import org.apache.tika.utils.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import vn.tienbm.webpage.dateparser.DateParser;

import java.text.DateFormat;
import java.util.Date;


/**
 * Created by PCU02 on 3/25/2015.
 */

public class Application {


    public static void main(String[] args) throws Exception {
        Config config = new Config();
        String url = "http://vnexpress.net/tin-tuc/the-gioi/viet-nam-trieu-dai-su-canada-phan-doi-dao-luat-hanh-trinh-den-tu-do-3205906.html";
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
