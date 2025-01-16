package ts4.helper.TS4Downloader.models;

import lombok.AllArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ts4.helper.TS4Downloader.enums.WebsiteEnum;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;

import static ts4.helper.TS4Downloader.enums.WebsiteEnum.PATREON_POSTS;
import static ts4.helper.TS4Downloader.enums.WebsiteEnum.SIMS_FINDS_DOWNLOADS;
import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE_MEMBERS;
import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE_CREATORS;
import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE_CAS;

@AllArgsConstructor
public class NestedURL {

    public final URL url;
    public final NestedURL previous;
    public final List<NestedURL> next;

    public NestedURL(URL url) {
        this.url = url;
        this.previous = null;
        this.next = new ArrayList<>();
    }

    private NestedURL(URL url, NestedURL previous) {
        this.url = url;
        this.previous = previous;
        this.next = new ArrayList<>();
    }

//    @SuppressWarnings("unchecked")
//    public JSONObject toJSON() {
////        JSONObject jsonObject = new JSONObject();
////        jsonObject.put("url", url.toString());
////        if (!next.isEmpty()) {
////            JSONArray jsonArray = new JSONArray();
////            jsonArray.addAll(next.stream().map(NestedURL::toJSON).toList());
////            jsonObject.put("next", jsonArray);
////        }
////        return jsonObject;
//
//        if (previous == null) {
//
//        } else if (next.isEmpty()) {
//
//        } else {
//
//        }
//
//    }

    public void add(List<URL> urls) {
        urls.forEach(this::add);
    }

    public void add(URL url) {
        this.next.add(new NestedURL(url, this));
    }

}
