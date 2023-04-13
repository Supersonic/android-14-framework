package com.android.internal.telephony;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.android.internal.telephony.HbpcdLookup;
/* loaded from: classes3.dex */
public final class HbpcdUtils {
    private static final boolean DBG = false;
    private static final String LOG_TAG = "HbpcdUtils";
    private ContentResolver resolver;

    public HbpcdUtils(Context context) {
        this.resolver = null;
        this.resolver = context.getContentResolver();
    }

    public int getMcc(int sid, int tz, int DSTflag, boolean isNitzTimeZone) {
        String[] projection2 = {"MCC"};
        Cursor c2 = this.resolver.query(HbpcdLookup.ArbitraryMccSidMatch.CONTENT_URI, projection2, "SID=" + sid, null, null);
        if (c2 != null) {
            int c2Counter = c2.getCount();
            if (c2Counter == 1) {
                c2.moveToFirst();
                int tmpMcc = c2.getInt(0);
                c2.close();
                return tmpMcc;
            }
            c2.close();
        }
        String[] projection3 = {"MCC"};
        Cursor c3 = this.resolver.query(HbpcdLookup.MccSidConflicts.CONTENT_URI, projection3, "SID_Conflict=" + sid + " and (((" + HbpcdLookup.MccLookup.GMT_OFFSET_LOW + "<=" + tz + ") and (" + tz + "<=" + HbpcdLookup.MccLookup.GMT_OFFSET_HIGH + ") and (0=" + DSTflag + ")) or ((" + HbpcdLookup.MccLookup.GMT_DST_LOW + "<=" + tz + ") and (" + tz + "<=" + HbpcdLookup.MccLookup.GMT_DST_HIGH + ") and (1=" + DSTflag + ")))", null, null);
        if (c3 != null) {
            int c3Counter = c3.getCount();
            if (c3Counter > 0) {
                if (c3Counter > 1) {
                    Log.m104w(LOG_TAG, "something wrong, get more results for 1 conflict SID: " + c3);
                }
                c3.moveToFirst();
                int tmpMcc2 = c3.getInt(0);
                if (!isNitzTimeZone) {
                    tmpMcc2 = 0;
                }
                c3.close();
                return tmpMcc2;
            }
            c3.close();
        }
        String[] projection5 = {"MCC"};
        Cursor c5 = this.resolver.query(HbpcdLookup.MccSidRange.CONTENT_URI, projection5, "SID_Range_Low<=" + sid + " and " + HbpcdLookup.MccSidRange.RANGE_HIGH + ">=" + sid, null, null);
        if (c5 != null) {
            if (c5.getCount() > 0) {
                c5.moveToFirst();
                int tmpMcc3 = c5.getInt(0);
                c5.close();
                return tmpMcc3;
            }
            c5.close();
        }
        return 0;
    }

    public String getIddByMcc(int mcc) {
        String idd = "";
        Cursor c = null;
        String[] projection = {HbpcdLookup.MccIdd.IDD};
        Cursor cur = this.resolver.query(HbpcdLookup.MccIdd.CONTENT_URI, projection, "MCC=" + mcc, null, null);
        if (cur != null) {
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                idd = cur.getString(0);
            }
            cur.close();
        }
        if (0 != 0) {
            c.close();
        }
        return idd;
    }
}
