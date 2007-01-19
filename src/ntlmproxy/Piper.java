package ntlmproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Piper implements Runnable {
	static Logger log = LoggerFactory.getLogger(Piper.class);

	InputStream is;

	OutputStream os;

	byte[] buffer = new byte[1500];

	int read;

	public Piper(InputStream is, OutputStream os) {
		this.is = is;
		this.os = os;
	}

	public Piper() {
	}

	/*
	 * protected void read() throws IOException { read = is.read(buffer); }
	 * 
	 * protected void write() throws IOException { os.write(buffer, 0, read); }
	 */

	public void run() {
		try {
			while (true) {
				int read = is.read(buffer);
//				if (read>0)
	//			log.debug(new String(buffer,0,read));
				if (read == -1)
					break;
				os.write(buffer, 0, read);
			}
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
		close();

	}

	public InputStream getIs() {
		return is;
	}

	public void setIs(InputStream is) {
		this.is = is;
	}

	public OutputStream getOs() {
		return os;
	}

	public void setOs(OutputStream os) {
		this.os = os;
	}

	public void close() {
		try {
			is.close();
		} catch (Exception ex) {
			log.debug(ex.getMessage(), ex);
		}
		try {
			os.close();
		} catch (Exception ex) {
			log.debug(ex.getMessage(), ex);
		}

	}
}
