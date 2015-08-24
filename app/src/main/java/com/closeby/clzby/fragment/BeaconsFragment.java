package com.closeby.clzby.fragment;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.closeby.clzby.AppData;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.FlashMessageActivity;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.activity.HomeActivity;
import com.closeby.clzby.customcontrol.CustomFontButton;
import com.closeby.clzby.customcontrol.CustomFontTextView;
import com.closeby.clzby.customcontrol.DialogCallBack;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.closeby.clzby.listener.ImageViewLayoutSize;
import com.closeby.clzby.listener.OnLayoutSizeListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import it.sephiroth.android.library.widget.HListView;

/**
 * Created by iGold on 6/3/15.
 */
public class BeaconsFragment extends BaseFragment {

    ListView mListView;
    MyCustomAdapter mAdapter;

    JSONArray arrayDataSource = new JSONArray();

    int imageViewWidth = 100;


    public static BeaconsFragment newInstance() {
        BeaconsFragment fragment = new BeaconsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_beacons, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    private void initView(View view) {

        mListView = (ListView) view.findViewById(R.id.listViw);
        mAdapter = new MyCustomAdapter();
        mListView.setAdapter(mAdapter);



        // network

        final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
        waitDialog.show();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);
        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("UserID", AppData.getInstance().loadLoginUserID());

        httpClient.get("/GetBusinessBeacons.aspx", params, new AsyncCallback() {

            @Override
            public void onComplete(HttpResponse httpResponse) {

                waitDialog.dismiss();

                try {
                    JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                    if (result.getString("Success").equals("Success")) {

                        arrayDataSource = result.getJSONArray("Data");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                mAdapter.setItem(arrayDataSource);
                                mAdapter.notifyDataSetChanged();
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


    public class MyCustomAdapter extends BaseAdapter {

        private static final int TYPE_FLASH = 0;
        private static final int TYPE_PROFILE = 1;
        private static final int TYPE_MAX_COUNT = TYPE_PROFILE + 1;

        private JSONArray items = new JSONArray();
        private LayoutInflater mInflater;

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        public void setItem(JSONArray array) {
            items = array;
        }

        @Override
        public int getViewTypeCount() {
            // Get the number of items in the enum
            return TYPE_MAX_COUNT;

        }

        @Override
        public int getItemViewType(int position) {

            try {
                JSONObject item = getItem(position);
                if (item != null) {

                    int beaconTypeID = item.getInt("BeaconTypeID");

                    if (beaconTypeID == 1) {
                        return TYPE_PROFILE;
                    }
                }
            }catch (Exception e) {e.printStackTrace();}

            return TYPE_FLASH;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return items.length();
        }

        @Override
        public JSONObject getItem(int position) {
            try {
                return items.getJSONObject(position);
            } catch (Exception e) { e.printStackTrace(); }

            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            int type = getItemViewType(position);

            if (convertView == null) {

                switch (type) {
                    case TYPE_FLASH: {
                        holder = new ViewHolderFlash();

                        convertView = mInflater.inflate(R.layout.grid_item_beacons_flash, null);

                        ((ViewHolderFlash)holder).lbBeaconTitle = (CustomFontTextView) convertView.findViewById(R.id.tvBeaconTitle);
                        ((ViewHolderFlash)holder).lbBeaconTagLine = (CustomFontTextView) convertView.findViewById(R.id.tvBeaconTagLine);
                        ((ViewHolderFlash)holder).lbBeaconDescription = (CustomFontTextView) convertView.findViewById(R.id.tvBeaconDescription);
                        ((ViewHolderFlash)holder).ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
                        ((ViewHolderFlash)holder).btnEditMessage = (Button) convertView.findViewById(R.id.btnEditMessage);


                        ((ViewHolderFlash)holder).chEnable = (CheckBox) convertView.findViewById(R.id.ckbEnable);
                        ((ViewHolderFlash)holder).btnTypeEdit = (ImageView) convertView.findViewById(R.id.btnEditType);
                        ((ViewHolderFlash)holder).btnBeaconTest = (CustomFontButton) convertView.findViewById(R.id.btnBeaconTest);



                        break;
                    }
                    case TYPE_PROFILE: {

                        holder = new ViewHolderProfile();

                        convertView = mInflater.inflate(R.layout.grid_item_beacons_profile, null);

                        ((ViewHolderProfile)holder).ivAvatar = (ImageView) convertView.findViewById(R.id.ivAvatar);
                        ((ViewHolderProfile)holder).lbBusinessName = (CustomFontTextView) convertView.findViewById(R.id.tvBusinessName);
                        ((ViewHolderProfile)holder).lbBusinessContactNumber = (CustomFontTextView) convertView.findViewById(R.id.tvBusinessPhone);
                        ((ViewHolderProfile)holder).lbBusinessBusinessAddress = (CustomFontTextView) convertView.findViewById(R.id.tvBusinessAddress);
                        ((ViewHolderProfile)holder).lbMainCategoryName = (CustomFontTextView) convertView.findViewById(R.id.tvBusinessCategory);

                        ((ViewHolderProfile)holder).viewCheckIn = (LinearLayout) convertView.findViewById(R.id.viewCheckIn);
                        ((ViewHolderProfile)holder).lbLikes= (CustomFontTextView) convertView.findViewById(R.id.tvLike);
                        ((ViewHolderProfile)holder).ivLikes = (ImageView) convertView.findViewById(R.id.ivLike);

                        ((ViewHolderProfile)holder).lbStartTimeMon = (CustomFontTextView) convertView.findViewById(R.id.tvStartTimeMon);
                        ((ViewHolderProfile)holder).lbEndTimeMon = (CustomFontTextView) convertView.findViewById(R.id.tvEndTimeMon);
                        ((ViewHolderProfile)holder).lbStartTimeTue = (CustomFontTextView) convertView.findViewById(R.id.tvStartTimeTue);
                        ((ViewHolderProfile)holder).lbEndTimeTue = (CustomFontTextView) convertView.findViewById(R.id.tvEndTimeTue);
                        ((ViewHolderProfile)holder).lbStartTimeWed = (CustomFontTextView) convertView.findViewById(R.id.tvStartTimeWed);
                        ((ViewHolderProfile)holder).lbEndTimeWed = (CustomFontTextView) convertView.findViewById(R.id.tvEndTimeWed);
                        ((ViewHolderProfile)holder).lbStartTimeThu = (CustomFontTextView) convertView.findViewById(R.id.tvStartTimeThu);
                        ((ViewHolderProfile)holder).lbEndTimeThu = (CustomFontTextView) convertView.findViewById(R.id.tvEndTimeThu);
                        ((ViewHolderProfile)holder).lbStartTimeFri = (CustomFontTextView) convertView.findViewById(R.id.tvStartTimeFri);
                        ((ViewHolderProfile)holder).lbEndTimeFri = (CustomFontTextView) convertView.findViewById(R.id.tvEndTimeFri);
                        ((ViewHolderProfile)holder).lbStartTimeSat = (CustomFontTextView) convertView.findViewById(R.id.tvStartTimeSat);
                        ((ViewHolderProfile)holder).lbEndTimeSat = (CustomFontTextView) convertView.findViewById(R.id.tvEndTimeSat);
                        ((ViewHolderProfile)holder).lbStartTimeSun = (CustomFontTextView) convertView.findViewById(R.id.tvStartTimeSun);
                        ((ViewHolderProfile)holder).lbEndTimeSun = (CustomFontTextView) convertView.findViewById(R.id.tvEndTimeSun);

                        ((ViewHolderProfile)holder).subDealLayout = (LinearLayout) convertView.findViewById(R.id.subDealList);

                        ((ViewHolderProfile)holder).ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
                        ((ViewHolderProfile)holder).lbDiscountTagLine = (CustomFontTextView) convertView.findViewById(R.id.tvDiscountTagLine);
                        ((ViewHolderProfile)holder).lbDealName = (CustomFontTextView) convertView.findViewById(R.id.tvDealName);
                        ((ViewHolderProfile)holder).lbProductDescription = (CustomFontTextView) convertView.findViewById(R.id.tvDealDescription);

                        ((ViewHolderProfile)holder).lbOriginPrice = (TextView) convertView.findViewById(R.id.tvOriginPrice);
                        ((ViewHolderProfile)holder).subDiscayPriceView = (LinearLayout) convertView.findViewById(R.id.specialOfferView);
                        ((ViewHolderProfile)holder).lbDiscayOriginPrice = (TextView) convertView.findViewById(R.id.tvDelayOrginPrice);
                        ((ViewHolderProfile)holder).lbDiscaySpecialPrice = (TextView) convertView.findViewById(R.id.tvDelaySpecialPrice);
                        ((ViewHolderProfile)holder).subDiscayRemainingView = (LinearLayout) convertView.findViewById(R.id.decayDurationView);
                        ((ViewHolderProfile)holder).lbDiscayRemaining = (TextView) convertView.findViewById(R.id.tvDiscayRemaining);


                        ((ViewHolderProfile)holder).chEnable = (CheckBox) convertView.findViewById(R.id.ckbEnable);
                        ((ViewHolderProfile)holder).btnTypeEdit = (ImageView) convertView.findViewById(R.id.btnEditType);
                        ((ViewHolderProfile)holder).btnBeaconTest = (CustomFontButton) convertView.findViewById(R.id.btnBeaconTest);


                        ((ViewHolderProfile)holder).mCellListView = (HListView) convertView.findViewById(R.id.hListView);
                        ((ViewHolderProfile)holder).myCellAdapter = new MyCellAdapter();
                        ((ViewHolderProfile)holder).mCellListView.setAdapter(((ViewHolderProfile)holder).myCellAdapter);

                        break;


                    }
                }

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder)convertView.getTag();
            }



            try {
                final JSONObject item = getItem(position);

                switch (type) {
                    case TYPE_FLASH: {

                        try {
                            final JSONObject beaconData = item.getJSONObject("BeaconData");

                            ((ViewHolderFlash) holder).btnEditMessage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    ((HomeActivity) getActivity()).gotoFlashMessage(item);
                                }
                            });

                            ((ViewHolderFlash) holder).lbBeaconTitle.setText(beaconData.getString("MainTitle"));
                            ((ViewHolderFlash) holder).lbBeaconTagLine.setText(beaconData.getString("TagLine"));
                            ((ViewHolderFlash) holder).lbBeaconDescription.setText(beaconData.getString("Description"));

                            try {

                                ((ViewHolderFlash) holder).ivPhoto.getViewTreeObserver().addOnGlobalLayoutListener(new ImageViewLayoutSize(((ViewHolderFlash) holder).ivPhoto, new OnLayoutSizeListener() {
                                    @Override
                                    public void onGetSize(int width, int height) {
                                        imageViewWidth = width;
                                    }
                                }));


                                String url = Global.getURLEncoded(beaconData.getString("ThumbImage"));
                                ImageLoader.getInstance().displayImage(url, ((ViewHolderFlash) holder).ivPhoto, new ImageLoadingListener() {
                                    @Override
                                    public void onLoadingStarted(String s, View view) {

                                    }

                                    @Override
                                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                                    }

                                    @Override
                                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                        ImageView imageView = (ImageView) view;

                                        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                                        int ivWidth = bitmap.getWidth();
                                        int ivHeight = bitmap.getHeight();
                                        int virturlHeight = 200;
                                        if (ivWidth != 0 && ivHeight != 0)
                                            virturlHeight= imageViewWidth * ivHeight / ivWidth;
                                        param.height = virturlHeight;
                                        imageView.setLayoutParams(param);

                                        Log.d("InventoryFragment", "width = " + param.width + " height = " + param.height);

                                        imageView.setImageBitmap(bitmap);
                                    }

                                    @Override
                                    public void onLoadingCancelled(String s, View view) {

                                    }
                                });

                            } catch (Exception e) {e.printStackTrace();}



                        } catch (Exception e) {e.printStackTrace();}


                    }
                    break;

                    case TYPE_PROFILE: {

                        try {

                            JSONObject dic = item.getJSONObject("BeaconData");


                            try {
                                String businessLogo = dic.getString("BusinessLogoSmall");
                                if (businessLogo != null && businessLogo.length() > 0) {
                                    ImageLoader.getInstance().displayImage(businessLogo, ((ViewHolderProfile) holder).ivAvatar);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                ((ViewHolderProfile) holder).lbBusinessName.setText(dic.getString("BusinessName"));
                                ((ViewHolderProfile) holder).lbBusinessContactNumber.setText(dic.getString("BusinessContactNumber"));
                                ((ViewHolderProfile) holder).lbBusinessBusinessAddress.setText(dic.getString("BusinessAddress"));
                                ((ViewHolderProfile) holder).lbMainCategoryName.setText("");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            try {
                                ((ViewHolderProfile) holder).lbLikes.setText(dic.getString("NumberOfLikes"));

                                String currentUserHasLiked = dic.getString("CurrentUserHasLiked");
                                if (currentUserHasLiked.equalsIgnoreCase("yes")) {
                                    ((ViewHolderProfile) holder).ivLikes.setImageResource(R.drawable.like_button_tapped);
                                } else {
                                    ((ViewHolderProfile) holder).ivLikes.setImageResource(R.drawable.like_button);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {

                                JSONArray openingHours = dic.getJSONArray("OpeningHours");

                                for (int i = 0; i < openingHours.length(); i++) {

                                    try {
                                        JSONObject openHour = openingHours.getJSONObject(i);


                                        String startTime = openHour.getString("StartTime");
                                        String endTime = openHour.getString("EndTime");

                                        if (startTime == null) {
                                            startTime = "00:00";
                                        }

                                        if (endTime == null) {
                                            endTime = "00:00";
                                        }

                                        switch (i) {
                                            case 0: {
                                                if (startTime.equalsIgnoreCase("00:00")) {
                                                    ((ViewHolderProfile) holder).lbStartTimeMon.setText("Closed");
                                                    ((ViewHolderProfile) holder).lbEndTimeMon.setVisibility(View.INVISIBLE);
                                                } else {
                                                    ((ViewHolderProfile) holder).lbStartTimeMon.setText(startTime);
                                                    ((ViewHolderProfile) holder).lbEndTimeMon.setText(endTime);
                                                }
                                                break;
                                            }
                                            case 1: {
                                                if (startTime.equalsIgnoreCase("00:00")) {
                                                    ((ViewHolderProfile) holder).lbStartTimeTue.setText("Closed");
                                                    ((ViewHolderProfile) holder).lbEndTimeTue.setVisibility(View.INVISIBLE);
                                                } else {
                                                    ((ViewHolderProfile) holder).lbStartTimeTue.setText(startTime);
                                                    ((ViewHolderProfile) holder).lbEndTimeTue.setText(endTime);
                                                }
                                                break;
                                            }
                                            case 2: {
                                                if (startTime.equalsIgnoreCase("00:00")) {
                                                    ((ViewHolderProfile) holder).lbStartTimeWed.setText("Closed");
                                                    ((ViewHolderProfile) holder).lbEndTimeWed.setVisibility(View.INVISIBLE);
                                                } else {
                                                    ((ViewHolderProfile) holder).lbStartTimeWed.setText(startTime);
                                                    ((ViewHolderProfile) holder).lbEndTimeWed.setText(endTime);
                                                }
                                                break;
                                            }
                                            case 3: {
                                                if (startTime.equalsIgnoreCase("00:00")) {
                                                    ((ViewHolderProfile) holder).lbStartTimeThu.setText("Closed");
                                                    ((ViewHolderProfile) holder).lbEndTimeThu.setVisibility(View.INVISIBLE);
                                                } else {
                                                    ((ViewHolderProfile) holder).lbStartTimeThu.setText(startTime);
                                                    ((ViewHolderProfile) holder).lbEndTimeThu.setText(endTime);
                                                }
                                                break;
                                            }
                                            case 4: {
                                                if (startTime.equalsIgnoreCase("00:00")) {
                                                    ((ViewHolderProfile) holder).lbStartTimeFri.setText("Closed");
                                                    ((ViewHolderProfile) holder).lbEndTimeFri.setVisibility(View.INVISIBLE);
                                                } else {
                                                    ((ViewHolderProfile) holder).lbStartTimeFri.setText(startTime);
                                                    ((ViewHolderProfile) holder).lbEndTimeFri.setText(endTime);
                                                }
                                                break;
                                            }
                                            case 5: {
                                                if (startTime.equalsIgnoreCase("00:00")) {
                                                    ((ViewHolderProfile) holder).lbStartTimeSat.setText("Closed");
                                                    ((ViewHolderProfile) holder).lbEndTimeSat.setVisibility(View.INVISIBLE);
                                                } else {
                                                    ((ViewHolderProfile) holder).lbStartTimeSat.setText(startTime);
                                                    ((ViewHolderProfile) holder).lbEndTimeSat.setText(endTime);
                                                }
                                                break;
                                            }
                                            case 6: {
                                                if (startTime.equalsIgnoreCase("00:00")) {
                                                    ((ViewHolderProfile) holder).lbStartTimeSun.setText("Closed");
                                                    ((ViewHolderProfile) holder).lbEndTimeSun.setVisibility(View.INVISIBLE);
                                                } else {
                                                    ((ViewHolderProfile) holder).lbStartTimeSun.setText(startTime);
                                                    ((ViewHolderProfile) holder).lbEndTimeSun.setText(endTime);
                                                }
                                                break;
                                            }

                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            //
                            try {
                                ((ViewHolderProfile) holder).businessDeals = dic.getJSONArray("BusinessDeals");

                                ((ViewHolderProfile) holder).myCellAdapter.setItem(((ViewHolderProfile) holder).businessDeals);
                                ((ViewHolderProfile) holder).myCellAdapter.notifyDataSetChanged();

                                if (((ViewHolderProfile) holder).businessDeals.length() > 0) {
                                    ((ViewHolderProfile) holder).subDealLayout.setVisibility(View.VISIBLE);
                                    ((ViewHolderProfile) holder).showDealDetail(getActivity(), 0);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) {e.printStackTrace();}



                    }
                    break;

                }

                // global

                holder.chEnable.setChecked(item.getBoolean("Enabled"));
                holder.chEnable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            final boolean oriChecked = item.getBoolean("Enabled");
                            String UUID = item.getString("BeaconUUID");
                            String major = item.getString("Major");
                            String minor = item.getString("Minor");


                            AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
                            httpClient.setMaxRetries(3);
                            ParameterMap params = httpClient.newParams()
                                    .add("guid", Global.kGUID)
                                    .add("Userid", AppData.getInstance().loadLoginUserID())
                                    .add("beaconid", UUID)
                                    .add("Major", major)
                                    .add("Minor", minor)
                                    .add("status", "" + !oriChecked)
                                    ;

                            httpClient.get("/EnableDisableNotification.aspx", params, new AsyncCallback() {

                                @Override
                                public void onComplete(HttpResponse httpResponse) {


                                    try {
                                        JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                                        if (result.getString("Success").equals("Success")) {

                                            JSONObject obj = item;
                                            obj.put("Enabled", !oriChecked);

                                            items.put(position, obj);

                                            notifyDataSetChanged();

                                            DialogHelper.showToast(getActivity(), !oriChecked == true ? "Beacon is enabled" : "Beacon is disabled");

                                        } else {

                                            DialogHelper.showToast(getActivity(), result.getString("message"));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                        } catch (Exception e) {e.printStackTrace();}


                    }
                });


                holder.btnBeaconTest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ((HomeActivity) getActivity()).testBeacon(item);

                        DialogHelper.getDialog(getActivity(), "TESTING", "Please lock your screen and walk far away from the Beacon (~40ft) then walk back to it", "OK", null, null).show();
                    }
                });

                holder.btnTypeEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int nBeaconType = 2 ;

                        try {
                            nBeaconType = item.getInt("BeaconTypeID");
                        } catch (Exception e) {e.printStackTrace();}


                        DialogHelper.getBeaconTypeDialog(getActivity(), nBeaconType, new DialogCallBack() {
                            @Override
                            public void onClick(int which) {

                                final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
                                waitDialog.show();

                                String UUID = "", major = "", minor = "";
                                try {
                                    UUID = item.getString("BeaconUUID");
                                    major = item.getString("Major");
                                    minor = item.getString("Minor");
                                } catch (Exception e) {e.printStackTrace();}

                                AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
                                httpClient.setMaxRetries(3);
                                ParameterMap params = httpClient.newParams()
                                        .add("guid", Global.kGUID)
                                        .add("userid", AppData.getInstance().loadLoginUserID())
                                        .add("BeaconUUID", UUID)
                                        .add("Major", major)
                                        .add("Minor", minor)
                                        .add("BeaconTypeID", "" + which)
                                        ;

                                httpClient.get("/UpdateBeaconType.aspx", params, new AsyncCallback() {

                                    @Override
                                    public void onComplete(HttpResponse httpResponse) {

                                        waitDialog.dismiss();

                                        try {
                                            JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                                            if (result.getString("Success").equals("Success")) {

                                                arrayDataSource = result.getJSONArray("Data");

                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // TODO Auto-generated method stub

                                                        mAdapter.setItem(arrayDataSource);
                                                        mAdapter.notifyDataSetChanged();
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
                        }).show();

                    }
                });





            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

    }

    public static class ViewHolder {

        CheckBox chEnable;
        ImageView btnTypeEdit;
        CustomFontButton btnBeaconTest;

    }

    public static class ViewHolderFlash extends ViewHolder {

        CustomFontTextView lbBeaconTitle;
        CustomFontTextView lbBeaconTagLine;
        CustomFontTextView lbBeaconDescription;
        ImageView          ivPhoto;

        Button btnEditMessage;

    }

    public static class ViewHolderProfile extends ViewHolder {

        ImageView ivAvatar;
        CustomFontTextView lbBusinessName;
        CustomFontTextView lbBusinessContactNumber;
        CustomFontTextView lbBusinessBusinessAddress;
        CustomFontTextView lbMainCategoryName;

        LinearLayout viewCheckIn;
        CustomFontTextView lbLikes;
        ImageView ivLikes;

        CustomFontTextView lbStartTimeMon;
        CustomFontTextView lbEndTimeMon;
        CustomFontTextView lbStartTimeTue;
        CustomFontTextView lbEndTimeTue;
        CustomFontTextView lbStartTimeWed;
        CustomFontTextView lbEndTimeWed;
        CustomFontTextView lbStartTimeThu;
        CustomFontTextView lbEndTimeThu;
        CustomFontTextView lbStartTimeFri;
        CustomFontTextView lbEndTimeFri;
        CustomFontTextView lbStartTimeSat;
        CustomFontTextView lbEndTimeSat;
        CustomFontTextView lbStartTimeSun;
        CustomFontTextView lbEndTimeSun;

        LinearLayout subDealLayout;

        ImageView ivPhoto;
        CustomFontTextView lbDiscountTagLine;
        CustomFontTextView lbDealName;
        CustomFontTextView lbProductDescription;

        TextView lbOriginPrice;
        LinearLayout subDiscayPriceView;
        TextView lbDiscayOriginPrice;
        TextView lbDiscaySpecialPrice;
        LinearLayout subDiscayRemainingView;
        TextView lbDiscayRemaining;



        HListView mCellListView;
        MyCellAdapter myCellAdapter;
        JSONArray businessDeals = new JSONArray();

        public void showDealDetail(Context context, int index) {

            try {

                JSONObject item = businessDeals.getJSONObject(index);

                try {
                    String url = Global.getURLEncoded(item.getString("ProductPhoto"));

                    Picasso.with(context)
                            .load(url)
                            .into(ivPhoto);
                } catch (Exception e) {e.printStackTrace();}

                try {

                    lbDiscountTagLine.setText(item.getString("DiscountedTagLing"));
                    lbDealName.setText(item.getString("ProductName"));
                    lbProductDescription.setText(item.getString("ProductDescription"));

                } catch (Exception e) {e.printStackTrace();}


                try {
                    boolean standardSpecial = item.getBoolean("StandardSpecial");
                    if (standardSpecial) {
                        lbOriginPrice.setVisibility(View.VISIBLE);

                        int originPrice = item.getInt("OrigionalPrice");
                        lbOriginPrice.setText("$" + originPrice);

                        subDiscayPriceView.setVisibility(View.GONE);
                        subDiscayRemainingView.setVisibility(View.GONE);
                    }
                    else {
                        lbOriginPrice.setVisibility(View.GONE);

                        lbDiscayOriginPrice.setText("$" + item.getInt("OrigionalPrice"));
                        lbDiscaySpecialPrice.setText("$" + item.getInt("SpecialPrice"));

                        lbDiscayRemaining.setText(Global.getLeftTime(item.getString("DecayDuration")));

                        subDiscayPriceView.setVisibility(View.VISIBLE);
                        subDiscayRemainingView.setVisibility(View.VISIBLE);

                    }
                } catch (Exception e) {e.printStackTrace();}


            } catch (Exception e) {e.printStackTrace();}
        }
    }



    public class MyCellAdapter extends BaseAdapter {

        private JSONArray items = new JSONArray();
        private LayoutInflater mInflater;

        int nSelectDeal = 0;

        public MyCellAdapter() {
            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        public void setItem(JSONArray array) {
            items = array;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return items.length();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.profile_item_deal, null);
            }

            ImageView ivThumb = (ImageView) convertView.findViewById(R.id.ivThumb);
            ImageView ivSelect = (ImageView) convertView.findViewById(R.id.ivSelect);

            if (nSelectDeal == position) {
                ivSelect.setBackgroundResource(R.drawable.select_deal_item_bg);
            } else {
                ivSelect.setBackgroundResource(0);
            }


            try {
                JSONObject item = items.getJSONObject(position);


                String url = Global.getURLEncoded(item.getString("ProductPhoto"));

                Picasso.with(getActivity())
                        .load(url)
                        .into(ivThumb);
            } catch (Exception e) {e.printStackTrace();}


            return convertView;
        }

    }
}
