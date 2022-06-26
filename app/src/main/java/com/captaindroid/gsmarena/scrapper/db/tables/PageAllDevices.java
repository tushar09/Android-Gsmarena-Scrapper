package com.captaindroid.gsmarena.scrapper.db.tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"link"}, unique = true)})
public class PageAllDevices {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String link;
    private String brandName;
    private boolean doneListingModels;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public boolean isDoneListingModels() {
        return doneListingModels;
    }

    public void setDoneListingModels(boolean doneListingModels) {
        this.doneListingModels = doneListingModels;
    }
}
