package com.android.server.people.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Binder;
import android.provider.Telephony;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseIntArray;
import java.util.function.BiConsumer;
/* loaded from: classes2.dex */
public class SmsQueryHelper {
    public static final SparseIntArray SMS_TYPE_TO_EVENT_TYPE;
    public final Context mContext;
    public final String mCurrentCountryIso;
    public final BiConsumer<String, Event> mEventConsumer;
    public long mLastMessageTimestamp;

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        SMS_TYPE_TO_EVENT_TYPE = sparseIntArray;
        sparseIntArray.put(1, 9);
        sparseIntArray.put(2, 8);
    }

    public SmsQueryHelper(Context context, BiConsumer<String, Event> biConsumer) {
        this.mContext = context;
        this.mEventConsumer = biConsumer;
        this.mCurrentCountryIso = Utils.getCurrentCountryIso(context);
    }

    public boolean querySince(long j) {
        String[] strArr = {"_id", "date", "type", "address"};
        String[] strArr2 = {Long.toString(j)};
        Binder.allowBlockingForCurrentThread();
        try {
            Cursor query = this.mContext.getContentResolver().query(Telephony.Sms.CONTENT_URI, strArr, "date > ?", strArr2, null);
            boolean z = false;
            if (query == null) {
                Slog.w("SmsQueryHelper", "Cursor is null when querying SMS table.");
                if (query != null) {
                    query.close();
                }
                return false;
            }
            while (query.moveToNext()) {
                query.getString(query.getColumnIndex("_id"));
                long j2 = query.getLong(query.getColumnIndex("date"));
                int i = query.getInt(query.getColumnIndex("type"));
                String formatNumberToE164 = PhoneNumberUtils.formatNumberToE164(query.getString(query.getColumnIndex("address")), this.mCurrentCountryIso);
                this.mLastMessageTimestamp = Math.max(this.mLastMessageTimestamp, j2);
                if (formatNumberToE164 != null && addEvent(formatNumberToE164, j2, i)) {
                    z = true;
                }
            }
            query.close();
            return z;
        } finally {
            Binder.defaultBlockingForCurrentThread();
        }
    }

    public long getLastMessageTimestamp() {
        return this.mLastMessageTimestamp;
    }

    public final boolean addEvent(String str, long j, int i) {
        if (validateEvent(str, j, i)) {
            this.mEventConsumer.accept(str, new Event(j, SMS_TYPE_TO_EVENT_TYPE.get(i)));
            return true;
        }
        return false;
    }

    public final boolean validateEvent(String str, long j, int i) {
        return !TextUtils.isEmpty(str) && j > 0 && SMS_TYPE_TO_EVENT_TYPE.indexOfKey(i) >= 0;
    }
}
