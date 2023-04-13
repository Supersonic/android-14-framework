package com.android.server.p011pm;

import android.content.ComponentName;
import android.content.IntentFilter;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.utils.SnapshotCache;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.PersistentPreferredActivity */
/* loaded from: classes2.dex */
public class PersistentPreferredActivity extends WatchedIntentFilter {
    public final ComponentName mComponent;
    public final boolean mIsSetByDpm;
    public final SnapshotCache<PersistentPreferredActivity> mSnapshot;

    public final SnapshotCache makeCache() {
        return new SnapshotCache<PersistentPreferredActivity>(this, this) { // from class: com.android.server.pm.PersistentPreferredActivity.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.server.utils.SnapshotCache
            public PersistentPreferredActivity createSnapshot() {
                PersistentPreferredActivity persistentPreferredActivity = new PersistentPreferredActivity();
                persistentPreferredActivity.seal();
                return persistentPreferredActivity;
            }
        };
    }

    public PersistentPreferredActivity(IntentFilter intentFilter, ComponentName componentName, boolean z) {
        super(intentFilter);
        this.mComponent = componentName;
        this.mIsSetByDpm = z;
        this.mSnapshot = makeCache();
    }

    public PersistentPreferredActivity(WatchedIntentFilter watchedIntentFilter, ComponentName componentName, boolean z) {
        this(watchedIntentFilter.mFilter, componentName, z);
    }

    public PersistentPreferredActivity(PersistentPreferredActivity persistentPreferredActivity) {
        super(persistentPreferredActivity);
        this.mComponent = persistentPreferredActivity.mComponent;
        this.mIsSetByDpm = persistentPreferredActivity.mIsSetByDpm;
        this.mSnapshot = new SnapshotCache.Sealed();
    }

    public PersistentPreferredActivity(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
        ComponentName unflattenFromString = ComponentName.unflattenFromString(attributeValue);
        this.mComponent = unflattenFromString;
        if (unflattenFromString == null) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: Bad activity name " + attributeValue + " at " + typedXmlPullParser.getPositionDescription());
        }
        this.mIsSetByDpm = typedXmlPullParser.getAttributeBoolean((String) null, "set-by-dpm", false);
        int depth = typedXmlPullParser.getDepth();
        String name = typedXmlPullParser.getName();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            }
            name = typedXmlPullParser.getName();
            if (next != 3 && next != 4 && next == 2) {
                if (name.equals("filter")) {
                    break;
                }
                PackageManagerService.reportSettingsProblem(5, "Unknown element: " + name + " at " + typedXmlPullParser.getPositionDescription());
                XmlUtils.skipCurrentTag(typedXmlPullParser);
            }
        }
        if (name.equals("filter")) {
            this.mFilter.readFromXml(typedXmlPullParser);
        } else {
            PackageManagerService.reportSettingsProblem(5, "Missing element filter at " + typedXmlPullParser.getPositionDescription());
            XmlUtils.skipCurrentTag(typedXmlPullParser);
        }
        this.mSnapshot = makeCache();
    }

    public void writeToXml(TypedXmlSerializer typedXmlSerializer) throws IOException {
        typedXmlSerializer.attribute((String) null, "name", this.mComponent.flattenToShortString());
        typedXmlSerializer.attributeBoolean((String) null, "set-by-dpm", this.mIsSetByDpm);
        typedXmlSerializer.startTag((String) null, "filter");
        this.mFilter.writeToXml(typedXmlSerializer);
        typedXmlSerializer.endTag((String) null, "filter");
    }

    @Override // com.android.server.p011pm.WatchedIntentFilter
    public IntentFilter getIntentFilter() {
        return this.mFilter;
    }

    public String toString() {
        return "PersistentPreferredActivity{0x" + Integer.toHexString(System.identityHashCode(this)) + " " + this.mComponent.flattenToShortString() + ", mIsSetByDpm=" + this.mIsSetByDpm + "}";
    }

    @Override // com.android.server.p011pm.WatchedIntentFilter, com.android.server.utils.Snappable
    public PersistentPreferredActivity snapshot() {
        return this.mSnapshot.snapshot();
    }
}
