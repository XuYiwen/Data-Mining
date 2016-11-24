/**
 * Created by XuYiwen on 11/13/16.
 */
public class Metrics {
    private int numLabels;
    private int[][] confusionMatrix;

    private double accuracy;
    private double[] sensitivity, specificity, precision, f1Score, f05Score, f2Score;

    public Metrics(int[][] _cm) {
        confusionMatrix = _cm;
        numLabels = _cm.length;

        sensitivity = new double[numLabels];
        specificity = new double[numLabels];
        precision = new double[numLabels];
        f1Score = new double[numLabels];
        f05Score = new double[numLabels];
        f2Score = new double[numLabels];

        computeOverallAccuracy();
        computeClassMetrics();
    }

    public void computeOverallAccuracy(){
        accuracy = 0;
        double total = 0;
        for (int i = 0; i < numLabels; i++) {
            for (int j = 0; j < numLabels; j++) {
                total += confusionMatrix[i][j];
                if (i == j) accuracy += confusionMatrix[i][j];
            }
        }
        accuracy = accuracy / total;
    }

    public double getAccuracy(){
        return accuracy;
    }

    public void computeClassMetrics(){
        for (int actual = 0; actual < numLabels; actual++) {
            double TP = confusionMatrix[actual][actual];

            double FN = 0;
            for (int predict = 0; predict < numLabels; predict++) {
                if (actual != predict) {
                    FN += confusionMatrix[actual][predict];
                }
            }

            double FP = 0;
            for (int predict = 0; predict < numLabels; predict++) {
                if (actual != predict) {
                    FP += confusionMatrix[predict][actual];
                }
            }

            double TN = 0;
            for (int i = 0; i < numLabels; i++) {
                for (int j = 0; j < numLabels; j++) {
                    if (i != actual && j != actual) {
                        TN += confusionMatrix[i][j];
                    }
                }
            }

            double P = TP + FN;
            double N = TN + FP;

            sensitivity[actual] = TP / P;
            specificity[actual] = TN / N;
            precision[actual] = TP / (TP + FP);
            f1Score[actual] = (2 * precision[actual] * sensitivity[actual])
                    / (precision[actual] + sensitivity[actual]);
            f05Score[actual] = ((1 + 0.5 * 0.5) * precision[actual] * sensitivity[actual])
                    / (0.5 * 0.5 * precision[actual] + sensitivity[actual]);
            f2Score[actual] = ((1 + 2 * 2) * precision[actual] * sensitivity[actual])
                    / (2 * 2 * precision[actual] + sensitivity[actual]);
        }
    }

    public void report(){
        System.out.print(">> Report Metrics...\n");
        System.out.print("-----------------------------------------------\n");
        System.out.format("Overall Accuracy: \t %.3f\n", accuracy);
        System.out.print("-----------------------------------------------\n");
        System.out.format("Sensitivity    \t %s\n", toString(sensitivity));
        System.out.format("Specificity    \t %s\n", toString(specificity));
        System.out.format("Precision      \t %s\n", toString(precision));
        System.out.format("F1_Score       \t %s\n", toString(f1Score));
        System.out.format("F05_Score      \t %s\n", toString(f05Score));
        System.out.format("F2_Score       \t %s\n", toString(f2Score));
        System.out.print("-----------------------------------------------\n");
    }

    private String toString(double[] metric) {
        String s = "";
        for (int i = 0; i < numLabels; i++) {
            s += String.format("%.3f \t", metric[i]);
        }
        s += "";
        return s;
    }
}
