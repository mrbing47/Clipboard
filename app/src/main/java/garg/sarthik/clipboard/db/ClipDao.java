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

    @Query("SELECT * from clip")
    List<Clip> getAll();

    @Query("DELETE from clip")
    void deleteAll();

    @Query("SELECT * FROM clip WHERE bookmarked = 1 ")
    List<Clip> getBookmarked();

    @Insert
    void insertClipList(List<Clip> clipList);

    @Insert
    void insertClip(Clip clip);

    @Delete
    void deleteClip(Clip clip);

    @Update
    void updateClip(Clip clip);

}
