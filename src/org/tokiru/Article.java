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
    private boolean loadFromFile;
    private boolean fail;
    private int id;

    int positiveVotesCount = -1;
    int negativeVotesCount = -1;
    String author = "VASA";
    int viewCount = -1;
    int favouriteCount = -1;
    int commentCount = -1;
    String timeAndDate = "VASA";
    String title = "VASATITLE";
    String text = "VASAETOBARADA";

    public Article(int id) throws IOException {
        this.id = id;
        fail = false;
        loadFromFile = lookForFile();

        if (loadFromFile) {
            BufferedReader reader = new BufferedReader(new FileReader(new File(getFileName())));
            reader.readLine();
            author = reader.readLine();
            positiveVotesCount = new Integer(reader.readLine());
            negativeVotesCount = new Integer(reader.readLine());
            viewCount = new Integer(reader.readLine());
            favouriteCount = new Integer(reader.readLine());
            commentCount = new Integer(reader.readLine());
            timeAndDate = reader.readLine();
            title = reader.readLine();
            text = reader.readLine();
        } else {
            try {
                doc = Jsoup.connect(getArticleURL(id)).get();
            } catch (Exception e) {
                fail = true;
            }
            positiveVotesCount = extractPositiveVotesCount();
            negativeVotesCount = extractNegativeVotesCount();
            author = extractAuthor();
            viewCount = extractViewCount();
            favouriteCount = extractFavouriteCount();
            commentCount = extractCommentsCount();
            timeAndDate = extractTimeAndDate();
            title = extractTitle();
            text = extractText();
        }
    }

    public int getPositiveVotesCount() {
        return positiveVotesCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getNegativeVotesCount() {
        return negativeVotesCount;
    }

    public String getText() {
        return text;
    }

    public String getTimeAndDate() {
        return timeAndDate;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public void save() throws IOException {
        if (fail || loadFromFile) return ;

        File file = new File(getFileName());
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(Integer.toString(id));
        writer.write("\n");
        writer.write(extractAuthor());
        writer.write("\n");
        writer.write(Integer.toString(positiveVotesCount));
        writer.write("\n");
        writer.write(Integer.toString(negativeVotesCount));
        writer.write("\n");
        writer.write(Integer.toString(viewCount));
        writer.write("\n");
        writer.write(Integer.toString(favouriteCount));
        writer.write("\n");
        writer.write(Integer.toString(commentCount));
        writer.write("\n");
        writer.write(timeAndDate);
        writer.write("\n");
        writer.write(title);
        writer.write("\n");
        writer.write(text);
        writer.write("\n");
        writer.flush();
        writer.close();
    }

    private boolean lookForFile() {
        File file = new File(getFileName());
        return file.exists();
    }

    private String getFileName() {
        return "articles/" + id;
    }

    private static String getArticleURL(int id) {
        return "http://habrahabr.ru/post/" + id + "/";
    }

    private int extractPositiveVotesCount() {
        return extractVotesCount(1);
    }

    private int extractNegativeVotesCount() {
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

        List<Integer> numbers = new ArrayList<>();

        while (m.find()) {
            numbers.add(new Integer(m.group()));
        }

        return numbers.get(id);
    }

    private String extractAuthor() {
        Elements e = doc.select("div.author");
        String[] a = e.text().split(" ");
        return a[0];
    }

    private String extractTitle() {
        Elements e = doc.select("span.post_title");
        return e.text();
    }

    private int extractViewCount() {
        Elements e = doc.select("div.pageviews");
        Integer count;
        try {
            count = new Integer(e.text());
        } catch (NumberFormatException ex) {
            return 0;
        }

        return count;
    }

    private int extractFavouriteCount() {
        Elements e = doc.select("div.favs_count");

        Integer count;
        try {
            count = new Integer(e.text());
        } catch (NumberFormatException ex) {
            return 0;
        }

        return count;
    }

    private int extractCommentsCount() {
        Elements e = doc.select("span[id=comments_count]");

        Integer count;
        try {
            count = new Integer(e.text());
        } catch (NumberFormatException ex) {
            return 0;
        }

        return count;
    }

    private String extractTimeAndDate() {
        Elements e = doc.select("div.published");
        return e.text();
    }

    private String extractText() {
        Elements e = doc.select("div.content.html_format");
        return e.text();
    }

    public boolean articleInDraft() {
        if (fail) {
            return true;
        }

        return  doc.title().equals("Хабрахабр — Доступ к странице ограничен");
    }
}