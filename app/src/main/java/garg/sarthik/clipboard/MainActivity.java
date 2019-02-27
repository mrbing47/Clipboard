package garg.sarthik.clipboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Parcelable {

    public String TAG = "MainActivity";
    RecyclerView rvClipBoard;
    Toolbar toolbar;

    boolean isSelected;
    int currentPage = 1;

    Intent intent;
    TabLayout tabLayout;
    ViewPager vp;

    List<Clip> clipAll;
    List<Clip> clipBookmark;

    Frag_Clip fragAll;
    Frag_Clip fragBookmark;

    protected MainActivity(Parcel in) {
        TAG = in.readString();
        isSelected = in.readByte() != 0;
        currentPage = in.readInt();
        intent = in.readParcelable(Intent.class.getClassLoader());
        clipAll = in.createTypedArrayList(Clip.CREATOR);
        clipBookmark = in.createTypedArrayList(Clip.CREATOR);
    }

    public static final Creator<MainActivity> CREATOR = new Creator<MainActivity>() {
        @Override
        public MainActivity createFromParcel(Parcel in) {
            return new MainActivity(in);
        }

        @Override
        public MainActivity[] newArray(int size) {
            return new MainActivity[size];
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);


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
                                isSelected = false;
                                for (Clip clip : clipAll) {
                                    if (clip.isChecked()) {
                                        isSelected = true;
                                        ClipApplication.getClipDb().getClipDao().deleteClip(clip);
                                    }
                                }
                                if (isSelected) {
                                    updateAdapter();
                                    Toast.makeText(MainActivity.this, "I will miss them", Toast.LENGTH_SHORT).show();
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
                                updateAdapter();
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

    void updateAdapter() {
        if (currentPage == 1 && fragAll != null)
            fragAll.updateAdapter();

        if (currentPage == 2 && fragBookmark != null)
            fragBookmark.updateAdapter();

        Log.e(TAG, "updateAdapter: " + currentPage);
    }
    public void callViewPager() {
        vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(vp);
    }

    @Override
    protected void onStart() {
        updateAdapter();
        super.onStart();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(TAG);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeInt(currentPage);
        dest.writeParcelable(intent, flags);
        dest.writeTypedList(clipAll);
        dest.writeTypedList(clipBookmark);
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
                    fragAll = new Frag_Clip().newInstance("all", (ArrayList<Clip>) clipAll, MainActivity.this);
                    return fragAll;
                case 1:
                    clipBookmark = ClipApplication.getClipDb().getClipDao().getBookmarked();
                    Collections.reverse(clipBookmark);
                    fragBookmark = new Frag_Clip().newInstance("bookmarked", (ArrayList<Clip>) clipBookmark, MainActivity.this);
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
