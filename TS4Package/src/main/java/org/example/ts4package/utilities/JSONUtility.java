//package org.example.ts4package.utilities;
//
//import org.example.ts4package.constants.StringConstants;
//import org.example.ts4package.models.JSONModel;
//import org.example.ts4package.models.WebsiteModel;
//import org.json.simple.JSONObject;
//
//public abstract class JSONUtility {
//
//    public static Object get(JSONObject jsonObject, WebsiteModel.Keys keys) {
//        return jsonObject.get(keys.toString());
//    }
//
//    @SuppressWarnings(StringConstants.UNCHECKED)
//    public static JSONObject put(JSONObject jsonObject, WebsiteModel.Keys keys, String value) {
//        jsonObject.put(keys.toString(), value);
//        return jsonObject;
//    }
//
//    @SuppressWarnings(StringConstants.UNCHECKED)
//    public static JSONObject put(JSONObject jsonObject, WebsiteModel.Keys keys, JSONModel value) {
//        jsonObject.put(keys.toString(), value);
//        return jsonObject;
//    }
//
//    @SuppressWarnings(StringConstants.UNCHECKED)
//    private static JSONObject put(JSONObject jsonObject, WebsiteModel.Keys keys, Object value) {
//        jsonObject.put(keys.toString(), value);
//        return jsonObject;
//    }
//
//}
