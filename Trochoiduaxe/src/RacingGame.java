import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

// Main class
public class RacingGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Racing Game");  // Tạo cửa sổ trò chơi
        GamePanel panel = new GamePanel();  // Tạo một panel (khu vực vẽ trò chơi)

        frame.setBounds(10, 10, 800, 600);  // Đặt vị trí và kích thước cửa sổ (800x600 pixels)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Đóng cửa sổ sẽ thoát khỏi ứng dụng
        frame.setResizable(false);  // Cấm thay đổi kích thước cửa sổ
        frame.add(panel);  // Thêm panel trò chơi vào cửa sổ
        frame.setVisible(true);  // Hiển thị cửa sổ trò chơi lên màn hình
    }
}

// Game Panel
class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final int CAR_WIDTH = 50;  // Chiều rộng của xe
    private final int CAR_HEIGHT = 100;  // Chiều cao của xe
    private final int OBSTACLE_WIDTH = 50;  // Chiều rộng của chướng ngại vật
    private final int OBSTACLE_HEIGHT = 100;  // Chiều cao của chướng ngại vật

    private int carX = 375;  // Vị trí X của xe (ở giữa màn hình)
    private int carY = 500;  // Vị trí Y của xe (gần đáy màn hình)

    private final ArrayList<Rectangle> obstacles = new ArrayList<>();  // Danh sách chứa các chướng ngại vật (dạng hình chữ nhật)
    private final Random random = new Random();  // Tạo đối tượng Random để sinh các giá trị ngẫu nhiên

    private Timer timer;  // Đối tượng Timer dùng để cập nhật trạng thái trò chơi
    private int score = 0;  // Điểm số của người chơi
    private boolean running = true;  // Biến kiểm tra trạng thái trò chơi (đang chạy hay kết thúc)

    // Constructor của GamePanel, nơi tạo giao diện trò chơi
    public GamePanel() {
        this.setPreferredSize(new Dimension(800, 600));  // Đặt kích thước của panel trò chơi
        this.setBackground(Color.gray);  // Đặt màu nền của panel là màu xám
        this.setFocusable(true);  // Cho phép panel nhận sự kiện từ bàn phím
        this.addKeyListener(this);  // Thêm listener để nghe sự kiện bàn phím

        timer = new Timer(20, this);  // Tạo một Timer với tần suất 20ms (cập nhật mỗi 20ms)
        timer.start();  // Bắt đầu Timer ngay khi trò chơi bắt đầu
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);  // Gọi phương thức paintComponent của lớp cha để vẽ lại mọi thứ

        if (running) {
            // Vẽ đường đua
            g.setColor(Color.darkGray);  // Đặt màu cho đường đua
            g.fillRect(200, 0, 400, 600);  // Vẽ một hình chữ nhật (đường đua)

            g.setColor(Color.white);  // Đặt màu cho vạch đường giữa
            int i = 0;
            while (i < 600) {
                g.fillRect(395, i, 10, 20);  // Vẽ vạch giữa đường
                i += 40;  // Mỗi vạch cách nhau 40 pixels
            }

            // Vẽ xe
            g.setColor(Color.blue);  // Đặt màu cho xe
            g.fillRect(carX, carY, CAR_WIDTH, CAR_HEIGHT);  // Vẽ xe tại vị trí (carX, carY)

            // Vẽ chướng ngại vật
            g.setColor(Color.red);  // Đặt màu cho chướng ngại vật
            for (Rectangle obstacle : obstacles) {
                g.fillRect(obstacle.x, obstacle.y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);  // Vẽ từng chướng ngại vật
            }

            // Vẽ điểm
            g.setColor(Color.white);  // Đặt màu cho điểm
            g.setFont(new Font("Arial", Font.BOLD, 20));  // Đặt font cho điểm số
            g.drawString("Score: " + score, 10, 30);  // Vẽ điểm số ở góc trên bên trái
        } else {
            // Màn hình Game Over
            g.setColor(Color.red);  // Đặt màu đỏ cho chữ "GAME OVER"
            g.setFont(new Font("Arial", Font.BOLD, 50));  // Đặt font cho chữ "GAME OVER"
            g.drawString("GAME OVER", 250, 300);  // Vẽ chữ "GAME OVER" ở giữa màn hình

            g.setFont(new Font("Arial", Font.PLAIN, 20));  // Đặt font cho hướng dẫn
            g.drawString("Press R to Restart", 310, 350);  // Vẽ hướng dẫn "Press R to Restart"
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            // Di chuyển chướng ngại vật
            int i = 0;
            while (i < obstacles.size()) {
                Rectangle obstacle = obstacles.get(i);  // Lấy chướng ngại vật tại vị trí i
                obstacle.y += 10;  // Di chuyển chướng ngại vật xuống dưới 10 pixels

                if (obstacle.y > 600) {  // Nếu chướng ngại vật vượt qua đáy màn hình
                    obstacles.remove(i);  // Xóa chướng ngại vật khỏi danh sách
                    score += 10;  // Tăng điểm 10 khi chướng ngại vật đi qua
                } else {
                    i++;  // Nếu không vượt qua, tiếp tục kiểm tra chướng ngại vật tiếp theo
                }
            }

            // Thêm chướng ngại vật mới với xác suất ngẫu nhiên
            int spawnChance = random.nextInt(100);  // Sinh một giá trị ngẫu nhiên từ 0 đến 99
            if (spawnChance < 5) {  // 5% xác suất thêm chướng ngại vật mới
                int xPosition = 200 + random.nextInt(350);  // Sinh vị trí ngẫu nhiên của chướng ngại vật trên đường
                obstacles.add(new Rectangle(xPosition, 0, OBSTACLE_WIDTH, OBSTACLE_HEIGHT));  // Thêm chướng ngại vật vào danh sách
            }

            // Kiểm tra va chạm giữa xe và chướng ngại vật
            for (Rectangle obstacle : obstacles) {
                if (obstacle.intersects(new Rectangle(carX, carY, CAR_WIDTH, CAR_HEIGHT))) {  // Nếu chướng ngại vật va chạm với xe
                    running = false;  // Dừng trò chơi
                    timer.stop();  // Dừng Timer
                    break;  // Thoát vòng lặp va chạm
                }
            }
        }
        repaint();  // Vẽ lại giao diện sau mỗi lần cập nhật
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (running) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {  // Nếu nhấn phím trái
                if (carX > 200) {  // Nếu xe chưa chạm vào cạnh trái của đường
                    carX -= 20;  // Di chuyển xe sang trái 20 pixels
                }
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {  // Nếu nhấn phím phải
                if (carX < 550) {  // Nếu xe chưa chạm vào cạnh phải của đường
                    carX += 20;  // Di chuyển xe sang phải 20 pixels
                }
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_R) {  // Nếu nhấn phím R khi game over
                resetGame();  // Gọi phương thức reset để khởi động lại trò chơi
            }
        }
    }

    private void resetGame() {
        carX = 375;  // Đặt lại vị trí ban đầu của xe
        carY = 500;  // Đặt lại vị trí ban đầu của xe
        obstacles.clear();  // Xóa tất cả chướng ngại vật
        score = 0;  // Đặt lại điểm số về 0
        running = true;  // Đặt lại trạng thái trò chơi thành "đang chạy"

        // Dừng và khởi động lại Timer
        timer.stop();  // Dừng Timer cũ
        timer = new Timer(20, this);  // Tạo lại một Timer mới
        timer.start();  // Khởi động lại Timer mới
        repaint();  // Vẽ lại giao diện trò chơi sau khi reset
    }

    @Override
    public void keyReleased(KeyEvent e) {}  // Phương thức này không cần xử lý

    @Override
    public void keyTyped(KeyEvent e) {}  // Phương thức này không cần xử lý
}
