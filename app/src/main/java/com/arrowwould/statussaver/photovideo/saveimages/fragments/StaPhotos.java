package com.arrowwould.statussaver.photovideo.saveimages.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.arrowwould.statussaver.photovideo.saveimages.Models.StatusModel;
import com.arrowwould.statussaver.photovideo.saveimages.adapter.StaPhotoAdapter;
import com.arrowwould.statussaver.photovideo.saveimages.interfaces.DialogButtonClickListener;
import com.arrowwould.statussaver.photovideo.saveimages.interfaces.OnCheckboxListener;
import com.arrowwould.statussaver.photovideo.saveimages.R;
import com.arrowwould.statussaver.photovideo.saveimages.Utility.CustomDialog;
import com.arrowwould.statussaver.photovideo.saveimages.databinding.WaDownloadFragmentBinding;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StaPhotos extends Fragment implements OnCheckboxListener {


    ArrayList<StatusModel> f = new ArrayList<>();
    StaPhotoAdapter myAdapter;
    int save = 10;
    ArrayList<StatusModel> filesToDelete = new ArrayList<>();

    WaDownloadFragmentBinding binding;
    //    loadDataAsync async;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = WaDownloadFragmentBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populateGrid();


        binding.deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!filesToDelete.isEmpty()) {
                    CustomDialog.showDeleteDialog(requireActivity(), getString(R.string.delete_files_items), "", null, null, new DialogButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick() {
                            //  new deleteAll().execute();
                            deleteAll();
                        }

                        @Override
                        public void onNegativeButtonClick() {

                        }
                    });

                }
            }
        });


        binding.selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (!compoundButton.isPressed()) {
                    return;
                }

                filesToDelete.clear();

                for (int i = 0; i < f.size(); i++) {
                    if (!f.get(i).selected) {
                        b = true;
                        break;
                    }
                }

                if (b) {
                    for (int i = 0; i < f.size(); i++) {
                        f.get(i).selected = true;
                        filesToDelete.add(f.get(i));
                    }
                    binding.selectAll.setChecked(true);
                } else {
                    for (int i = 0; i < f.size(); i++) {
                        f.get(i).selected = false;
                    }
                    binding.actionLay.setVisibility(View.GONE);
                }
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    public void populateGrid() {
//        async = new loadDataAsync();
//        async.execute();
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (async != null) {
//            async.cancel(true);
//        }
    }

    public void getFromSdcard() {
        File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "Download" + File.separator + getResources().getString(R.string.app_name) + "/Images");
        f = new ArrayList<>();
        if (file.isDirectory()) {
            File[] listFile = file.listFiles();
            Arrays.sort(listFile, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            for (int i = 0; i < listFile.length; i++) {
                f.add(new StatusModel(listFile[i].getAbsolutePath()));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == save && resultCode == save) {
            myAdapter.notifyDataSetChanged();

            populateGrid();
            binding.actionLay.setVisibility(View.GONE);
            binding.selectAll.setChecked(false);
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
        if (filesToDelete.size() == f.size()) {
            binding.selectAll.setChecked(true);
        }
        if (!filesToDelete.isEmpty()) {
            binding.actionLay.setVisibility(View.VISIBLE);
            return;
        }
        binding.selectAll.setChecked(false);
        binding.actionLay.setVisibility(View.GONE);
    }

    /*    class deleteAll extends AsyncTask<Void, Void, Void> {
            AlertDialog alertDialog;
            int success = -1;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                alertDialog = Utils.loadingPopup(getActivity());
                alertDialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                ArrayList<StatusModel> deletedFiles = new ArrayList<>();
                for (int i = 0; i < filesToDelete.size(); i++) {
                    StatusModel details = filesToDelete.get(i);
                    File file = new File(details.getFilePath());
                    if (file.exists()) {
                        if (file.delete()) {
                            deletedFiles.add(details);
                            if (success == 0) {
                                break;
                            }
                            success = 1;
                        } else {
                            success = 0;
                        }
                    } else {
                        success = 0;
                    }
                }

                filesToDelete.clear();
                for (StatusModel deletedFile : deletedFiles) {
                    f.remove(deletedFile);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                myAdapter.notifyDataSetChanged();
                if (success == 0) {
                    Toast.makeText(getContext(), "Couldn't delete some files", Toast.LENGTH_SHORT).show();
                } else if (success == 1) {
                    Toast.makeText(getActivity(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                }
                actionLay.setVisibility(View.GONE);
                selectAll.setChecked(false);
                alertDialog.dismiss();


            }
        }*/
    public void deleteAll() {
        ExecutorService Service = Executors.newSingleThreadExecutor();
        Service.execute(new Runnable() {

            int success = -1;

            @Override
            public void run() {


                // onPreExecute
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomDialog.showProgressDialog(requireActivity());
                    }
                });

                // do in background
                ArrayList<StatusModel> deletedFiles = new ArrayList<>();
                for (int i = 0; i < filesToDelete.size(); i++) {
                    StatusModel details = filesToDelete.get(i);
                    File file = new File(details.getFilePath());
                    if (file.exists()) {
                        if (file.delete()) {
                            deletedFiles.add(details);
                            if (success == 0) {
                                break;
                            }
                            success = 1;
                        } else {
                            success = 0;
                        }
                    } else {
                        success = 0;
                    }
                }

                filesToDelete.clear();
                for (StatusModel deletedFile : deletedFiles) {
                    f.remove(deletedFile);
                }

                // onPostExecute
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter.notifyDataSetChanged();
                        if (success == 0) {
                            Toast.makeText(getContext(), "Couldn't delete some files", Toast.LENGTH_SHORT).show();
                        } else if (success == 1) {
                            Toast.makeText(requireActivity(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                        binding.actionLay.setVisibility(View.GONE);
                        binding.selectAll.setChecked(false);
                        CustomDialog.hideProgressDialog();
                    }
                });
            }
        });
    }

    /*    class loadDataAsync extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loaderLay.setVisibility(View.VISIBLE);
                imagegrid.setVisibility(View.GONE);
                emptyLay.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                getFromSdcard();
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);

                new Handler().postDelayed(() -> {
                    if (requireActivity() != null) {
                        if (f != null && f.size() != 0) {
                            myAdapter = new StaPhotoAdapter(StaPhotos.this, f, StaPhotos.this);
                            imagegrid.setAdapter(myAdapter);
                            imagegrid.setVisibility(View.VISIBLE);
                        }
                        loaderLay.setVisibility(View.GONE);
                    }

                    if (f == null || f.size() == 0) {
                        emptyLay.setVisibility(View.VISIBLE);
                    } else {
                        emptyLay.setVisibility(View.GONE);
                    }

                }, 500);
            }
        }*/
    public void loadData() {
        ExecutorService Service = Executors.newSingleThreadExecutor();
        Service.execute(new Runnable() {

            @Override
            public void run() {


                // onPreExecute
                requireActivity().runOnUiThread(() -> {
                    binding.videoGrid.setVisibility(View.GONE);
                    binding.noItemsFound.noItemsFoundLinear.setVisibility(View.GONE);
                });

                // do in background
                getFromSdcard();

                // onPostExecute
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(() -> {
                            if (requireActivity() != null) {
                                if (f != null && f.size() != 0) {
                                    myAdapter = new StaPhotoAdapter(StaPhotos.this, f, StaPhotos.this);
                                    binding.videoGrid.setAdapter(myAdapter);
                                    binding.videoGrid.setVisibility(View.VISIBLE);
                                }

                            }

                            if (f == null || f.size() == 0) {
                                binding.noItemsFound.noItemsFoundLinear.setVisibility(View.VISIBLE);
                            } else {
                                binding.noItemsFound.noItemsFoundLinear.setVisibility(View.GONE);
                            }
                        }, 500);
                    }
                });
            }
        });
    }
}
