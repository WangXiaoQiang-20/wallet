package com.bsn.wallet;

import com.bsn.model.OpbWallet;
import com.bsn.model.OpbWalletFile;
import com.bsn.utils.JsonUtils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;
import org.web3j.utils.Strings;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
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
public class DeployContractPanel {
    // 创建1个字体实例
    static Font font = new Font("宋体", Font.PLAIN, 16);
    static JTextArea addressTextArea = new JTextArea();
    static JTextArea passwordText = new JTextArea();
    static JTextArea privateKeyText = new JTextArea();

    public static JSplitPane swapPanel() {
        JPanel upPanel1 = new JPanel(new BorderLayout()); // 使用 BorderLayout 布局管理器

        // 创建圆角边框
        Border lineBorder = BorderFactory.createLineBorder(new Color(173, 216, 230)); // 浅蓝色边框
        Border emptyBorder = new EmptyBorder(5, 5, 5, 5); // 内边距
        Border roundedBorder = BorderFactory.createCompoundBorder(lineBorder, emptyBorder);

        // 密码
        JPanel upPanel2 = new JPanel(new GridBagLayout()); // 使用 GridBagLayout 布局管理器
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel label1 = new JLabel("Keystore-密码:  ");
        label1.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 0;
        upPanel2.add(label1, gbc);

        passwordText.setRows(1);
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

        // 私钥
        JPanel upPanel3 = new JPanel(new GridBagLayout()); // 使用 GridBagLayout 布局管理器
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel label2 = new JLabel("Keystore-JSON:  ");
        label2.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 0;
        upPanel3.add(label2, gbc);

        privateKeyText.setRows(20);
        privateKeyText.setColumns(100);
        privateKeyText.setLineWrap(true);
        privateKeyText.setWrapStyleWord(true);
        privateKeyText.setFont(font);
        privateKeyText.setBorder(roundedBorder); // 设置圆角边框
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // 设置垂直方向的扩展比例
        upPanel3.add(privateKeyText, gbc);

        // 按钮
        JPanel upPanel4 = new JPanel(new BorderLayout()); // 使用 BorderLayout 布局管理器
        upPanel4.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // 设置上下内边距各5像素

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // 使用 FlowLayout 布局管理器，并居中对齐

        JButton restAccountButton = new JButton("重置");
        restAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        restAccountButton.addActionListener(new restButtonListener());
        setupButtonStyle(restAccountButton, new Color(0, 150, 0), new Color(0, 180, 0));
        restAccountButton.setPreferredSize(new Dimension(100, 35));
        buttonPanel.add(restAccountButton);

        JButton createAccountButton = new JButton("获取钱包地址");
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountButton.addActionListener(new swapButtonListener());
        setupButtonStyle(createAccountButton, new Color(0, 150, 0), new Color(0, 180, 0));
        createAccountButton.setPreferredSize(new Dimension(120, 35));
        buttonPanel.add(createAccountButton);

        upPanel4.add(buttonPanel, BorderLayout.CENTER); // 将按钮面板放在中间

        upPanel1.add(upPanel2, BorderLayout.NORTH); // 将 upPanel2 放在北边
        upPanel1.add(upPanel3, BorderLayout.CENTER); // 将 upPanel3 放在中间
        upPanel1.add(upPanel4, BorderLayout.SOUTH); // 将 upPanel4 放在南边

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
            String privateKey = privateKeyText.getText().trim();
            String pwd = passwordText.getText().trim();

            if (Strings.isEmpty(privateKey)) {
                addressTextArea.setText("兄弟 keystore Json得有啊");
            } else {
                if (pwd.isEmpty()) {
                    try {
                        String privateKey2 = OpbWallet.getPriKeyTwo("", JsonUtils.jsonToPojo(privateKey, OpbWalletFile.class));

                        ECKeyPair ec = ECKeyPair.create(Numeric.toBigInt(privateKey2));
                        Credentials credentials = Credentials.create(ec);

                        StringBuilder address = new StringBuilder();
                        address.append("Wallet：\n\n");
                        address.append("Address:" + credentials.getAddress() + "\n\n");
                        address.append("PrivateKey:" + privateKey2 + "\n\n");

                        addressTextArea.setText(address.toString());
                    } catch (Exception ex) {
                        addressTextArea.setText("不行啊，报错了，检查下吧," + ex.getMessage());
                    }
                } else {
                    try {
                        String privateKey2 = OpbWallet.getPriKeyTwo(pwd, JsonUtils.jsonToPojo(privateKey, OpbWalletFile.class));

                        ECKeyPair ec = ECKeyPair.create(Numeric.toBigInt(privateKey2));
                        Credentials credentials = Credentials.create(ec);

                        StringBuilder address = new StringBuilder();
                        address.append("Wallet：\n\n");
                        address.append("Address:" + credentials.getAddress() + "\n\n");
                        address.append("PrivateKey:" + privateKey2 + "\n\n");

                        addressTextArea.setText(address.toString());
                    } catch (Exception ex) {
                        addressTextArea.setText("不行啊，报错了，检查下吧," + ex.getMessage());
                    }
                }
            }
            addressTextArea.setVisible(true);
        }
    }

    private static class restButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            passwordText.setText("");
            privateKeyText.setText("");
            addressTextArea.setText("");
        }
    }
}
