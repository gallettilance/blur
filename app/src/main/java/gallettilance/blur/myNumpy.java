package gallettilance.blur;

import android.util.Log;

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
        Log.d("aRows", Integer.toString(aRows));

        int bRows = B.length;
        Log.d("bRows", Integer.toString(bRows));

        int aColumns = A[0].length;
        Log.d("aColumns", Integer.toString(aColumns));

        int bColumns = B[0].length;
        Log.d("bColumns", Integer.toString(bColumns));

        if (aRows != bColumns) {
            throw new IllegalArgumentException("A:Rows: " + aRows + " did not match B:Columns " + bColumns + ".");
        }

        double[][] result = new double[aColumns][bRows];

        for (int i = 0; i < aColumns; i++) {
            for (int j = 0; j < bRows; j++) {
                result[i][j] = 0.0;
            }
        }

        for (int i = 0; i < aColumns; i++) {
            for (int j = 0; j < bRows; j++) {
                for (int k = 0; k < aRows; k++) {
                    result[i][j] += A[k][i] * B[j][k];
                }
            }
        }

        return result;
    }

    public static double[][] transpose(double[][] A) {
        double[][] res = new double[A[0].length][A.length];

        for(int i=0; i < A.length; i++){
            for(int j=0; j < A[i].length; j++){
                res[j][i] = A[i][j];
            }
        }

        return res;

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
