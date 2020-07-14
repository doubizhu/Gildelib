package com.sdyl.easyblelib.glide.load;


import com.sdyl.easyblelib.glide.resourse.Value;

/**
 * 加载外部资源 成功 和 失败 回调
 */
public interface ResponseListener {

    public void responseSuccess(Value value);

    public void responseException(Exception e);

}
