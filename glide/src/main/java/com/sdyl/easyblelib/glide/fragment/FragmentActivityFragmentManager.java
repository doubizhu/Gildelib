package com.sdyl.easyblelib.glide.fragment;

import androidx.fragment.app.Fragment;

/**
 * created by lxx
 * on 2020/4/20
 */
public class FragmentActivityFragmentManager extends Fragment {
    public FragmentActivityFragmentManager(){}

    private LifecycleCallback callback;

    public FragmentActivityFragmentManager(LifecycleCallback callback){
        this.callback = callback;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (callback != null){
            callback.glideInitAction();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (callback != null){
            callback.glideStopAction();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callback != null){
            callback.glideRecycleAction();
        }
    }
}
