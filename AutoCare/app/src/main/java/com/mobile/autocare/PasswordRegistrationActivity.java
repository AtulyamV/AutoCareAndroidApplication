package com.mobile.autocare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.mobile.autocare.constants.Constants;
import com.mobile.autocare.util.PasswordUtil;


public class PasswordRegistrationActivity extends AppCompatActivity {

    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private CheckBox mShowPasswordView;
    private CheckBox mConfirmShowPasswordView;

    SharedPreferences autoCarePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPasswordView = (EditText) findViewById(R.id.password);
        mConfirmPasswordView = (EditText) findViewById(R.id.confirmPassword);
        mShowPasswordView = (CheckBox) findViewById(R.id.showPasswordCheckBox);
        mConfirmShowPasswordView = (CheckBox) findViewById(R.id.confirmPasswordCheckBox);

        autoCarePreferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);

        mPasswordView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mPasswordView, MotionEvent event) {
                mPasswordView.setFocusable(true);
                mPasswordView.setFocusableInTouchMode(true);
                return false;
            }
        });

        mShowPasswordView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            boolean isChecked = ((CheckBox) view).isChecked();

            if (isChecked) {
                mPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mPasswordView.setSelection(mPasswordView.getText().length());

            } else {
                mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mPasswordView.setSelection(mPasswordView.getText().length());
            }
            }
        });

        mConfirmPasswordView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mConfirmPasswordView, MotionEvent event) {
            mConfirmPasswordView.setFocusable(true);
            mConfirmPasswordView.setFocusableInTouchMode(true);
            return false;
            }
        });

        mConfirmShowPasswordView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            boolean isChecked = ((CheckBox) view).isChecked();

            if (isChecked) {
                mConfirmPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mConfirmPasswordView.setSelection(mConfirmPasswordView.getText().length());

            } else {
                mConfirmPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mConfirmPasswordView.setSelection(mConfirmPasswordView.getText().length());
            }

            }
        });

        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.mobileRegistration || id == EditorInfo.IME_NULL) {
                    checkPasswordRegDetails();
                    return true;
                }
                return false;
            }
        });

        Button mMobileRegButton = (Button) findViewById(R.id.mobile_registration_next_button);
        mMobileRegButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {checkPasswordRegDetails();
            }
        });

    }

    private void checkPasswordRegDetails() {
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if(!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_field_required));
            if(!cancel) {
                focusView = mConfirmPasswordView;
                cancel = true;
            }
        } else if(!isPasswordValid(confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_invalid_password));
            if(!cancel) {
                focusView = mConfirmPasswordView;
                cancel = true;
            }
        } else if (!passwordsValidation(password, confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_password_are_not_equal));
            if(!cancel)  {
                focusView = mConfirmPasswordView;
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
            focusView.setFocusableInTouchMode(true);
            showKeypad();
        } else {
            hideKeypad();
            String securePassword = PasswordUtil.getSecurePassword(password);
            SharedPreferences.Editor autoCareEditor = autoCarePreferences.edit();
            autoCareEditor.putString(Constants.PREF_PASSWORD,securePassword);
            autoCareEditor.commit();

            Intent intent = new Intent(this, AddressRegistrationActivity.class);
            startActivity(intent);

        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean passwordsValidation(String password, String confirmPassword) {
        return password.equals(confirmPassword);
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

