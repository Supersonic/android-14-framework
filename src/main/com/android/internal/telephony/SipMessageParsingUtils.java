package com.android.internal.telephony;

import android.net.Uri;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes3.dex */
public class SipMessageParsingUtils {
    private static final String ACCEPT_CONTACT_HEADER_KEY = "accept-contact";
    private static final String ACCEPT_CONTACT_HEADER_KEY_COMPACT = "a";
    private static final String BRANCH_PARAM_KEY = "branch";
    private static final String CALL_ID_SIP_HEADER_KEY = "call-id";
    private static final String CALL_ID_SIP_HEADER_KEY_COMPACT = "i";
    private static final String FROM_HEADER_KEY = "from";
    private static final String FROM_HEADER_KEY_COMPACT = "f";
    private static final String HEADER_KEY_VALUE_SEPARATOR = ":";
    private static final String PARAM_KEY_VALUE_SEPARATOR = "=";
    private static final String PARAM_SEPARATOR = ";";
    private static final String[] SIP_REQUEST_METHODS = {"INVITE", "ACK", "OPTIONS", "BYE", "CANCEL", "REGISTER", "PRACK", "SUBSCRIBE", "NOTIFY", "PUBLISH", "INFO", "REFER", "MESSAGE", "UPDATE"};
    private static final String SIP_VERSION_2 = "SIP/2.0";
    private static final String SUBHEADER_VALUE_SEPARATOR = ",";
    private static final String TAG = "SipMessageParsingUtils";
    private static final String TAG_PARAM_KEY = "tag";
    private static final String TO_HEADER_KEY = "to";
    private static final String TO_HEADER_KEY_COMPACT = "t";
    private static final String VIA_SIP_HEADER_KEY = "via";
    private static final String VIA_SIP_HEADER_KEY_COMPACT = "v";

    public static boolean isSipRequest(String startLine) {
        String[] splitLine = splitStartLineAndVerify(startLine);
        if (splitLine == null) {
            return false;
        }
        return verifySipRequest(splitLine);
    }

    public static boolean isSipResponse(String startLine) {
        String[] splitLine = splitStartLineAndVerify(startLine);
        if (splitLine == null) {
            return false;
        }
        return verifySipResponse(splitLine);
    }

    public static String getTransactionId(String headerString) {
        List<Pair<String, String>> headers = parseHeaders(headerString, true, VIA_SIP_HEADER_KEY, "v");
        for (Pair<String, String> header : headers) {
            String[] subHeaders = header.second.split(",");
            for (String subHeader : subHeaders) {
                String paramValue = getParameterValue(subHeader, BRANCH_PARAM_KEY);
                if (paramValue != null) {
                    return paramValue;
                }
            }
        }
        return null;
    }

    private static String getParameterValue(String headerValue, String parameterKey) {
        String[] params = headerValue.split(";");
        if (params.length < 2) {
            return null;
        }
        for (String param : params) {
            String[] pair = param.split(PARAM_KEY_VALUE_SEPARATOR);
            if (pair.length >= 2) {
                if (pair.length > 2) {
                    Log.m104w(TAG, "getParameterValue: unexpected parameter" + Arrays.toString(pair));
                }
                pair[0] = pair[0].trim();
                pair[1] = pair[1].trim();
                if (parameterKey.equalsIgnoreCase(pair[0])) {
                    return pair[1];
                }
            }
        }
        return null;
    }

    public static String getCallId(String headerString) {
        List<Pair<String, String>> headers = parseHeaders(headerString, true, CALL_ID_SIP_HEADER_KEY, CALL_ID_SIP_HEADER_KEY_COMPACT);
        if (headers.isEmpty()) {
            return null;
        }
        return headers.get(0).second;
    }

    public static String getFromTag(String headerString) {
        List<Pair<String, String>> headers = parseHeaders(headerString, true, FROM_HEADER_KEY, "f");
        if (headers.isEmpty()) {
            return null;
        }
        return getParameterValue(headers.get(0).second, "tag");
    }

    public static String getToTag(String headerString) {
        List<Pair<String, String>> headers = parseHeaders(headerString, true, TO_HEADER_KEY, "t");
        if (headers.isEmpty()) {
            return null;
        }
        return getParameterValue(headers.get(0).second, "tag");
    }

    public static String[] splitStartLineAndVerify(String startLine) {
        String[] splitLine = startLine.split(" ", 3);
        if (isStartLineMalformed(splitLine)) {
            return null;
        }
        return splitLine;
    }

