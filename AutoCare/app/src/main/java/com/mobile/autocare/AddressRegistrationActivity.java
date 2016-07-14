package com.mobile.autocare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobile.autocare.constants.Constants;

public class AddressRegistrationActivity extends AppCompatActivity {

    private EditText mBuildingView;
    private EditText mAreaView;
    private EditText mCityView;
    private EditText mPinCodeView;

    SharedPreferences autoCarePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBuildingView = (EditText) findViewById(R.id.building);
        mAreaView = (EditText) findViewById(R.id.area);
        mCityView = (EditText) findViewById(R.id.city);
        mPinCodeView = (EditText) findViewById(R.id.pincode);

        autoCarePreferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);

        mBuildingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mBuildingView, MotionEvent event) {
                mBuildingView.setFocusable(true);
                mBuildingView.setFocusableInTouchMode(true);
                return false;
            }
        });

        mAreaView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mAreaView, MotionEvent event) {
                mAreaView.setFocusable(true);
                mAreaView.setFocusableInTouchMode(true);
                return false;
            }
        });

        mCityView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mCityView, MotionEvent event) {
                mCityView.setFocusable(true);
                mCityView.setFocusableInTouchMode(true);
                return false;
            }
        });
        mPinCodeView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mPinCodeView, MotionEvent event) {
                mPinCodeView.setFocusable(true);
                mPinCodeView.setFocusableInTouchMode(true);
                return false;
            }
        });

        mPinCodeView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.addressRegistration || id == EditorInfo.IME_NULL) {
                    checkUserAddress();
                    return true;
                }
                return false;
            }
        });

        Button mUserAddressRegButton = (Button) findViewById(R.id.address_registration_next_button);
        Button mUserAddressSkipButton = (Button) findViewById(R.id.address_registration_skip_button);

        mUserAddressRegButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUserAddress();
            }
        });

        mUserAddressSkipButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeypad();
                Intent intent = new Intent(getApplicationContext(), CarBrandActivity.class);
                startActivity(intent);
            }
        });

    }
    private void checkUserAddress() {

        mBuildingView.setError(null);
        mAreaView.setError(null);
        mCityView.setError(null);
        mPinCodeView.setError(null);

        String building = mBuildingView.getText().toString();
        String area = mAreaView.getText().toString();
        String city = mCityView.getText().toString();
        String pincode = mPinCodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(building)) {
            mBuildingView.setError(getString(R.string.error_field_required));
            focusView = mBuildingView;
            cancel = true;
        }

        if (TextUtils.isEmpty(area)) {
            mAreaView.setError(getString(R.string.error_field_required));
            if (!cancel) {
                focusView = mAreaView;
                cancel = true;
            }
        }

        if (TextUtils.isEmpty(city)) {
            mCityView.setError(getString(R.string.error_field_required));
            if (!cancel) {
                focusView = mCityView;
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
            focusView.setFocusableInTouchMode(true);
            showKeypad();
        } else {
            hideKeypad();
            SharedPreferences.Editor autoCareEditor = autoCarePreferences.edit();
            autoCareEditor.putString(Constants.PREF_BUILDING_NAME,building);
            autoCareEditor.putString(Constants.PREF_LOCALITY_NAME,area);
            autoCareEditor.putString(Constants.PREF_CITY,city);
            autoCareEditor.putString(Constants.PREF_PIN_CODE,pincode);
            autoCareEditor.commit();
            Intent intent = new Intent(this, CarBrandActivity.class);
            startActivity(intent);
      }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            hideKeypad();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showKeypad() {
        InputMethodManager inputMgrObj = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMgrObj.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideKeypad() {
        InputMethodManager inputMgrObj = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        inputMgrObj.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideKeypad();
    }
}
