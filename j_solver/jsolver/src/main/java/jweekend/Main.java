package jweekend;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    Hero hero;
    List<Monster> monsters;

    public static void main(String[] args) throws JsonProcessingException, IOException{

        for (int i = 1; i <= 25; ++i) {
            compute("test\\" + i + "\\input", i);
        }
 
    }

    public static void compute(String path, int task) {
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
                hero.turnsLeft = numTurns;
            }
            Solver solver = new Solver(hero, monsters, w, h);
            solver.solve();
            //Paths.get("solutions\\"+task+".json").toFile().createNewFile();
            solver.writeSolution(objectMapper, "target\\"+task+".json");
        } catch( IOException e) {
            e.printStackTrace();
        }

    }
}