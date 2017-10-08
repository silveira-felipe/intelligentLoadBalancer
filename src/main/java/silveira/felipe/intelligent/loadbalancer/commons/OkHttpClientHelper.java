/**
 * MIT License
 * <p>
 * Copyright (c) 2017.  Felipe Silveira
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package silveira.felipe.intelligent.loadbalancer.commons;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * {@link OkHttpClient} Helper.
 */
public class OkHttpClientHelper {

    /**
     * Logger object used to log messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OkHttpClientHelper.class);

    /**
     * SSL constant.
     */
    private static final String SSL = "SSL";

    /**
     * @return a {@link OkHttpClient}.
     */
    public OkHttpClient getClient() {
        return getClient(10000);
    }

    /**
     * @param timeout a timeout for connect, read and write operation.
     * @return a {@link OkHttpClient}.
     */
    public OkHttpClient getClient(final int timeout) {

        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(timeout, MILLISECONDS)
                .readTimeout(timeout, MILLISECONDS)
                .writeTimeout(timeout, MILLISECONDS);

        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance(SSL);
            sslContext.init(null, NaiveTrustManager.NAIVE_TRUST_MANAGER_ARRAY,
                    new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier((hostname, session) -> true);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LOGGER.error("Error disabling certificate validation.", e);
        }

        return builder.build();
    }

}
