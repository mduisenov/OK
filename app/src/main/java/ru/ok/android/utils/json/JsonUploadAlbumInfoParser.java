package ru.ok.android.utils.json;

import org.json.JSONObject;
import ru.ok.android.utils.Logger;
import ru.ok.model.photo.PhotoAlbumInfo;

public class JsonUploadAlbumInfoParser {
    public static PhotoAlbumInfo parse(String jsonReponse) {
        PhotoAlbumInfo result = null;
        String aid = null;
        String gid = null;
        String name = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonReponse);
            if (jsonObject.has("aid")) {
                aid = jsonObject.getString("aid");
            }
            if (jsonObject.has("gid")) {
                gid = jsonObject.getString("gid");
            }
            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }
            PhotoAlbumInfo result2 = new PhotoAlbumInfo();
            try {
                result2.setId(aid);
                result2.setTitle(name);
                result2.setGroupId(gid);
                return result2;
            } catch (Exception e) {
                result = result2;
                Logger.m176e("Failed to parse upload album info json: " + jsonReponse);
                return result;
            }
        } catch (Exception e2) {
            Logger.m176e("Failed to parse upload album info json: " + jsonReponse);
            return result;
        }
    }
}
