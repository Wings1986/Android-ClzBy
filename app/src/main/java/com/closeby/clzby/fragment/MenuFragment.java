package com.closeby.clzby.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.closeby.clzby.AppData;
import com.closeby.clzby.R;
import com.closeby.clzby.customcontrol.CircleImageView;
import com.closeby.clzby.customcontrol.CustomFontTextView;
import com.closeby.clzby.listener.OnClickMenuItemListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by iGold on 6/3/15.
 */


public class MenuFragment extends BaseFragment {

    ListView mListView;
    private MyCustomAdapter mAdapter;

    private OnClickMenuItemListener mOnClickMenuitemListener;

    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    private void initView(View view) {

        mListView = (ListView) view.findViewById(R.id.listview);


        mAdapter = new MyCustomAdapter();
        mListView.setAdapter(mAdapter);
    }

    public void setOnClickMenuItemListener(OnClickMenuItemListener listener) {
        mOnClickMenuitemListener = listener;
    }


    public class MyCustomAdapter extends BaseAdapter {

        private static final int TYPE_PROFILE = 0;
        private static final int TYPE_ITEM = 1;
        private static final int TYPE_MAX_COUNT = TYPE_ITEM + 1;


        private List<Item> items = new ArrayList<>();
        private LayoutInflater mInflater;


        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (AppData.getInstance().isBusiness()) {
                items.add(new Item("Profile", R.drawable.icon_menu_profile));
                items.add(new Item("Inventory", R.drawable.icon_menu_inventory));
                items.add(new Item("Beacons", R.drawable.icon_menu_beacon));
                items.add(new Item("Dashboard", R.drawable.icon_menu_dashboard));
                items.add(new Item("What's CloseBy?", R.drawable.icon_menu_deals));
                items.add(new Item("Shops CloseBy", R.drawable.icon_menu_shop));
                items.add(new Item("Map", R.drawable.icon_menu_map));
                items.add(new Item("Log Out", R.drawable.icon_menu_logout));
            }
            else {
                items.add(new Item("What's CloseBy?", R.drawable.icon_menu_deals));
                items.add(new Item("Shops CloseBy", R.drawable.icon_menu_shop));
                items.add(new Item("Map", R.drawable.icon_menu_map));
                items.add(new Item("Favourite", R.drawable.icon_menu_favourite));
                items.add(new Item("Notification", R.drawable.icon_menu_notify));
                items.add(new Item("Log Out", R.drawable.icon_menu_logout));
            }

        }

        @Override
        public int getViewTypeCount() {
            // Get the number of items in the enum
            return TYPE_MAX_COUNT;

        }

        @Override
        public int getItemViewType(int position) {
            // Use getViewType from the Item interface

            if (AppData.getInstance().isBusiness() && position == 0) { // business
                return TYPE_PROFILE;
            }
            else {
                return TYPE_ITEM;
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return AppData.getInstance().isBusiness() ? items.size() + 1 : items.size();
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

            int type = getItemViewType(position);

            if (convertView == null) {
                holder = new ViewHolder();
                switch (type) {
                    case TYPE_PROFILE:
                        convertView = mInflater.inflate(R.layout.list_item_menu_profile, null);
                        holder.imageView = (CircleImageView) convertView.findViewById(R.id.ivAvatar);
                        holder.textView = (CustomFontTextView)convertView.findViewById(R.id.tvTitle);
                        break;
                    case TYPE_ITEM:
                        convertView = mInflater.inflate(R.layout.list_item_menu_item, null);

                        holder.imageView = (ImageView) convertView.findViewById(R.id.ivAvatar);
                        holder.textView = (CustomFontTextView)convertView.findViewById(R.id.tvTitle);

                        holder.backView = convertView;

                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            if (AppData.getInstance().isBusiness() && position == 0) {

                if (AppData.getInstance().loadUserImageUrl().length() < 1) {
                    holder.imageView.setImageResource(R.drawable.avatar);
                }
                else {
                    Picasso.with(getActivity())
                            .load(AppData.getInstance().loadUserImageUrl())
                            .placeholder(R.drawable.avatar)
                            .into(holder.imageView);
                }
                holder.textView.setText(AppData.getInstance().loadUsername());
            }
            else {
                final int index = AppData.getInstance().isBusiness() ? position -1 : position;
                holder.imageView.setImageResource(items.get(index).getResID());
                holder.textView.setText(items.get(index).getTitle());

                holder.backView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnClickMenuitemListener != null) {
                            mOnClickMenuitemListener.onClickMenuItem(index);
                        }
                    }
                });

            }

            return convertView;
        }

    }

    public static class ViewHolder {
        public View backView;
        public CustomFontTextView textView;
        public ImageView imageView;
    }

    public class Item {
        String title;
        int resID;

        public Item(String _title, int _resID) {
            this.title = _title;
            this.resID = _resID;
        }
        public String getTitle() { return this.title; }
        public int getResID() { return this.resID; }
    }



}
