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

package silveira.felipe.intelligent.loadbalancer.workunit;

import static javax.servlet.http.HttpServletResponse.SC_OK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import silveira.felipe.intelligent.loadbalancer.commons.OkHttpClientHelper;
import silveira.felipe.intelligent.loadbalancer.config.ConfigManager;

import java.io.IOException;

@Service
@ComponentScan
@EnableAutoConfiguration
public class WorkUnitManagerImpl implements WorkUnitManager {

    /**
     * Logger object used to log messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkUnitManagerImpl.class);

    /**
     * Constant with the WorkUnit end point.
     */
    private static final String WORKUNIT_API = "workunit/api/v0.1/worker/";

    /**
     * {@link OkHttpClientHelper}.
     */
    private final OkHttpClientHelper okHttpClientHelper = new OkHttpClientHelper();

    /**
     * {@inheritDoc}.
     */
    @Override
    public WorkReport workRequest(final int workers, final int workUnitNumber, final String workLoadType) {
        LOGGER.debug("Starting Work Request: workUnitNumber={}, workers=#{}", workUnitNumber, workers);

        String workUnitPort = Integer.toString(ConfigManager.getWorkUnitPort() + workUnitNumber);
        String workUnitUrl = new StringBuilder(ConfigManager.getWorkUnitUrl())
                .append(workUnitPort)
                .append("/")
                .append(WORKUNIT_API)
                .toString();

        final WorkUnitService workUnitService = getWorkUnitService(workUnitUrl);

        final Call<WorkReport> call
                = workUnitService.workRequest(workers, workLoadType);
        try {
            LOGGER.debug("Requesting {} workers from workUnit={}.", workers, workUnitNumber);
            final Response<WorkReport> response = call(call);
            LOGGER.info("Response from WorkUnit:", response.message());
            if (response.code() == SC_OK && response.body() != null) {
                return response.body();
            } else {
                LOGGER.error("Error requesting worker from workUnit={}. Response was: {}.",
                        workers, response.message());
                throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.error("Error requesting worker from workUnit. Response was: {}.", e.getMessage(), e);
        }
        return new WorkReport(workUnitNumber + "", false);
    }

    /**
     * This method create a {@link Retrofit} WorkUnitService.
     *
     * @param url a workUnit base URL.
     * @return {@link Retrofit} WorkUnitService
     */
    private WorkUnitService getWorkUnitService(final String url) {
        final Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClientHelper.getClient())
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(WorkUnitService.class);
    }

    /**
     * Call WorkUnit.
     *
     * @param call {@link Call}
     * @return response for the call
     * @throws IOException if there is an error performing the call.
     */
    private Response call(final Call call) throws IOException {
        Response response = null;
        for (int i = 0; i < 3; i++) {
            response = call.clone().execute();
            if (response.isSuccessful()) {
                return response;
            } else {
                LOGGER.error("Attempt #{} failed to make call on WorkUnit", i);
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    LOGGER.error("Thread.sleep fail {}", e.getMessage());
                }
            }
        }
        return response;
    }
}
