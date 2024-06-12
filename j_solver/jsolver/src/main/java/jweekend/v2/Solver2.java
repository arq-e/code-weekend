package jweekend.v2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import jweekend.Monster;
import jweekend.Position;
import jweekend.Turn;

public class Solver2 {
    Hero2 hero;
    List<Monster2> monsters;
    Set<Integer> monsterAlive;
    List<Turn> turns;
    int width;
    int height;
    int totalTurns;
    int turnsLeft;

    long[][] dangerMap;
    int[][][] valueMap;
    int[][] monstersMap;
    int maxGold;
    //int monstersCount;

    public Solver2(Hero2 hero, List<Monster2> monsters, int width, int height, int num_turns) {
        this.hero = hero;
        this.monsters = monsters;
        monsterAlive = new HashSet<>();
        for (int i = 0; i < monsters.size(); ++i) {
            monsters.get(i).name = i;
            monsterAlive.add(i);
        }
        
        this.width = width;
        this.height = height;
        this.turns = new ArrayList<>();
        totalTurns = num_turns;
        turnsLeft = num_turns;
        maxGold = 0;
    }


    public int[] getBestRandomPos(int n, int maxSteps) {
        boolean[][] used = new boolean[width+1][height+1];
        used[0][0] = true;
        if (n > height * width / 10) n = height * width /10;
        int[][] targets = new int[n][2];
        for (int i = 0; i < n; ++i) {
           
            int x = 0;
            int y = 0;
            while (used[x][y]) {
                x = (int) (Math.random()*width);
                y = (int) (Math.random()*height);
            }

            used[x][y] = true;
            targets[i][0] = x;
            targets[i][1] = y;
        }
        int max = -1;
        int maxX = 0;
        int maxY = 0;
        for (int i = 0; i < n; ++i) {
            int x = targets[i][0];
            int y = targets[i][1];
            int val = 0;
            double range = Math.sqrt(Math.pow(hero.x - x, 2) + Math.pow(hero.y - y, 2));
            int turns = (int)(range + hero.s) / hero.s;
            if (range / hero.s <= maxSteps) {
                for (int k = Math.max(0, targets[i][0] - hero.r); k <= Math.min(width, targets[i][0] + hero.r); ++k) {
                    for (int l = Math.max(0, targets[i][1] - hero.r); l <= Math.min(height, targets[i][1] + hero.r); ++l) {
                        if (monstersMap[k][l] > - 1) {
                            val += hero.calculateProfit(monsters.get(monstersMap[k][l]), turnsLeft, totalTurns, targets[i][0], targets[i][1], dangerMap);
                        }
                    }
                }
                if (val > max) {
                    max = val / turns;
                    maxX = targets[i][0];
                    maxY = targets[i][1];
                }
            }

        }
        if (max == -1) return null;


        return new int[]{maxX, maxY};
    }

    public void solveSeekingRandomPos() {
        loadDanger();
        setMonsters();
        //int level = hero.level;
        while (turnsLeft > 0) {
            //System.out.println(turnsLeft);
            if (monsterAlive.size() <= 0) break;
            int[] pos = null;
            //pos = pickBestPosFromRange(hero.r);
            // int steps = 1;
            while (pos == null) {
                pos = getBestRandomPos(100000, 100);
            }
                
            moveToPosition(pos[0], pos[1]);
                //if (heatMap[pos[1]][pos[2]] != pos[0])
                //    continue;

            if (turnsLeft <= 0) break;
            clearAtPosition(catchMonsters(), pos, false);
        }
    }



