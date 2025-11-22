package com.portal.kids.event.model;

public enum AgeCategory {
    TODDLER("1–3 years"),
    PRESCHOOL("3–5 years"),
    KIDS("6–8 years"),
    JUNIOR("9–10 years"),
    TWEEN("11–12 years"),
    TEEN("13–15 years"),
    OLDER_TEEN("16–18 years"),
    ALL("all years");


    private String displayName;

    AgeCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
