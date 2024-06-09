package jweekend;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

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
    public int gold;

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

    public void destroy(Monster monster, List<Turn> turns) {
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

    public int clear(PriorityQueue<Integer> pq, List<Turn> turns, int[][] monsterMap, List<Monster> monsters, int[] value, int[][] heatMap) {
        int baseValue = value[0];
        int res = 0;
        while (pq.size() > 0) {
            int target = pq.poll();
            Monster m = monsters.get(target);
            int hp = m.hp;
            if (hp / this.p > turnsLeft)
                return res;
            if (m.exp > this.expToUp && turnsLeft > 0.1 * baseTurns) {
                if (Math.random() * value[0] / baseValue < 0.8) return res;
            }
            while (hp > 0) {
                this.turnsLeft--;
                if (turnsLeft <= 0) return res;
                hp -= this.p;
                Turn nexTurn = new Turn(m.name);
                turns.add(nexTurn);                
            }
            ++res;
            monsterMap[m.x][m.y] = -1;
            gold += m.gold;
            value[0] -= m.value(this.turnsLeft * 1.0 / this.baseTurns);
            //heatMap[value[1]][value[2]] -= m.gold + m.exp;
            addExp(m.exp);
        }
        return res;

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
        if (pos.canReach(this.r,new Position(monster.x, monster.y))) {
            this.pos.x = monster.x;
            this.pos.y = monster.y;
            this.turnsLeft--;
            if (turnsLeft <= 0) return;
            Turn nextTurn = new Turn(this.pos.x, this.pos.y);
            turns.add(nextTurn);            
        }
    }

    public void moveToPosition(Position position, List<Turn> turns, int[][] wealth) {
        if (this.pos.x == position.x && this.pos.y == position.y) return;
        double numOfTurns = Math.sqrt(Math.pow(position.x - this.pos.x, 2) + Math.pow(position.y - this.pos.y, 2)) / s;
        int dx = (int) ((position.x - this.pos.x) / numOfTurns);
        int dy = (int) ((position.y - this.pos.y) / numOfTurns);

        while (numOfTurns-- > 1) {
            this.pos.move(dx, dy);
            this.turnsLeft--;
            if (turnsLeft <= 0) return;
            Turn nextTurn = new Turn(this.pos.x, this.pos.y);
            turns.add(nextTurn);
            if (wealth[this.pos.x][this.pos.y] * (Math.random() + 0.5) > wealth[position.x][position.y]) {
                return;
            }
        }
        if (this.pos.x != position.x && this.pos.y != position.y && this.pos.canReach(this.s, position)) {
            this.pos.x = position.x;
            this.pos.y = position.y;     
            this.turnsLeft--;
            if (turnsLeft < 0) return;
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
        double profit = (monster.gold +  monster.exp * expCoeff) /  defeatTime;
        //profit *= (turnsLeft - defeatTime) / (turnsLeft);
        return profit;
    }

}
