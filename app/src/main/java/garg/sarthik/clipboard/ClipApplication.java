package garg.sarthik.clipboard;

import android.app.Application;
import android.arch.persistence.room.Room;

import garg.sarthik.clipboard.db.ClipDb;

public class ClipApplication extends Application {

    static ClipDb clipDb;

    public static ClipDb getClipDb() {
        return clipDb;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        clipDb = Room.databaseBuilder(getApplicationContext(),
                ClipDb.class,
                "clip-db")
                .allowMainThreadQueries()
                .build();
    }

}
