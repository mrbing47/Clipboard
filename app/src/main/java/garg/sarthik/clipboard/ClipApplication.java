package garg.sarthik.clipboard;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import garg.sarthik.clipboard.db.ClipDb;

public class ClipApplication extends Application {

    final static Migration migration1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE clip " +
                    "ADD COLUMN bookmarked INTEGER DEFAULT 0 NOT NULL");
        }
    };
    final static Migration migration2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE clip " +
                    "ADD COLUMN hidden INTEGER DEFAULT 0 NOT NULL");
        }
    };
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
                .addMigrations(migration1_2, migration2_3)
                .build();
    }

}
