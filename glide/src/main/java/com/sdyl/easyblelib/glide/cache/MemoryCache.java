package com.sdyl.easyblelib.glide.cache;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

import com.sdyl.easyblelib.glide.resourse.Value;

/**
 * created by lxx
 * on 2020/4/20
 */
public class MemoryCache extends LruCache<String, Value> {

    private MemoryCacheCallback memoryCacheCallback;
    private boolean activeRemove;

    public void setMemoryCacheCallback(MemoryCacheCallback memoryCacheCallback) {
        this.memoryCacheCallback = memoryCacheCallback;
    }

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    public Value activeRemove(String key){
        activeRemove = true;
        Value value = remove(key);
        activeRemove = false;
        return value;
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, Value oldValue, Value newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if (memoryCacheCallback != null && !activeRemove){
            memoryCacheCallback.entryRemoveMemoryCache(key,oldValue);
        }
    }

    @Override
    protected int sizeOf(String key, Value value) {
        Bitmap bitmap = value.getmBitmap();
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        return bitmap.getByteCount();
    }
}
