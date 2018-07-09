package ntlmproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;

public class StreamingRequestEntity implements RequestEntity {

	private HttpHeaderParser parser;
	private InputStream input;

	private byte[] repeatable;

	public StreamingRequestEntity(HttpHeaderParser parser, InputStream is ) {
		this.parser = parser;
		this.input = is;
	}

	public long getContentLength() {
		return parser.getContentLength();
	}

	public String getContentType() {
		return parser.getContentType();
	}

	public boolean isRepeatable() {
		return true;
	}

	public void writeRequest(OutputStream out) throws IOException {
		// TODO read might return -1
		if (repeatable == null) {
			repeatable = new byte[(int) this.getContentLength()];
			long length = this.getContentLength();
			for (int i = 0; i < length; i++)
				repeatable[i] = (byte) input.read();
		}
		out.write(repeatable);

	}

}
