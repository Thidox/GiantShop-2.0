/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.digest;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.StringUtils;

/**
 * Operations to simplify common {@link java.security.MessageDigest} tasks. This class is thread safe.
 *
 * @version $Id: DigestUtils.java 1380826 2012-09-04 19:35:06Z ggregory $
 */
public class DigestUtils {

    private static final int STREAM_BUFFER_LENGTH = 1024;

    /**
     * Read through an InputStream and returns the digest for the data
     *
     * @param digest
     *            The MessageDigest to use (e.g. MD5)
     * @param data
     *            Data to digest
     * @return MD5 digest
     * @throws IOException
     *             On error reading from the stream
     */
    private static byte[] digest(MessageDigest digest, InputStream data) throws IOException {
        byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);

        while (read > -1) {
            digest.update(buffer, 0, read);
            read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);
        }

        return digest.digest();
    }

    /**
     * Calls {@link StringUtils#getBytesUtf8(String)}
     *
     * @param data
     *            the String to encode
     * @return encoded bytes
     */
    private static byte[] getBytesUtf8(String data) {
        return StringUtils.getBytesUtf8(data);
    }

    /**
     * Returns a <code>MessageDigest</code> for the given <code>algorithm</code>.
     *
     * @param algorithm
     *            the name of the algorithm requested. See <a
     *            href="http://java.sun.com/j2se/1.3/docs/guide/security/CryptoSpec.html#AppA">Appendix A in the Java
     *            Cryptography Architecture API Specification & Reference</a> for information about standard algorithm
     *            names.
     * @return An MD5 digest instance.
     * @see MessageDigest#getInstance(String)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught.
     */
    public static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Updates the given {@link MessageDigest}.
     *
     * @param messageDigest
     *            the {@link MessageDigest} to update
     * @param valueToDigest
     *            the value to update the {@link MessageDigest} with
     * @return the updated {@link MessageDigest}
     * @since 1.7
     */
    public static MessageDigest updateDigest(final MessageDigest messageDigest, byte[] valueToDigest) {
        messageDigest.update(valueToDigest);
        return messageDigest;
    }

    /**
     * Updates the given {@link MessageDigest}.
     *
     * @param messageDigest
     *            the {@link MessageDigest} to update
     * @param valueToDigest
     *            the value to update the {@link MessageDigest} with
     * @return the updated {@link MessageDigest}
     * @since 1.7
     */
    public static MessageDigest updateDigest(final MessageDigest messageDigest, final String valueToDigest) {
        messageDigest.update(getBytesUtf8(valueToDigest));
        return messageDigest;
    }
}
