package ntlmproxy;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.commons.httpclient.methods.RequestEntity;

public class StreamingRequestEntity implements RequestEntity {

	HttpParser parser;

	byte[] repeatable;

	public StreamingRequestEntity(HttpParser parser) {
		this.parser = parser;
	}

	public long getContentLength() {
		return parser.getContentLength();
	}

	public String getContentType() {
		// TODO Auto-generated method stub
		return parser.getContentType();
	}

	public boolean isRepeatable() {
		// TODO Auto-generated method stub
		return true;
	}

	public void writeRequest(OutputStream out) throws IOException {
		// TODO read might return -1
		if (repeatable == null) {
			repeatable = new byte[(int) this.getContentLength()];
			long length = this.getContentLength();
			for (int i = 0; i < length; i++)
				repeatable[i] = (byte) parser.read();
		}
		out.write(repeatable);

	}

}
