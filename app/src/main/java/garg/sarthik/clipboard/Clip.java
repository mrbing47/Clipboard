package garg.sarthik.clipboard;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "clip")
public class Clip implements Parcelable {

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
    @NonNull
    @PrimaryKey
    private String content;
    private String date;
    @Ignore
    private boolean isChecked = false;

    public Clip(String content, String date) {
        this.content = content;
        this.date = date;
    }

    protected Clip(Parcel in) {
        content = in.readString();
        date = in.readString();
        isChecked = in.readByte() != 0;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(date);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }
}
