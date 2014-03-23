package org.tokiru;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Indexer {
    public static void main(String[] args) {
        Directory dir = null;
        try {
            dir = FSDirectory.open(new File("index"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);

        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setRAMBufferSizeMB(1024);

        IndexWriter writer = null;
        try {
            writer = new IndexWriter(dir, config);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 1000; i < 10000; i++) {
            Article article = new Article(i);
            //article.save();
            if (!article.articleInDraft()) {
                List<Field> fields = new ArrayList<>();
                fields.add(new IntField("id", i, Field.Store.YES));
                fields.add(new IntField("positiveVote", article.extractPositiveVotesCount(), Field.Store.YES));
                fields.add(new IntField("negativeVote", article.extractNegativeVotesCount(), Field.Store.YES));
                fields.add(new IntField("viewCount", article.extractViewCount(), Field.Store.YES));
                fields.add(new IntField("favouriteCount", article.extractFavouriteCount(), Field.Store.YES));
                fields.add(new IntField("commentCount", article.extractCommentsCount(), Field.Store.YES));
                fields.add(new StringField("author", article.extractAuthor(), Field.Store.YES));
                fields.add(new StringField("date", article.extractTimeAndDate(), Field.Store.YES));
                fields.add(new TextField("content", article.extractText(), Field.Store.YES));

                Document doc = new Document();
                for (Field field : fields) {
                    doc.add(field);
                }

                System.out.println("adding document id = " + i);
                try {
                    writer.addDocument(doc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}