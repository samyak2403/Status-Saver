package com.arrowwould.statussaver.photovideo.saveimages;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.arrowwould.statussaver.photovideo.saveimages.Models.StatusModel;
import com.arrowwould.statussaver.photovideo.saveimages.adapter.FullscreenImageAdapter;
import com.arrowwould.statussaver.photovideo.saveimages.R;
import com.arrowwould.statussaver.photovideo.saveimages.interfaces.DialogButtonClickListener;
import com.arrowwould.statussaver.photovideo.saveimages.Utility.CustomDialog;
import com.arrowwould.statussaver.photovideo.saveimages.Utility.Utils;
import com.arrowwould.statussaver.photovideo.saveimages.databinding.WaActivityPagerPreviewBinding;

import java.io.File;
import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {


    ArrayList<StatusModel> imageList;
    int position;
    FullscreenImageAdapter fullscreenImageAdapter;
    String statusdownload;

    PreviewActivity activity;
    WaActivityPagerPreviewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = WaActivityPagerPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity= this;

        imageList = getIntent().getParcelableArrayListExtra("images");
        position = getIntent().getIntExtra("position", 0);
        statusdownload = getIntent().getStringExtra("statusdownload");

        assert statusdownload != null;
        if (statusdownload.equals("download")) {
            binding.downloadIV.setVisibility(View.GONE);
        } else {
            binding.downloadIV.setVisibility(View.VISIBLE);
        }

        fullscreenImageAdapter = new FullscreenImageAdapter(PreviewActivity.this, imageList);
        binding.viewPager.setAdapter(fullscreenImageAdapter);
        binding.viewPager.setCurrentItem(position);

        binding.viewPager.setClipChildren(false);
        binding.viewPager.setClipToPadding(false);
        binding.viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(20));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.14f);
        });
        binding.viewPager.setPageTransformer(transformer);


        binding.downloadIV.setOnClickListener(this);
        binding.shareIV.setOnClickListener(this);
        binding.deleteIV.setOnClickListener(this);
        binding.waBackButton.setOnClickListener(this);
        binding.wAppIV.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == binding.waBackButton) {
            finish();
        } else if (v == binding.downloadIV) {
            if (!imageList.isEmpty()) {
                try {
                    Utils.download(PreviewActivity.this, imageList.get(binding.viewPager.getCurrentItem()).getFilePath());
                    Toast.makeText(PreviewActivity.this, "Status saved successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(PreviewActivity.this, "Sorry we can't move file.try with other file.", Toast.LENGTH_LONG).show();
                }
            } else {
                finish();
            }
        } else if (v == binding.shareIV) {
            if (!imageList.isEmpty()) {
                Utils.shareFile(PreviewActivity.this, Utils.isVideoFile(PreviewActivity.this, imageList.get(binding.viewPager.getCurrentItem()).getFilePath()), imageList.get(binding.viewPager.getCurrentItem()).getFilePath());
            } else {
                finish();
            }
        } else if (v == binding.deleteIV) {
            if (!imageList.isEmpty()) {
                CustomDialog.showDeleteDialog(activity, getString(R.string.delete_status_items), "", null, null, new DialogButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        int currentItem = 0;

                        if (statusdownload.equals("download")) {
                            File file = new File(imageList.get(binding.viewPager.getCurrentItem()).getFilePath());
                            if (file.exists()) {
                                boolean del = file.delete();
                                delete(currentItem);
                            }
                        } else {
                            DocumentFile fromTreeUri = DocumentFile.fromSingleUri(PreviewActivity.this, Uri.parse(imageList.get(binding.viewPager.getCurrentItem()).getFilePath()));
                            assert fromTreeUri != null;
                            if (fromTreeUri.exists()) {
                                boolean del = fromTreeUri.delete();
                                delete(currentItem);
                            }
                        }
                    }

                    @Override
                    public void onNegativeButtonClick() {

                    }
                });
            } else {
                finish();
            }
        } else if (v == binding.wAppIV) {
            Utils.repostWhatsApp(PreviewActivity.this, Utils.isVideoFile(PreviewActivity.this, imageList.get(binding.viewPager.getCurrentItem()).getFilePath()), imageList.get(binding.viewPager.getCurrentItem()).getFilePath());

        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void delete(int currentItem) {
        if (!imageList.isEmpty() && binding.viewPager.getCurrentItem() < imageList.size()) {
            currentItem = binding.viewPager.getCurrentItem();
        }
        imageList.remove(binding.viewPager.getCurrentItem());
        fullscreenImageAdapter = new FullscreenImageAdapter(PreviewActivity.this, imageList);
        binding.viewPager.setAdapter(fullscreenImageAdapter);

        Intent intent = new Intent();
        setResult(10, intent);

        if (!imageList.isEmpty()) {
            binding.viewPager.setCurrentItem(currentItem);
        } else {
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
