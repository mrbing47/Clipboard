package garg.sarthik.clipboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MainActivity";
    RecyclerView rvClipBoard;
    Button btnAdd;
    Button btnRemove;
    Toolbar toolbar;

    boolean isSelected;

    Intent intent;
    ClipAdaptor clipAdaptor;
    List<Clip> clipList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        rvClipBoard = findViewById(R.id.rvClipBoard);
        btnAdd = findViewById(R.id.btnAdd);
        btnRemove = findViewById(R.id.btnRemove);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MyForegroundService.isListening) {
                    intent = new Intent(MainActivity.this, MyForegroundService.class);
                    ContextCompat.startForegroundService(MainActivity.this, intent);
                    MyForegroundService.isListening = true;
                    Log.e(TAG, "onClick: btnAdd" + MyForegroundService.isListening);
                    Toast.makeText(MainActivity.this, "Go Go Go", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MainActivity.this, "Check your notifications", Toast.LENGTH_SHORT).show();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: Entering btnRemove, isListening Value = " + MyForegroundService.isListening);
                if (MyForegroundService.isListening) {
                    Log.e(TAG, "onClick: btnRemove");
                    intent = new Intent(MainActivity.this, MyForegroundService.class);
                    intent.putExtra("KEY", true);
                    ContextCompat.startForegroundService(MainActivity.this, intent);
                    MyForegroundService.isListening = false;
                    Toast.makeText(MainActivity.this, "Fallback", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MainActivity.this, "I know the meaning of Stop", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.miDelete: {
                new AlertDialog.Builder(this)
                        .setTitle("DO YOU WANT TO DELETE THE SELECTED CLIP(S)?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isSelected = false;
                                for (Clip clip : clipList) {
                                    if (clip.isChecked()) {
                                        isSelected = true;
                                        ClipApplication.getClipDb().getClipDao().deleteClip(clip);
                                    }
                                }
                                if(isSelected) {
                                    callAdapter();
                                    Toast.makeText(MainActivity.this, "I will miss them", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(MainActivity.this, "Please select the victim first", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "WOAH!! THAT WAS A CLOSE ONE", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .show();

                return true;
            }
            case R.id.miDeleteAll: {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("DO YOU WANT TO DELETE ALL CLIPS?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deleteAll(clipList);
                                callAdapter();
                                Toast.makeText(MainActivity.this, "RIP to all those clips", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "WOAH!! THAT WAS A CLOSE ONE", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .show();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void deleteAll(List<Clip> clipList) {
        for (Clip clip : clipList)
            ClipApplication.getClipDb().getClipDao().deleteClip(clip);
    }

    public void callAdapter() {
        rvClipBoard.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        clipList = ClipApplication.getClipDb().getClipDao().getAll();
        clipAdaptor = new ClipAdaptor(clipList, this);
        rvClipBoard.setAdapter(clipAdaptor);
    }

    @Override
    protected void onResume() {
        callAdapter();
        super.onResume();
    }

}
