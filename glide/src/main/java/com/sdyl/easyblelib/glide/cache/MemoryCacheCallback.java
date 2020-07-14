package com.sdyl.easyblelib.glide.cache;

import com.sdyl.easyblelib.glide.resourse.Value;

/**
 * created by lxx
 * on 2020/4/20
 */
public interface MemoryCacheCallback {
    public void entryRemoveMemoryCache(String key, Value oldValue);
}
