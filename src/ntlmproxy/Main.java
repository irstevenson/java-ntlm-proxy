package ntlmproxy;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	public static String PROPS_FILE = "ntlm-proxy.properties";

	public static String PROXY_PORT = "proxy.port";

	public static String PROXY_DELEGATE_HOST_NAME = "delegate.host.name";

	public static String PROXY_DELEGATE_HOST_PORT = "delegate.host.port";

	public static String PROXY_DELEGATE_USERNAME = "delegate.username";

	public static String PROXY_DELEGATE_PASSWORD = "delegate.password";

	public static String PROXY_DELEGATE_DOMAIN = "delegate.domain";

	public static String PROXY_FORWARD = "proxy.forward";

	public static String PROXY_NO_DELEGATE = "proxy.nodelegate";

	public static String PROXY_LOG_WIRE = "proxy.log.wire";

	public static String PROXY_LOG_DISABLE = "proxy.log.disable";

	static final Logger log = LoggerFactory.getLogger(Main.class);

	public static Pattern noForwardPattern;
	public static String delegatePassword;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			Class.forName("ntlmproxy.DodgyURI");

			if (!new File(PROPS_FILE).exists())
				throw new Exception("Can't locate props file: " + PROPS_FILE);
			Properties props = new Properties();
			props.load(new FileInputStream(PROPS_FILE));
			log.info("Starting NTLM proxy");

			if (props.getProperty(Main.PROXY_LOG_DISABLE, "false").equals(
					"true")) {
				org.apache.log4j.Logger.getRootLogger().removeAllAppenders();
			} else if (props.getProperty(Main.PROXY_LOG_WIRE, "false").equals(
					"true")) {
				org.apache.log4j.Logger wire = org.apache.log4j.Logger
						.getLogger("httpclient.wire");
				wire.setLevel(Level.DEBUG);
			}

			String delegatePassword = props.getProperty(Main.PROXY_DELEGATE_PASSWORD);
			if (delegatePassword == null)
			{
				System.out.print("Enter password for delegate proxy: ");
				try
				{
					Class clazz = Class.forName("java.io.Console");
					delegatePassword = new String((char[])clazz.getMethod("readPassword",null).invoke(System.class.getMethod("console",null).invoke(null,null),null));
				}catch(Exception e)
				{
					throw new Exception("Use Java 1.6 for console passwords.");
				}
			}	
			String noForward = props.getProperty(Main.PROXY_NO_DELEGATE);
			if (noForward != null) {
				log.info("No delegate for: " + noForward);
				noForwardPattern = Pattern.compile(noForward);
			}

			/*
			 * Server server = new Server(Integer.parseInt(props.getProperty(
			 * Main.PROXY_PORT, "3128"))); Context root = new Context(server,
			 * "/", Context.SESSIONS); root.setInitParams(props);
			 * root.addServlet(new ServletHolder(new NTLMProxy()), "/*");
			 * server.start();
			 */
			String forwardString = props.getProperty(Main.PROXY_FORWARD);
			new HttpForwarder(props, Integer.parseInt(props
					.getProperty(Main.PROXY_PORT))).start();

			if (forwardString != null) {
				String[] forwards = forwardString.split(",");
				Pattern pattern = Pattern.compile("(\\d+):([^:]+):(\\d+)");
				for (int i = 0; i < forwards.length; i++) {
					Matcher m = pattern.matcher(forwards[i]);
					if (!m.matches())
						throw new Exception(
								"Forward format is localport:remotehost:remoteport, got "
										+ forwards[i]);
					log.info("Forwarding port: " + forwards[i]);
					new Forwarder(props, Integer.parseInt(m.group(1)), m
							.group(2), Integer.parseInt(m.group(3))).start();
				}
			}
			System.err.println("Logging to ntlm-proxy.log");
		} catch (Exception e) {
			log.error(e.getMessage());
			System.err.println("FATAL: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
