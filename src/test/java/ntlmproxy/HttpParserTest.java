package ntlmproxy;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpParserTest {
    private final static String CRLF = "\r\n";

    @Test
    void testSimpleParse() throws IOException, ParseException {
        String sampleRequest =
            "GET http://www.abc.net.au/ HTTP/1.1" + CRLF +
            "Host: www.abc.net.au" + CRLF +
            "User-Agent: curl/7.60.0" + CRLF +
            "Accept: */*" + CRLF +
            "Proxy-Connection: Keep-Alive" + CRLF +
            CRLF;

        HttpParser parser = new HttpParser( new ByteArrayInputStream( sampleRequest.getBytes() ) );

        assertTrue( parser.parse() );
        assertEquals( "GET", parser.getMethod() );
        assertEquals( 4, parser.getHeaders().length );
    }
}
