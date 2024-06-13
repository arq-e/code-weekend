package codeweekend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MonsterNew {

    @JsonProperty("x")
    private int x;
    @JsonProperty("y")
    private int y;
    @JsonProperty("hp")
    private int hp;
    @JsonProperty("gold")
    private int gold;
    @JsonProperty("exp")
    private int exp;
    @JsonProperty("range")
    private int range = 0;
    @JsonProperty("attack")
    private int attack = 0;

    private int name;

    public MonsterNew() {

    }

    public MonsterNew(int x, int y, int hp, int gold, int exp) {
        this.hp = hp;
        this.gold = gold;
        this.exp = exp;
    }

    /*public double value(double turnsRemaining) {
        double coeff = turnsRemaining >= 0.9 ? expEarly : expCoeff;
        return this.gold + this.exp * coeff;
    }*/
    
    public static int compare(MonsterNew m1, MonsterNew m2) {
        return m1.gold * m1.exp > m2.gold * m2.exp ? 1 : -1;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getHp() {
        return this.hp;
    }

    public int getGold() {
        return this.gold;
    }

    public int getExp() {
        return this.exp;
    }

    public int getRange() {
        return this.range;
    }

    public int getAttack() {
        return this.attack;
    }

    public int getName() {
        return this.name;
    }

    public void setName(int name) {
        this.name = name;
    }

}
