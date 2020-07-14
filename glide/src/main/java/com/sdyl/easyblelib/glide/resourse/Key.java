package com.sdyl.easyblelib.glide.resourse;

import com.sdyl.easyblelib.glide.Tool;

/**
 * created by lxx
 * on 2020/4/20
 */
public class Key {
    private String key;

    public Key(String path) {
        this.key = Tool.getSHA256StrJava(path);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
