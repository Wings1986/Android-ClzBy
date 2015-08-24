package com.closeby.clzby.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.closeby.clzby.AppData;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.FlashMessageActivity;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.activity.SpecialSettingActivity;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontButton;
import com.closeby.clzby.customcontrol.CustomFontEdittext;
import com.closeby.clzby.customcontrol.DialogCallBack;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.closeby.clzby.model.BeaconItem;
import com.closeby.clzby.model.ProductItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iGold on 6/3/15.
 */
public class FlashMessageFragment extends BaseFragment {

    BeaconItem beaconData;

    ImageView ivPhoto;
    Bitmap bitmap;

    public static FlashMessageFragment newInstance(BeaconItem beacon) {

        FlashMessageFragment fragment = new FlashMessageFragment();
        Bundle args = new Bundle();

        if (beacon != null) {

            args.putSerializable("beacon", beacon);

        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_flash_msg, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        beaconData = (BeaconItem) getArguments().getSerializable("beacon");


        final CustomFontEdittext etMainTitle = (CustomFontEdittext) view.findViewById(R.id.etMainTitle);
        final CustomFontEdittext etTagline = (CustomFontEdittext) view.findViewById(R.id.etTagLine);
        final CustomFontEdittext etPrice = (CustomFontEdittext) view.findViewById(R.id.etPrice);

        ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openImageIntent();

            }
        });

        final CustomFontButton btnUpdate = (CustomFontButton) view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnTouchListener(CustomButtonTouchListener.getInstance());
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mainTitle = etMainTitle.getText().toString();
                String tagline = etTagline.getText().toString();
                String price = etPrice.getText().toString();

                if (mainTitle.length() < 1) {
                    DialogHelper.showToast(getActivity(), "Please input product quantity");
                    return;
                }
                if (tagline.length() < 1) {
                    DialogHelper.showToast(getActivity(), "Please input tag line");
                    return;
                }
                if (price.length() < 1) {
                    DialogHelper.showToast(getActivity(), "Please input price");
                    return;
                }


                bitmap = ((BitmapDrawable)ivPhoto.getDrawable()).getBitmap();
                if (bitmap == null) {
                    DialogHelper.showToast(getActivity(), "Please choose image");
                    return;
                }


                final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
                waitDialog.show();

                AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
                httpClient.setMaxRetries(3);

                ParameterMap params = httpClient.newParams()
                        .add("guid", Global.kGUID)
                        .add("UserID", AppData.getInstance().loadLoginUserID())
                        .add("BeaconUUID", beaconData.beaconUUID)
                        .add("Major", beaconData.beaconMajor)
                        .add("Minor", beaconData.beaconMinor)
                        .add("BeaconTypeID", "" + 2)
                        .add("MainTitle", mainTitle)
                        .add("TagLine", tagline)
                        .add("Description", price)
                        .add("FlashImage", Global.base64StringForImage(bitmap))
                        ;


                httpClient.post("/UpdateBusinessBeaconFlashMessage.aspx", params, new AsyncCallback() {

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


        if (beaconData != null) {

            if (beaconData.MainTitle.length() < 1) {
                ((FlashMessageActivity) getActivity()).setTitle("Add Flash Message");
                btnUpdate.setText("Add Beacon Message");

                // request apis

                final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
                waitDialog.show();

                AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
                httpClient.setMaxRetries(3);

                ParameterMap params = httpClient.newParams()
                        .add("guid", Global.kGUID)
                        .add("BeaconUUID", beaconData.beaconUUID)
                        .add("Major", beaconData.beaconMajor)
                        .add("Minor", beaconData.beaconMinor)
                        ;


                httpClient.get("/GetBusinessBeaconData.aspx", params, new AsyncCallback() {

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

                                        ((FlashMessageActivity) getActivity()).setTitle("Update Flash Message");
                                        btnUpdate.setText("Update Beacon Message");

                                        try {

                                            JSONObject data = result.getJSONObject("Data");

                                            etMainTitle.setText(data.getString("MainTitle"));
                                            etTagline.setText(data.getString("TagLine"));
                                            etPrice.setText(data.getString("Description"));


                                            String url = Global.getURLEncoded(data.getString("FlashImage"));
                                            ImageLoader.getInstance().displayImage(url, ivPhoto, new SimpleImageLoadingListener() {
                                                @Override
                                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                    // Empty implementation
                                                    bitmap = loadedImage;
                                                }
                                            });


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
            else {
                ((FlashMessageActivity) getActivity()).setTitle("Update Flash Message");
                btnUpdate.setText("Update Beacon Message");

                etMainTitle.setText(beaconData.MainTitle);
                etTagline.setText(beaconData.TagLine);
                etPrice.setText(beaconData.Description);

                String url = Global.getURLEncoded(beaconData.FlashImage);
                ImageLoader.getInstance().displayImage(url, ivPhoto, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // Empty implementation
                        bitmap = loadedImage;
                    }
                });
            }
        }


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

                    bitmap = Global.scaleImage(getActivity(), mImageUri);

                    ivPhoto.setImageBitmap(bitmap);

                } catch (Exception e) {e.printStackTrace();}

            }
        }
    }

}
