package codeweekend.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Hero {
    private static final int BASE_EXP = 1000;
    @JsonProperty("base_speed")
    private int baseS;
    @JsonProperty("base_power")
    private int baseP;
    @JsonProperty("base_range")
    private int baseR;
    @JsonProperty("level_speed_coeff")
    private int speedC;
    @JsonProperty("level_power_coeff")
    private int powerC;
    @JsonProperty("level_range_coeff")
    private int rangeC;

    private int x;
    private int y;
    private int level;
    private int s;
    private int p;
    private int r;
    private int expToUp;
    private int gold;
    private long fatique;
    private List<Turn> turns;

    public Hero() {

    }


    public Hero(int baseS, int baseP, int baseR, int speedC, int powerC, int rangeC, int x, int y, int level, int s, int p, int r, int expToUp, int gold, long fatique, List<Turn> turns) {
        this.baseS = baseS;
        this.baseP = baseP;
        this.baseR = baseR;
        this.speedC = speedC;
        this.powerC = powerC;
        this.rangeC = rangeC;
        this.x = x;
        this.y = y;
        this.level = level;
        this.s = s;
        this.p = p;
        this.r = r;
        this.expToUp = expToUp;
        this.gold = gold;
        this.fatique = fatique;
        this.turns = turns;
    }


    public void initBaseStats(int x, int y) {
        this.x = x;
        this.y = y;
        s = baseS;
        p = baseP;
        r = baseR;
        level = 0;
        gold = 0;
        expToUp = BASE_EXP;
        fatique = 0;
        turns = new ArrayList<>();
    }

    public void copy(Hero hero) {
        this.baseS = hero.getBaseS();
        this.baseP = hero.getBaseP();
        this.baseR = hero.getBaseR();
        this.speedC = hero.getSpeedC();
        this.powerC = hero.getPowerC();
        this.rangeC = hero.getRangeC();
    }

    public void earnGold(int gold) {
        this.gold += (gold * 1000) / (1000 + fatique);
    }

    public void levelUp() {
        ++level;
        expToUp = BASE_EXP + (level) * (level + 1) * 50;
        s = (int) (baseS + baseS * level  * (speedC * 1.0 / 100));
        p = (int) (baseP + baseP * level  * (powerC * 1.0 / 100));
        r = (int) (baseR + baseR * level * (rangeC * 1.0 / 100));
    }

    public void addExp(int exp) {
        while (exp >= expToUp) {
            
            exp -= expToUp;
            levelUp();
        }
        expToUp -= exp;
    }

    public void addFatique(int amount) {
        fatique += amount;
    }
 
    public void destroy(Monster monster, Game rules, boolean getFatique) {
        if (!rules.canReach(r, x, y, monster.getX(), monster.getY())) {
            moveToMonster(monster, rules, getFatique);
        }
        int hp = monster.getHp();
        int turnsSpent = 0;
        while (hp > 0) {
            ++turnsSpent;
            if (turnsSpent > rules.getTurnsLeft()) {
                rules.setTurnsLeft(0);
                return;
            }
            
            hp -= p;
            if (hp > 0 && getFatique) {
                takeDamage(rules);
                //fatique += rules.getDanger(x, y);
            }
            
            
            Turn nexTurn = new Turn(monster.getName());
            turns.add(nexTurn);
            
        }

        earnGold(monster.getGold());
        addExp(monster.getExp());
        
        if (getFatique) {
            rules.removeMonster(monster);
            takeDamage(rules);
        }

        //fatique += rules.getDanger(x, y);
        rules.subtractTurns(turnsSpent);
    }

    public void takeDamage(Game rules) {
        List<Monster> monsters = rules.getMonsters();
        for (int name : rules.getMonsterAlive()) {
            Monster m = monsters.get(name);
            double range = Math.sqrt(Math.pow(m.getX() - x, 2) + Math.pow(m.getY() - y, 2));
            if (range <= m.getRange()) {
                this.fatique += m.getAttack();
            }
        }
    }

    public boolean moveToMonster(Monster monster, Game rules, boolean getFatique) {
        return moveToPosition(monster.getX(), monster.getY(), rules, false, getFatique);
    }

    public boolean moveToPosition(int posX, int posY, Game rules, boolean stepIn, boolean getFatique) {
        if (!stepIn && rules.canReach(r, x, y, posX, posY)) return true;

        double numOfTurns = rules.calcRange(x, y, posX, posY) / this.s;
        int dx = (int) ((posX - x) / numOfTurns);
        int dy = (int) ((posY - y) / numOfTurns);

        int turnsSpent = 0;
        while (numOfTurns-- > 1) {
            move(dx, dy, rules);
            if (getFatique) {
                takeDamage(rules);
            }
                
            ++turnsSpent;
            if (turnsSpent >= rules.getTurnsLeft()) {
                rules.setTurnsLeft(0);
                return false;
            }

            Turn nextTurn = new Turn(x, y);
            turns.add(nextTurn);
            if (!stepIn && rules.canReach(r, x, y,posX, posY)) {
                rules.subtractTurns(turnsSpent);
                return true;
            }
        }
        if (posX!= x || posY != y) {
            if (rules.canReach(s, x, y, posX, posY)) {
                setPosition(posX, posY);

                if (getFatique) {
                    takeDamage(rules);
                }

                ++turnsSpent;
                if (turnsSpent > rules.getTurnsLeft()) {
                    rules.setTurnsLeft(0);
                    return false;
                }     
                Turn nextTurn = new Turn(x, y);
                turns.add(nextTurn); 
            }                
        }
        
        rules.subtractTurns(turnsSpent);
        return true;
    }

    public void move(int dx, int dy, Game rules) {
        x = rules.adjustPosition(x, dx, 'w');
        y = rules.adjustPosition(y, dy, 'h');
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public long movementFatique(int posX, int posY, Game rules) {

        if (posX == x && posY == y) return 0;

        long fate = rules.getDanger(posX, posY);
        double numOfTurns = rules.calcRange(x, y, posX, posY) / s;
        int dx = (int) (( posX - x ) / numOfTurns);
        int dy = (int) (( posY - y ) / numOfTurns);

        int curX = this.x;
        int curY = this.y;
        while (numOfTurns-- > 1) {
            curX = rules.adjustPosition(curX, dx, 'w');
            curY = rules.adjustPosition(curY, dy, 'h'); 
            fate += rules.getDanger(curX, curY);
        }  
        
        return fate;
    }

    public Boolean moveToAimAtPosition(Game rules, int tx, int ty, int evadeRange) {
        if (x == tx && y == ty) return false;
        double numOfTurns = Math.sqrt(Math.pow(tx - x, 2) + Math.pow(ty - y, 2)) / s;
        if (numOfTurns < 1) numOfTurns = 1;
        if (numOfTurns > rules.getTurnsLeft()) return false;
        int dx = (int) ((tx - x) / numOfTurns);
        int dy = (int) ((ty - y) / numOfTurns);

        if (rules.canReach(r,x, y, tx, ty) || rules.canReach(evadeRange,x, y, tx, ty)) return true;

        while (numOfTurns-- > 1) {
            int proposed_dx = dx;
            int proposed_dy = dy;
            while(rules.canReach(evadeRange, x + proposed_dx, y + proposed_dy, tx, ty)) {
                if (proposed_dx == 0 && proposed_dy == 0) break;
                if (Math.abs(proposed_dx) > Math.abs(proposed_dy)) {
                    if (proposed_dx > 0) {
                        --proposed_dx;
                    } else {
                        ++proposed_dx;
                    }
                } else {
                    if (proposed_dy > 0) {
                        --proposed_dy;
                    } else {
                        ++proposed_dy;
                    }
                }
            }
            move(proposed_dx, proposed_dy, rules);
            rules.subtractTurns(1);
            if (rules.getTurnsLeft() <= 0) return false;
            Turn nextTurn = new Turn(x, y);
            turns.add(nextTurn);
            if (rules.canReach(r,x, y, tx, ty)) return true;
        }
        if (rules.canReach(s, x, y, tx, ty)) {
            int proposed_dx = tx - x;
            int proposed_dy = ty - y;
            while(rules.canReach(evadeRange, x + proposed_dx, y + proposed_dy, tx, ty)) {
                int new_dx = proposed_dx;
                int new_dy = proposed_dy;
                if (proposed_dx == 0 && proposed_dy == 0) break;
                if (Math.abs(proposed_dx) > Math.abs(proposed_dy)) {
                    if (proposed_dx > 0) {
                        --proposed_dx;
                    } else {
                        ++proposed_dx;
                    }
                } else {
                    if (proposed_dy > 0) {
                        --proposed_dy;
                    } else {
                        ++proposed_dy;
                    }
                }
                if (!rules.canReach(r, x + proposed_dx, y + proposed_dy, tx, ty)) {
                    proposed_dx = new_dx;
                    proposed_dy = new_dy;
                    break;
                }
            }
            move(proposed_dx, proposed_dy, rules);

            if (!rules.canReach(r, x, y, tx, ty)) {
                System.out.println("can't reach " + (rules.totalTurns - rules.getTurnsLeft()));
            }
            // hero.x = x;
            // hero.y = y;
            takeDamage(rules);
            rules.subtractTurns(1);
            if (rules.getTurnsLeft() < 0) return false;
            Turn nextTurn = new Turn(x, y);
            turns.add(nextTurn);                   
        }
        return true;
    }

    public Hero clone() {
        return new Hero(baseS, baseP, baseR, speedC, powerC, rangeC, x, y, level, s, p, r, expToUp, gold, fatique, new ArrayList<Turn>());
    }

    public void clearTurns() {
        turns.clear();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getLevel() {
        return this.level;
    }

    public int getS() {
        return this.s;
    }


    public int getP() {
        return this.p;
    }

    public int getR() {
        return this.r;
    }

    public int getGold() {
        return this.gold;
    }

    public long getFatique() {
        return this.fatique;
    }

    public List<Turn> getTurns() {
        return this.turns;
    }


    public int getBaseS() {
        return this.baseS;
    }

    public int getBaseP() {
        return this.baseP;
    }
    public int getBaseR() {
        return this.baseR;
    }

    public int getSpeedC() {
        return this.speedC;
    }

    public int getPowerC() {
        return this.powerC;
    }

    public int getRangeC() {
        return this.rangeC;
    }
}
