package jweekend;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import jweekend.v2.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

    public static void main(String[] args) throws JsonProcessingException, IOException{

        int[] bestScores = loadBest(50);
        int[] oldBest = Arrays.copyOf(bestScores, 50);
        boolean[] improved = new boolean[50];
        Set<Integer> updated = loadUpdated(50);

        int part = 1;
        int t = 2;
        if (part == 1) {
            
            double[] params = new double[]{0, 0, 0, 0, 0};
            for (int k = 0; k < 100; ++k) {
                for (int i = 1; i <= 25; ++i) {
                    int res = compute("/home/nr/prj/code-weekend/j_solver/jsolver/test/" + i + "/input", i, bestScores, improved, params); 
                    System.out.println(i + " " + res);
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
        } else {
            double[] params = new double[]{0, 0, 0, 0, 0};
            for (int k = 0; k < 1; ++k) {
                for (int i = 25; i <= 50; ++i) {
                    int res = compute2("/home/nr/prj/code-weekend/j_solver/jsolver/test/" + i + "/input", i, bestScores, improved, params, updated); 
                    if (t == 2 && res > oldBest[i-1]) 
                        System.out.println("Task " + i + " result was improved from " + oldBest[i-1] + " to " + res);
                    else 
                        System.out.println("Task " + i + " not improved " + res + "/" + oldBest[i-1]);
                }
            }
    
            for (int i = 25; i < 50; ++i) {
                if (improved[i]) {
                    System.out.println("Task " + (i + 1) + " result was improved:" + oldBest[i] + " to " + bestScores[i]);
                } 
            }
            writeBest(bestScores);
            writeBest(updated);
        }

        System.out.println("End!");
    }
    public static int compute(String path, int task, int[] bestScores, boolean[] improved, double[] params) {
        ObjectMapper objectMapper = new ObjectMapper();
        int res = 0;
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

            hero.initHero(params);
            Solver solver = new Solver(hero, monsters, w, h);
            //solver.solvePositional();
            solver.solveWithNClosest(1);
            res = hero.gold; 
            if (res > bestScores[task-1]) {
                bestScores[task-1] = res;
                improved[task-1] = true;
            } 
            solver.writeSolution(objectMapper, "target/"+task+".json");
        } catch( IOException e) {
            e.printStackTrace();
        }

        return res;

    }

    public static int compute2(String path, int task, int[] bestScores, boolean[] improved, double[] params, Set<Integer> updated) {
        ObjectMapper objectMapper = new ObjectMapper();
        int res = 0;
        try {
            JsonNode node = objectMapper.readTree(new File(path));
            Iterator<Entry<String, JsonNode>> it = node.fields();
            Hero2 hero = null;
            List<Monster2> monsters = new ArrayList<>();
            int x = 0;
            int y = 0;
            int numTurns = 0;
            int w = 0;
            int h = 0;        
            while(it.hasNext()) {
    
                Entry<String, JsonNode> entry = it.next();
                switch (entry.getKey()) {
                    case "hero":
                        hero = objectMapper.readValue(entry.getValue().toString(), Hero2.class);
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
                        monsters = objectMapper.readValue(entry.getValue().toString(), new TypeReference<List<Monster2>>(){});
                        break;
                    default:
                        break;
                }
    
            }

            hero.init();
            hero.x = x;
            hero.y = y;
            Solver2 solver = new Solver2(hero, monsters, w, h, numTurns);
            // solver.solveWithNClosest(2);
            solver.solveSeekingRandomPos();
            // solver.solveTask50();
            // solver.solveSeekingBestPos();

            // solver.solveTask36();
            // solver.solveSeekingBestPosInRange();
            res = (int) hero.gold; 
            if (res > bestScores[task-1]) {
                bestScores[task-1] = res;
                improved[task-1] = true;
                updated.add(task);
                solver.writeSolution(objectMapper, "target/"+task+".json");                 
            } 

            //System.out.println(hero.gold +" " + hero.fatique);
        } catch( IOException e) {
            e.printStackTrace();
        }

        return res;

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

    private static Set<Integer> loadUpdated(int count) {
        Set<Integer> updated = new HashSet<>();   
        try(BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("updated"))))) {
            String scoreString = br.readLine();
            if (scoreString != null) {
                String[] strs = scoreString.split(" ");
                for (int i = 0; i < strs.length; ++i) {
                    updated.add(Integer.parseInt(strs[i]));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("BestScores not found!");
        } catch (IOException e) {
            System.out.println("Error while reading bestScores...");
        }
        return updated;
    }    

    private static void writeBest(Set<Integer> updated) {
        StringBuilder sb = new StringBuilder();
        List<Integer> upd = new ArrayList<>(updated);
        Collections.sort(upd);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get("updated"))))) {
            for (Integer score: upd) {
                sb.append(score).append(" ");
            }
            bw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }        
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