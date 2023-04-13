package android.view.textservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.p001pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.AttributeSet;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.C4057R;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes4.dex */
public final class SpellCheckerInfo implements Parcelable {
    private final String mId;
    private final int mLabel;
    private final ResolveInfo mService;
    private final String mSettingsActivityName;
    private final ArrayList<SpellCheckerSubtype> mSubtypes;
    private static final String TAG = SpellCheckerInfo.class.getSimpleName();
    public static final Parcelable.Creator<SpellCheckerInfo> CREATOR = new Parcelable.Creator<SpellCheckerInfo>() { // from class: android.view.textservice.SpellCheckerInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SpellCheckerInfo createFromParcel(Parcel source) {
            return new SpellCheckerInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SpellCheckerInfo[] newArray(int size) {
            return new SpellCheckerInfo[size];
        }
    };

    public SpellCheckerInfo(Context context, ResolveInfo service) throws XmlPullParserException, IOException {
        int i;
        Resources res;
        this.mSubtypes = new ArrayList<>();
        this.mService = service;
        ServiceInfo si = service.serviceInfo;
        this.mId = new ComponentName(si.packageName, si.name).flattenToShortString();
        PackageManager pm = context.getPackageManager();
        XmlResourceParser parser = null;
        try {
            try {
                parser = si.loadXmlMetaData(pm, SpellCheckerSession.SERVICE_META_DATA);
                if (parser == null) {
                    throw new XmlPullParserException("No android.view.textservice.scs meta-data");
                }
                Resources res2 = pm.getResourcesForApplication(si.applicationInfo);
                AttributeSet attrs = Xml.asAttributeSet(parser);
                while (true) {
                    int type = parser.next();
                    i = 1;
                    if (type == 1 || type == 2) {
                        break;
                    }
                }
                String nodeName = parser.getName();
                if (!"spell-checker".equals(nodeName)) {
                    throw new XmlPullParserException("Meta-data does not start with spell-checker tag");
                }
                TypedArray sa = res2.obtainAttributes(attrs, C4057R.styleable.SpellChecker);
                int label = sa.getResourceId(0, 0);
                String settingsActivityComponent = sa.getString(1);
                sa.recycle();
                int depth = parser.getDepth();
                while (true) {
                    int type2 = parser.next();
                    if ((type2 == 3 && parser.getDepth() <= depth) || type2 == i) {
                        break;
                    }
                    if (type2 != 2) {
                        res = res2;
                    } else {
                        String subtypeNodeName = parser.getName();
                        if (!"subtype".equals(subtypeNodeName)) {
                            throw new XmlPullParserException("Meta-data in spell-checker does not start with subtype tag");
                        }
                        TypedArray a = res2.obtainAttributes(attrs, C4057R.styleable.SpellChecker_Subtype);
                        res = res2;
                        SpellCheckerSubtype subtype = new SpellCheckerSubtype(a.getResourceId(0, 0), a.getString(1), a.getString(4), a.getString(2), a.getInt(3, 0));
                        a.recycle();
                        this.mSubtypes.add(subtype);
                    }
                    res2 = res;
                    i = 1;
                }
                this.mLabel = label;
                this.mSettingsActivityName = settingsActivityComponent;
            } catch (Exception e) {
                Slog.m96e(TAG, "Caught exception: " + e);
                throw new XmlPullParserException("Unable to create context for: " + si.packageName);
            }
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }

    public SpellCheckerInfo(Parcel source) {
        ArrayList<SpellCheckerSubtype> arrayList = new ArrayList<>();
        this.mSubtypes = arrayList;
        this.mLabel = source.readInt();
        this.mId = source.readString();
        this.mSettingsActivityName = source.readString();
        this.mService = ResolveInfo.CREATOR.createFromParcel(source);
        source.readTypedList(arrayList, SpellCheckerSubtype.CREATOR);
    }

    public String getId() {
        return this.mId;
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
    }

    public String getPackageName() {
        return this.mService.serviceInfo.packageName;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mLabel);
        dest.writeString(this.mId);
        dest.writeString(this.mSettingsActivityName);
        this.mService.writeToParcel(dest, flags);
        dest.writeTypedList(this.mSubtypes);
    }

    public CharSequence loadLabel(PackageManager pm) {
        if (this.mLabel == 0 || pm == null) {
            return "";
        }
        return pm.getText(getPackageName(), this.mLabel, this.mService.serviceInfo.applicationInfo);
    }

    public Drawable loadIcon(PackageManager pm) {
        return this.mService.loadIcon(pm);
    }

    public ServiceInfo getServiceInfo() {
        return this.mService.serviceInfo;
    }

    public String getSettingsActivity() {
        return this.mSettingsActivityName;
    }

    public int getSubtypeCount() {
        return this.mSubtypes.size();
    }

    public SpellCheckerSubtype getSubtypeAt(int index) {
        return this.mSubtypes.get(index);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.println(prefix + "mId=" + this.mId);
        pw.println(prefix + "mSettingsActivityName=" + this.mSettingsActivityName);
        pw.println(prefix + "Service:");
        this.mService.dump(new PrintWriterPrinter(pw), prefix + "  ");
        int N = getSubtypeCount();
        for (int i = 0; i < N; i++) {
            SpellCheckerSubtype st = getSubtypeAt(i);
            pw.println(prefix + "  Subtype #" + i + ":");
            pw.println(prefix + "    locale=" + st.getLocale() + " languageTag=" + st.getLanguageTag());
            pw.println(prefix + "    extraValue=" + st.getExtraValue());
        }
    }
}
