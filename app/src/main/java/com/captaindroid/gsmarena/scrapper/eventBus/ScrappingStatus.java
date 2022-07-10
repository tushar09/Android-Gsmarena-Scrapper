package com.captaindroid.gsmarena.scrapper.eventBus;

public class ScrappingStatus {
    private String scrappingName;
    private String phoneImageUrl;
    private boolean indetermination;
    private boolean finished;
    private int progress;

    public String getScrappingName() {
        return scrappingName;
    }

    public void setScrappingName(String scrappingName) {
        this.scrappingName = scrappingName;
    }

    public boolean isIndetermination() {
        return indetermination;
    }

    public void setIndetermination(boolean indetermination) {
        this.indetermination = indetermination;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getPhoneImageUrl() {
        return phoneImageUrl;
    }

    public void setPhoneImageUrl(String phoneImageUrl) {
        this.phoneImageUrl = phoneImageUrl;
    }
}
