package com.huawei.it.euler.util;

import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.prng.SP800SecureRandom;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class ThreadLocalSecureRandom {

    private static final ThreadLocal<SecureRandom> secureRandoms = ThreadLocal.withInitial(ThreadLocalSecureRandom::create);

    public static SecureRandom getInstance() {
        return secureRandoms.get();
    }

    private static SecureRandom create() {
        try {
            SecureRandom source = SecureRandom.getInstanceStrong();
            boolean predictionResistant = true;
            AESEngine aESEngine = new AESEngine();
            int cipherLen = 256;
            int entropyBitesRequired = 384;
            byte[] nonce = new byte[cipherLen / 8];
            source.nextBytes(nonce);
            boolean reSeed = false;
            SP800SecureRandom sP800SecureRandom = (new SP800SecureRandomBuilder(source, predictionResistant))
                    .setEntropyBitsRequired(entropyBitesRequired)
                    .buildCTR(aESEngine, cipherLen, nonce, reSeed);
            sP800SecureRandom.nextInt();
            return sP800SecureRandom;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("cannot get strong random instance", e);
        }
    }
}
