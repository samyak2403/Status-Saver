package com.arrowwould.statussaver.photovideo.saveimages;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.arrowwould.statussaver.photovideo.saveimages.BuildConfig;
import com.arrowwould.statussaver.photovideo.saveimages.R;
import com.arrowwould.statussaver.photovideo.saveimages.adapter.ViewPagerAdapter;
import com.arrowwould.statussaver.photovideo.saveimages.fragments.RecentWapp;
import com.arrowwould.statussaver.photovideo.saveimages.fragments.RecentWappBus;
import com.arrowwould.statussaver.photovideo.saveimages.Utility.PermissionUtility;
import com.arrowwould.statussaver.photovideo.saveimages.Utility.Utils;
import com.arrowwould.statussaver.photovideo.saveimages.databinding.WaActivityRecentStatusBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecentStatusActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private Utils utils;

    RecentStatusActivity activity;

    WaActivityRecentStatusBinding binding;

    private DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = WaActivityRecentStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;

        utils = new Utils(activity);

        drawerLayout = binding.drawerLayout;
        navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        toggle.syncState();

        binding.toolbar.imBack.setImageResource(R.drawable.menu);

        binding.toolbar.imBack.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }

        });

        binding.toolbar.toolbarTxt.setText(R.string.app_name);

        requestPermission();



        recentWapp = new RecentWapp();
        recentWappBus = new RecentWappBus();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(recentWapp);
        viewPagerAdapter.addFragment(recentWappBus);

        binding.viewpagerRecent.setAdapter(viewPagerAdapter);


        binding.viewpagerRecent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                binding.viewpagerRecent.setCurrentItem(0, true);
                return true;
            } else if (itemId == R.id.single_photos_icon) {
                binding.viewpagerRecent.setCurrentItem(1, true);
                return true;
            }
            return false;
        });


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finishAffinity();
                }
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.gallery) {
            startActivity(new Intent(RecentStatusActivity.this, DownloadStatusActivity.class));
        } else if (itemId == R.id.nav_share) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                String message = getResources().getString(R.string.share_app_message);
                shareIntent.putExtra(Intent.EXTRA_TEXT, message + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                startActivity(Intent.createChooser(shareIntent, "choose one"));

            } catch (Exception e) {

            }
        } else if (itemId == R.id.nav_rate_us) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
            }

        } else if (itemId == R.id.nav_feedback) {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] recipients = {this.getResources().getString(R.string.email_feedback)};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, this.getResources().getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_CC, this.getResources().getString(R.string.email_feedback));
                intent.putExtra(Intent.EXTRA_TEXT, "Feedback: " +  "\n\n" + "App Version: V " + BuildConfig.VERSION_NAME + "\n SDK Level: " + Build.VERSION.SDK_INT);
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                this.startActivity(Intent.createChooser(intent, "Send mail"));
            }catch (Exception e){

            }

        } else if (itemId == R.id.nav_privacy) {
            utils.openCustomTab(activity, getResources().getString(R.string.policy_url));
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    RecentWapp recentWapp;
    RecentWappBus recentWappBus;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }
    public void requestPermission() {
        PermissionUtility permissionUtility = new PermissionUtility(this, requestPermissionLauncher);
        if (!permissionUtility.isPermissiongGranted()) {
            permissionUtility.requestPermissions();
        }
    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {

        @Override
        public void onActivityResult(Map<String, Boolean> result) {

            boolean allPermissionClear = true;
            List<String> blockPermissionCheck = new ArrayList<>();
            for (String key : result.keySet()) {
                if (Boolean.FALSE.equals(result.get(key))) {
                    allPermissionClear = false;
                    blockPermissionCheck.add(PermissionUtility.getPermissionStatus(activity, key));
                }
            }
//            if (blockPermissionCheck.contains("blocked")) {
//                showPermissionDialog("Permission Required", "This App requires for Particular features to work as expected as Save Photos and Videos");
//            }
        }
    });

}
