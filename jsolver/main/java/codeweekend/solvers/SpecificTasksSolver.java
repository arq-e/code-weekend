package codeweekend.solvers;

import java.util.List;

import codeweekend.model.Game;
import codeweekend.model.Hero;
import codeweekend.model.Monster;
import codeweekend.model.Turn;
import codeweekend.scoring.Scoring;

public class SpecificTasksSolver extends Solver {

    @Override
    public List<Turn> solve(Hero hero, Game rules, Scoring scoring) {
        rules.loadProfit(hero.getR(), hero.getP());
        if (rules.getTestNum() == 36) {
            solveTask36(rules, hero);
        } else if (rules.getTestNum() == 50) {
            solveTask50(rules, hero);
        }

        scoring.updateScore(rules.getTestNum(), hero.getGold()); 
        System.out.printf("Task %d result: %d\n", rules.getTestNum(),  hero.getGold());
        return hero.getTurns();
    }

    public void solveTask36(Game rules, Hero hero) {
        int[][] targets = new int[15][2];

        targets[0][0] = 1;
        targets[0][1] = 950;
        targets[1][0] = 188;
        targets[1][1] = 950;
        targets[2][0] = 188;
        targets[2][1] = 50;
        targets[3][0] = 313;
        targets[3][1] = 50;
        targets[4][0] = 313;
        targets[4][1] = 950;
        targets[5][0] = 438;
        targets[5][1] = 950;
        targets[6][0] = 438;
        targets[6][1] = 50;
        targets[7][0] = 563;
        targets[7][1] = 50;
        targets[8][0] = 563;
        targets[8][1] = 950;
        targets[9][0] = 688;
        targets[9][1] = 950;
        targets[10][0] = 688;
        targets[10][1] = 50;
        targets[11][0] = 813;
        targets[11][1] = 50;
        targets[12][0] = 813;
        targets[12][1] = 950;
        targets[13][0] = 938;
        targets[13][1] = 950;
        targets[14][0] = 938;
        targets[14][1] = 50;

        for (int i = 0; i < targets.length; ++i) {
            int[] pos = targets[i];
            System.out.println(pos[0] + " " + pos[1]);
            moveToPositionAndClear(rules, hero, pos[0], pos[1]);
            if (rules.getTurnsLeft() <= 0) { System.out.println(i); break; }
        }

        while (rules.getTurnsLeft() > 0) {
            //System.out.println(turnsLeft);
            //if (rules.getMonsterAlive().size() <= 0) break;
            int[] pos = null;
            //pos = pickBestPosFromRange(hero.r);
            // int steps = 1;
            while (pos == null) {
                pos = getBestRandomPos(rules, hero, 100, 10);
            }
                
            moveToPositionAndClear(rules, hero, pos[0], pos[1]);
        }
    }

    

