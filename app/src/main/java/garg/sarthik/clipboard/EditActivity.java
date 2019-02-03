package garg.sarthik.clipboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvEditDate;
    EditText etClip;

    String orgTxt;
    String modTxt;

    Clip orgClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        toolbar = findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);

        tvEditDate = findViewById(R.id.tvEditDate);
        etClip = findViewById(R.id.etClip);

        if (getIntent() != null) {
            orgClip = getIntent().getParcelableExtra("clip");
            orgTxt = orgClip.getContent();
            etClip.setText(orgTxt);
            tvEditDate.setText(orgClip.getDate());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.miSave: {

                modTxt = etClip.getText().toString();
                Clip clip = new Clip(modTxt, DateFormat.getDateTimeInstance().format(new Date()));
                if (!orgTxt.equals(modTxt)) {
                    ClipApplication.getClipDb().getClipDao().deleteClip(orgClip);
                    ClipApplication.getClipDb().getClipDao().insertClip(clip);

                } else
                    ClipApplication.getClipDb().getClipDao().updateClip(clip);
                finish();
            }

            case R.id.miDiscard:{
                finish();
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
