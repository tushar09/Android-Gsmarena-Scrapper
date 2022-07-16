package com.captaindroid.gsmarena.scrapper.db.tables;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"link"}, unique = true)})
public class PhoneBrand {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int totalItem;
    private String link;
    private boolean doneAllPage;
    private long createdAt;
    private long updatedAt;

    @Ignore
    private boolean newPhoneAvailable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isDoneAllPage() {
        return doneAllPage;
    }

    public void setDoneAllPage(boolean doneAllPage) {
        this.doneAllPage = doneAllPage;
    }

    public boolean isNewPhoneAvailable() {
        return newPhoneAvailable;
    }

    public void setNewPhoneAvailable(boolean newPhoneAvailable) {
        this.newPhoneAvailable = newPhoneAvailable;
    }
}
