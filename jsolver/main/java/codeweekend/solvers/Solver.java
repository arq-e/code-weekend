package codeweekend.solvers;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import codeweekend.model.Game;
import codeweekend.model.Hero;
import codeweekend.model.Monster;
import codeweekend.model.Turn;
import codeweekend.scoring.Scoring;

public abstract class Solver {
    private static final int MAX_HP_TO_ATTACK = 100000;

    public abstract List<Turn> solve(Hero hero, Game rules, Scoring scoring);

    public static long calculateProfitInPosition(int x, int y, Game rules, Hero hero) {
        double expCoeff = 1.0;
        
        //double turnsRemainingFrac = rules.getTurnsLeft() * 1.0 / rules.getMaxTurns();
        /*if (turnsRemainingFrac >= 0.7) {
            expCoeff = 10.0;
        } else if (turnsRemainingFrac <= 0.3) {
            expCoeff = 0.1;
        } else if (turnsRemainingFrac <= 0.1) {
            expCoeff = 0;
        } */

        int stepCount = (int) (rules.calcRange(x, y, hero.getX(), hero.getY()) / (hero.getS()));
        if (stepCount == 0) stepCount = 1;
        int[] baseProfits = rules.getProfit(x, y);

        long profit = (long) (baseProfits[0] + baseProfits[1] * expCoeff) / (2 + stepCount);


        return profit;
    }

    public static long calculateProfitOfMonster(Monster m, Game rules, Hero hero) {
        double expCoeff = 1.0;
        
        //double turnsRemainingFrac = rules.getTurnsLeft() * 1.0 / rules.getMaxTurns();
        /*if (turnsRemainingFrac >= 0.7) {
            expCoeff = 10.0;
        } else if (turnsRemainingFrac <= 0.3) {
            expCoeff = 0.1;
        } else if (turnsRemainingFrac <= 0.1) {
            expCoeff = 0;
        } */

        int stepCount = (int) (rules.calcRange(m.getX(), m.getY(), hero.getX(), hero.getY()) / (hero.getS())) + 1;
        long profit = (long) (m.getGold() + m.getHp() * expCoeff) / stepCount;

        return profit;
    }

    public static PriorityQueue<Integer> catchMonsters(Game rules, int range, int x, int y) {
        List<Monster> monsters = rules.getMonsters();
        PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> (Monster.compare(monsters.get(a), monsters.get(b))));

        for (int name : rules.getMonsterAlive()) {
            Monster m = monsters.get(name);
            double dist = rules.calcRange(x, y, m.getX(), m.getY());
            if (dist <= range && m.getHp() < MAX_HP_TO_ATTACK) {
                pq.offer(name);
            }
        }

