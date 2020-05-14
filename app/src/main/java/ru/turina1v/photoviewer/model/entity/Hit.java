package ru.turina1v.photoviewer.model.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
@Entity(tableName = "table_photos")
public class Hit {
    @PrimaryKey(autoGenerate = true)
    private int dbId;
    @Expose
    @SerializedName("previewURL")
    private String previewUrl;
    @Expose
    @SerializedName("webformatURL")
    private String webFormatUrl;
    @Expose
    @SerializedName("largeImageURL")
    private String largeImageUrl;

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
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
}
