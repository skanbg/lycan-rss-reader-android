package com.lycan.stilian.lycanrssreader.models;

import com.lycan.stilian.lycanrssreader.models.base.Rss;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayOutputStream;

public class FeedDataModel extends Rss {
    public String DbId;
    public String link;

    public static FeedDataModel mapFromXml(String source) {
        FeedDataModel binder = null;

        try {
            Serializer serializer = new Persister();
            binder = serializer.read(FeedDataModel.class, source);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return binder;
    }

    public String toString() {
        String binder = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Serializer serializer = new Persister();
            serializer.write(this, baos);
            binder = new String(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return binder;
    }
}
