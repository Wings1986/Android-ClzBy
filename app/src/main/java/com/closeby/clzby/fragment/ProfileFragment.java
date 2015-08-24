package com.closeby.clzby.fragment;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.closeby.clzby.AppData;

import com.closeby.clzby.MyLocation;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontTextView;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.huewu.pla.lib.MultiColumnListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;

/**
 * Created by iGold on 6/3/15.
 */
public class ProfileFragment extends BaseFragment {

    View thisView = null;

    HListView mListView;
    MyCustomAdapter mAdapter;

    JSONArray businessDeals = new JSONArray();


    String businessUserID;
    int nSelectDeal = 0;

    public static ProfileFragment newInstance(String businessUserID) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();

        args.putString("businessID", businessUserID);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        businessUserID = getArguments().getString("businessID", "");

        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();

        requestAPI();
    }

    private void initView(View view) {

        thisView = view;


        mListView = (HListView) view.findViewById(R.id.hListView);
        mListView.setHeaderDividersEnabled( true );
        mListView.setFooterDividersEnabled( true );

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (nSelectDeal != i) {
                    nSelectDeal = i;
                    showDealDetail(nSelectDeal);

                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        mAdapter = new MyCustomAdapter();
        mListView.setAdapter(mAdapter);

    }

    private void requestAPI() {

        if (thisView == null)
            return;

        final ImageView ivAvatar = (ImageView) thisView.findViewById(R.id.ivAvatar);
        final CustomFontTextView lbBusinessName = (CustomFontTextView) thisView.findViewById(R.id.tvBusinessName);
        final CustomFontTextView lbBusinessContactNumber = (CustomFontTextView) thisView.findViewById(R.id.tvBusinessPhone);
        final CustomFontTextView lbBusinessBusinessAddress = (CustomFontTextView) thisView.findViewById(R.id.tvBusinessAddress);
        final CustomFontTextView lbMainCategoryName = (CustomFontTextView) thisView.findViewById(R.id.tvBusinessCategory);

        LinearLayout viewCheckIn = (LinearLayout) thisView.findViewById(R.id.viewCheckIn);
        final CustomFontTextView lbLikes= (CustomFontTextView) thisView.findViewById(R.id.tvLike);
        final ImageView ivLikes = (ImageView) thisView.findViewById(R.id.ivLike);

        if (AppData.getInstance().isBusiness()) {
            viewCheckIn.setVisibility(View.GONE);
        }
        else {
            viewCheckIn.setVisibility(View.VISIBLE);
        }

        final CustomFontTextView lbStartTimeMon = (CustomFontTextView) thisView.findViewById(R.id.tvStartTimeMon);
        final CustomFontTextView lbEndTimeMon = (CustomFontTextView) thisView.findViewById(R.id.tvEndTimeMon);
        final CustomFontTextView lbStartTimeTue = (CustomFontTextView) thisView.findViewById(R.id.tvStartTimeTue);
        final CustomFontTextView lbEndTimeTue = (CustomFontTextView) thisView.findViewById(R.id.tvEndTimeTue);
        final CustomFontTextView lbStartTimeWed = (CustomFontTextView) thisView.findViewById(R.id.tvStartTimeWed);
        final CustomFontTextView lbEndTimeWed = (CustomFontTextView) thisView.findViewById(R.id.tvEndTimeWed);
        final CustomFontTextView lbStartTimeThu = (CustomFontTextView) thisView.findViewById(R.id.tvStartTimeThu);
        final CustomFontTextView lbEndTimeThu = (CustomFontTextView) thisView.findViewById(R.id.tvEndTimeThu);
        final CustomFontTextView lbStartTimeFri = (CustomFontTextView) thisView.findViewById(R.id.tvStartTimeFri);
        final CustomFontTextView lbEndTimeFri = (CustomFontTextView) thisView.findViewById(R.id.tvEndTimeFri);
        final CustomFontTextView lbStartTimeSat = (CustomFontTextView) thisView.findViewById(R.id.tvStartTimeSat);
        final CustomFontTextView lbEndTimeSat = (CustomFontTextView) thisView.findViewById(R.id.tvEndTimeSat);
        final CustomFontTextView lbStartTimeSun = (CustomFontTextView) thisView.findViewById(R.id.tvStartTimeSun);
        final CustomFontTextView lbEndTimeSun = (CustomFontTextView) thisView.findViewById(R.id.tvEndTimeSun);




        // network

        final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
        waitDialog.show();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);

        String url = "";

        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("UserID", AppData.getInstance().loadLoginUserID());


        if (businessUserID.length() < 1) {

            url = "/GetCompleteBusinessProfile.aspx";

            params.add("UserID", AppData.getInstance().loadLoginUserID());

        } else {
            url = "/GetCompleteBusinessProfileForCustomer.aspx";

            params.add("CustomerUserID", AppData.getInstance().loadLoginUserID())
                    .add("BusinessUserID", businessUserID)
                    .add("lat", String.valueOf(MyLocation.getInstance().getLatitude()))
                    .add("long", String.valueOf(MyLocation.getInstance().getLongitude()))
            ;
        }


        httpClient.get(url, params, new AsyncCallback() {

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

                                    JSONObject dic = result.getJSONObject("Data");


                                    try {
                                        String businessLogo = dic.getString("SmallImage");
                                        if (businessLogo != null && businessLogo.length() > 0) {
//                                            Picasso.with(getActivity())
//                                                    .load(businessLogo)
//                                                    .into(ivAvatar);
                                            ImageLoader.getInstance().displayImage(businessLogo, ivAvatar);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        lbBusinessName.setText(dic.getString("BusinessName"));
                                        lbBusinessContactNumber.setText(dic.getString("BusinessContactNumber"));
                                        lbBusinessBusinessAddress.setText(dic.getString("BusinessAddress"));
                                        lbMainCategoryName.setText(dic.getString("MainCategoryName"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    if (!AppData.getInstance().isBusiness()) {

                                        try {
                                            lbLikes.setText(dic.getString("NumberOfLikes"));

                                            String currentUserHasLiked = dic.getString("CurrentUserHasLiked");
                                            if (currentUserHasLiked.equalsIgnoreCase("yes")) {
                                                ivLikes.setImageResource(R.drawable.like_button_tapped);
                                            } else {
                                                ivLikes.setImageResource(R.drawable.like_button);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    try {

                                        JSONArray openingHours = dic.getJSONArray("OpeningHours");

                                        for (int i = 0 ; i < openingHours.length() ; i ++) {

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
                                                            lbStartTimeMon.setText("Closed");
                                                            lbEndTimeMon.setVisibility(View.INVISIBLE);
                                                        } else {
                                                            lbStartTimeMon.setText(startTime);
                                                            lbEndTimeMon.setText(endTime);
                                                        }
                                                        break;
                                                    }
                                                    case 1: {
                                                        if (startTime.equalsIgnoreCase("00:00")) {
                                                            lbStartTimeTue.setText("Closed");
                                                            lbEndTimeTue.setVisibility(View.INVISIBLE);
                                                        } else {
                                                            lbStartTimeTue.setText(startTime);
                                                            lbEndTimeTue.setText(endTime);
                                                        }
                                                        break;
                                                    }
                                                    case 2: {
                                                        if (startTime.equalsIgnoreCase("00:00")) {
                                                            lbStartTimeWed.setText("Closed");
                                                            lbEndTimeWed.setVisibility(View.INVISIBLE);
                                                        } else {
                                                            lbStartTimeWed.setText(startTime);
                                                            lbEndTimeWed.setText(endTime);
                                                        }
                                                        break;
                                                    }
                                                    case 3: {
                                                        if (startTime.equalsIgnoreCase("00:00")) {
                                                            lbStartTimeThu.setText("Closed");
                                                            lbEndTimeThu.setVisibility(View.INVISIBLE);
                                                        } else {
                                                            lbStartTimeThu.setText(startTime);
                                                            lbEndTimeThu.setText(endTime);
                                                        }
                                                        break;
                                                    }
                                                    case 4: {
                                                        if (startTime.equalsIgnoreCase("00:00")) {
                                                            lbStartTimeFri.setText("Closed");
                                                            lbEndTimeFri.setVisibility(View.INVISIBLE);
                                                        } else {
                                                            lbStartTimeFri.setText(startTime);
                                                            lbEndTimeFri.setText(endTime);
                                                        }
                                                        break;
                                                    }
                                                    case 5: {
                                                        if (startTime.equalsIgnoreCase("00:00")) {
                                                            lbStartTimeSat.setText("Closed");
                                                            lbEndTimeSat.setVisibility(View.INVISIBLE);
                                                        } else {
                                                            lbStartTimeSat.setText(startTime);
                                                            lbEndTimeSat.setText(endTime);
                                                        }
                                                        break;
                                                    }
                                                    case 6: {
                                                        if (startTime.equalsIgnoreCase("00:00")) {
                                                            lbStartTimeSun.setText("Closed");
                                                            lbEndTimeSun.setVisibility(View.INVISIBLE);
                                                        } else {
                                                            lbStartTimeSun.setText(startTime);
                                                            lbEndTimeSun.setText(endTime);
                                                        }
                                                        break;
                                                    }

                                                }


                                            } catch (Exception e) {e.printStackTrace();}

                                        }


                                        }catch(Exception e){ e.printStackTrace();}



                                    //
                                    try {
                                        businessDeals = dic.getJSONArray("BusinessDeals");

                                        mAdapter.setItem(businessDeals);
                                        mAdapter.notifyDataSetChanged();

                                        if (businessDeals.length() > 0) {
                                            showDealDetail(nSelectDeal);
                                        }
                                        else {
                                            showDealDetail(-1);
                                        }

                                    } catch (Exception e) {e.printStackTrace();}





                                } catch (Exception e) {
                                    e.printStackTrace();
                                    }

                                }
                            }

                            );
                        }else {

                        DialogHelper.showToast(getActivity(), result.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void showDealDetail(int index) {

        LinearLayout subDealView = (LinearLayout) thisView.findViewById(R.id.subDealList);

        if (index == -1) {
            subDealView.setVisibility(View.GONE);
            return;
        }

        subDealView.setVisibility(View.VISIBLE);

        ImageView ivPhoto = (ImageView) thisView.findViewById(R.id.ivPhoto);
        CustomFontTextView lbDiscountTagLine = (CustomFontTextView) thisView.findViewById(R.id.tvDiscountTagLine);
        CustomFontTextView lbDealName = (CustomFontTextView) thisView.findViewById(R.id.tvDealName);
        CustomFontTextView lbProductDescription = (CustomFontTextView) thisView.findViewById(R.id.tvDealDescription);

        TextView lbOriginPrice = (TextView) thisView.findViewById(R.id.tvOriginPrice);
        LinearLayout subDiscayPriceView = (LinearLayout) thisView.findViewById(R.id.specialOfferView);
        TextView lbDiscayOriginPrice = (TextView) thisView.findViewById(R.id.tvDelayOrginPrice);
        TextView lbDiscaySpecialPrice = (TextView) thisView.findViewById(R.id.tvDelaySpecialPrice);
        LinearLayout subDiscayRemainingView = (LinearLayout) thisView.findViewById(R.id.decayDurationView);
        TextView lbDiscayRemaining = (TextView) thisView.findViewById(R.id.tvDiscayRemaining);


        try {

            JSONObject item = businessDeals.getJSONObject(index);

            try {
                String url = Global.getURLEncoded(item.getString("ProductPhoto"));

                Picasso.with(getActivity())
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


    public class MyCustomAdapter extends BaseAdapter {

        private JSONArray items = new JSONArray();
        private LayoutInflater mInflater;

        public MyCustomAdapter() {
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

//            ViewGroup.LayoutParams params = convertView.getLayoutParams();
//            params.width = getResources().getDimensionPixelSize( R.dimen.profile_deal_width);


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
