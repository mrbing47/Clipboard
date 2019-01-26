package garg.sarthik.clipboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MainActivity";
    RecyclerView rvClipBoard;
    Button btnAdd;
    Button btnRemove;
    Button btnDeleteAll;

    Intent intent;
    ClipAdaptor clipAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvClipBoard = findViewById(R.id.rvClipBoard);
        btnAdd = findViewById(R.id.btnAdd);
        btnRemove = findViewById(R.id.btnRemove);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);

        callAaptor();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MyForegroundService.isListening) {
                    intent = new Intent(MainActivity.this, MyForegroundService.class);
                    ContextCompat.startForegroundService(MainActivity.this, intent);
                    MyForegroundService.isListening = true;
                    Log.e(TAG, "onClick: btnAdd" + MyForegroundService.isListening);
                }
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
                }
            }
        });
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("DO YOU WANT TO DELETE ALL CLIPS?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deleteAll(ClipApplication.getClipDb().getClipDao().getAll());
                                callAaptor();
                                Toast.makeText(MainActivity.this, "ALL CLIP REMOVED", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "WOAH!! THAT WAS A CLOSE ONE", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

    }

    public void deleteAll(List<Clip> clipList) {

        for (Clip clip : clipList)
            ClipApplication.getClipDb().getClipDao().deleteClip(clip);

    }

    public void callAaptor() {
        rvClipBoard.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        clipAdaptor = new ClipAdaptor(ClipApplication.getClipDb().getClipDao().getAll(), this);
        rvClipBoard.setAdapter(clipAdaptor);
    }

    @Override
    protected void onResume() {

        callAaptor();
        super.onResume();
    }
}
