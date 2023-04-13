package android.view.accessibility;

import android.graphics.Rect;
import android.graphics.Region;
import android.p008os.Bundle;
import android.p008os.LocaleList;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.LongArray;
import android.util.Pools;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes4.dex */
public final class AccessibilityWindowInfo implements Parcelable {
    public static final int ACTIVE_WINDOW_ID = Integer.MAX_VALUE;
    public static final int ANY_WINDOW_ID = -2;
    private static final int BOOLEAN_PROPERTY_ACCESSIBILITY_FOCUSED = 4;
    private static final int BOOLEAN_PROPERTY_ACTIVE = 1;
    private static final int BOOLEAN_PROPERTY_FOCUSED = 2;
    private static final int BOOLEAN_PROPERTY_PICTURE_IN_PICTURE = 8;
    private static final boolean DEBUG = false;
    private static final int MAX_POOL_SIZE = 10;
    public static final int PICTURE_IN_PICTURE_ACTION_REPLACER_WINDOW_ID = -3;
    public static final int TYPE_ACCESSIBILITY_OVERLAY = 4;
    public static final int TYPE_APPLICATION = 1;
    public static final int TYPE_INPUT_METHOD = 2;
    public static final int TYPE_MAGNIFICATION_OVERLAY = 6;
    public static final int TYPE_SPLIT_SCREEN_DIVIDER = 5;
    public static final int TYPE_SYSTEM = 3;
    public static final int UNDEFINED_CONNECTION_ID = -1;
    public static final int UNDEFINED_WINDOW_ID = -1;
    private static AtomicInteger sNumInstancesInUse;
    private int mBooleanProperties;
    private LongArray mChildIds;
    private CharSequence mTitle;
    private long mTransitionTime;
    private static final Pools.SynchronizedPool<AccessibilityWindowInfo> sPool = new Pools.SynchronizedPool<>(10);
    public static final Parcelable.Creator<AccessibilityWindowInfo> CREATOR = new Parcelable.Creator<AccessibilityWindowInfo>() { // from class: android.view.accessibility.AccessibilityWindowInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AccessibilityWindowInfo createFromParcel(Parcel parcel) {
            AccessibilityWindowInfo info = AccessibilityWindowInfo.obtain();
            info.initFromParcel(parcel);
            return info;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AccessibilityWindowInfo[] newArray(int size) {
            return new AccessibilityWindowInfo[size];
        }
    };
    private int mDisplayId = -1;
    private int mType = -1;
    private int mLayer = -1;
    private int mId = -1;
    private int mParentId = -1;
    private int mTaskId = -1;
    private Region mRegionInScreen = new Region();
    private long mAnchorId = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
    private int mConnectionId = -1;
    private LocaleList mLocales = LocaleList.getEmptyLocaleList();

    public AccessibilityWindowInfo() {
    }

    public AccessibilityWindowInfo(AccessibilityWindowInfo info) {
        init(info);
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public void setTitle(CharSequence title) {
        this.mTitle = title;
    }

    public int getType() {
        return this.mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public int getLayer() {
        return this.mLayer;
    }

    public void setLayer(int layer) {
        this.mLayer = layer;
    }

    public AccessibilityNodeInfo getRoot() {
        return getRoot(4);
    }

    public AccessibilityNodeInfo getRoot(int prefetchingStrategy) {
        if (this.mConnectionId == -1) {
            return null;
        }
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        return client.findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mId, AccessibilityNodeInfo.ROOT_NODE_ID, true, prefetchingStrategy, (Bundle) null);
    }

    public void setAnchorId(long anchorId) {
        this.mAnchorId = anchorId;
    }

    public AccessibilityNodeInfo getAnchor() {
        if (this.mConnectionId == -1 || this.mAnchorId == AccessibilityNodeInfo.UNDEFINED_NODE_ID || this.mParentId == -1) {
            return null;
        }
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        return client.findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mParentId, this.mAnchorId, true, 0, (Bundle) null);
    }

    public void setPictureInPicture(boolean pictureInPicture) {
        setBooleanProperty(8, pictureInPicture);
    }

