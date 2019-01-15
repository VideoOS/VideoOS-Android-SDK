package cn.com.venvy.common.http.urlconnection.tool;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by mac on 18/3/26.
 */

public class HttpHeaderParser {
    static final String HEADER_CONTENT_TYPE = "Content-Type";

    private static final String DEFAULT_CONTENT_CHARSET = "ISO-8859-1";


    public static String parseCharset(Map<String, String> headers, String defaultCharset) {
        String contentType = headers.get(HEADER_CONTENT_TYPE);
        if (contentType != null) {
            String[] params = contentType.split(";");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }

        return defaultCharset;
    }

    /**
     * Returns the charset specified in the Content-Type of this header,
     * or the HTTP default (ISO-8859-1) if none can be found.
     */
    public static String parseCharset(Map<String, String> headers) {
        return parseCharset(headers, DEFAULT_CONTENT_CHARSET);
    }


    public static Map<String, String> convertHeaders(Map<String, List<String>> headers) {
        Map<String, String> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey() != null) {
                List<String> values = entry.getValue();
                result.put(entry.getKey(), values.get(0));
            }
        }
        return result;
    }
}
