package com.sdyl.easyblelib.glide.api;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

/**
 * created by lxx
 * on 2020/4/20
 */
public class RequestManagerRetriver {
    public RequestManager get(FragmentActivity fragmentActivity){
        return new RequestManager(fragmentActivity);
    }

    public RequestManager get(Activity activity){
        return new RequestManager(activity);
    }

    public RequestManager get(Context context){
        return new RequestManager(context);
    }
}
