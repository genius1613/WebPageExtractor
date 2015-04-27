package vn.tienbm.utils;

/**
 * Created by tienbm on 27/04/2015.
 */

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;
import vn.tienbm.webpage.extractor.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 3/25/2015.
 */
public class LanguageDetector {
    private String c_langProfile = "resources/profiles";

    // Private constructor prevents instantiation from other classes
    private LanguageDetector() {
        try {
            DetectorFactory.loadProfile(c_langProfile);
        } catch (LangDetectException e) {
        }
    }

    private LanguageDetector(Config config) {
        try {
            DetectorFactory.loadProfile(config.getProfilePath());
        } catch (LangDetectException e) {
        }
    }


    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class LanguageDetectorHolder {
        private static final LanguageDetector INSTANCE = new LanguageDetector();
    }

    public static LanguageDetector getInstance() {
        return LanguageDetectorHolder.INSTANCE;
    }

    public String detect(String text) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.detect();
    }

    public ArrayList<Language> detectLangs(String text) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.getProbabilities();
    }

    // langState: af--> Afrikaans; ar--> Arabic; bg--> Bulgarian; bn--> Bengali; cs--> Czech; da--> Danish; de--> German; el--> Greek; en--> English; es--> Spanish; et--> Estonian; fa--> Persian; fi--> Finnish; fr--> French; gu--> Gujarati; he--> Hebrew; hi--> Hindi; hr--> Croatian; hu--> Hungarian; id--> Indonesian; it--> Italian; ja--> Japanese; kn--> Kannada; ko--> Korean; lt--> Lithuanian; lv--> Latvian; mk--> Macedonian; ml--> Malayalam; mr--> Marathi; ne--> Nepali; nl--> Dutch; no--> Norwegian; pa--> Punjabi; pl--> Polish; pt--> Portuguese; ro--> Romanian; ru--> Russian; sk--> Slovak; sl--> Slovene; so--> Somali; sq--> Albanian; sv--> Swedish; sw--> Swahili; ta--> Tamil; te--> Telugu; th--> Thai; tl--> Tagalog; tr--> Turkish; uk--> Ukrainian; ur--> Urdu; vi--> Vietnamese; zh-cn--> Simplified Chinese; zh-tw--> Traditional Chinese
    public boolean isSpecLangDetected(String text, String langState, double threshold) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        List<Language> list_lang = detector.getProbabilities();
        boolean is_detect = false;
        for (Language lan : list_lang) {
            if (lan.lang.indexOf(langState) >= 0 && lan.prob >= threshold) {
                is_detect = true;
            }
        }

        return is_detect;
    }
}
