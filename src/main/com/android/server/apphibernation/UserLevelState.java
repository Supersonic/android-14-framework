package com.android.server.apphibernation;

import java.text.SimpleDateFormat;
/* loaded from: classes.dex */
public final class UserLevelState {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public boolean hibernated;
    public long lastUnhibernatedMs;
    public String packageName;
    public long savedByte;

    public UserLevelState() {
    }

    public UserLevelState(UserLevelState userLevelState) {
        this.packageName = userLevelState.packageName;
        this.hibernated = userLevelState.hibernated;
        this.savedByte = userLevelState.savedByte;
        this.lastUnhibernatedMs = userLevelState.lastUnhibernatedMs;
    }

    public String toString() {
        return "UserLevelState{packageName='" + this.packageName + "', hibernated=" + this.hibernated + "', savedByte=" + this.savedByte + "', lastUnhibernated=" + DATE_FORMAT.format(Long.valueOf(this.lastUnhibernatedMs)) + '}';
    }
}
