package jweekend.v2;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jweekend.Hero;
import jweekend.Monster;
import jweekend.Position;
import jweekend.Turn;
public class Hero2{
    @JsonProperty("base_speed")
    int baseS;
    @JsonProperty("base_power")
    int baseP;
    @JsonProperty("base_range")
    int baseR;
    @JsonProperty("level_speed_coeff")
    int speedC;
    @JsonProperty("level_power_coeff")
    int powerC;
    @JsonProperty("level_range_coeff")
    int rangeC;

    public int x;
    public int y;
    int level;
    int s;
    int p;
    int r;
    int expToUp;
    public long gold;
    long fatique;

    public Hero2() {

    }
    public Hero2(int baseS, int  baseP, int baseR, int speedC, int powerC, int rangeC) {
        this.baseS = baseS;
        this.baseP = baseP;
        this.baseR = baseR;
        this.speedC = speedC;
        this.powerC = powerC;
        this.rangeC = rangeC;

        init();
    }

    public void init() {
        s = baseS;
        p = baseP;
        r = baseR;
        this.level = 0;
        gold = 0;
        expToUp = 1000;
        fatique = 0;
    }

    public void levelUp() {
        ++level;
        expToUp = 1000 + (level) * (level + 1) * 50;
        s = (int) (baseS * (1 + level * 1.0 * speedC / 100));
        p = (int) (baseP * (1 + level * 1.0 * powerC / 100));
        r = (int) (baseR * (1 + level * 1.0 * rangeC / 100));
    }

    public void earnGold(int gold) {
        this.gold += gold * (1000 * 1.0 / (1000 + this.fatique));
    }

    public void addExp(int exp) {
        while (exp >= expToUp) {
            
            exp -= expToUp;
            levelUp();
        }
        expToUp -= exp;
    }

    public int destroyMonster(Monster2 monster, List<Turn> turns, int turnsLeft, Set<Integer> monsterAlive) {
        if (monster.name == 934){
            System.out.println();
        }
        int hp = monster.hp;
        while (hp > 0) {
            turnsLeft--;
            if (turnsLeft < 0) return turnsLeft;
            hp -= p;
            Turn nexTurn = new Turn(monster.name);
            turns.add(nexTurn);
        }
        monsterAlive.remove(monster.name);
        earnGold(monster.gold);
        addExp(monster.exp);
        return turnsLeft;
    }


    /*public double calculateProfit(Monster monster) {
        double expCoeff = expBase;
        if (turnsLeft * 1.0 / baseTurns >= early) {
            expCoeff = expEarly;
        } else if (turnsLeft * 1.0 / baseTurns <= late) {
            expCoeff = expLate;
        }
        double dist = (Math.pow(this.pos.x - monster.x, 2) + Math.pow(this.pos.y - monster.y, 2));
        double defeatTime = dist / this.s + monster.hp / this.p;
        if (defeatTime > turnsLeft) return 0;
        double profit = (monster.gold +  monster.exp * expCoeff) /  defeatTime;
        //profit *= (turnsLeft - defeatTime) / (turnsLeft);
        return profit;
    } */
}
