package silveira.felipe.intelligent.loadbalancer.loadbalancer;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import silveira.felipe.intelligent.loadbalancer.model.WorkRequestOrder;
import silveira.felipe.intelligent.loadbalancer.workunit.WorkReport;
import silveira.felipe.intelligent.loadbalancer.workunit.WorkUnitManager;
import silveira.felipe.intelligent.loadbalancer.workunit.WorkUnitManagerImpl;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class RoundRobin extends LoadBalancer {

    /**
     * Logger object used to log messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoundRobin.class);

    /**
     * RoundRobin token to track the current node.
     */
    private int roundRobinToken = 0;

    /**
     * Number of available nodes.
     */
    private final static int AVAILABLE_NODES = 3;

    /**
     * {@link WorkUnitManager}.
     */
    private WorkUnitManager workerManager = new WorkUnitManagerImpl();

    /**
     * RoundRobin constructor.
     *
     * @param workRequestMaxNumber the work request max number.
     */
    public RoundRobin(int workRequestMaxNumber) {
        super(workRequestMaxNumber);
    }

    public String run() {
        LOGGER.info("Starting Round Robin Balancing.");
        File libsvmFile = new File("roundRobinData.txt");
        StringBuilder libsvmDataStringBuilder = new StringBuilder();
        Stack<WorkRequestOrder> workOrderStack = createWorkRequestStack(workRequestMaxNumber);

        for(int i = 0; i < workOrderStack.size(); i++) {
            LOGGER.info("Processing work order #{} from {}.", i, workOrderStack.size());
            WorkRequestOrder workRequestOrder = workOrderStack.pop();
            String response = findNodeAvailable(workRequestOrder.getNumberOfWorkers(), workRequestOrder.getWorkLoadType());
            try {
                FileUtils.writeStringToFile(libsvmFile, response, "UTF-8", true);
            } catch (IOException e) {
                LOGGER.info("Error while writing file.",  e);
            }
            libsvmDataStringBuilder.append(response);
        }
        return libsvmDataStringBuilder.toString();
    }

    /**
     * The method will go round in the node list until finds an available node.
     */
    private String findNodeAvailable(int numberOfWorkers, String workType) {
        LOGGER.info("Starting findNodeAvailable, actual node={} numberOfWorkers={}", roundRobinToken, numberOfWorkers);
        boolean nodeFound = false;
        StringBuilder stringBuilder = new StringBuilder();
        int counter = 0;

        while (!nodeFound) {
            LOGGER.info("Trying node={}", roundRobinToken);
            WorkReport workReport = workerManager.workRequest(numberOfWorkers, this.roundRobinToken, workType);
            if (workReport.getWorkAccepted()) {
                nodeFound = true;
                stringBuilder.append(roundRobinToken)
                        .append(' ')
                        .append("1:").append(numberOfWorkers)
                        .append(' ')
                        .append("2:").append(this.loadTypeToLabel(workType))
                        .append('\n');
            }

            this.nextToken();
            counter++;

            if (counter == AVAILABLE_NODES - 1) {
                LOGGER.info("All nodes are unavailable. Waiting 1 min...");
                counter = 0;
                try {
                   Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        LOGGER.info("Node found={}", roundRobinToken);
        LOGGER.info("String appended={}", stringBuilder.toString());
        return stringBuilder.toString();
    }

    private void nextToken() {
        if (roundRobinToken == AVAILABLE_NODES - 1) {
            roundRobinToken = 0;
        } else {
            roundRobinToken++;
        }
    }

}
