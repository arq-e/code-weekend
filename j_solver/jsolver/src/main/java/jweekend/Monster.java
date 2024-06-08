package jweekend;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Monster {
    public Position pos;
    @JsonProperty("x")
    public int x;
    @JsonProperty("y")
    public int y;
    @JsonProperty("hp")
    public int hp;
    @JsonProperty("gold")
    public int gold;
    @JsonProperty("exp")
    public int exp;

    public int name;

    public Monster() {

    }

    public Monster(int x, int y, int hp, int gold, int exp) {
        pos = new Position(x, y);
        this.hp = hp;
        this.gold = gold;
        this.exp = exp;
    }
}
