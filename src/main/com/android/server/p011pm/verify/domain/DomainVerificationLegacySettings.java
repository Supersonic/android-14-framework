package com.android.server.p011pm.verify.domain;

import android.content.pm.IntentFilterVerificationInfo;
import android.util.ArrayMap;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.p011pm.SettingsXml;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.verify.domain.DomainVerificationLegacySettings */
/* loaded from: classes2.dex */
public class DomainVerificationLegacySettings {
    public final Object mLock = new Object();
    public final ArrayMap<String, LegacyState> mStates = new ArrayMap<>();

    public void add(String str, IntentFilterVerificationInfo intentFilterVerificationInfo) {
        synchronized (this.mLock) {
            getOrCreateStateLocked(str).setInfo(intentFilterVerificationInfo);
        }
    }

    public void add(String str, int i, int i2) {
        synchronized (this.mLock) {
            getOrCreateStateLocked(str).addUserState(i, i2);
        }
    }

    public int getUserState(String str, int i) {
        synchronized (this.mLock) {
            LegacyState legacyState = this.mStates.get(str);
            if (legacyState != null) {
                return legacyState.getUserState(i);
            }
            return 0;
        }
    }

    public SparseIntArray getUserStates(String str) {
        synchronized (this.mLock) {
            LegacyState legacyState = this.mStates.get(str);
            if (legacyState != null) {
                return legacyState.getUserStates();
            }
            return null;
        }
    }

    public IntentFilterVerificationInfo remove(String str) {
        synchronized (this.mLock) {
            LegacyState legacyState = this.mStates.get(str);
            if (legacyState == null || legacyState.isAttached()) {
                return null;
            }
            legacyState.markAttached();
            return legacyState.getInfo();
        }
    }

    @GuardedBy({"mLock"})
    public final LegacyState getOrCreateStateLocked(String str) {
        LegacyState legacyState = this.mStates.get(str);
        if (legacyState == null) {
            LegacyState legacyState2 = new LegacyState();
            this.mStates.put(str, legacyState2);
            return legacyState2;
        }
        return legacyState;
    }

    public void writeSettings(TypedXmlSerializer typedXmlSerializer) throws IOException {
        SettingsXml.Serializer serializer = SettingsXml.serializer(typedXmlSerializer);
        try {
            SettingsXml.WriteSection startSection = serializer.startSection("domain-verifications-legacy");
            synchronized (this.mLock) {
                int size = this.mStates.size();
                for (int i = 0; i < size; i++) {
                    SparseIntArray userStates = this.mStates.valueAt(i).getUserStates();
                    if (userStates != null) {
                        SettingsXml.WriteSection attribute = serializer.startSection("user-states").attribute("packageName", this.mStates.keyAt(i));
                        int size2 = userStates.size();
                        for (int i2 = 0; i2 < size2; i2++) {
                            attribute.startSection("user-state").attribute("userId", userStates.keyAt(i2)).attribute("state", userStates.valueAt(i2)).finish();
                        }
                        if (attribute != null) {
                            attribute.close();
                        }
                    }
                }
            }
            if (startSection != null) {
                startSection.close();
            }
            serializer.close();
        } catch (Throwable th) {
            if (serializer != null) {
                try {
                    serializer.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public void readSettings(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        SettingsXml.ChildSection children = SettingsXml.parser(typedXmlPullParser).children();
        while (children.moveToNext()) {
            if ("user-states".equals(children.getName())) {
                readUserStates(children);
            }
        }
    }

    public final void readUserStates(SettingsXml.ReadSection readSection) {
        String string = readSection.getString("packageName");
        synchronized (this.mLock) {
            LegacyState orCreateStateLocked = getOrCreateStateLocked(string);
            SettingsXml.ChildSection children = readSection.children();
            while (children.moveToNext()) {
                if ("user-state".equals(children.getName())) {
                    readUserState(children, orCreateStateLocked);
                }
            }
        }
    }

    public final void readUserState(SettingsXml.ReadSection readSection, LegacyState legacyState) {
        legacyState.addUserState(readSection.getInt("userId"), readSection.getInt("state"));
    }

    /* renamed from: com.android.server.pm.verify.domain.DomainVerificationLegacySettings$LegacyState */
    /* loaded from: classes2.dex */
    public static class LegacyState {
        public boolean attached;
        public IntentFilterVerificationInfo mInfo;
        public SparseIntArray mUserStates;

        public IntentFilterVerificationInfo getInfo() {
            return this.mInfo;
        }

        public int getUserState(int i) {
            return this.mUserStates.get(i, 0);
        }

        public SparseIntArray getUserStates() {
            return this.mUserStates;
        }

        public void setInfo(IntentFilterVerificationInfo intentFilterVerificationInfo) {
            this.mInfo = intentFilterVerificationInfo;
        }

        public void addUserState(int i, int i2) {
            if (this.mUserStates == null) {
                this.mUserStates = new SparseIntArray(1);
            }
            this.mUserStates.put(i, i2);
        }

        public boolean isAttached() {
            return this.attached;
        }

        public void markAttached() {
            this.attached = true;
        }
    }
}
