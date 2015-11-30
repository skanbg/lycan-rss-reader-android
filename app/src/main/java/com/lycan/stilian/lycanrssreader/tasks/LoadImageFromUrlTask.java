package com.lycan.stilian.lycanrssreader.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.lycan.stilian.lycanrssreader.tasks.constants.ACTION_TYPE;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.AbstractUpdaterTask;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.IUpdateable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

//http://square.github.io/picasso/
public class LoadImageFromUrlTask extends AbstractUpdaterTask<String, Void, Bitmap> {
    private static HashMap<String, Bitmap> bitmapCache = new HashMap<>();

    public LoadImageFromUrlTask(IUpdateable mContext) {
        super(Bitmap.class ,mContext, ACTION_TYPE.LOAD_IMAGE);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imageUrl = params[0];
        Bitmap imageContent = getImageContent(imageUrl);
        return imageContent;
    }

//    @Override
//    protected Bitmap doInBackground(ImageView... params) {
//        this.mImageView = params[0];
//        String imageUrl = (String)this.mImageView.getTag();
//        Bitmap imageContent = getImageContent(imageUrl);
//        return imageContent;
//    }

    private Bitmap getImageContent(String imageUrl) {
        if(bitmapCache.containsKey(imageUrl)){
            return bitmapCache.get(imageUrl);
        }

        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(imageUrl);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bitmapCache.put(imageUrl, bitmap);
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            Log.w("ImageDownloader", "Error downloading image from " + imageUrl);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
//        Bitmap imageContent = null;
//
//        try {
//            InputStream in = new URL(imageUrl).openStream();
//            imageContent = BitmapFactory.decodeStream(in);
//        } catch (Exception e) {
//            Log.e("Error", e.getMessage());
//            e.printStackTrace();
//        }
//
//        return imageContent;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        if (isCancelled()) {
            result = null;
        }

        this.mContext.updateData(result, ACTION_TYPE.LOAD_IMAGE);
    }
}