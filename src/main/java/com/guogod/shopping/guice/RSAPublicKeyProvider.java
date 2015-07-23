package com.guogod.shopping.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.commons.configuration.Configuration;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

/**
 * Created by shijunbo on 2015/7/15.
 */
public class RSAPublicKeyProvider implements Provider<PublicKey> {
    private Configuration configuration;
    @Inject
    public RSAPublicKeyProvider(Configuration configuration)
    {
        this.configuration = configuration;
    }
    @Override
    public PublicKey get() {
        BigInteger modulus =   new BigInteger(configuration.getString("rsa.modulus"));
        BigInteger publicExponent = new BigInteger(configuration.getString("rsa.public.exponent"));
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus,publicExponent);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return  keyFactory.generatePublic(publicKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
