package uni.apps.responsetesting.mail;

import java.security.AccessController;
import java.security.Provider;

public class JSSEProvider extends Provider {

	protected JSSEProvider() {
		super("HarmonyJSSE", 1.0, "Harmony JSSE Provider");
		// TODO Auto-generated constructor stub
		AccessController.doPrivileged(new java.security.PrivilegedAction<Void>(){
			public Void run(){
				put("SSLContext.TLS","org.apache.harmony.xnet.provider.jsse.SSLContextImpl");
				put("Alg.Alias.SSLContect.TSLv1", "TSL");
				put("KeyManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl");
				put("TrustManagerFactory", "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl");
				return null;
			}
		});
	}

}
