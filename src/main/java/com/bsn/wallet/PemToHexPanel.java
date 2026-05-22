package com.bsn.wallet;

import com.bsn.utils.PemUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
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
import java.io.StringReader;
import java.security.Security;
import java.util.Objects;

/**
 * @author wxq
 * @create 2023/2/9 17:38
 * @description swap
 */
public class PemToHexPanel {
    // 创建1个字体实例
    static Font font = new Font("宋体", Font.PLAIN, 16);
    static JTextArea addressTextArea = new JTextArea();
    static JTextArea pemPrivateKeyText = new JTextArea();

    public static JSplitPane swapPanel() {
        JPanel upPanel1 = new JPanel(new BorderLayout()); // 使用 BorderLayout 布局管理器

        // 创建圆角边框
        Border lineBorder = BorderFactory.createLineBorder(new Color(173, 216, 230)); // 浅蓝色边框
        Border emptyBorder = new EmptyBorder(5, 5, 5, 5); // 内边距
        Border roundedBorder = BorderFactory.createCompoundBorder(lineBorder, emptyBorder);

        // 公钥
        JPanel upPanel2 = new JPanel(new GridBagLayout()); // 使用 GridBagLayout 布局管理器
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel label1 = new JLabel("PEM格式私钥:  ");
        label1.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 0;
        upPanel2.add(label1, gbc);

        pemPrivateKeyText.setRows(10);
        pemPrivateKeyText.setColumns(100);
        pemPrivateKeyText.setLineWrap(true);
        pemPrivateKeyText.setWrapStyleWord(true);
        pemPrivateKeyText.setFont(font);
        pemPrivateKeyText.setBorder(roundedBorder); // 设置圆角边框
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        upPanel2.add(pemPrivateKeyText, gbc);

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

        JButton createAccountButton = new JButton("转 HEX");
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountButton.addActionListener(new swapButtonListener());
        setupButtonStyle(createAccountButton, new Color(0, 150, 0), new Color(0, 180, 0));
        createAccountButton.setPreferredSize(new Dimension(120, 35));
        buttonPanel.add(createAccountButton);

        upPanel4.add(buttonPanel, BorderLayout.CENTER); // 将按钮面板放在中间

        upPanel1.add(upPanel2, BorderLayout.CENTER); // 将 upPanel2 放在中间
        upPanel1.add(upPanel4, BorderLayout.SOUTH); // 将 upPanel4 放在南边

        JPanel downPanel = new JPanel();
        addressTextArea.setRows(20);
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
            String pemText = pemPrivateKeyText.getText().trim();

            if (Strings.isEmpty(pemText)) {
                addressTextArea.setText("抠门 啥也不写，转个毛线啊！");
            } else {
                if (PemUtils.isPemPrivateKey(pemText)){
                    pemPrivateKeyToAddress(pemPrivateKeyText.getText());
                }else {
                    addressTextArea.setText("这货不是PEM格式私钥啊！！");
                }
            }
            addressTextArea.setVisible(true);
        }
    }

    public static void pemPrivateKeyToAddress(String pemStr) {
        Security.addProvider(new BouncyCastleProvider());

        try (StringReader stringReader = new StringReader(pemStr);
             PemReader pemReader = new PemReader(stringReader)) {

            PemObject pemObject = pemReader.readPemObject();
            if (pemObject != null) {
                byte[] content = pemObject.getContent();
                PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(content);
                ECPrivateKeyParameters privateKeyParameters = (ECPrivateKeyParameters) PrivateKeyFactory.createKey(privateKeyInfo);
                byte[] privateKeyBytes = privateKeyParameters.getD().toByteArray();
                String hexPrivateKey = Hex.toHexString(privateKeyBytes);
                System.out.println("Hexadecimal Private Key: " + Numeric.toHexString(privateKeyBytes));

                ECKeyPair ec = ECKeyPair.create(Numeric.toBigInt(hexPrivateKey));
                System.out.println("Hexadecimal Public Key: " + Numeric.toHexStringWithPrefix(ec.getPublicKey()));

                StringBuilder address = new StringBuilder();
                address.append("Your account:\n\n");
                address.append("Address: " + privateKeyToAddress(Numeric.toHexString(privateKeyBytes)) + "\n\n");
                address.append("PrivateKey：" + Numeric.toHexString(privateKeyBytes) + "\n\n");
                address.append("PublicKey：" + Numeric.toHexStringWithPrefix(ec.getPublicKey()) + "\n\n");
                addressTextArea.setText(address.toString());
            } else {
                addressTextArea.setText("兄弟 你这个转不了啊！！！");
            }
        } catch (Exception e) {
            addressTextArea.setText("兄弟，发生了意外 失败了！！！");
        }
    }

    public static String privateKeyToAddress(String hexPrivateKey) {
        String result;
        try {
            result = Keys.toChecksumAddress(Keys.getAddress(ECKeyPair.create(Numeric.toBigInt(hexPrivateKey))));
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    private static class restButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            pemPrivateKeyText.setText("");
            addressTextArea.setText("");
        }
    }
}
