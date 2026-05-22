package com.bsn.wallet;

import com.bsn.utils.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;
import org.web3j.utils.Strings;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author wxq
 * @create 2023/2/9 13:43
 * @description walletFile panel
 */
public class KeystorePanel {
    // 创建1个字体实例
    static Font font = new Font("宋体", Font.PLAIN, 16);
    static JTextArea addressTextArea = new JTextArea();
    static JTextArea passwordText = new JTextArea();
    static JTextField privateKeyText = new JTextField(100);
    static JTextField mnemonicText = new JTextField(100);

    public static JSplitPane swapPanel() {
        JPanel upPanel1 = new JPanel();

        // 创建圆角边框
        Border lineBorder = BorderFactory.createLineBorder(new Color(173, 216, 230)); // 浅蓝色边框
        Border emptyBorder = new EmptyBorder(5, 5, 5, 5); // 内边距
        Border roundedBorder = BorderFactory.createCompoundBorder(lineBorder, emptyBorder);

        // 密码
        JPanel upPanel2 = new JPanel(new BorderLayout()); // 使用 BorderLayout 布局管理器

        passwordText.setRows(2);
        passwordText.setColumns(100);
        passwordText.setLineWrap(true);
        passwordText.setWrapStyleWord(true);
        passwordText.setFont(font);
        passwordText.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordText.setBorder(roundedBorder); // 设置圆角边框

        JLabel label1 = new JLabel("密码:  ");
        label1.setFont(font);
        upPanel2.add(label1, BorderLayout.WEST); // 将标签放在西边
        upPanel2.add(passwordText, BorderLayout.CENTER); // 将 JTextArea 放在中间

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

        JButton createAccountButton = new JButton("生成·Keystore");
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
            String password = passwordText.getText().trim();
            String privateKey = privateKeyText.getText().trim();
            String mnemonic = mnemonicText.getText().trim();

            if (!Strings.isEmpty(privateKey) && !Strings.isEmpty(mnemonic)) {
                addressTextArea.setText("兄弟 私钥或者助记词 一个就够了");
            } else {
                String strType = "";
                if (!privateKey.isEmpty()) {
                    strType = "privateKey";
                }
                if (!mnemonic.isEmpty()) {
                    strType = "mnemonic";
                }
                if (strType.isEmpty()) {
                    // 直接生成 Keystore
                    generateBip39Wallet(password.trim());
                } else {
                    switch (strType) {
                        case "privateKey":
                            privateKeyToKeystore(privateKey.trim());
                            break;
                        case "mnemonic":
                            mnemonicToKeystore(mnemonic);
                            break;
                        default:
                            addressTextArea.setText("我了个乖乖");
                    }
                }
            }

            addressTextArea.setVisible(true);
        }
    }

    public static void privateKeyToKeystore(String hexPrivateKey) {
        String keystore = "";
        String filePath = getFilePath();
        try {
            ECKeyPair ec = ECKeyPair.create(Numeric.toBigInt(hexPrivateKey));
            keystore = WalletUtils.generateWalletFile(passwordText.getText().trim(), ec, new File(filePath), false);
        } catch (Exception e) {
            keystore = e.getMessage();
        }
        addressTextArea.setText(filePath + "\\" + keystore);
    }

    public static void mnemonicToKeystore(String mnemonic) {
        StringBuilder address = new StringBuilder();
        String walletAddress;
        Credentials credentials = null;
        String walletFile = "";
        String filePath = getFilePath();
        try {
            credentials = WalletUtils.loadBip39Credentials(passwordText.getText().trim(), mnemonic.trim());
            Bip39Wallet keystore = WalletUtils.generateBip39WalletFromMnemonic(passwordText.getText().trim(), mnemonic, new File(filePath));
            walletFile = keystore.getFilename();
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
            address.append("FilePath：" + filePath + "\\" + walletFile + "\n\n");
        }
        addressTextArea.setText(address.toString());
    }

    @NotNull
    private static String getFilePath() {
        String filePath = "C:\\wallet\\" + LocalDate.now();
        FileUtil.createOrExistsDir(new File(filePath));
        return filePath;
    }

    private static void generateBip39Wallet(String password) {
        String filePath = getFilePath();
        String walletFile = "";
        String mnemonic;
        try {
            Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet(password, new File(filePath));
            walletFile = bip39Wallet.getFilename();
            mnemonic = bip39Wallet.getMnemonic();
        } catch (Exception e) {
            mnemonic = e.getMessage();
        }
        StringBuilder address = new StringBuilder();
        address.append("Your account:\n\n");
        address.append("Mnemonic: " + mnemonic + "\n\n");
        address.append("FilePath：" + filePath + "\\" + walletFile + "\n\n");
        addressTextArea.setText(address.toString());
    }

    private static class restButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            passwordText.setText("");
            privateKeyText.setText("");
            mnemonicText.setText("");
            addressTextArea.setText("");
        }
    }
}
