package vn.tienbm.webpage.extractor;

import org.junit.Test;

/**
 * Created by tienbm on 25/04/2015.
 */
public class TestDateParser {

    @Test
    public void testLoadConfig(){
        Config config = new Config();
        System.out.println(config.getRegexPath("regex_url.conf"));
        System.out.println(config.getRegexPath("regex_content.conf"));
    }
}
