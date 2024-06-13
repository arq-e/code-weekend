package codeweekend.scoring;

import java.util.Arrays;

import codeweekend.io.IOUtils;

public class Scoring {

    private int size;
    private int[] prevBestScores;
    private int[] bestScores;

    public Scoring() {
        prevBestScores = IOUtils.loadBestScores();
        size = prevBestScores.length;
        bestScores = Arrays.copyOf(prevBestScores, size);
    }

    public void writeBestScores() {
        IOUtils.writeBestScores(bestScores);
    }

    public void showScoreUpdates() {
        for (int i = 1; i < size; ++i) {
            if (bestScores[i] > prevBestScores[i]) {
                System.out.println("Task " + i + " result was improved:" + prevBestScores[i] + " to " + bestScores[i]);
            }
        }
    }

    public boolean gotImproved(int task) {
        return bestScores[task] > prevBestScores[task];
    }

    public boolean updateScore(int task, int score) {
        if (score > bestScores[task]) {
            bestScores[task] = score;
            return true;
        }

        return false;
    }

    public int getBestScore(int task) {
        return bestScores[task];
    }
}
