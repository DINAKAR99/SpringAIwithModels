package com.dinakar.SpringAIDemo;

import java.util.Random;

public class EmbeddingModel {

    private int dim = 512; // embedding dimension
    private Random random = new Random();

    // fake embedding for demo
    public float[] embed(String text) {
        float[] vector = new float[dim];
        for (int i = 0; i < dim; i++) {
            vector[i] = random.nextFloat();
        }
        return vector;
    }
}
