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
import com.arrowwould.statussaver.photovideo.saveimages.Utility.SharedPrefs;
import com.arrowwould.statussaver.photovideo.saveimages.Utility.Utils;
import com.arrowwould.statussaver.photovideo.saveimages.adapter.RecWappAdapter;
import com.arrowwould.statussaver.photovideo.saveimages.interfaces.OnCheckboxListener;
import com.arrowwould.statussaver.photovideo.saveimages.databinding.WaRecentFragmentBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecentWappBus extends Fragment implements OnCheckboxListener {


    ArrayList<StatusModel> f = new ArrayList<>();
    RecWappAdapter myAdapter;
    ArrayList<StatusModel> filesToDelete = new ArrayList<>();

    int REQUEST_ACTION_OPEN_DOCUMENT_TREE = 1001;
    //    loadDataAsync async;

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
            if (!SharedPrefs.getWBTree(requireActivity()).equals("")) {
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

            if (Utils.appInstalledOrNot(requireActivity(), "com.whatsapp.w4b")) {
                StorageManager sm = (StorageManager) requireActivity().getSystemService(Context.STORAGE_SERVICE);

                Intent intent = null;
                String statusDir = getWhatsupFolder();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
                    Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

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
                Toast.makeText(requireActivity(), "Please Install WhatsApp Business For Download Status!!!!!", Toast.LENGTH_SHORT).show();
            }


        });

        if (!SharedPrefs.getWBTree(requireActivity()).equals("")) {
            populateGrid();
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
            myAdapter = new RecWappAdapter(RecentWappBus.this, f, RecentWappBus.this);
            binding.recentGrid.setAdapter(myAdapter);


        }

        if (requestCode == REQUEST_ACTION_OPEN_DOCUMENT_TREE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.e("onActivityResult: ", "" + data.getData());
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    requireContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            SharedPrefs.setWBTree(requireActivity(), uri.toString());

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
        String treeUri = SharedPrefs.getWBTree(requireActivity());
        DocumentFile fromTreeUri = DocumentFile.fromTreeUri(requireContext().getApplicationContext(), Uri.parse(treeUri));
        if (fromTreeUri != null && fromTreeUri.exists() && fromTreeUri.isDirectory() && fromTreeUri.canRead() && fromTreeUri.canWrite()) {

            return fromTreeUri.listFiles();
        } else {
            return null;
        }
    }

    public String getWhatsupFolder() {
        if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp.w4b/WhatsApp Business" + File.separator + "Media" + File.separator + ".Statuses").isDirectory()) {
            return "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses";
        } else {
            return "WhatsApp Business%2FMedia%2F.Statuses";
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
                       binding. allowAcessBtn.setVisibility(View.GONE);
                        binding.noItemsFound.noItemsFoundLinear.setVisibility(View.GONE);
                    }
                });

                // do in background
                allFiles = null;
                f = new ArrayList<>();
                allFiles = getFromSdcard();
//            Arrays.sort(allFiles, (o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
                for (int i = 0; i < allFiles.length; i++) {
                    if (!allFiles[i].getUri().toString().contains(".nomedia")) {
                        f.add(new StatusModel(allFiles[i].getUri().toString()));
                    }
                }

                // onPostExecute
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(() -> {
                            if (isAdded()) {
                                myAdapter = new RecWappAdapter(RecentWappBus.this, f, RecentWappBus.this);
                                binding.recentGrid.setAdapter(myAdapter);
                               binding. recentGrid.setVisibility(View.VISIBLE);
                            }

                            if (f == null || f.size() == 0) {
                                binding.noItemsFound.noItemsFoundLinear.setVisibility(View.VISIBLE);
                            } else {
                                binding.noItemsFound.noItemsFoundLinear.setVisibility(View.GONE);
                            }
                        }, 100);
                    }
                });
            }
        });
    }


}
