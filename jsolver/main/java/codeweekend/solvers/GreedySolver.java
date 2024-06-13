package codeweekend.solvers;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import codeweekend.model.GameNew;
import codeweekend.model.HeroNew;
import codeweekend.model.MonsterNew;
import codeweekend.model.Turn;
import codeweekend.scoring.Scoring;

public class GreedySolver extends SolverNew {
    private boolean destroyAllInRange = false;
    private int targetingMethod = 1;

    public GreedySolver(boolean destroyAllInRange, int targetingMethod) {
        this.destroyAllInRange = destroyAllInRange;
        this.targetingMethod = targetingMethod;
    }

    @Override
    public List<Turn> solve(HeroNew hero, GameNew rules, Scoring scoring) {

        while (rules.getTurnsLeft() > 0) {
            MonsterNew target = null;
            switch (targetingMethod) {
                case 1:
                    target = selectClosestMonster(rules, hero);
                    break;
                case 2:
                    target = SelectMostProfitableMonster(rules, hero);
                    break;
                case 3:
                    target = selectFromMostProfitableMonsters(rules, hero, 3, 0.15);
                    break;
                case 4:
                    target = selectRandomFromClosestMonsters(rules, hero);
                    break;
                default:
                    //target = selectRandomMonster(rules, hero, 5000, 4);
                    break;
            }
            
            if (target != null) {
                hero.moveToMonster(target, rules, true);
                int level = hero.getLevel();
                if (destroyAllInRange) {
                    clearAtPosition(catchMonsters(rules, hero), rules, hero,true);
                    if (hero.getLevel() != level) {
                        clearAtPosition(catchMonsters(rules, hero), rules, hero, false);
                    }
                } else {
                    hero.destroy(target, rules, true);
                }
            }
        }

        scoring.updateScore(rules.getTestNum(), hero.getGold()); 
        System.out.printf("Task %d result: %d\n", rules.getTestNum(),  hero.getGold());
        return hero.getTurns();
    }
    
    

}
