package garg.sarthik.clipboard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AuthActivity extends AppCompatActivity {

    private final String TAG = "Auth";
    private SharedPreferences sharedPreferences;
    private boolean isAvail = false;
    private boolean isChange = false;
    private String pin = "";
    private String beforeString;

    private TextView tvEnter;
    private TextView tvCreate;
    private EditText etPin;
    private Button btnChange;

    @Override
    protected void onStart() {
        super.onStart();

        Statics.currentActivity = "auth";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        boilerPlate();

        if (getIntent() != null) {
            isAvail = getIntent().getBooleanExtra("change", false);
        }

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        if (sharedPreferences.contains("pin") && !isAvail) {
            tvCreate.setVisibility(View.GONE);
            tvEnter.setVisibility(View.VISIBLE);
            btnChange.setText("Change Your PIN");
            pin = sharedPreferences.getString("pin", "");
            isAvail = true;
        } else {
            tvCreate.setVisibility(View.VISIBLE);
            tvEnter.setVisibility(View.GONE);
            btnChange.setText("Submit PIN");
            isAvail = false;
        }

        etPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeString = s.toString().trim();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(TAG, "beforeTextChanged: " + count);
                if (s.toString().trim().length() == 4) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    //Find the currently focused view, so we can grab the correct window token from it.
                    View view = getCurrentFocus();
                    //If no view currently has focus, create a new one, just so we can grab a window token from it
                    if (view == null) {
                        view = new View(getBaseContext());
                    }
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    if (isAvail)
                        enterPin(s.toString().trim());

                } else {
                    if (s.toString().trim().length() > 4) {
                        etPin.setText(beforeString);
                        Toast.makeText(AuthActivity.this, "PIN must have a length of 4", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input = etPin.getText().toString().trim();


                if (!isAvail) {

                    if (input.length() != 4) {
                        Toast.makeText(AuthActivity.this, "Please enter a valid PIN", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    sharedPreferences.edit().putString("pin", input).apply();
                    startActivity(new Intent(AuthActivity.this, MainActivity.class).putExtra("hide", true));
                    finish();

                } else {

                    if (!isChange) {
                        isChange = true;
                        Toast.makeText(AuthActivity.this, "Please enter your current PIN", Toast.LENGTH_SHORT).show();
                        btnChange.setText("Cancel");
                    } else {
                        isChange = false;
                        btnChange.setText("Change Your PIN");
                    }
                }

            }
        });

    }

    private void boilerPlate() {
        tvCreate = findViewById(R.id.tvCreate);
        tvEnter = findViewById(R.id.tvEnter);
        etPin = findViewById(R.id.etPin);
        btnChange = findViewById(R.id.btnChange);
    }

    private void enterPin(final String input) {

        if (input.equals(pin)) {
            if (!isChange) {
                startActivity(new Intent(this, MainActivity.class).putExtra("hide", true));
                finish();
            } else {
                startActivity(new Intent(this, AuthActivity.class).putExtra("change", true));
                finish();
            }
        } else {
            Toast.makeText(this, "Incorrect PIN, please try again", Toast.LENGTH_SHORT).show();
            etPin.setText("");

        }
    }
}
