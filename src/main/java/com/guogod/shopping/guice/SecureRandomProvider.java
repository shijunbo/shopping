package com.guogod.shopping.guice;

import com.google.inject.Provider;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by shijunbo on 2015/7/15.
 */
public class SecureRandomProvider implements Provider<SecureRandom> {
    @Override
    public SecureRandom get() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return  null;
    }
}
