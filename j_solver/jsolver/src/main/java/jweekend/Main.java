package jweekend;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

    public static void main(String[] args) throws JsonProcessingException, IOException{

        int[] bestScores = loadBest(25);
        int[] oldBest = Arrays.copyOf(bestScores, 25);
        boolean[] improved = new boolean[25];
        for (int k = 0; k < 1; ++k) {
            for (int i = 1; i <= 25; ++i) {
                compute("test\\" + i + "\\input", i, bestScores, improved);
            }
        }
        for (int i = 0; i < 25; ++i) {
            if (improved[i]) {
                System.out.println("Task " + (i + 1) + " result was improved:" + oldBest[i] + " to " + bestScores[i]);
            } else {
                //System.out.println("Task " + (i + 1) + " result was improved:" + oldBest[i] + " to " + bestScores[i]);                
            }
        }
        writeBest(bestScores);
        System.out.println("End!");
    }

    public static void compute(String path, int task, int[] bestScores, boolean[] improved) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode node = objectMapper.readTree(new File(path));
            Iterator<Entry<String, JsonNode>> it = node.fields();
            Hero hero = null;
            List<Monster> monsters = new ArrayList<>();
            int x = 0;
            int y = 0;
            int numTurns = 0;
            int w = 0;
            int h = 0;        
            while(it.hasNext()) {
    
                Entry<String, JsonNode> entry = it.next();
                switch (entry.getKey()) {
                    case "hero":
                        hero = objectMapper.readValue(entry.getValue().toString(), Hero.class);
                        break;
                    case "start_x":
                        x = Integer.parseInt(entry.getValue().toString());
                        break;
                    case "start_y":
                        y = Integer.parseInt(entry.getValue().toString());
                        break;
                    case "width":
                        w = Integer.parseInt(entry.getValue().toString());
                        break;    
                    case "height":
                        h = Integer.parseInt(entry.getValue().toString());
                        break;
                    case "num_turns":
                        numTurns = Integer.parseInt(entry.getValue().toString());
                        break;                
                    case "monsters":
                        monsters = objectMapper.readValue(entry.getValue().toString(), new TypeReference<List<Monster>>(){});
                        break;
                    default:
                        break;
                }
    
            }
            if (hero != null) {
                hero.initHero();
                hero.pos = new Position(x, y);
                hero.pos.maxX = w;
                hero.pos.maxY = h;
                hero.turnsLeft = numTurns;
                hero.baseTurns = numTurns;
            }
            Solver solver = new Solver(hero, monsters, w, h);
            solver.solveByRelativeProfit();
            //solver.solveWithNClosest(2);
            //solver.solveHuntingRandom();
            //solver.solveHuntingClosest();            
            if (solver.hero.gold > bestScores[task-1]) {
                bestScores[task-1] = solver.hero.gold;
                improved[task-1] = true;
                //System.out.println("Task " + task + " result was improved!");
                solver.writeSolution(objectMapper, "target\\"+task+".json");
            }
            
        } catch( IOException e) {
            e.printStackTrace();
        }

    }

    private static int[] loadBest(int count) {
        int[] bestScores = new int[count];    
        try(BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("bestScores"))))) {
            String scoreString = br.readLine();
            if (scoreString != null) {
                String[] strs = scoreString.split(" ");
                for (int i = 0; i < strs.length; ++i) {
                    bestScores[i] = Integer.parseInt(strs[i]);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("BestScores not found!");
        } catch (IOException e) {
            System.out.println("Error while reading bestScores...");
        }
        return bestScores;
    }

    private static void writeBest(int[] bestScores) {
        StringBuilder sb = new StringBuilder();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get("bestScores"))))) {
            for (Integer score: bestScores) {
                sb.append(score).append(" ");
            }
            bw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
}