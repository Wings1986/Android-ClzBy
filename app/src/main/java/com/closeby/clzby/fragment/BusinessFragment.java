package com.closeby.clzby.fragment;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.closeby.clzby.AppData;
import com.closeby.clzby.MyLocation;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.activity.HomeActivity;
import com.closeby.clzby.customcontrol.CircleImageView;
import com.closeby.clzby.customcontrol.CustomFontTextView;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.closeby.clzby.listener.OnClickMenuItemListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
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


public class BusinessFragment extends BaseFragment {

    LinearLayout searchLayout;
    EditText searchView;

//    ListView mListView;
    private MyCustomAdapter mAdapter;
    PullToRefreshListView mListView;

    JSONArray arrayData = new JSONArray(), arrayDataSource = new JSONArray();

    int m_offset = 0, m_limit = 10;
    private int oldScrolly;

    public static BusinessFragment newInstance() {
        BusinessFragment fragment = new BusinessFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_business, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    private void initView(View view) {

        searchLayout = (LinearLayout) view.findViewById(R.id.searchLayout);
        searchLayout.setVisibility(View.INVISIBLE);

        searchView = (EditText) view.findViewById(R.id.searchView);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchFilter(searchView.getText().toString());

                mAdapter.setItem(arrayDataSource);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mListView = (PullToRefreshListView) view.findViewById(R.id.listview);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                m_offset = 0;
                requestAPI();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                requestAPI();
            }
        });


        mAdapter = new MyCustomAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    JSONObject obj = arrayDataSource.getJSONObject(i - 1);

                    ((HomeActivity) getActivity()).gotoProfile(obj.getString("UserID"));

                } catch (Exception e) {e.printStackTrace();}
            }
        });

//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                View view = absListView.getChildAt(0);
//                int scrolly = (view == null) ? 0 : -view.getTop() + absListView.getFirstVisiblePosition() * view.getHeight();
//                int margin = 10;
//
//                if (scrolly > oldScrolly + margin) {
//                    Log.d("", "SCROLL_UP");
//                    oldScrolly = scrolly;
//
//                    searchLayout.setVisibility(View.VISIBLE);
//
//                } else if (scrolly < oldScrolly - margin) {
//                    Log.d("", "SCROLL_DOWN");
//                    oldScrolly = scrolly;
//
//                    searchLayout.setVisibility(View.INVISIBLE);
//                }
//            }
//        });


//        SearchView searchView = (SearchView) view.findViewById(R.id.searchView);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                if (arrayData == null) {
//                    return false;
//                }
//
//                arrayDataSource = MyJSON.clearJSONArray(arrayDataSource);
//
//
//                if (newText.length() < 1) {
//                    arrayDataSource = arrayData;
//                }
//                else {
//                    for (int i = 0 ; i < arrayData.length() ; i ++) {
//
//                        try {
//                            JSONObject obj = arrayData.getJSONObject(i);
//
//
//                            String businessName = obj.getString("BusinessName");
//                            String categories = obj.getString("CategoryName");
//
//                            if ((businessName != null && businessName.toUpperCase().contains(newText.toUpperCase()))
//                                    || (categories != null && categories.toUpperCase().contains(newText.toUpperCase()))) {
//
//                                arrayDataSource = MyJSON.addJSONObject(arrayDataSource, obj);
//                            }
//                        }catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                mAdapter.setItem(arrayDataSource);
//                mAdapter.notifyDataSetChanged();
//
//                return false;
//            }
//        });

        m_offset = 0;
        m_limit = 10;
        requestAPI();
    }

    void requestAPI() {
        // network

        final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
        waitDialog.show();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);
        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("UserID", AppData.getInstance().loadLoginUserID())
                .add("userlat", "" + MyLocation.getInstance().getLatitude())
                .add("userlong", "" + MyLocation.getInstance().getLongitude())
                .add("range", "" + AppData.RANGE)
                .add("fromrow", "" + m_offset)
                .add("limit", "" + m_limit)
                ;


        httpClient.get("/GetBusinessesWithinRange.aspx", params, new AsyncCallback() {

            @Override
            public void onComplete(HttpResponse httpResponse) {

                waitDialog.dismiss();

                try {
                    JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                    if (result.getString("Success").equals("Success")) {

                        JSONArray products = result.getJSONObject("Data").getJSONArray("BusinessListing");

                        if (m_offset == 0) {
                            arrayData = products;
                        }
                        else {
                            arrayData = MyJSON.addJSONArray(arrayData, products);
                        }

                        arrayDataSource = arrayData;

                        searchFilter(searchView.getText().toString());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                mAdapter.setItem(arrayDataSource);
                                mAdapter.notifyDataSetChanged();

                                mListView.onRefreshComplete();
                            }
                        });

                        if (products.length() != 0) {
                            m_offset += m_limit;
                        }


                    } else {

                        DialogHelper.showToast(getActivity(), result.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    void searchFilter(String newText){
        if (arrayData == null) {
            return;
        }

        arrayDataSource = MyJSON.clearJSONArray(arrayDataSource);

        if (newText.length() < 1) {
            arrayDataSource = arrayData;
        }
        else {
            for (int i = 0 ; i < arrayData.length() ; i ++) {

                try {
                    JSONObject obj = arrayData.getJSONObject(i);


                    String businessName = obj.getString("BusinessName");
                    String categories = obj.getString("CategoryName");

                    if ((businessName != null && businessName.toUpperCase().contains(newText.toUpperCase()))
                            || (categories != null && categories.toUpperCase().contains(newText.toUpperCase()))) {

                        arrayDataSource = MyJSON.addJSONObject(arrayDataSource, obj);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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

                convertView = mInflater.inflate(R.layout.list_item_business, null);

                holder.imageView = (ImageView) convertView.findViewById(R.id.ivAvatar);
                holder.lbBusinesName = (CustomFontTextView)convertView.findViewById(R.id.tvBusinessName);
                holder.lbBusinesAddress = (CustomFontTextView)convertView.findViewById(R.id.tvBusinessAddress);
                holder.lbBusinesCategory = (CustomFontTextView)convertView.findViewById(R.id.tvBusinessCategory);
                holder.lbLikes = (CustomFontTextView)convertView.findViewById(R.id.tvLike);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }


            try {

                JSONObject item = items.getJSONObject(position);


                try {
                    String url = Global.getURLEncoded(item.getString("LogoImageThumb"));

                    ImageLoader.getInstance().displayImage(url, holder.imageView);

                } catch (Exception e) {e.printStackTrace();}

                try {

                    holder.lbBusinesName.setText(item.getString("BusinessName"));
                    holder.lbBusinesAddress.setText(item.getString("BusinessAddressUserInput"));
                    holder.lbBusinesCategory.setText(item.getString("CategoryName"));
                } catch (Exception e) {e.printStackTrace();}


                if (AppData.getInstance().isBusiness()) {
                    holder.lbLikes.setVisibility(View.GONE);
                }
                else {
                    holder.lbLikes.setVisibility(View.VISIBLE);

                    int likes = 0;
                    try {
                        likes = item.getInt("Likes");
                    } catch (Exception e) {e.printStackTrace();}
                    holder.lbLikes.setText("" + likes + " Likes");
                }

            } catch (Exception e) {e.printStackTrace();}


            return convertView;
        }

    }

    public static class ViewHolder {

        public ImageView imageView;
        public CustomFontTextView lbBusinesName;
        public CustomFontTextView lbBusinesAddress;
        public CustomFontTextView lbBusinesCategory;

        public CustomFontTextView lbLikes;

    }

}
