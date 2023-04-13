package android.filterfw.p003io;

import android.content.Context;
import android.filterfw.core.FilterGraph;
import android.filterfw.core.KeyValueMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
/* renamed from: android.filterfw.io.GraphReader */
/* loaded from: classes.dex */
public abstract class GraphReader {
    protected KeyValueMap mReferences = new KeyValueMap();

    public abstract FilterGraph readGraphString(String str) throws GraphIOException;

    public abstract KeyValueMap readKeyValueAssignments(String str) throws GraphIOException;

    public FilterGraph readGraphResource(Context context, int resourceId) throws GraphIOException {
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        InputStreamReader reader = new InputStreamReader(inputStream);
        StringWriter writer = new StringWriter();
        char[] buffer = new char[1024];
        while (true) {
            try {
                int bytesRead = reader.read(buffer, 0, 1024);
                if (bytesRead > 0) {
                    writer.write(buffer, 0, bytesRead);
                } else {
                    return readGraphString(writer.toString());
                }
            } catch (IOException e) {
                throw new RuntimeException("Could not read specified resource file!");
            }
        }
    }

    public void addReference(String name, Object object) {
        this.mReferences.put(name, object);
    }

    public void addReferencesByMap(KeyValueMap refs) {
        this.mReferences.putAll(refs);
    }

    public void addReferencesByKeysAndValues(Object... references) {
        this.mReferences.setKeyValues(references);
    }
}
