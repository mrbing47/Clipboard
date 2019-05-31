package garg.sarthik.clipboard;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "clip")
public class Clip implements Parcelable {


    @NonNull
    @PrimaryKey
    private String content;
    private String date;
    private int bookmarked = 0;
    private int hidden = 0;
    @Ignore
    private boolean isChecked = false;


    public Clip(String content, String date) {
        this.content = content;
        this.date = date;
    }

    @Ignore
    public Clip(@NonNull String content, String date, int bookmarked, int hidden) {
        this.content = content;
        this.date = date;
        this.bookmarked = bookmarked;
        this.hidden = hidden;
    }

    protected Clip(Parcel in) {
        content = in.readString();
        date = in.readString();
        bookmarked = in.readInt();
        hidden = in.readInt();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<Clip> CREATOR = new Creator<Clip>() {
        @Override
        public Clip createFromParcel(Parcel in) {
            return new Clip(in);
        }

        @Override
        public Clip[] newArray(int size) {
            return new Clip[size];
        }
    };

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(int bookmarked) {
        this.bookmarked = bookmarked;
    }

    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(date);
        dest.writeInt(bookmarked);
        dest.writeInt(hidden);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }
}
