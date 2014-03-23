package org.tokiru;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tokiru on 3/23/14.
 */
public class Article implements Serializable{
    private Document doc = new Document("aoeu");
    private int id;

    public Article(int id) {
        try {
            doc = Jsoup.connect(getArticleURL(id)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.id = id;
    }


    private static String getArticleURL(int id) {
        StringBuilder sb = new StringBuilder("http://habrahabr.ru/post/");
        sb.append(id);
        sb.append('/');
        return sb.toString();
    }

    public int extractPositiveVotesCount() {
        return extractVotesCount(1);
    }

    public int extractNegativeVotesCount() {
        return extractVotesCount(2);
    }

    private int extractVotesCount(int id) {
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

    public String extractAuthor() {
        Elements e = doc.select("div.author");
        String[] a = e.text().split(" ");
        return a[0];
    }

    public int extractViewCount() {
        Elements e = doc.select("div.pageviews");
        Integer count = null;
        try {
            count = new Integer(e.text());
        } catch (NumberFormatException ex) {
            return 0;
        }

        return count.intValue();
    }

    public int extractFavouriteCount() {
        Elements e = doc.select("div.favs_count");

        Integer count = null;
        try {
            count = new Integer(e.text());
        } catch (NumberFormatException ex) {
            return 0;
        }

        return count.intValue();
    }

    public int extractCommentsCount() {
        Elements e = doc.select("span[id=comments_count]");

        Integer count = null;
        try {
            count = new Integer(e.text());
        } catch (NumberFormatException ex) {
            return 0;
        }

        return count.intValue();
    }

    public String extractTimeAndDate() {
        Elements e = doc.select("div.published");
        return e.text();
    }

    public String extractText() {
        Elements e = doc.select("div.content.html_format");
        return e.text();
    }

    public boolean articleInDraft() {
        return  doc.title().equals("Хабрахабр — Доступ к странице ограничен");
    }

    public void save() {
        StringBuilder fileName = new StringBuilder("articles/");
        fileName.append(id);

        File file = new File(String.valueOf(fileName));
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ObjectOutputStream oos = null;
        try {
             oos = new ObjectOutputStream(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
