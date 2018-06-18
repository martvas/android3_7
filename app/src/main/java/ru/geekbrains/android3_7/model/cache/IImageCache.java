package ru.geekbrains.android3_7.model.cache;

import android.graphics.Bitmap;

import java.io.File;

public interface IImageCache {
    File getFile(String url);

    boolean contains(String url);

    void clear();

    File saveImage(final String url, Bitmap bitmap);

    File getImageDir();

    String MD5(String s);

    float getSizeKb();

    void deleteFileOrDirRecursive(File fileOrDirectory);

    long getFileOrDirSize(File f);
}
