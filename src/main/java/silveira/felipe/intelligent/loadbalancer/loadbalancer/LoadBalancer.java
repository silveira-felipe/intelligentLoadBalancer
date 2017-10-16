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
     * Work request order stack.
     */
    protected final Stack<WorkRequestOrder> workRequestOrderStack;

    /**
     * LoadBalancer constructor.
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
     * Create the work request order stack.
     * @param workRequestMaxNumber the work request max number.
     * @return the work request order stack.
     */
    public Stack<WorkRequestOrder> createWorkRequestStack(int workRequestMaxNumber) {
        int numberOfWorkers;
        String workLoadType;
        Stack<WorkRequestOrder> workOrderStack = new Stack<>();

        Random random = new Random();
        for (int i = 0; i < workRequestMaxNumber;i++) {
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
        return workOrderStack;
    }

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
