package com.mobile.autocare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobile.autocare.constants.Constants;
import com.mobile.autocare.httpclient.AutoCareHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class PhotoUploadActivity extends AppCompatActivity {

    private TextView mSelectedValue;
    private Button mUploadButton;
    private FloatingActionButton mCamera;
    private Activity currentActivity;
    private GridView gridview;
    ImageAdapter imageAdapter;
    private int count = 0;
    private Uri fileUri;
    private String picturePath;
    private Uri selectedImage;
    private String imageData1;
    private String imageData2;
    private String imageData3;
    private String imageData4;
    private ProgressDialog progressDialog;
    SharedPreferences autoCarePreferences;

    private String mCurrentPhotoPath;
    private String imageFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
        setContentView(R.layout.activity_photo_upload);
        mSelectedValue = (TextView) findViewById(R.id.selectedValue);
        String selectedValue = "";
        String value = "";
        autoCarePreferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);

        if (getIntent() != null && getIntent().getExtras() != null) {
            selectedValue = getIntent().getExtras().getString("selected");
        }

        if (selectedValue.equalsIgnoreCase("DENT")) {
            value = "Upload Photos for Dent";
        } else if (selectedValue.equalsIgnoreCase("PAINT")) {
            value = "Upload Photos for Paint";
        } else if (selectedValue.equalsIgnoreCase("QUOTE")) {
            value = "Get Quote details";
        } else {
            value = "Upload Photos";
        }
        mSelectedValue.setText(value);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridview = (GridView) findViewById(R.id.gridView);

        mUploadButton = (Button) findViewById(R.id.upload);
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(PhotoUploadActivity.this);
                progressDialog.setMax(100);
                progressDialog.setMessage(Constants.UPLOAD_LOADING_MESSAGE);
                progressDialog.show();
                uploadPhotos();
            }
        });
        mCamera = (FloatingActionButton) findViewById(R.id.newUser);
        try {
            fileUri = Uri.fromFile(createImageFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, 0);
            }
        });

        imageAdapter = new ImageAdapter(this);
        gridview.setAdapter(imageAdapter);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_image.jpg";

        File file = new File(Environment.getExternalStorageDirectory()+File.separator + imageFileName);
        return file;
    }


    private String convertToBase64String(String picturePath, Bitmap bm) {
        String base64Image = null;
        StringBuilder sb = new StringBuilder();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        base64Image = Base64.encodeToString(ba, Base64.NO_WRAP);
        sb.append(base64Image);
        return sb.toString();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                File file = new File(Environment.getExternalStorageDirectory()+File.separator + imageFileName);

                Bitmap capturedBitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 200, 200);
                picturePath = file.getAbsolutePath();

                if (count == 0) {
                    imageAdapter.mBitmaps[0] = capturedBitmap;
                    imageData1 = convertToBase64String(picturePath, capturedBitmap);
                    count++;
                } else if (count == 1) {
                    imageAdapter.mBitmaps[1] = capturedBitmap;
                    imageData2 = convertToBase64String(picturePath, capturedBitmap);
                    count++;
                } else if (count == 2) {
                    imageAdapter.mBitmaps[2] = capturedBitmap;
                    imageData3 = convertToBase64String(picturePath, capturedBitmap);
                    count++;
                } else if (count == 3) {
                    imageAdapter.mBitmaps[3] = capturedBitmap;
                    imageData4 = convertToBase64String(picturePath, capturedBitmap);
                    count++;
                }
                gridview.setAdapter(imageAdapter);

                if (count == 4) {
                    mCamera.setVisibility(View.GONE);
                    mUploadButton.setVisibility(View.VISIBLE);
                }
            } else if (resultCode == RESULT_CANCELED) {
                // DO NOTHING
            }
        }


    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    {
        Bitmap capturedImage = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);


        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        options.inJustDecodeBounds = false;



        capturedImage = BitmapFactory.decodeFile(path, options);


       ExifInterface ei = null;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);


            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    capturedImage = rotateImage(capturedImage, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    capturedImage = rotateImage(capturedImage, 180);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return capturedImage;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void uploadPhotos() {


        JSONObject uploadImage = new JSONObject();
        StringEntity entity = null;

        try {
            uploadImage.put("userId", autoCarePreferences.getString(Constants.PREF_MOBILE, ""));
            uploadImage.put("imageData1", imageData1);
            uploadImage.put("imageData2", imageData2);
            uploadImage.put("imageData3", imageData3);
            uploadImage.put("imageData4", imageData4);

                entity = new StringEntity(uploadImage.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!Constants.APP_DEMO_MODE) {
            AutoCareHttpClient.post(getApplicationContext(), "UploadImageService/upload", entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.cancel();
                    try {

                        if (response.getString("status").equals(Constants.SUCCESS)) {
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.putExtra("carName", autoCarePreferences.getString(Constants.PREF_CAR_NAME, ""));
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Issue in Uploading images", Toast.LENGTH_LONG)
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Issue in Uploading images", Toast.LENGTH_LONG)
                                .show();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(),
                            "Issue in Uploading images", Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(),
                            "Issue in Uploading images", Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(),
                            "Issue in Uploading images", Toast.LENGTH_LONG)
                            .show();
                }

            });
        } else {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }
            }, 1000);

        }

    }

}
