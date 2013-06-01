
package me.yingyixu.scannmock.ui;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;

import me.yingyixu.scannmock.R;
import me.yingyixu.scannmock.http.Comments;
import me.yingyixu.scannmock.types.News;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NewsTab extends SherlockFragment {

    private ProgressDialog progressDialog;

    private LoadMoreListView commentsList;

    private NewsAdapter newsAdapter;

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
        // Get the view from fragmenttab3.xml
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        commentsList = (LoadMoreListView) view.findViewById(R.id.news_list);
        newsAdapter = new NewsAdapter(getActivity());
        commentsList.setAdapter(newsAdapter);
        commentsList.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                curPage++;
                onLoadPage(curPage);
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        start(InfoActivity.pResp.product);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

    private String mProduct;

    public void start(String product) {
        mProduct = product;
        // progressDialog.setMessage(String.format(getString(R.string.getting_cmt),
        // product));
        // progressDialog.show();

        curPage = 1;
        onLoadPage(curPage);
    }

    private int curPage = 1;

    private void onLoadPage(int page) {
        Comments.getNews(mProduct, page, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                onError(error, content);
            }

            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);

                Gson gson = new Gson();
                ArrayList<News> newsList = new ArrayList<News>();

                newsList = gson.fromJson(content, new TypeToken<ArrayList<News>>() {
                }.getType());

                if (curPage == 1) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    newsAdapter.setNews(newsList);
                    newsAdapter.notifyDataSetChanged();
                } else {
                    newsAdapter.appendNews(newsList);
                    newsAdapter.notifyDataSetChanged();
                    commentsList.onLoadMoreComplete();
                }
            }

        });
    }

    private void onError(Throwable error, String content) {
        if (curPage == 1) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(getActivity(), R.string.scan_error, Toast.LENGTH_LONG).show();
        } else {
            commentsList.onLoadMoreComplete();
        }
    }

}

class NewsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    public NewsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    private ArrayList<News> newsList = new ArrayList<News>();

    public void setNews(ArrayList<News> news) {
        if (news == null) {
            newsList.clear();
        } else {
            newsList = news;
        }
    }

    public void appendNews(ArrayList<News> news) {
        if (news != null) {
            newsList.addAll(news);
        }
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public News getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.news_item_layout, null);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.bindNews(getItem(position));

        return convertView;
    }

    private class ViewHolder {

        private TextView title;

        private TextView source;

        private View root;

        public ViewHolder(View holder) {
            title = (TextView) holder.findViewById(R.id.tv_title);
            source = (TextView) holder.findViewById(R.id.tv_source);
            root = holder;
        }

        public void bindNews(News news) {
            title.setText(news.title);
            source.setText(news.source);

            root.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                }
            });
        }
    }

}
