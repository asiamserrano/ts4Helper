package org.example;

import java.net.URI;
import java.net.URL;

public class Main {

    public static void main(String[] args) throws Exception {
        String string = "https://www.patreon.com/posts/sxv-year-of-11-75418958";
        URI uri = new URI(string);
        URL url = uri.toURL();
//        String urlString = (String) url;
//        System.out.println(url.getPath());
//        System.out.println(url.getHost());
        String s = String.format("%s%s", url.getHost(), url.getPath());
        System.out.println(s);
        System.out.println(string.split("//")[1]);
    }
}
