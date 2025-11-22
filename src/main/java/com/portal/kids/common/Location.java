package com.portal.kids.common;

public enum Location {
    SOFIA("Sofia"),
    PLOVDIV("Plovdiv"),
    VARNA("Varna"),
    BURGAS("Burgas"),
    RUSE("Ruse"),
    STARA_ZAGORA("Stara Zagora"),
    PLEVEN("Pleven"),
    SLIVEN("Sliven"),
    DOBRICH("Dobrich"),
    SHUMEN("Shumen"),
    PERNIK("Pernik"),
    HASKOVO("Haskovo"),
    YAMBOL("Yambol"),
    BLAGOEVGRAD("Blagoevgrad"),
    VRATSA("Vratsa"),
    GABROVO("Gabrovo"),
    ASENOVGRAD("Asenovgrad"),
    VIDIN("Vidin"),
    KAZANLAK("Kazanlak"),
    KYUSTENDIL("Kyustendil"),
    MONTANA("Montana"),
    TARGOVISHTE("Targovishte"),
    LOVECH("Lovech"),
    SILISTRA("Silistra"),
    DUPNITSA("Dupnitsa");

    private String displayName;
    Location(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    }