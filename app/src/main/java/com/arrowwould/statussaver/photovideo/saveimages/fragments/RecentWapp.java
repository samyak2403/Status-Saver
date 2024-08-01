package com.arrowwould.statussaver.photovideo.saveimages.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.arrowwould.statussaver.photovideo.saveimages.Models.StatusModel;
import com.arrowwould.statussaver.photovideo.saveimages.adapter.RecWappAdapter;
import com.arrowwould.statussaver.photovideo.saveimages.interfaces.OnCheckboxListener;
import com.arrowwould.statussaver.photovideo.saveimages.Utility.SharedPrefs;
import com.arrowwould.statussaver.photovideo.saveimages.Utility.Utils;
import com.arrowwould.statussaver.photovideo.saveimages.databinding.WaRecentFragmentBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecentWapp extends Fragment implements OnCheckboxListener {


    ArrayList<StatusModel> f = new ArrayList<>();
    RecWappAdapter myAdapter;
    ArrayList<StatusModel> filesToDelete = new ArrayList<>();
    int REQUEST_ACTION_OPEN_DOCUMENT_TREE = 101;

    WaRecentFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = WaRecentFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.swipeToRefresh.setOnRefreshListener(() -> {

            if (!SharedPrefs.getWATree(requireActivity()).equals("")) {
                for (StatusModel deletedFile : filesToDelete) {
                    f.contains(deletedFile.selected = false);
                }
                if (myAdapter != null) {
                    myAdapter.notifyDataSetChanged();
                }
                filesToDelete.clear();

                populateGrid();
            }
            binding.swipeToRefresh.setRefreshing(false);
        });




        binding.sAccessBtn.setOnClickListener(v -> {

            if (Utils.appInstalledOrNot(requireActivity(), "com.whatsapp")) {

                StorageManager sm = (StorageManager) requireActivity().getSystemService(Context.STORAGE_SERVICE);

                String statusDir = getWhatsupFolder();
                Intent intent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
                    Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

                    assert uri != null;
                    String scheme = uri.toString();

                    scheme = scheme.replace("/root/", "/document/");

                    scheme += "%3A" + statusDir;

                    uri = Uri.parse(scheme);

                    intent.putExtra("android.provider.extra.INITIAL_URI", uri);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.putExtra("android.provider.extra.INITIAL_URI", Uri.parse("content://com.android.externalstorage.documents/document/primary%3A" + statusDir));
                }


                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

                startActivityForResult(intent, REQUEST_ACTION_OPEN_DOCUMENT_TREE);
//                ActivityResultLauncher.launch(intent);

            } else {
                Toast.makeText(requireActivity(), "Please Install WhatsApp For Download Status!!!!!", Toast.LENGTH_SHORT).show();
            }
        });

        if (!SharedPrefs.getWATree(requireActivity()).equals("")) {
            populateGrid();
        }

    }

    public void populateGrid() {


        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private DocumentFile[] getFromSdcard() {
        String treeUri = SharedPrefs.getWATree(requireActivity());
        DocumentFile fromTreeUri = DocumentFile.fromTreeUri(requireContext().getApplicationContext(), Uri.parse(treeUri));
        if (fromTreeUri != null && fromTreeUri.exists() && fromTreeUri.isDirectory() && fromTreeUri.canRead() && fromTreeUri.canWrite()) {

            return fromTreeUri.listFiles();
        } else {
            return null;
        }
    }

    public String getWhatsupFolder() {
        if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + ".Statuses").isDirectory()) {
            return "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
        } else {
            return "WhatsApp%2FMedia%2F.Statuses";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && resultCode == 10) {
//            myAdapter.notifyDataSetChanged();
            DocumentFile[] allFiles = null;
            f = new ArrayList<>();
            allFiles = getFromSdcard();
            for (DocumentFile allFile : allFiles) {
                if (!allFile.getUri().toString().contains(".nomedia")) {
                    f.add(new StatusModel(allFile.getUri().toString()));
                }
            }
            Collections.reverse(f);
            myAdapter = new RecWappAdapter(RecentWapp.this, f, RecentWapp.this);
            binding.recentGrid.setAdapter(myAdapter);


        }


        if (requestCode == REQUEST_ACTION_OPEN_DOCUMENT_TREE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.e("onActivityResult: ", "" + data.getData());
            try {
                requireContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } catch (Exception e) {
                e.printStackTrace();
            }

            SharedPrefs.setWATree(requireActivity(), uri.toString());

            populateGrid();
        }

    }

    @Override
    public void onCheckboxListener(View view, List<StatusModel> list) {
        filesToDelete.clear();
        for (StatusModel details : list) {
            if (details.isSelected()) {
                filesToDelete.add(details);
            }
        }

    }

    public void loadData() {
        ExecutorService Service = Executors.newSingleThreadExecutor();
        Service.execute(new Runnable() {
            DocumentFile[] allFiles;

            @Override
            public void run() {
                // onPreExecute
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.recentGrid.setVisibility(View.GONE);
                        binding.allowAcessBtn.setVisibility(View.GONE);
                        binding.noItemsFound.noItemsFoundLinear.setVisibility(View.GONE);
                    }
                });

                // do in background
                allFiles = null;
                f = new ArrayList<>();
                allFiles = getFromSdcard();
//            Arrays.sort(allFiles, (o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
                for (int i = 0; i < Objects.requireNonNull(allFiles).length; i++) {
                    if (!allFiles[i].getUri().toString().contains(".nomedia")) {
                        f.add(new StatusModel(allFiles[i].getUri().toString()));
                    }
                }
                Collections.reverse(f);

                // onPostExecute
                requireActivity().runOnUiThread(() -> new Handler().postDelayed(() -> {
                    if (isAdded()) {
                        myAdapter = new RecWappAdapter(RecentWapp.this, f, RecentWapp.this);
                        binding.recentGrid.setAdapter(myAdapter);
                        binding.recentGrid.setVisibility(View.VISIBLE);
                    }

                    if (f == null || f.isEmpty()) {
                        binding.noItemsFound.noItemsFoundLinear.setVisibility(View.VISIBLE);
                    } else {
                        binding.noItemsFound.noItemsFoundLinear.setVisibility(View.GONE);
                    }
                }, 100));
            }
        });
    }

}
