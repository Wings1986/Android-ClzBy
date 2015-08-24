package com.closeby.clzby.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.closeby.clzby.AppData;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.activity.SpecialSettingActivity;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontButton;
import com.closeby.clzby.customcontrol.CustomFontEdittext;
import com.closeby.clzby.customcontrol.DialogCallBack;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.closeby.clzby.model.ProductItem;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iGold on 6/3/15.
 */
public class SpecialDealFragment extends BaseFragment {

    int m_mainCategory = -1;
    JSONArray arrayTime;

    ProductItem productData;


    CustomFontEdittext etCategory;



    public static SpecialDealFragment newInstance(ProductItem product) {

        SpecialDealFragment fragment = new SpecialDealFragment();
        Bundle args = new Bundle();

        if (product != null) {

            args.putSerializable("product", product);

        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_special_deal, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productData = (ProductItem) getArguments().getSerializable("product");


        final CustomFontEdittext etTagline = (CustomFontEdittext) view.findViewById(R.id.etTagLine);
        final CustomFontEdittext etQuantity = (CustomFontEdittext) view.findViewById(R.id.etQuantity);
        final CustomFontEdittext etPrice = (CustomFontEdittext) view.findViewById(R.id.etPrice);

        etCategory = (CustomFontEdittext) view.findViewById(R.id.etDealTime);
        etCategory.setFocusableInTouchMode(false);
        etCategory.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    openMainCategoryDlg();
                }

                return false;
            }
        });


        CustomFontButton btnUpdate = (CustomFontButton) view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnTouchListener(CustomButtonTouchListener.getInstance());
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tagline = etTagline.getText().toString();
                String price = etPrice.getText().toString();
                String quantity = etQuantity.getText().toString();


                if (tagline.length() < 1) {
                    DialogHelper.showToast(getActivity(), "Please input tag line");
                    return;
                }
                if (price.length() < 1) {
                    DialogHelper.showToast(getActivity(), "Please input price");
                    return;
                }
                if (quantity.length() < 1) {
                    DialogHelper.showToast(getActivity(), "Please input product quantity");
                    return;
                }


                if (m_mainCategory == -1) {
                    DialogHelper.showToast(getActivity(), "Please choose deal duration");
                    return;
                }

                int duration = 0;
                try {
                    duration = arrayTime.getJSONObject(m_mainCategory).getInt("Timeslot");
                } catch (Exception e) {e.printStackTrace();}


                final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
                waitDialog.show();

                AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
                httpClient.setMaxRetries(3);

                ParameterMap params = httpClient.newParams()
                        .add("guid", Global.kGUID)
                        .add("UserID", AppData.getInstance().loadLoginUserID())
                        .add("ProductID", productData.ID)
                        .add("SpecialTagLine", tagline)
                        .add("Quantity", quantity)
                        .add("SpecialPrice", price)
                        .add("Duration", "" + duration)
                ;


                httpClient.get("/UpdateProductToTimeDecayingDeal.aspx", params, new AsyncCallback() {

                    @Override
                    public void onComplete(HttpResponse httpResponse) {

                        waitDialog.dismiss();


                        try {
                            final JSONObject result = new JSONObject(httpResponse.getBodyAsString());


                            if (result.getString("Success").equals("Success")) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub

                                        try {

                                            DialogHelper.getDialog(getActivity(), "Success", result.getString("Message"), "OK", null, new DialogCallBack() {
                                                @Override
                                                public void onClick(int which) {

                                                    getActivity().finish();

                                                }
                                            }).show();

                                        } catch (Exception e) {e.printStackTrace();}

                                    }
                                });
                            } else {

                                DialogHelper.showToast(getActivity(), result.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });


        ((SpecialSettingActivity) getActivity()).setTitle("Time Based Deal");

        if (productData != null) {

            etTagline.setText(productData.DiscountedTagLing);
            etPrice.setText("" + productData.SpecialPrice);
            etQuantity.setText("" + productData.QuantityRemaining);

        }


        getTimes();
    }


    void getTimes() {

        if (arrayTime == null) {
            AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
            httpClient.setMaxRetries(3);
            ParameterMap params = httpClient.newParams()
                    .add("guid", Global.kGUID)
                    ;



            httpClient.get("/GetTimeDurationList.aspx", params, new AsyncCallback() {

                @Override
                public void onComplete(HttpResponse httpResponse) {

                    try {
                        JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                        if (result.getString("Success").equals("Success")) {

                            arrayTime = result.getJSONArray("Data");

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub

                                    if (productData != null) {

                                        for (int index = 0 ; index < arrayTime.length() ; index ++) {

                                            int subCategoryID = 0;
                                            try {
                                                subCategoryID = arrayTime.getJSONObject(index).getInt("Timeslot");
                                                if (subCategoryID == productData.DecayDuration) {
                                                    m_mainCategory = index;
                                                    break;
                                                }
                                            } catch (Exception e) {e.printStackTrace();}

                                        }

                                    }

                                    m_mainCategory = m_mainCategory == -1 ? 0 : m_mainCategory;

                                    try {
                                        etCategory.setText(arrayTime.getJSONObject(m_mainCategory).getString("Text"));
                                    } catch (Exception e) {e.printStackTrace();}

                                }
                            });
                        } else {

                            DialogHelper.showToast(getActivity(), result.getString("Message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }


    }

    private void openMainCategoryDlg() {

        if (arrayTime == null)
            return;
        m_mainCategory = m_mainCategory == -1 ? 0 : m_mainCategory;


        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());


        final String[] choiceList = new String[arrayTime.length()];

        for (int i = 0 ; i < arrayTime.length() ; i ++) {
            try {
                JSONObject obj = arrayTime.getJSONObject(i);

                choiceList[i] = obj.getString("Text");

            } catch (Exception e) {e.printStackTrace();}
        }



        builder.setSingleChoiceItems(
                choiceList,
                m_mainCategory,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {
                        //set to buffKey instead of selected
                        //(when cancel not save to selected)
                        m_mainCategory = which;
                    }
                })
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

//                                m_mainCategory = which;

                                etCategory.setText(choiceList[m_mainCategory]);

                            }
                        }
                )
                .setNegativeButton("Cancel", null);

        AlertDialog alert = builder.create();
        alert.show();

    }


}
