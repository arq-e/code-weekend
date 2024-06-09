package jweekend;

import java.util.PriorityQueue;

public class Score {
    public int totalScore;
    public int[] monsters;
    public int pos;

    public Score(int score, int[] monsters) {
        this.totalScore = score;
        this.monsters = monsters;
        pos = 0;
    }

}
