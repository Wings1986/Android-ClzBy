package com.closeby.clzby.fragment;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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


import com.huewu.pla.lib.MultiColumnPullToRefreshListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;
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

    LinearLayout searchLayout;

    MultiColumnPullToRefreshListView mGridView;
    MyCustomAdapter mAdapter;
//    PullToRefreshStaggeredView mPullRefreshGridView;


    JSONArray arrayData = new JSONArray(), arrayDataSource = new JSONArray();

    int imageViewWidth = 300;

    int m_offset = 0, m_limit = 10;

    private int oldScrolly;

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

        searchLayout = (LinearLayout) view.findViewById(R.id.searchLayout);
        searchLayout.setVisibility(View.INVISIBLE);

/*
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
*/
        mGridView = (MultiColumnPullToRefreshListView) view.findViewById(R.id.gridView);
        mGridView.setOnRefreshListener(new MultiColumnPullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        mAdapter = new MyCustomAdapter();
        mGridView.setAdapter(mAdapter);

        mGridView.setOnScrollListener(new PLA_AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(PLA_AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View view = absListView.getChildAt(0);
                int scrolly = (view == null) ? 0 : -view.getTop() + absListView.getFirstVisiblePosition() * view.getHeight();
                int margin = 10;

                if (scrolly > oldScrolly + margin) {
                    Log.d("", "SCROLL_UP");
                    oldScrolly = scrolly;

                    searchLayout.setVisibility(View.VISIBLE);

                } else if (scrolly < oldScrolly - margin) {
                    Log.d("", "SCROLL_DOWN");
                    oldScrolly = scrolly;

                    searchLayout.setVisibility(View.INVISIBLE);
                }
            }
        });



        final EditText searchView = (EditText) view.findViewById(R.id.searchView);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (arrayData == null) {
                    return;
                }

                arrayDataSource = MyJSON.clearJSONArray(arrayDataSource);

                String newText = searchView.getText().toString();

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
            }

            @Override
            public void afterTextChanged(Editable s) {

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

//                                mPullRefreshGridView.onRefreshComplete();
                                mGridView.onRefreshComplete();
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

        public JSONArray items = new JSONArray();
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
        public Object getItem(int position) {
            try {
                return items.get(position);
            } catch (Exception e) {e.printStackTrace();}
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
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


                    int liked = 0;
                    int likes = 0;
                    try {
                        liked = item.getInt("CurrentUserHasLiked");
                        likes = item.getInt("NumberOfLikes");
                    } catch (Exception e) {e.printStackTrace();}

                    holder.ivLike.setImageResource(liked == 1 ? R.drawable.like_button_tapped : R.drawable.like_button);
                    holder.lbLike.setText("" + likes);

                    holder.ivLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onLike(position);
                        }
                    });

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
                    holder.lbOriginPrice.setText("$" + String.format("%.02f", item.getDouble("OrigionalPrice")));
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

                        holder.lbDecayOriginPrice.setText("$" + String.format("%.02f", item.getDouble("OrigionalPrice")));
                        holder.lbDecaySpecialPrice.setText("$" + String.format("%.02f", item.getDouble("SpecialPrice")));

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


    void onLike(final int pos) {
        final JSONObject item = (JSONObject) mAdapter.getItem(pos);
        if (item == null) {
            return;
        }

        String businessID = "";
        String dealID = "";

        try {
            businessID = item.getString("BusinessID");
        } catch (Exception e) {e.printStackTrace();}

        try {
            dealID = item.getString("ID");
        } catch (Exception e) {e.printStackTrace();}




        final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
        waitDialog.show();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);
        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("UserID", AppData.getInstance().loadLoginUserID())
                .add("BusinessID", businessID)
                .add("DealID", dealID)
                ;

        String url = "/UserSaveDeal.aspx";


        httpClient.get(url, params, new AsyncCallback() {

            @Override
            public void onComplete(HttpResponse httpResponse) {

                waitDialog.dismiss();

                try {
                    JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                    if (result.getString("Success").equals("Success")) {

                        int liked = 0;
                        int likes = 0;

                        try {
                            liked = item.getInt("CurrentUserHasLiked");
                            likes = item.getInt("NumberOfLikes");
                        } catch (Exception e) {e.printStackTrace();}

                        try {
                            if (liked == 1) {
                                item.put("CurrentUserHasLiked", 0);
                                item.put("NumberOfLikes", likes - 1);
                            } else {
                                item.put("CurrentUserHasLiked", 1);
                                item.put("NumberOfLikes", likes + 1);
                            }
                        } catch (Exception e) {e.printStackTrace();}

                        arrayDataSource.put(pos, item);


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                mAdapter.setItem(arrayDataSource);
                                mAdapter.notifyDataSetChanged();

//                                mPullRefreshGridView.onRefreshComplete();
                                mGridView.onRefreshComplete();
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
