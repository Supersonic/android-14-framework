package android.util.jar;

import android.util.jar.StrictJarManifest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class StrictJarManifestReader {
    private final byte[] buf;
    private final int endOfMainSection;
    private Attributes.Name name;
    private int pos;
    private String value;
    private final HashMap<String, Attributes.Name> attributeNameCache = new HashMap<>();
    private final ByteArrayOutputStream valueBuffer = new ByteArrayOutputStream(80);
    private int consecutiveLineBreaks = 0;

    public StrictJarManifestReader(byte[] buf, Attributes main) throws IOException {
        this.buf = buf;
        while (readHeader()) {
            main.put(this.name, this.value);
        }
        this.endOfMainSection = this.pos;
    }

    public void readEntries(Map<String, Attributes> entries, Map<String, StrictJarManifest.Chunk> chunks) throws IOException {
        int mark = this.pos;
        while (readHeader()) {
            if (!StrictJarManifest.ATTRIBUTE_NAME_NAME.equals(this.name)) {
                throw new IOException("Entry is not named");
            }
            String entryNameValue = this.value;
            Attributes entry = entries.get(entryNameValue);
            if (entry == null) {
                entry = new Attributes(12);
            }
            while (readHeader()) {
                entry.put(this.name, this.value);
            }
            if (chunks != null) {
                if (chunks.get(entryNameValue) != null) {
                    throw new IOException("A jar verifier does not support more than one entry with the same name");
                }
                chunks.put(entryNameValue, new StrictJarManifest.Chunk(mark, this.pos));
                mark = this.pos;
            }
            entries.put(entryNameValue, entry);
        }
    }

    public int getEndOfMainSection() {
        return this.endOfMainSection;
    }

    private boolean readHeader() throws IOException {
        if (this.consecutiveLineBreaks > 1) {
            this.consecutiveLineBreaks = 0;
            return false;
        }
        readName();
        this.consecutiveLineBreaks = 0;
        readValue();
        return this.consecutiveLineBreaks > 0;
    }

    private void readName() throws IOException {
        int i;
        byte[] bArr;
        int i2;
        int mark = this.pos;
        do {
            i = this.pos;
            bArr = this.buf;
            if (i < bArr.length) {
                i2 = i + 1;
                this.pos = i2;
            } else {
                return;
            }
        } while (bArr[i] != 58);
        String nameString = new String(bArr, mark, (i2 - mark) - 1, StandardCharsets.US_ASCII);
        byte[] bArr2 = this.buf;
        int i3 = this.pos;
        this.pos = i3 + 1;
        if (bArr2[i3] != 32) {
            throw new IOException(String.format("Invalid value for attribute '%s'", nameString));
        }
        try {
            Attributes.Name name = this.attributeNameCache.get(nameString);
            this.name = name;
            if (name == null) {
                Attributes.Name name2 = new Attributes.Name(nameString);
                this.name = name2;
                this.attributeNameCache.put(nameString, name2);
            }
        } catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage());
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x0051, code lost:
        r8.valueBuffer.write(r4, r1, r2 - r1);
        r8.value = r8.valueBuffer.toString(java.nio.charset.StandardCharsets.UTF_8.name());
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0066, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void readValue() throws IOException {
        boolean lastCr = false;
        int mark = this.pos;
        int last = this.pos;
        this.valueBuffer.reset();
        while (true) {
            int i = this.pos;
            byte[] bArr = this.buf;
            if (i < bArr.length) {
                int i2 = i + 1;
                this.pos = i2;
                byte next = bArr[i];
                switch (next) {
                    case 0:
                        throw new IOException("NUL character in a manifest");
                    case 10:
                        if (!lastCr) {
                            this.consecutiveLineBreaks++;
                            continue;
                        } else {
                            lastCr = false;
                        }
                    case 13:
                        lastCr = true;
                        this.consecutiveLineBreaks++;
                        continue;
                    case 32:
                        if (this.consecutiveLineBreaks != 1) {
                            break;
                        } else {
                            this.valueBuffer.write(bArr, mark, last - mark);
                            mark = this.pos;
                            this.consecutiveLineBreaks = 0;
                            continue;
                        }
                }
                if (this.consecutiveLineBreaks >= 1) {
                    this.pos = i2 - 1;
                } else {
                    last = this.pos;
                }
            }
        }
    }
}
