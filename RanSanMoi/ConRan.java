package ransanmoi;

import java.awt.*;
import java.util.Random;

public class ConRan {
    int doDai = 3;
    int[] x = new int[400], y = new int[400];
    public static int GoUP = 1, GoDown = -1, GoRight = 2, GoLeft = -2;
    int vector = GoDown;

    public ConRan() { resetGame(); }

    public void resetGame() {
        doDai = 3;
        x[0] = 5; y[0] = 5;
        x[1] = 5; y[1] = 4;
        x[2] = 5; y[2] = 3;
        vector = GoDown;
    }

    public void setVector(int v) { if (vector != -v) vector = v; }

    public int getSpeed() {
        int base = (GameScreen.difficultyIndex == 0) ? 180 : (GameScreen.difficultyIndex == 1) ? 110 : 65;
        return Math.max(30, base - (GameScreen.diem / 50) * 5);
    }

    public void update() {
        for (int i = doDai - 1; i > 0; i--) { x[i] = x[i - 1]; y[i] = y[i - 1]; }
        if (vector == GoUP) y[0]--; if (vector == GoDown) y[0]++;
        if (vector == GoLeft) x[0]--; if (vector == GoRight) x[0]++;

        if (x[0] < 0) x[0] = 19; if (x[0] > 19) x[0] = 0;
        if (y[0] < 0) y[0] = 19; if (y[0] > 19) y[0] = 0;

        if (GameScreen.bg[x[0]][y[0]] == 3) { GameScreen.isGameEnd = true; return; }
        for (int i = 1; i < doDai; i++) if (x[0] == x[i] && y[0] == y[i]) { GameScreen.isGameEnd = true; return; }

        if (GameScreen.bg[x[0]][y[0]] == 2) {
            doDai++; GameScreen.bg[x[0]][y[0]] = 0;
            GameScreen.diem += 10;
            taoMoiTrongRan();
        }
    }

    private void taoMoiTrongRan() {
        Random r = new Random();
        while (true) {
            int nx = r.nextInt(20), ny = r.nextInt(20);
            if (GameScreen.bg[nx][ny] == 0) {
                boolean trungRan = false;
                for (int i = 0; i < doDai; i++) if (x[i] == nx && y[i] == ny) trungRan = true;
                if (!trungRan) { GameScreen.bg[nx][ny] = 2; break; }
            }
        }
    }

    public void veRan(Graphics g) {
        for (int i = 0; i < doDai; i++) veNhanVatMau(g, x[i] * 20, y[i] * 20, GameScreen.skinIndex, i == 0);
    }

    public void veNhanVatMau(Graphics g, int px, int py, int type, boolean isHead) {
        Graphics2D g2d = (Graphics2D) g;
        switch (type) {
            case 0: // Angry Bird
                g2d.setColor(new Color(210, 30, 30)); g2d.fillOval(px, py, 20, 20);
                if (isHead) {
                    g2d.setColor(Color.WHITE); g2d.fillArc(px+2, py+10, 16, 8, 0, -180);
                    g2d.setColor(Color.ORANGE); int[] ox={px+8,px+12,px+10}; int[] oy={py+8,py+8,py+13}; g2d.fillPolygon(ox,oy,3);
                    g2d.setColor(Color.BLACK); g2d.fillOval(px+4, py+4, 4, 4); g2d.fillOval(px+11, py+4, 4, 4);
                }
                break;
            case 1: // Minion
                g2d.setColor(new Color(255, 215, 0)); g2d.fillRoundRect(px+1, py, 18, 20, 10, 10);
                if (isHead) {
                    g2d.setColor(Color.GRAY); g2d.fillOval(px+3, py+4, 14, 8);
                    g2d.setColor(Color.WHITE); g2d.fillOval(px+5, py+5, 4, 6); g2d.fillOval(px+11, py+5, 4, 6);
                    g2d.setColor(Color.BLACK); g2d.fillOval(px+6, py+7, 2, 2); g2d.fillOval(px+12, py+7, 2, 2);
                } else { g2d.setColor(new Color(30, 80, 160)); g2d.fillRect(px+1, py+12, 18, 8); }
                break;
            case 2: // Creeper
                g2d.setColor(new Color(0, 170, 0)); g2d.fillRect(px+1, py+1, 18, 18);
                if (isHead) {
                    g2d.setColor(Color.BLACK); g2d.fillRect(px+4, py+4, 4, 4); g2d.fillRect(px+12, py+4, 4, 4);
                    g2d.fillRect(px+8, py+8, 4, 4); g2d.fillRect(px+6, py+10, 2, 6); g2d.fillRect(px+12, py+10, 2, 6);
                }
                break;
            case 3: // Baymax
                g2d.setColor(Color.WHITE); g2d.fillOval(px+1, py+1, 18, 18);
                if (isHead) {
                    g2d.setColor(Color.BLACK); g2d.fillOval(px+5, py+8, 3, 3); g2d.fillOval(px+12, py+8, 3, 3);
                    g2d.drawLine(px+6, py+9, px+14, py+9);
                }
                break;
        }
    }
}