package vn.tienbm.webpage.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import vn.tienbm.pojo.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tienbm on 27/04/2015.
 */
public class HtmlExtractor {

    public final static HtmlExtractor INSTANCE = new HtmlExtractor();

    private Document document;
    List<String> images;

    public HtmlExtractor(String html) {
        document = Jsoup.parse(html);
        images = new ArrayList<String>();
    }

    public HtmlExtractor() {

    }

    public List<String> getImageTags() {
        Elements newsHeadlines = document.select("img");
        for (Element i : newsHeadlines) {
            System.out.println(i.toString());
            images.add(i.toString());
        }
        return images;
    }

    public List<Image> getImages() {
        List<Image> imageList = new ArrayList<Image>();
        Elements newsHeadlines = document.select("img");
        for (Element i : newsHeadlines) {
            Image img = new Image();
            img.setAlign(i.attr("align"));
            img.setAlt(i.attr("alt"));
            img.setBorder(i.attr("border"));
            img.setCrossorigin(i.attr("crossorigin"));
            img.setHeight(i.attr("height"));
            img.setHspace(i.attr("hspace"));
            img.setIsmap(i.attr("ismap"));
            img.setLongdesc(i.attr("longdesc"));
            img.setSrc(i.attr("src"));
            img.setUsemap(i.attr("usemap"));
            img.setVspace(i.attr("vspace"));
            img.setWidth(i.attr("width"));
            imageList.add(img);
        }
        return imageList;
    }
}
