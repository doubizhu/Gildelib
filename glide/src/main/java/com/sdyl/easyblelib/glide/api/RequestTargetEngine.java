package com.sdyl.easyblelib.glide.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import com.sdyl.easyblelib.glide.Tool;
import com.sdyl.easyblelib.glide.cache.ActiveCache;
import com.sdyl.easyblelib.glide.cache.MemoryCache;
import com.sdyl.easyblelib.glide.cache.MemoryCacheCallback;
import com.sdyl.easyblelib.glide.cache.disk.DiskLruCacheImpl;
import com.sdyl.easyblelib.glide.fragment.LifecycleCallback;
import com.sdyl.easyblelib.glide.load.LoadDataManager;
import com.sdyl.easyblelib.glide.load.ResponseListener;
import com.sdyl.easyblelib.glide.resourse.Key;
import com.sdyl.easyblelib.glide.resourse.Value;
import com.sdyl.easyblelib.glide.resourse.ValueCallback;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * created by lxx
 * on 2020/4/20
 */
public class RequestTargetEngine implements LifecycleCallback, ValueCallback, MemoryCacheCallback, ResponseListener {

    private final String TAG = RequestTargetEngine.class.getSimpleName();

    @Override
    public void glideInitAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期之 已经开启了 初始化了....");
    }

    @Override
    public void glideStopAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期之 已经停止中 ....");
    }

    @Override
    public void glideRecycleAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期之 进行释放操作 缓存策略释放操作等 >>>>>> ....");
        shutDown = true;
        if (activeCache != null){
            activeCache.closeThread();
        }
    }

    private ActiveCache activeCache; // 活动缓存
    private MemoryCache memoryCache; // 内存缓存
    private DiskLruCacheImpl diskLruCache; // 磁盘缓存
    private Set<WeakReference<Value>> reusablePool;//内存复用池

    private final int MEMORY_MAX_SIZE = 1024 * 1024 * 60;

    public RequestTargetEngine(Context context){
        if (activeCache == null){
            activeCache = new ActiveCache(this);
        }
        if (memoryCache == null){
            memoryCache = new MemoryCache(MEMORY_MAX_SIZE);
            memoryCache.setMemoryCacheCallback(this);
        }
        if (reusablePool == null){
            reusablePool = Collections.synchronizedSet(new HashSet<WeakReference<Value>>());
        }


        diskLruCache = new DiskLruCacheImpl(context);
    }

    @Override
    public void entryRemoveMemoryCache(String key, Value oldValue) {
            if (oldValue.getmBitmap().isMutable()){//是否易变
                reusablePool.add(new WeakReference<Value>(oldValue,getReusableQueue()));
                Log.d(TAG, "cacheAction: 加入到内存复用池当中去");
            }else {
                oldValue.getmBitmap().recycle();
            }
    }


    private ReferenceQueue<Value> referenceQueue;
    boolean shutDown = false;
    private ReferenceQueue<Value> getReusableQueue() {
        if (referenceQueue == null){
            referenceQueue = new ReferenceQueue<>();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!shutDown){
                        try {
                            Reference<? extends Value> remove = referenceQueue.remove();
                            Bitmap bitmap =remove.get().getmBitmap();
                            if (bitmap != null && !bitmap.isRecycled()) {
                                bitmap.recycle();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }
        return null;
    }

    @Override
    public void responseSuccess(Value value) {
        if (null != value){
            saveCache(key,value);
            imageView.setImageBitmap(value.getmBitmap());
        }
    }

    @Override
    public void responseException(Exception e) {
        Log.d(TAG, "responseException: 加载外部资源失败 e:" + e.getMessage());
    }

    @Override
    public void valueNonUseListener(Object key, Object value) {
        if (key != null && value != null){
            memoryCache.put((String)key,(Value) value);
        }
    }

    public void into(ImageView imageView){
        this.imageView = imageView;
        imageView.getContext().getApplicationContext();

        Tool.checkNotEmpty(imageView);
        Tool.assertMainThread();

        Value value = cacheAction();
        if (null != value){
            value.nonUseAction();
            imageView.setImageBitmap(value.getmBitmap());
        }
    }

    private Value cacheAction(){
        Bitmap reusable = null;
        Value value = activeCache.get(key);
        if (null != value){
            Log.d(TAG, "cacheAction: 本次加载的是在（活动缓存）中获取的资源>>>");
            value.useAction();
            return value;
        }

        value = memoryCache.get(key);
        if (null != value){
            Log.d(TAG, "cacheAction: 本次加载的是在（内存缓存）中获取的资源>>>");
            memoryCache.activeRemove(key);
            activeCache.put(key,value);
            value.useAction();
            return value;
        }
        reusable =getReusable(60,60,1);
        value = diskLruCache.get(key,reusable);
        if (null != value){
            Log.d(TAG, "cacheAction: 本次加载的是在（磁盘缓存）中获取的资源>>>");
            activeCache.put(key,value);
            value.useAction();
            return value;
        }
        value = new LoadDataManager().loadResource(path,this,glideContext,reusable);
        if (null != value){
            return value;
        }
        return null;
    }

    private String path;
    private Context glideContext;
    private String key;
    private ImageView imageView;

    public void loadValueInitAction(String path, Context requestManagerContext) {
        this.path = path;
        this.glideContext = requestManagerContext;
        this.key = new Key(path).getKey();
    }

    private void saveCache(String key,Value value){
        Log.d(TAG, "saveCahce: >>>>>>>>>>>>>>>>>>>>>>>>>> 加载外置资源成功后 ，保存到缓存中， key:" + key + " value:" + value);
        value.setKey(key);
        if (diskLruCache != null){
            diskLruCache.put(key,value);
        }
    }

    public Bitmap getReusable(int w,int h,int inSampleSize){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
            return null;
        }
        Bitmap reusable = null;
        Iterator<WeakReference<Value>> iterator =reusablePool.iterator();
        while (iterator.hasNext()){
            Bitmap bitmap = iterator.next().get().getmBitmap();
            if (bitmap == null){
                if (isBitmapReusable(bitmap,w,h,inSampleSize)){
                    reusable = bitmap;
                    iterator.remove();
                    break;
                }
            }else {
                iterator.remove();
            }
        }
        return reusable;
    }

    private boolean isBitmapReusable(Bitmap bitmap,int w,int h,int inSampleSize){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            return bitmap.getWidth() == w && bitmap.getHeight() == h && inSampleSize==1;
        }

        if (inSampleSize > 1){
            w /= inSampleSize;
            h /= inSampleSize;
        }
        int byteCount = getBytesPerPixel(bitmap.getConfig());
        return byteCount <= bitmap.getAllocationByteCount();
    }

    private int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        }
        return 2;
    }
}
