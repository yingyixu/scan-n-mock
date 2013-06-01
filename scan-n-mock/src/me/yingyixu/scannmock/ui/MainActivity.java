
package me.yingyixu.scannmock.ui;

import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import me.yingyixu.scannmock.R;
import me.yingyixu.scannmock.http.Comments;
import me.yingyixu.scannmock.types.CommentsResp;
import me.yingyixu.scannmock.types.ProductResp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private LoadMoreListView commentsList;

    private CommentsAdapter commentsAdapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commentsList = (LoadMoreListView) findViewById(R.id.comments_list);
        commentsAdapter = new CommentsAdapter(this);
        commentsList.setAdapter(commentsAdapter);
        commentsList.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                curPage++;
                onLoadPage(curPage);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.identifying_product));

        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.initiateScan();
    }

    private String mProduct;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,
                data);
        if (scanResult != null) {

            progressDialog.show();

            final String code = scanResult.getContents();
            Comments.getProduct(code, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String content) {
                    super.onSuccess(content);

                    Gson gson = new Gson();
                    ProductResp pResp = gson.fromJson(content, ProductResp.class);

                    mProduct = pResp.product;
                    setTitle(mProduct);
                    progressDialog.setMessage(String.format(getString(R.string.getting_cmt),
                            mProduct));

                    curPage = 1;
                    onLoadPage(curPage);
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                    onError(error, content);
                }
            });
        }

    }

    private void onError(Throwable error, String content) {
        if (curPage == 1) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(this, R.string.scan_error, Toast.LENGTH_LONG).show();
        } else {
            commentsList.onLoadMoreComplete();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
