package org.projects.ts4.utility.utilities;

import java.util.*;

import static org.projects.ts4.utility.constants.StringConstants.EMPTY;
import static org.projects.ts4.utility.constants.StringConstants.GREATER_THAN;
import static org.projects.ts4.utility.constants.StringConstants.LESS_THAN;
import static org.projects.ts4.utility.constants.StringConstants.SINGLE_QUOTE;

public class BookmarksUtility {

        public static void main(String[] args) {

        List<String> bookmarks = StringUtility.loadResourceList("bookmarks_2_5_25.html");

        BookmarksUtility.Folder folder = null;
        BookmarksUtility.Link link;

        BookmarksUtility bookmarksUtility = new BookmarksUtility();

        for (String bookmark : bookmarks) {

            if (bookmark.contains(BookmarksUtility.FOLDER_TOKEN)) {
                folder = BookmarksUtility.Folder.build(bookmark);
            }

            if (bookmark.contains(BookmarksUtility.LINK_TOKEN)) {
                link = BookmarksUtility.Link.build(folder, bookmark);
                if (link != null) {
                    bookmarksUtility.add(link);
                }
            }
        }

        bookmarksUtility.print();
    }

    public static final String FOLDER_TOKEN = "<DT><H3 ADD_DATE";
    public static final String LINK_TOKEN = "<DT><A HREF=\"";

    public static class Link {
        
        public static Link build(Folder folder, String line) {
            String name = StringUtility.getStringBetweenRegex(line, LINK_TOKEN, SINGLE_QUOTE);
            if (name == null || name.isEmpty()) {
                return null;
            } else {
                return new Link(folder, name);
            }
        }

        public final Folder folder;
        public final String link;

        public Link(Folder folder, String link) {
            this.folder = folder;
            this.link = link;
        }

        @Override
        public String toString() {
            String folderName = folder == null ? EMPTY : folder.toString();
            return String.format("%-20s%s", folderName, this.link);
        }
    }
    
    public static class Folder {
        
        public static Folder build(String line) {
            String name = StringUtility.getStringBetweenRegex(line, FOLDER_TOKEN, LESS_THAN);
            if (name == null || name.isEmpty()) {
                return null;
            } else {
                return new Folder(name.split(GREATER_THAN)[1]);
            }
        }

        public final String name;

        public Folder(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private static class BookmarksSet {

        private final Set<String> set;

        public BookmarksSet() {
            this.set = new HashSet<>();
        }

        public BookmarksSet add(String bookmark) {
            this.set.add(bookmark);
            return this;
        }

        public void print() {
            for (String line: this.set.stream().sorted().toList()) {
                System.out.println(line);
            }
        }

        public boolean isEmpty() {
            return this.set.isEmpty();
        }

    }

    private final Map<String, BookmarksSet> map;

    public BookmarksUtility() {
        this.map = new HashMap<>();
    }

    public void add(Link link) {
        String key = link.folder.name;
        BookmarksSet set = map.getOrDefault(key, new BookmarksSet());
        map.put(key, set.add(link.link));
    }

    public void print() {
        for (String key: map.keySet()) {
            System.out.println(key);
            BookmarksSet set = map.get(key);
            if (set != null && !set.isEmpty()) {
                set.print();
            }
        }
    }

}
