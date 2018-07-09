package ntlmproxy;

import org.apache.commons.httpclient.HttpParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpHeaderParserTest {
    private final static String CRLF = "\r\n";

    @Test
    void testSimpleParse() throws IOException, ParseException {
        String sampleRequest =
            "POST / HTTP/1.1" + CRLF +
            "Host: oof.foo.io" + CRLF +
            "User-Agent: curl/7.60.0" + CRLF +
            "Accept: */*" + CRLF +
            "Content-Length: 13" + CRLF +
            "Content-Type: application/x-www-form-urlencoded";

        HttpHeaderParser headerParser = new HttpHeaderParser(sampleRequest);

        assertEquals( "POST", headerParser.getMethod() );
        assertEquals( "/", headerParser.getUri() );
        assertEquals( "HTTP/1.1", headerParser.getProtocol() );

        assertEquals( "application/x-www-form-urlencoded", headerParser.getContentType() );
        assertEquals( 13, headerParser.getContentLength().intValue() );

        assertEquals( 5, headerParser.getHeaders().length );
    }
}
