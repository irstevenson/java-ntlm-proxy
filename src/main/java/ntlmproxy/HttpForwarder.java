package ntlmproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.ProxyClient.ConnectResponse;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.httpclient.cookie.CookiePolicy;


public class HttpForwarder extends Thread {

	private final Logger log = LoggerFactory.getLogger(HttpForwarder.class);

	ServerSocket ssocket;

	Properties props;

	ExecutorService threadPool = Executors.newCachedThreadPool();

	HttpClient delegateClient;
	HttpClient noDelegateClient;

	public HttpForwarder(Properties props, int lport) throws IOException {
		ssocket = new ServerSocket(lport);
		this.props = props;

		MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
		manager.getParams().setDefaultMaxConnectionsPerHost(20);
		delegateClient = new HttpClient(manager);
		delegateClient.getHostConfiguration().setProxy(
				props.getProperty(Main.PROXY_DELEGATE_HOST_NAME),
				Integer.parseInt(props
						.getProperty(Main.PROXY_DELEGATE_HOST_PORT)));
		delegateClient.getState().setProxyCredentials(
				new AuthScope(AuthScope.ANY),
				new NTCredentials(props
						.getProperty(Main.PROXY_DELEGATE_USERNAME),
						props
						.getProperty(Main.PROXY_DELEGATE_PASSWORD), InetAddress
						.getLocalHost().getHostName(), props
						.getProperty(Main.PROXY_DELEGATE_DOMAIN)));
		noDelegateClient = new HttpClient(manager);
	}

	static List stripHeadersIn = Arrays.asList(
	        "Content-Type",
            "Content-Length",
            "Proxy-Connection");

	static List stripHeadersOut = Arrays.asList(
	        "Proxy-Authentication",
            "Proxy-Authorization");

	class Handler implements Runnable {
		Socket localSocket;

		public Handler(Socket localSocket) {
			this.localSocket = localSocket;
		}

		public void run() {
			log.debug("run() - START");

			try {
                HttpHeaderParser headerParser = new HttpHeaderParser(
                        readStartlineAndHeaders(localSocket.getInputStream()));

                HttpClient client =
                        (Main.noForwardPattern != null
                                && Main.noForwardPattern.matcher(headerParser.getUri()).find())
                                ? noDelegateClient
                                : delegateClient;

                HttpMethod method;
                switch (headerParser.getMethod()) {
                    case "GET":
                        method = new GetMethod();
                        break;
                    case "POST":
                        method = new PostMethod();
                        break;
                    case "HEAD":
                        method = new HeadMethod();
                        break;
                    case "CONNECT":
                        doConnect(
                                headerParser.getUri(),
                                localSocket.getInputStream(),
                                localSocket.getOutputStream());
                        return;
                    default:
                        throw new Exception("Unknown method: " + headerParser.getMethod());
                }
				if (method instanceof EntityEnclosingMethod) {
					// log.debug(new String(new char[] { (char) bis.read()}));
					EntityEnclosingMethod method2 = (EntityEnclosingMethod) method;
					method2.setRequestEntity(
					        new StreamingRequestEntity(
					                headerParser,
                                    localSocket.getInputStream()));
					// method2.getParams().set
				}
				method.setURI(new URI(headerParser.getUri(),true));
				method.setFollowRedirects(false);
				method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
				// method.getParams().makeLenient();

				log.debug("Preparing headers for request to proxy.");
				for (int i = 0; i < headerParser.getHeaders().length; i++) {
					Header h = headerParser.getHeaders()[i];
					if (stripHeadersIn.contains(h.getName()))
						continue;
					log.debug("... adding: " + h.getName());
					method.addRequestHeader(h);
				}

				log.debug("Sending request to proxy");
				client.executeMethod(method);
				String statusLine = method.getStatusLine().toString();
				log.debug("Proxy status line: " + statusLine);
				localSocket.shutdownInput();
				OutputStream os = localSocket.getOutputStream();
				os.write(statusLine.getBytes());
				os.write("\r\n".getBytes());

				Header[] headers = method.getResponseHeaders();
				log.debug("Sending proxy response headers to client:");
				for (int i = 0; i < headers.length; i++) {
					if (stripHeadersOut.contains(headers[i]))
						continue;
					os.write(headers[i].toExternalForm().getBytes());
					log.debug("> " + headers[i].toExternalForm().trim());
				}

				log.debug("Sending proxy response body to client.");
				InputStream is = method.getResponseBodyAsStream();
				if (is != null) {
					os.write("\r\n".getBytes());
					new Piper(is, os).run();
					// is.close();
				}

				log.debug("Closing connection and stream.");
				method.releaseConnection();
				localSocket.close();

			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}

			log.debug("run() - END");
		}
	}

	public void run() {

		while (true) {
			try {

				Socket s = ssocket.accept();
				threadPool.execute(new Handler(s));
			} catch (IOException e) {
				log.debug(e.getMessage(), e);
				break;
			}
		}
	}

	public void close() throws IOException {
		ssocket.close();
	}

	void doConnect(String fullUri, InputStream is, OutputStream os) {
		String[] uri = fullUri.split(":");
		ProxyClient client = new ProxyClient();
		client.getHostConfiguration().setHost(uri[0], Integer.parseInt(uri[1]));
		client.getHostConfiguration().setProxy(
				props.getProperty(Main.PROXY_DELEGATE_HOST_NAME),
				Integer.parseInt(props
						.getProperty(Main.PROXY_DELEGATE_HOST_PORT)));
		try {
			client.getState().setProxyCredentials(
					new AuthScope(AuthScope.ANY),
					new NTCredentials(props
							.getProperty(Main.PROXY_DELEGATE_USERNAME), props
							.getProperty(Main.PROXY_DELEGATE_PASSWORD),
							InetAddress.getLocalHost().getHostName(), props
									.getProperty(Main.PROXY_DELEGATE_DOMAIN)));
		} catch (UnknownHostException e) {
			log.error(e.getMessage(), e);
		}
		Socket remoteSocket = null;
		try {
			ConnectResponse response = client.connect();
			remoteSocket = response.getSocket();
			if (remoteSocket == null) {
				ConnectMethod method = response.getConnectMethod();
				throw new IOException("Socket not created: "
						+ method.getStatusLine());
			}
			os.write(response.getConnectMethod().getStatusLine().toString()
					.getBytes());

			os.write("\r\n\r\n".getBytes());
			threadPool.execute(new Piper(is, remoteSocket.getOutputStream()));
			new Piper(remoteSocket.getInputStream(), os).run();
			is.close();
			os.close();
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		} finally {
			if (remoteSocket != null)
				try {
					remoteSocket.close();
				} catch (Exception fe) {
					log.debug(fe.getMessage(), fe);
				}
			// TODO
			try {
				os.close();
			} catch (IOException e) {
			}
			try {
				is.close();
			} catch (IOException e) {
			}
			/*
			 * if (localSocket != null) try { localSocket.close(); } catch
			 * (Exception fe) { log.debug(fe.getMessage(), fe); }
			 */

		}
	}

    private String readStartlineAndHeaders(InputStream input) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            byte[] line = HttpParser.readRawLine(input);
            if (line == null || line.length < 1) {
                break;
            }
            if (line.length == 2 && line[0] == '\r' && line[1] == '\n' ) {
            	// blank line found - ideally breaking headers from body
				break;
			}
            stringBuilder.append(new String(line));
        }
        if (stringBuilder.length() < 1) {
            throw new IOException("Invalid HTTP request - no content!");
        }

        return stringBuilder.toString();
    }
}
