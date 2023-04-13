package com.android.server.integrity.parser;

import android.content.integrity.Rule;
import java.util.List;
/* loaded from: classes.dex */
public interface RuleParser {
    List<Rule> parse(RandomAccessObject randomAccessObject, List<RuleIndexRange> list) throws RuleParseException;
}
