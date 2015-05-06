package vn.tienbm.webpage.extractor;

/**
 * Created by tienbm on 27/04/2015.
 */

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.*;

public class ContentExtractor {

    public final static ContentExtractor INSTANCE = new ContentExtractor();

    public ContentExtractor getInstances() {
        return INSTANCE;
    }

    public String getArticleContent(String html) throws BoilerpipeProcessingException {
        return ArticleExtractor.INSTANCE.getText(html);
    }

    public String getLargestContent(String html) throws BoilerpipeProcessingException {
        return ArticleExtractor.INSTANCE.getText(html);
    }

    public String getEverythingContent(String html) throws BoilerpipeProcessingException {
        return ArticleExtractor.INSTANCE.getText(html);
    }

    public ExtractorBase getArticleExtractor() {
        return ArticleExtractor.INSTANCE;
    }

    public ExtractorBase getLargestContentExtractor() {
        return LargestContentExtractor.INSTANCE;
    }

    public ExtractorBase getDefaultExtractor() {
        return DefaultExtractor.getInstance().INSTANCE;
    }

    public ExtractorBase getEveryThingExtractor() {
        return KeepEverythingExtractor.INSTANCE;
    }
}
