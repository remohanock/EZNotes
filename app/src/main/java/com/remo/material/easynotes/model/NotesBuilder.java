package com.remo.material.easynotes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

public class NotesBuilder extends SugarRecord implements Parcelable {

    private String title, content;
    private long noteid;

    public static Creator<NotesBuilder> getCREATOR() {
        return CREATOR;
    }

    public NotesBuilder() {

    }

    public NotesBuilder(String title, String content,long noteid) {
        this.title = title;
        this.content = content;
        this.setNoteid(noteid);

    }

    protected NotesBuilder(Parcel in) {
        title = in.readString();
        content = in.readString();
        noteid = in.readLong();
    }

    public static final Creator<NotesBuilder> CREATOR = new Creator<NotesBuilder>() {
        @Override
        public NotesBuilder createFromParcel(Parcel in) {
            return new NotesBuilder(in);
        }

        @Override
        public NotesBuilder[] newArray(int size) {
            return new NotesBuilder[size];
        }
    };

    public long getNoteid() {
        return noteid;
    }

    public void setNoteid(long noteid) {
        this.noteid = noteid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeLong(noteid);
    }
}
