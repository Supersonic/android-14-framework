package com.android.server.permission.access.permission;

import android.content.pm.PermissionInfo;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.modules.utils.BinaryXmlPullParser;
import com.android.modules.utils.BinaryXmlSerializer;
import com.android.server.permission.access.AccessState;
import com.android.server.permission.access.SystemState;
import com.android.server.permission.access.UserState;
import com.android.server.permission.jarjar.kotlin.jvm.internal.DefaultConstructorMarker;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
import org.xmlpull.v1.XmlPullParserException;
/* compiled from: UidPermissionPersistence.kt */
/* loaded from: classes2.dex */
public final class UidPermissionPersistence {
    public static final Companion Companion = new Companion(null);
    public static final String LOG_TAG = UidPermissionPersistence.class.getSimpleName();

    public final void parseSystemState(BinaryXmlPullParser binaryXmlPullParser, AccessState accessState) {
        SystemState systemState = accessState.getSystemState();
        String name = binaryXmlPullParser.getName();
        if (Intrinsics.areEqual(name, "permission-trees")) {
            parsePermissions(binaryXmlPullParser, systemState.getPermissionTrees());
        } else if (Intrinsics.areEqual(name, "permissions")) {
            parsePermissions(binaryXmlPullParser, systemState.getPermissions());
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:36:0x00a2, code lost:
        r0 = r10.next();
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00a6, code lost:
        if (r0 == 1) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00a8, code lost:
        if (r0 == 2) goto L51;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00aa, code lost:
        if (r0 == 3) goto L49;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void parseAppIdPermissions(BinaryXmlPullParser binaryXmlPullParser, ArrayMap<String, Integer> arrayMap) {
        int next;
        int next2;
        int next3;
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
                return;
            }
            if (eventType2 != 2) {
                if (eventType2 == 3) {
                    return;
                }
                throw new XmlPullParserException("Unexpected event type " + eventType2);
            }
            int depth = binaryXmlPullParser.getDepth();
            if (Intrinsics.areEqual(binaryXmlPullParser.getName(), "permission")) {
                parseAppIdPermission(binaryXmlPullParser, arrayMap);
            } else {
                String str = LOG_TAG;
                String name = binaryXmlPullParser.getName();
                Log.w(str, "Ignoring unknown tag " + name + " when parsing permission state");
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
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:36:0x009e, code lost:
        r0 = r10.next();
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00a2, code lost:
        if (r0 == 1) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00a4, code lost:
        if (r0 == 2) goto L51;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00a6, code lost:
        if (r0 == 3) goto L49;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void parsePermissions(BinaryXmlPullParser binaryXmlPullParser, ArrayMap<String, Permission> arrayMap) {
        int next;
        int next2;
        int next3;
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
                return;
            }
            if (eventType2 != 2) {
                if (eventType2 == 3) {
                    return;
                }
                throw new XmlPullParserException("Unexpected event type " + eventType2);
            }
            int depth = binaryXmlPullParser.getDepth();
            String name = binaryXmlPullParser.getName();
            if (Intrinsics.areEqual(name, "permission")) {
                parsePermission(binaryXmlPullParser, arrayMap);
            } else {
                String str = LOG_TAG;
                Log.w(str, "Ignoring unknown tag " + name + " when parsing permissions");
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
        }
    }

    public final void serializeAppId(BinaryXmlSerializer binaryXmlSerializer, int i, ArrayMap<String, Integer> arrayMap) {
        binaryXmlSerializer.startTag((String) null, "app-id");
        binaryXmlSerializer.attributeInt((String) null, "id", i);
        serializeAppIdPermissions(binaryXmlSerializer, arrayMap);
        binaryXmlSerializer.endTag((String) null, "app-id");
    }

    public final void serializeAppIdPermission(BinaryXmlSerializer binaryXmlSerializer, String str, int i) {
        binaryXmlSerializer.startTag((String) null, "permission");
        binaryXmlSerializer.attributeInterned((String) null, "name", str);
        binaryXmlSerializer.attributeInt((String) null, "flags", i);
        binaryXmlSerializer.endTag((String) null, "permission");
    }

    public final void serializePermissionFlags(BinaryXmlSerializer binaryXmlSerializer, UserState userState) {
        binaryXmlSerializer.startTag((String) null, "permissions");
        SparseArray<ArrayMap<String, Integer>> uidPermissionFlags = userState.getUidPermissionFlags();
        int size = uidPermissionFlags.size();
        for (int i = 0; i < size; i++) {
            serializeAppId(binaryXmlSerializer, uidPermissionFlags.keyAt(i), uidPermissionFlags.valueAt(i));
        }
        binaryXmlSerializer.endTag((String) null, "permissions");
    }

    public final void serializePermissions(BinaryXmlSerializer binaryXmlSerializer, String str, ArrayMap<String, Permission> arrayMap) {
        binaryXmlSerializer.startTag((String) null, str);
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            serializePermission(binaryXmlSerializer, arrayMap.valueAt(i));
        }
        binaryXmlSerializer.endTag((String) null, str);
    }

    public final void serializeAppIdPermissions(BinaryXmlSerializer binaryXmlSerializer, ArrayMap<String, Integer> arrayMap) {
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            serializeAppIdPermission(binaryXmlSerializer, arrayMap.keyAt(i), arrayMap.valueAt(i).intValue());
        }
    }

    public final void serializeSystemState(BinaryXmlSerializer binaryXmlSerializer, AccessState accessState) {
        SystemState systemState = accessState.getSystemState();
        serializePermissions(binaryXmlSerializer, "permission-trees", systemState.getPermissionTrees());
        serializePermissions(binaryXmlSerializer, "permissions", systemState.getPermissions());
    }

