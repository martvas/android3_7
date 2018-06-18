package ru.geekbrains.android3_7.model.entity.realm;

import io.realm.RealmObject;

/**
 * Created by spellbit on 29-Nov-16.
 */

public class CachedImage extends RealmObject {
    String url;
    String path;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