    public void solveTask36() {
        loadDanger();
        setMonsters();
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

        // for (int i = 0; i < targets.length; ++i) {
        //     targets[i][0] += 25;
        // }

        for (int i = 0; i < targets.length; ++i) {
            int[] pos = targets[i];
            System.out.println(pos[0] + " " + pos[1]);
            moveToPositionAndClear(pos[0], pos[1]);
            clearAtPositionWithLimit(catchMonsters(), pos, false, 1000);
            if (turnsLeft <= 0) { System.out.println(i); break; }
        }

        while (turnsLeft > 0) {
            //System.out.println(turnsLeft);
            if (monsterAlive.size() <= 0) break;
            int[] pos = null;
            //pos = pickBestPosFromRange(hero.r);
            // int steps = 1;
            while (pos == null) {
                pos = getBestRandomPos(10, 10);
            }
                
            moveToPosition(pos[0], pos[1]);
                //if (heatMap[pos[1]][pos[2]] != pos[0])
                //    continue;

            if (turnsLeft <= 0) break;
            clearAtPositionWithLimit(catchMonsters(), pos, false, 1000);
        }
    }

    

    public void solveTask50() {
        loadDanger();
        setMonsters();
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

        for (int i = 0; i < targets.length; ++i) {
            int[] pos = targets[i];
            // System.out.println("")
            Monster2 target = null;
            for (Monster2 m : monsters) {
                if (m.x == pos[0] && m.y == pos[1]) {
                    target = m;
                    break;
                }
            }
            if (target == null) {
                System.out.println("no target found at" + pos[0] + " " + pos[1]);
                continue;
            }
            moveToAimAtPosition(targets[i][0], targets[i][1], target.range);
            turnsLeft = hero.destroyMonster(target, turns, turnsLeft, monsterAlive, monsters, dangerMap);
            // clearAtPosition(catchMonsters(), pos, false);
            if (turnsLeft <= 0) { System.out.println(i); break; }
        }

        while (turnsLeft > 0) {
            //System.out.println(turnsLeft);
            if (monsterAlive.size() <= 0) break;
            int[] pos = null;
            //pos = pickBestPosFromRange(hero.r);
            // int steps = 1;
            while (pos == null) {
                pos = getBestRandomPos(100000, 100);
            }
                
            moveToPosition(pos[0], pos[1]);
                //if (heatMap[pos[1]][pos[2]] != pos[0])
                //    continue;

            if (turnsLeft <= 0) break;
            clearAtPosition(catchMonsters(), pos, false);
        }
    }

    public int[] getBestPos() {
        double max = -1;
        int maxX = 0;
        int maxY = 0;
        for (int i = 0; i <= width; ++i) {
            for (int j = 0; j <= height; ++j) {
                if (valueMap[i][j][0] == 0 && valueMap[i][j][1] == 0) continue;
                double range = Math.sqrt(Math.pow(hero.x - i, 2) + Math.pow(hero.y - j, 2));
                int turns = (int)(range + hero.s) / hero.s;
                double val = 0;

                if (turns < 5) {
                    /*for (int k = Math.max(0, i - hero.r); k <= Math.min(width, i + hero.r); ++k) {
                        for (int l = Math.max(0, j - hero.r); l <= Math.min(height, j + hero.r); ++l) {
                            double dist = Math.sqrt(Math.pow(hero.x - k, 2) + Math.pow(hero.y - l, 2));
                            if (dist < hero.r && monstersMap[k][l] > - 1) {
                                val += hero.calculateProfit(valueMap[i][j], turnsLeft, totalTurns, i, j, dangerMap);
                            }
                        }
                    }*/
                    val = hero.calculateProfit(valueMap[i][j], turnsLeft, totalTurns, i, j, dangerMap);
                    if (val > max && i != hero.x && j != hero.y)  {
                        max = val / turns;
                        maxX = i;
                        maxY = j;
                    }
                }
            }
        }
        //if (max == -1) return getBestRandomPos(1000, 4);

        return new int[]{maxX, maxY};
    }

