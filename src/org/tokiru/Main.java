package org.tokiru;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Article a = new Article(4);
        a.save();
    }
}
