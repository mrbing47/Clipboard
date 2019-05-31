package garg.sarthik.clipboard.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import garg.sarthik.clipboard.Clip;

@Database(entities = Clip.class, version = 3, exportSchema = false)
public abstract class ClipDb extends RoomDatabase {

    public abstract ClipDao getClipDao();
}
