package com.mobile.autocare;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobile.autocare.constants.Constants;
import com.mobile.autocare.httpclient.AutoCareHttpClient;
import com.mobile.autocare.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText mRegisteredEmail;
    private Button mForgotPwdSubmitButton;
    private ProgressDialog progressDialog;
    Util util;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pwd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        util = new Util(this);
        mRegisteredEmail = (EditText)findViewById(R.id.forgot_pwd_email);
        mRegisteredEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mMobileView, MotionEvent event) {
                mMobileView.setFocusable(true);
                mMobileView.setFocusableInTouchMode(true);
                return false;
            }
        });

        mRegisteredEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.forgot_pwd_submit_button || id == EditorInfo.IME_NULL) {
                    if (util.isConnected()) {
                        hideKeypad();
                        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
                        progressDialog.setMax(100);
                        progressDialog.setMessage(Constants.RESET_PWD_LOADING_MESSAGE);
                        progressDialog.show();
                        validateForm();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please connect your device to internet connection", Toast.LENGTH_LONG)
                                .show();
                    }
                    return true;
                }
                return false;
            }
        });
        mForgotPwdSubmitButton = (Button) findViewById(R.id.forgot_pwd_submit_button);
        mForgotPwdSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (util.isConnected()) {
                    progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
                    progressDialog.setMax(100);
                    progressDialog.setMessage(Constants.RESET_PWD_LOADING_MESSAGE);
                    progressDialog.show();
                    validateForm();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please connect your device to internet connection", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    public void validateForm() {
        String regEmail = mRegisteredEmail.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(regEmail)) {
            mRegisteredEmail.setError(getString(R.string.error_field_required));
            focusView = mRegisteredEmail;
            cancel = true;
        } else if (!isEmailValid(regEmail)) {
            mRegisteredEmail.setError(getString(R.string.error_invalid_email));
            focusView = mRegisteredEmail;
            cancel = true;
        }

        if (cancel) {
            progressDialog.cancel();
            focusView.requestFocus();
            focusView.setFocusableInTouchMode(true);
            showKeypad();
        } else {
            triggerForgotPassword(regEmail);
        }
    }

    private void triggerForgotPassword(String regEmail) {
        hideKeypad();
        JSONObject user = new JSONObject();
        StringEntity entity = null;

        try {
            user.put("email", regEmail);
            entity = new StringEntity(user.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!Constants.APP_DEMO_MODE) {

            AutoCareHttpClient.post(getApplicationContext(), "UserService/resetPassword", entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.cancel();
                    try {

                        if (response.getString("status").equals(Constants.SUCCESS)) {
                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Issue in Resetting Password", Toast.LENGTH_LONG)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Issue in Resetting Password", Toast.LENGTH_LONG)
                                .show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    handleFailure(e);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(throwable);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    handleFailure(throwable);
                }
            });
        } else {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(intent);
                }
            }, 1000);
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
    private boolean isEmailValid(String email) {
        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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

    private  void handleFailure (Throwable e) {
        progressDialog.cancel();
        String className = e.getClass().getName();
        String toastMessage;
        if (className != null && className.indexOf("ConnectTimeoutException")!= -1) {
            toastMessage = "Please connect your device to internet connection";
        } else {
            toastMessage = "Issue in Resetting Password";
        }
        Toast.makeText(getApplicationContext(),
                toastMessage, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideKeypad();
    }
}

