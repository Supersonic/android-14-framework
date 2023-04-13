package android.view;

import android.graphics.drawable.Icon;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
/* loaded from: classes4.dex */
public final class KeyboardShortcutInfo implements Parcelable {
    public static final Parcelable.Creator<KeyboardShortcutInfo> CREATOR = new Parcelable.Creator<KeyboardShortcutInfo>() { // from class: android.view.KeyboardShortcutInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeyboardShortcutInfo createFromParcel(Parcel source) {
            return new KeyboardShortcutInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeyboardShortcutInfo[] newArray(int size) {
            return new KeyboardShortcutInfo[size];
        }
    };
    private final char mBaseCharacter;
    private final Icon mIcon;
    private final int mKeycode;
    private final CharSequence mLabel;
    private final int mModifiers;

    public KeyboardShortcutInfo(CharSequence label, Icon icon, int keycode, int modifiers) {
        this.mLabel = label;
        this.mIcon = icon;
        boolean z = false;
        this.mBaseCharacter = (char) 0;
        if (keycode >= 0 && keycode <= KeyEvent.getMaxKeyCode()) {
            z = true;
        }
        Preconditions.checkArgument(z);
        this.mKeycode = keycode;
        this.mModifiers = modifiers;
    }

    public KeyboardShortcutInfo(CharSequence label, int keycode, int modifiers) {
        this(label, null, keycode, modifiers);
    }

    public KeyboardShortcutInfo(CharSequence label, char baseCharacter, int modifiers) {
        this.mLabel = label;
        Preconditions.checkArgument(baseCharacter != 0);
        this.mBaseCharacter = baseCharacter;
        this.mKeycode = 0;
        this.mModifiers = modifiers;
        this.mIcon = null;
    }

    private KeyboardShortcutInfo(Parcel source) {
        this.mLabel = source.readCharSequence();
        this.mIcon = (Icon) source.readParcelable(null, Icon.class);
        this.mBaseCharacter = (char) source.readInt();
        this.mKeycode = source.readInt();
        this.mModifiers = source.readInt();
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public Icon getIcon() {
        return this.mIcon;
    }

    public int getKeycode() {
        return this.mKeycode;
    }

    public char getBaseCharacter() {
        return this.mBaseCharacter;
    }

    public int getModifiers() {
        return this.mModifiers;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeCharSequence(this.mLabel);
        dest.writeParcelable(this.mIcon, 0);
        dest.writeInt(this.mBaseCharacter);
        dest.writeInt(this.mKeycode);
        dest.writeInt(this.mModifiers);
    }
}
