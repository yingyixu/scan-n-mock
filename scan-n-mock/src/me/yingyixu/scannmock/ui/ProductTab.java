
package me.yingyixu.scannmock.ui;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;

import me.yingyixu.scannmock.R;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class ProductTab extends SherlockFragment {

    private LoadMoreListView priceList;

    private PriceAdapter priceAdapter;

    @Override
    public SherlockFragmentActivity getSherlockActivity() {
        return super.getSherlockActivity();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price, container, false);

        priceList = (LoadMoreListView) view.findViewById(R.id.prices_list);
        priceAdapter = new PriceAdapter(getActivity());
        priceList.setAdapter(priceAdapter);
        priceList.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                curPage++;
                onLoadPage(curPage);
            }
        });

        start(InfoActivity.pResp.product);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

    public void start(String product) {
        priceAdapter.setPrices(InfoActivity.pResp.prices);
    }

    private int curPage = 1;

    private void onLoadPage(int page) {
        // TODO
    }

}

class PriceAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    public PriceAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    private Map<String, String> prices = new HashMap<String, String>();

    public void setPrices(Map<String, String> prcs) {
        if (prcs == null) {
            prices.clear();
        } else {
            prices = prcs;
        }
    }

    public void appendComments(Map<String, String> prcs) {
        if (prcs != null) {
            prices.putAll(prcs);
        }
    }

    @Override
    public int getCount() {
        return prices.size();
    }

    @Override
    public String getItem(int position) {
        return prices.get(prices.keySet().toArray()[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.price_item_layout, null);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.bindPrice((String) prices.keySet().toArray()[position], getItem(position));

        return convertView;
    }

    private class ViewHolder {

        private TextView storeTV;

        private TextView priceTV;

        public ViewHolder(View holder) {
            storeTV = (TextView) holder.findViewById(R.id.tv_store);
            priceTV = (TextView) holder.findViewById(R.id.tv_price);
        }

        public void bindPrice(String store, String price) {
            storeTV.setText(store);
            priceTV.setText(price);
        }
    }

}
