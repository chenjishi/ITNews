package com.misscellapp.news;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chenjishi on 16/7/27.
 */
public class Feed implements Parcelable {

    public String id;

    public String title;

    public String summary;

    public int commentNum;

    public String views;

    public String time;

    public String url;

    public String imageUrl;

    public static final Creator<Feed> CREATOR = new Creator<Feed>() {
        @Override
        public Feed createFromParcel(Parcel source) {
            return new Feed(source);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };

    public Feed() {

    }

    public Feed(Parcel in) {
        id = in.readString();
        title = in.readString();
        summary = in.readString();
        commentNum = in.readInt();
        views = in.readString();
        time = in.readString();
        url = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(summary);
        dest.writeInt(commentNum);
        dest.writeString(views);
        dest.writeString(time);
        dest.writeString(url);
        dest.writeString(imageUrl);
    }
}
