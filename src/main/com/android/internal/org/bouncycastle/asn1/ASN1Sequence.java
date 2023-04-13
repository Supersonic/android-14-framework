package com.android.internal.org.bouncycastle.asn1;

import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Iterable;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
/* loaded from: classes4.dex */
public abstract class ASN1Sequence extends ASN1Primitive implements Iterable<ASN1Encodable> {
    ASN1Encodable[] elements;

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public abstract void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException;

    public static ASN1Sequence getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1Sequence)) {
            return (ASN1Sequence) obj;
        }
        if (obj instanceof ASN1SequenceParser) {
            return getInstance(((ASN1SequenceParser) obj).toASN1Primitive());
        }
        if (obj instanceof byte[]) {
            try {
                return getInstance(fromByteArray((byte[]) obj));
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to construct sequence from byte[]: " + e.getMessage());
            }
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive primitive = ((ASN1Encodable) obj).toASN1Primitive();
            if (primitive instanceof ASN1Sequence) {
                return (ASN1Sequence) primitive;
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1Sequence getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        if (explicit) {
            if (!taggedObject.isExplicit()) {
                throw new IllegalArgumentException("object implicit - explicit expected.");
            }
            return getInstance(taggedObject.getObject());
        }
        ASN1Primitive o = taggedObject.getObject();
        if (taggedObject.isExplicit()) {
            if (taggedObject instanceof BERTaggedObject) {
                return new BERSequence(o);
            }
            return new DLSequence(o);
        } else if (o instanceof ASN1Sequence) {
            ASN1Sequence s = (ASN1Sequence) o;
            if (taggedObject instanceof BERTaggedObject) {
                return s;
            }
            return (ASN1Sequence) s.toDLObject();
        } else {
            throw new IllegalArgumentException("unknown object in getInstance: " + taggedObject.getClass().getName());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1Sequence() {
        this.elements = ASN1EncodableVector.EMPTY_ELEMENTS;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1Sequence(ASN1Encodable element) {
        if (element == null) {
            throw new NullPointerException("'element' cannot be null");
        }
        this.elements = new ASN1Encodable[]{element};
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1Sequence(ASN1EncodableVector elementVector) {
        if (elementVector == null) {
            throw new NullPointerException("'elementVector' cannot be null");
        }
        this.elements = elementVector.takeElements();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1Sequence(ASN1Encodable[] elements) {
        if (Arrays.isNullOrContainsNull(elements)) {
            throw new NullPointerException("'elements' cannot be null, or contain null");
        }
        this.elements = ASN1EncodableVector.cloneElements(elements);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Sequence(ASN1Encodable[] elements, boolean clone) {
        this.elements = clone ? ASN1EncodableVector.cloneElements(elements) : elements;
    }

    public ASN1Encodable[] toArray() {
        return ASN1EncodableVector.cloneElements(this.elements);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Encodable[] toArrayInternal() {
        return this.elements;
    }

    public Enumeration getObjects() {
        return new Enumeration() { // from class: com.android.internal.org.bouncycastle.asn1.ASN1Sequence.1
            private int pos = 0;

            @Override // java.util.Enumeration
            public boolean hasMoreElements() {
                return this.pos < ASN1Sequence.this.elements.length;
            }

            @Override // java.util.Enumeration
            public Object nextElement() {
                if (this.pos < ASN1Sequence.this.elements.length) {
                    ASN1Encodable[] aSN1EncodableArr = ASN1Sequence.this.elements;
                    int i = this.pos;
                    this.pos = i + 1;
                    return aSN1EncodableArr[i];
                }
                throw new NoSuchElementException();
            }
        };
    }

    public ASN1SequenceParser parser() {
        final int count = size();
        return new ASN1SequenceParser() { // from class: com.android.internal.org.bouncycastle.asn1.ASN1Sequence.2
            private int pos = 0;

            @Override // com.android.internal.org.bouncycastle.asn1.ASN1SequenceParser
            public ASN1Encodable readObject() throws IOException {
                if (count == this.pos) {
                    return null;
                }
                ASN1Encodable[] aSN1EncodableArr = ASN1Sequence.this.elements;
                int i = this.pos;
                this.pos = i + 1;
                ASN1Encodable obj = aSN1EncodableArr[i];
                if (obj instanceof ASN1Sequence) {
                    return ((ASN1Sequence) obj).parser();
                }
                if (obj instanceof ASN1Set) {
                    return ((ASN1Set) obj).parser();
                }
                return obj;
            }

            @Override // com.android.internal.org.bouncycastle.asn1.InMemoryRepresentable
            public ASN1Primitive getLoadedObject() {
                return ASN1Sequence.this;
            }

            @Override // com.android.internal.org.bouncycastle.asn1.ASN1Encodable
            public ASN1Primitive toASN1Primitive() {
                return ASN1Sequence.this;
            }
        };
    }

    public ASN1Encodable getObjectAt(int index) {
        return this.elements[index];
    }

    public int size() {
        return this.elements.length;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive, com.android.internal.org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        int i = this.elements.length;
        int hc = i + 1;
        while (true) {
            i--;
            if (i >= 0) {
                hc = (hc * 257) ^ this.elements[i].toASN1Primitive().hashCode();
            } else {
                return hc;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean asn1Equals(ASN1Primitive other) {
        if (other instanceof ASN1Sequence) {
            ASN1Sequence that = (ASN1Sequence) other;
            int count = size();
            if (that.size() != count) {
                return false;
            }
            for (int i = 0; i < count; i++) {
                ASN1Primitive p1 = this.elements[i].toASN1Primitive();
                ASN1Primitive p2 = that.elements[i].toASN1Primitive();
                if (p1 != p2 && !p1.asn1Equals(p2)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public ASN1Primitive toDERObject() {
        return new DERSequence(this.elements, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public ASN1Primitive toDLObject() {
        return new DLSequence(this.elements, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return true;
    }

    public String toString() {
        int count = size();
        if (count == 0) {
            return "[]";
        }
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        int i = 0;
        while (true) {
            sb.append(this.elements[i]);
            i++;
            if (i < count) {
                sb.append(", ");
            } else {
                sb.append(']');
                return sb.toString();
            }
        }
    }

    @Override // com.android.internal.org.bouncycastle.util.Iterable, java.lang.Iterable
    public Iterator<ASN1Encodable> iterator() {
        return new Arrays.Iterator(this.elements);
    }
}
