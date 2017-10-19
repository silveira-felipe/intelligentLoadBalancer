package silveira.felipe.intelligent.loadbalancer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import silveira.felipe.intelligent.loadbalancer.loadbalancer.Observer;
import silveira.felipe.intelligent.loadbalancer.loadbalancer.RoundRobin;

@Service
@ComponentScan
@EnableAutoConfiguration
public class LoadBalancerManagerImpl implements LoadBalancerManager {

    /**
     * Logger object used to log messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadBalancerManagerImpl.class);

    @Override
    public String roundRobin(final int workRequestMaxNumber) {
        RoundRobin roundRobin = new RoundRobin(workRequestMaxNumber);
        return roundRobin.run();
    }

    @Override
    public String observer(final int workRequestMaxNumber) {
        Observer observer = new Observer(workRequestMaxNumber);
        return observer.run();
    }

    @Override
    public String asker(final int workRequestMaxNumber) {
        return null;
    }


}
