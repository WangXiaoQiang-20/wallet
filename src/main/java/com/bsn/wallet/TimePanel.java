package com.bsn.wallet;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author wxq
 * @create 2023/2/9 13:43
 * @description walletFile panel
 */
public class TimePanel {
    // 创建1个字体实例
    static Font font = new Font("宋体", Font.PLAIN, 24);
    static JLabel timeLabel = new JLabel();

    public static JPanel timePanel() {
        JPanel upPanel1 = new JPanel(new BorderLayout()); // 使用 BorderLayout 布局管理器

        // 创建圆角边框
        Border lineBorder = BorderFactory.createLineBorder(new Color(173, 216, 230)); // 浅蓝色边框
        Border emptyBorder = new EmptyBorder(5, 5, 5, 5); // 内边距
        Border roundedBorder = BorderFactory.createCompoundBorder(lineBorder, emptyBorder);

        // 文本区域
        JPanel textPanel = new JPanel(new BorderLayout());
        timeLabel.setFont(font);
        timeLabel.setBorder(roundedBorder); // 设置圆角边框
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER); // 水平居中
        timeLabel.setVerticalAlignment(SwingConstants.CENTER); // 垂直居中
        timeLabel.setOpaque(true); // 设置背景不透明
        timeLabel.setBackground(Color.WHITE); // 设置背景颜色为白色

        // 设置默认文本
        updateLabel();

        // 使用 Timer 定期更新时间
        Timer timer = new Timer(1000, e -> updateLabel());
        timer.start();

        textPanel.add(timeLabel, BorderLayout.CENTER);

        // GIF 图像
        JPanel gifPanel = new JPanel(new BorderLayout());
        ImageIcon gifIcon = new ImageIcon(TimePanel.class.getResource("/images/hhh.gif"));
        if (gifIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            JLabel gifLabel = new JLabel(gifIcon);
            gifPanel.add(gifLabel, BorderLayout.CENTER);
        } else {
            JLabel gifLabel = new JLabel(gifIcon);
            gifPanel.add(gifLabel, BorderLayout.CENTER);
        }

        // 将 textPanel 和 gifPanel 添加到 upPanel1
        upPanel1.add(textPanel, BorderLayout.NORTH);
        upPanel1.add(gifPanel, BorderLayout.CENTER);

        return upPanel1;
    }

    private static void updateLabel() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = now.format(formatter);
        String contactInfo = "有问题 ---> WangXiaoQiang";

        StringBuilder text = new StringBuilder();
        text.append(currentTime).append("\n\n | ");
        text.append(contactInfo).append("\n");

        timeLabel.setText(text.toString());
    }

    public static void main(String[] args) {
        // 创建窗体
        JFrame frame = new JFrame("Time Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(timePanel(), BorderLayout.CENTER);

        // 显示窗体
        frame.setSize(1000, 650);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
