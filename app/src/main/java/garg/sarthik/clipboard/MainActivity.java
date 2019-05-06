package garg.sarthik.clipboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Frag_Bookmark.FragmentUpdateAll, Frag_Clip.FragmentUpdateBookmark {

    public String TAG = "MainActivity";
    Toolbar toolbar;
    Intent intent;
    TabLayout tabLayout;
    ViewPager vp;
    List<Clip> clipAll;
    Frag_Clip fragAll;
    Frag_Bookmark fragBookmark;

    Menu menu;

    SharedPreferences sharedPreferences;

    @Override
    protected void onStart() {
        if (fragAll != null)
            updateAdapterAll();
        if (fragBookmark != null)
            updateAdapterBookmark();

        Statics.checkedCounter = 0;

        if (menu != null) {
            Statics.swapMenu(this);
        }

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);


        if (sharedPreferences.contains("layout")) {
            Statics.layout = sharedPreferences.getString("layout", "grid");
        } else
            Statics.layout = Statics.gridView;

        clipAll = new ArrayList<>();
        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        vp = findViewById(R.id.vpFrags);
        tabLayout = findViewById(R.id.tabLayout);
        callViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        Statics.menuMain = menu;
        // Inflate the menuMain; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (menu != null) {
            Statics.swapMenu(this);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.miStart: {
                if (!Statics.isListening) {
                    intent = new Intent(MainActivity.this, MyForegroundService.class);
                    ContextCompat.startForegroundService(MainActivity.this, intent);
                    Statics.isListening = true;
                    Log.e(TAG, "onClick: btnAdd" + Statics.isListening);
                    Toast.makeText(MainActivity.this, "Go Go Go", Toast.LENGTH_SHORT).show();

                    menu.findItem(R.id.miStart).setVisible(false);
                    menu.findItem(R.id.miStop).setVisible(true);
                } else
                    Toast.makeText(MainActivity.this, "Check your notifications", Toast.LENGTH_SHORT).show();

                return true;
            }
            case R.id.miStop: {
                Log.e(TAG, "onClick: Entering btnRemove, isListening Value = " + Statics.isListening);
                if (Statics.isListening) {
                    Log.e(TAG, "onClick: btnRemove");
                    intent = new Intent(MainActivity.this, MyForegroundService.class);
                    intent.putExtra("KEY", true);
                    ContextCompat.startForegroundService(MainActivity.this, intent);
                    Statics.isListening = false;
                    Toast.makeText(MainActivity.this, "Fallback", Toast.LENGTH_SHORT).show();

                    menu.findItem(R.id.miStart).setVisible(true);
                    menu.findItem(R.id.miStop).setVisible(false);

                } else
                    Toast.makeText(MainActivity.this, "I know the meaning of Stop", Toast.LENGTH_SHORT).show();

                return true;
            }
            case R.id.miSearch: {

                int current = vp.getCurrentItem();
                Log.e(TAG, "onClick: " + current);

                if (current == 0) {
                    startActivity(new Intent(MainActivity.this, SearchActivity.class).putExtra("content", "all"));
                } else {
                    startActivity(new Intent(MainActivity.this, SearchActivity.class).putExtra("content", "bookmark"));
                }

                return true;
            }
            case R.id.miDelete: {
                new AlertDialog.Builder(this)
                        .setTitle("DO YOU WANT TO DELETE THE SELECTED CLIP(S)?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int current = vp.getCurrentItem();
                                Log.e(TAG, "onClick: " + current);

                                if (current == 0) {
                                    clipAll = fragAll.send();
                                } else {
                                    clipAll = fragBookmark.send();
                                }

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

                                    Statics.checkedCounter = 0;
                                    Statics.swapMenu(MainActivity.this);

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
                                deleteAll();
                                updateBoth();

                                Toast.makeText(MainActivity.this, "RIP to all those clips", Toast.LENGTH_SHORT).show();

                                Statics.checkedCounter = 0;
                                Statics.swapMenu(MainActivity.this);
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
            case R.id.miGridView: {
                Statics.layout = Statics.gridView;
                menu.findItem(R.id.miGridView).setVisible(false);
                menu.findItem(R.id.miListView).setVisible(true);
                updateBoth();
                return true;
            }
            case R.id.miListView: {
                Statics.layout = Statics.listView;
                menu.findItem(R.id.miListView).setVisible(false);
                menu.findItem(R.id.miGridView).setVisible(true);
                updateBoth();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void deleteAll() {
        List<Clip> clipList = ClipApplication.getClipDb().getClipDao().getAll();
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
        if (fragAll != null)
            fragAll.update();
    }

    @Override
    public void updateAdapterBookmark() {
        if (fragBookmark != null)
            fragBookmark.update();
    }

    @Override
    protected void onStop() {

        Log.e(TAG, "onDestroy: 1");
        super.onStop();
        Log.e(TAG, "onDestroy: 2");

        sharedPreferences.edit().putString("layout", Statics.layout).apply();

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
                    fragAll = new Frag_Clip();
                    return fragAll;
                case 1:
                    fragBookmark = new Frag_Bookmark();
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
