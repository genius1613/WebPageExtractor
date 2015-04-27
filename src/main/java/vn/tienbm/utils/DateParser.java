package vn.tienbm.utils;

/**
 * Created by tienbm on 12/03/2015.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.tienbm.webpage.extractor.Config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Singleton
public class DateParser {
    public static final Logger LOG = LoggerFactory.getLogger(DateParser.class);

    private String YEAR_LABLE = "pubYear";
    private String MON_LABLE = "pubMon";
    private String DATE_LABLE = "pubDate";
    private String HOUR_LABLE = "pubHour";
    private String MINUTE_LABLE = "pubMinute";
    private String TYPE_LABLE = "pubType";
    private String ZONE_LABLE = "pubZone";

    private String c_regexURL = "resources/regex_url.conf";
    private String c_regexContent = "resources/regex_content.conf";

    private String WEB_SERVICE_URL;

    public static final long TEN_YEAR = 1000l * 3600l * 24l * 365l * 10l;
    private Map<String, String> c_monthIndex = new HashMap<String, String>();
    private List<String> c_patterns = new ArrayList<String>();
    static List<String> c_listURLPatterns = new ArrayList<String>();
    static List<String> c_listContentPatterns = new ArrayList<String>();

    // Private constructor prevents instantiation from other classes
    private DateParser(String url) {
        /////////////////////////////////////////////
        // Map to get month
        c_monthIndex.put("má»™t", "1");
        c_monthIndex.put("hai", "2");
        c_monthIndex.put("ba", "3");
        c_monthIndex.put("tÆ°", "4");
        c_monthIndex.put("bá»‘n", "4");
        c_monthIndex.put("nÄƒm", "5");
        c_monthIndex.put("sÃ¡u", "6");
        c_monthIndex.put("báº£y", "7");
        c_monthIndex.put("tÃ¡m", "8");
        c_monthIndex.put("chÃ­n", "9");
        c_monthIndex.put("mÆ°á»i", "10");
        c_monthIndex.put("mÆ°á»i má»™t", "11");
        c_monthIndex.put("mÆ°á»i hai", "12");

        c_monthIndex.put("january", "1");
        c_monthIndex.put("february", "2");
        c_monthIndex.put("march", "3");
        c_monthIndex.put("april", "4");
        c_monthIndex.put("may", "5");
        c_monthIndex.put("june", "6");
        c_monthIndex.put("july", "7");
        c_monthIndex.put("august", "8");
        c_monthIndex.put("september", "9");
        c_monthIndex.put("october", "10");
        c_monthIndex.put("november", "11");
        c_monthIndex.put("december", "12");

        c_monthIndex.put("jan", "1");
        c_monthIndex.put("feb", "2");
        c_monthIndex.put("mar", "3");
        c_monthIndex.put("apr", "4");
        c_monthIndex.put("may", "5");
        c_monthIndex.put("jun", "6");
        c_monthIndex.put("jul", "7");
        c_monthIndex.put("aug", "8");
        c_monthIndex.put("sep", "9");
        c_monthIndex.put("oct", "10");
        c_monthIndex.put("nov", "11");
        c_monthIndex.put("dec", "12");

        c_monthIndex.put("1", "1");
        c_monthIndex.put("2", "2");
        c_monthIndex.put("3", "3");
        c_monthIndex.put("4", "4");
        c_monthIndex.put("5", "5");
        c_monthIndex.put("6", "6");
        c_monthIndex.put("7", "7");
        c_monthIndex.put("8", "8");
        c_monthIndex.put("9", "9");
        c_monthIndex.put("10", "10");
        c_monthIndex.put("11", "11");
        c_monthIndex.put("12", "12");

        c_monthIndex.put("01", "1");
        c_monthIndex.put("02", "2");
        c_monthIndex.put("03", "3");
        c_monthIndex.put("04", "4");
        c_monthIndex.put("05", "5");
        c_monthIndex.put("06", "6");
        c_monthIndex.put("07", "7");
        c_monthIndex.put("08", "8");
        c_monthIndex.put("09", "9");

        /////////////////////////////////////////////
        // Pattern to get date

        //Friday, March 13, 2015 3:34:29 PM
        //Friday, March 13, 2015 3:34
        // Format: April 20, 2014
        c_patterns.add("(?:(?:Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday),\\s*)?(?<pubMon>January|February|March|April|May|June|July|August|September|October|November|December)\\s*(?<pubDate>(?:[12]\\d)|30|31|(?:0?\\d)),\\s*(?<pubYear>2[01]\\d{2})(?:\\s*(?<pubHour>\\d{1,2}):(?<pubMinute>\\d{1,2})(?::\\d{1,2}\\s*(?<pubType>AM|PM))?)?");

        // Format: 13/04/2014
        // Format: 13-04-2014
        c_patterns.add("(?<pubDate>(?:[12][1-9])|30|31|(?:0?[1-9]))[\\/\\-](?<pubMon>(?:0?[1-9])|(?:1[012]))[\\/\\-](?<pubYear>2[01]\\d{2})");

        // Format: 2014-2-22
        // Format: 2014/2/22
        // https://www.schneier.com/blog/archives/2014/04/heartbleed.htmlâ€Ž
        c_patterns.add("(?<pubYear>2[01]\\d{2})[\\/\\-](?<pubMon>(?:1[012])|(?:0?[1-9]))(?:[\\/\\-](?<pubDate>(?:[12][1-9])|30|31|(?:0?[1-9])))T?(?:(?<pubHour>\\d{1,2}):(?<pubMinute>\\d{1,2})(?::\\d{1,2}\\s*(?<pubZone>[\\-\\+]\\d{2})?)?)?");

        // Format: 13/04/14, 13-04-07
        c_patterns.add("(?<pubDate>(?:[12][1-9])|30|31|(?:0?[1-9]))[\\/\\-](?<pubMon>(?:0?[1-9])|(?:1[012]))[\\/\\-](?<pubYear>[0-9]{2})");

        // Format: http://trachnhiemonline.com/Nhan-Dinh/ND-150316-LaTuCuoiCungCuaTongThongNguyenVanThieu.htm
        // Format: 20100725
        // Format: 25072010
        c_patterns.add("(?<pubDate>(?:[12][1-9])|30|31|(?:0?[1-9]))(?<pubMon>(?:1[012])|(?:0?[1-9]))(?<pubYear>(?:2[01]\\d{2})|(?:[0-9]{2}))");
        c_patterns.add("(?<pubYear>(?:2[01]\\d{2})|(?:[0-9]{2}))(?<pubMon>(?:1[012])|(?:0?[1-9]))(?<pubDate>(?:[12][1-9])|30|31|(?:0?[1-9]))");

        // Format: 13 ThÃ¡ng SÃ¡u, 2013
        c_patterns.add("(?<pubDate>(?:[12][1-9])|30|31|(?:0?[1-9]))\\s*th.ng\\s*(?<pubMon>(?:m..i\\s*)?.{2,4}),\\s*(?<pubYear>2[01]\\d{2})");

        // Format: ThÃ¡ng Ba 9, 2012
        c_patterns.add("th.ng\\s*(?<pubMon>(?:m..i\\s*)?.{2,4})\\s*(?<pubDate>(?:[12][1-9])|30|31|(?:0?[1-9])),\\s*(?<pubYear>2[01]\\d{2})");

        // Format: 10 October 2013
        c_patterns.add("(?<pubDate>[12]\\d|30|31|0?[1-9])\\s*(?<pubMon>January|February|March|April|May|June|July|August|September|October|November|December)\\s*(?<pubYear>2[01]\\d{2})");

        // Format: 2012-Oct-15
        c_patterns.add("(?<pubYear>2[01]\\d{2})\\-(?<pubMon>Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec).?\\-(?<pubDate>[12]\\d|30|31|0?[1-9])");

        // Format: Nov 2, 2006
        c_patterns.add("(?<pubMon>Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec).?\\s*(?<pubDate>[12]\\d|30|31|0?[1-9]),\\s*(?<pubYear>2[01]{1}\\d{2})");

        // Format: Dec 12 '12
        c_patterns.add("(?<pubMon>Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec).?\\s*(?<pubDate>[12]\\d|30|31|0?[1-9])\\s*'(?<pubYear>\\d{2})");

        this.WEB_SERVICE_URL = url;
        c_listURLPatterns = loadListPatternsFromFile("/home/tienbm/IdeaProjects/BigCrawler/implementation/datcm/nutch-2.x/conf/regex_url.conf");
        c_listContentPatterns = loadListPatternsFromFile("/home/tienbm/IdeaProjects/BigCrawler/implementation/datcm/nutch-2.x/conf/regex_content.conf");
    }

    public DateParser(Config config) {
        c_monthIndex.put("má»™t", "1");
        c_monthIndex.put("hai", "2");
        c_monthIndex.put("ba", "3");
        c_monthIndex.put("tÆ°", "4");
        c_monthIndex.put("bá»‘n", "4");
        c_monthIndex.put("nÄƒm", "5");
        c_monthIndex.put("sÃ¡u", "6");
        c_monthIndex.put("báº£y", "7");
        c_monthIndex.put("tÃ¡m", "8");
        c_monthIndex.put("chÃ­n", "9");
        c_monthIndex.put("mÆ°á»i", "10");
        c_monthIndex.put("mÆ°á»i má»™t", "11");
        c_monthIndex.put("mÆ°á»i hai", "12");

        c_monthIndex.put("january", "1");
        c_monthIndex.put("february", "2");
        c_monthIndex.put("march", "3");
        c_monthIndex.put("april", "4");
        c_monthIndex.put("may", "5");
        c_monthIndex.put("june", "6");
        c_monthIndex.put("july", "7");
        c_monthIndex.put("august", "8");
        c_monthIndex.put("september", "9");
        c_monthIndex.put("october", "10");
        c_monthIndex.put("november", "11");
        c_monthIndex.put("december", "12");

        c_monthIndex.put("jan", "1");
        c_monthIndex.put("feb", "2");
        c_monthIndex.put("mar", "3");
        c_monthIndex.put("apr", "4");
        c_monthIndex.put("may", "5");
        c_monthIndex.put("jun", "6");
        c_monthIndex.put("jul", "7");
        c_monthIndex.put("aug", "8");
        c_monthIndex.put("sep", "9");
        c_monthIndex.put("oct", "10");
        c_monthIndex.put("nov", "11");
        c_monthIndex.put("dec", "12");

        c_monthIndex.put("1", "1");
        c_monthIndex.put("2", "2");
        c_monthIndex.put("3", "3");
        c_monthIndex.put("4", "4");
        c_monthIndex.put("5", "5");
        c_monthIndex.put("6", "6");
        c_monthIndex.put("7", "7");
        c_monthIndex.put("8", "8");
        c_monthIndex.put("9", "9");
        c_monthIndex.put("10", "10");
        c_monthIndex.put("11", "11");
        c_monthIndex.put("12", "12");

        c_monthIndex.put("01", "1");
        c_monthIndex.put("02", "2");
        c_monthIndex.put("03", "3");
        c_monthIndex.put("04", "4");
        c_monthIndex.put("05", "5");
        c_monthIndex.put("06", "6");
        c_monthIndex.put("07", "7");
        c_monthIndex.put("08", "8");
        c_monthIndex.put("09", "9");

        /////////////////////////////////////////////
        // Pattern to get date

        //Friday, March 13, 2015 3:34:29 PM
        //Friday, March 13, 2015 3:34
        // Format: April 20, 2014
        c_patterns.add("(?:(?:Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday),\\s*)?(?<pubMon>January|February|March|April|May|June|July|August|September|October|November|December)\\s*(?<pubDate>(?:[12]\\d)|30|31|(?:0?\\d)),\\s*(?<pubYear>2[01]\\d{2})(?:\\s*(?<pubHour>\\d{1,2}):(?<pubMinute>\\d{1,2})(?::\\d{1,2}\\s*(?<pubType>AM|PM))?)?");

        // Format: 13/04/2014
        // Format: 13-04-2014
        c_patterns.add("(?<pubDate>(?:[12][1-9])|30|31|(?:0?[1-9]))[\\/\\-](?<pubMon>(?:0?[1-9])|(?:1[012]))[\\/\\-](?<pubYear>2[01]\\d{2})");

        // Format: 2014-2-22
        // Format: 2014/2/22
        // https://www.schneier.com/blog/archives/2014/04/heartbleed.htmlâ€Ž
        c_patterns.add("(?<pubYear>2[01]\\d{2})[\\/\\-](?<pubMon>(?:1[012])|(?:0?[1-9]))(?:[\\/\\-](?<pubDate>(?:[12][1-9])|30|31|(?:0?[1-9])))T?(?:(?<pubHour>\\d{1,2}):(?<pubMinute>\\d{1,2})(?::\\d{1,2}\\s*(?<pubZone>[\\-\\+]\\d{2})?)?)?");

        // Format: 13/04/14, 13-04-07
        c_patterns.add("(?<pubDate>(?:[12][1-9])|30|31|(?:0?[1-9]))[\\/\\-](?<pubMon>(?:0?[1-9])|(?:1[012]))[\\/\\-](?<pubYear>[0-9]{2})");

        // Format: http://trachnhiemonline.com/Nhan-Dinh/ND-150316-LaTuCuoiCungCuaTongThongNguyenVanThieu.htm
        // Format: 20100725
        // Format: 25072010
        c_patterns.add("(?<pubDate>(?:[12][1-9])|30|31|(?:0?[1-9]))(?<pubMon>(?:1[012])|(?:0?[1-9]))(?<pubYear>(?:2[01]\\d{2})|(?:[0-9]{2}))");
        c_patterns.add("(?<pubYear>(?:2[01]\\d{2})|(?:[0-9]{2}))(?<pubMon>(?:1[012])|(?:0?[1-9]))(?<pubDate>(?:[12][1-9])|30|31|(?:0?[1-9]))");

        // Format: 13 ThÃ¡ng SÃ¡u, 2013
        c_patterns.add("(?<pubDate>(?:[12][1-9])|30|31|(?:0?[1-9]))\\s*th.ng\\s*(?<pubMon>(?:m..i\\s*)?.{2,4}),\\s*(?<pubYear>2[01]\\d{2})");

        // Format: ThÃ¡ng Ba 9, 2012
        c_patterns.add("th.ng\\s*(?<pubMon>(?:m..i\\s*)?.{2,4})\\s*(?<pubDate>(?:[12][1-9])|30|31|(?:0?[1-9])),\\s*(?<pubYear>2[01]\\d{2})");

        // Format: 10 October 2013
        c_patterns.add("(?<pubDate>[12]\\d|30|31|0?[1-9])\\s*(?<pubMon>January|February|March|April|May|June|July|August|September|October|November|December)\\s*(?<pubYear>2[01]\\d{2})");

        // Format: 2012-Oct-15
        c_patterns.add("(?<pubYear>2[01]\\d{2})\\-(?<pubMon>Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec).?\\-(?<pubDate>[12]\\d|30|31|0?[1-9])");

        // Format: Nov 2, 2006
        c_patterns.add("(?<pubMon>Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec).?\\s*(?<pubDate>[12]\\d|30|31|0?[1-9]),\\s*(?<pubYear>2[01]{1}\\d{2})");

        // Format: Dec 12 '12
        c_patterns.add("(?<pubMon>Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec).?\\s*(?<pubDate>[12]\\d|30|31|0?[1-9])\\s*'(?<pubYear>\\d{2})");

        c_listURLPatterns = loadListPatternsFromFile(config.getRegexPath("regex_url.conf"));
        c_listContentPatterns = loadListPatternsFromFile(config.getRegexPath("regex_content.conf"));
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
//    private static class DateParserHolder {
//        //        private static final DateParser INSTANCE = new DateParser();
//        private static final DateParser INSTANCE = new DateParser(Config);
//    }
    public static DateParser getInstance(String url) {
        final DateParser INSTANCE = new DateParser(url);
        return INSTANCE;
    }

    public static DateParser getInstance(Config config) {
        final DateParser INSTANCE = new DateParser(config);
        return INSTANCE;
    }

    private String get_monthIndex(String month) {
        // Get index of a month (Vietnamese, English)
        month = month.trim();
        month = month.toLowerCase();

        if (c_monthIndex.containsKey(month))
            return c_monthIndex.get(month);

        return "1";
    }

    public String dateToString(Date d) {
        // Convert a Date instance to a string representation of
        // 1995-12-31T23:59:59Z
        String ret = "";

        Timestamp _timestamp = new Timestamp(d.getTime());
        SimpleDateFormat _dateF = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'");
        ret = _dateF.format(_timestamp);
        return ret;
    }

    private List<String> loadListPatternsFromFile(String regexFile) {
        List<String> _list_patterns = new ArrayList<String>();

        String _line = null;
        try {
            // open input stream test.txt for reading purpose.
            FileReader fileReader = new FileReader(regexFile);
            BufferedReader br = new BufferedReader(fileReader);
            while ((_line = br.readLine()) != null) {
                if (_line.length() > 0) {
                    _list_patterns.add(_line.trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return _list_patterns;
    }

//    private List<String> loadListPatternsFromUrl(String url, String type, int limit, int offset) {
//        RestAdapter restAdapter = new RestAdapter.Builder()
//                .setEndpoint(url)
//                .build();
//
//
//        RegexParserClient service = restAdapter.create(RegexParserClient.class);
//
//        ListResponse<RegexParser> response = service.getRegexs(type, limit, offset);
////        ListResponse<Regex> response = service.getRegexs(type, limit, offset);
//
//        List<String> regexs = new ArrayList<String>();
//        List<RegexParser> regexList = response.getData();
//        for (int i = 0; i < regexList.size(); i++) {
//            regexs.add(regexList.get(i).getRegex());
//        }
//        return regexs;
//    }

//    private List<String> loadListPatternsFromUrl(String url, String type) {
//        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
//                .setEndpoint(url)
//                .build();
//
//        System.out.println("========>" + url + " : " + type);
//        RegexParserClient service = restAdapter.create(RegexParserClient.class);
//
//        ListResponse<RegexParser> response = service.getRegexs(type);
//
//        List<String> regexs = new ArrayList<String>();
//        List<RegexParser> regexList = response.getData();
//        for (int i = 0; i < regexList.size(); i++) {
//            regexs.add(regexList.get(i).getRegex());
//        }
//        return regexs;
//    }

    public long getDatetimeByStandardRegex(String url, String content) {
        long _ret = -1;      // -1 --> Can not parse datetime
        int _year = 0;
        int _mon = 1;
        int _day = 1;
        int _hour = 0;
        int _minute = 0;

        String _real_date = "";
        String _type = "";
        System.out.println("======================> Null mai duoc chang: " + c_listContentPatterns.size());
        boolean isDetected = false;
        // 1st: parse content to get datetime
        for (String pattern : c_listContentPatterns) {
            Matcher m = Pattern.compile(pattern, Pattern.DOTALL | Pattern.CASE_INSENSITIVE)
                    .matcher(content);
            while (m.find()) {
                try {
                    _year = Integer.parseInt(getGroupValue(m, YEAR_LABLE));
                } catch (NumberFormatException e) {
                    break;
                }
                if (_year < 100) {
                    _year += 2000;
                }
                try {

                    _mon = Integer.parseInt(get_monthIndex(getGroupValue(m, MON_LABLE)));
                } catch (NumberFormatException e) {
//                    e.printStackTrace();
                }
                try {
                    _day = Integer.parseInt(getGroupValue(m, DATE_LABLE));
                } catch (NumberFormatException e) {
//                    e.printStackTrace();
                }
                try {
                    _hour = Integer.parseInt(getGroupValue(m, HOUR_LABLE)) + Integer.parseInt(getGroupValue(m, ZONE_LABLE));
                } catch (NumberFormatException e) {
                }
                try {
                    _minute = Integer.parseInt(getGroupValue(m, MINUTE_LABLE));
                } catch (NumberFormatException e) {
                }

                _type = getGroupValue(m, TYPE_LABLE);
                if (_type.indexOf("pm") > 0 || _type.indexOf("ch") > 0) {
                    _hour += 12;
                }

                _real_date = String.format("%02d-%02d-%04d %02d:%02d", _mon, _day, _year, _hour, _minute);
                if (pattern.indexOf("<abbr") >= 0 || pattern.indexOf("<meta") >= 0) {
                    isDetected = true;
                }
                break;
            }
            if (isDetected) break;
        }


        _ret = convertStringToTimestamp(_real_date);
        // 2nd: If can not find datetime in content, try to get from url
        if (_ret == -1) {
            isDetected = false;
            for (String pattern : c_listURLPatterns) {
                Matcher m = Pattern.compile(pattern, Pattern.DOTALL | Pattern.CASE_INSENSITIVE)
                        .matcher(url);

                while (m.find()) {
                    _year = Integer.parseInt(getGroupValue(m, YEAR_LABLE));
                    if (_year < 100) {
                        _year += 2000;
                    }
                    _mon = Integer.parseInt(get_monthIndex(getGroupValue(m, MON_LABLE)));
                    _day = Integer.parseInt(getGroupValue(m, DATE_LABLE));
                    _hour = Integer.parseInt(getGroupValue(m, HOUR_LABLE)) + Integer.parseInt(getGroupValue(m, ZONE_LABLE));
                    _minute = Integer.parseInt(getGroupValue(m, MINUTE_LABLE));

                    _type = getGroupValue(m, TYPE_LABLE);
                    if (_type == "pm" || _type.indexOf("ch") > 0) {
                        _hour += 12;
                    }
                    _real_date = String.format("%02d-%02d-%04d %02d:%02d", _mon, _day, _year, _hour, _minute);

                }
                if (isDetected) break;
            }

            _ret = convertStringToTimestamp(_real_date);
        }
        return _ret;
    }

    public static long getDateFromContent(String content) {
        // Try to grab the published date from the content
        // Return the date in form of 1995-12-31T23:59:59Z
        // If there's no pattern matched, return blank

        // define a list of applicable patterns
        List<String> patterns = new ArrayList<String>();

        // 0: Format: 13/04/2014, 13/04/14, 13-04-07
        patterns.add("([0-9]{1,2})[/-](0?[0-9]{1,2})[/-]((20|19)[0-9]{2,4})");

        // 1: mashable.com/2014/04/09/heartbleed-bug-websites-affected/‎
        patterns.add("([0-9]{4})/([0-9]{2})/([0-9]{2})");

        // 2: Format: 13 Tháng Sáu, 2013
        patterns.add("([0-9]{1,2}) ([Tt]háng.*), ([0-9]{4})");

        // 3: https://www.schneier.com/blog/archives/2014/04/heartbleed.html‎
        patterns.add("([0-9]{4})/([0-9]{1,2})");

        // 4: Format: Tháng Ba 9, 2012
        patterns.add("([Tt]háng .*)([0-9]{1,2}), ([0-9]{4})");

        // 5: Format: April 20, 2014
        patterns.add("(January|February|March|April|May|June|July|August|September|October|November|December) ([0-9]{1,2}), ([0-9]{4})");

        // 6: Format: 10 October 2013
        patterns.add("([0-9]{1,2}) (January|February|March|April|May|June|July|August|September|October|November|December) ([0-9]{4})");

        // 7: Format: 2012-Oct-15
        patterns.add("([0-9]{4})-(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec).?-([0-9]{2})");

        // 8: Format: Nov 2, 2006
        patterns.add("(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec).? ([0-9]{1,2}), ([0-9]{4})");

        // 9: Format: Dec 12 '12
        patterns.add("(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec).? ([0-9]{1,2}) '([0-9]{2})");

        Boolean matched = true;
        Calendar date = Calendar.getInstance();
        int count = 0;
        String year = "2000", month = "12", day = "1";

        List<Long> results = new ArrayList<Long>();

        // Go through a list of patterns to determine the matched string with the lowest index
        for (String pattern : patterns) {
            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);
            // Now create matcher object.
            Matcher m = r.matcher(content);
            count += 1;
            if (!m.find())
                continue;

            switch (count - 1) {
                case 0:
                    // 0: Format: 13/04/2014 or 12/3/14
                    year = m.group(3);
                    if (year.length() == 2)
                        year = "20" + year; // 20xx
                    month = m.group(2);
                    day = m.group(1);
                    break;

                case 1:
                    // 1: mashable.com/2014/04/09/heartbleed-bug-websites-affected/‎
                    year = m.group(1);
                    month = m.group(2);
                    day = m.group(3);
                    break;

                case 2:
                    // 2: Format: 13 Tháng Sáu, 2013
                    year = m.group(3);
//                    month = DateParser.getMonthIndex(m.group(2));
                    day = m.group(1);
                    break;

                case 3:
                    // 3: https://www.schneier.com/blog/archives/2014/04/heartbleed.html‎
                    year = m.group(1);
                    month = m.group(2);
                    break;

                case 4:
                    // 4: Format: Tháng Ba 9, 2012
                    year = m.group(3);
//                    month = DateParser.getMonthIndex(m.group(1));
                    day = m.group(2);
                    break;

                case 5:
                    // 6: Format: April 20, 2014
                    year = m.group(3);
//                    month = DateParser.getMonthIndex(m.group(1));
                    day = m.group(2);
                    break;

                case 6:
                    // 6: Format: 10 October 2013
                    year = m.group(3);
//                    month = DateParser.getMonthIndex(m.group(2));
                    day = m.group(1);
                    break;

                case 7:
                    // 7: Format: 2012-Oct-15
                    year = m.group(1);
//                    month = DateParser.getMonthIndex(m.group(2));
                    day = m.group(3);
                    break;

                case 8:
                    // 8: Format: Nov 2, 2006
                    year = m.group(3);
//                    month = DateParser.getMonthIndex(m.group(1));
                    day = m.group(2);
                    break;

                case 9:
                    // 9: Format: Dec 12 '12
//                    DateParser.log("In 9");
                    year = m.group(3);
//                    month = DateParser.getMonthIndex(m.group(1));
                    day = m.group(2);
                    break;


            }
            date.set(Integer.parseInt(year), // year
                    Integer.parseInt(month) - 1, // month
                    Integer.parseInt(day)); // date
            if (date.getTimeInMillis() < System.currentTimeMillis() && date.getTimeInMillis() > (System.currentTimeMillis() - TEN_YEAR)) {
                results.add(date.getTimeInMillis());
            }
        }

        if (results.isEmpty())
            // no match
            return 0;
        Long result = results.get(0);
        for (Long r : results) {
            if (result < r)
                result = r;
        }
        return result;
    }

    private static long convertStringToTimestamp(String str_date) {
        long _ret = -1;
        DateFormat _formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        Date _date = null;
        try {
            _date = (Date) _formatter.parse(str_date);
            _ret = _date.getTime();
        } catch (ParseException e) {
            //e.printStackTrace();
        }

        return _ret;
    }

    public static String getGroupValue(Matcher m, String groupLable) {
        String _group_value = "0";

        try {
            _group_value = m.group(groupLable);
        } catch (Exception e) {
            _group_value = "0";
            //e.printStackTrace();
        }
        if (_group_value == null) {
            _group_value = "0";
        }
        return _group_value.toLowerCase();
    }


    public long getDateFromContent(String url, String content) throws MalformedURLException {
        // Try to grab the published date from the content
        // Return the date in form of 1995-12-31T23:59:59Z
        // If there's no pattern matched, return blank

        long _ret = -1;      // -1 --> Can not parse datetime
        int _year = 0;
        int _mon = 0;
        int _day = 0;
        int _hour = 0;
        int _minute = 0;

        String _real_date = "";
        String _type = "";

        // 1st: parse content to get datetime
        for (String pattern : c_patterns) {
            Matcher m = Pattern.compile(pattern, Pattern.DOTALL | Pattern.CASE_INSENSITIVE)
                    .matcher(content);
            //System.out.println("CONTENT: " + pattern);
            while (m.find()) {
                _year = Integer.parseInt(getGroupValue(m, YEAR_LABLE));
                if (_year < 100) {
                    _year += 2000;
                }
                _mon = Integer.parseInt(get_monthIndex(getGroupValue(m, MON_LABLE)));
                _day = Integer.parseInt(getGroupValue(m, DATE_LABLE));
                _hour = Integer.parseInt(getGroupValue(m, HOUR_LABLE)) + Integer.parseInt(getGroupValue(m, ZONE_LABLE));
                _minute = Integer.parseInt(getGroupValue(m, MINUTE_LABLE));

                _type = getGroupValue(m, TYPE_LABLE);
                if (_type.indexOf("pm") > 0 || _type.indexOf("ch") > 0) {
                    _hour += 12;
                }

                _real_date = String.format("%02d-%02d-%04d %02d:%02d", _mon, _day, _year, _hour, _minute);
//                CLog.getInstance().writeLog("\nDATETIME DETECTED: " + String.valueOf(_real_date) + " >> " + url + " >> " + pattern);

            }
        }

        _ret = convertStringToTimestamp(_real_date);
        System.out.println("----> Parsed date is : " + _ret + "and leng of c_patterns");
        return _ret;
    }

    private static String getCurrentDate(String url) {
        // Get the current date
        return (DateParser.getInstance(url).dateToString(new Date(0)));
    }

    public String getWEB_SERVICE_URL() {
        return WEB_SERVICE_URL;
    }

    public void setWEB_SERVICE_URL(String WEB_SERVICE_URL) {
        this.WEB_SERVICE_URL = WEB_SERVICE_URL;
    }
}
