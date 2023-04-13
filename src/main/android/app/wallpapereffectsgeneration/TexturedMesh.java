package android.app.wallpapereffectsgeneration;

import android.annotation.SystemApi;
import android.graphics.Bitmap;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes.dex */
public final class TexturedMesh implements Parcelable {
    public static final Parcelable.Creator<TexturedMesh> CREATOR = new Parcelable.Creator<TexturedMesh>() { // from class: android.app.wallpapereffectsgeneration.TexturedMesh.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TexturedMesh createFromParcel(Parcel in) {
            return new TexturedMesh(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TexturedMesh[] newArray(int size) {
            return new TexturedMesh[size];
        }
    };
    public static final int INDICES_LAYOUT_TRIANGLES = 1;
    public static final int INDICES_LAYOUT_UNDEFINED = 0;
    public static final int VERTICES_LAYOUT_POSITION3_UV2 = 1;
    public static final int VERTICES_LAYOUT_UNDEFINED = 0;
    private Bitmap mBitmap;
    private int[] mIndices;
    private int mIndicesLayoutType;
    private float[] mVertices;
    private int mVerticesLayoutType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface IndicesLayoutType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface VerticesLayoutType {
    }

    private TexturedMesh(Parcel in) {
        this.mIndicesLayoutType = in.readInt();
        this.mVerticesLayoutType = in.readInt();
        this.mBitmap = (Bitmap) in.readTypedObject(Bitmap.CREATOR);
        Parcel data = Parcel.obtain();
        try {
            byte[] bytes = in.readBlob();
            data.unmarshall(bytes, 0, bytes.length);
            data.setDataPosition(0);
            this.mIndices = data.createIntArray();
            this.mVertices = data.createFloatArray();
        } finally {
            data.recycle();
        }
    }

    private TexturedMesh(Bitmap bitmap, int[] indices, float[] vertices, int indicesLayoutType, int verticesLayoutType) {
        this.mBitmap = bitmap;
        this.mIndices = indices;
        this.mVertices = vertices;
        this.mIndicesLayoutType = indicesLayoutType;
        this.mVerticesLayoutType = verticesLayoutType;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public int[] getIndices() {
        return this.mIndices;
    }

    public float[] getVertices() {
        return this.mVertices;
    }

    public int getIndicesLayoutType() {
        return this.mIndicesLayoutType;
    }

    public int getVerticesLayoutType() {
        return this.mVerticesLayoutType;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mIndicesLayoutType);
        out.writeInt(this.mVerticesLayoutType);
        out.writeTypedObject(this.mBitmap, flags);
        Parcel data = Parcel.obtain();
        try {
            data.writeIntArray(this.mIndices);
            data.writeFloatArray(this.mVertices);
            out.writeBlob(data.marshall());
        } finally {
            data.recycle();
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private Bitmap mBitmap;
        private int[] mIndices;
        private int mIndicesLayoutType;
        private float[] mVertices;
        private int mVerticesLayouttype;

        @SystemApi
        public Builder(Bitmap bitmap) {
            this.mBitmap = bitmap;
        }

        public Builder setIndices(int[] indices) {
            this.mIndices = indices;
            return this;
        }

        public Builder setVertices(float[] vertices) {
            this.mVertices = vertices;
            return this;
        }

        public Builder setIndicesLayoutType(int indicesLayoutType) {
            this.mIndicesLayoutType = indicesLayoutType;
            return this;
        }

        public Builder setVerticesLayoutType(int verticesLayoutype) {
            this.mVerticesLayouttype = verticesLayoutype;
            return this;
        }

        public TexturedMesh build() {
            return new TexturedMesh(this.mBitmap, this.mIndices, this.mVertices, this.mIndicesLayoutType, this.mVerticesLayouttype);
        }
    }
}
