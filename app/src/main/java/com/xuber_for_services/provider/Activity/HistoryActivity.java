package com.xuber_for_services.provider.Activity;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.xuber_for_services.provider.Fragments.OnGoingTrips;
import com.xuber_for_services.provider.Fragments.PastTrips;
import com.xuber_for_services.provider.R;


public class HistoryActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        backArrow = (ImageView) findViewById(R.id.backArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[]{"Past", "Upcoming"};
        private Context context;

        public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PastTrips();
                case 1:
                    return new OnGoingTrips();
                default:
                    return new PastTrips();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

}
