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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.closeby.clzby.AppData;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.AddProductActivity;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.activity.SpecialSettingActivity;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontButton;
import com.closeby.clzby.customcontrol.CustomFontEdittext;
import com.closeby.clzby.customcontrol.DialogCallBack;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.closeby.clzby.model.ProductItem;
import com.nostra13.universalimageloader.core.ImageLoader;
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
public class StandardDealFragment extends BaseFragment {

    ProductItem productData;


    public static StandardDealFragment newInstance(ProductItem product) {

        StandardDealFragment fragment = new StandardDealFragment();
        Bundle args = new Bundle();

        if (product != null) {

            args.putSerializable("product", product);

        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_standard_deal, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productData = (ProductItem) getArguments().getSerializable("product");


        final CustomFontEdittext etTagLine = (CustomFontEdittext) view.findViewById(R.id.etProductName);
        final CustomFontEdittext etQuantity = (CustomFontEdittext) view.findViewById(R.id.etCategory);

        CustomFontButton btnUpdate = (CustomFontButton) view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnTouchListener(CustomButtonTouchListener.getInstance());
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tagline = etTagLine.getText().toString();
                String quantity = etQuantity.getText().toString();

                if (tagline.length() < 1) {
                    DialogHelper.showToast(getActivity(), "Please input tag line");
                    return;
                }
                if (quantity.length() < 1) {
                    DialogHelper.showToast(getActivity(), "Please input product quantity");
                    return;
                }


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
                ;

                httpClient.get("/UpdateProductToDeal.aspx", params, new AsyncCallback() {

                    @Override
                    public void onComplete(HttpResponse httpResponse) {

                        waitDialog.dismiss();


                        try {
                            final JSONObject result = new JSONObject(httpResponse.getBodyAsString());


                            if (!result.getString("Success").equalsIgnoreCase("FAIL")) {

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


        ((SpecialSettingActivity) getActivity()).setTitle("Standard Deal");

        if (productData != null) {

            etTagLine.setText(productData.DiscountedTagLing);
            etQuantity.setText("" + productData.QuantityRemaining);
        }

    }

}
