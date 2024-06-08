package jweekend;

public class Position {
    int maxX;
    int maxY;
    int x;
    int y;

    public Position(int maxX, int maxY, int x, int y) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.x = x;
        this.y = y;
    }
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public boolean canReach(int range, Position next) {
        return Math.pow(next.x - x, 2) + Math.pow(next.y - y, 2) <= Math.pow(range, 2);
    }

    public int timeToReach(int range, Position next) {
        if (next.x < 0 || next.x > maxX || next.y < 0 || next.y > maxY)
            return -1;
            
        return (int) ((Math.pow(next.x - x, 2) + Math.pow(next.y - y, 2)) / Math.pow(range, 2));            
    }


}