    public boolean isInPictureInPictureMode() {
        return getBooleanProperty(8);
    }

    public AccessibilityWindowInfo getParent() {
        if (this.mConnectionId == -1 || this.mParentId == -1) {
            return null;
        }
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        return client.getWindow(this.mConnectionId, this.mParentId);
    }

    public void setParentId(int parentId) {
        this.mParentId = parentId;
    }

    public int getId() {
        return this.mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int getTaskId() {
        return this.mTaskId;
    }

    public void setTaskId(int taskId) {
        this.mTaskId = taskId;
    }

    public void setConnectionId(int connectionId) {
        this.mConnectionId = connectionId;
    }

    public void getRegionInScreen(Region outRegion) {
        outRegion.set(this.mRegionInScreen);
    }

    public void setRegionInScreen(Region region) {
        this.mRegionInScreen.set(region);
    }

    public void getBoundsInScreen(Rect outBounds) {
        outBounds.set(this.mRegionInScreen.getBounds());
    }

    public boolean isActive() {
        return getBooleanProperty(1);
    }

    public void setActive(boolean active) {
        setBooleanProperty(1, active);
    }

    public boolean isFocused() {
        return getBooleanProperty(2);
    }

    public void setFocused(boolean focused) {
        setBooleanProperty(2, focused);
    }

    public boolean isAccessibilityFocused() {
        return getBooleanProperty(4);
    }

    public void setAccessibilityFocused(boolean focused) {
        setBooleanProperty(4, focused);
    }

    public int getChildCount() {
        LongArray longArray = this.mChildIds;
        if (longArray != null) {
            return longArray.size();
        }
        return 0;
    }

    public AccessibilityWindowInfo getChild(int index) {
        LongArray longArray = this.mChildIds;
        if (longArray == null) {
            throw new IndexOutOfBoundsException();
        }
        if (this.mConnectionId == -1) {
            return null;
        }
        int childId = (int) longArray.get(index);
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        return client.getWindow(this.mConnectionId, childId);
    }

    public void addChild(int childId) {
        if (this.mChildIds == null) {
            this.mChildIds = new LongArray();
        }
        this.mChildIds.add(childId);
    }

    public void setDisplayId(int displayId) {
        this.mDisplayId = displayId;
    }

    public int getDisplayId() {
        return this.mDisplayId;
    }

    public void setTransitionTimeMillis(long transitionTime) {
        this.mTransitionTime = transitionTime;
    }

    public long getTransitionTimeMillis() {
        return this.mTransitionTime;
    }

    public void setLocales(LocaleList locales) {
        this.mLocales = locales;
    }

    public LocaleList getLocales() {
        return this.mLocales;
    }

    public static AccessibilityWindowInfo obtain() {
        AccessibilityWindowInfo info = sPool.acquire();
        if (info == null) {
            info = new AccessibilityWindowInfo();
        }
        AtomicInteger atomicInteger = sNumInstancesInUse;
        if (atomicInteger != null) {
            atomicInteger.incrementAndGet();
        }
        return info;
    }

    public static AccessibilityWindowInfo obtain(AccessibilityWindowInfo info) {
        AccessibilityWindowInfo infoClone = obtain();
        infoClone.init(info);
        return infoClone;
    }

    public static void setNumInstancesInUseCounter(AtomicInteger counter) {
        if (sNumInstancesInUse != null) {
            sNumInstancesInUse = counter;
        }
    }

    public void recycle() {
        clear();
        sPool.release(this);
        AtomicInteger atomicInteger = sNumInstancesInUse;
        if (atomicInteger != null) {
            atomicInteger.decrementAndGet();
        }
    }

    public boolean refresh() {
        if (this.mConnectionId == -1 || this.mId == -1) {
            return false;
        }
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        AccessibilityWindowInfo refreshedInfo = client.getWindow(this.mConnectionId, this.mId, true);
        if (refreshedInfo == null) {
            return false;
        }
        init(refreshedInfo);
        refreshedInfo.recycle();
        return true;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mDisplayId);
        parcel.writeInt(this.mType);
        parcel.writeInt(this.mLayer);
        parcel.writeInt(this.mBooleanProperties);
        parcel.writeInt(this.mId);
        parcel.writeInt(this.mParentId);
        parcel.writeInt(this.mTaskId);
        this.mRegionInScreen.writeToParcel(parcel, flags);
        parcel.writeCharSequence(this.mTitle);
        parcel.writeLong(this.mAnchorId);
        parcel.writeLong(this.mTransitionTime);
        LongArray childIds = this.mChildIds;
        if (childIds == null) {
            parcel.writeInt(0);
        } else {
            int childCount = childIds.size();
            parcel.writeInt(childCount);
            for (int i = 0; i < childCount; i++) {
                parcel.writeInt((int) childIds.get(i));
            }
        }
        parcel.writeInt(this.mConnectionId);
        parcel.writeParcelable(this.mLocales, flags);
    }

