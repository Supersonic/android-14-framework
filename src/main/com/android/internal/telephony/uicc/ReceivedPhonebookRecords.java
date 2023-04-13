package com.android.internal.telephony.uicc;

import java.util.List;
/* loaded from: classes.dex */
public class ReceivedPhonebookRecords {
    public static final int RS_ABORT = 3;
    public static final int RS_ERROR = 2;
    public static final int RS_FINAL = 4;
    public static final int RS_OK = 1;
    private List<SimPhonebookRecord> mEntries;
    private int mPhonebookReceivedState;

    public ReceivedPhonebookRecords(int i, List<SimPhonebookRecord> list) {
        this.mPhonebookReceivedState = i;
        this.mEntries = list;
    }

    public boolean isCompleted() {
        return this.mPhonebookReceivedState == 4;
    }

    public boolean isRetryNeeded() {
        return this.mPhonebookReceivedState == 3;
    }

    public boolean isOk() {
        return this.mPhonebookReceivedState == 1;
    }

    public List<SimPhonebookRecord> getPhonebookRecords() {
        return this.mEntries;
    }
}