        return pq;
    }

    public static PriorityQueue<Integer> catchMonsters(Game rules, Hero hero) {
        return catchMonsters(rules, hero.getR(), hero.getX(), hero.getY());
    }

    public static void clearAtPosition(PriorityQueue<Integer> pq, Game rules, Hero hero, boolean updateProfits) {
        int level = hero.getLevel();
        while (pq.size() > 0) {
            int target = pq.poll();
            Monster m = rules.getMonsters().get(target);
            if (m.getHp() * 1.0 / hero.getP() > rules.getTurnsLeft() && pq.size() > 0)
                continue;
            hero.destroy(m, rules, true);
        }
        if (level != hero.getLevel()) {
            clearAtPosition(catchMonsters(rules, hero), rules, hero, updateProfits);
        }
    }

    /* ---------------------------------------------------------------------------------------------------- */

    public Boolean moveToPositionAndClear(Game rules, Hero hero, int x, int y) {
        hero.moveToPosition(x, y, rules, true, true);
        clearAtPositionWithLimit(catchMonsters(rules, hero), rules, hero, false, 1000);

        return true;
    }

    public int clearAtPositionWithLimit(PriorityQueue<Integer> pq, Game rules, Hero hero, boolean updateProfits, int hpLimit) {
        List<Monster> monsters = rules.getMonsters();

        int res = 0;
        while (pq.size() > 0) {
            int target = pq.poll();
            Monster m = monsters.get(target);
            int hp = m.getHp();
            if (hp > hpLimit) {
                continue;
            }
            if (m.getHp() * 1.0 / hero.getP() > rules.getTurnsLeft())
                continue;
            hero.destroy(m, rules, true);
        }
        return res;

    }

    /* ------------------------------------------------------------------- Greedy/Random Target Selections --------------------------------------------------------------- */
        public static Monster SelectMostProfitableMonster(Game rules, Hero hero) {
        List<Monster> monsters = rules.getMonsters();

        Monster closest = rules.getMonsters().get(0);
        double max = 0;
        for (int name : rules.getMonsterAlive()) {
            double rProfit = calculateProfitOfMonster(monsters.get(name), rules, hero);

            if (rProfit > max) {
                max = rProfit;
                closest = monsters.get(name);
            }
        }   

        return closest;     
    }

    public static Monster selectClosestMonster(Game rules, Hero hero) {
        List<Monster> monsters = rules.getMonsters();

        Monster closest = monsters.get(0);
        double min = Integer.MAX_VALUE;
        for (int name : rules.getMonsterAlive()) {
            Monster m = monsters.get(name);
            double dist = rules.calcRange(hero.getX(), hero.getY(), m.getX(), m.getY());
            
            if ((dist / hero.getR()) < min) {
                min = dist / hero.getR();
                closest = m; 
            }
        }
        return closest;

    }

    public Monster selectFromMostProfitableMonsters(Game rules, Hero hero, int n, double rand) {
        List<Monster> monsters = rules.getMonsters();

        PriorityQueue<long[]> pq = new PriorityQueue<>((a,b) -> (int) (a[1] - b[1]));
        for (int name : rules.getMonsterAlive()) {
            long[] val = new long[2];
            val[0] = name;
            val[1] = calculateProfitOfMonster(monsters.get(name), rules, hero);
            pq.offer(val);
            if (pq.size() > n) pq.poll();
        }
        long res = pq.poll()[0];
        while (pq.size() > 0 ) {
            if (Math.random() < rand) {
                res = pq.poll()[0];
            } else 
                pq.poll();
        }
        return monsters.get((int) res);
    }

    public static Monster selectRandomFromClosestMonsters(Game rules, Hero hero) {
        List<Monster> monsters = rules.getMonsters();

        List<Integer> closests = new ArrayList<>();
        double min = Integer.MAX_VALUE;
        for (int name : rules.getMonsterAlive()) {
            Monster m = monsters.get(name);
            double dist = rules.calcRange(hero.getX(), hero.getY(), m.getX(), m.getY());
            double steps = (dist + hero.getR()) / hero.getR();
            if (steps < min) {
                closests.clear();
                closests.add(name);
                min = steps;
            } else if (steps == min) {
                closests.add(name);
            }
        }

        int target = closests.get((int)(Math.random()*closests.size()));
        return monsters.get(target);
    }

    public int[] getBestRandomPos(Game rules, Hero hero, int n, int maxSteps) {
        int[][] targets = rules.selectRandomPositions(n);

        long max = -1;
        int[] bestPos = new int[2];

        for (int i = 0; i < n; ++i) {
            int x = targets[i][0];
            int y = targets[i][1];

            long val = 0;
            double range = Math.sqrt(Math.pow(hero.getX() - x, 2) + Math.pow(hero.getY() - y, 2));
            int turns = (int)(range + hero.getS()) / hero.getS();
            if (turns <= maxSteps) {
                val = calculateProfitInPosition(x, y, rules, hero);

                if (val > max) {
                    max = val / turns;
                    bestPos = targets[i];
                }
            }

        }

        if (max == -1) return null;
        return bestPos;
    }
}
