package ntlmproxy;

import java.util.BitSet;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;

public class DodgyURI extends URI {

	static{
		URI.abs_path.set(0,256, true);
	}
	
}
