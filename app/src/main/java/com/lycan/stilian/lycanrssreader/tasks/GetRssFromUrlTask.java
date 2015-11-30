package com.lycan.stilian.lycanrssreader.tasks;

import com.lycan.stilian.lycanrssreader.tasks.constants.ACTION_TYPE;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.AbstractUpdaterTask;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.IDataTransformer;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.IUpdateable;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetRssFromUrlTask<T extends Object> extends AbstractUpdaterTask<Object, Void, T> {
    public GetRssFromUrlTask(Class<?> classMap, IUpdateable mContext, ACTION_TYPE actionType) {
        super(classMap, mContext, actionType);
    }

    public GetRssFromUrlTask(Class<?> classMap, IUpdateable mContext, ACTION_TYPE actionType, IDataTransformer dataTransformer) {
        super(classMap, mContext, actionType, dataTransformer);
    }

    public GetRssFromUrlTask(T modelInstance, IUpdateable mContext, ACTION_TYPE actionType) {
        super(modelInstance, mContext, actionType);
    }

    public GetRssFromUrlTask(T modelInstance, IUpdateable mContext, ACTION_TYPE actionType, IDataTransformer dataTransformer) {
        super(modelInstance, mContext, actionType, dataTransformer);
    }

    @Override
    protected T doInBackground(Object... params) {
        String rssUrl = (String)params[0];
        T rssContent = this.getRssContent(rssUrl);
        return rssContent;
    }

    private T getRssContent(String rssUrl) {
        InputStream is = null;

        try {
            URL url = new URL(rssUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10 * 1000);
            connection.setConnectTimeout(10 * 1000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();
            if (response > 400) {
                is = null;
            } else {
                is = connection.getInputStream();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Object binder = null;

        try
        {
            Serializer serializer = new Persister();
//            Class<T> castedModelType = (Class<T>)this.modelType;
            binder = serializer.read(this.modelType, is);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(this.dataTransformer != null){
            this.dataTransformer.afterTransform(binder);
        }

        return (T)binder;
    }
}
