package android.util.jar;

import com.android.net.module.util.NetworkStackConstants;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import libcore.io.Streams;
/* loaded from: classes3.dex */
public class StrictJarManifest implements Cloneable {
    static final int LINE_LENGTH_LIMIT = 72;
    private HashMap<String, Chunk> chunks;
    private final HashMap<String, Attributes> entries;
    private final Attributes mainAttributes;
    private int mainEnd;
    private static final byte[] LINE_SEPARATOR = {13, 10};
    private static final byte[] VALUE_SEPARATOR = {58, NetworkStackConstants.TCPHDR_URG};
    static final Attributes.Name ATTRIBUTE_NAME_NAME = new Attributes.Name("Name");

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static final class Chunk {
        final int end;
        final int start;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Chunk(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    public StrictJarManifest() {
        this.entries = new HashMap<>();
        this.mainAttributes = new Attributes();
    }

    public StrictJarManifest(InputStream is) throws IOException {
        this();
        read(Streams.readFully(is));
    }

    public StrictJarManifest(StrictJarManifest man) {
        this.mainAttributes = (Attributes) man.mainAttributes.clone();
        this.entries = (HashMap) ((HashMap) man.getEntries()).clone();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StrictJarManifest(byte[] manifestBytes, boolean readChunks) throws IOException {
        this();
        if (readChunks) {
            this.chunks = new HashMap<>();
        }
        read(manifestBytes);
    }

    public void clear() {
        this.entries.clear();
        this.mainAttributes.clear();
    }

    public Attributes getAttributes(String name) {
        return getEntries().get(name);
    }

    public Map<String, Attributes> getEntries() {
        return this.entries;
    }

    public Attributes getMainAttributes() {
        return this.mainAttributes;
    }

    public Object clone() {
        return new StrictJarManifest(this);
    }

    public void write(OutputStream os) throws IOException {
        write(this, os);
    }

    public void read(InputStream is) throws IOException {
        read(Streams.readFullyNoClose(is));
    }

    private void read(byte[] buf) throws IOException {
        if (buf.length == 0) {
            return;
        }
        StrictJarManifestReader im = new StrictJarManifestReader(buf, this.mainAttributes);
        this.mainEnd = im.getEndOfMainSection();
        im.readEntries(this.entries, this.chunks);
    }

    public int hashCode() {
        return this.mainAttributes.hashCode() ^ getEntries().hashCode();
    }

    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass() || !this.mainAttributes.equals(((StrictJarManifest) o).mainAttributes)) {
            return false;
        }
        return getEntries().equals(((StrictJarManifest) o).getEntries());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Chunk getChunk(String name) {
        return this.chunks.get(name);
    }

    void removeChunks() {
        this.chunks = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMainAttributesEnd() {
        return this.mainEnd;
    }

    static void write(StrictJarManifest manifest, OutputStream out) throws IOException {
        CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
        ByteBuffer buffer = ByteBuffer.allocate(72);
        Attributes.Name versionName = Attributes.Name.MANIFEST_VERSION;
        String version = manifest.mainAttributes.getValue(versionName);
        if (version == null) {
            versionName = Attributes.Name.SIGNATURE_VERSION;
            version = manifest.mainAttributes.getValue(versionName);
        }
        if (version != null) {
            writeEntry(out, versionName, version, encoder, buffer);
            Iterator<?> entries = manifest.mainAttributes.keySet().iterator();
            while (entries.hasNext()) {
                Attributes.Name name = (Attributes.Name) entries.next();
                if (!name.equals(versionName)) {
                    writeEntry(out, name, manifest.mainAttributes.getValue(name), encoder, buffer);
                }
            }
        }
        out.write(LINE_SEPARATOR);
        for (String key : manifest.getEntries().keySet()) {
            writeEntry(out, ATTRIBUTE_NAME_NAME, key, encoder, buffer);
            Attributes attributes = manifest.entries.get(key);
            Iterator<?> entries2 = attributes.keySet().iterator();
            while (entries2.hasNext()) {
                Attributes.Name name2 = (Attributes.Name) entries2.next();
                writeEntry(out, name2, attributes.getValue(name2), encoder, buffer);
            }
            out.write(LINE_SEPARATOR);
        }
    }

    private static void writeEntry(OutputStream os, Attributes.Name name, String value, CharsetEncoder encoder, ByteBuffer bBuf) throws IOException {
        String nameString = name.toString();
        os.write(nameString.getBytes(StandardCharsets.US_ASCII));
        os.write(VALUE_SEPARATOR);
        encoder.reset();
        bBuf.clear().limit((72 - nameString.length()) - 2);
        CharBuffer cBuf = CharBuffer.wrap(value);
        while (true) {
            CoderResult r = encoder.encode(cBuf, bBuf, true);
            if (CoderResult.UNDERFLOW == r) {
                r = encoder.flush(bBuf);
            }
            os.write(bBuf.array(), bBuf.arrayOffset(), bBuf.position());
            os.write(LINE_SEPARATOR);
            if (CoderResult.UNDERFLOW != r) {
                os.write(32);
                bBuf.clear().limit(71);
            } else {
                return;
            }
        }
    }
}
