package ru.turina1v.photoviewer.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
@Entity(tableName = "table_photos")
public class Hit implements Parcelable, Comparable<Hit> {
    @PrimaryKey
    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("previewURL")
    private String previewUrl;
    @Expose
    @SerializedName("webformatURL")
    private String webFormatUrl;
    @Expose
    @SerializedName("largeImageURL")
    private String largeImageUrl;
    @Expose
    private long clickTimestamp;
    @Expose
    private long expireTimestamp;

    public Hit() {
    }

    protected Hit(Parcel in) {
        id = in.readInt();
        previewUrl = in.readString();
        webFormatUrl = in.readString();
        largeImageUrl = in.readString();
        clickTimestamp = in.readLong();
        expireTimestamp = in.readLong();
    }

    public static final Creator<Hit> CREATOR = new Creator<Hit>() {
        @Override
        public Hit createFromParcel(Parcel in) {
            return new Hit(in);
        }

        @Override
        public Hit[] newArray(int size) {
            return new Hit[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getWebFormatUrl() {
        return webFormatUrl;
    }

    public void setWebFormatUrl(String webFormatUrl) {
        this.webFormatUrl = webFormatUrl;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    public long getClickTimestamp() {
        return clickTimestamp;
    }

    public void setClickTimestamp(long clickTimestamp) {
        this.clickTimestamp = clickTimestamp;
    }

    public long getExpireTimestamp() {
        return expireTimestamp;
    }

    public void setExpireTimestamp(long expireTimestamp) {
        this.expireTimestamp = expireTimestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(previewUrl);
        dest.writeString(webFormatUrl);
        dest.writeString(largeImageUrl);
        dest.writeLong(clickTimestamp);
        dest.writeLong(expireTimestamp);
    }

    @Override
    public int compareTo(Hit otherHit) {
        if (this.clickTimestamp > otherHit.clickTimestamp) {
            return -1;
        } else if (this.clickTimestamp == otherHit.clickTimestamp) {
            return 0;
        } else {
            return 1;
        }
    }
}
