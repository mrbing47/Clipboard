package garg.sarthik.clipboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Frag_Bookmark.FragmentUpdateAll, Frag_Clip.FragmentUpdateBookmark {

    public String TAG = "MainActivity";
    RecyclerView rvClipBoard;
    Toolbar toolbar;
    int currentPage = 1;
    Intent intent;
    TabLayout tabLayout;
    ViewPager vp;
    List<Clip> clipAll;
    List<Clip> clipBookmark;
    Frag_Clip fragAll;
    Frag_Bookmark fragBookmark;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        fragAll = new Frag_Clip();
        fragBookmark = new Frag_Bookmark();

        vp = findViewById(R.id.vpFrags);
        tabLayout = findViewById(R.id.tabLayout);
        callViewPager();
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
            case R.id.miStart: {
                if (!MyForegroundService.isListening) {
                    intent = new Intent(MainActivity.this, MyForegroundService.class);
                    ContextCompat.startForegroundService(MainActivity.this, intent);
                    MyForegroundService.isListening = true;
                    Log.e(TAG, "onClick: btnAdd" + MyForegroundService.isListening);
                    Toast.makeText(MainActivity.this, "Go Go Go", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MainActivity.this, "Check your notifications", Toast.LENGTH_SHORT).show();

                return true;
            }
            case R.id.miStop: {
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

                return true;
            }
            case R.id.miBookmark: {
                startActivity(new Intent(this, BookmarkActivity.class));
                return true;
            }
            case R.id.miDelete: {
                new AlertDialog.Builder(this)
                        .setTitle("DO YOU WANT TO DELETE THE SELECTED CLIP(S)?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean isSelected = false;
                                boolean isBookmarked = false;
                                for (Clip clip : clipAll) {
                                    if (clip.isChecked()) {
                                        isSelected = true;
                                        if (clip.getBookmarked() == 1)
                                            isBookmarked = true;
                                        ClipApplication.getClipDb().getClipDao().deleteClip(clip);
                                    }
                                }
                                if (isSelected) {
                                    Toast.makeText(MainActivity.this, "I will miss them", Toast.LENGTH_SHORT).show();
                                    if (isBookmarked)
                                        updateBoth();
                                    else
                                        updateAdapterAll();
                                } else
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

                                deleteAll(clipAll);
                                updateBoth();
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


    public void callViewPager() {
        vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(vp);
    }

    public void updateBoth() {
        updateAdapterAll();
        updateAdapterBookmark();
    }

    @Override
    public void updateAdapterAll() {
        fragAll.update();
    }

    @Override
    public void updateAdapterBookmark() {
        fragBookmark.update();
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "All";
                case 1:
                    return "Bookmarked";
                default:
                    return "";
            }

        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    clipAll = ClipApplication.getClipDb().getClipDao().getAll();
                    Collections.reverse(clipAll);
                    return fragAll;
                case 1:
                    clipBookmark = ClipApplication.getClipDb().getClipDao().getBookmarked();
                    Collections.reverse(clipBookmark);
                    return fragBookmark;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
