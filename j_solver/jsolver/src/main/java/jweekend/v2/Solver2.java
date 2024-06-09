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
        if (n > height * width) n = height * width;
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
            while (pos == null) {
                pos = getBestRandomPos(1000, 5);
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
                long profit = calculateProfitIntPosition(i, j);
                if (danger == 0 && profit > maxProfit) {
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



    public void solveSeekingBestPos() {
        loadDanger();
        loadProfit(hero.r);
        setMonsters();
        int prevTurns = turnsLeft;
        while (turnsLeft > 0) {
            //System.out.println(turnsLeft + " " + hero.fatique + " " + dangerMap[48][62]);
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
            loadProfit(hero.r);
            prevTurns = turnsLeft;
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
                if (monstersMap[k][l] > -1 && 
                    Math.pow(hero.x - k, 2) + Math.pow(hero.y - l, 2) <= Math.pow(hero.r, 2) ) {
                    pq.offer(monstersMap[k][l]);
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



    public void moveToPosition(int x, int y) {
        if (hero.x == x && hero.y == y) return;
        double numOfTurns = Math.sqrt(Math.pow(x - hero.x, 2) + Math.pow(y - hero.y, 2)) / hero.s;
        int dx = (int) ((x - hero.x) / numOfTurns);
        int dy = (int) ((y - hero.y) / numOfTurns);

        while (numOfTurns-- > 1) {
            moveTurn(dx, dy);
            turnsLeft--;
            if (turnsLeft <= 0) return;
            Turn nextTurn = new Turn(hero.x, hero.y);
            turns.add(nextTurn);
        }
        if (hero.x != x && hero.y != y && canReach(hero.s, x, y)) {
            hero.x = x;
            hero.y = y;
            hero.fatique += dangerMap[hero.x][hero.y];
            turnsLeft--;
            if (turnsLeft < 0) return;
            Turn nextTurn = new Turn(hero.x, hero.y);
            turns.add(nextTurn);                   
        }      
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
