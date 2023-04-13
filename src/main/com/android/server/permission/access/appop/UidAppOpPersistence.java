package com.android.server.permission.access.appop;

import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.modules.utils.BinaryXmlPullParser;
import com.android.modules.utils.BinaryXmlSerializer;
import com.android.server.permission.access.AccessState;
import com.android.server.permission.access.UserState;
import com.android.server.permission.jarjar.kotlin.jvm.internal.DefaultConstructorMarker;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
import org.xmlpull.v1.XmlPullParserException;
/* compiled from: UidAppOpPersistence.kt */
/* loaded from: classes2.dex */
public final class UidAppOpPersistence extends BaseAppOpPersistence {
    public static final Companion Companion = new Companion(null);
    public static final String LOG_TAG = UidAppOpPersistence.class.getSimpleName();

    /* JADX WARN: Code restructure failed: missing block: B:36:0x00ab, code lost:
        r0 = r11.next();
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00af, code lost:
        if (r0 == 1) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00b1, code lost:
        if (r0 == 2) goto L51;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00b3, code lost:
        if (r0 == 3) goto L49;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void parseUidAppOps(BinaryXmlPullParser binaryXmlPullParser, AccessState accessState, int i) {
        int next;
        int next2;
        int next3;
        UserState userState = accessState.getUserStates().get(i);
        int eventType = binaryXmlPullParser.getEventType();
        if (eventType != 0 && eventType != 2) {
            throw new XmlPullParserException("Unexpected event type " + eventType);
        }
        do {
            next = binaryXmlPullParser.next();
            if (next == 1 || next == 2) {
                break;
            }
        } while (next != 3);
        while (true) {
            int eventType2 = binaryXmlPullParser.getEventType();
            if (eventType2 == 1) {
                break;
            } else if (eventType2 == 2) {
                int depth = binaryXmlPullParser.getDepth();
                if (Intrinsics.areEqual(binaryXmlPullParser.getName(), "app-id")) {
                    parseAppId(binaryXmlPullParser, userState);
                } else {
                    Log.w(LOG_TAG, "Ignoring unknown tag " + binaryXmlPullParser.getName() + " when parsing app-op state");
                }
                int depth2 = binaryXmlPullParser.getDepth();
                if (depth2 != depth) {
                    throw new XmlPullParserException("Unexpected post-block depth " + depth2 + ", expected " + depth);
                }
                while (true) {
                    int eventType3 = binaryXmlPullParser.getEventType();
                    if (eventType3 == 2) {
                        do {
                            next3 = binaryXmlPullParser.next();
                            if (next3 != 1 && next3 != 2) {
                            }
                        } while (next3 != 3);
                    } else if (eventType3 != 3) {
                        throw new XmlPullParserException("Unexpected event type " + eventType3);
                    } else if (binaryXmlPullParser.getDepth() <= depth) {
                        break;
                    } else {
                        do {
                            next2 = binaryXmlPullParser.next();
                            if (next2 != 1 && next2 != 2) {
                            }
                        } while (next2 != 3);
                    }
                }
            } else if (eventType2 != 3) {
                throw new XmlPullParserException("Unexpected event type " + eventType2);
            }
        }
        SparseArray<ArrayMap<String, Integer>> uidAppOpModes = userState.getUidAppOpModes();
        for (int size = uidAppOpModes.size() - 1; -1 < size; size--) {
            int keyAt = uidAppOpModes.keyAt(size);
            uidAppOpModes.valueAt(size);
            boolean contains = accessState.getSystemState().getAppIds().contains(keyAt);
            if (!contains) {
                Log.w(LOG_TAG, "Dropping unknown app ID " + keyAt + " when parsing app-op state");
            }
            if (!contains) {
                uidAppOpModes.removeAt(size);
            }
        }
    }

    public final void serializeAppId(BinaryXmlSerializer binaryXmlSerializer, int i, ArrayMap<String, Integer> arrayMap) {
        binaryXmlSerializer.startTag((String) null, "app-id");
        binaryXmlSerializer.attributeInt((String) null, "id", i);
        serializeAppOps(binaryXmlSerializer, arrayMap);
        binaryXmlSerializer.endTag((String) null, "app-id");
    }

    public final void serializeUidAppOps(BinaryXmlSerializer binaryXmlSerializer, UserState userState) {
        binaryXmlSerializer.startTag((String) null, "uid-app-ops");
        SparseArray<ArrayMap<String, Integer>> uidAppOpModes = userState.getUidAppOpModes();
        int size = uidAppOpModes.size();
        for (int i = 0; i < size; i++) {
            serializeAppId(binaryXmlSerializer, uidAppOpModes.keyAt(i), uidAppOpModes.valueAt(i));
        }
        binaryXmlSerializer.endTag((String) null, "uid-app-ops");
    }

    @Override // com.android.server.permission.access.appop.BaseAppOpPersistence
    public void serializeUserState(BinaryXmlSerializer binaryXmlSerializer, AccessState accessState, int i) {
        serializeUidAppOps(binaryXmlSerializer, accessState.getUserStates().get(i));
    }

    /* compiled from: UidAppOpPersistence.kt */
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public Companion() {
        }
    }

    @Override // com.android.server.permission.access.appop.BaseAppOpPersistence
    public void parseUserState(BinaryXmlPullParser binaryXmlPullParser, AccessState accessState, int i) {
        if (Intrinsics.areEqual(binaryXmlPullParser.getName(), "uid-app-ops")) {
            parseUidAppOps(binaryXmlPullParser, accessState, i);
        }
    }

    public final void parseAppId(BinaryXmlPullParser binaryXmlPullParser, UserState userState) {
        int attributeInt = binaryXmlPullParser.getAttributeInt((String) null, "id");
        ArrayMap<String, Integer> arrayMap = new ArrayMap<>();
        userState.getUidAppOpModes().set(attributeInt, arrayMap);
        parseAppOps(binaryXmlPullParser, arrayMap);
    }
}
