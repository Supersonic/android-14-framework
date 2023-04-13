package com.android.server.integrity.serializer;

import android.content.integrity.Rule;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
/* loaded from: classes.dex */
public interface RuleSerializer {
    void serialize(List<Rule> list, Optional<Integer> optional, OutputStream outputStream, OutputStream outputStream2) throws RuleSerializeException;
}
