package silveira.felipe.intelligent.loadbalancer.loadbalancer;

import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.mllib.util.MLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;
import silveira.felipe.intelligent.loadbalancer.model.WorkRequestOrder;
import silveira.felipe.intelligent.loadbalancer.workunit.WorkReport;
import silveira.felipe.intelligent.loadbalancer.workunit.WorkUnitManager;
import silveira.felipe.intelligent.loadbalancer.workunit.WorkUnitManagerImpl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
     * {@link WorkUnitManager}.
     */
    private WorkUnitManager workerManager = new WorkUnitManagerImpl();

    /**
     * Decision Tree model used by the observer.
     */
    private DecisionTreeModel decisionTreeModel;

    /**
     * LoadBalancer constructor.
     *
     * @param workRequestMaxNumber the work request max number.
     */
    public Observer(int workRequestMaxNumber) {
        super(workRequestMaxNumber);
        this.decisionTreeModel = createDecisionTree();
    }

    public String run() {
        LOGGER.info("Starting Observer Balancing.");
        File libsvmFile = new File("observerData.txt");
        StringBuilder libsvmDataStringBuilder = new StringBuilder();
        Stack<WorkRequestOrder> workOrderStack = createWorkRequestStack(workRequestMaxNumber);

        for(int i = 0; i < workOrderStack.size(); i++) {
            LOGGER.info("Processing work order #{} from {}.", i, workOrderStack.size());
            WorkRequestOrder workRequestOrder = workOrderStack.pop();
            String response = predictAvailableNode(workRequestOrder.getNumberOfWorkers(), workRequestOrder.getWorkLoadType());
            try {
                FileUtils.writeStringToFile(libsvmFile, response, "UTF-8", true);
            } catch (IOException e) {
                LOGGER.info("Error while writing file.",  e);
            }
            libsvmDataStringBuilder.append(response);
        }
        return libsvmDataStringBuilder.toString();
    }

    public DecisionTreeModel createDecisionTree() {
        LOGGER.info("Method createDecisionTree started.");
        SparkConf sparkConf = new SparkConf().setAppName("ObserverJavaDecisionTree");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        LOGGER.info("Loading data.");
        String dataPath = "src/resources/roundRobinData.txt";
        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(jsc.sc(), dataPath).toJavaRDD();

        LOGGER.info("Creating training and test data.");
        JavaRDD<LabeledPoint>[] splits = data.randomSplit(new double[]{0.7, 0.3});
        JavaRDD<LabeledPoint> trainingData = splits[0];
        JavaRDD<LabeledPoint> testData = splits[1];

        LOGGER.info("Method createDecisionTree started.");
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
        String impurity = "gini";
        int maxDepth = 5;
        int maxBins = 32;

        DecisionTreeModel model = DecisionTree.trainClassifier(trainingData, AVAILABLE_NODES,
                categoricalFeaturesInfo, impurity, maxDepth, maxBins);

        JavaPairRDD<Double, Double> predictionAndLabel = testData.mapToPair(p ->
                new Tuple2<>(model.predict(p.features()), p.label()));

        double testError = predictionAndLabel.filter(pl -> !pl._1().equals(pl._2())).count()
                / (double) testData.count();

        LOGGER.info("Test error={}.", testError);
        LOGGER.info("Tree model:", model.toDebugString());

        model.save(jsc.sc(), "target/ObserverDecisionTreeClassificationModel");
        return DecisionTreeModel.load(jsc.sc(), "target/ObserverDecisionTreeClassificationModel");
    }

    /**
     * The method will go round in the node list until finds an available node.
     */
    private String predictAvailableNode(int numberOfWorkers, String workType) {
        LOGGER.info("Starting predictAvailableNode, numberOfWorkers={} workType={}", numberOfWorkers, workType);
        boolean nodeFound = false;
        StringBuilder stringBuilder = new StringBuilder();
        SparkConf sparkConf = new SparkConf().setAppName("ObserverJavaDecisionTree");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
        int counter = 0;
        Map currentNode;

        while (!nodeFound) {
            String s = stringBuilder
                    .append("1:").append(numberOfWorkers)
                    .append(' ')
                    .append("2:").append(this.loadTypeToLabel(workType))
                    .toString();
            JavaRDD<LabeledPoint> data = MLUtils.loadLabeledPoints(jsc.sc(), s).toJavaRDD();
            JavaPairRDD<Double, Double> predictionAndLabel =
                    data.mapToPair(p -> new Tuple2<>(decisionTreeModel.predict(p.features()), p.label()));
            currentNode = predictionAndLabel.collectAsMap();
            LOGGER.info("Trying node={}", counter);
            WorkReport workReport = workerManager.workRequest(numberOfWorkers, counter, workType);
            if (workReport.getWorkAccepted()) {
                nodeFound = true;
                stringBuilder.append(counter)
                        .append(' ')
                        .append("1:").append(numberOfWorkers)
                        .append(' ')
                        .append("2:").append(this.loadTypeToLabel(workType))
                        .append('\n');
            }

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
        LOGGER.info("Node found={}", counter);
        LOGGER.info("String appended={}", stringBuilder.toString());
        return stringBuilder.toString();
    }
}
