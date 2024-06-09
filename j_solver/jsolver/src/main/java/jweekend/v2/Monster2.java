package jweekend.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import jweekend.Monster;

public class Monster2 {
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
    @JsonProperty("range")
    public int range;
    @JsonProperty("attack")
    public int attack;

    public int name;

    public Monster2() {

    }

    public double value(int heroPower) {
        int timeToKill = (this.hp + heroPower) / heroPower;
        return this.gold * this.exp / (timeToKill * Math.pow(attack, 2));
    }
    
    public static int compare(Monster2 m1, Monster2 m2, int heroPower) {
        return m1.value(heroPower) > m2.value(heroPower) ? 1 : -1;
    }    

}
