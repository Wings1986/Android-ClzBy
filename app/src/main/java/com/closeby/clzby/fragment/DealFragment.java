package com.closeby.clzby.fragment;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.closeby.clzby.AppData;
import com.closeby.clzby.MyLocation;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.activity.HomeActivity;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontTextView;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.closeby.clzby.listener.ImageViewLayoutSize;
import com.closeby.clzby.listener.OnLayoutSizeListener;
import com.github.siyamed.shapeimageview.CircularImageView;


import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by iGold on 6/3/15.
 */
public class DealFragment extends BaseFragment {

    GridView mGridView;
    MyCustomAdapter mAdapter;
    PullToRefreshStaggeredView mPullRefreshGridView;


    JSONArray arrayData = new JSONArray(), arrayDataSource = new JSONArray();

    int imageViewWidth = 300;

    int m_offset = 0, m_limit = 10;



    public static DealFragment newInstance() {
        DealFragment fragment = new DealFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_deals, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    private void initView(View view) {


        mPullRefreshGridView = (PullToRefreshStaggeredView) view.findViewById(R.id.gridview);
        mGridView = mPullRefreshGridView.getRefreshableView();

        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {


            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                m_offset = 0;
                requestAPI();
            }

            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                requestAPI();
            }

        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    JSONObject obj = arrayDataSource.getJSONObject(i);

                    ((HomeActivity) getActivity()).gotoProfile(obj.getString("UserID"));

                } catch (Exception e) {e.printStackTrace();}


            }
        });

        mAdapter = new MyCustomAdapter();
        mGridView.setAdapter(mAdapter);


        SearchView searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (arrayData == null) {
                    return false;
                }

                arrayDataSource = MyJSON.clearJSONArray(arrayDataSource);


                if (newText.length() < 1) {
                    arrayDataSource = arrayData;
                } else {
                    for (int i = 0; i < arrayData.length(); i++) {

                        try {
                            JSONObject obj = arrayData.getJSONObject(i);


                            String productName = obj.getString("ProductName");
                            String businessName = obj.getString("BusinessName");
                            String categories = obj.getString("DealSubCategories");

                            if ((productName != null && productName.toUpperCase().contains(newText.toUpperCase()))
                                    || (businessName != null && businessName.toUpperCase().contains(newText.toUpperCase()))
                                    || (categories != null && categories.toUpperCase().contains(newText.toUpperCase()))) {

                                arrayDataSource = MyJSON.addJSONObject(arrayDataSource, obj);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                mAdapter.setItem(arrayDataSource);
                mAdapter.notifyDataSetChanged();

                return false;
            }
        });

        requestAPI();
    }

    public void refreshListView () {
        m_offset = 0;
        requestAPI();
    }

    void requestAPI() {
        // network

        int offset = m_offset;
        int limit = m_limit;


        final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
        waitDialog.show();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);
        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("UserID", AppData.getInstance().loadLoginUserID())
                .add("lat", "" + MyLocation.getInstance().getLatitude())
                .add("long", "" + MyLocation.getInstance().getLongitude())
                .add("fromrow", "" + offset)
                .add("limit", "" + limit)
                ;

        String url = "/ReturnDealsWithGPS.aspx";


        httpClient.get(url, params, new AsyncCallback() {

            @Override
            public void onComplete(HttpResponse httpResponse) {

                waitDialog.dismiss();

                try {
                    JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                    if (result.getString("Success").equals("Success")) {

                        JSONArray products = result.getJSONObject("Data").getJSONArray("Products");

                        if (m_offset == 0) {
                            arrayData = products;
                        }
                        else {
                            arrayData = MyJSON.addJSONArray(arrayData, products);
                        }

                        arrayDataSource = arrayData;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                mAdapter.setItem(arrayDataSource);
                                mAdapter.notifyDataSetChanged();

                                mPullRefreshGridView.onRefreshComplete();
                            }
                        });

                        if (products.length() != 0) {
                            m_offset += m_limit;
                        }


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

        private JSONArray items = new JSONArray();
        private LayoutInflater mInflater;

        public MyCustomAdapter() {
            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.grid_item_deal, null);


                holder.ivBusinessLogo = (CircularImageView) convertView.findViewById(R.id.tvBusinessLogo);
                holder.lbBusinessName= (CustomFontTextView) convertView.findViewById(R.id.tvBusinessName);
                holder.ivLike = (ImageView) convertView.findViewById(R.id.ivLike);
                holder.lbLike = (CustomFontTextView) convertView.findViewById(R.id.tvLike);


                holder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
//                holder.ivPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.lbOriginPrice = (TextView) convertView.findViewById(R.id.tvOriginPrice);
                holder.subDecayView = (LinearLayout) convertView.findViewById(R.id.specialOfferView);
                holder.lbDecayOriginPrice = (TextView) convertView.findViewById(R.id.tvDelayOrginPrice);
                holder.lbDecaySpecialPrice = (TextView) convertView.findViewById(R.id.tvDelaySpecialPrice);

                holder.lbTagLine= (CustomFontTextView) convertView.findViewById(R.id.tvTagLine);
                holder.lbDealName= (CustomFontTextView) convertView.findViewById(R.id.tvDealName);
                holder.lbDealDescription= (CustomFontTextView) convertView.findViewById(R.id.tvDealDescription);
                holder.lbDealSubCategory = (CustomFontTextView) convertView.findViewById(R.id.tvSubCategory);

                holder.subDecayTimeView = (LinearLayout) convertView.findViewById(R.id.decayDurationView);
                holder.lbDecayDuration = (TextView) convertView.findViewById(R.id.tvDecayDuration);



                holder.btnMap = (Button) convertView.findViewById(R.id.btnMap);
                holder.btnMap.setOnTouchListener(CustomButtonTouchListener.getInstance());
                holder.btnMap.setTag(position);
                holder.btnMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ((HomeActivity) getActivity()).gotoMap();
                    }
                });

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            try {
                JSONObject item = items.getJSONObject(position);

                try {
                    String url = Global.getURLEncoded(item.getString("SmallImageLogo"));
                    ImageLoader.getInstance().displayImage(url, holder.ivBusinessLogo);
                } catch (Exception e) {e.printStackTrace();}

                try {
                    holder.lbBusinessName.setText(item.getString("BusinessName"));

                    if (AppData.getInstance().isBusiness()) {
                        holder.ivLike.setVisibility(View.GONE);
                        holder.lbLike.setVisibility(View.GONE);
                    }
                    else {
                        holder.ivLike.setVisibility(View.VISIBLE);
                        holder.lbLike.setVisibility(View.VISIBLE);

                        boolean liked = false;
                        int likes = 0;
                        try {
                            liked = item.getBoolean("CurrentUserHasLiked");
                            likes = item.getInt("NumberOfLikes");
                        } catch (Exception e) {e.printStackTrace();}

                        holder.ivLike.setImageResource(liked ? R.drawable.like_button_tapped : R.drawable.like_button);
                        holder.lbLike.setText("" + likes);
                    }
                } catch (Exception e) {e.printStackTrace();}

                try {

                    holder.ivPhoto.getViewTreeObserver().addOnGlobalLayoutListener(new ImageViewLayoutSize(holder.ivPhoto, new OnLayoutSizeListener() {
                        @Override
                        public void onGetSize(int width, int height) {
                            imageViewWidth = width;
                        }
                    }));


                    String url = Global.getURLEncoded(item.getString("ThumbImage"));
                    Log.d("InventoryFragment", "url = " + url);
//                    Picasso.with(getActivity())
//                            .load(url)
//                            .into(new CustomTarget(holder.ivPhoto));

                    ImageLoader.getInstance().displayImage(url, holder.ivPhoto, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            ImageView imageView = (ImageView) view;

                            FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) imageView.getLayoutParams();
                            int ivWidth = bitmap.getWidth();
                            int ivHeight = bitmap.getHeight();
                            int virturlHeight = 320;
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

                } catch (Exception e) {
                    e.printStackTrace();
                }


                holder.lbOriginPrice.setVisibility(View.GONE);
                try {
                    holder.lbOriginPrice.setText("$" + item.getString("OrigionalPrice"));
                } catch (Exception e) {}

                boolean bDecaySpecial = false;
                try {
                    bDecaySpecial = item.getBoolean("DecayingSpecial");
                } catch (Exception e) {}

                if (bDecaySpecial) {
                    try {
                        holder.lbOriginPrice.setVisibility(View.GONE);
                        holder.subDecayView.setVisibility(View.VISIBLE);
                        holder.subDecayTimeView.setVisibility(View.VISIBLE);

                        holder.lbDecayOriginPrice.setText("$" + item.getInt("OrigionalPrice"));
                        holder.lbDecaySpecialPrice.setText("$" + item.getInt("SpecialPrice"));

                        holder.lbDecayDuration.setText(Global.getLeftTime(item.getString("DecayEndTime")));
                    } catch (Exception e) {}
                }
                else {
                    holder.lbOriginPrice.setVisibility(View.VISIBLE);
                    holder.subDecayView.setVisibility(View.GONE);
                    holder.subDecayTimeView.setVisibility(View.GONE);
                }

                try {

                    holder.lbTagLine.setText(item.getString("DiscountedTagLine"));
                    holder.lbDealName.setText(item.getString("ProductName"));
                    holder.lbDealDescription.setText(item.getString("ProductDescription"));
                    holder.lbDealSubCategory.setText(item.getString("DealSubCategories"));


                } catch (Exception e) {}



            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

    }

    public static class ViewHolder {

//        RoundedImageView ivBusinessLogo;
        CircularImageView ivBusinessLogo;
        CustomFontTextView lbBusinessName;
        ImageView ivLike;
        CustomFontTextView lbLike;

        ImageView ivPhoto;

        TextView lbOriginPrice;
        LinearLayout subDecayView;
        TextView lbDecayOriginPrice;
        TextView lbDecaySpecialPrice;


        CustomFontTextView lbTagLine;
        CustomFontTextView lbDealName;
        CustomFontTextView lbDealDescription;
        CustomFontTextView lbDealSubCategory;

        LinearLayout subDecayTimeView;
        TextView lbDecayDuration;

        Button btnMap;

    }


}
