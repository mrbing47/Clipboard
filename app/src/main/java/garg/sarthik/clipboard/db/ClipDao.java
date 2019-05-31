package garg.sarthik.clipboard.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import garg.sarthik.clipboard.Clip;

@Dao
public interface ClipDao {

    @Query("SELECT * from clip WHERE hidden = 0")
    List<Clip> getAll();

    @Query("SELECT * from clip WHERE hidden = 1")
    List<Clip> getAllHidden();

    @Query("SELECT * FROM clip WHERE bookmarked = 1 AND hidden = 0")
    List<Clip> getBookmarked();

    @Query("SELECT * FROM clip WHERE bookmarked = 1 AND hidden = 1")
    List<Clip> getBookmarkedHidden();

    @Insert
    void insertClip(Clip clip);

    @Delete
    void deleteClip(Clip clip);

    @Update
    void updateClip(Clip clip);

}
