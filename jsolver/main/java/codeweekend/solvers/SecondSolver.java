package codeweekend.solvers;

import java.util.List;
import java.util.PriorityQueue;

import codeweekend.model.GameNew;
import codeweekend.model.HeroNew;
import codeweekend.model.Turn;
import codeweekend.scoring.Scoring;

public class SecondSolver extends SolverNew{

    @Override
    public List<Turn> solve(HeroNew hero, GameNew rules, Scoring scoring) {
        rules.loadProfit(hero.getR(), hero.getP());

        hero.moveToPosition(950, 100, rules, true, true);
        while (rules.getTurnsLeft() > 0) {
            //System.out.println(rules.getTurnsLeft());
            

            if (rules.getMonsterAlive().size() <= 0) break;

            int[] pos = getBestPosInRange(hero, rules, hero.getS(), true);
            hero.moveToPosition(pos[0], pos[1], rules, true, true);
            PriorityQueue<Integer> pq = catchMonsters(rules, hero);

            int turns = rules.getTurnsLeft() + 1;
            // System.out.println(hero.x + " " + hero.y + " " + pos[0] + " " + pos[1]);
            while (pos[0] == hero.getX() && pos[1] == hero.getY() && rules.getTurnsLeft() < turns) {
                if (pq.isEmpty()) {
                    pos = getBestPosInRange(hero, rules, hero.getS(), true);
                    break;
                }
                clearAtPosition(pq, rules, hero, false);

                pq = catchMonsters(rules, hero);
                rules.loadProfit(hero.getR(), hero.getP());
                pos = getBestPosInRange(hero, rules, hero.getS(), true);
                turns = rules.getTurnsLeft();
            }
            if (hero.getFatique()  > (1e6)) break;

        }

        scoring.updateScore(rules.getTestNum(), hero.getGold()); 
        System.out.printf("Task %d result: %d\n", rules.getTestNum(),  hero.getGold());

        return hero.getTurns();
    }

    public int[] getBestPosInRange(HeroNew hero, GameNew rules, int range, Boolean allow_self) {
        int x = hero.getX(); int y = hero.getY();

        double max = -1;
        int maxX = 0;
        int maxY = 0;
        for (int i = Math.max(0, x - range); i <= Math.min(rules.width, x + range); ++i) {
            for (int j = Math.max(0, y - range); j <= Math.min(rules.height, y + range); ++j) {
                if (!rules.canReach(range, x, y, i, j)) {
                    continue;
                }
                double val = calculateProfitInPosition(i, j, rules, hero);
                if (val > max)  {
                    if (allow_self || i != x || j != y) {
                        max = val;
                        maxX = i;
                        maxY = j;
                    }
                }
                }
            }
        if (max < 1E-6 && range <= Math.max(rules.width, rules.height)) {
            return getBestPosInRange(hero, rules, range * 2, false);
        }

        return new int[]{maxX, maxY};
    }
    
}
