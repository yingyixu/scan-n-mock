
package me.yingyixu.scannmock.ui;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

import me.yingyixu.scannmock.R;
import me.yingyixu.scannmock.types.CommentsResp;
import me.yingyixu.scannmock.types.ProductResp;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

public class InfoActivity extends SherlockFragmentActivity {

    public static ProductResp pResp;

    public static CommentsResp cResp;

    public static String title;

    private ActionBar mActionBar;

    private ViewPager mPager;

    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setTitle(title);

        mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mActionBar.setSelectedNavigationItem(position);
            }
        });
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
                // do nothing
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
                // do nothing
            }
        };

        ActionBar.Tab tab = getSupportActionBar().newTab();

        if (getIntent().getExtras().getInt(MainActivity.KEY_IMG, 0) > 0) {
            mAdapter.setIsImg(true);

            tab.setText("Images");
            tab.setTabListener(tabListener);
            mActionBar.addTab(tab);
        } else {
            tab.setText("Price");
            tab.setTabListener(tabListener);
            mActionBar.addTab(tab);
        }

        tab = getSupportActionBar().newTab();
        tab.setText("Weibo");
        tab.setTabListener(tabListener);
        mActionBar.addTab(tab);

        tab = getSupportActionBar().newTab();
        tab.setText("News");
        tab.setTabListener(tabListener);
        mActionBar.addTab(tab);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
