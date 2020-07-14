package com.sdyl.easyblelib.glide.cache;

import com.sdyl.easyblelib.glide.Tool;
import com.sdyl.easyblelib.glide.resourse.Value;
import com.sdyl.easyblelib.glide.resourse.ValueCallback;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * created by lxx
 * on 2020/4/20
 */
public class ActiveCache {

    private Map<String,WeakReference<Value>> mapList = new HashMap<>();
    private ReferenceQueue<Value> referenceQueue;
    private Thread thread;
    private boolean isCloseThread;
    private boolean isActiveClose;

    private ValueCallback valueCallback;

    public ActiveCache(ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
    }

    public void put(String key, Value value){
        Tool.checkNotEmpty(key);
        value.setCallback(valueCallback);
        mapList.put(key,new CustomWeakReference(value,getReferenceQueue(),key));
    }



    public Value get(String key){
        WeakReference<Value> valueWeakReference = mapList.get(key);
        if (null != valueWeakReference){
            return valueWeakReference.get();
        }
        return null;
    }

    public Value remove(String key){
        isActiveClose = true;
        WeakReference<Value> remove = mapList.remove(key);
        isActiveClose = false;

        if (null != remove){
            return remove.get();
        }

        return null;
    }

    public void closeThread(){
        isCloseThread = true;
        mapList.clear();
        System.gc();;
    }

    public class CustomWeakReference extends WeakReference<Value>{

        private String key;

        public CustomWeakReference(Value referent, ReferenceQueue<? super Value> q,String key) {
            super(referent, q);
            this.key = key;
        }
    }

    private ReferenceQueue<? super Value> getReferenceQueue() {
        if (referenceQueue == null){
            referenceQueue = new ReferenceQueue<>();
            thread = new Thread(){
                @Override
                public void run() {
                    super.run();
                    while (!isCloseThread){
                        try {
                            Reference<? extends Value> remove = referenceQueue.remove();

                            CustomWeakReference weakReference = (CustomWeakReference) remove;
                            if (mapList !=null && !mapList.isEmpty()){
                                mapList.remove(weakReference.key);
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.start();
        }
        return referenceQueue;
    }
}
