package jweekend;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Turn {
    @JsonProperty("type")    
    String type;
    @JsonProperty("target_id")    
    int id;
    @JsonProperty("target_x")    
    int x;
    @JsonProperty("target_y")
    int y;

    public Turn() {

    }

    public Turn(int id) {
        this.type = "attack";
        this.id = id;
    }

    public Turn(int x, int y) {
        this.type = "move";
        this.x = x;
        this.y = y;
    }
}
