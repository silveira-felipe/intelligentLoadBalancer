package silveira.felipe.intelligent.loadbalancer.loadbalancer;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import silveira.felipe.intelligent.loadbalancer.ai.AiManagerImpl;
import silveira.felipe.intelligent.loadbalancer.model.WorkRequestOrder;
import silveira.felipe.intelligent.loadbalancer.workunit.WorkReport;
import silveira.felipe.intelligent.loadbalancer.workunit.WorkUnitManager;
import silveira.felipe.intelligent.loadbalancer.workunit.WorkUnitManagerImpl;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Observer extends LoadBalancer {

    /**
     * Logger object used to log messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Observer.class);

    /**
     * Number of available nodes.
     */
    private final static int AVAILABLE_NODES = 3;

    /**
     * Load balancer wait time counter.
     */
    private int waitTimeCounter = 0;

    /**
     * {@link WorkUnitManager}.
     */
    private WorkUnitManager workerManager = new WorkUnitManagerImpl();

    /**
     * {@link WorkUnitManager}.
     */
    private AiManagerImpl aiManager = new AiManagerImpl();

    /**
     * Observer constructor.
     *
     * @param workRequestMaxNumber the work request max number.
     */
    public Observer(int workRequestMaxNumber) {
        super(workRequestMaxNumber);
    }

    /**
     * This method runs the Observer Load Balancing.
     */
    public String run() {
        LOGGER.info("Starting Observer Balancing.");
        final long startTime = Instant.now().toEpochMilli();
        File libsvmFile = new File("observerData.txt");
        StringBuilder libsvmDataStringBuilder = new StringBuilder();
        Stack<WorkRequestOrder> workOrderStack = createWorkRequestStack(workRequestMaxNumber);

        for (int i = 0; i < workOrderStack.size(); i++) {
            LOGGER.info("Processing work order #{} from {}.", i, workOrderStack.size());
            WorkRequestOrder workRequestOrder = workOrderStack.pop();
            String response = predictAvailableNode(workRequestOrder.getNumberOfWorkers(), workRequestOrder.getWorkLoadType());
            try {
                LOGGER.info("Writing in the file={}.", response);
                FileUtils.writeStringToFile(libsvmFile, response, "UTF-8", true);
            } catch (IOException e) {
                LOGGER.info("Error while writing file.", e);
            }
            libsvmDataStringBuilder.append(response);
        }

        final long time = Instant.now().toEpochMilli() - startTime;

        return "It took=" + time + "\nHaving to wait=#" + waitTimeCounter;
    }

    /**
     * The method will go trough the nodes list until finds an available node,
     * using the prediction from the Machine Learn algorithm.
     */
    private String predictAvailableNode(int numberOfWorkers, String workType) {
        LOGGER.info("Starting predictAvailableNode, numberOfWorkers={} workType={}", numberOfWorkers, workType);

        boolean nodeFound = false;
        int counter = 0;
        StringBuilder stringBuilder = new StringBuilder();
        List<Double> predictionList;
        ArrayList<Integer> nodeArrayOrderList;

        predictionList = aiManager.predict(numberOfWorkers, this.loadTypeToLabel(workType));
        nodeArrayOrderList = getNodeOrderArray(predictionList);
        LOGGER.debug("nodeArrayOrderList=", nodeArrayOrderList);

        while (!nodeFound) {
            LOGGER.info("Trying node={}", nodeArrayOrderList);
            WorkReport workReport = workerManager.workRequest(numberOfWorkers, counter, workType);
            if (workReport.getWorkAccepted()) {
                nodeFound = true;
                stringBuilder.append(nodeArrayOrderList.get(counter))
                        .append(' ')
                        .append("1:").append(numberOfWorkers)
                        .append(' ')
                        .append("2:").append(this.loadTypeToLabel(workType))
                        .append('\n');
            }

            counter++;

            if (counter == AVAILABLE_NODES - 1) {
                LOGGER.info("All nodes are unavailable. Waiting 2 secs...");
                counter = 0;
                try {
                    waitTimeCounter++;
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        LOGGER.info("Node found={}", counter);
        LOGGER.info("String appended={}", stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * This method return a list with the node order to follow.
     */
    private ArrayList<Integer> getNodeOrderArray(List<Double> list) {
        ArrayList<Integer> nodeOrderArrayList = new ArrayList<>();
        ArrayList predictionList = new ArrayList((list));
        int size = predictionList.size();

        for (int i = 0; i < size; i++) {
            int index = predictionList.indexOf(Collections.max(predictionList));
            nodeOrderArrayList.add(index);
            predictionList.remove(index);
            predictionList.add(index, "0.0");
        }
        return nodeOrderArrayList;
    }
}
