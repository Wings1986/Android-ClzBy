package com.closeby.clzby.fragment;

import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;

/**
 * Created by iGold on 6/3/15.
 */
public class BaseFragment extends Fragment {

    public int getScreenWidth() {
        DisplayMetrics dimension = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dimension);
        int screen_width = dimension.widthPixels;
        int screen_height = dimension.heightPixels;

        return screen_width;
    }

}
