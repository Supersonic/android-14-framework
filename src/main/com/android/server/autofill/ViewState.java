package com.android.server.autofill;

import android.graphics.Rect;
import android.p005os.IInstalld;
import android.service.autofill.FillResponse;
import android.util.DebugUtils;
import android.util.Slog;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class ViewState {

    /* renamed from: id */
    public final AutofillId f1130id;
    public AutofillValue mAutofilledValue;
    public AutofillValue mCurrentValue;
    public String mDatasetId;
    public final Listener mListener;
    public FillResponse mResponse;
    public AutofillValue mSanitizedValue;
    public int mState;
    public Rect mVirtualBounds;

    /* loaded from: classes.dex */
    public interface Listener {
        void onFillReady(FillResponse fillResponse, AutofillId autofillId, AutofillValue autofillValue, int i);
    }

    public ViewState(AutofillId autofillId, Listener listener, int i) {
        this.f1130id = autofillId;
        this.mListener = listener;
        this.mState = i;
    }

    public Rect getVirtualBounds() {
        return this.mVirtualBounds;
    }

    public AutofillValue getCurrentValue() {
        return this.mCurrentValue;
    }

    public void setCurrentValue(AutofillValue autofillValue) {
        this.mCurrentValue = autofillValue;
    }

    public AutofillValue getAutofilledValue() {
        return this.mAutofilledValue;
    }

    public void setAutofilledValue(AutofillValue autofillValue) {
        this.mAutofilledValue = autofillValue;
    }

    public AutofillValue getSanitizedValue() {
        return this.mSanitizedValue;
    }

    public void setSanitizedValue(AutofillValue autofillValue) {
        this.mSanitizedValue = autofillValue;
    }

    public FillResponse getResponse() {
        return this.mResponse;
    }

    public void setResponse(FillResponse fillResponse) {
        this.mResponse = fillResponse;
    }

    public int getState() {
        return this.mState;
    }

    public String getStateAsString() {
        return getStateAsString(this.mState);
    }

    public static String getStateAsString(int i) {
        return DebugUtils.flagsToString(ViewState.class, "STATE_", i);
    }

    public void setState(int i) {
        int i2 = this.mState;
        if (i2 == 1) {
            this.mState = i;
        } else {
            this.mState = i2 | i;
        }
        if (i == 4) {
            this.mState |= IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES;
        }
    }

    public void resetState(int i) {
        this.mState = (~i) & this.mState;
    }

    public String getDatasetId() {
        return this.mDatasetId;
    }

    public void setDatasetId(String str) {
        this.mDatasetId = str;
    }

    public void update(AutofillValue autofillValue, Rect rect, int i) {
        if (autofillValue != null) {
            this.mCurrentValue = autofillValue;
        }
        if (rect != null) {
            this.mVirtualBounds = rect;
        }
        maybeCallOnFillReady(i);
    }

    public void maybeCallOnFillReady(int i) {
        if ((this.mState & 4) != 0 && (i & 1) == 0) {
            if (Helper.sDebug) {
                Slog.d("ViewState", "Ignoring UI for " + this.f1130id + " on " + getStateAsString());
                return;
            }
            return;
        }
        FillResponse fillResponse = this.mResponse;
        if (fillResponse != null) {
            if (fillResponse.getDatasets() == null && this.mResponse.getAuthentication() == null) {
                return;
            }
            this.mListener.onFillReady(this.mResponse, this.f1130id, this.mCurrentValue, i);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ViewState: [id=");
        sb.append(this.f1130id);
        if (this.mDatasetId != null) {
            sb.append(", datasetId:");
            sb.append(this.mDatasetId);
        }
        sb.append(", state:");
        sb.append(getStateAsString());
        if (this.mCurrentValue != null) {
            sb.append(", currentValue:");
            sb.append(this.mCurrentValue);
        }
        if (this.mAutofilledValue != null) {
            sb.append(", autofilledValue:");
            sb.append(this.mAutofilledValue);
        }
        if (this.mSanitizedValue != null) {
            sb.append(", sanitizedValue:");
            sb.append(this.mSanitizedValue);
        }
        if (this.mVirtualBounds != null) {
            sb.append(", virtualBounds:");
            sb.append(this.mVirtualBounds);
        }
        sb.append("]");
        return sb.toString();
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.print(str);
        printWriter.print("id:");
        printWriter.println(this.f1130id);
        if (this.mDatasetId != null) {
            printWriter.print(str);
            printWriter.print("datasetId:");
            printWriter.println(this.mDatasetId);
        }
        printWriter.print(str);
        printWriter.print("state:");
        printWriter.println(getStateAsString());
        if (this.mResponse != null) {
            printWriter.print(str);
            printWriter.print("response id:");
            printWriter.println(this.mResponse.getRequestId());
        }
        if (this.mCurrentValue != null) {
            printWriter.print(str);
            printWriter.print("currentValue:");
            printWriter.println(this.mCurrentValue);
        }
        if (this.mAutofilledValue != null) {
            printWriter.print(str);
            printWriter.print("autofilledValue:");
            printWriter.println(this.mAutofilledValue);
        }
        if (this.mSanitizedValue != null) {
            printWriter.print(str);
            printWriter.print("sanitizedValue:");
            printWriter.println(this.mSanitizedValue);
        }
        if (this.mVirtualBounds != null) {
            printWriter.print(str);
            printWriter.print("virtualBounds:");
            printWriter.println(this.mVirtualBounds);
        }
    }
}
