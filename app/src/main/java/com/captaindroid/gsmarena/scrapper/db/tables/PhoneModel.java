package com.captaindroid.gsmarena.scrapper.db.tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"detailsLink"}, unique = true)})
public class PhoneModel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String brandName;
    private String phoneModelName;
    private String imageLink;
    private String toolTips;
    private String detailsLink;
    private String details;
    private boolean scrappingDone;
    private boolean uploadToServerDone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getPhoneModelName() {
        return phoneModelName;
    }

    public void setPhoneModelName(String phoneModelName) {
        this.phoneModelName = phoneModelName;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getToolTips() {
        return toolTips;
    }

    public void setToolTips(String toolTips) {
        this.toolTips = toolTips;
    }

    public String getDetailsLink() {
        return detailsLink;
    }

    public void setDetailsLink(String detailsLink) {
        this.detailsLink = detailsLink;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isScrappingDone() {
        return scrappingDone;
    }

    public void setScrappingDone(boolean scrappingDone) {
        this.scrappingDone = scrappingDone;
    }

    public boolean isUploadToServerDone() {
        return uploadToServerDone;
    }

    public void setUploadToServerDone(boolean uploadToServerDone) {
        this.uploadToServerDone = uploadToServerDone;
    }
}
