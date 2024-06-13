package codeweekend.solvers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import codeweekend.io.IOUtils;
import codeweekend.model.Game;
import codeweekend.model.Hero;
import codeweekend.model.Monster;
import codeweekend.model.Turn;
import codeweekend.scoring.Scoring;

public class AnnealingSolver extends Solver{

    @Override
    public List<Turn> solve(Hero hero, Game rules, Scoring scoring) {

        StringBuilder sb = new StringBuilder();
        int idx[] = IOUtils.readAnnealingPath(rules.getTestNum());
        if (idx == null) idx = get_indices(rules.getMonsters());
        List<Turn> solution = new ArrayList<>();
        int path[] = Arrays.copyOf(idx, idx.length);
        int prev_score[] = calc_score(solution, idx, hero.clone(), rules.clone(), scoring, path);
        System.out.printf("SA starting score: %d\n", prev_score[0]);

        double MAX_MSEC = 1000.0;
        double tempStart = prev_score[0];
        double tempEnd = 0.1;
        long startTime = Instant.now().toEpochMilli();
        long elapsedTime = 0L;
        long steps = 0;
        
        while (elapsedTime < MAX_MSEC) {
            elapsedTime = Instant.now().toEpochMilli() - startTime;
            double frac = elapsedTime / MAX_MSEC;
            double temp = tempStart * Math.pow(tempEnd / tempStart, frac);

            int l = (int)(Math.random() * idx.length);
            int r = (int)(Math.random() * idx.length);
            if (l == r) {
                continue;
            }
            if (l > r) {
                int tmp = l;
                l = r;
                r = tmp;
            }
            if (l > prev_score[1]) { // no improvement
                continue;
            } 

            int[] idx1;
            if (Math.random() > 0.5) {
                idx1 = partial_reverse(idx, l, r);
            } else {
                idx1 = swap(idx, l, r);
            }
            
            int score[] = calc_score(solution, idx1, hero.clone(), rules.clone(), scoring, path);
            if (score[0] >= prev_score[0] || Math.random() > Math.exp((prev_score[0] - score[0]) / temp)) {
                prev_score = score;
                idx = idx1;
            }

            ++steps;
            if (steps % 3000000 == 0) {
                System.out.println(rules.getTestNum() + " " + scoring.getBestScore(rules.getTestNum()));
            } 

        }

        sb.append(prev_score[0] + " ");
        System.out.println("Iterations: " + steps);
        System.out.printf("Task %d result: %d\n", rules.getTestNum(), scoring.getBestScore(rules.getTestNum()));
        //IOUtils.writeAnnealingScores(rules.getTestNum(), sb);
        IOUtils.writeAnnealingPath(rules.getTestNum(), path);

        return solution;
    }


    public static int[] get_indices(List<Monster> monsters) {
        int idx[] = new int[monsters.size()];
        for (int i = 0; i < monsters.size(); ++i) {
            idx[i] = i;
        }
        return idx;
    }

    public static int[] swap(int[] idx, int start, int end) {
        int res[] = new int[idx.length];
        for (int i = 0; i < idx.length; ++i) {
            res[i] = idx[i];
        }
        res[start] = idx[end];
        res[end] = idx[start];
        return res;
    }

    public static int[] partial_reverse(int[] idx, int start, int end) {
        int res[] = new int[idx.length];
        for (int i = 0; i < idx.length; ++i) {
            res[i] = idx[i];
        }
        for (int i = 0; i <= (end - start); ++i) {
            res[start + i] = idx[end - i];
        }
        return res;
    }

    public int[] calc_score(List<Turn> solution, int[] idx, Hero hero, Game rules, Scoring scoring, int[] path) {

        int maxBeaten = solveByGivenOrder(idx, hero, rules);
        int res = hero.getGold();
        if (scoring.updateScore(rules.getTestNum(), res)) {
            solution.clear();
            
            solution.addAll(hero.getTurns());
            for (int i = 0; i < idx.length; ++i) {
                path[i] = idx[i];
            }
        }

        return new int[]{res, maxBeaten};
    }

    public int solveByGivenOrder(int[] idx, Hero hero, Game rules) {
        List<Monster> monsters = rules.getMonsters();
        int i = 0;
        
        while (rules.getTurnsLeft() > 0 && i < idx.length) {
            Monster target = monsters.get(idx[i]);
            hero.moveToMonster(target, rules, false);
            hero.destroy(target, rules, false);
            ++i;
        }

        return i-1; //maxBeaten
    }
}
