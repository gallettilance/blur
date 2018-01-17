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


    public void train(double[][] input_list, double[][] target_list) {

        double[][] hidden_inputs = myNumpy.matmul(this.weights_input_hidden, input_list);
        double[][] hidden_outputs = myNumpy.activate(hidden_inputs, this);

        double[][] final_inputs = myNumpy.matmul(this.weights_hidden_output, hidden_outputs);
        double[][] final_outputs = myNumpy.activate(final_inputs, this);

        double[][] output_error = new double[target_list.length][target_list[0].length];

        for (int i=0; i < output_error.length; i++) {
            for (int j=0; j < output_error[i].length; j++) {
                output_error[i][j] = target_list[i][j] - input_list[i][j];
            }
        }

        double[][] hidden_error = myNumpy.matmul(myNumpy.transpose(this.weights_hidden_output), output_error);

        double[][] output_temp = new double[output_error.length][output_error[0].length];
        double[][] hidden_temp = new double[hidden_error.length][hidden_error[0].length];

        for (int i=0; i < output_temp.length; i++) {
            for (int j=0; j < output_temp[i].length; j++) {
                output_temp[i][j] = output_error[i][j] * final_outputs[i][j] * (1.0 - final_outputs[i][j]);
            }
        }

        double[][] diff_who = myNumpy.matmul(output_temp, myNumpy.transpose(hidden_outputs));

        for (int i=0; i < this.weights_hidden_output.length; i++) {
            for (int j = 0; j < this.weights_hidden_output[i].length; j++) {
                this.weights_hidden_output[i][j] += this.learning_rate * diff_who[i][j];
            }
        }

        for (int i=0; i < hidden_temp.length; i++) {
            for (int j=0; j < hidden_temp[i].length; j++) {
                hidden_temp[i][j] = hidden_error[i][j] * hidden_outputs[i][j] * (1.0 - hidden_outputs[i][j]);
            }
        }

        double[][] diff_wih = myNumpy.matmul(hidden_temp, myNumpy.transpose(input_list));

        for (int i=0; i < this.weights_input_hidden.length; i++) {
            for (int j = 0; j < this.weights_input_hidden[i].length; j++) {
                this.weights_input_hidden[i][j] += this.learning_rate * diff_wih[i][j];
            }
        }
    }


    public NeuralNetwork initializeFromAPI(String type) {

        JSONObject json;
        HttpGETRequest getRequest = new HttpGETRequest();
        String myUrl;

        if (type.equals("digit")) {
            myUrl = "https://rest-blur.herokuapp.com/model_digits";
        } else {
            if (type.equals("letter")) {
                myUrl = "https://rest-blur.herokuapp.com/model_letters";
            } else {
                myUrl = "https://rest-blur.herokuapp.com/model_words";
            }
        }

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
