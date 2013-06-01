
package me.yingyixu.scannmock.ui;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import me.yingyixu.scannmock.R;
import me.yingyixu.scannmock.http.Comments;
import me.yingyixu.scannmock.types.CommentsResp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class WeiboTab extends SherlockFragment {

    private ProgressDialog progressDialog;

    private LoadMoreListView commentsList;

    private CommentsAdapter commentsAdapter;

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
        View view = inflater.inflate(R.layout.fragment_weibo, container, false);

        commentsList = (LoadMoreListView) view.findViewById(R.id.comments_list);
        commentsAdapter = new CommentsAdapter(getActivity());
        commentsList.setAdapter(commentsAdapter);
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
        Comments.getCommnets(mProduct, page, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                onError(error, content);
            }

            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);

                Gson gson = new Gson();
                CommentsResp cResp = gson.fromJson(content, CommentsResp.class);

                if (curPage == 1) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    commentsAdapter.setComments(cResp.comments);
                    commentsAdapter.notifyDataSetChanged();
                } else {
                    commentsAdapter.appendComments(cResp.comments);
                    commentsAdapter.notifyDataSetChanged();
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

class CommentsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    public CommentsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    private ArrayList<CommentsResp.Comment> comments = new ArrayList<CommentsResp.Comment>();

    public void setComments(ArrayList<CommentsResp.Comment> cmts) {
        if (cmts == null) {
            comments.clear();
        } else {
            comments = cmts;
        }
    }

    public void appendComments(ArrayList<CommentsResp.Comment> cmts) {
        if (cmts != null) {
            comments.addAll(cmts);
        }
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public CommentsResp.Comment getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.comment_item_layout, null);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.bindComment(getItem(position));

        return convertView;
    }

    private class ViewHolder {

        private ImageView avatar;

        private TextView nick;

        private TextView time;

        private TextView content;

        private TextView source;

        private ImageView pic;

        public ViewHolder(View holder) {
            avatar = (ImageView) holder.findViewById(R.id.img_avatar);
            nick = (TextView) holder.findViewById(R.id.tv_nick);
            time = (TextView) holder.findViewById(R.id.tv_time);
            content = (TextView) holder.findViewById(R.id.tv_content);
            source = (TextView) holder.findViewById(R.id.tv_source);
            pic = (ImageView) holder.findViewById(R.id.iv_pic);
        }

        public void bindComment(CommentsResp.Comment cmt) {
            nick.setText(cmt.user.screen_name);
            time.setText(cmt.time);
            content.setText(Html.fromHtml(cmt.content).toString());
            source.setText(cmt.source);

            if (!TextUtils.isEmpty(cmt.user.avatar)) {
                avatar.setImageResource(R.drawable.ic_launcher);
                ImageLoader.getInstance().displayImage(cmt.user.avatar, avatar);
            }
            if (!TextUtils.isEmpty(cmt.pic)) {
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(0);
                ImageLoader.getInstance().displayImage(cmt.pic, pic);
            } else {
                pic.setVisibility(View.GONE);
            }
        }
    }

}
