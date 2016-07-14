package com.mobile.autocare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobile.autocare.constants.Constants;
import com.mobile.autocare.httpclient.AutoCareHttpClient;
import com.mobile.autocare.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CarBrandActivity extends AppCompatActivity {

    private GridView gridview;
    private CarImageAdapter carImageAdapter;
    private ProgressDialog progressDialog;
    SharedPreferences autoCarePreferences;
    ArrayList<String> carBrands;
    Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_brand);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        util = new Util(this);
        gridview = (GridView) findViewById(R.id.gridView);
        autoCarePreferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        carImageAdapter = new CarImageAdapter(this);
        gridview.setAdapter(carImageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (util.isConnected()) {
                    progressDialog = new ProgressDialog(CarBrandActivity.this);
                    progressDialog.setMax(100);
                    progressDialog.setMessage(Constants.CAR_NAME_LOADING_MESSAGE);
                    progressDialog.show();
                    String carName = Constants.carbrands[position];
                    getCarDetails(carName);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please connect your device to internet connection", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });
    }

    private void getCarDetails (String carBrandName) {

        if (!Constants.APP_DEMO_MODE) {

            AutoCareHttpClient.get("CarService/car/"+carBrandName, null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.cancel();
                    try {

                        if (response.getString("status").equals(Constants.SUCCESS)) {
                            JSONArray carArray = response.getJSONArray("carBrand");
                            carBrands = getListFromJsonArray(carArray);
                            Intent intent = new Intent(getApplicationContext(), CarRegistrationActivity.class);
                            intent.putStringArrayListExtra("carName", carBrands);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Issue in fetching Car Brand details", Toast.LENGTH_LONG)
                                    .show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),
                                "Issue in fetching Car Brand details", Toast.LENGTH_LONG)
                                .show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(),
                            "Issue in fetching Car Brand details", Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(),
                            "Issue in fetching Car Brand details", Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(),
                            "Issue in fetching Car Brand details", Toast.LENGTH_LONG)
                            .show();
                }
            });
        } else {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                    ArrayList<String> carNames = new ArrayList<String>();
                    carNames.add("Spark");
                    carNames.add("Beat");
                    carNames.add("Sail Hatchback");
                    carNames.add("Sail");
                    carNames.add("Cruze");
                    carNames.add("Enjoy");
                    carNames.add("Tavera");
                    carNames.add("Trailblazer");
                    Intent intent = new Intent(getApplicationContext(), CarRegistrationActivity.class);
                    intent.putStringArrayListExtra("carName", carNames);
                    startActivity(intent);
                }
            }, 1000);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<String> getListFromJsonArray(JSONArray jArray) throws JSONException {
        ArrayList<String> returnList = new ArrayList<String>();
        for (int i = 0; i < jArray.length(); i++) {
            returnList.add(jArray.getString(i));
        }
        return returnList;
    }
}
