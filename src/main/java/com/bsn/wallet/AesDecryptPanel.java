package com.bsn.wallet;

import com.bsn.utils.AesUtil;
import org.web3j.utils.Strings;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author wxq
 * @create 2023/2/9 13:43
 * @description walletFile panel
 */
public class AesDecryptPanel {
    // 创建1个字体实例
    static Font font = new Font("宋体", Font.PLAIN, 16);
    static JTextArea addressTextArea = new JTextArea();
    static JTextArea passwordText = new JTextArea();

    public static JSplitPane swapPanel() {
        JPanel upPanel1 = new JPanel();

        // 创建圆角边框
        Border lineBorder = BorderFactory.createLineBorder(new Color(173, 216, 230)); // 浅蓝色边框
        Border emptyBorder = new EmptyBorder(5, 5, 5, 5); // 内边距
        Border roundedBorder = BorderFactory.createCompoundBorder(lineBorder, emptyBorder);

        // 密码
        JPanel upPanel2 = new JPanel(new GridBagLayout()); // 使用 GridBagLayout 布局管理器
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel label1 = new JLabel("看啥，这里输入:  ");
        label1.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 0;
        upPanel2.add(label1, gbc);

        passwordText.setRows(4);
        passwordText.setColumns(90);
        passwordText.setLineWrap(true);
        passwordText.setWrapStyleWord(true);
        passwordText.setFont(font);
        passwordText.setBorder(roundedBorder); // 设置圆角边框

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        upPanel2.add(passwordText, gbc);

        // 按钮
        JPanel upPanel4 = new JPanel(new BorderLayout()); // 使用 BorderLayout 布局管理器
        upPanel4.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // 设置上下内边距各5像素

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // 使用 FlowLayout 布局管理器，并居中对齐

        JButton restAccountButton = new JButton("加密");
        restAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        restAccountButton.addActionListener(new restButtonListener());
        setupButtonStyle(restAccountButton, new Color(0, 150, 0), new Color(0, 180, 0));
        restAccountButton.setPreferredSize(new Dimension(100, 35));
        restAccountButton.setPreferredSize(new Dimension(100, 35));
        buttonPanel.add(restAccountButton);

        JButton createAccountButton = new JButton("解密");
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountButton.addActionListener(new swapButtonListener());
        setupButtonStyle(createAccountButton, new Color(0, 150, 0), new Color(0, 180, 0));
        createAccountButton.setPreferredSize(new Dimension(120, 35));
        buttonPanel.add(createAccountButton);

        upPanel4.add(buttonPanel, BorderLayout.CENTER); // 将按钮面板放在中间

        upPanel1.add(upPanel2);
        upPanel1.add(upPanel4);
        upPanel1.setLayout(new GridLayout(4, 1));

        JPanel downPanel = new JPanel();
        addressTextArea.setRows(8);
        addressTextArea.setColumns(90);
        addressTextArea.setLineWrap(true);
        addressTextArea.setWrapStyleWord(true);
        addressTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        addressTextArea.setFont(font);
        downPanel.add(addressTextArea, BorderLayout.CENTER);

        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                upPanel1, downPanel);
        pane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                pane.setDividerLocation(0.5);
            }
        });
        return pane;
    }

    private static void setupButtonStyle(JButton button, Color bgColor, Color hoverColor) {
        button.setFont(font);
        button.setForeground(Color.BLACK);
        button.setBackground(bgColor);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    private static class swapButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String pwd = passwordText.getText().trim();

            if (Strings.isEmpty(pwd)) {
                addressTextArea.setText("兄弟 啥也没有 咋整啊！！");
            } else {
                try {
                    String dePwd = AesUtil.decrypt(pwd);
                    addressTextArea.setText("Decrypt pwd: " + dePwd);
                } catch (Exception ex) {
                    addressTextArea.setText("啥也不是 你的密码就是胡搞来的");
                }
            }
            addressTextArea.setVisible(true);
        }
    }

    private static class restButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String pwd = passwordText.getText();

            if (Strings.isEmpty(pwd)) {
                addressTextArea.setText("兄弟 啥也没有 咋整啊！！");
            } else {
                try {
                    String dePwd = AesUtil.encrypt(pwd);
                    addressTextArea.setText("Encrypt pwd: " + dePwd);
                } catch (Exception ex) {
                    addressTextArea.setText("啥也不是 你的密码就是胡搞来的");
                }
            }
            addressTextArea.setVisible(true);
        }
    }
}
