package com.android.internal.org.bouncycastle.asn1;
/* loaded from: classes4.dex */
public class ASN1EncodableVector {
    private static final int DEFAULT_CAPACITY = 10;
    static final ASN1Encodable[] EMPTY_ELEMENTS = new ASN1Encodable[0];
    private boolean copyOnWrite;
    private int elementCount;
    private ASN1Encodable[] elements;

    public ASN1EncodableVector() {
        this(10);
    }

    public ASN1EncodableVector(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("'initialCapacity' must not be negative");
        }
        this.elements = initialCapacity == 0 ? EMPTY_ELEMENTS : new ASN1Encodable[initialCapacity];
        this.elementCount = 0;
        this.copyOnWrite = false;
    }

    public void add(ASN1Encodable element) {
        if (element == null) {
            throw new NullPointerException("'element' cannot be null");
        }
        int capacity = this.elements.length;
        int minCapacity = this.elementCount + 1;
        if ((minCapacity > capacity) | this.copyOnWrite) {
            reallocate(minCapacity);
        }
        this.elements[this.elementCount] = element;
        this.elementCount = minCapacity;
    }

    public void addAll(ASN1EncodableVector other) {
        if (other == null) {
            throw new NullPointerException("'other' cannot be null");
        }
        int otherElementCount = other.size();
        if (otherElementCount < 1) {
            return;
        }
        int capacity = this.elements.length;
        int minCapacity = this.elementCount + otherElementCount;
        if ((minCapacity > capacity) | this.copyOnWrite) {
            reallocate(minCapacity);
        }
        int i = 0;
        do {
            ASN1Encodable otherElement = other.get(i);
            if (otherElement == null) {
                throw new NullPointerException("'other' elements cannot be null");
            }
            this.elements[this.elementCount + i] = otherElement;
            i++;
        } while (i < otherElementCount);
        this.elementCount = minCapacity;
    }

    public ASN1Encodable get(int i) {
        if (i >= this.elementCount) {
            throw new ArrayIndexOutOfBoundsException(i + " >= " + this.elementCount);
        }
        return this.elements[i];
    }

    public int size() {
        return this.elementCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Encodable[] copyElements() {
        int i = this.elementCount;
        if (i == 0) {
            return EMPTY_ELEMENTS;
        }
        ASN1Encodable[] copy = new ASN1Encodable[i];
        System.arraycopy(this.elements, 0, copy, 0, i);
        return copy;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Encodable[] takeElements() {
        int i = this.elementCount;
        if (i == 0) {
            return EMPTY_ELEMENTS;
        }
        ASN1Encodable[] aSN1EncodableArr = this.elements;
        if (aSN1EncodableArr.length == i) {
            this.copyOnWrite = true;
            return aSN1EncodableArr;
        }
        ASN1Encodable[] copy = new ASN1Encodable[i];
        System.arraycopy(aSN1EncodableArr, 0, copy, 0, i);
        return copy;
    }

    private void reallocate(int minCapacity) {
        int oldCapacity = this.elements.length;
        int newCapacity = Math.max(oldCapacity, (minCapacity >> 1) + minCapacity);
        ASN1Encodable[] copy = new ASN1Encodable[newCapacity];
        System.arraycopy(this.elements, 0, copy, 0, this.elementCount);
        this.elements = copy;
        this.copyOnWrite = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ASN1Encodable[] cloneElements(ASN1Encodable[] elements) {
        return elements.length < 1 ? EMPTY_ELEMENTS : (ASN1Encodable[]) elements.clone();
    }
}
