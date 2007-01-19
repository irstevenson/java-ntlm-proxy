package ntlmproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser extends InputStream {

	public int read() throws IOException {
		if (bodyIndex < index)
			return buffer[bodyIndex++];
		return is.read();
	}

	public int read(byte[] b, int off, int len) throws IOException {
		if (bodyIndex < index) {
			int toCopy = Math.min(len, index - bodyIndex);
			System.arraycopy(buffer, bodyIndex, b, off, toCopy);
			bodyIndex += toCopy;
			return toCopy;
		}
		return is.read(b, off, len);
	}

	static Logger log = LoggerFactory.getLogger(HttpParser.class);

	static List methods = Arrays.asList(new String[] { "GET", "HEAD", "POST" });

	int index;

	boolean first;

	byte[] buffer = new byte[4096];

	InputStream is;

	public HttpParser(InputStream is) {
		this.is = is;
	}

	public boolean parse() throws IOException, ParseException {
		log.debug("hee");
		index += is.read(buffer, index, buffer.length - index);
		String line = new String(buffer);
		log.debug(line);
		int splitAt = line.indexOf("\r\n\r\n");
		if (splitAt == -1)
			return false;
		bodyIndex = splitAt + 4;

		// log.debug(line.substring(bodyIndex));
		line = line.substring(0, splitAt);
		if (line.length() == 0)
			throw new IOException("Bad HTTP header");
		String[] headerLines = line.split("\r\n");
		if (headerLines.length == 0)
			throw new IOException("Bad HTTP header");
		String[] httpStuff = ((String) headerLines[0]).split(" ");
		if (httpStuff.length != 3)
			throw new IOException("Bad HTTP header: " + httpStuff.length);

		method = httpStuff[0];
		uri = httpStuff[1];
		protocol = httpStuff[2];
		log.debug(method + " " + uri + " " + protocol);

		headers = new Header[0];

		headers = new Header[headerLines.length - 1];
		for (int i = 1; i < headerLines.length; i++) {
			String[] header = headerLines[i].split(": ", 2);
			if (header.length != 2)
				throw new IOException("Bad Header:" + headerLines[i]);
			Header h = headers[i - 1] = new Header(header[0], header[1]);
			log.debug(h.toExternalForm());
			if (h.getName().equals("Content-Type"))
				this.contentType = h.getValue();
			else if (h.getName().equals("Content-Length"))
				this.contentLength = NumberFormat.getIntegerInstance().parse(
						h.getValue()).intValue();

		}

		return true;
	}

	String method, uri, protocol;

	public String getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	public String getProtocol() {
		return protocol;
	}

	public int getLength() {
		return buffer.length;
	}

	int bodyIndex;

	public int getBodyIndex() {
		return bodyIndex;
	}

	public void setBodyIndex(int i) {
		this.bodyIndex = i;
	}

	int contentLength;

	public int getContentLength() {
		return contentLength;
	}

	String contentType;

	public String getContentType() {
		return contentType;
	}

	Header[] headers;

	public Header[] getHeaders() {
		return headers;
	}

	public void close() throws IOException {
		is.close();
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public int getIndex() {
		return index;
	}

	/*
	 * public int read(byte[] b) throws IOException { if (bodyIndex < index)
	 * return super.read(b);
	 *  // TODO Auto-generated method stub return is.read(b); }
f	 */
}
