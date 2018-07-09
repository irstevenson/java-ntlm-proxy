package ntlmproxy;

import org.apache.commons.httpclient.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class HttpHeaderParser {
    private static Logger log = LoggerFactory.getLogger(HttpHeaderParser.class);

    private String method;
    private String uri;
    private String protocol;
    private String contentType;
    private Integer contentLength;
    private Header[] headers;

    HttpHeaderParser(String httpRequest) throws IOException {
        log.debug("Parsing:\n---START---\n" + httpRequest + "\n---END--");
        parse(httpRequest);
        log.debug("Parsing complete");
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getContentType() {
        return contentType;
    }

    public Integer getContentLength() {
        return contentLength;
    }

    public Header[] getHeaders() {
        return headers;
    }

    private void parse(String httpRequest) throws IOException {
        String[] lines = httpRequest.split("\r\n");
        if (lines.length == 0) {
            throw new IOException("Bad HTTP header - empty");
        }

        String[] startLineParts = lines[0].split(" ");
        if (startLineParts.length != 3) {
            throw new IOException("Bad HTTP header - invalid start-line with only: " + startLineParts.length + " parts.");
        }

        method = startLineParts[0];
        uri = startLineParts[1];
        protocol = startLineParts[2];
        log.debug("Start-line parsed to: method [" + method + "] uri [" + uri + "] protocol [" + protocol + "]");

        ArrayList<Header> parsedHeaders = new ArrayList<>(lines.length - 1);
        for (int i = 1; i < lines.length; i++) {
            String[] headerParts = lines[i].split(": ", 2);
            if (headerParts.length != 2) {
                throw new IOException("Bad Header:" + lines[i]);
            }

            Header h = new Header(headerParts[0], headerParts[1]);
            parsedHeaders.add(h);

            if (h.getName().equals("Content-Type")) {
                this.contentType = h.getValue();
            } else if (h.getName().equals("Content-Length")) {
                this.contentLength = Integer.valueOf(h.getValue());
            }

            log.debug("Parsed: " + h.toExternalForm().trim());
        }
        headers = parsedHeaders.toArray(new Header[0]);
    }
}
