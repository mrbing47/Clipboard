package garg.sarthik.clipboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchDelete";
    private Toolbar toolbar;
    private ImageButton btnSearch;
    private EditText etSearch;
    private RecyclerView rvClipBoard;
    private ClipAdaptor clipAdaptor;
    private String searchtxt;
    private List<Clip> clipList;
    private List<Clip> resultClips;

    private boolean isBordered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        btnSearch = findViewById(R.id.btnSearch);
        etSearch = findViewById(R.id.etSearch);
        rvClipBoard = findViewById(R.id.rvSearch);

        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);


        String content = getIntent().getStringExtra("content");

        if (content.equals("all")) {
            clipList = ClipApplication.getClipDb().getClipDao().getAll();
        } else {
            clipList = ClipApplication.getClipDb().getClipDao().getBookmarked();
        }


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etSearch.getText().toString().isEmpty()) {
                    searchtxt = etSearch.getText().toString().trim();

                    if (search())
                        Toast.makeText(SearchActivity.this, "No Clip(s) Found", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(SearchActivity.this, "I hope you found what you were looking for", Toast.LENGTH_SHORT).show();
                    callAdapter();
                }
            }
        });


    }

    private void callAdapter() {
        if (Statics.layout.equals(Statics.gridView))
            rvClipBoard.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        else
            rvClipBoard.setLayoutManager(new LinearLayoutManager(this));

        clipAdaptor = new ClipAdaptor(resultClips, this, this);
        rvClipBoard.setAdapter(clipAdaptor);
        if (!isBordered) {
            rvClipBoard.addItemDecoration(new Statics.SpaceItemDecoration(resultClips,this));
            isBordered = true;
        }
    }

    public boolean search() {

        boolean isFound = false;
        resultClips = new ArrayList<>();

        for (Clip clip : clipList) {

            if (Pattern.compile(Pattern.quote(searchtxt), Pattern.CASE_INSENSITIVE).matcher(clip.getContent()).find()) {
                resultClips.add(clip);
                isFound = true;
            }
        }

        return isFound;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.miDeleteSearch: {
                new AlertDialog.Builder(this)
                        .setTitle("DO YOU WANT TO DELETE THE SELECTED CLIP(S)?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                boolean isSelected = false;
                                List<Clip> deleteClips = new ArrayList<>();
                                copy(resultClips, deleteClips);

                                try {
                                    for (Clip clip : deleteClips) {
                                        if (clip.isChecked()) {
                                            isSelected = true;
                                            Log.e(TAG, "onClick: ");
                                            ClipApplication.getClipDb().getClipDao().deleteClip(clip);
                                            resultClips.remove(clip);
                                        }
                                    }
                                    if (isSelected) {
                                        Toast.makeText(SearchActivity.this, "I will miss them", Toast.LENGTH_SHORT).show();
                                        clipAdaptor.notifyDataSetChanged();
                                    } else
                                        Toast.makeText(SearchActivity.this, "Please select the victim first", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Log.e(TAG, "onClick: \n\n\n", e);
                                    Toast.makeText(SearchActivity.this, "Please try again!!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SearchActivity.this, "WOAH!! THAT WAS A CLOSE ONE", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .show();

                return true;
            }
            case R.id.miDeleteAllSearch: {
                new AlertDialog.Builder(SearchActivity.this)
                        .setTitle("DO YOU WANT TO DELETE ALL CLIPS?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAll();
                                clipAdaptor.notifyDataSetChanged();
                                Toast.makeText(SearchActivity.this, "RIP to all those clips", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SearchActivity.this, "WOAH!! THAT WAS A CLOSE ONE", Toast.LENGTH_SHORT).show();
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

    public void copy(List<Clip> src, List<Clip> dest) {
        for (Clip clip : src)
            dest.add(clip);
    }

    public void deleteAll() {
        for (Clip clip : resultClips) {
            ClipApplication.getClipDb().getClipDao().deleteClip(clip);
        }
        resultClips.clear();
    }


}
