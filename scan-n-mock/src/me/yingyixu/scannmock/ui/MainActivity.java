
package me.yingyixu.scannmock.ui;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpResponseHandler;

import me.yingyixu.scannmock.R;
import me.yingyixu.scannmock.http.Comments;
import me.yingyixu.scannmock.types.ProductResp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends SherlockActivity implements OnClickListener {

    private ProgressDialog progressDialog;

    private Button codeBtn;

    private Button objBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        codeBtn = (Button) findViewById(R.id.btn_qrcode);
        objBtn = (Button) findViewById(R.id.btn_object);

        codeBtn.setOnClickListener(this);
        objBtn.setOnClickListener(this);

        InfoActivity.pResp = null;
        InfoActivity.cResp = null;

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.identifying_product));
    }

    @Override
    public void onClick(View v) {
        if (v == codeBtn) {
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.initiateScan();
        } else if (v == objBtn) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,
                data);
        if (scanResult != null) {
            final String code = scanResult.getContents();
            progressDialog.show();
            Comments.getProduct(code, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String content) {
                    super.onSuccess(content);

                    Gson gson = new Gson();
                    ProductResp pResp = gson.fromJson(content, ProductResp.class);

                    progressDialog.dismiss();
                    InfoActivity.pResp = pResp;
                    startActivity(new Intent(MainActivity.this, InfoActivity.class));
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, R.string.scan_error, Toast.LENGTH_LONG)
                            .show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
