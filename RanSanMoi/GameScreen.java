package ransanmoi;

import javax.swing.JPanel;
import java.awt.*;
import java.io.*;
import java.util.*;

public class GameScreen extends JPanel implements Runnable {
    static int[][] bg = new int[20][20];
    static boolean isGameEnd = false, isPause = false, hasStartedOnce = false, hasSaved = false, isReady = false;
    static int level = 1, diem = 0;

    static final int STATE_MENU = 0, STATE_PLAYING = 1, STATE_SKIN_SELECT = 2, STATE_HIGH_SCORE = 3;
    static int currentState = STATE_MENU;

    int menuSelection = 0, skinSelection = 0;
    static int difficultyIndex = 1, skinIndex = 0;

    static ArrayList<Integer> highScores = new ArrayList<>();
    private final String FILE_NAME = "highscores.txt";

    ConRan ran;
    Thread thread;

    public GameScreen() {
        setPreferredSize(new Dimension(600, 400));
        ran = new ConRan();
        loadScores();
        resetBg();
        thread = new Thread(this);
        thread.start();
    }

    public void loadScores() {
        highScores.clear();
        File f = new File(FILE_NAME);
        if(!f.exists()) { for(int i=0; i<3; i++) highScores.add(0); return; }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) highScores.add(Integer.parseInt(line.trim()));
        } catch (Exception e) { while (highScores.size() < 3) highScores.add(0); }
        Collections.sort(highScores, Collections.reverseOrder());
    }

    public void saveScore(int s) {
        highScores.add(s);
        Collections.sort(highScores, Collections.reverseOrder());
        while (highScores.size() > 3) highScores.remove(3);
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int score : highScores) pw.println(score);
        } catch (Exception e) {}
    }

    public void resetBg() {
        for (int i = 0; i < 20; i++) for (int j = 0; j < 20; j++) bg[i][j] = 0;
        if (level == 2) { for (int i = 5; i < 15; i++) bg[i][10] = 3; }
        else if (level == 3) {
            for (int i = 4; i < 7; i++) {
                bg[i][4] = 3; bg[i+9][4] = 3;
                bg[i][15] = 3; bg[i+9][15] = 3;
            }
        } else if (level == 4) {
            for (int i = 4; i < 16; i++) {
                bg[i][4] = 3; bg[i][15] = 3;
                bg[4][i] = 3; bg[15][i] = 3;
            }
            bg[10][4] = 0; bg[10][15] = 0;
        }
        taoMoi();
        hasSaved = false;
        isReady = false; // Reset trạng thái sẵn sàng
    }

    public void taoMoi() {
        Random r = new Random();
        while (true) {
            int nx = r.nextInt(20), ny = r.nextInt(20);
            if (bg[nx][ny] == 0) {
                boolean trungRan = false;
                for (int i = 0; i < ran.doDai; i++) if (ran.x[i] == nx && ran.y[i] == ny) trungRan = true;
                if (!trungRan) { bg[nx][ny] = 2; break; }
            }
        }
    }

    public void run() {
        long t = 0;
        while (true) {
            if (currentState == STATE_PLAYING && !isPause && !isGameEnd && isReady) {
                if (System.currentTimeMillis() - t > ran.getSpeed()) {
                    t = System.currentTimeMillis();
                    ran.update();
                    if (isGameEnd && !hasSaved) { saveScore(diem); hasSaved = true; }
                }
            }
            repaint();
            try { Thread.sleep(20); } catch (Exception e) {}
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (currentState == STATE_MENU) veMenu(g2d);
        else if (currentState == STATE_SKIN_SELECT) veChonSkin(g2d);
        else if (currentState == STATE_HIGH_SCORE) veBangDiem(g2d);
        else veGame(g2d);
    }

    private void veMenu(Graphics2D g) {
        g.setColor(Color.BLACK); g.fillRect(0, 0, 600, 400);
        g.setColor(Color.GREEN); g.setFont(new Font("SansSerif", Font.BOLD, 40));
        g.drawString("SNAKE MENU", 170, 60);
        String dStr = (difficultyIndex == 0) ? "DỄ" : (difficultyIndex == 1) ? "VỪA" : "KHÓ";
        String[] opts = { "CHƠI TIẾP", "CHƠI MỚI", "MÀN CHƠI: " + level, "ĐỘ KHÓ: " + dStr, "CHỌN SKIN", "XEM ĐIỂM CAO", "THOÁT" };
        for (int i = 0; i < opts.length; i++) {
            g.setFont(new Font("SansSerif", Font.PLAIN, 20));
            if (i == menuSelection) {
                g.setColor(Color.YELLOW); g.drawString(">> " + opts[i] + " <<", 180, 115 + i * 38);
            } else {
                g.setColor(Color.WHITE); g.drawString(opts[i], 210, 115 + i * 38);
            }
        }
    }

    private void veChonSkin(Graphics2D g) {
        g.setColor(Color.BLACK); g.fillRect(0, 0, 600, 400);
        g.setColor(Color.CYAN); g.setFont(new Font("SansSerif", Font.BOLD, 30));
        g.drawString("CHỌN NHÂN VẬT", 180, 60);
        String[] names = {"Angry Bird", "Minion", "Creeper", "Baymax"};
        for (int i = 0; i < 4; i++) {
            int x = 40 + i * 140;
            g.setColor(i == skinSelection ? Color.YELLOW : Color.DARK_GRAY);
            g.drawRoundRect(x, 110, 120, 180, 15, 15);
            ran.veNhanVatMau(g, x + 50, 180, i, true);
            g.setColor(Color.WHITE); g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString(names[i], x + 20, 260);
        }
    }

    private void veBangDiem(Graphics2D g) {
        g.setColor(Color.BLACK); g.fillRect(0, 0, 600, 400);
        g.setColor(Color.ORANGE); g.setFont(new Font("SansSerif", Font.BOLD, 35));
        g.drawString("TOP 3 ĐIỂM CAO", 160, 80);
        for (int i = 0; i < highScores.size(); i++) {
            g.setColor(Color.WHITE); g.setFont(new Font("SansSerif", Font.PLAIN, 25));
            g.drawString("TOP " + (i+1) + " : " + highScores.get(i), 220, 160 + i * 50);
        }
        g.setFont(new Font("SansSerif", Font.ITALIC, 15)); g.drawString("ESC để quay lại", 230, 350);
    }

    private void veGame(Graphics2D g) {
        g.setColor(Color.BLACK); g.fillRect(0, 0, 400, 400);
        g.setColor(new Color(40, 40, 40));
        for (int i = 0; i <= 400; i += 20) { g.drawLine(i, 0, i, 400); g.drawLine(0, i, 400, i); }
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                if (bg[i][j] == 2) { g.setColor(Color.CYAN); g.fillOval(i * 20 + 5, j * 20 + 5, 10, 10); }
                else if (bg[i][j] == 3) { g.setColor(new Color(200, 50, 50)); g.fill3DRect(i * 20 + 1, j * 20 + 1, 18, 18, true); }
            }
        }
        ran.veRan(g);

        // Bảng thông tin bên phải
        g.setColor(new Color(30,30,30)); g.fillRect(400, 0, 200, 400);
        g.setColor(Color.WHITE); g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("ĐIỂM: " + diem, 420, 60);
        g.drawString("MAP: " + level, 420, 95);
        String d = (difficultyIndex == 0) ? "Dễ" : (difficultyIndex == 1) ? "Vừa" : "Khó";
        g.drawString("TỐC ĐỘ: " + d, 420, 130);

        if (isGameEnd) {
            g.setColor(Color.RED); g.setFont(new Font("SansSerif", Font.BOLD, 22));
            g.drawString("GAME OVER", 420, 200);
        }

        // Thông báo ấn SPACE để bắt đầu
        if (!isReady && !isGameEnd && currentState == STATE_PLAYING) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(50, 170, 300, 60);
            g.setColor(Color.YELLOW);
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            g.drawString("ẤN 'SPACE' ĐỂ BẮT ĐẦU", 95, 208);
        }
    }
}