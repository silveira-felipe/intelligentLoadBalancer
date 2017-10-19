package silveira.felipe.intelligent.loadbalancer.loadbalancer;

import silveira.felipe.intelligent.loadbalancer.model.WorkRequestOrder;

import java.util.Random;
import java.util.Stack;

public class LoadBalancer {

    /**
     * Work request max number.
     */
    protected final int workRequestMaxNumber;

    /**
     * WorkStack used for test (DEBUG ONLY)
     */
    protected Stack<WorkRequestOrder> workOrderStack = new Stack<>();

    /**
     * Work request order stack.
     */
    protected final Stack<WorkRequestOrder> workRequestOrderStack;

    /**
     * LoadBalancer constructor.
     *
     * @param workRequestMaxNumber the work request max number.
     */
    public LoadBalancer(int workRequestMaxNumber) {
        this.workRequestMaxNumber = workRequestMaxNumber;
        this.workRequestOrderStack = createWorkRequestStack(workRequestMaxNumber);
    }

    public int getWorkRequestMaxNumber() {
        return workRequestMaxNumber;
    }

    /**
     * Create the work request order stack. This method is allowing only one
     * stack with the propose to compare the different load balancer types.
     *
     * @param workRequestMaxNumber the work request max number.
     * @return the work request order stack.
     */
    public Stack<WorkRequestOrder> createWorkRequestStack(int workRequestMaxNumber) {
        if (workOrderStack.isEmpty()) {
            int numberOfWorkers;
            String workLoadType;

            Random random = new Random();
            for (int i = 0; i < workRequestMaxNumber; i++) {
                numberOfWorkers = random.nextInt(3) + 1;
                switch (random.nextInt(3)) {
                    case 0:
                        workLoadType = "light";
                        break;
                    case 1:
                        workLoadType = "medium";
                        break;
                    default:
                        workLoadType = "high";
                        break;
                }
                workOrderStack.push(new WorkRequestOrder(numberOfWorkers, workLoadType));
            }
        }
        return workOrderStack;
    }

    /**
     * Return a numerical representation for the work load type.
     *
     * @param loadType work load type
     * @return a numerical representation of the load type
     */
    public int loadTypeToLabel(String loadType) {
        switch (loadType) {
            case "high":
                return 3;
            case "medium":
                return 2;
            default:
                return 1;
        }
    }
}
