package codeweekend.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameNew {
    public final int width;
    public final int height;
    public final int totalTurns;
    public final int testNum;

    private int turnsLeft;
    private int maxGold;

    private Set<Integer> monsterAlive;
    private List<MonsterNew> monsters;
    public HeroNew hero;

    private long[][] dangerMap;
    private int[][][] profitMap;
    private boolean[][] visitedMap;
    private int[][] monsterMap;

    public GameNew(int testNum, int width, int height, int maxTurns) {
        this.testNum = testNum;
        this.width = width;
        this.height = height;
        this.totalTurns = maxTurns;
        this.turnsLeft = maxTurns;
    }



    public GameNew(int width, int height, int maxTurns, int testNum, int turnsLeft, int maxGold, Set<Integer> monsterAlive, List<MonsterNew> monsters) {
        this.width = width;
        this.height = height;
        this.totalTurns = maxTurns;
        this.testNum = testNum;
        this.turnsLeft = turnsLeft;
        this.maxGold = maxGold;
        this.monsterAlive = monsterAlive;
        this.monsters = monsters;
        //init(monsters);
    }


    public void init(List<MonsterNew> monsters) {
        this.monsters = monsters;
        monsterAlive = new HashSet<>();
        for (MonsterNew monster : monsters) {
            monsterAlive.add(monster.getName());
        }
        dangerMap = new long[width + 1][height + 1];
        loadMonsters();
        loadProfit(maxGold, height);
    }

    public void loadMonsters() {
        visitedMap = new boolean[width + 1][height + 1];
        monsterMap = new int[width+1][height+1];
        for (int i = 0; i <= width; ++i) {
            Arrays.fill(monsterMap[i], -1);
        }

        for (Integer name : monsterAlive) {
            
            MonsterNew m = monsters.get(name);
            monsterMap[m.getX()][m.getY()] = name;
            for (int i = Math.max(0, m.getX() - m.getRange()); i <= Math.min(width, m.getX() + m.getRange()); ++i) {
                for (int j = Math.max(0, m.getY() - m.getRange()); j <= Math.min(height, m.getY() + m.getRange()); ++j) {

                    double range = calcRange(m.getX(), m.getY(), i, j);

                    if (range <= m.getRange()) {
                        dangerMap[i][j] += m.getAttack();
                    }  
                }
            }
            //maxGold = Math.max(maxGold, m.gold);
        }
    }

    
    public void clearDanger(MonsterNew m) {
        monsterAlive.remove(m.getName());
        for (int i = Math.max(0, m.getX() - m.getRange()); i <= Math.min(width, m.getX() + m.getRange()); ++i) {
            for (int j = Math.max(0, m.getY() - m.getRange()); j <= Math.min(height, m.getY() + m.getRange()); ++j) {
                double range = calcRange(m.getX(), m.getY(), i, j);
                if (range <= m.getRange()) {
                    if (dangerMap[i][j] >= m.getAttack()) dangerMap[i][j] -= m.getAttack();
                }          
            }
        }        
    }

    public void loadProfit(int range, int power) {
        profitMap = new int[width + 1][height + 1][3]; 
        for (Integer name : monsterAlive) {
            MonsterNew m = monsters.get(name);
            for (int i = Math.max(0, m.getX() - range); i <= Math.min(width, m.getX() + range); ++i) {
                for (int j = Math.max(0, m.getY() - range); j <= Math.min(height, m.getY() + range); ++j) {
                    double dist = calcRange(i, j, m.getX(), m.getY());
                    if (dist <= range) {
                        if (m.getHp() / power < 100) {
                            profitMap[i][j][0] += m.getGold();
                            profitMap[i][j][1] += m.getExp();
                            profitMap[i][j][2] += (m.getHp() + power) / power;
                        }
                    }           
                }
            }
        }
    }



    public boolean inRange(int x, int y) {
        return x >= 0 && y >= 0 && x <= width && y <= height;
    }

    public boolean canReach(int range, int x1, int y1, int x2, int y2) {
        return inRange(x1, y1) && calcRange(x1, y1, x2, y2) <= range;
    }

    public double calcRange(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public int adjustPosition(int x, int dx, char axis) {
        x += dx;
        if (x < 0) x = 0;
        if (axis == 'w' && x > width) {
            x = width;
        } else if (axis == 'h' && x > height) {
            x = height;
        }

        return x;
    }

    public void subtractTurns(int turns) {
        this.turnsLeft -= turns;
    }

    public int[][] selectRandomPositions(int n) {

        boolean[][] used = new boolean[width+1][height+1];
        used[0][0] = true;
        if (n > height * width / 10) n = height * width /10;
        int[][] targets = new int[n][2];
        for (int i = 0; i < n; ++i) {
            int x = 0;
            int y = 0;
            while (used[x][y]) {
                x = (int) (Math.random()*width);
                y = (int) (Math.random()*height);
            }

            used[x][y] = true;
            targets[i][0] = x;
            targets[i][1] = y;
        }

        return targets;
    }

    public GameNew clone() {
        return new GameNew(width, height, totalTurns, testNum, turnsLeft, maxGold, monsterAlive, monsters);

    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getTotalTurns() {
        return this.totalTurns;
    }


    public int getTestNum() {
        return this.testNum;
    }

    public int getTurnsLeft() {
        return this.turnsLeft;
    }

    public void setTurnsLeft(int turnsLeft) {
        this.turnsLeft = turnsLeft;
    }

    public long getDanger(int x, int y) {
        return dangerMap[x][y];
    }

    public int[] getProfit(int x, int y) {
        return profitMap[x][y];
    }

    public boolean isVisited(int x, int y) {
        return visitedMap[x][y];
    }

    public void setVisited(int x, int y) {
        visitedMap[x][y] = true;
    }

    public int getMonster(int x, int y) {
        return monsterMap[x][y];
    }

    public void removeMonster(MonsterNew m) {
        monsterMap[m.getX()][m.getY()] = -1;
        clearDanger(m);
    }

    public long getDangerFromClearing(int x, int y) {
        return dangerMap[x][y] * profitMap[x][y][2];
    }

    public Set<Integer> getMonsterAlive() {
        return monsterAlive;
    }

    public List<MonsterNew> getMonsters() {
        return monsters;
    }

    public HeroNew getHero() {
        return hero;
    }


}
