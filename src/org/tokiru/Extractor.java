package org.tokiru;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tokiru on 3/23/14.
 */
public class Extractor {
    public static void main(String[] args) {
        /*for(int i = 1; i < 100; i++) {
            Document doc = downloadPage(getArticleURL(i));
            System.out.println(extractAuthor(doc));
            System.out.println(extractViewCount(doc));
            System.out.println(extractFavouriteCount(doc));
            System.out.println(extractCommentsCount(doc));
            System.out.println(extractTimeAndDate(doc));
        }*/

        Document doc = downloadPage(getArticleURL(200000));
        System.out.println(extractText(doc));
    }

    private static String getArticleURL(int id) {
        StringBuilder sb = new StringBuilder("http://habrahabr.ru/post/");
        sb.append(id);
        sb.append('/');
        return sb.toString();
    }

    //*[@id="infopanel_post_1"]/div[1]/div/span

    private static Document downloadPage(String URL) {
        Document result = null;
        try {
            result = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static int extractPositiveVotesCount(Document doc) {
        return extractVotesCount(doc, 1);
    }

    private static int extractNegativeVotesCount(Document doc) {
        return extractVotesCount(doc, 2);
    }

    private static int extractVotesCount(Document doc, int id) {
        Elements e = doc.select("span.score");
        if (e.size() == 0) {
            return 0;
        }
        Element ee = e.get(0);
        String data = ee.attributes().get("title");
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(data);

        List<Integer> numbers = new ArrayList<Integer>();

        while (m.find()) {
            numbers.add(new Integer(m.group()));
        }

        return numbers.get(id).intValue();
    }

    private static String extractAuthor(Document doc) {
        Elements e = doc.select("div.author");
        String[] a = e.text().split(" ");
        return a[0];
    }

    private static int extractViewCount(Document doc) {
        Elements e = doc.select("div.pageviews");
        Integer count = null;
        try {
            count = new Integer(e.text());
        } catch (NumberFormatException ex) {
            return 0;
        }

        return count.intValue();
    }

    private static int extractFavouriteCount(Document doc) {
        Elements e = doc.select("div.favs_count");

        Integer count = null;
        try {
            count = new Integer(e.text());
        } catch (NumberFormatException ex) {
            return 0;
        }

        return count.intValue();
    }

    private static int extractCommentsCount(Document doc) {
        Elements e = doc.select("span[id=comments_count]");

        Integer count = null;
        try {
            count = new Integer(e.text());
        } catch (NumberFormatException ex) {
            return 0;
        }

        return count.intValue();
    }

    private static String extractTimeAndDate(Document doc) {
        Elements e = doc.select("div.published");
        return e.text();
    }

    private static String extractText(Document doc) {
        Elements e = doc.select("div.content.html_format");
        return e.text();
    }
}
