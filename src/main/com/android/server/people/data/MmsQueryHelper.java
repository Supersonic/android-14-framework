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
public class MmsQueryHelper {
    public static final SparseIntArray MSG_BOX_TO_EVENT_TYPE;
    public final Context mContext;
    public String mCurrentCountryIso;
    public final BiConsumer<String, Event> mEventConsumer;
    public long mLastMessageTimestamp;

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        MSG_BOX_TO_EVENT_TYPE = sparseIntArray;
        sparseIntArray.put(1, 9);
        sparseIntArray.put(2, 8);
    }

    public MmsQueryHelper(Context context, BiConsumer<String, Event> biConsumer) {
        this.mContext = context;
        this.mEventConsumer = biConsumer;
        this.mCurrentCountryIso = Utils.getCurrentCountryIso(context);
    }

    public boolean querySince(long j) {
        String[] strArr = {"_id", "date", "msg_box"};
        String[] strArr2 = {Long.toString(j / 1000)};
        Binder.allowBlockingForCurrentThread();
        try {
            Cursor query = this.mContext.getContentResolver().query(Telephony.Mms.CONTENT_URI, strArr, "date > ?", strArr2, null);
            boolean z = false;
            if (query == null) {
                Slog.w("MmsQueryHelper", "Cursor is null when querying MMS table.");
                if (query != null) {
                    query.close();
                }
                return false;
            }
            while (query.moveToNext()) {
                String string = query.getString(query.getColumnIndex("_id"));
                long j2 = query.getLong(query.getColumnIndex("date")) * 1000;
                int i = query.getInt(query.getColumnIndex("msg_box"));
                this.mLastMessageTimestamp = Math.max(this.mLastMessageTimestamp, j2);
                String mmsAddress = getMmsAddress(string, i);
                if (mmsAddress != null && addEvent(mmsAddress, j2, i)) {
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

    public final String getMmsAddress(String str, int i) {
        Cursor query = this.mContext.getContentResolver().query(Telephony.Mms.Addr.getAddrUriForMessage(str), new String[]{"address", "type"}, null, null, null);
        try {
            if (query == null) {
                Slog.w("MmsQueryHelper", "Cursor is null when querying MMS address table.");
                if (query != null) {
                    query.close();
                }
                return null;
            }
            String str2 = null;
            while (query.moveToNext()) {
                int i2 = query.getInt(query.getColumnIndex("type"));
                if ((i == 1 && i2 == 137) || (i == 2 && i2 == 151)) {
                    str2 = query.getString(query.getColumnIndex("address"));
                }
            }
            query.close();
            if (Telephony.Mms.isPhoneNumber(str2)) {
                return PhoneNumberUtils.formatNumberToE164(str2, this.mCurrentCountryIso);
            }
            return null;
        } catch (Throwable th) {
            if (query != null) {
                try {
                    query.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public final boolean addEvent(String str, long j, int i) {
        if (validateEvent(str, j, i)) {
            this.mEventConsumer.accept(str, new Event(j, MSG_BOX_TO_EVENT_TYPE.get(i)));
            return true;
        }
        return false;
    }

    public final boolean validateEvent(String str, long j, int i) {
        return !TextUtils.isEmpty(str) && j > 0 && MSG_BOX_TO_EVENT_TYPE.indexOfKey(i) >= 0;
    }
}
