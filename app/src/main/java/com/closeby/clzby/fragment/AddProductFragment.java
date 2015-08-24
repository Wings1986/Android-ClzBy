package com.closeby.clzby.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontButton;
import com.closeby.clzby.customcontrol.CustomFontEdittext;
import com.closeby.clzby.customcontrol.DialogCallBack;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.closeby.clzby.model.ProductItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iGold on 6/3/15.
 */
public class AddProductFragment extends BaseFragment {

    int m_mainCategory = -1;
    JSONArray arrayMainCategory;

    ProductItem productData;


    CustomFontEdittext etCategory;
    ImageView ivPhoto;
    Bitmap bitmap;

    public static AddProductFragment newInstance(ProductItem product) {

        AddProductFragment fragment = new AddProductFragment();
        Bundle args = new Bundle();

        if (product != null) {

            args.putSerializable("product", product);

        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_add_product, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productData = (ProductItem) getArguments().getSerializable("product");


        final CustomFontEdittext etProductName = (CustomFontEdittext) view.findViewById(R.id.etProductName);
        etCategory = (CustomFontEdittext) view.findViewById(R.id.etCategory);
        final CustomFontEdittext etPrice = (CustomFontEdittext) view.findViewById(R.id.etPrice);
        final CustomFontEdittext etDescription = (CustomFontEdittext) view.findViewById(R.id.etDescription);

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

        ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openImageIntent();

            }
        });

        CustomFontButton btnUpdate = (CustomFontButton) view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnTouchListener(CustomButtonTouchListener.getInstance());
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String productName = etProductName.getText().toString();
                String price = etPrice.getText().toString();
                String productDescription = etDescription.getText().toString();


                if (productName.length() < 1) {
                    DialogHelper.showToast(getActivity(), "Please input product name");
                    return;
                }
                if (price.length() < 1) {
                    DialogHelper.showToast(getActivity(), "Please input price");
                    return;
                }
                if (productDescription.length() < 1) {
                    DialogHelper.showToast(getActivity(), "Please input product description");
                    return;
                }

                if (bitmap == null) {
                    DialogHelper.showToast(getActivity(), "Please choose image");
                    return;
                }

                if (m_mainCategory == -1) {
                    DialogHelper.showToast(getActivity(), "Please choose category");
                    return;
                }

                String categoryID = "";
                try {
                    categoryID = arrayMainCategory.getJSONObject(m_mainCategory).getString("SubCategoryID");
                } catch (Exception e) {
                    e.printStackTrace();
                }


                final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
                waitDialog.show();

                AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
                httpClient.setMaxRetries(3);

                ParameterMap params = httpClient.newParams()
                        .add("guid", Global.kGUID)
                        .add("UserID", AppData.getInstance().loadLoginUserID())
                        .add("ProductName", productName)
                        .add("ProductDescription", productDescription)
                        .add("OrigionalPrice", price)
                        .add("SelectedSubCategories", categoryID)
                        .add("ProductPhoto", Global.base64StringForImage(bitmap));

                String url = "";
                if (productData == null) {
                    url = "/AddNewProduct.aspx";
                } else {
                    url = "/EditBusinessProduct.aspx";
                    params.add("ProductID", productData.ID);
                }


                httpClient.post(url, params, new AsyncCallback() {

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

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    }
                                });
                            } else {

                                DialogHelper.showToast(getActivity(), result.getString("message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });


        if (productData == null) {
            btnUpdate.setText("Add Product");
            ((AddProductActivity) getActivity()).setTitle("Add Product");
        }
        else {
            btnUpdate.setText("Update Product");
            ((AddProductActivity) getActivity()).setTitle("Update Product");

            etProductName.setText(productData.ProductName);
            etPrice.setText("" + productData.OrigionalPrice);
            etDescription.setText(productData.ProductDescription);

            String url = Global.getURLEncoded(productData.ProductPhoto);
            ImageLoader.getInstance().displayImage(url, ivPhoto, new SimpleImageLoadingListener() {

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    // Empty implementation
                    bitmap = loadedImage;
                }

            });
        }


        getMainCategory();
    }


    void getMainCategory() {

        if (arrayMainCategory == null) {
            AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
            httpClient.setMaxRetries(3);
            ParameterMap params = httpClient.newParams()
                    .add("guid", Global.kGUID)
                    .add("UserID", AppData.getInstance().loadLoginUserID())
                    ;



            httpClient.get("/GetSubcategoriesForMaincategory.aspx", params, new AsyncCallback() {

                @Override
                public void onComplete(HttpResponse httpResponse) {

                    try {
                        JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                        if (result.getString("Success").equals("Success")) {

                            arrayMainCategory = result.getJSONArray("Data");

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub

                                    if (productData != null) {

                                        for (int index = 0 ; index < arrayMainCategory.length() ; index ++) {

                                            String subCategoryID = "";
                                            try {
                                                subCategoryID = arrayMainCategory.getJSONObject(index).getString("SubCategoryID");
                                                if (subCategoryID.equalsIgnoreCase(productData.CategoryIds)) {
                                                    m_mainCategory = index;

                                                    etCategory.setText(arrayMainCategory.getJSONObject(index).getString("SubcategoryName"));

                                                    break;
                                                }

                                            } catch (Exception e) {e.printStackTrace();}

                                        }
                                    }



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

        if (arrayMainCategory == null) {
            getMainCategory();
            return;
        }
        m_mainCategory = m_mainCategory == -1 ? 0 : m_mainCategory;


        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());


        final String[] choiceList = new String[arrayMainCategory.length()];

        for (int i = 0 ; i < arrayMainCategory.length() ; i ++) {
            try {
                JSONObject obj = arrayMainCategory.getJSONObject(i);

                choiceList[i] = obj.getString("SubcategoryName");

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


    private void openImageIntent() {
        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(
                captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName,
                    res.activityInfo.name));
            intent.setPackage(packageName);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent,
                "Please Choose");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                cameraIntents.toArray(new Parcelable[] {}));

        startActivityForResult(chooserIntent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)
            return;
        if (data == null)
            return;

        if (requestCode == 100) {

            // output image path
            Uri mImageUri = data.getData();
            Bundle extra = data.getExtras();

            if( null != extra ) {

                try {

//                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageUri);

//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inSampleSize = 4;
//
//                    AssetFileDescriptor fileDescriptor =null;
//                    fileDescriptor =
//                            getActivity().getContentResolver().openAssetFileDescriptor(mImageUri, "r");
//
//                    bitmap
//                            = BitmapFactory.decodeFileDescriptor(
//                            fileDescriptor.getFileDescriptor(), null, options);

                    bitmap = Global.scaleImage(getActivity(), mImageUri);

                    ivPhoto.setImageBitmap(bitmap);

                } catch (Exception e) {e.printStackTrace();}

            }
        }
    }


}