    public int[] getBestPosBase() {
        long maxProfit = -1;
        int maxX = 0;
        int maxY = 0;
        long minDanger = Long.MAX_VALUE;
        int minX = 0;
        int minY = 0;
        for (int i = 0; i <= width; ++i) {
            for (int j = 0; j <= height; ++j) {
                long danger = dangerMap[i][j] + hero.movementFatique(i, j, dangerMap);
                if (danger > 1E6) continue;
                long profit = calculateProfitIntPosition(i, j);
                if (danger < 100 && profit > maxProfit) {
                    maxProfit = profit;
                    maxX = i;
                    maxY = j;
                } else if (danger < minDanger && profit > 0) {
                    minDanger = danger;
                    minX = i;
                    minY = j;
                }
            }
        }
        //if (max == -1) return getBestRandomPos(1000, 4);

        if (maxProfit > 0) {
            return new int[]{maxX, maxY};
        } else return new int[]{minX, minY};

    }

    public long calculateProfitIntPosition(int x, int y) {
        double expCoeff = 1.0;
        if (turnsLeft * 1.0 / totalTurns >= 0.7) {
            expCoeff = 10.0;
        } else if (turnsLeft * 1.0 / totalTurns <= 0.2) {
            expCoeff = 0.1;
        } else if (turnsLeft * 1.0 / totalTurns <= 0.1) {
            expCoeff = 0;
        }
        
        long profit = (long) (valueMap[x][y][0] + valueMap[x][y][1] * expCoeff);


        return profit;
    }



    public int[] getBestPosInRange(int range, Boolean allow_self) {
        double max = -1;
        int maxX = 0;
        int maxY = 0;
        for (int i = Math.max(0, hero.x - range); i <= Math.min(width, hero.x + range); ++i) {
            for (int j = Math.max(0, hero.x - range); j <= Math.min(height, hero.x + range); ++j) {
                if (!canReach(range, i, j)) {
                    continue;
                }
                double val = 0;
                for (int k = Math.max(0, i - hero.r); k <= Math.min(width, i + hero.r); ++k) {
                    for (int l = Math.max(0, j - hero.r); l <= Math.min(height, j + hero.r); ++l) {
                        if (monstersMap[k][l] > - 1) {
                            val += hero.calculateProfit(valueMap[i][j], turnsLeft, totalTurns, i, j, dangerMap);
                        }
                    }
                }
                if (val > max)  {
                    if (allow_self || i != hero.x || j != hero.y) {
                        max = val;
                        maxX = i;
                        maxY = j;
                    }
                }
                }
            }
        if (max < 1E-6 &&range <= Math.max(width, height)) {
            return getBestPosInRange(range * 2, false);
        }
        // System.out.println("maxX " + maxX + " maxY " + maxY + " max " + max);
        return new int[]{maxX, maxY};
    }

    public void solveSeekingBestPos() {
        loadDanger();
        loadProfit(hero.r);
        setMonsters();
        int prevTurns = turnsLeft;
        // moveToPosition(950, 0);
        // moveToPosition(0, 950);
        // moveToPosition(150, 950);
        // System.out.println(" hero.fatique " + hero.fatique + " turnsLeft " + turnsLeft);
        while (turnsLeft > 0) {
            //System.out.println(turnsLeft + " " + hero.fatique + " " + dangerMap[48][62]);
            // System.out.println(turnsLeft + " " + hero.fatique);
            if (monsterAlive.size() <= 0) break;
            int[] pos = null;
            //pos = getBestPos();
            pos = getBestPosBase();
            moveToPosition(pos[0], pos[1]);
            if (turnsLeft <= 0) break;
            int level = hero.level;
            clearAtPosition(catchMonsters(), pos, true);
            if (hero.level != level) {
                clearAtPosition(catchMonsters(), pos, false);
            }
            if (prevTurns == turnsLeft || hero.fatique > maxGold * 1000) {
                System.out.println("Path not Found!");
                return;
            }
            if (hero.fatique > maxGold * 1000) {
                System.out.println("No more gold!");
                return;
            }
            loadProfit(hero.r);
            prevTurns = turnsLeft;
        }
    }

    Boolean stop() {
        return hero.fatique > maxGold * 1000 || turnsLeft <= 0;
    }