    public final void serializePermission(BinaryXmlSerializer binaryXmlSerializer, Permission permission) {
        String obj;
        int type = permission.getType();
        if (type != 0) {
            if (type == 1) {
                return;
            }
            if (type != 2) {
                String str = LOG_TAG;
                String name = binaryXmlSerializer.getName();
                Log.w(str, "Skipping serializing permission " + name + " with unknown type " + type);
                return;
            }
        }
        binaryXmlSerializer.startTag((String) null, "permission");
        binaryXmlSerializer.attributeInterned((String) null, "name", permission.getPermissionInfo().name);
        binaryXmlSerializer.attributeInterned((String) null, "packageName", permission.getPermissionInfo().packageName);
        binaryXmlSerializer.attributeIntHex((String) null, "protectionLevel", permission.getPermissionInfo().protectionLevel);
        binaryXmlSerializer.attributeInt((String) null, "type", type);
        if (type == 2) {
            PermissionInfo permissionInfo = permission.getPermissionInfo();
            int i = permissionInfo.icon;
            if (i != 0) {
                binaryXmlSerializer.attributeIntHex((String) null, "icon", i);
            }
            CharSequence charSequence = permissionInfo.nonLocalizedLabel;
            if (charSequence != null && (obj = charSequence.toString()) != null) {
                binaryXmlSerializer.attribute((String) null, "label", obj);
            }
        }
        binaryXmlSerializer.endTag((String) null, "permission");
    }

    public final void parseUserState(BinaryXmlPullParser binaryXmlPullParser, AccessState accessState, int i) {
        if (Intrinsics.areEqual(binaryXmlPullParser.getName(), "permissions")) {
            parsePermissionFlags(binaryXmlPullParser, accessState, i);
        }
    }

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
    public final void parsePermissionFlags(BinaryXmlPullParser binaryXmlPullParser, AccessState accessState, int i) {
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
                    Log.w(LOG_TAG, "Ignoring unknown tag " + binaryXmlPullParser.getName() + " when parsing permission state");
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
        SparseArray<ArrayMap<String, Integer>> uidPermissionFlags = userState.getUidPermissionFlags();
        for (int size = uidPermissionFlags.size() - 1; -1 < size; size--) {
            int keyAt = uidPermissionFlags.keyAt(size);
            uidPermissionFlags.valueAt(size);
            boolean contains = accessState.getSystemState().getAppIds().contains(keyAt);
            if (!contains) {
                Log.w(LOG_TAG, "Dropping unknown app ID " + keyAt + " when parsing permission state");
            }
            if (!contains) {
                uidPermissionFlags.removeAt(size);
            }
        }
    }

    public final void parseAppIdPermission(BinaryXmlPullParser binaryXmlPullParser, ArrayMap<String, Integer> arrayMap) {
        String intern = binaryXmlPullParser.getAttributeValue(binaryXmlPullParser.getAttributeIndexOrThrow((String) null, "name")).intern();
        Intrinsics.checkNotNullExpressionValue(intern, "this as java.lang.String).intern()");
        arrayMap.put(intern, Integer.valueOf(binaryXmlPullParser.getAttributeInt((String) null, "flags")));
    }

    public final void parsePermission(BinaryXmlPullParser binaryXmlPullParser, ArrayMap<String, Permission> arrayMap) {
        String intern = binaryXmlPullParser.getAttributeValue(binaryXmlPullParser.getAttributeIndexOrThrow((String) null, "name")).intern();
        Intrinsics.checkNotNullExpressionValue(intern, "this as java.lang.String).intern()");
        PermissionInfo permissionInfo = new PermissionInfo();
        permissionInfo.name = intern;
        String intern2 = binaryXmlPullParser.getAttributeValue(binaryXmlPullParser.getAttributeIndexOrThrow((String) null, "packageName")).intern();
        Intrinsics.checkNotNullExpressionValue(intern2, "this as java.lang.String).intern()");
        permissionInfo.packageName = intern2;
        permissionInfo.protectionLevel = binaryXmlPullParser.getAttributeIntHex((String) null, "protectionLevel");
        int attributeInt = binaryXmlPullParser.getAttributeInt((String) null, "type");
        if (attributeInt != 0) {
            if (attributeInt == 1) {
                String str = LOG_TAG;
                Log.w(str, "Ignoring unexpected config permission " + intern);
                return;
            } else if (attributeInt == 2) {
                permissionInfo.icon = binaryXmlPullParser.getAttributeIntHex((String) null, "icon", 0);
                permissionInfo.nonLocalizedLabel = binaryXmlPullParser.getAttributeValue((String) null, "label");
            } else {
                String str2 = LOG_TAG;
                Log.w(str2, "Ignoring permission " + intern + " with unknown type " + attributeInt);
                return;
            }
        }
        arrayMap.put(intern, new Permission(permissionInfo, false, attributeInt, 0, null, false, 48, null));
    }

    public final void serializeUserState(BinaryXmlSerializer binaryXmlSerializer, AccessState accessState, int i) {
        serializePermissionFlags(binaryXmlSerializer, accessState.getUserStates().get(i));
    }

    public final void parseAppId(BinaryXmlPullParser binaryXmlPullParser, UserState userState) {
        int attributeInt = binaryXmlPullParser.getAttributeInt((String) null, "id");
        ArrayMap<String, Integer> arrayMap = new ArrayMap<>();
        userState.getUidPermissionFlags().set(attributeInt, arrayMap);
        parseAppIdPermissions(binaryXmlPullParser, arrayMap);
    }

    /* compiled from: UidPermissionPersistence.kt */
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public Companion() {
        }
    }
}
