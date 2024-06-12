package jweekend.v2;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    public int level;
    public int s;
    public int p;
    public int r;
    public int expToUp;
    public double gold;
    public long fatique;

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
        this.gold += (gold * 1000) / (1000 + this.fatique);
    }

    public void addExp(int exp) {
        while (exp >= expToUp) {
            
            exp -= expToUp;
            levelUp();
        }
        expToUp -= exp;
    }

    public int destroyMonster(Monster2 monster, List<Turn> turns, int turnsLeft, Set<Integer> monsterAlive, List<Monster2> monsters, long[][] dangerMap) {
        int hp = monster.hp;
        while (hp > 0) {
            turnsLeft--;
            if (turnsLeft < 0) return turnsLeft;
            hp -= p;
            if (hp > 0) {
                this.fatique += dangerMap[this.x][this.y];
                //takeDamage(monsterAlive, monsters);
            }
            Turn nexTurn = new Turn(monster.name);
            turns.add(nexTurn);
        }
        monsterAlive.remove(monster.name);
        earnGold(monster.gold);
        addExp(monster.exp);
        this.fatique += dangerMap[this.x][this.y];
        //takeDamage(monsterAlive, monsters);
        return turnsLeft;
    }

    public void takeDamage(Set<Integer> monsterAlive, List<Monster2> monsters) {
        for (int name : monsterAlive) {
            Monster2 m = monsters.get(name);
            double range = Math.sqrt(Math.pow(m.x - x, 2) + Math.pow(m.y - y, 2));
            if (range <= m.range) {
                this.fatique += m.attack;
            }
        }
    }


    public double calculateProfit(Monster2 monster, int turnsLeft, int totalTurns, int x, int y, long[][] dangerMap) {
        double expCoeff = 1.0;
        if (turnsLeft * 1.0 / totalTurns >= 0.7) {
            expCoeff = 10.0;
        } else if (turnsLeft * 1.0 / totalTurns <= 0.2) {
            expCoeff = 0.1;
        } else if (turnsLeft * 1.0 / totalTurns <= 0.1) {
            expCoeff = 0;
        }

        double range = Math.sqrt(Math.pow(x - monster.x, 2) + Math.pow(y - monster.y, 2));
        
        double defeatTime = monster.hp / this.p;
        if (defeatTime > turnsLeft) return 0;
        double profit = (monster.gold +  monster.exp * expCoeff) /  (defeatTime * dangerMap[x][y]);

        double dangerCoeff = 10.0;
        if (dangerMap[x][y] / monster.gold > dangerCoeff) {
            profit = 0;
        }
        //profit *= (turnsLeft - defeatTime) / (turnsLeft);
        return profit;
    } 

    public double calculateProfit(int[] values,  int turnsLeft, int totalTurns, int x, int y, long[][] dangerMap) {
        double expCoeff = 1.0;
        if (turnsLeft * 1.0 / totalTurns >= 0.7) {
            expCoeff = 10.0;
        } else if (turnsLeft * 1.0 / totalTurns <= 0.2) {
            expCoeff = 0.1;
        } else if (turnsLeft * 1.0 / totalTurns <= 0.1) {
            expCoeff = 0;
        }
        //if (values[1] > expToUp) expCoeff *= 20;

        double range = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
        
        if (values[2] > turnsLeft) return 0;
        double profit;
        // double profit = (values[0] +  values[1] * expCoeff) /  (1000 + values[2] * dangerMap[x][y]);

        double dangerCoeff = 1000.0;

        long danger = dangerMap[x][y];
        if (danger > 0) {
            profit = 0;
        } else {
            profit = range;
        }

        

        if (danger > 1E6) {
            profit = 0;
        } else {
            profit = (values[0] + values[1]);
        }

        // if (profit != 0 && dangerMap[x][y] / profit > dangerCoeff) {
        //     profit = 0;
        // }
        //profit *= (turnsLeft - defeatTime) / (turnsLeft);
        return profit;
    } 

    public long movementFatique(int x, int y, long[][] dangerMap) {

        long fate = dangerMap[x][y];
        int posX = this.x;
        int posY = this.y;

        if (posX == x && posY == y) return 0;
        double numOfTurns = Math.sqrt(Math.pow(x - posX, 2) + Math.pow(y - posY, 2)) / this.s;
        int dx = (int) ((x - posX) / numOfTurns);
        int dy = (int) ((y - posY) / numOfTurns);

        while (numOfTurns-- > 1) {

            posX += dx;
            posY += dy;
            if (posX < 0) posX = 0;
            if (posX >= dangerMap.length) posX = dangerMap.length - 1;
            if (posY < 0) posY = 0;
            if (posY >= dangerMap[0].length) posY = dangerMap[0].length - 1;   
            fate += dangerMap[posX][posY];
        }  
        
        return fate;
    }


}
