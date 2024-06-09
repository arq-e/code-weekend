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

import jweekend.Hero;
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

    int[][] heatMap;
    int[][] monstersMap;
    int monstersCount;

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
                            val+= (monsters.get(monstersMap[k][l]).value(hero.p)) / range;
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
        for (Monster2 monster : monsters) {
            monstersMap[monster.x][monster.y] = monster.name;
        }
        monstersCount = monsters.size();
    }

    public void monsterTurn() {
        for (int name : monsterAlive) {
            Monster2 m = monsters.get(name);
            double range = Math.sqrt(Math.pow(m.x - hero.x, 2) + Math.pow(m.y - hero.y, 2));
            if (range <= m.range) {
                hero.fatique += m.attack;
            }
        }
    }

    public PriorityQueue<Integer> catchMonsters() {
        PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> (Monster2.compare(monsters.get(a), monsters.get(b), hero.p)));
        for (int k = Math.max(0, hero.x - hero.r); k <= Math.min(width, hero.x + hero.r); ++k) {
            for (int l = Math.max(0, hero.y - hero.r); l <= Math.min(height, hero.y + hero.r); ++l) {
                if (monstersMap[k][l] > - 1 && Math.pow(hero.x  - k, 2) + Math.pow(hero.y-l, 2) <= Math.pow(hero.r, 2) ) {
                    pq.offer(monstersMap[k][l]);
                }
            }
        }
        return pq;
    }

    public void solvePositional() {
        setMonsters();
        //int level = hero.level;
        while (turnsLeft > 0) {
            if (monstersCount <= 0) break;
            int[] pos = setHeatMap(1000);
                //if (heatMap[pos[1]][pos[2]] != pos[0])
                //    continue;
            moveToPosition(pos[1], pos[2]);
            if (turnsLeft <= 0) break;
            clearAtPosition(catchMonsters(), pos);
        }
    }

    public int clearAtPosition(PriorityQueue<Integer> pq, int[] value) {
        int baseValue = value[0];
        int res = 0;
        while (pq.size() > 0) {
            int target = pq.poll();
            Monster2 m = monsters.get(target);
            int hp = m.hp;
            if (hp / hero.p > turnsLeft)
                return res;
            if (m.exp > hero.expToUp && turnsLeft > 0.1 * totalTurns) {
                if (Math.random() * value[0] / baseValue < 0.8) return res;
            }
            turnsLeft = hero.destroyMonster(m, turns, turnsLeft, monsterAlive);
            monstersCount = monsterAlive.size();
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
            if (heatMap[hero.x][hero.y] * (Math.random() + 0.5) > heatMap[x][y]) {
                return;
            }
        }
        if (hero.x != x && hero.y != y && this.canReach(hero.s, x, y)) {
            hero.x = x;
            hero.y = y;     
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
}