    public void solveSeekingBestPosInRange() {
        loadDanger();
        loadProfit(hero.r);
        setMonsters();
        moveToPosition(950, 100);
        while (turnsLeft > 0) {

            if (monsterAlive.size() <= 0) break;
            int[] pos = getBestPosInRange(hero.s, true);
            PriorityQueue<Integer> pq = catchMonsters();
            int level = hero.level;
            // System.out.println(hero.x + " " + hero.y + " " + pos[0] + " " + pos[1]);
            while (pos[0] == hero.x && pos[1] == hero.y && !stop()) {
                if (pq.isEmpty()) {
                    pos = getBestPosInRange(hero.s, false);
                    break;
                }
                Monster2 m = monsters.get(pq.poll());
                turnsLeft = hero.destroyMonster(m, turns, turnsLeft, monsterAlive, monsters, dangerMap);
                clearDanger(m);
                clearProfit(m, hero.r);
                if (level != hero.level) {
                    pq = catchMonsters();
                    loadProfit(hero.r);
                    level = hero.level;
                }
                pos = getBestPosInRange(hero.s, true);
            }
            if (stop() || !moveToPosition(pos[0], pos[1])) return;

        }
    }

    public void loadDanger() {
        dangerMap = new long[width + 1][height + 1];

        for (Integer name : monsterAlive) {
            Monster2 m = monsters.get(name);
            for (int i = Math.max(0, m.x - m.range); i <= Math.min(width, m.x + m.range); ++i) {
                for (int j = Math.max(0, m.y - m.range); j <= Math.min(height, m.y + m.range); ++j) {

                    double range = Math.sqrt(Math.pow(m.x - i, 2) + Math.pow(m.y - j, 2));

                    if (range <= m.range) {
                        dangerMap[i][j] += m.attack;
                    }  
                }
            }
            maxGold = Math.max(maxGold, m.gold);
        }
    }

    public void loadProfit(int range) {
        valueMap = new int[width + 1][height + 1][3]; 
        for (Integer name : monsterAlive) {
            Monster2 m = monsters.get(name);
            for (int i = Math.max(0, m.x - range); i <= Math.min(width, m.x + range); ++i) {
                for (int j = Math.max(0, m.y - range); j <= Math.min(height, m.y + range); ++j) {
                    if (Math.pow(i - m.x, 2) + Math.pow(j - m.y, 2) <= Math.pow(range, 2)) {
                        valueMap[i][j][0] += m.gold;
                        valueMap[i][j][1] += m.exp;
                        valueMap[i][j][2] += (m.hp + hero.p) / hero.p;
                    }       

                        
                }
            }
        }
    }

    public void clearDanger(Monster2 m) {
        for (int i = Math.max(0, m.x - m.range); i <= Math.min(width, m.x + m.range); ++i) {
            for (int j = Math.max(0, m.y - m.range); j <= Math.min(height, m.y + m.range); ++j) {
                if (Math.pow(i - m.x, 2) + Math.pow(j - m.y, 2) <= Math.pow(m.range, 2)) {
                    if (dangerMap[i][j] > 0) dangerMap[i][j] -= m.attack;
                }          
            }
        }        
    }

    public void clearProfit(Monster2 m, int range) {
        for (int i = Math.max(0, m.x - range); i <= Math.min(width, m.x + range); ++i) {
            for (int j = Math.max(0, m.y - range); j <= Math.min(height, m.y + range); ++j) {
                if (Math.pow(i - m.x, 2) + Math.pow(j - m.y, 2) <= Math.pow(range, 2)) {
                    if (valueMap[i][j][0] > 0) {
                        valueMap[i][j][0] -= m.gold;
                        valueMap[i][j][1] -= m.exp;
                        valueMap[i][j][2] -= (m.hp + hero.p) / hero.p;
                    }
                }          
            }
        }        
    }

