package com.sdyl.easyblelib.glide.load;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.sdyl.easyblelib.glide.resourse.Value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * created by lxx
 * on 2020/4/20
 */
public class LoadDataManager implements ILoadData,Runnable {

    private final static String TAG = LoadDataManager.class.getSimpleName();

    private String path;
    private ResponseListener responseListener;
    private Context context;
    private Bitmap reusable;

    @Override
    public Value loadResource(String path, ResponseListener responseListener, Context context,Bitmap reusable) {
        this.path = path;
        this.responseListener = responseListener;
        this.context = context;
        this.reusable = reusable;

        Uri uri = Uri.parse(path);
        if ("HTTP".equalsIgnoreCase(uri.getScheme()) || "HTTPS".equalsIgnoreCase(uri.getScheme())){
            new ThreadPoolExecutor(0,Integer.MAX_VALUE,60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>()).execute(this);
        }
        return null;
    }

    @Override
    public void run() {
        InputStream inputStream =  null;
        HttpURLConnection httpURLConnection = null; // HttpURLConnection内部已经是Okhttp，因为太高效了

        try {
            URL url = new URL(path);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(5000);

            final int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                byte[] inputStream2ByteArr = inputStream2ByteArr(inputStream);
                //TODO 网络上获取图片 模拟接收过程。

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeByteArray(inputStream2ByteArr,0,inputStream2ByteArr.length,options);

                int w = options.outWidth;
                int h = options.outHeight;

                options.inSampleSize = caculateInsampleSize(w,h,80,80);

                options.inJustDecodeBounds = false;

                options.inMutable =true;
                options.inBitmap = reusable;

                final Bitmap bitmap =  BitmapFactory.decodeByteArray(inputStream2ByteArr,0,inputStream2ByteArr.length,options);

                // 成功 切换主线程
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Value value = Value.getInstance();
                        value.setmBitmap(bitmap);

                        // 回调成功
                        responseListener.responseSuccess(value);
                    }
                });
            } else {
                // 失败 切换主线程
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // 回调失败
                        responseListener.responseException(new IllegalStateException("请求失败，请求码：" + responseCode));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: 关闭 inputStream.close(); e:" + e.getMessage());
                }
            }

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    private int caculateInsampleSize(int w,int h,int maxW,int maxH){
        int inSampleSize = 1;
        if (w > maxW && h >maxH){
            inSampleSize = 2;
            while (w / inSampleSize > maxW && h / inSampleSize > maxH){
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private byte[] inputStream2ByteArr(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buff)) != -1) {
            outputStream.write(buff, 0, len);
        }
        inputStream.close();
        outputStream.close();
        return outputStream.toByteArray();
    }
}
