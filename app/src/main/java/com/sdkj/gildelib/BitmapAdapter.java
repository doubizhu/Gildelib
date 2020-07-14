package com.sdkj.gildelib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sdyl.easyblelib.glide.api.Glide;


public class BitmapAdapter extends RecyclerView.Adapter<BitmapAdapter.BitmapViewHolder> {

    private Context context;

    public BitmapAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public BitmapViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item, null, false);
        BitmapViewHolder bitmapViewHolder = new BitmapViewHolder(view);
        return bitmapViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BitmapViewHolder bitmapViewHolder, int i) {
        Glide.with(context).load("https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg").
                into(bitmapViewHolder.iv);
    }

    @Override
    public int getItemCount() {
        return 1000;
    }

    class BitmapViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv;

        public BitmapViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
        }

    }
}
