package com.arrowwould.statussaver.photovideo.saveimages;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.arrowwould.statussaver.photovideo.saveimages.R;
import com.arrowwould.statussaver.photovideo.saveimages.adapter.ViewPagerAdapter;
import com.arrowwould.statussaver.photovideo.saveimages.fragments.StaPhotos;
import com.arrowwould.statussaver.photovideo.saveimages.fragments.StaVideos;
import com.arrowwould.statussaver.photovideo.saveimages.Utility.Utils;
import com.arrowwould.statussaver.photovideo.saveimages.databinding.WaActivityDownloadStatusBinding;

public class DownloadStatusActivity extends AppCompatActivity {

    DownloadStatusActivity activity;
    WaActivityDownloadStatusBinding binding;

    private Utils utils;

    StaPhotos staPhotos;
    StaVideos staVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = WaActivityDownloadStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        utils = new Utils(activity);
        staPhotos = new StaPhotos();
        staVideos = new StaVideos();

        binding.toolbar.toolbarTxt.setText(R.string.downloaded_status);
        binding.toolbar.imBack.setOnClickListener(v -> finish());


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(staPhotos);
        viewPagerAdapter.addFragment(staVideos);

        binding.viewpagerDownloadStatus.setAdapter(viewPagerAdapter);


        binding.viewpagerDownloadStatus.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.bottomNevigationBar.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.bottomNevigationBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.single_story_icon) {
                binding.viewpagerDownloadStatus.setCurrentItem(0, true);
                return true;
            } else if (itemId == R.id.single_photos_icon) {
                binding.viewpagerDownloadStatus.setCurrentItem(1, true);
                return true;
            }
            return false;
        });


    }

}
