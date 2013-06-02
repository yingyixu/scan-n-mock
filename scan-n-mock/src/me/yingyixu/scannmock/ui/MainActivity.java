
package me.yingyixu.scannmock.ui;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpResponseHandler;

import me.yingyixu.scannmock.R;
import me.yingyixu.scannmock.http.Comments;
import me.yingyixu.scannmock.types.ImgResp;
import me.yingyixu.scannmock.types.ProductResp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends SherlockActivity implements OnClickListener {

    private ProgressDialog progressDialog;

    private Button codeBtn;

    private Button objBtn;

    private CheckBox langCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        codeBtn = (Button) findViewById(R.id.btn_qrcode);
        objBtn = (Button) findViewById(R.id.btn_object);
        langCB = (CheckBox) findViewById(R.id.checkbox);

        codeBtn.setOnClickListener(this);
        objBtn.setOnClickListener(this);

        InfoActivity.pResp = null;
        InfoActivity.cResp = null;

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.identifying_product));
    }

    public static String imgPath;

    public static String imgName;

    public static String KEY_IMG = "_img";

    @Override
    public void onClick(View v) {
        if (v == codeBtn) {
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.initiateScan();
        } else if (v == objBtn) {
            Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imgName = "snm-" + System.currentTimeMillis() + ".jpg";
            imgPath = Environment.getExternalStorageDirectory().getPath() + "/" + imgName;
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imgPath)));
            imageCaptureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
                    Configuration.ORIENTATION_PORTRAIT);
            startActivityForResult(imageCaptureIntent, CAPTURE_IMAGE);
        }
    }

    private static final int CAPTURE_IMAGE = 111;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentIntegrator.REQUEST_CODE && resultCode == RESULT_OK) {
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
                        InfoActivity.title = pResp.product;
                        Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                        intent.putExtra(KEY_IMG, 0);
                        startActivity(intent);
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
        } else if (requestCode == CAPTURE_IMAGE && resultCode == RESULT_OK) {
            progressDialog.show();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            options.inJustDecodeBounds = false;
            Bitmap tmpBMP = BitmapFactory.decodeFile(imgPath, options);

            int degree = readPictureDegree(imgPath);
            Bitmap bmp = rotaingImageView(degree, tmpBMP);

            BufferedOutputStream bos = null;
            try {
                File myCaptureFile = new File(imgPath);
                bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                bos.flush();
                bos.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                bmp.recycle();
            }

            Comments.uploadImg(imgPath, langCB.isChecked() ? "chi_sim" : "eng",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String content) {
                            super.onSuccess(content);

                            Gson gson = new Gson();
                            ImgResp imgResp = gson.fromJson(content, ImgResp.class);

                            StringBuffer sb = new StringBuffer();
                            if (imgResp.results != null) {
                                for (int i = 0; i < imgResp.results.length && i < 2; i++) {
                                    sb.append(imgResp.results[i]);
                                    sb.append(',');
                                }
                            }

                            ProductResp pResp = new ProductResp();
                            pResp.prices = new HashMap<String, String>();
                            pResp.product = sb.toString();

                            sb = new StringBuffer();
                            if (imgResp.results != null) {
                                for (int i = 0; i < imgResp.results.length; i++) {
                                    sb.append(imgResp.results[i]);
                                    sb.append(',');
                                }
                            }
                            InfoActivity.title = sb.toString();

                            progressDialog.dismiss();
                            InfoActivity.pResp = pResp;
                            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                            intent.putExtra(KEY_IMG, 1);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Throwable error, String content) {
                            super.onFailure(error, content);
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, R.string.scan_error,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }
}
