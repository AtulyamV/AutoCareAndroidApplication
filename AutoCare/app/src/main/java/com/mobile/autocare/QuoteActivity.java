package com.mobile.autocare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class QuoteActivity extends AppCompatActivity {

    private TextView mBookingId;
    private TextView mBookingIdValue;
    private TextView mRepairDescription;
    private TextView mRepairCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);
        mBookingId = (TextView) findViewById(R.id.bookingId);
        mBookingIdValue = (TextView) findViewById(R.id.bookingIdValue);
        mRepairDescription = (TextView) findViewById(R.id.repairDescription);
        mRepairCost = (TextView) findViewById(R.id.repairCost);

        mRepairCost.setText("\u20B9" + "1500");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

