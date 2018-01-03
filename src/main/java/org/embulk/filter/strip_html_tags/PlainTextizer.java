package org.embulk.filter.strip_html_tags;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PlainTextizer {
    public String execute(String str) {
        if (str == null) {
            return null;
        }

        Document doc = Jsoup.parseBodyFragment(str);
        return doc.body().text();
    }
}
