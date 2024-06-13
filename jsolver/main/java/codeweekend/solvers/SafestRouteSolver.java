package codeweekend.solvers;

import java.util.List;

import codeweekend.model.GameNew;
import codeweekend.model.HeroNew;
import codeweekend.model.Turn;
import codeweekend.scoring.Scoring;

public class SafestRouteSolver extends SolverNew {
    private static final int NEGLIGIBLE_FATIQUE = 0;
    private static final int MAX_ALLOWED_FATIQUE = 1500;
    
    @Override
    public List<Turn> solve(HeroNew hero, GameNew rules, Scoring scoring) {
        rules.loadProfit(hero.getR(), hero.getP());

        int prevTurns = rules.getTurnsLeft();

        //boolean rand = false;
        if (rules.getTestNum() == 36 || rules.getTestNum() == 37) hero.moveToPosition(110, 970, rules, true, true);

        while (rules.getTurnsLeft() > 0 && hero.getFatique() < (long) 1e9) {
            //System.out.println(rules.getTurnsLeft()  + " " + hero.getFatique() + " " + rules.monsterAlive.size());
            //if (hero.getFatique() > (long)1e9) break;
            //if (monsterAlive.size() <= 0) break;

            int[] pos = selectSafestTarget(rules, hero);
            /*if (rand) {
                rand = false;
                pos = selectSafestTarget(rules, hero);
            } else {
                pos = selectRandomSafestTarget(rules, hero, 7000, 4);
            } */

            rules.setVisited(pos[0], pos[1]);
            //PriorityQueue<Integer> targets = catchMonsters(rules, hero.getR(), pos[0], pos[1]);
            /*if (targets.size() == 0) {
                pos = selectRandomSafestTarget(rules, hero, 7000, 4);
            }*/
            rules.setVisited(pos[0], pos[1]);
            hero.moveToPosition(pos[0], pos[1], rules, true, true);
 
            clearAtPosition(catchMonsters(rules, hero), rules, hero, true);

            /*if (prevTurns == rules.getTurnsLeft()) {
                System.out.println("Hero Stuck!");
                rand = true;
                //return;
            } */

            rules.loadProfit(hero.getR(), hero.getP());
            prevTurns = rules.getTurnsLeft();
        }

        scoring.updateScore(rules.getTestNum(), hero.getGold()); 
        System.out.printf("Task %d result: %d\n", rules.getTestNum(), hero.getFatique());

        return hero.getTurns();
    }

    public static int[] selectSafestTarget(GameNew rules, HeroNew hero) {

        long maxProfit = -1;
        int[] maxProfitPos = new int[2];
        long minDanger = MAX_ALLOWED_FATIQUE;
        int[] safestPos = new int[2];

        for (int i = 0; i <= rules.getWidth(); ++i) {
            for (int j = 0; j <= rules.getHeight(); ++j) {
                if (!rules.isVisited(i, j)) {
                    long projectedFatique = rules.getDangerFromClearing(i, j) + hero.movementFatique(i, j, rules);
                    long profit = calculateProfitInPosition(i, j, rules, hero);
                    if (projectedFatique <= NEGLIGIBLE_FATIQUE && profit > maxProfit) {
                        maxProfit = profit;
                        maxProfitPos[0] = i;
                        maxProfitPos[1] = j;
                    } else if (projectedFatique < minDanger && profit > 0) {
                        minDanger = projectedFatique;
                        safestPos[0] = i;
                        safestPos[1] = j;
                    }
                }
            }
        }

        if (maxProfit > 0) {
            return maxProfitPos;
        } else return safestPos;
    }

    public static int[] selectRandomSafestTarget(GameNew rules, HeroNew hero, int n, int maxSteps) {
        int[][] targets = rules.selectRandomPositions(n);

        long maxProfit = -1;
        int[] maxProfitPos = new int[2];

        long minDanger = MAX_ALLOWED_FATIQUE;
        int[] safestPos = new int[2];

        for (int i = 0; i < n; ++i) {
            int x = targets[i][0];
            int y = targets[i][1];
            if (!rules.isVisited(x, y)) {
                long val = 0;
                double range = rules.calcRange(x, y, hero.getX(), hero.getY());
                if (range / hero.getS() <= maxSteps) {
                    val = calculateProfitInPosition(x, y, rules, hero);
                    long fatique = rules.getDangerFromClearing(x, y) + hero.movementFatique(x, y, rules);
                    if (fatique <= NEGLIGIBLE_FATIQUE && val > maxProfit) {
                        maxProfit = val;
                        maxProfitPos[0] = x;
                        maxProfitPos[1] = y;
                    } else {
                        if (fatique < minDanger && val > 0) {
                            minDanger = fatique;
                            safestPos[0] = x;
                            safestPos[1] = y;
                        }
                    }
    
                }            
            }
        }

        if (maxProfit == -1 && minDanger >= 10000) {
            return null;
        }
        
        if (maxProfit > 0) {
            return maxProfitPos;
        } else return safestPos;
    }
}
