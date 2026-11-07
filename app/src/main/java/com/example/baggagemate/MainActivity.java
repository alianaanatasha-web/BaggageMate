package com.example.baggagemate;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private EditText etWeight, etRate;
    private RadioGroup rgTicketClass;
    private RadioButton rbEconomy, rbBusiness;
    private Button btnCalculate;

    private TextView tvExcessWeight;
    private TextView tvExcessCharge;
    private TextView tvServiceTax;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        setSupportActionBar(toolbar);
        // Connect Java variables to XML views
        etWeight = findViewById(R.id.etWeight);
        etRate = findViewById(R.id.etRate);

        rgTicketClass = findViewById(R.id.rgTicketClass);
        rbEconomy = findViewById(R.id.rbEconomy);
        rbBusiness = findViewById(R.id.rbBusiness);

        btnCalculate = findViewById(R.id.btnCalculate);

        tvExcessWeight = findViewById(R.id.tvExcessWeight);
        tvExcessCharge = findViewById(R.id.tvExcessCharge);
        tvServiceTax = findViewById(R.id.tvServiceTax);
        tvTotal = findViewById(R.id.tvTotal);

        // Calculate when the user taps the button
        btnCalculate.setOnClickListener(v -> calculateFee());

    }

    private void calculateFee() {

        String weightInput = etWeight.getText().toString().trim();
        String rateInput = etRate.getText().toString().trim();

        // Check for empty inputs
        if (weightInput.isEmpty()) {
            etWeight.setError("Please enter baggage weight");
            etWeight.requestFocus();
            return;
        }

        if (rateInput.isEmpty()) {
            etRate.setError("Please enter the rate per kg");
            etRate.requestFocus();
            return;
        }

        double baggageWeight;
        double rate;

        // Validate numeric inputs
        try {
            baggageWeight = Double.parseDouble(
                    weightInput.replace(',', '.'));

            rate = Double.parseDouble(
                    rateInput.replace(',', '.'));

        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "Please enter valid numbers.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Prevent negative values
        if (!Double.isFinite(baggageWeight)
                || !Double.isFinite(rate)
                || baggageWeight < 0 || rate < 0) {

            Toast.makeText(this,
                    "Weight and rate cannot be negative.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine baggage allowance
        double allowance;

        if (rbEconomy.isChecked()) {
            allowance = 20.0;
        } else if (rbBusiness.isChecked()) {
            allowance = 40.0;
        } else {
            Toast.makeText(this,
                    "Please select a ticket class.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate excess baggage weight
        double excessWeight = Math.max(
                baggageWeight - allowance, 0.0);

        // Calculate charges and 8% service tax
        double excessCharge = excessWeight * rate;
        double serviceTax = excessCharge * 0.08;
        double totalPayable = excessCharge + serviceTax;

        // Display results to two decimal places
        tvExcessWeight.setText(String.format(Locale.US,
                "Excess weight: %.2f kg", excessWeight));

        tvExcessCharge.setText(String.format(Locale.US,
                "Excess charge: RM %.2f", excessCharge));

        tvServiceTax.setText(String.format(Locale.US,
                "Service tax (8%%): RM %.2f", serviceTax));

        tvTotal.setText(String.format(Locale.US,
                "Total payable: RM %.2f", totalPayable));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_share) {

            String appUrl = "https://github.com/YOUR-USERNAME/BaggageMate";

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out BaggageMate, an app to calculate excess baggage fees!\n"
                            + appUrl
            );

            startActivity(Intent.createChooser(shareIntent, "Share BaggageMate"));
            return true;
        }

        if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
