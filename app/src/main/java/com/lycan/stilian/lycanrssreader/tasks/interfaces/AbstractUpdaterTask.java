package com.lycan.stilian.lycanrssreader.tasks.interfaces;

import android.os.AsyncTask;

import com.lycan.stilian.lycanrssreader.tasks.constants.ACTION_TYPE;

public abstract class AbstractUpdaterTask<X, Y, T> extends AsyncTask<X, Y, T> {
    protected IUpdateable mContext;
    protected Class<T> modelType;
//    protected T modelInstance;
    protected IDataTransformer dataTransformer;
    protected ACTION_TYPE actionType;

    public AbstractUpdaterTask(Class<?> classMap, IUpdateable mContext, ACTION_TYPE actionType) {
        this.modelType = (Class) classMap;
        this.mContext = mContext;
        this.actionType = actionType;
    }

    public AbstractUpdaterTask(Class<?> classMap, IUpdateable mContext, ACTION_TYPE actionType, IDataTransformer dataTransformer) {
        this(classMap, mContext, actionType);
        this.dataTransformer = dataTransformer;
    }

    public AbstractUpdaterTask(T instance, IUpdateable mContext, ACTION_TYPE actionType, IDataTransformer dataTransformer) {
        this((Class) instance.getClass(), mContext, actionType, dataTransformer);
    }

    public AbstractUpdaterTask(T instance, IUpdateable mContext, ACTION_TYPE actionType) {
        this(instance, mContext, actionType, null);
//        this.modelInstance = instance;
    }

    @Override
    protected void onPostExecute(T result) {
        super.onPostExecute(result);

        if (isCancelled()) {
            result = null;
        }

        T castResult = result;
        this.mContext.updateData(castResult, this.actionType);
    }
}
