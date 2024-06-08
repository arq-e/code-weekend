package jweekend;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
            hero.kill(target, turns);
            monsters.remove(target);            
        }
    }

    public void solveByRelativeProfit() {
        while (hero.turnsLeft > 0) {
            Monster target = getMostProfitable();
            if (target == null) break;
            hero.move(target, turns);
            hero.kill(target, turns);
            monsters.remove(target);            
        }
    }

    public void solveHuntingRandom() {
        while (hero.turnsLeft > 0) {
            Monster target = getClosestMonster();
            if (target == null) break;
            hero.move(target, turns);
            hero.kill(target, turns);
            monsters.remove(target);            
        }
    }

    public void solveHuntingClosest() {
        while (hero.turnsLeft > 0) {
            Monster target = getClosestMonster();
            if (target == null) break;
            hero.move(target, turns);
            hero.kill(target, turns);
            monsters.remove(target);
        }

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
        while (monsters.size() > 0 ) {
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
