
package me.yingyixu.scannmock.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;

    private ProductTab productTab;

    private WeiboTab weiboTab;

    private NewsTab newsTab;

    private ImgTab imgTab;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private boolean isIMG = false;

    public void setIsImg(boolean is) {
        isIMG = is;
    }

    @Override
    public Fragment getItem(int arg0) {
        switch (arg0) {

            case 0:
                if (isIMG) {
                    if (imgTab == null) {
                        imgTab = new ImgTab();
                    }
                    return imgTab;
                } else {
                    if (productTab == null) {
                        productTab = new ProductTab();
                    }
                    return productTab;
                }

            case 1:
                if (weiboTab == null) {
                    weiboTab = new WeiboTab();
                }
                return weiboTab;

            case 2:
                if (newsTab == null) {
                    newsTab = new NewsTab();
                }
                return newsTab;
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
