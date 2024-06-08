package jweekend;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Hero {
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

    double expBase;
    double expEarly;
    double expLate;
    double early;
    double late; 

    int baseTurns;
    int turnsLeft;
    Position pos;
    int level;
    int s;
    int p;
    int r;
    int expToUp;
    int gold;

    public Hero() {

    }
    public Hero(int baseS, int  baseP, int baseR, int speedC, int powerC, int rangeC) {
        this.baseS = baseS;
        this.baseP = baseP;
        this.baseR = baseR;
        this.speedC = speedC;
        this.powerC = powerC;
        this.rangeC = rangeC;

        this.s = baseS;
        this.p = baseP;
        this.r = baseR;
        this.level = 0;
        gold = 0;
        expToUp = 1000;
    }

    public void initHero(double[] params) {
        expBase = params[0];
        expEarly = params[1];
        expLate = params[2];
        early = params[3];
        late = params[4]; 

        s = baseS;
        p = baseP;
        r = baseR;
        this.level = 0;
        gold = 0;
        expToUp = 1000;
    }

    public void levelUp() {
        ++level;
        expToUp = 1000 + (level) * (level + 1) * 50;
        s = (int) (baseS * (1 + level * 1.0 * speedC / 100));
        p = (int) (baseP * (1 + level * 1.0 * powerC / 100));
        r = (int) (baseR * (1 + level * 1.0 * rangeC / 100));
    }

    public void addExp(int exp) {
        while (exp >= expToUp) {
            
            exp -= expToUp;
            levelUp();
        }
        expToUp -= exp;
    }

    public void kill(Monster monster, List<Turn> turns) {
        while (monster.hp > 0) {
            this.turnsLeft--;
            if (turnsLeft < 0) return;
            monster.hp -= p;
            Turn nexTurn = new Turn(monster.name);
            turns.add(nexTurn);
        }
        gold += monster.gold;
        addExp(monster.exp);
    }

    public void move(Monster monster, List<Turn> turns) {
        if (pos.canReach(this.r,new Position(monster.x, monster.y))) return;
        double numOfTurns = Math.sqrt(Math.pow(monster.x - this.pos.x, 2) + Math.pow(monster.y - this.pos.y, 2)) / s;
        int dx = (int) ((monster.x - this.pos.x) / numOfTurns);
        int dy = (int) ((monster.y - this.pos.y) / numOfTurns);
        while (numOfTurns > 1) {
            this.pos.move(dx, dy);
            this.turnsLeft--;
            if (turnsLeft <= 0) return;
            Turn nextTurn = new Turn(this.pos.x, this.pos.y);
            turns.add(nextTurn);
            if (pos.canReach(this.r,new Position(monster.x, monster.y))) return;
        }
        if (!pos.canReach(this.r,new Position(monster.x, monster.y))) {
            this.pos.x = monster.x;
            this.pos.y = monster.y;
            this.turnsLeft--;
            Turn nextTurn = new Turn(this.pos.x, this.pos.y);
            turns.add(nextTurn);            
        }
    }

    public double calculateProfit(Monster monster) {
        double expCoeff = expBase;
        if (turnsLeft * 1.0 / baseTurns >= early) {
            expCoeff = expEarly;
        } else if (turnsLeft * 1.0 / baseTurns <= late) {
            expCoeff = expLate;
        }
        double dist = (Math.pow(this.pos.x - monster.x, 2) + Math.pow(this.pos.y - monster.y, 2));
        double defeatTime = dist / this.s + monster.hp / this.p;
        if (defeatTime > turnsLeft) return 0;
        double profit = (monster.gold + monster.exp * expCoeff) /  defeatTime;
        profit *= (turnsLeft - defeatTime) / (turnsLeft);
        return profit;
    }

}