    public void setMonsters() {
        monstersMap = new int[width + 1][height + 1];
        for (int i = 0; i <= width; ++i) {
            Arrays.fill(monstersMap[i], -1);
        }
        for (Monster2 monster : monsters) {
            monstersMap[monster.x][monster.y] = monster.name;
        }
        //monsterCount = monsters.size();
    }

    public int monsterTurn(int x, int y) {
        int sum = 0;
        for (int name : monsterAlive) {
            Monster2 m = monsters.get(name);
            double range = Math.sqrt(Math.pow(m.x - x, 2) + Math.pow(m.y - y, 2));
            if (range <= m.range) {
                sum += m.attack;
            }
        }
        return sum;
    }

    public PriorityQueue<Integer> catchMonsters() {
        PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> (Monster2.compare(monsters.get(a), monsters.get(b), hero.p)));
        for (int k = Math.max(0, hero.x - hero.r); k <= Math.min(width, hero.x + hero.r); ++k) {
            for (int l = Math.max(0, hero.y - hero.r); l <= Math.min(height, hero.y + hero.r); ++l) {
                if (canReach(hero.r, k, l)) {
                    if (monstersMap[k][l] > -1 && 
                        Math.pow(hero.x - k, 2) + Math.pow(hero.y - l, 2) <= Math.pow(hero.r, 2) - 1E-6 ) {
                        pq.offer(monstersMap[k][l]);
                    }
                }
            }
        }
        return pq;
    }





    public int clearAtPosition(PriorityQueue<Integer> pq, int[] value, boolean updateProfits) {
        int res = 0;
        while (pq.size() > 0) {
            int target = pq.poll();
            Monster2 m = monsters.get(target);
            int hp = m.hp;
            if (hp / hero.p > turnsLeft)
                return res;
            turnsLeft = hero.destroyMonster(m, turns, turnsLeft, monsterAlive, monsters, dangerMap);
            clearDanger(m);
            if(updateProfits) {
                clearProfit(m, hero.r);
            }
            monstersMap[m.x][m.y] = -1;
        }
        return res;

    }


    public boolean canReachTarget(int range, int x, int y, int a, int b) {
        if (x < 0 || y < 0 || x > this.width || y > this.height) return false;
        return Math.pow(a - x, 2) + Math.pow(b - y, 2) <= Math.pow(range, 2);
    }

    public Boolean moveToAimAtPosition(int x, int y, int evadeRange) {
        if (hero.x == x && hero.y == y) return false;
        double numOfTurns = Math.sqrt(Math.pow(x - hero.x, 2) + Math.pow(y - hero.y, 2)) / hero.s;
        if (numOfTurns < 1) numOfTurns = 1;
        if (numOfTurns > turnsLeft) return false;
        int dx = (int) ((x - hero.x) / numOfTurns);
        int dy = (int) ((y - hero.y) / numOfTurns);

        if (canReach(hero.r, x, y) || canReach(evadeRange, x, y)) return true;

        while (numOfTurns-- > 1) {
            int proposed_dx = dx;
            int proposed_dy = dy;
            while(canReachTarget(evadeRange, hero.x + proposed_dx, hero.y + proposed_dy, x, y)) {
                if (proposed_dx == 0 && proposed_dy == 0) break;
                if (Math.abs(proposed_dx) > Math.abs(proposed_dy)) {
                    if (proposed_dx > 0) {
                        --proposed_dx;
                    } else {
                        ++proposed_dx;
                    }
                } else {
                    if (proposed_dy > 0) {
                        --proposed_dy;
                    } else {
                        ++proposed_dy;
                    }
                }
            }
            moveTurn(proposed_dx, proposed_dy);
            turnsLeft--;
            if (turnsLeft <= 0) return false;
            Turn nextTurn = new Turn(hero.x, hero.y);
            turns.add(nextTurn);
            if (canReach(hero.r, x, y)) return true;
        }
        if (canReach(hero.s, x, y)) {
            int proposed_dx = x - hero.x;
            int proposed_dy = y - hero.y;
            while(canReachTarget(evadeRange, hero.x + proposed_dx, hero.y + proposed_dy, x, y)) {
                int new_dx = proposed_dx;
                int new_dy = proposed_dy;
                if (proposed_dx == 0 && proposed_dy == 0) break;
                if (Math.abs(proposed_dx) > Math.abs(proposed_dy)) {
                    if (proposed_dx > 0) {
                        --proposed_dx;
                    } else {
                        ++proposed_dx;
                    }
                } else {
                    if (proposed_dy > 0) {
                        --proposed_dy;
                    } else {
                        ++proposed_dy;
                    }
                }
                if (!canReachTarget(hero.r, hero.x + proposed_dx, hero.y + proposed_dy, x, y)) {
                    proposed_dx = new_dx;
                    proposed_dy = new_dy;
                    break;
                }
            }
            hero.x += proposed_dx;
            hero.y += proposed_dy;

            if (!canReach(hero.r, x, y)) {
                System.out.println("can't reach " + (totalTurns-turnsLeft));
            }
            // hero.x = x;
            // hero.y = y;
            hero.fatique += dangerMap[hero.x][hero.y];
            turnsLeft--;
            if (turnsLeft < 0) return false;
            Turn nextTurn = new Turn(hero.x, hero.y);
            turns.add(nextTurn);                   
        }
        return true;
    }

    public Boolean moveToPosition(int x, int y) {
        if (hero.x == x && hero.y == y) return false;
        double numOfTurns = Math.sqrt(Math.pow(x - hero.x, 2) + Math.pow(y - hero.y, 2)) / hero.s;
        if (numOfTurns < 1) numOfTurns = 1;
        if (numOfTurns > turnsLeft) return false;
        int dx = (int) ((x - hero.x) / numOfTurns);
        int dy = (int) ((y - hero.y) / numOfTurns);

        while (numOfTurns-- > 1) {
            moveTurn(dx, dy);
            turnsLeft--;
            if (turnsLeft <= 0) return false;
            Turn nextTurn = new Turn(hero.x, hero.y);
            turns.add(nextTurn);
        }
        if (canReach(hero.s, x, y)) {
            hero.x = x;
            hero.y = y;
            hero.fatique += dangerMap[hero.x][hero.y];
            turnsLeft--;
            if (turnsLeft < 0) return false;
            Turn nextTurn = new Turn(hero.x, hero.y);
            turns.add(nextTurn);                   
        }
        return true;
    }

    public Boolean moveToPositionAndClear(int x, int y) {
        if (hero.x == x && hero.y == y) return false;
        double numOfTurns = Math.sqrt(Math.pow(x - hero.x, 2) + Math.pow(y - hero.y, 2)) / hero.s;
        // if (numOfTurns < 1) numOfTurns = 1;
        // if (numOfTurns > turnsLeft) return false;
        int dx = (int) ((x - hero.x) / numOfTurns);
        int dy = (int) ((y - hero.y) / numOfTurns);

        while (numOfTurns-- > 1) {
            moveTurn(dx, dy);
            System.out.println("clearing at " + hero.x+ " " + hero.y);
            turnsLeft--;
            if (turnsLeft <= 0) return false;
            Turn nextTurn = new Turn(hero.x, hero.y);
            turns.add(nextTurn);
            clearAtPositionWithLimit(catchMonsters(), new int[] {hero.x, hero.y}, false, 1000);
        }
        if ((hero.x != x || hero.y != y) && canReach(hero.s, x, y)) {
            hero.x = x;
            hero.y = y;
            hero.fatique += dangerMap[hero.x][hero.y];
            turnsLeft--;
            if (turnsLeft < 0) return false;
            Turn nextTurn = new Turn(hero.x, hero.y);
            turns.add(nextTurn);                   
        }
        return true;
    }

    public int clearAtPositionWithLimit(PriorityQueue<Integer> pq, int[] value, boolean updateProfits, int hp_limit) {
        int res = 0;
        while (pq.size() > 0) {
            int target = pq.poll();
            Monster2 m = monsters.get(target);
            int hp = m.hp;
            if (hp > hp_limit) {
                continue;
            }
            if (hp / hero.p > turnsLeft)
                return res;
            turnsLeft = hero.destroyMonster(m, turns, turnsLeft, monsterAlive, monsters, dangerMap);
            clearDanger(m);
            if(updateProfits) {
                clearProfit(m, hero.r);
            }
            monstersMap[m.x][m.y] = -1;
        }
        return res;

    }

    public void moveTurn(int dx, int dy) {
        hero.x += dx;
        hero.y += dy;
        if (hero.x < 0) hero.x = 0;
        if (hero.x > width) hero.x = width;
        if (hero.y < 0) hero.y = 0;
        if (hero.y > height) hero.y = height;   
        hero.fatique += dangerMap[hero.x][hero.y];   
    }


    public boolean canReach(int range, int x, int y) {
        if (x < 0 || y < 0 || x > this.width || y > this.height) return false;
        return Math.pow(hero.x - x, 2) + Math.pow(hero.y - y, 2) <= Math.pow(range, 2);
    }

    public void writeSolution(ObjectMapper objectMapper, String path) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"moves\": [\n");
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(path))))) {
            for (Turn turn : turns) {
                sb.append(objectMapper.writeValueAsString(turn)).append(",\n");
            }
            sb.deleteCharAt(sb.length()-2);
            sb.append("]\n}");
            bw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   /*-------------------------------------------------------------------------------------------------------------------------------------------------------- */
    public void solveByBestPosInRange() {
        while (turnsLeft > 0) {
            ArrayList<Monster> removed = new ArrayList<>();
            for (Monster2 m : monsters) {
                if (canReach(hero.r, m.x, m.y)) {
                    hero.destroyMonster(m, turns, turnsLeft, monsterAlive, monsters, dangerMap);
                    //monstersCount = monsterAlive.size();
                    monstersMap[m.x][m.y] = -1;
                }
            }
            monsters.removeAll(removed);
            int speed = hero.s;
            Position pos = null;
            do {
                //int[] pos = pickBestPosFromRange(speed);
                speed += hero.s;
            } while (pos == null);
            //hero.movePos(pos, turns);
        }
    }

    public int[] pickBestPosFromRange(int range) {
        int maxCount = 0;
        int bestX = 0;
        int bestY = 0;
        for (int i = 0; i <= hero.s; ++i) {
            for (int j = 0; j <= hero.s; ++j) {
                if (!canReach(hero.s, i, j)) {
                    continue;
                }
                int count = 0;
                for (Monster2 m : monsters) {
                    if (canReach(hero.r, m.x, m.y)) {
                        count += m.value();
                    }
                }
                if (count > maxCount) {
                    maxCount = count;
                    bestX = i;
                    bestY = j;
                }
            }
        }
        if (maxCount == 0) return null;

        return new int[]{bestX, bestY};
    }

    /*------------------------------------------------------------------------------------- */
    public void solveWithNClosest(int n) {
        while (turnsLeft > 0) {
            //System.out.println(turnsLeft);
            Monster2 target = null;
            target = getFromNClosest(n);

            if (target == null) break;
            moveToPosition(target.x, target.y);
            hero.destroyMonster(target, turns, n, monsterAlive, monsters, dangerMap);         
        }
    }

    public Monster2 getFromNClosest(int n) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((a,b) -> a[1] - b[1]);
        for (int i : monsterAlive) {
            int[] val = new int[2];
            val[0] = i;
            val[1] = (int) (Math.pow(hero.x - monsters.get(i).x, 2) + Math.pow(hero.y - monsters.get(i).y, 2));
            pq.offer(val);
            if (pq.size() > n) pq.poll();
        }
        int res = pq.poll()[0];
        while (pq.size() > 0 ) {
            if ((int)(Math.min(Math.random()*10,1)) < 7) {
                res = pq.poll()[0];
            }
        }
        return monsters.get(res);
    }
}
