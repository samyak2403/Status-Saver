package com.arrowwould.statussaver.photovideo.saveimages.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.arrowwould.statussaver.photovideo.saveimages.Models.StatusModel;
import com.arrowwould.statussaver.photovideo.saveimages.interfaces.OnCheckboxListener;
import com.arrowwould.statussaver.photovideo.saveimages.PreviewActivity;
import com.arrowwould.statussaver.photovideo.saveimages.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class RecWappAdapter extends BaseAdapter {

    public OnCheckboxListener onCheckboxListener;
    Fragment context;
    List<StatusModel> arrayList;
    int width;
    LayoutInflater inflater;

    public RecWappAdapter(Fragment context, List<StatusModel> arrayList, OnCheckboxListener onCheckboxListener) {
        this.context = context;
        this.arrayList = arrayList;

        inflater = (LayoutInflater) context.requireActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        width = displayMetrics.widthPixels; // width of the device

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
        View grid = inflater.inflate(R.layout.wa_row_status_recent, null);
        CircleImageView imageView = grid.findViewById(R.id.gridImage);

        Glide.with(context.requireActivity()).load(arrayList.get(arg0).getFilePath()).into(imageView);



        imageView.setOnClickListener(view -> {
            Log.e("click", "click");
            Intent intent = new Intent(context.requireActivity(), PreviewActivity.class);

            intent.putParcelableArrayListExtra("images", (ArrayList<? extends Parcelable>) arrayList);
            intent.putExtra("position", arg0);
            intent.putExtra("statusdownload", "no");

            context.startActivity(intent);

        });
        return grid;
    }

}
