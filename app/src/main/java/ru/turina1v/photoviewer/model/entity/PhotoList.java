package ru.turina1v.photoviewer.model.entity;

import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings("unused")
public class PhotoList {
    @Expose
    private List<Hit> hits;
    @Expose
    private Long total;
    @Expose
    private Long totalHits;

    public List<Hit> getHits() {
        return hits;
    }

    public void setHits(List<Hit> hits) {
        this.hits = hits;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(Long totalHits) {
        this.totalHits = totalHits;
    }
}
