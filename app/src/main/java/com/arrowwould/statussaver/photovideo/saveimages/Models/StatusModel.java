package com.arrowwould.statussaver.photovideo.saveimages.Models;


import android.os.Parcel;
import android.os.Parcelable;

public class StatusModel implements Parcelable {
    public static final Creator<StatusModel> CREATOR = new Creator<StatusModel>() {
        @Override
        public StatusModel createFromParcel(Parcel in) {
            return new StatusModel(in);
        }

        @Override
        public StatusModel[] newArray(int size) {
            return new StatusModel[size];
        }
    };
    public boolean selected = false;
    private String filepath;

    public StatusModel(String filepath) {
        this.filepath = filepath;
    }

    protected StatusModel(Parcel in) {
        filepath = in.readString();
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected2) {
        this.selected = selected2;
    }

    public String getFilePath() {
        return this.filepath;
    }


    public void setFilePath(String paramString) {
        this.filepath = paramString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(filepath);
    }


}
