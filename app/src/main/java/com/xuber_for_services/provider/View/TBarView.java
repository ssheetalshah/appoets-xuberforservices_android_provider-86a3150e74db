package com.xuber_for_services.provider.View;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.xuber_for_services.provider.Listener.MenuItemClickListener;
import com.xuber_for_services.provider.Listener.NavigationCallBack;
import com.xuber_for_services.provider.R;

public class TBarView implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private Activity activity;
    private Toolbar mToolbar;
    private TextView mTitleTxt;
    private Fragment fragment;

    private boolean isDetailScreen;

    private int menuResID;

    public TBarView(Fragment fragment, Toolbar mToolbar) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.mToolbar = mToolbar;
        this.mTitleTxt = (TextView) this.mToolbar.findViewById(R.id.toolbar_title);
    }

    public TBarView(Activity activity, Toolbar mToolbar) {
        this.activity = activity;
        this.mToolbar = mToolbar;
        this.mTitleTxt = (TextView) this.mToolbar.findViewById(R.id.toolbar_title);
        //this.mTitleTxt = (TextView) this.mToolbar.findViewById(R.id.toolbar_title);
        // this.mTitleTxt = (TextView) this.mToolbar.findViewById(R.id.toolbar_title);
    }

    public TBarView() {

    }

    public void setupToolbar(int navIcon, String title, boolean displayMenu, final boolean isDetailScreen) {
        this.isDetailScreen = isDetailScreen;
        mTitleTxt.setText(title);
        mToolbar.setNavigationIcon(navIcon);
        mToolbar.setTitleTextColor(Color.WHITE);
        if (displayMenu) {
            mToolbar.inflateMenu(menuResID);
            mToolbar.setOnMenuItemClickListener(this);
        }
        mToolbar.setNavigationOnClickListener(this);
        enableDisableNavigationDrawer(!isDetailScreen);
    }


    public void enableDisableNavigationDrawer(boolean enable) {
        if (activity instanceof NavigationCallBack) {
            final NavigationCallBack navigationCallBack = (NavigationCallBack) activity;
            navigationCallBack.enableDisableNavigationDrawer(enable);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        MenuItemClickListener menuItemClickListener = (MenuItemClickListener) fragment;
        menuItemClickListener.onMenuItemClick(item, null);
        return false;
    }

    public void setMenuResID(int menuResID) {
        this.menuResID = menuResID;
    }

    @Override
    public void onClick(View v) {
        if (isDetailScreen) {
            Log.e("Back arror pressed", "onClick: ");
            if (activity != null)
                hideSoftKeyboard(activity);
            activity.onBackPressed();
        } else {
            NavigationCallBack navigationCallBack = (NavigationCallBack) activity;
            navigationCallBack.handleNavigationDrawer();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
