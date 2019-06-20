package com.xuber_for_services.provider.Fragments;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.xuber_for_services.provider.Helper.SharedHelper;
import com.xuber_for_services.provider.Listener.NavUpdateListener;
import com.xuber_for_services.provider.Models.NavMenu;
import com.xuber_for_services.provider.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * NavigationDrawerFragment used in Admi Mode.
 * usage : used to navigate between screens within the App.
 * the listener is implement.
 * Dealer Navigation Drawer
 */
public class NavigationDrawerFragment extends Fragment implements View.OnClickListener, NavUpdateListener {

    public static final String TAG = "NavDrawerFgmt";

    private CircleImageView mUserProfileImg;
    private TextView mNameTxt;
    private TextView mEmailTxt;

    private Button homeBtn, settingsBtn, share_btn, logoutBtn, historyBtn, earningsBtn, summaryBtn, helpBtn;

    private RelativeLayout headerLayout;
    private ProgressBar mImgProgressBar;
    // private RelativeLayout headerLayout;

    private NavMenu mNavMenuItems = NavMenu.HOME;

    private NavDrawerFgmtListener mListener;
    private DrawerLayout mDrawerLayout;
    private View mNavigationView;

    public NavigationDrawerFragment() {
    }

    public static NavigationDrawerFragment newInstance() {
        NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavDrawerFgmtListener) {
            mListener = (NavDrawerFgmtListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void setBtnDrawables() {
        Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.home);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        homeBtn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.service_history);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        historyBtn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.settings);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        settingsBtn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.share);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        share_btn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.logout);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        logoutBtn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.help_select);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        helpBtn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.summary_select);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        summaryBtn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.earnings_select);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        earningsBtn.setCompoundDrawables(img, null, null, null);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        findViewsById(view);
        setClickListeners();
        setBasicDetails();
        if (savedInstanceState == null)
            setBtnStates(homeBtn);
        else
            checkBtnStates();
        setBtnDrawables();
        return view;
    }


    private void findViewsById(View view) {
        mUserProfileImg = (CircleImageView) view.findViewById(R.id.circleView);
        mNameTxt = (TextView) view.findViewById(R.id.name);
        String strName = SharedHelper.getKey(getContext(), "first_name") + " " + SharedHelper.getKey(getContext(), "last_name");
        mNameTxt.setText(strName);

        if (!SharedHelper.getKey(getContext(), "picture").equalsIgnoreCase("")) {
            Picasso.with(getContext()).load(SharedHelper.getKey(getContext(), "picture"))
                    .error(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE)
                    .fit()
                    .into(mUserProfileImg);
        }

        homeBtn = (Button) view.findViewById(R.id.home_btn);
        settingsBtn = (Button) view.findViewById(R.id.settings_btn);
        share_btn = (Button) view.findViewById(R.id.share_btn);
        logoutBtn = (Button) view.findViewById(R.id.logout_btn);
        historyBtn = (Button) view.findViewById(R.id.history_btn);
        earningsBtn = (Button) view.findViewById(R.id.earnings_btn);
        summaryBtn = (Button) view.findViewById(R.id.summary_btn);
        helpBtn = (Button) view.findViewById(R.id.help_btn);
        headerLayout = (RelativeLayout) view.findViewById(R.id.navigation_header);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUserProfileImg = null;
        mNameTxt = null;
        mEmailTxt = null;
        homeBtn = null;
        settingsBtn = null;
        share_btn = null;
        logoutBtn = null;
        historyBtn = null;
        headerLayout = null;
        //mImgProgressBar = null;
    }

    private void setClickListeners() {
        homeBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
        share_btn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);
        headerLayout.setOnClickListener(this);
        summaryBtn.setOnClickListener(this);
        helpBtn.setOnClickListener(this);
        earningsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isDrawerOpen())
            closeDrawer();
        if (v == homeBtn) {
            setBtnStates(homeBtn);
            mNavMenuItems = NavMenu.HOME;
        } else if (v == settingsBtn) {
            setBtnStates(homeBtn);
            mNavMenuItems = NavMenu.SETTINGS;
        } else if (v == share_btn) {
//            setBtnStates(share_btn);
            mNavMenuItems = NavMenu.SHARE;
        } else if (v == logoutBtn) {
            mNavMenuItems = NavMenu.LOGOUT;
        } else if (v == historyBtn) {
            setBtnStates(historyBtn);
            mNavMenuItems = NavMenu.HISTORY;
        } else if (v == helpBtn) {
            setBtnStates(historyBtn);
            mNavMenuItems = NavMenu.HELP;
        } else if (v == summaryBtn) {
            setBtnStates(historyBtn);
            mNavMenuItems = NavMenu.SUMMARY;
        } else if (v == earningsBtn) {
            setBtnStates(historyBtn);
            mNavMenuItems = NavMenu.EARNINGS;
        }
        if (v == headerLayout) {
            mListener.headerClicked();
            mNavMenuItems = null;
        }
        if (mNavMenuItems != null && mListener != null)
            mListener.menuClicked(mNavMenuItems);

        if (v == headerLayout) {
            mListener.headerClicked();
        }
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mNavigationView);
    }

    public void closeDrawer() {
        if (mDrawerLayout != null && isDrawerOpen()) {
            mDrawerLayout.closeDrawer(mNavigationView);
        }
    }

    public void openDrawer() {
        if (mDrawerLayout != null && !isDrawerOpen()) {
            mDrawerLayout.openDrawer(mNavigationView);
        }
    }

    public void setupDrawer(int fragmentId, final DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
        mNavigationView = getActivity().findViewById(fragmentId);
        mListener.menuClicked(NavMenu.HOME);
    }

    public void checkBtnStates() {
        if (mNavMenuItems != null) {
            if (mNavMenuItems.equals(NavMenu.HOME)) {
                setBtnStates(homeBtn);
            }
            if (mNavMenuItems.equals(NavMenu.SETTINGS)) {
                setBtnStates(settingsBtn);
            }
            if (mNavMenuItems.equals(NavMenu.SHARE)) {
                setBtnStates(share_btn);
            }
            if (mNavMenuItems.equals(NavMenu.HISTORY)) {
                setBtnStates(historyBtn);
            }
            if (mNavMenuItems.equals(NavMenu.LOGOUT)) {
                setBtnStates(logoutBtn);
            }
        }
    }

    private void setBtnStates(Button button) {
        homeBtn.setSelected(button == homeBtn);
        settingsBtn.setSelected(button == settingsBtn);
        share_btn.setSelected(button == share_btn);
        historyBtn.setSelected(button == historyBtn);
        logoutBtn.setSelected(button == logoutBtn);
    }

    public void enableDisableDrawer(boolean isEnable) {
        if (mDrawerLayout != null) {
            int lockMode = (isEnable) ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
            mDrawerLayout.setDrawerLockMode(lockMode);
        }
        checkBtnStates();
    }

    public void setNavMenuItems(NavMenu navMenuItems) {
        this.mNavMenuItems = navMenuItems;
        checkBtnStates();
    }

    public void setBasicDetails() {
       /* String name = String.format(getContext().getString(R.string.name), SharedHelper.getKey(getContext(), "first_name"),
                SharedHelper.getKey(getContext(), "last_name"));
        mNameTxt.setText(name);
        String gender = SharedHelper.getKey(getContext(), "gender");
        if (gender != null && !gender.equalsIgnoreCase("null")) {
            if (gender.equalsIgnoreCase("male")) {
                Picasso.with(getContext()).load(SharedHelper.getKey(getContext(), "avatar"))
                        .error(R.drawable.man_user)
                        .centerCrop()
                        .fit()
                        .into(mUserProfileImg);
            } else if (gender.equalsIgnoreCase("female")) {
                Picasso.with(getContext()).load(SharedHelper.getKey(getContext(), "avatar"))
                        .error(R.drawable.woman_user)
                        .centerCrop()
                        .fit()
                        .into(mUserProfileImg);
            } else {
                Picasso.with(getContext()).load(SharedHelper.getKey(getContext(), "avatar"))
                        .error(R.drawable.man_user)
                        .centerCrop()
                        .fit()
                        .into(mUserProfileImg);
            }
        }*/
    }

    @Override
    public void onProfileUpdateReflect() {
        setBasicDetails();
    }

    public interface NavDrawerFgmtListener {
        void menuClicked(NavMenu navMenuItems);

        void headerClicked();

        void headerProfileClicked();
    }
}
