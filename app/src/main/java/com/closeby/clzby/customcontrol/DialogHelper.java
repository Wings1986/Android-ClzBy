package com.closeby.clzby.customcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.closeby.clzby.R;


/**
 * Created by iGold on 6/2/15.
 */

public class DialogHelper {

    public static Dialog getDialog(Context context, String title, String content,
                                               String firstText, String secondText,
                                               final DialogCallBack callback) {
        if (context != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(content);
            builder.setCancelable(true);
            builder.setNegativeButton(firstText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();

                    if (callback != null) {
                        callback.onClick(0);
                    }
                }
            });

            if (secondText != null) {
                builder.setPositiveButton(secondText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (callback != null) {
                            callback.onClick(1);
                        }
                    }
                });
            }

            AlertDialog alertDialog = builder.create();
            alertDialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
            return alertDialog;
        }
        return null;
    }

    public static void showToast (Context context, String title) {
        if (context != null) {
            Toast.makeText(context, title, Toast.LENGTH_LONG).show();
        }

    }


    public static ProgressDialog getProgressDialog(Context context){

        ProgressDialog waitDialog = new ProgressDialog(context);
        waitDialog.setMessage("Loading...");
        waitDialog.setCancelable(false);
        return waitDialog;
    }

    public static Dialog getWaitDialog(Activity context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.wait_dialog_layout, null));
        builder.setCancelable(false);
        return builder.create();
    }

    // custome 3 buttons

    public static Dialog getSpecialDialog(Activity context, final DialogCallBack callback) {
        if (context != null) {
            final Dialog dialog = new Dialog(context);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            LayoutInflater inflater = context.getLayoutInflater();

            View view = inflater.inflate(R.layout.dialog_special_setting_layout, null);

            CustomFontButton btnStandard = (CustomFontButton) view.findViewById(R.id.btnStandard);
            btnStandard.setOnTouchListener(CustomButtonTouchListener.getInstance());
            btnStandard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();

                    if (callback != null) {
                        callback.onClick(0);
                    }
                }
            });

            CustomFontButton btnSpecial = (CustomFontButton) view.findViewById(R.id.btnSpecial);
            btnSpecial.setOnTouchListener(CustomButtonTouchListener.getInstance());
            btnSpecial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();

                    if (callback != null) {
                        callback.onClick(1);
                    }
                }
            });

            CustomFontButton btnCancel = (CustomFontButton) view.findViewById(R.id.btnCancel);
            btnCancel.setOnTouchListener(CustomButtonTouchListener.getInstance());
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();

                    if (callback != null) {
                        callback.onClick(2);
                    }
                }
            });

            dialog.setContentView(view);
            dialog.setCancelable(true);

            return dialog;
        }

        return null;

    }


    static int nCurrentType = 0;

    public static Dialog getBeaconTypeDialog(Activity context, int nType, final DialogCallBack callback) {
        if (context != null) {
            final Dialog dialog = new Dialog(context);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            LayoutInflater inflater = context.getLayoutInflater();

            View view = inflater.inflate(R.layout.dialog_beacon_type_layout, null);


            final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radiogroup);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {

                    switch (i) {
                        case R.id.radioFlash:
                            nCurrentType = 2;
                            break;
                        case R.id.radioBusiness:
                            nCurrentType = 1;
                            break;
                    }
                }
            });

            nCurrentType = nType;
            int resID = 0;
            switch (nCurrentType) {
                case 2:
                    resID = R.id.radioFlash; break;
                case 1:
                default:
                    resID = R.id.radioBusiness; break;
            }
            RadioButton radioButton = (RadioButton) view.findViewById(resID);
            radioButton.setChecked(true);


            CustomFontButton btnSave = (CustomFontButton) view.findViewById(R.id.btnSave);
            btnSave.setOnTouchListener(CustomButtonTouchListener.getInstance());
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();

                    if (callback != null) {
                        callback.onClick(nCurrentType);
                    }
                }
            });

            CustomFontButton btnCancel = (CustomFontButton) view.findViewById(R.id.btnCancel);
            btnCancel.setOnTouchListener(CustomButtonTouchListener.getInstance());
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();

                }
            });

            dialog.setContentView(view);
            dialog.setCancelable(true);

            return dialog;
        }

        return null;

    }




    class CustomClickListener implements View.OnClickListener {

        AlertDialog dialog;
        int index = 0;
        DialogCallBack callback;

        CustomClickListener(AlertDialog dlg, int i, DialogCallBack callback) {
            this.dialog = dlg;
            this.index = i;
            this.callback = callback;
        }

        @Override
        public void onClick(View view) {

            if (dialog != null) {
                dialog.dismiss();
            }

            if (callback != null) {
                callback.onClick(index);
            }
        }
    }
}
