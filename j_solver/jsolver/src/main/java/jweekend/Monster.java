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


    public static double expCoeff = 0.5;
    public static double expEarly = 8.0;
    public int name;

    public Monster() {

    }

    public Monster(int x, int y, int hp, int gold, int exp) {
        pos = new Position(x, y);
        this.hp = hp;
        this.gold = gold;
        this.exp = exp;
    }

    public double value(double turnsRemaining) {
        double coeff = turnsRemaining >= 0.9 ? expEarly : expCoeff;
        return this.gold + this.exp * coeff;
    }
    
    public static int compare(Monster m1, Monster m2, double turnsRemaining) {
        double coeff = turnsRemaining >= 0.7 ? expEarly : expCoeff;
        return m1.gold + m1.exp *expCoeff > m2.gold + m2.exp *expCoeff ? 1 : -1;
    }
}
