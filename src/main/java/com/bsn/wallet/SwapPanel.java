package com.bsn.wallet;

import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import javax.swing.*;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;
import org.web3j.utils.Strings;

public class SwapPanel {
    // 创建1个字体实例
    static Font font = new Font("宋体", Font.PLAIN, 16);
    static JTextArea addressTextArea = new JTextArea();
    static JTextArea publicKeyText = new JTextArea();
    static JTextField privateKeyText = new JTextField(100);
    static JTextField mnemonicText = new JTextField(100);

    public static JSplitPane swapPanel() {
        JPanel upPanel1 = new JPanel();

        // 公钥
        JPanel upPanel2 = new JPanel(new BorderLayout()); // 使用 BorderLayout 布局管理器

        publicKeyText.setRows(2);
        publicKeyText.setColumns(100);
        publicKeyText.setLineWrap(true);
        publicKeyText.setWrapStyleWord(true);
        publicKeyText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label1 = new JLabel("公钥:  ");
        label1.setFont(font);
        upPanel2.add(label1, BorderLayout.WEST); // 将标签放在西边

        publicKeyText.setFont(font);
        Border border = BorderFactory.createLineBorder(new Color(173, 216, 230)); // 浅蓝色边框

        Border emptyBorder = new EmptyBorder(5, 5, 5, 5); // 内边距
        Border roundedBorder = BorderFactory.createCompoundBorder(border, emptyBorder);
        publicKeyText.setBorder(roundedBorder); // 设置圆角边框
        upPanel2.add(publicKeyText, BorderLayout.CENTER); // 将 JTextArea 放在中间

        // 私钥
        JPanel upPanel3 = new JPanel(new BorderLayout()); // 使用 BorderLayout 布局管理器
        JLabel label2 = new JLabel("私钥:  ");
        label2.setFont(font);
        upPanel3.add(label2, BorderLayout.WEST); // 将标签放在西边

        privateKeyText.setFont(font);
        privateKeyText.setBorder(roundedBorder); // 设置圆角边框
        upPanel3.add(privateKeyText, BorderLayout.CENTER); // 将 JTextField 放在中间

        // 助记词
        JPanel upPanel5 = new JPanel(new BorderLayout()); // 使用 BorderLayout 布局管理器
        JLabel label5 = new JLabel("助记词:");
        label5.setFont(font);
        upPanel5.add(label5, BorderLayout.WEST); // 将标签放在西边

        mnemonicText.setFont(font);
        mnemonicText.setBorder(roundedBorder); // 设置圆角边框
        upPanel5.add(mnemonicText, BorderLayout.CENTER); // 将 JTextField 放在中间

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

        JButton createAccountButton = new JButton("转换地址");
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountButton.addActionListener(new swapButtonListener());
        setupButtonStyle(createAccountButton, new Color(0, 150, 0), new Color(0, 180, 0));
        createAccountButton.setPreferredSize(new Dimension(120, 35));
        buttonPanel.add(createAccountButton);

        upPanel4.add(buttonPanel, BorderLayout.CENTER); // 将按钮面板放在中间

        upPanel1.add(upPanel2);
        upPanel1.add(upPanel3);
        upPanel1.add(upPanel5);
        upPanel1.add(upPanel4);
        upPanel1.setLayout(new GridLayout(4, 1));

        JPanel downPanel = new JPanel();
        addressTextArea.setRows(10);
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
            String publicKey = publicKeyText.getText().trim();
            String privateKey = privateKeyText.getText().trim();
            String mnemonic = mnemonicText.getText().trim();

            if (Strings.isEmpty(publicKey) && Strings.isEmpty(privateKey) && Strings.isEmpty(mnemonic)) {
                addressTextArea.setText("给你一毛钱，写个值吧！");
            } else {
                String strType = "";
                int count = 0;
                if (!publicKey.isEmpty()) {
                    strType = "publicKey";
                    count++;
                }
                if (!privateKey.isEmpty()) {
                    strType = "privateKey";
                    count++;
                }
                if (!mnemonic.isEmpty()) {
                    strType = "mnemonic";
                    count++;
                }
                if (count != 1) {
                    addressTextArea.setText("不能脚踏两只船，给我一个值就好！");
                } else {
                    switch (strType) {
                        case "publicKey":
                            System.out.println("公钥" + publicKeyText.getText());
                            publicKeyToAddress(publicKeyText.getText());
                            break;
                        case "privateKey":
                            privateKeyToAddress(privateKeyText.getText());
                            break;
                        case "mnemonic":
                            mnemonicToAddress(mnemonicText.getText());
                            break;
                        default:
                            addressTextArea.setText("我了个乖乖");
                    }
                }
            }
            addressTextArea.setVisible(true);
        }
    }

    public static void publicKeyToAddress(String HexPublicKey) {
        String result;
        try {
            result = "0x" + Keys.getAddress(Numeric.toBigInt(HexPublicKey));
        } catch (Exception e) {
            result = "兄弟 输入的啥玩意啊：" + e.getMessage();
        }
        addressTextArea.setText(result);
    }

    public static void privateKeyToAddress(String hexPrivateKey) {
        String result;
        try {
            result = Keys.toChecksumAddress(Keys.getAddress(ECKeyPair.create(Numeric.toBigInt(hexPrivateKey))));
        } catch (Exception e) {
            result = "兄弟 输入的啥玩意啊：" + e.getMessage();
        }
        addressTextArea.setText(result);
    }

    public static void mnemonicToAddress(String mnemonic) {
        StringBuilder address = new StringBuilder();
        String walletAddress;
        Credentials credentials = null;
        try {
            credentials = WalletUtils.loadBip39Credentials("", mnemonic.trim());
            walletAddress = credentials.getAddress();
        } catch (Exception e) {
            walletAddress = "兄弟 输入的啥玩意啊：" + e.getMessage();
            address.append(walletAddress);
        }
        if (Objects.nonNull(credentials)) {
            address.append("Your account:\n\n");
            address.append("Address: " + walletAddress + "\n\n");
            address.append("PrivateKey：0x" + credentials.getEcKeyPair().getPrivateKey().toString(16) + "\n\n");
            address.append("PublicKey：0x" + credentials.getEcKeyPair().getPublicKey().toString(16) + "\n\n");
        }
        addressTextArea.setText(address.toString());
    }

    private static class restButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            publicKeyText.setText("");
            privateKeyText.setText("");
            mnemonicText.setText("");
            addressTextArea.setText("");
        }
    }
}
