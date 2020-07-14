package com.sdyl.easyblelib.glide.load;

import android.content.Context;
import android.graphics.Bitmap;

import com.sdyl.easyblelib.glide.resourse.Value;


/**
 * 加载外部资源 标准制定
 */
public interface ILoadData {

    // 加载外部资源的行为
    Value loadResource(String path, ResponseListener responseListener, Context context, Bitmap reusable);

}
