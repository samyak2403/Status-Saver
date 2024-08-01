package com.arrowwould.statussaver.photovideo.saveimages;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.arrowwould.statussaver.photovideo.saveimages.R;
import com.arrowwould.statussaver.photovideo.saveimages.Utility.Utils;


public class WaVideoActivity extends AppCompatActivity {

    VideoView displayVV;
    ImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wa_activity_video_preview);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        backIV = findViewById(R.id.backIV);
        backIV.setOnClickListener(view -> finish());


        displayVV = (VideoView) findViewById(R.id.displayVV);

        displayVV.setVideoPath(Utils.mPath);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(displayVV);

        displayVV.setMediaController(mediaController);

        displayVV.start();


    }


    @Override
    protected void onResume() {
        super.onResume();
        displayVV.setVideoPath(Utils.mPath);
        displayVV.start();
    }


}
