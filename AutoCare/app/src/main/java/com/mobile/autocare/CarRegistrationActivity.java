package com.mobile.autocare;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobile.autocare.constants.Constants;
import com.mobile.autocare.httpclient.AutoCareHttpClient;
import com.mobile.autocare.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class CarRegistrationActivity extends AppCompatActivity {

    private EditText mCarManufactureView;
    private String carName;
    private String carFuelType;
    private String carBodyType;
    private ProgressDialog progressDialog;
    private ImageButton mCarManufactureButton;
    private AppCompatSpinner carFuelTypeSpinner;
    private AppCompatSpinner carNameSpinner;
    private ArrayList<String> carNames;
    Util util;

    SharedPreferences autoCarePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        util = new Util(this);
        mCarManufactureView = (EditText) findViewById(R.id.carmanufacturetext);
        autoCarePreferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        mCarManufactureButton = (ImageButton) findViewById(R.id.carmanufacturebutton);

        mCarManufactureButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        if(getIntent() != null && getIntent().getExtras() != null) {
            carNames = getIntent().getStringArrayListExtra("carName");
        }

        Button mCreateAccountButton = (Button) findViewById(R.id.create_account_button);
        mCreateAccountButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if(util.isConnected()) {

                    progressDialog = new ProgressDialog(CarRegistrationActivity.this);
                    progressDialog.setMax(100);
                    progressDialog.setMessage(Constants.CREATE_LOADING_MESSAGE);
                    progressDialog.show();
                    attemptCreateAccount();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please connect your device to internet connection", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });


        carFuelTypeSpinner = (AppCompatSpinner) findViewById(R.id.carfueltype);
        carFuelTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carFuelType = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> carFuelTypes = new ArrayList<String>();
        carFuelTypes.add("Petrol");
        carFuelTypes.add("Diesel");

        ArrayAdapter<String> carFuelTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, carFuelTypes);
        carFuelTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carFuelTypeSpinner.setAdapter(carFuelTypeAdapter);

        carNameSpinner = (AppCompatSpinner) findViewById(R.id.carFullName);
        carNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carName = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> carNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, carNames);
        carNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carNameSpinner.setAdapter(carNameAdapter);



        AppCompatSpinner carBodySpinner = (AppCompatSpinner) findViewById(R.id.carbodytype);
        carBodySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carBodyType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> carBodyTypes = new ArrayList<String>();
        carBodyTypes.add("Hatchback");
        carBodyTypes.add("Sedan");
        carBodyTypes.add("SUV/MUV");
        carBodyTypes.add("Truck");
        carBodyTypes.add("Minivan/Van");
        carBodyTypes.add("Station Wagon");
        carBodyTypes.add("Coupe");
        carBodyTypes.add("Convertible");

        ArrayAdapter<String> carBodyTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, carBodyTypes);
        carBodyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carBodySpinner.setAdapter(carBodyTypeAdapter);
    }

    private void attemptCreateAccount() {
        String carManufactureYear = mCarManufactureView.getText().toString();
        boolean cancel = false;

        if (cancel) {
            progressDialog.cancel();
        } else {
            SharedPreferences.Editor autoCareEditor = autoCarePreferences.edit();
            autoCareEditor.putString(Constants.PREF_CAR_NAME,carName);
            autoCareEditor.putString(Constants.PREF_CAR_FUEL_TYPE,carFuelType);
            autoCareEditor.putString(Constants.PREF_CAR_BODY_TYPE,carBodyType);
            autoCareEditor.putString(Constants.PREF_CAR_MANUFACTURE_YEAR,carManufactureYear);
            autoCareEditor.commit();
            createAccount();
        }
    }


    private void createAccount () {

        JSONObject user = new JSONObject();
        JSONObject address = new JSONObject();
        JSONObject car = new JSONObject();
        StringEntity entity = null;
        try {
            user.put("mobileNo", autoCarePreferences.getString(Constants.PREF_MOBILE,""));
            user.put("password", autoCarePreferences.getString(Constants.PREF_PASSWORD,""));
            user.put("firstName", autoCarePreferences.getString(Constants.PREF_FIRST_NAME,""));
            user.put("lastName", autoCarePreferences.getString(Constants.PREF_LAST_NAME,""));
            user.put("email", autoCarePreferences.getString(Constants.PREF_EMAIL,""));
            address.put("buildingName", autoCarePreferences.getString(Constants.PREF_BUILDING_NAME,""));
            address.put("locality", autoCarePreferences.getString(Constants.PREF_LOCALITY_NAME,""));
            address.put("city", autoCarePreferences.getString(Constants.PREF_CITY,""));
            address.put("pinCode", autoCarePreferences.getString(Constants.PREF_PIN_CODE,""));
            car.put("carName", autoCarePreferences.getString(Constants.PREF_CAR_NAME,""));
            car.put("carFuelType", autoCarePreferences.getString(Constants.PREF_CAR_FUEL_TYPE,""));
            car.put("carBodyType", autoCarePreferences.getString(Constants.PREF_CAR_BODY_TYPE,""));
            car.put("carManufactureYear", autoCarePreferences.getString(Constants.PREF_CAR_MANUFACTURE_YEAR,""));
            user.put("address",address);
            user.put("car",car);
            entity = new StringEntity(user.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!Constants.APP_DEMO_MODE) {

            AutoCareHttpClient.post(getApplicationContext(), "UserService/user", entity, "application/json", new JsonHttpResponseHandler() {
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
                                    "Issue in Creating Account", Toast.LENGTH_LONG)
                                    .show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),
                                "Issue in Creating Account", Toast.LENGTH_LONG)
                                .show();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(),
                            "Issue in Creating Account", Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(),
                            "Issue in Creating Account", Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(),
                            "Issue in Creating Account", Toast.LENGTH_LONG)
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
                    intent.putExtra("carName", autoCarePreferences.getString(Constants.PREF_CAR_NAME, ""));
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
}

