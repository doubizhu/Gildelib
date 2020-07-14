package com.sdyl.easyblelib.glide.resourse;

import android.graphics.Bitmap;
import android.util.Log;

import com.sdyl.easyblelib.glide.Tool;

/**
 * created by lxx
 * on 2020/4/20
 */
public class Value {
    private final static String TAG = Value.class.getSimpleName();

    private static Value value;

    public static Value getInstance(){
        if (null == value){
            synchronized (Value.class){
                if (null == value){
                    value = new Value();
                }
            }
        }
        return value;
    }

    private Bitmap mBitmap;
    private int count;
    private ValueCallback callback;
    private String key;

    public static Value getValue() {
        return value;
    }

    public static void setValue(Value value) {
        Value.value = value;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ValueCallback getCallback() {
        return callback;
    }

    public void setCallback(ValueCallback callback) {
        this.callback = callback;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 使用计数
     */
    public void useAction(){
        Tool.checkNotEmpty(mBitmap);
        if (mBitmap.isRecycled()){
            Log.d(TAG, "useAction: 已经被回收了");
            return;
        }
        Log.d(TAG, "useAction: 加一 count:" + count);
        count++;
    }

    public void nonUseAction(){
        count--;
        if (count <= 0 && callback != null){
            callback.valueNonUseListener(key,value);
        }
        Log.d(TAG, "nonUseAction: 减一 count:" + count);
    }

    public void recycleBitmap(){
        if (count > 0){
            Log.d(TAG, "recycleBitmap: 引用计数大于0，正在使用中...，不能释放");
            return;
        }

        if (count > 0){
            Log.d(TAG, "recycleBitmap: 引用计数大于0，正在使用中...，不能释放");
            return;
        }

        if (mBitmap.isRecycled()){
            Log.d(TAG, "recycleBitmap: 都已经被回收了，不能释放");
            return;
        }

        mBitmap.recycle();
        value = null;
        System.gc();
    }
}
