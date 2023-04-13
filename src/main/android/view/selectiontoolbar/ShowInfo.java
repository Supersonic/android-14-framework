package android.view.selectiontoolbar;

import android.annotation.NonNull;
import android.graphics.Rect;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class ShowInfo implements Parcelable {
    public static final Parcelable.Creator<ShowInfo> CREATOR = new Parcelable.Creator<ShowInfo>() { // from class: android.view.selectiontoolbar.ShowInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ShowInfo[] newArray(int size) {
            return new ShowInfo[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ShowInfo createFromParcel(Parcel in) {
            return new ShowInfo(in);
        }
    };
    private final Rect mContentRect;
    private final IBinder mHostInputToken;
    private final boolean mIsLightTheme;
    private final boolean mLayoutRequired;
    private final List<ToolbarMenuItem> mMenuItems;
    private final int mSuggestedWidth;
    private final Rect mViewPortOnScreen;
    private final long mWidgetToken;

    public ShowInfo(long widgetToken, boolean layoutRequired, List<ToolbarMenuItem> menuItems, Rect contentRect, int suggestedWidth, Rect viewPortOnScreen, IBinder hostInputToken, boolean isLightTheme) {
        this.mWidgetToken = widgetToken;
        this.mLayoutRequired = layoutRequired;
        this.mMenuItems = menuItems;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) menuItems);
        this.mContentRect = contentRect;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) contentRect);
        this.mSuggestedWidth = suggestedWidth;
        this.mViewPortOnScreen = viewPortOnScreen;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) viewPortOnScreen);
        this.mHostInputToken = hostInputToken;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hostInputToken);
        this.mIsLightTheme = isLightTheme;
    }

    public long getWidgetToken() {
        return this.mWidgetToken;
    }

    public boolean isLayoutRequired() {
        return this.mLayoutRequired;
    }

    public List<ToolbarMenuItem> getMenuItems() {
        return this.mMenuItems;
    }

    public Rect getContentRect() {
        return this.mContentRect;
    }

    public int getSuggestedWidth() {
        return this.mSuggestedWidth;
    }

    public Rect getViewPortOnScreen() {
        return this.mViewPortOnScreen;
    }

    public IBinder getHostInputToken() {
        return this.mHostInputToken;
    }

    public boolean isIsLightTheme() {
        return this.mIsLightTheme;
    }

    public String toString() {
        return "ShowInfo { widgetToken = " + this.mWidgetToken + ", layoutRequired = " + this.mLayoutRequired + ", menuItems = " + this.mMenuItems + ", contentRect = " + this.mContentRect + ", suggestedWidth = " + this.mSuggestedWidth + ", viewPortOnScreen = " + this.mViewPortOnScreen + ", hostInputToken = " + this.mHostInputToken + ", isLightTheme = " + this.mIsLightTheme + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShowInfo that = (ShowInfo) o;
        if (this.mWidgetToken == that.mWidgetToken && this.mLayoutRequired == that.mLayoutRequired && Objects.equals(this.mMenuItems, that.mMenuItems) && Objects.equals(this.mContentRect, that.mContentRect) && this.mSuggestedWidth == that.mSuggestedWidth && Objects.equals(this.mViewPortOnScreen, that.mViewPortOnScreen) && Objects.equals(this.mHostInputToken, that.mHostInputToken) && this.mIsLightTheme == that.mIsLightTheme) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Long.hashCode(this.mWidgetToken);
        return (((((((((((((_hash * 31) + Boolean.hashCode(this.mLayoutRequired)) * 31) + Objects.hashCode(this.mMenuItems)) * 31) + Objects.hashCode(this.mContentRect)) * 31) + this.mSuggestedWidth) * 31) + Objects.hashCode(this.mViewPortOnScreen)) * 31) + Objects.hashCode(this.mHostInputToken)) * 31) + Boolean.hashCode(this.mIsLightTheme);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int flg = this.mLayoutRequired ? 0 | 2 : 0;
        if (this.mIsLightTheme) {
            flg |= 128;
        }
        dest.writeInt(flg);
        dest.writeLong(this.mWidgetToken);
        dest.writeParcelableList(this.mMenuItems, flags);
        dest.writeTypedObject(this.mContentRect, flags);
        dest.writeInt(this.mSuggestedWidth);
        dest.writeTypedObject(this.mViewPortOnScreen, flags);
        dest.writeStrongBinder(this.mHostInputToken);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    ShowInfo(Parcel in) {
        int flg = in.readInt();
        boolean layoutRequired = (flg & 2) != 0;
        boolean isLightTheme = (flg & 128) != 0;
        long widgetToken = in.readLong();
        ArrayList arrayList = new ArrayList();
        in.readParcelableList(arrayList, ToolbarMenuItem.class.getClassLoader(), ToolbarMenuItem.class);
        Rect contentRect = (Rect) in.readTypedObject(Rect.CREATOR);
        int suggestedWidth = in.readInt();
        Rect viewPortOnScreen = (Rect) in.readTypedObject(Rect.CREATOR);
        IBinder hostInputToken = in.readStrongBinder();
        this.mWidgetToken = widgetToken;
        this.mLayoutRequired = layoutRequired;
        this.mMenuItems = arrayList;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) arrayList);
        this.mContentRect = contentRect;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) contentRect);
        this.mSuggestedWidth = suggestedWidth;
        this.mViewPortOnScreen = viewPortOnScreen;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) viewPortOnScreen);
        this.mHostInputToken = hostInputToken;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hostInputToken);
        this.mIsLightTheme = isLightTheme;
    }

    @Deprecated
    private void __metadata() {
    }
}
