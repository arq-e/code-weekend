package codeweekend.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import codeweekend.model.Game;
import codeweekend.model.Hero;
import codeweekend.model.Monster;
import codeweekend.model.Turn;

public class IOUtils {
    private static final String INPUT_PATH = "tasks/";
    private static final String OUTPUT_PATH = "solutions/";
    private static final String ANNEALING_ROUTES_PATH = "paths/";
    private static final String ANNEALING_SCORES_PATH = "scores/";
    private static final String BEST_SCORES_PATH = "bestScores";
    private static final int NUM_OF_TESTS = 50;

    public static Game parseInput(ObjectMapper objectMapper,int task, Hero hero){
        try {
            JsonNode node = objectMapper.readTree(new File(INPUT_PATH + task + "/input"));
            Iterator<Entry<String, JsonNode>> it = node.fields();

            Hero taskHero = null;
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
                        taskHero = objectMapper.readValue(entry.getValue().toString(), Hero.class);
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
            for (int i = 0; i < monsters.size(); ++i) {
                monsters.get(i).setName(i);
            }
            hero.copy(taskHero);
            hero.initBaseStats(x, y);
            Game game = new Game(task, w, h, numTurns);
            game.init( monsters);
                
            return game;
        } catch( IOException e) {
            System.out.printf("Error occured while reading %d/input.json\n", task);
        }

        return null;
    }

    public static void writeSolution(ObjectMapper objectMapper, int task, List<Turn> turns) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"moves\": [\n");
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(OUTPUT_PATH + task + ".json"))))) {
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

    public static int[] loadBestScores() {
        int[] bestScores = new int[NUM_OF_TESTS + 1];    
        try(BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(BEST_SCORES_PATH))))) {
            String scoreString = br.readLine();
            if (scoreString != null) {
                String[] strs = scoreString.trim().split(" ");
                for (int i = 0; i < strs.length; ++i) {
                    bestScores[i+1] = Integer.parseInt(strs[i]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error occured while reading bestScores.");
        }

        return bestScores;
    }

    public static void writeBestScores(int[] bestScores) {
        StringBuilder sb = new StringBuilder();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(BEST_SCORES_PATH))))) {
            for (int i = 1; i <= NUM_OF_TESTS; ++i) {
                sb.append(bestScores[i]).append(" ");
            }
            bw.write(sb.toString());
        } catch (IOException e) {
            System.out.println("Error occured while reading bestScores.");
        }        
    }

    public static int[] readAnnealingPath(int task) {
        int[] idx = null;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(ANNEALING_ROUTES_PATH + task))))) {
            String scoreString = br.readLine();
            
            if (scoreString != null) {
                String[] strs = scoreString.trim().split(" ");
                idx = new int[strs.length];
                for (int i = 0; i < strs.length; ++i) {
                    idx[i] = Integer.parseInt(strs[i]);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Annealing paths not found!");
        } catch (IOException e) {
            System.out.println("Error while reading Annealing path...");
        } 
        return idx;
    }
    
    public static void writeAnnealingPath(int task, int[] idx) {
        StringBuilder sb = new StringBuilder();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(ANNEALING_ROUTES_PATH + task))))) {
            for (Integer score: idx) {
                sb.append(score).append(" ");
            }
            bw.write(sb.toString().trim());
        } catch (IOException e) {
            e.printStackTrace();
        }    
    }

    public static void writeAnnealingScores(int task, StringBuilder sb) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(ANNEALING_SCORES_PATH + task))))) {
            bw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
