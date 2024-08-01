package com.arrowwould.statussaver.photovideo.saveimages.Utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arrowwould.statussaver.photovideo.saveimages.interfaces.DialogButtonClickListener;
import com.arrowwould.statussaver.photovideo.saveimages.R;

import java.util.Objects;

public class CustomDialog {
    static Dialog dialogProgress;

    public static void showProgressDialog(Context context) {
        dialogProgress = new Dialog(context);
        dialogProgress.setContentView(R.layout.dialog_loading);
        Objects.requireNonNull(dialogProgress.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Objects.requireNonNull(dialogProgress.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogProgress.getWindow().setGravity(Gravity.CENTER);
        dialogProgress.setCanceledOnTouchOutside(true);
        dialogProgress.show();
    }

    public static void hideProgressDialog() {
        if (dialogProgress != null && dialogProgress.isShowing()) {
            dialogProgress.dismiss();
        }
    }

    public static void showDeleteDialog(Context context, String titleText, String secondTxt, String positiveText, String negativeText, DialogButtonClickListener listener) {
        Dialog dialogDelete = new Dialog(context);
        // setting content view to dialog
        dialogDelete.setContentView(R.layout.dialog_delete);

        dialogDelete.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDelete.getWindow().setGravity(Gravity.BOTTOM);
        dialogDelete.setCanceledOnTouchOutside(true);

        // getting reference of TextView
        TextView title = dialogDelete.findViewById(R.id.textView);
        TextView secondText = dialogDelete.findViewById(R.id.textview_second);
        TextView dialogButtonYes = dialogDelete.findViewById(R.id.yes_Button);
        TextView dialogButtonNo = dialogDelete.findViewById(R.id.no_Button);

        title.setText(titleText);
        secondText.setText(secondTxt);

        if (positiveText != null) {
            dialogButtonYes.setText(positiveText);
        }
        if (negativeText != null) {
            dialogButtonNo.setText(negativeText);
        }
        // click listener for No
        dialogButtonNo.setOnClickListener(v -> {
            //dismiss the dialog
            if (listener != null) {
                listener.onNegativeButtonClick();
            }
            dialogDelete.dismiss();

        });

        // click listener for Yes
        dialogButtonYes.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPositiveButtonClick();
            }
            dialogDelete.dismiss();
        });
        dialogDelete.show();
        hideProgressDialog();
    }



}
