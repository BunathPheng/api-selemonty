package org.hrd.finalprojectmuseum.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class LandscapeProcessor {
    public static List<String> getImageUrls(JSONObject landscapeLink) {
        List<String> imageUrls = new ArrayList<>();
        try {
            if (landscapeLink != null && landscapeLink.containsKey("images")) {
                JSONArray imagesArray = landscapeLink.getJSONArray("images");

                for (int i = 0; i < imagesArray.size(); i++) {
                    String imageUrl = imagesArray.getString(i);
                    imageUrls.add(imageUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageUrls;
    }

    public static String extractFilename(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        String baseUrl = "http://34.143.146.124:9125/api/v1/file/view/";
        if (url.startsWith(baseUrl)) {
            return url.substring(baseUrl.length());
        }

        // Fallback to last slash method if base URL doesn't match
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex != -1 && lastSlashIndex < url.length() - 1) {
            return url.substring(lastSlashIndex + 1);
        }
        return url;
    }
}
