package garg.sarthik.clipboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity {

    private ImageButton btnSearch;
    private EditText etSearch;
    private RecyclerView rvClipBoard;
    private ClipAdaptor clipAdaptor;
    private String searchtxt;
    private List<Clip> clipList;
    private List<Clip> resultClips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toast.makeText(this, "I hope you'll find what you are looking for :)", Toast.LENGTH_SHORT).show();
        btnSearch = findViewById(R.id.btnSearch);
        etSearch = findViewById(R.id.etSearch);
        rvClipBoard = findViewById(R.id.rvSearch);

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

                    resultClips = new ArrayList<>();

                    search();
                    callAdapter();
                }
            }
        });


    }

    private void callAdapter() {
        rvClipBoard.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        clipAdaptor = new ClipAdaptor(resultClips, this, this);
        rvClipBoard.setAdapter(clipAdaptor);
    }

    public void search() {

        String content;
        for (Clip clip : clipList) {
            content = clip.getContent();
            if (Pattern.compile(Pattern.quote(searchtxt), Pattern.CASE_INSENSITIVE).matcher(content).find())
                //    Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
                //  if (content.contains(searchtxt)) {
                resultClips.add(clip);
            //}
        }
    }

}
