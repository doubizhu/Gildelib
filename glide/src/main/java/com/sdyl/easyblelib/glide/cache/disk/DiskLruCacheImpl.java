package com.sdyl.easyblelib.glide.cache.disk;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.sdyl.easyblelib.glide.Tool;
import com.sdyl.easyblelib.glide.resourse.Value;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * created by lxx
 * on 2020/4/20
 */
public class DiskLruCacheImpl {
    private final String TAG = DiskLruCacheImpl.class.getSimpleName();
    private  String DISKLRU_DIR = "disk_lru_dir";

    private final int APP_VERSION = 1;
    private final int VALUE_COUNT = 1;
    private final long MAX_SIZE = 1024 * 1024 * 100;

    private DiskLruCache diskLruCache;

    public DiskLruCacheImpl(Context context){
        try {
            diskLruCache = DiskLruCache.open(getDiskCacheDir(context,DISKLRU_DIR),APP_VERSION,VALUE_COUNT,MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {

            cachePath = context.getExternalCacheDir().getPath();

        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    public void put(String key, Value value){
        Tool.checkNotEmpty(key);
        DiskLruCache.Editor editor = null;
        OutputStream outputStream = null;
        try {
            editor = diskLruCache.edit(key);
            outputStream = editor.newOutputStream(0);
            Bitmap bitmap = value.getmBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();
        }catch (IOException e){
            e.printStackTrace();
            try {
                editor.abort();
            }catch (IOException e1){
                e1.printStackTrace();
                Log.e(TAG, "put: editor.abort() e:" + e.getMessage());
            }
        }finally {
            try {
                editor.commit();
                diskLruCache.flush();
            }catch (IOException e){
                e.printStackTrace();
                Log.e(TAG, "put: editor.commit(); e:" + e.getMessage());
            }
            if (outputStream != null){
                try {
                    outputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                    Log.e(TAG, "put: outputStream.close(); e:" + e.getMessage());
                }
            }
        }
    }

    public Value get(String key,Bitmap reusalbe){
        Tool.checkNotEmpty(key);
        InputStream inputStream = null;
        Bitmap bitmap = null;
        try {
            if (null == diskLruCache){
                return null;
            }
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            if (null != snapshot){
                Value value = Value.getInstance();
                inputStream = snapshot.getInputStream(0);

                BitmapFactory.Options options =new BitmapFactory.Options();
                options.inMutable = true;
                options.inBitmap = reusalbe;
                bitmap = BitmapFactory.decodeStream(inputStream,null,options);

                value.setmBitmap(bitmap);
                value.setKey(key);
                return value;
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "get: inputStream.close(); e:" + e.getMessage());
                }
            }
        }
        return null;
    }

}
