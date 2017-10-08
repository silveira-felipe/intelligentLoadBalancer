/**
 * MIT License
 *
 * Copyright (c) 2017.  Felipe Silveira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package silveira.felipe.intelligent.loadbalancer.commons;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Naive trust manager that accepts anything.
 */
public class NaiveTrustManager implements X509TrustManager {

    public static final TrustManager[] NAIVE_TRUST_MANAGER_ARRAY =
            new TrustManager[]{new NaiveTrustManager()};

    public void checkClientTrusted(final X509Certificate[] x509Certificates, final String str)
            throws CertificateException {
    }

    public void checkServerTrusted(final X509Certificate[] x509Certificates, final String str)
            throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{};
    }

}
