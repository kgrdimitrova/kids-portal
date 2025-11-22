package com.portal.kids.event.model;

public enum EventPeriodicity {
    ONE_TIME("one-time event"),
    TRAINING("periodic training");


    private String displayName;

    EventPeriodicity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
