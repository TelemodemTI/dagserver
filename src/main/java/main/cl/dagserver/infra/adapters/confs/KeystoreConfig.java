package main.cl.dagserver.infra.adapters.confs;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:properties-config.xml")
public class KeystoreConfig {

	
	@Value( "${param.keystore.password}" )
	private String keystorePwd;
	
	@Bean
	public KeyStore initializeKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		char[] pwdArray = keystorePwd.toCharArray();
		ks.load(null, pwdArray);
		return ks;
	}
}
