package com.sdyl.easyblelib.glide.api;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.sdyl.easyblelib.glide.fragment.ActivityFragmentManager;
import com.sdyl.easyblelib.glide.fragment.FragmentActivityFragmentManager;

/**
 * created by lxx
 * on 2020/4/20
 */
public class RequestManager {

    private final String TAG = RequestManager.class.getSimpleName();

    private final String FRAGMENT_ACTIVITY_NAME = "Fragment_Activity_NAME";
    private final String ACTIVTTY_NAME = "Activity_NAME";

    private Context requestManagerContext;

    private static RequestTargetEngine callback;

    private final int NEXT_HANDLER_MSG = 995465; // Handler 标记



    FragmentActivity fragmentActivity;

    public RequestManager(FragmentActivity fragmentActivity){
        this.fragmentActivity = fragmentActivity;
        requestManagerContext = fragmentActivity;
        if (callback == null){
            callback = new RequestTargetEngine(requestManagerContext);
        }
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();

        Fragment fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if (null == fragment){
            fragment = new FragmentActivityFragmentManager(callback);
            supportFragmentManager.beginTransaction().add(fragment,FRAGMENT_ACTIVITY_NAME);
        }

        Fragment fragment1 = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        Log.d(TAG, "RequestManager: fragment1" + fragment1);
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);
    }

    public RequestManager(Activity activity){
        this.requestManagerContext = activity;
        if (callback == null){
            callback = new RequestTargetEngine(requestManagerContext);
        }
        android.app.FragmentManager fragmentManager = activity.getFragmentManager();

        android.app.Fragment fragment  = fragmentManager.findFragmentByTag(ACTIVTTY_NAME);
        if (null == fragment){
            fragment = new ActivityFragmentManager(callback);

            fragmentManager.beginTransaction().add(fragment,ACTIVTTY_NAME).commitAllowingStateLoss();
        }
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);
    }

    public RequestManager(Context context){
        this.requestManagerContext = context;
        if (callback == null){
            callback = new RequestTargetEngine(requestManagerContext);
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Fragment fragment2 = fragmentActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
            Log.d(TAG, "Handler: fragment2" + fragment2);
            return false;
        }
    });

    public RequestTargetEngine load(String path){
        mHandler.removeMessages(NEXT_HANDLER_MSG);

        callback.loadValueInitAction(path,requestManagerContext);
        return callback;
    }
}
