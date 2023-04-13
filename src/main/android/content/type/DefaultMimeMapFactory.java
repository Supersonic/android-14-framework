package android.content.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import libcore.content.type.MimeMap;
/* loaded from: classes.dex */
public class DefaultMimeMapFactory {
    private DefaultMimeMapFactory() {
    }

    public static MimeMap create() {
        return create(new Function() { // from class: android.content.type.DefaultMimeMapFactory$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                InputStream resourceAsStream;
                resourceAsStream = r1.getResourceAsStream("/res/" + ((String) obj));
                return resourceAsStream;
            }
        });
    }

    public static MimeMap create(Function<String, InputStream> resourceSupplier) {
        MimeMap.Builder builder = MimeMap.builder();
        parseTypes(builder, resourceSupplier, "debian.mime.types");
        parseTypes(builder, resourceSupplier, "android.mime.types");
        parseTypes(builder, resourceSupplier, "vendor.mime.types");
        return builder.build();
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0077, code lost:
        throw new java.lang.IllegalArgumentException("Malformed line: " + r3);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static void parseTypes(MimeMap.Builder builder, Function<String, InputStream> resourceSupplier, String resourceName) {
        try {
            InputStream inputStream = (InputStream) Objects.requireNonNull(resourceSupplier.apply(resourceName));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                List<String> specs = new ArrayList<>(10);
                loop0: while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        specs.clear();
                        int startIdx = 0;
                        do {
                            int endIdx = line.indexOf(32, startIdx);
                            if (endIdx < 0) {
                                endIdx = line.length();
                            }
                            String spec = line.substring(startIdx, endIdx);
                            if (spec.isEmpty()) {
                                break loop0;
                            }
                            specs.add(spec);
                            startIdx = endIdx + 1;
                        } while (startIdx < line.length());
                        builder.addMimeMapping(specs.get(0), specs.subList(1, specs.size()));
                    } else {
                        reader.close();
                        if (inputStream != null) {
                            inputStream.close();
                            return;
                        }
                        return;
                    }
                }
            } catch (Throwable th) {
                try {
                    reader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Failed to parse " + resourceName, e);
        }
    }
}
