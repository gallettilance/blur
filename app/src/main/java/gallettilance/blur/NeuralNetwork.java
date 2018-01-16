package gallettilance.blur;

import java.lang.Math;
import org.json.JSONObject;
import android.util.Log;

public class NeuralNetwork {

    private int input_layer;
    private int hidden_layer;
    private int output_layer;
    private double learning_rate;
    private double[][] weights_input_hidden = new double[input_layer][hidden_layer];
    private double[][] weights_hidden_output = new double[hidden_layer][output_layer];

    public double activation_function(double x) {
        //some activation function
        return 1 / (1 + Math.exp(-x));
    }

    public double derivative_activation_function(double x) {
        //the derivative of the activation function
        return activation_function(x) / (1 - activation_function(x));
    }

    public NeuralNetwork(int inode, int hnode, int onode, double lr) {
        this.input_layer = inode;
        this.hidden_layer = hnode;
        this.output_layer = onode;
        this.learning_rate = lr;

        for (int i = 0; i < input_layer; i++) {
            for (int j = 0; i < hidden_layer; j++) {
                this.weights_input_hidden[i][j] = (int) (Math.random() - .5);
            }
        }

        for (int i = 0; i < hidden_layer; i++) {
            for (int j = 0; i < output_layer; j++) {
                this.weights_hidden_output[i][j] = (int) (Math.random() - .5);
            }
        }

    }

    public int getInput_layer() {
        return this.input_layer;
    }

    public int getHidden_layer() {
        return this.hidden_layer;
    }

    public int getOutput_layer() {
        return this.output_layer;
    }

    public double getLearning_rate() {
        return this.learning_rate;
    }

    public double[][] getWeights_input_hidden() {
        return this.weights_input_hidden;
    }

    public double[][] getWeights_hidden_output() {
        return this.weights_hidden_output;
    }


    public double[][] query(double[][] input_list) {

        double[][] hidden_input = myNumpy.matmul(this.weights_input_hidden, input_list);
        double[][] hidden_output = myNumpy.activate(hidden_input, this);

        double[][] final_input = myNumpy.matmul(this.weights_hidden_output, myNumpy.transpose(hidden_output));
        double[][] final_output = myNumpy.activate(final_input, this);

        for (int i=0; i < final_output.length; i++) {
            Log.d("final_output["+Integer.toString(i)+"][0]", Double.toString(final_output[i][0]));
        }

        return final_output;
    }

    public NeuralNetwork initializeFromAPI() {
        JSONObject json;
        HttpGETRequest getRequest = new HttpGETRequest();
        String myUrl = "https://rest-blur.herokuapp.com/model_digits";
        try {
            json = getRequest.execute(myUrl).get();
        } catch (Exception e) {
            json = null;
        }

        if (json != null) {

            String[] wih;
            String[] who;

            try {
                wih = json.get("model_wih").toString().split(",");
                who = json.get("model_who").toString().split(",");

                this.input_layer = Integer.parseInt(json.get("model_input_layer").toString());
                this.hidden_layer = Integer.parseInt(json.get("model_hidden_layer").toString());
                this.output_layer = Integer.parseInt(json.get("model_output_layer").toString());

                this.weights_input_hidden = new double[this.input_layer][this.hidden_layer];
                this.weights_hidden_output = new double[this.hidden_layer][this.output_layer];
                /*
                Log.d("wih length", Integer.toString(wih.length - 1));
                Log.d("who length", Integer.toString(who.length - 1));

                Log.d("input_layer", Integer.toString(this.input_layer));
                Log.d("hidden_layer", Integer.toString(this.hidden_layer));
                Log.d("output_layer", Integer.toString(this.output_layer));
                */

                int row = 0;
                int col = 0;

                for (int i = 0; i < wih.length - 1; i++) {
                    String c = wih[i].replace("{", "").replace("}", "").replace("\"", "");

                    try {
                        if (i % this.hidden_layer == 0) {
                            row += 1;
                        }

                        this.weights_input_hidden[row - 1][col] = Double.valueOf(c);

                        if (col == this.hidden_layer - 1) {
                            col = 0;
                        } else {
                            col += 1;
                        }

                    } catch(Exception e) {
                        Log.d("Error", e.toString()+" at wih["+Integer.toString(i / this.hidden_layer)+"]["+Integer.toString(i % this.input_layer)+"]");
                    }
                }

                row = 0;
                col = 0;

                for (int i = 0; i < who.length - 1; i++) {
                    String c = who[i].replace("{", "").replace("}", "").replace("\"", "");
                    try {
                        if (i % this.output_layer == 0) {
                            row += 1;
                        }

                        this.weights_hidden_output[row - 1][col] = Double.valueOf(c);

                        if (col == this.output_layer - 1) {
                            col = 0;
                        } else {
                            col += 1;
                        }
                    } catch(Exception e) {
                        Log.d("Error", e.toString()+" at who["+Integer.toString(i / this.output_layer)+"]["+Integer.toString(i % this.hidden_layer)+"]");
                    }
                }

            } catch (Exception e) {
                Log.d("Error", e.toString());
                return this;
            }
        }

        return this;
    }
}
