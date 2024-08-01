package com.arrowwould.statussaver.photovideo.saveimages.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.arrowwould.statussaver.photovideo.saveimages.Models.StatusModel;
import com.jsibbold.zoomage.ZoomageView;
import com.arrowwould.statussaver.photovideo.saveimages.R;
import com.arrowwould.statussaver.photovideo.saveimages.Utility.Utils;
import com.arrowwould.statussaver.photovideo.saveimages.WaVideoActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FullscreenImageAdapter extends RecyclerView.Adapter<FullscreenImageAdapter.ViewHolder> {
    Context context;
    ArrayList<StatusModel> imageList;

    public FullscreenImageAdapter(Context context, ArrayList<StatusModel> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.wa_preview_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        if (!Utils.getBack(imageList.get(position).getFilePath(), "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)").isEmpty()) {
            holder.iconplayer.setVisibility(View.VISIBLE);
        } else {
            holder.iconplayer.setVisibility(View.GONE);
        }

        Glide.with(context).load(imageList.get(position).getFilePath()).into(holder.imageView);

        holder.iconplayer.setOnClickListener(view -> {
            if (!Utils.getBack(imageList.get(position).getFilePath(), "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)").isEmpty()) {
                Utils.mPath = imageList.get(position).getFilePath();
                context.startActivity(new Intent(context, WaVideoActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private final ZoomageView imageView;
        private final ImageView iconplayer;


        public ViewHolder(@NonNull @NotNull View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            iconplayer = view.findViewById(R.id.iconplayer);

        }
    }
}
