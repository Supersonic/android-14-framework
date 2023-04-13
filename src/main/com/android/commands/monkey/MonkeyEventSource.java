package com.android.commands.monkey;
/* loaded from: classes.dex */
public interface MonkeyEventSource {
    MonkeyEvent getNextEvent();

    void setVerbose(int i);

    boolean validate();
}