    public void solveTask50(Game rules, Hero hero) {

        int[][] targets = new int[300][2];
        int t = 0;
        for (int group = 0; group < 5; ++group) {
            int startY = group * 600;
            int startX = 0;

            if (group == 0) {
                for (int col = 1; col <= 7; ++col) {
                    int y = startY + col * 62;
                    int startXPos = 0;
                    int endXPos = 0;
                    if (col % 2 == 0) {
                        startXPos = 7;
                        endXPos = (col > 1) ? 2 : 3;
                    } else {
                        startXPos = (col > 1) ? 2 : 3;
                        endXPos = 7;
                    }
                    System.out.println(startXPos + " " + endXPos + " " + t);
                    if (startXPos < endXPos) {
                        for (int xPos = startXPos; xPos <= endXPos; ++xPos) {
                            int x = startX + xPos * 62;
                            targets[t][0] = y;
                            targets[t][1] = x;
                            ++t;
                        }
                    } else {
                        for (int xPos = startXPos; xPos >= endXPos; --xPos) {
                            int x = startX + xPos * 62;
                            targets[t][0] = y;
                            targets[t][1] = x;
                            ++t;
                        }
                    }
                }
            } else if (group == 1) {
                for (int col = 1; col <= 7; ++col) {
                    int y = startY + col * 62;
                    int startXPos = 0;
                    int endXPos = 0;
                    if (col % 2 != 0) {
                        startXPos = 7;
                        endXPos = (col > 6) ? 2 : 3;
                    } else {
                        startXPos = (col > 6) ? 2 : 3;
                        endXPos = 7;
                    }
                    System.out.println(startXPos + " " + endXPos + " " + t);
                    if (startXPos < endXPos) {
                        for (int xPos = startXPos; xPos <= endXPos; ++xPos) {
                            int x = startX + xPos * 62;
                            targets[t][0] = y;
                            targets[t][1] = x;
                            ++t;
                        }
                    } else {
                        for (int xPos = startXPos; xPos >= endXPos; --xPos) {
                            int x = startX + xPos * 62;
                            targets[t][0] = y;
                            targets[t][1] = x;
                            ++t;
                        }
                    }
                }
            } else if (group == 2) {
                for (int col = 1; col <= 7; ++col) {
                    int y = startY + col * 62;
                    int startXPos = 0;
                    int endXPos = 0;
                    if (col % 2 == 0) {
                        startXPos = 7;
                        endXPos = (col >= 6) ? 1 : 2;
                    } else {
                        startXPos = (col > 6) ? 1 : 2;
                        endXPos = 7;
                    }
                    System.out.println(startXPos + " " + endXPos + " " + t);
                    if (startXPos < endXPos) {
                        for (int xPos = startXPos; xPos <= endXPos; ++xPos) {
                            int x = startX + xPos * 62;
                            targets[t][0] = y;
                            targets[t][1] = x;
                            ++t;
                        }
                    } else {
                        for (int xPos = startXPos; xPos >= endXPos; --xPos) {
                            int x = startX + xPos * 62;
                            targets[t][0] = y;
                            targets[t][1] = x;
                            ++t;
                        }
                    }
                }
            } else {
                for (int col = 1; col <= 7; ++col) {
                    int y = startY + col * 62;
                    int startXPos = 0;
                    int endXPos = 0;
                    if (col % 2 == 0) {
                        startXPos = 1;
                        endXPos = (col > 6) ? 7 : 7;
                    } else {
                        startXPos = (col > 6) ? 7 : 7;
                        endXPos = 1;
                    }
                    System.out.println(startXPos + " " + endXPos + " " + t);
                    if (startXPos < endXPos) {
                        for (int xPos = startXPos; xPos <= endXPos; ++xPos) {
                            int x = startX + xPos * 62;
                            targets[t][0] = y;
                            targets[t][1] = x;
                            ++t;
                        }
                    } else {
                        for (int xPos = startXPos; xPos >= endXPos; --xPos) {
                            int x = startX + xPos * 62;
                            targets[t][0] = y;
                            targets[t][1] = x;
                            ++t;
                        }
                    }
                }
            }
            System.out.println(t);
        }
        System.out.println(t);

        List<Monster> monsters = rules.getMonsters();

        for (int i = 0; i < targets.length; ++i) {
            int[] pos = targets[i];
            // System.out.println("")
            Monster target = null;
            for (Monster m : rules.getMonsters()) {
                if (m.getX() == pos[0] && m.getY() == pos[1]) {
                    target = m;
                    break;
                }
            }
            if (target == null) {
                System.out.println("no target found at" + pos[0] + " " + pos[1]);
                continue;
            }
            hero.moveToAimAtPosition(rules,targets[i][0], targets[i][1], target.getRange());
            hero.destroy(target, rules,true);
            // clearAtPosition(catchMonsters(), pos, false);
            if (rules.getTurnsLeft() <= 0) { System.out.println(i); break; }
        }

        while (rules.getTurnsLeft() > 0) {
            //System.out.println(turnsLeft);
            if (rules.getMonsterAlive().size() <= 0) break;
            int[] pos = null;
            //pos = pickBestPosFromRange(hero.r);
            // int steps = 1;
            while (pos == null) {
                pos = getBestRandomPos(rules, hero, 100000, 10);
            }
                
            hero.moveToPosition(pos[0], pos[1], rules, true, true);
                //if (heatMap[pos[1]][pos[2]] != pos[0])
                //    continue;

            if (rules.getTurnsLeft() <= 0) break;
            clearAtPosition(catchMonsters(rules, hero), rules, hero, false);
        }
    }
}
