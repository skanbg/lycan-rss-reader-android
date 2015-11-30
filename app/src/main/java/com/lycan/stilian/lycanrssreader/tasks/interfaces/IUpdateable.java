package com.lycan.stilian.lycanrssreader.tasks.interfaces;

import com.lycan.stilian.lycanrssreader.tasks.constants.ACTION_TYPE;

public interface IUpdateable {
    void updateData(Object data, ACTION_TYPE actionType);
}
