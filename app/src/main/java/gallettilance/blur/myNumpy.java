package gallettilance.blur;

public class myNumpy {

    public static double dot(double[] a, double[] b) {
        if(a.length != b.length){
            throw new IllegalArgumentException("The dimensions have to be equal!");
        }

        double sum = 0;
        for(int i = 0; i < a.length; i++){
            sum += a[i] * b[i];
        }

        return sum;
    }

    public static double[][] matmul(double[][] A, double[][] B) {

        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        double[][] result = new double[aRows][bColumns];

        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                result[i][j] = 0.0;
            }
        }

        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                for (int k = 0; k < aColumns; k++) {
                    result[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return result;
    }

    public static double[][] activate(double[][] A, NeuralNetwork NN) {
        for(int i=0; i < A.length; i++){
            for(int j=0; j < A[i].length; j++){
                A[i][j] = NN.activation_function(A[i][j]);
            }
        }

        return A;
    }

}
