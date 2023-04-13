package android.media;
/* loaded from: classes2.dex */
class AudioHandle {
    private final int mId;

    AudioHandle(int id) {
        this.mId = id;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: id */
    public int m152id() {
        return this.mId;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof AudioHandle)) {
            return false;
        }
        AudioHandle ah = (AudioHandle) o;
        return this.mId == ah.m152id();
    }

    public int hashCode() {
        return this.mId;
    }

    public String toString() {
        return Integer.toString(this.mId);
    }
}
