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

        double[][] final_input = myNumpy.matmul(this.weights_hidden_output, hidden_output);
        double[][] final_output = myNumpy.activate(final_input, this);

        return final_output;
    }

    public NeuralNetwork initializeFromAPI() {
        JSONObject json;
        HttpGETRequest getRequest = new HttpGETRequest();
        String myUrl = "https://rest-blur.herokuapp.com/model/digits";
        try {
            json = getRequest.execute(myUrl).get();
        } catch (Exception e) {
            json = null;
        }

        if (json != null) {

            String wih;
            String who;
            double[][] model_wih;
            double[][] model_who;

            try {
                wih = json.get("model_wih").toString();
                who = json.get("model_who").toString();

                this.input_layer = Integer.parseInt(json.get("model_input_layer").toString());
                this.hidden_layer = Integer.parseInt(json.get("model_hidden_layer").toString());
                this.output_layer = Integer.parseInt(json.get("model_output_layer").toString());

                model_wih = new double[this.input_layer][this.hidden_layer];
                model_who = new double[this.hidden_layer][this.output_layer];

                for (int i = 0; i < wih.length(); i++) {
                    char c = wih.charAt(i);
                    if (c != ',') {
                        model_wih[i / this.hidden_layer][i % this.input_layer] = Character.getNumericValue(c);
                    }
                }

                for (int i = 0; i < who.length(); i++) {
                    char c = who.charAt(i);
                    if (c != ',') {
                        model_who[i / this.output_layer][i % this.hidden_layer] = Character.getNumericValue(c);
                    }
                }

                this.weights_input_hidden = model_wih;
                this.weights_hidden_output = model_who;

            } catch (Exception e) {
                Log.d("Error", e.toString());
                return this;
            }
        }

        return this;
    }
}
