package silveira.felipe.intelligent.loadbalancer.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WorkRequestOrder {

    /**
     * Max number of workers.
     */
    private int numberOfWorkers;

    /**
     * Work load type.
     */
    private String workLoadType;

    /**
     * Work request order constructor.
     *
     * @param numberOfWorkers the work request max number.
     * @param workLoadType work load type.
     */
    public WorkRequestOrder(int numberOfWorkers, String workLoadType) {
        this.numberOfWorkers = numberOfWorkers;
        this.workLoadType = workLoadType;
    }

    /**
     * This method gets the number of workers.
     *
     * @return number of workers.
     */
    public int getNumberOfWorkers() {
        return numberOfWorkers;
    }

    /**
     * This method sets the number of workers.
     *
     * @param numberOfWorkers the number of workers.
     */
    public void setNumberOfWorkers(int numberOfWorkers) {
        this.numberOfWorkers = numberOfWorkers;
    }

    /**
     * This method gets the work load type.
     *
     * @return the work load type.
     */
    public String getWorkLoadType() {
        return workLoadType;
    }

    /**
     * This method sets the work load type.
     *
     * @param workLoadType the work load type.
     */
    public void setWorkLoadType(String workLoadType) {
        this.workLoadType = workLoadType;
    }
}