    public static Set<String> getAcceptContactFeatureTags(String headerString) {
        List<Pair<String, String>> headers = parseHeaders(headerString, false, ACCEPT_CONTACT_HEADER_KEY, "a");
        if (headerString.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> featureTags = new ArraySet<>();
        for (Pair<String, String> header : headers) {
            String[] splitParams = header.second.split(";");
            int i = 2;
            if (splitParams.length >= 2) {
                char c = 1;
                Set<String> fts = (Set) Arrays.asList(splitParams).subList(1, splitParams.length).stream().map(new Function() { // from class: com.android.internal.telephony.SipMessageParsingUtils$$ExternalSyntheticLambda1
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return ((String) obj).trim();
                    }
                }).filter(new Predicate() { // from class: com.android.internal.telephony.SipMessageParsingUtils$$ExternalSyntheticLambda2
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean startsWith;
                        startsWith = ((String) obj).startsWith("+");
                        return startsWith;
                    }
                }).collect(Collectors.toSet());
                for (String ft : fts) {
                    String[] paramKeyValue = ft.split(PARAM_KEY_VALUE_SEPARATOR, i);
                    if (paramKeyValue.length < i) {
                        featureTags.add(ft);
                    } else {
                        String[] splitValue = splitParamValue(paramKeyValue[c]);
                        int length = splitValue.length;
                        int i2 = 0;
                        while (i2 < length) {
                            String value = splitValue[i2];
                            featureTags.add(paramKeyValue[0] + PARAM_KEY_VALUE_SEPARATOR + value);
                            i2++;
                            headers = headers;
                        }
                        i = 2;
                        c = 1;
                    }
                }
            }
        }
        return featureTags;
    }

    private static String[] splitParamValue(String paramValue) {
        if (!paramValue.startsWith("\"") && !paramValue.endsWith("\"")) {
            return new String[]{paramValue};
        }
        String[] splitValues = paramValue.substring(1, paramValue.length() - 1).split(",");
        for (int i = 0; i < splitValues.length; i++) {
            splitValues[i] = "\"" + splitValues[i] + "\"";
        }
        return splitValues;
    }

    private static boolean isStartLineMalformed(String[] startLine) {
        return startLine == null || startLine.length == 0 || startLine.length != 3;
    }

    private static boolean verifySipRequest(final String[] request) {
        if (request[2].contains(SIP_VERSION_2)) {
            try {
                boolean verified = Uri.parse(request[1]).getScheme() != null;
                return verified & Arrays.stream(SIP_REQUEST_METHODS).anyMatch(new Predicate() { // from class: com.android.internal.telephony.SipMessageParsingUtils$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean contains;
                        contains = request[0].contains((String) obj);
                        return contains;
                    }
                });
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    private static boolean verifySipResponse(String[] response) {
        if (!response[0].contains(SIP_VERSION_2)) {
            return false;
        }
        try {
            int statusCode = Integer.parseInt(response[1]);
            if (statusCode < 100 || statusCode >= 700) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static List<Pair<String, String>> parseHeaders(String headerString, boolean stopAtFirstMatch, String... matchingHeaderKeys) {
        String headerString2 = removeLeadingWhitespace(headerString);
        List<Pair<String, String>> result = new ArrayList<>();
        String[] headerLines = headerString2.split("\\r?\\n");
        if (headerLines.length == 0) {
            return Collections.emptyList();
        }
        String headerKey = null;
        StringBuilder headerValueSegment = new StringBuilder();
        for (String line : headerLines) {
            if (line.startsWith("\t") || line.startsWith(" ")) {
                headerValueSegment.append(removeLeadingWhitespace(line));
            } else {
                if (headerKey != null) {
                    final String key = headerKey;
                    if (matchingHeaderKeys == null || matchingHeaderKeys.length == 0 || Arrays.stream(matchingHeaderKeys).anyMatch(new Predicate() { // from class: com.android.internal.telephony.SipMessageParsingUtils$$ExternalSyntheticLambda3
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            boolean equalsIgnoreCase;
                            equalsIgnoreCase = ((String) obj).equalsIgnoreCase(key);
                            return equalsIgnoreCase;
                        }
                    })) {
                        result.add(new Pair<>(key, headerValueSegment.toString()));
                        if (stopAtFirstMatch) {
                            return result;
                        }
                    }
                    headerKey = null;
                    headerValueSegment = new StringBuilder();
                }
                String[] pair = line.split(":", 2);
                if (pair.length < 2) {
                    Log.m104w(TAG, "parseHeaders - received malformed line: " + line);
                } else {
                    headerKey = pair[0].trim();
                    for (int i = 1; i < pair.length; i++) {
                        headerValueSegment.append(removeLeadingWhitespace(pair[i]));
                    }
                }
            }
        }
        if (headerKey != null) {
            final String key2 = headerKey;
            if (matchingHeaderKeys == null || matchingHeaderKeys.length == 0 || Arrays.stream(matchingHeaderKeys).anyMatch(new Predicate() { // from class: com.android.internal.telephony.SipMessageParsingUtils$$ExternalSyntheticLambda4
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean equalsIgnoreCase;
                    equalsIgnoreCase = ((String) obj).equalsIgnoreCase(key2);
                    return equalsIgnoreCase;
                }
            })) {
                result.add(new Pair<>(key2, headerValueSegment.toString()));
            }
        }
        return result;
    }

    private static String removeLeadingWhitespace(String line) {
        return line.replaceFirst("^\\s*", "");
    }
}
