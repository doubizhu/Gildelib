package com.sdyl.easyblelib.glide.api;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.io.File;

/**
 * created by lxx
 * on 2020/4/20
 */
public class Glide {
    RequestManagerRetriver retriver;

    public Glide(RequestManagerRetriver retriver){
        this.retriver = retriver;
    }

    public static RequestManager with(FragmentActivity fragmentActivity){
        return getRetriver(fragmentActivity).get(fragmentActivity);
    }

    public static RequestManager with(Activity activity){
        return getRetriver(activity).get(activity);
    }

    public static RequestManager with(Context context){
        return getRetriver(context).get(context);
    }

    public RequestManagerRetriver getRetriver() {
        return retriver;
    }

    private static RequestManagerRetriver getRetriver(Context context) {
        return Glide.get(context).getRetriver();
    }

    private static Glide get(Context context) {
        return new GlideBuilder(context).build();
    }

    public static class GlideBuilder {
        public GlideBuilder(Context context){

        }

        public Glide build(){
            Glide glide = new Glide(new RequestManagerRetriver());
            return glide;
        }
    }


}
