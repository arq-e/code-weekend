package jweekend;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Solver {
    Hero hero;
    List<Monster> monsters;
    List<Turn> turns;
    int width;
    int height;
    int[][] heatMap;
    int[][] monstersMap;
    int monstersCount;

    public Solver(Hero hero, List<Monster> monsters, int width, int height) {
        this.hero = hero;
        this.monsters = monsters;
        for (int i = 0; i < monsters.size(); ++i) {
            monsters.get(i).name = i;
        }
        this.width = width;
        this.height = height;
        this.turns = new ArrayList<>();
    }

    public int[] setHeatMap(int n) {
        heatMap = new int[width+1][height+1];
        //PriorityQueue<int[]> pq = new PriorityQueue<>((a,b) -> b[0] - a[0]);
        int[][] targets = new int[n][2];
        for (int i = 0; i < n; ++i) {
            targets[i][0] = (int) (Math.random()*width);
            targets[i][1] = (int) (Math.random()*height);
        }
        int max = 0;
        int maxX = 0;
        int maxY = 0;
            for (int i = 0; i < n; ++i) {
                int val = 0;
                for (int k = Math.max(0, targets[i][0] - hero.r); k <= Math.min(width, targets[i][0] + hero.r); ++k) {
                    for (int l = Math.max(0, targets[i][1] - hero.r); l <= Math.min(height, targets[i][1] + hero.r); ++l) {
                        if (monstersMap[k][l] > - 1) {
                            double range = Math.pow(targets[i][0] - k, 2) + Math.pow(targets[i][1]- l, 2);
                            val+= (monsters.get(monstersMap[k][l]).value(hero.turnsLeft * 1.0 / hero.baseTurns)) / range;
                        }
                    }
                }
                if (val > max) {
                    max = val;
                    maxX = targets[i][0];
                    maxY = targets[i][1];
                }
            }


        return new int[]{max, maxX, maxY};

    }

    public void setMonsters() {
        monstersMap = new int[width + 1][height + 1];
        for (int i = 0; i <= width; ++i) {
            Arrays.fill(monstersMap[i], -1);
        }
        for (Monster monster : monsters) {
            monstersMap[monster.x][monster.y] = monster.name;
        }
        monstersCount = monsters.size();
    }

    public PriorityQueue<Integer> catchMonsters() {
        PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> (Monster.compare(monsters.get(a), monsters.get(b), hero.turnsLeft * 1.0 / hero.baseTurns)));
        for (int k = Math.max(0, hero.pos.x - hero.r); k <= Math.min(width, hero.pos.x + hero.r); ++k) {
            for (int l = Math.max(0, hero.pos.y - hero.r); l <= Math.min(height, hero.pos.y + hero.r); ++l) {
                if (monstersMap[k][l] > - 1 && Math.pow(hero.pos.x  - k, 2) + Math.pow(hero.pos.y-l, 2) <= Math.pow(hero.r, 2) ) {
                    pq.offer(monstersMap[k][l]);
                }
            }
        }
        return pq;
    }

    public void solvePositional() {
        setMonsters();
        //int level = hero.level;
        while (hero.turnsLeft > 0) {
            //System.out.println(hero.turnsLeft);
            if (monstersCount <= 0) break;
            int[] pos = setHeatMap(2000);
                //if (heatMap[pos[1]][pos[2]] != pos[0])
                //    continue;
                hero.moveToPosition(new Position(pos[1], pos[2]), turns, heatMap);
                if (hero.turnsLeft <= 0) break;
                monstersCount -= hero.clear(catchMonsters(), turns, monstersMap, monsters, pos, heatMap);
        }
    }

    public void solveByRelativeProfitPoint() {
        while (hero.turnsLeft > 0) {
            int[] peak = getBestPosition();
            hero.move(new Monster(peak[0], peak[1], 0, 0, 0), turns);
            //while ()
        }
    }

    public int[] getBestPosition() {
        for (int i = 0; i <= hero.s; ++i) {
            for (int j = 0; j <= hero.s; ++i) {

            }
        }
        double maxVal = 0;
        int[] max = new int[2];
        for (int i = 0; i <= width; ++i) {
            for (int j = 0; j <= height; ++j) {
                double range = Math.sqrt(Math.pow(hero.pos.x - i, 2) + Math.pow(hero.pos.y - j, 2));
                double score = heatMap[i][j] / range;
                if (score >= maxVal) {
                    max[0] = i;
                    max[1] = j;
                    maxVal = heatMap[i][j]; 
                }
            }
        }   
        return max;     
    }
    

    public void solveWithNClosest(int n) {
        while (hero.turnsLeft > 0) {
            Monster target = null;
            if (Math.random()*10 > 2) {
                target = getClosestMonster();
            } else {
                target = getFromNClosest(n);
            }

            if (target == null) break;
            hero.move(target, turns);
            hero.destroy(target, turns);
            monsters.remove(target);            
        }
    }

    public void solveWithNClosestProfit(int n) {
        while (hero.turnsLeft > 0) {
            Monster target = null;
            if (Math.random()*10 > 2) {
                target = getClosestMonster();
            } else {
                target = getFromNClosestProfit(n);
            }

            if (target == null) break;
            hero.move(target, turns);
            hero.destroy(target, turns);
            monsters.remove(target);            
        }
    }

    public void solveByRelativeProfit() {
        while (hero.turnsLeft > 0) {
            Monster target = getMostProfitable();
            if (target == null) break;
            hero.move(target, turns);
            hero.destroy(target, turns);
            monsters.remove(target);            
        }
    }

    public void solveHuntingRandom() {
        while (hero.turnsLeft > 0) {
            Monster target = getClosestMonster();
            if (target == null) break;
            hero.move(target, turns);
            hero.destroy(target, turns);
            monsters.remove(target);            
        }
    }

    public void solveHuntingClosest() {
        while (hero.turnsLeft > 0) {
            Monster target = getClosestMonster();
            if (target == null) break;
            hero.move(target, turns);
            hero.destroy(target, turns);
            monsters.remove(target);
        }

    }

    public Monster getFromNClosestProfit(int n) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((a,b) -> a[1] - b[1]);
        for (int i = 0; i < monsters.size(); ++i) {
            int[] val = new int[2];
            val[0] = i;
            val[1] = (int)hero.calculateProfit(monsters.get(i));
            pq.offer(val);
            if (pq.size() > n) pq.poll();
        }
        int res = pq.poll()[0];
        while (pq.size() > 0 ) {
            if ((Math.min(Math.random()*10,1)) < 8) {
                res = pq.poll()[0];
            }
        }
        return monsters.get(res);
    }

    public Monster getMostProfitable() {
        if (monsters.size() == 0) return null;
        Monster closest = monsters.get(0);
        double max = 0;
        for (Monster monster : monsters) {
            double rProfit = hero.calculateProfit(monster);

            if (rProfit > max) {
                max = rProfit;
                closest = monster;
            }
        }   
        return closest;     
    }

    public Monster getFromNClosest(int n) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((a,b) -> a[1] - b[1]);
        for (int i = 0; i < monsters.size(); ++i) {
            int[] val = new int[2];
            val[0] = i;
            val[1] = (int) (Math.pow(hero.pos.x - monsters.get(i).x, 2) + Math.pow(hero.pos.y - monsters.get(i).y, 2));
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

    public Monster getRandomMonster() {
        return monsters.get((int)(Math.min(Math.random()*monsters.size(),1)));
    }

    public Monster getClosestMonster() {
        if (monsters.size() == 0) return null;
        Monster closest = monsters.get(0);
        int min = Integer.MAX_VALUE;
        for (Monster monster : monsters) {
            int dist = (int) (Math.pow(hero.pos.x - monster.x, 2) + Math.pow(hero.pos.y - monster.y, 2));
            if (dist < min) {
                min = dist;
                closest = monster;
            }
        }
        return closest;
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


}
