package android.text.style;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.p008os.Parcel;
import android.provider.Browser;
import android.text.ParcelableSpan;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
/* loaded from: classes3.dex */
public class URLSpan extends ClickableSpan implements ParcelableSpan {
    private final String mURL;

    public URLSpan(String url) {
        this.mURL = url;
    }

    public URLSpan(Parcel src) {
        this.mURL = src.readString();
    }

    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    public int getSpanTypeIdInternal() {
        return 11;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        writeToParcelInternal(dest, flags);
    }

    public void writeToParcelInternal(Parcel dest, int flags) {
        dest.writeString(this.mURL);
    }

    public String getURL() {
        return this.mURL;
    }

    @Override // android.text.style.ClickableSpan
    public void onClick(View widget) {
        Uri uri = Uri.parse(getURL());
        Context context = widget.getContext();
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.m104w("URLSpan", "Actvity was not found for intent, " + intent.toString());
        }
    }

    @Override // android.text.style.ClickableSpan
    public String toString() {
        return "URLSpan{URL='" + getURL() + DateFormat.QUOTE + '}';
    }
}
