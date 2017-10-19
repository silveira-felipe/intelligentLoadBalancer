package silveira.felipe.intelligent.loadbalancer.ai;

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
import java.util.Arrays;
import java.util.List;

@Service
@ComponentScan
@EnableAutoConfiguration
public class AiManagerImpl implements AiManager{

    /**
     * Logger object used to log messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AiManagerImpl.class);

    /**
     * Constant with the AI end point.
     */
    private static final String AI_API = "/loadbalancer/api/v0.1/";

    /**
     * {@link OkHttpClientHelper}.
     */
    private final OkHttpClientHelper okHttpClientHelper = new OkHttpClientHelper();


    @Override
    public List<Double> predict(double numberOfWorkers, double workLoadType) {
        LOGGER.debug("Starting Predict Request: numberOfWorkers={}, workLoadType=#{}", numberOfWorkers, workLoadType);

        String aiUrl = new StringBuilder(ConfigManager.getWorkUnitUrl())
                .append("5000")
                .append(AI_API)
                .toString();

        final AiService aiService = getAiService(aiUrl);

        final Call<String> call
                = aiService.predict(numberOfWorkers, workLoadType);
        try {
            LOGGER.debug("Predicting: numberOfWorkers={}, workLoadType=#{}", numberOfWorkers, workLoadType);
            final Response<String> response = call(call);
            LOGGER.info("Response from AI:", response.message());
            if (response.code() == SC_OK && response.body() != null) {
                List list = Arrays.asList(response.body().split(" "));
                return list;
            } else {
                LOGGER.error("Error requesting prediction from AI. Response was: {}.", response.message());
                throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.error("Error requesting prediction from AI. Response was: {}.", e.getMessage(), e);
        }
        return null;
    }

    /**
     * This method create a {@link Retrofit} AiService.
     *
     * @param url a AI base URL.
     * @return {@link Retrofit} AiService
     */
    private AiService getAiService(final String url) {
        final Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClientHelper.getClient())
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(AiService.class);
    }

    /**
     * Call Ai.
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
                LOGGER.error("Attempt #{} failed to make call on AI", i);
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