    private void init(AccessibilityWindowInfo other) {
        this.mDisplayId = other.mDisplayId;
        this.mType = other.mType;
        this.mLayer = other.mLayer;
        this.mBooleanProperties = other.mBooleanProperties;
        this.mId = other.mId;
        this.mParentId = other.mParentId;
        this.mTaskId = other.mTaskId;
        this.mRegionInScreen.set(other.mRegionInScreen);
        this.mTitle = other.mTitle;
        this.mAnchorId = other.mAnchorId;
        this.mTransitionTime = other.mTransitionTime;
        LongArray longArray = this.mChildIds;
        if (longArray != null) {
            longArray.clear();
        }
        LongArray longArray2 = other.mChildIds;
        if (longArray2 != null && longArray2.size() > 0) {
            LongArray longArray3 = this.mChildIds;
            if (longArray3 == null) {
                this.mChildIds = other.mChildIds.m4807clone();
            } else {
                longArray3.addAll(other.mChildIds);
            }
        }
        this.mConnectionId = other.mConnectionId;
        this.mLocales = other.mLocales;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initFromParcel(Parcel parcel) {
        this.mDisplayId = parcel.readInt();
        this.mType = parcel.readInt();
        this.mLayer = parcel.readInt();
        this.mBooleanProperties = parcel.readInt();
        this.mId = parcel.readInt();
        this.mParentId = parcel.readInt();
        this.mTaskId = parcel.readInt();
        this.mRegionInScreen = Region.CREATOR.createFromParcel(parcel);
        this.mTitle = parcel.readCharSequence();
        this.mAnchorId = parcel.readLong();
        this.mTransitionTime = parcel.readLong();
        int childCount = parcel.readInt();
        if (childCount > 0) {
            if (this.mChildIds == null) {
                this.mChildIds = new LongArray(childCount);
            }
            for (int i = 0; i < childCount; i++) {
                int childId = parcel.readInt();
                this.mChildIds.add(childId);
            }
        }
        int i2 = parcel.readInt();
        this.mConnectionId = i2;
        this.mLocales = (LocaleList) parcel.readParcelable(null, LocaleList.class);
    }

    public int hashCode() {
        return this.mId;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AccessibilityWindowInfo other = (AccessibilityWindowInfo) obj;
        if (this.mId == other.mId) {
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AccessibilityWindowInfo[");
        builder.append("title=").append(this.mTitle);
        builder.append(", displayId=").append(this.mDisplayId);
        builder.append(", id=").append(this.mId);
        builder.append(", taskId=").append(this.mTaskId);
        builder.append(", type=").append(typeToString(this.mType));
        builder.append(", layer=").append(this.mLayer);
        builder.append(", region=").append(this.mRegionInScreen);
        builder.append(", bounds=").append(this.mRegionInScreen.getBounds());
        builder.append(", focused=").append(isFocused());
        builder.append(", active=").append(isActive());
        builder.append(", pictureInPicture=").append(isInPictureInPictureMode());
        builder.append(", transitionTime=").append(this.mTransitionTime);
        boolean z = true;
        builder.append(", hasParent=").append(this.mParentId != -1);
        builder.append(", isAnchored=").append(this.mAnchorId != AccessibilityNodeInfo.UNDEFINED_NODE_ID);
        StringBuilder append = builder.append(", hasChildren=");
        LongArray longArray = this.mChildIds;
        if (longArray == null || longArray.size() <= 0) {
            z = false;
        }
        append.append(z);
        builder.append(']');
        return builder.toString();
    }

    private void clear() {
        this.mDisplayId = -1;
        this.mType = -1;
        this.mLayer = -1;
        this.mBooleanProperties = 0;
        this.mId = -1;
        this.mParentId = -1;
        this.mTaskId = -1;
        this.mRegionInScreen.setEmpty();
        this.mChildIds = null;
        this.mConnectionId = -1;
        this.mAnchorId = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
        this.mTitle = null;
        this.mTransitionTime = 0L;
    }

    private boolean getBooleanProperty(int property) {
        return (this.mBooleanProperties & property) != 0;
    }

    private void setBooleanProperty(int property, boolean value) {
        if (value) {
            this.mBooleanProperties |= property;
        } else {
            this.mBooleanProperties &= ~property;
        }
    }

    public static String typeToString(int type) {
        switch (type) {
            case 1:
                return "TYPE_APPLICATION";
            case 2:
                return "TYPE_INPUT_METHOD";
            case 3:
                return "TYPE_SYSTEM";
            case 4:
                return "TYPE_ACCESSIBILITY_OVERLAY";
            case 5:
                return "TYPE_SPLIT_SCREEN_DIVIDER";
            case 6:
                return "TYPE_MAGNIFICATION_OVERLAY";
            default:
                return "<UNKNOWN:" + type + ">";
        }
    }

    public int differenceFrom(AccessibilityWindowInfo other) {
        if (other.mId != this.mId) {
            throw new IllegalArgumentException("Not same window.");
        }
        if (other.mType != this.mType) {
            throw new IllegalArgumentException("Not same type.");
        }
        int changes = 0;
        if (!TextUtils.equals(this.mTitle, other.mTitle)) {
            changes = 0 | 4;
        }
        if (!this.mRegionInScreen.equals(other.mRegionInScreen)) {
            changes |= 8;
        }
        if (this.mLayer != other.mLayer) {
            changes |= 16;
        }
        if (getBooleanProperty(1) != other.getBooleanProperty(1)) {
            changes |= 32;
        }
        if (getBooleanProperty(2) != other.getBooleanProperty(2)) {
            changes |= 64;
        }
        if (getBooleanProperty(4) != other.getBooleanProperty(4)) {
            changes |= 128;
        }
        if (getBooleanProperty(8) != other.getBooleanProperty(8)) {
            changes |= 1024;
        }
        if (this.mParentId != other.mParentId) {
            changes |= 256;
        }
        if (!Objects.equals(this.mChildIds, other.mChildIds)) {
            return changes | 512;
        }
        return changes;
    }

    /* loaded from: classes4.dex */
    public static final class WindowListSparseArray extends SparseArray<List<AccessibilityWindowInfo>> implements Parcelable {
        public static final Parcelable.Creator<WindowListSparseArray> CREATOR = new Parcelable.Creator<WindowListSparseArray>() { // from class: android.view.accessibility.AccessibilityWindowInfo.WindowListSparseArray.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public WindowListSparseArray createFromParcel(Parcel source) {
                WindowListSparseArray array = new WindowListSparseArray();
                ClassLoader loader = array.getClass().getClassLoader();
                int count = source.readInt();
                for (int i = 0; i < count; i++) {
                    ArrayList arrayList = new ArrayList();
                    source.readParcelableList(arrayList, loader, AccessibilityWindowInfo.class);
                    array.put(source.readInt(), arrayList);
                }
                return array;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public WindowListSparseArray[] newArray(int size) {
                return new WindowListSparseArray[size];
            }
        };

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            int count = size();
            dest.writeInt(count);
            for (int i = 0; i < count; i++) {
                dest.writeParcelableList(valueAt(i), 0);
                dest.writeInt(keyAt(i));
            }
        }
    }
}
