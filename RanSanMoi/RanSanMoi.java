package ransanmoi;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class RanSanMoi extends JFrame {
    GameScreen game;

    public RanSanMoi() {
        setTitle("Rắn Săn Mồi - Pro Edition 2026");
        setSize(615, 440);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game = new GameScreen();
        add(game);
        setResizable(false);
        setLocationRelativeTo(null);
        game.setFocusable(true);
        game.addKeyListener(new DiChuyen());
        setVisible(true);
    }

    public static void main(String[] args) {
        new RanSanMoi();
    }

    private class DiChuyen implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();

            // Xử lý Menu
            if (GameScreen.currentState == GameScreen.STATE_MENU) {
                if (code == KeyEvent.VK_UP) game.menuSelection = (game.menuSelection - 1 + 7) % 7;
                if (code == KeyEvent.VK_DOWN) game.menuSelection = (game.menuSelection + 1) % 7;
                if (code == KeyEvent.VK_ENTER) thucThiMenu(game.menuSelection);
            }
            // Xử lý Chọn Skin
            else if (GameScreen.currentState == GameScreen.STATE_SKIN_SELECT) {
                if (code == KeyEvent.VK_LEFT) game.skinSelection = (game.skinSelection - 1 + 4) % 4;
                if (code == KeyEvent.VK_RIGHT) game.skinSelection = (game.skinSelection + 1) % 4;
                if (code == KeyEvent.VK_ENTER) {
                    GameScreen.skinIndex = game.skinSelection;
                    GameScreen.currentState = GameScreen.STATE_MENU;
                }
                if (code == KeyEvent.VK_ESCAPE) GameScreen.currentState = GameScreen.STATE_MENU;
            }
            // Xử lý Trong Game
            else {
                if (code == KeyEvent.VK_ESCAPE) GameScreen.currentState = GameScreen.STATE_MENU;

                if (GameScreen.currentState == GameScreen.STATE_PLAYING) {
                    // Ấn Space để bắt đầu
                    if (code == KeyEvent.VK_SPACE && !GameScreen.isGameEnd) {
                        GameScreen.isReady = true;
                    }

                    if (code == KeyEvent.VK_P) GameScreen.isPause = !GameScreen.isPause;

                    // Chỉ cho phép điều hướng khi đã ấn Space
                    if (GameScreen.isReady && !GameScreen.isPause) {
                        if (code == KeyEvent.VK_UP) game.ran.setVector(ConRan.GoUP);
                        if (code == KeyEvent.VK_DOWN) game.ran.setVector(ConRan.GoDown);
                        if (code == KeyEvent.VK_LEFT) game.ran.setVector(ConRan.GoLeft);
                        if (code == KeyEvent.VK_RIGHT) game.ran.setVector(ConRan.GoRight);
                    }
                }
            }
            game.repaint();
        }

        private void thucThiMenu(int select) {
            switch (select) {
                case 0: // Chơi tiếp
                    if (GameScreen.hasStartedOnce && !GameScreen.isGameEnd) {
                        GameScreen.currentState = GameScreen.STATE_PLAYING;
                        GameScreen.isReady = false; // Bắt đầu lại cần ấn Space
                    }
                    else JOptionPane.showMessageDialog(null, "Không có trận nào đang diễn ra!");
                    break;
                case 1: // Chơi mới
                    GameScreen.hasStartedOnce = true;
                    game.resetBg();
                    game.ran.resetGame();
                    GameScreen.diem = 0;
                    GameScreen.isGameEnd = false;
                    GameScreen.isReady = false; // Chờ ấn Space
                    GameScreen.currentState = GameScreen.STATE_PLAYING;
                    break;
                case 2: GameScreen.level = (GameScreen.level % 4) + 1; game.resetBg(); break;
                case 3: GameScreen.difficultyIndex = (GameScreen.difficultyIndex + 1) % 3; break;
                case 4: GameScreen.currentState = GameScreen.STATE_SKIN_SELECT; break;
                case 5: GameScreen.currentState = GameScreen.STATE_HIGH_SCORE; break;
                case 6: System.exit(0); break;
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {}
    }
}