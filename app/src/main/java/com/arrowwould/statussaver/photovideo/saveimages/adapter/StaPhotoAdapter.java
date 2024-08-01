package com.arrowwould.statussaver.photovideo.saveimages.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.arrowwould.statussaver.photovideo.saveimages.Models.StatusModel;
import com.arrowwould.statussaver.photovideo.saveimages.interfaces.OnCheckboxListener;
import com.arrowwould.statussaver.photovideo.saveimages.PreviewActivity;
import com.arrowwould.statussaver.photovideo.saveimages.R;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class StaPhotoAdapter extends BaseAdapter {

    public OnCheckboxListener onCheckboxListener;
    Fragment context;
    List<StatusModel> arrayList;
    //    int width;
    LayoutInflater inflater;

    public StaPhotoAdapter(Fragment context, List<StatusModel> arrayList, OnCheckboxListener onCheckboxListener) {
        this.context = context;
        this.arrayList = arrayList;

        inflater = (LayoutInflater) context.requireActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        this.onCheckboxListener = onCheckboxListener;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {

        View grid = inflater.inflate(R.layout.wa_row_status_download, null);

        ImageView play = grid.findViewById(R.id.play);

        if (isVideoFile(arrayList.get(arg0).getFilePath())) {
            play.setVisibility(View.VISIBLE);
        } else {
            play.setVisibility(View.GONE);
        }




        ImageView imageView = grid.findViewById(R.id.gridImageVideo);


        Glide.with(context.requireActivity()).load(arrayList.get(arg0).getFilePath()).into(imageView);


        CheckBox checkbox = grid.findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            arrayList.get(arg0).setSelected(isChecked);
            if (onCheckboxListener != null) {
                onCheckboxListener.onCheckboxListener(buttonView, arrayList);
            }
        });
        checkbox.setChecked(arrayList.get(arg0).isSelected());

        imageView.setOnClickListener(view -> {
            Log.e("click", "click");
            Intent intent = new Intent(context.requireActivity(), PreviewActivity.class);
            intent.putParcelableArrayListExtra("images", (ArrayList<? extends Parcelable>) arrayList);
            intent.putExtra("position", arg0);
            intent.putExtra("statusdownload", "download");
            context.startActivity(intent);

        });

        return grid;
    }



    void share(String path) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.setType("video/*");
        Uri photoURI = FileProvider.getUriForFile(
                context.requireActivity().getApplicationContext(),
                context.requireActivity().getApplicationContext()
                        .getPackageName() + ".fileprovider", new File(path));
        share.putExtra(Intent.EXTRA_STREAM,
                photoURI);
        context.startActivity(Intent.createChooser(share, "Share via"));

    }

    public boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }


}
