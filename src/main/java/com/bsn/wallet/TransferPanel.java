package com.bsn.wallet;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;
import org.web3j.utils.Strings;
import org.web3j.utils.Convert;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TransferPanel {
    static Font font = new Font("宋体", Font.PLAIN, 16);
    static JTextArea resultTextArea = new JTextArea();
    static JTextField nodeUrlText = new JTextField(100);
    static JTextField fromWalletPrivateKeyText = new JTextField(100);
    static JTextField fromWalletAddrText = new JTextField(100);
    static JTextField toWalletAddrText = new JTextField(100);
    static JTextField amountText = new JTextField(100);

    private static Web3j web3j;
    private static String currentChainId;

    public static JSplitPane transferPanel() {
        JPanel upPanel1 = new JPanel(new GridLayout(5, 1, 5, 5));
        upPanel1.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        Border lineBorder = BorderFactory.createLineBorder(new Color(173, 216, 230));
        Border emptyBorder = new EmptyBorder(8, 8, 8, 8);
        Border roundedBorder = BorderFactory.createCompoundBorder(lineBorder, emptyBorder);

        fromWalletAddrText.setEditable(false);
        fromWalletAddrText.setBackground(new Color(240, 240, 240));

        upPanel1.add(createInputPanel(roundedBorder, "Node URL:", nodeUrlText));

        JPanel privateKeyPanel = createInputPanel(roundedBorder, "From Private Key:", fromWalletPrivateKeyText);

        fromWalletPrivateKeyText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFromAddress();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFromAddress();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFromAddress();
            }

            private void updateFromAddress() {
                String privateKey = fromWalletPrivateKeyText.getText().trim();
                if (!privateKey.isEmpty()) {
                    try {
                        Credentials credentials = Credentials.create(privateKey);
                        fromWalletAddrText.setText(credentials.getAddress());
                    } catch (Exception ex) {
                        fromWalletAddrText.setText("无效的私钥");
                    }
                } else {
                    fromWalletAddrText.setText("");
                }
            }
        });

        upPanel1.add(privateKeyPanel);
        upPanel1.add(createInputPanel(roundedBorder, "From Address:", fromWalletAddrText));
        upPanel1.add(createTwoInputPanel(roundedBorder, "To Address:", toWalletAddrText, "Amount:", amountText));

        JPanel buttonPanel = createButtonPanel();
        upPanel1.add(buttonPanel);

        JPanel downPanel = new JPanel(new BorderLayout());
        downPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        resultTextArea.setRows(10);
        resultTextArea.setColumns(90);
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        resultTextArea.setFont(font);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230)));
        downPanel.add(scrollPane, BorderLayout.CENTER);

        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upPanel1, downPanel);
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // 使用 SwingUtilities.invokeLater 延迟设置分割线位置
        SwingUtilities.invokeLater(() -> {
            pane.setDividerLocation(0.45);
        });
        return pane;
    }
    private static JPanel createTwoInputPanel(Border roundedBorder, String label1Text, JTextField textField1,
                                              String label2Text, JTextField textField2) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 5, 2, 5);

        // 第一个标签
        JLabel label1 = new JLabel(label1Text);
        label1.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(label1, gbc);

        // 第一个输入框
        textField1.setFont(font);
        textField1.setBorder(roundedBorder);
        textField1.setColumns(1);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.45;
        panel.add(textField1, gbc);

        // 第二个标签
        JLabel label2 = new JLabel(label2Text);
        label2.setFont(font);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(2, 15, 2, 5);
        panel.add(label2, gbc);

        // 第二个输入框
        textField2.setFont(font);
        textField2.setBorder(roundedBorder);
        textField2.setColumns(1);
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.45;
        panel.add(textField2, gbc);

        // 单位标签
        JLabel unitLabel = new JLabel("(单位：WEI)");
        unitLabel.setFont(font);
        unitLabel.setForeground(Color.GRAY);
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(2, 5, 2, 5);
        panel.add(unitLabel, gbc);

        return panel;
    }
    private static JPanel createInputPanel(Border roundedBorder, String labelText, JTextField textField) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 5, 2, 5);

        JLabel label = new JLabel(labelText);
        label.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(label, gbc);

        textField.setFont(font);
        textField.setBorder(roundedBorder);
        textField.setColumns(1);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(textField, gbc);

        return panel;
    }

    private static JPanel createInputPanelWithLabel(Border roundedBorder, String labelText, JTextField textField, String unitText) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 5, 2, 5);

        JLabel label = new JLabel(labelText);
        label.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(label, gbc);

        textField.setFont(font);
        textField.setBorder(roundedBorder);
        textField.setColumns(1);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(textField, gbc);

        JLabel unitLabel = new JLabel(unitText);
        unitLabel.setFont(font);
        unitLabel.setForeground(Color.GRAY);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(2, 10, 2, 5);
        panel.add(unitLabel, gbc);

        return panel;
    }

    private static JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // --- 重置按钮 ---
        JButton resetButton = new JButton("重置");
        setupButtonStyle(resetButton, new Color(0, 150, 0), new Color(0, 180, 0));
        resetButton.setPreferredSize(new Dimension(100, 35));
        resetButton.addActionListener(new ResetButtonListener());
        panel.add(resetButton);

        // --- 发送交易按钮 ---
        JButton sendButton = new JButton("发送交易");
        setupButtonStyle(sendButton, new Color(0, 150, 0), new Color(0, 180, 0)); // 传入 默认色, 悬停色
        sendButton.setPreferredSize(new Dimension(120, 35));
        sendButton.addActionListener(new SendButtonListener());
        panel.add(sendButton);

        return panel;
    }
    /**
     * 辅助方法：统一设置按钮样式，确保文字始终可见
     * @param button 按钮对象
     * @param bgColor 背景颜色
     * @param hoverColor 鼠标悬停时的背景颜色
     */
    private static void setupButtonStyle(JButton button, Color bgColor, Color hoverColor) {
        // 1. 设置字体
        button.setFont(font);

        // 2. 关键：设置文字颜色（前景色）为白色
        button.setForeground(Color.BLACK);

        // 3. 关键：设置背景颜色
        button.setBackground(bgColor);

        // 4. 关键：必须设置为不透明，否则背景色可能不生效或被外观主题覆盖
        button.setOpaque(true);

        // 5. 移除边框（可选，为了让纯色背景更干净，看个人喜好）
        button.setBorder(BorderFactory.createEmptyBorder());
        // 或者保留边框但设置颜色: button.setBorder(BorderFactory.createLineBorder(bgColor.darker()));

        // 6. 添加鼠标悬停效果，解决“看不见字”的问题，同时增加交互感
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor); // 鼠标放上去变亮
                button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 变成手型光标
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor); // 鼠标移开恢复原色
            }
        });
    }
    private static class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String nodeUrl = nodeUrlText.getText().trim();
            String privateKey = fromWalletPrivateKeyText.getText().trim();
            String toAddress = toWalletAddrText.getText().trim();
            String amount = amountText.getText().trim();

            if (Strings.isEmpty(nodeUrl) || Strings.isEmpty(privateKey) ||
                    Strings.isEmpty(toAddress) || Strings.isEmpty(amount)) {
                resultTextArea.setText("请填写所有必填字段！");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "确认发送交易？\n\n" +
                            "目标地址：" + toAddress + "\n" +
                            "金额：" + amount + " WEI",
                    "交易确认",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            sendTransaction(nodeUrl, privateKey, toAddress, amount);
        }
    }

    private static void sendTransaction(String nodeUrl, String privateKey, String toAddress, String amount) {
        // 禁用发送按钮，防止重复点击
        try {
            web3j = Web3j.build(new HttpService(nodeUrl));

            Long chainId = 0L;
            try {
                String netVersion = web3j.netVersion().send().getNetVersion();
                chainId = Long.parseLong(netVersion);
            } catch (Exception e) {
                resultTextArea.setText("无法获取链 ID，请检查节点 URL 是否正确！");
                return;
            }

            BigInteger gasPrice = null;
            try {
                gasPrice = web3j.ethGasPrice().send().getGasPrice();
                // price 加上 30%
                gasPrice = gasPrice.multiply(BigInteger.valueOf(130)).divide(BigInteger.valueOf(100));
            } catch (Exception e) {
                resultTextArea.setText("无法获取 GasPrice，请检查节点 URL 是否正确！");
                return;
            }

            ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
            Credentials credentials = Credentials.create(ecKeyPair);

            EthGetTransactionCount txCount = web3j.ethGetTransactionCount(
                    credentials.getAddress(),
                    DefaultBlockParameterName.LATEST
            ).send();

            BigInteger nonce = txCount.getTransactionCount();

            BigInteger value = new BigInteger(amount);

            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                    nonce,
                    gasPrice,
                    BigInteger.valueOf(21000),
                    toAddress,
                    value
            );

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
            String hexValue = Numeric.toHexString(signedMessage);

            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

            StringBuilder result = new StringBuilder();
            if (ethSendTransaction.hasError()) {
                result.append("交易发送失败！\n");
                result.append("错误信息：").append(ethSendTransaction.getError().getMessage());
                resultTextArea.setText(result.toString());
            } else {
                String txHash = ethSendTransaction.getTransactionHash();
                result.append("=== 交易已发送 ===\n\n");
                result.append("Transaction Hash: ").append(txHash).append("\n\n");
                resultTextArea.setText(result.toString());

                // 使用新的线程来获取交易回执
                new Thread(() -> {
                    try {
                        boolean foundReceipt = false;

                        // 循环等待交易回执，最多尝试 60 次，每次间隔 2 秒
                        for (int i = 0; i < 60; i++) {
                            Thread.sleep(2000);

                            EthGetTransactionReceipt txReceipt = web3j.ethGetTransactionReceipt(txHash).send();
                            if (txReceipt.getTransactionReceipt().isPresent()) {
                                foundReceipt = true;
                                TransactionReceipt receipt = txReceipt.getTransactionReceipt().get();

                                final StringBuilder receiptInfo = new StringBuilder();
                                receiptInfo.append("=== 交易回执 ===\n\n");
                                receiptInfo.append("Block Number: ").append(receipt.getBlockNumber()).append("\n\n");
                                receiptInfo.append("Block Hash: ").append(receipt.getBlockHash()).append("\n\n");
                                receiptInfo.append("Transaction Index: ").append(receipt.getTransactionIndex()).append("\n\n");
                                receiptInfo.append("From: ").append(receipt.getFrom()).append("\n\n");
                                receiptInfo.append("To: ").append(receipt.getTo()).append("\n\n");
                                receiptInfo.append("Gas Used: ").append(receipt.getGasUsed()).append("\n\n");
                                receiptInfo.append("Cumulative Gas Used: ").append(receipt.getCumulativeGasUsed()).append("\n\n");
                                receiptInfo.append("Status: ").append(receipt.getStatus() != null ? receipt.getStatus() : "N/A").append("\n\n");

                                SwingUtilities.invokeLater(() -> {
                                    // 删除等待提示
                                    String currentText = result.toString();
                                    int lastIndex = currentText.lastIndexOf("\n正在等待交易确认");
                                    if (lastIndex > 0) {
                                        result.delete(lastIndex, currentText.length());
                                    }
                                    result.append(receiptInfo.toString());
                                    resultTextArea.setText(result.toString());
                                });
                                break;
                            }

                            // 更新等待状态（不覆盖 Transaction Hash）
                            final int attempt = i + 1;
                            SwingUtilities.invokeLater(() -> {
                                // 先删除之前的等待提示（如果存在）
                                String currentText = result.toString();
                                int lastIndex = currentText.lastIndexOf("\n正在等待交易确认");
                                if (lastIndex > 0) {
                                    result.delete(lastIndex, currentText.length());
                                }
                                // 添加新的等待提示
                                result.append("\n正在等待交易确认... (").append(attempt).append("/60)");
                                resultTextArea.setText(result.toString());
                            });
                        }

                        // 如果 60 次后还没获取到
                        if (!foundReceipt) {
                            SwingUtilities.invokeLater(() -> {
                                // 删除等待提示
                                String currentText = result.toString();
                                int lastIndex = currentText.lastIndexOf("\n正在等待交易确认");
                                if (lastIndex > 0) {
                                    result.delete(lastIndex, currentText.length());
                                }
                                result.append("\n未在预期时间内获取到交易回执，请稍后在区块浏览器查询。\n");
                                resultTextArea.setText(result.toString());
                            });
                        }

                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() -> {
                            // 删除等待提示
                            String currentText = result.toString();
                            int lastIndex = currentText.lastIndexOf("\n正在等待交易确认");
                            if (lastIndex > 0) {
                                result.delete(lastIndex, currentText.length());
                            }
                            result.append("\n获取交易回执失败：").append(ex.getMessage());
                            resultTextArea.setText(result.toString());
                        });
                    }
                }).start();
            }

        } catch (Exception ex) {
            resultTextArea.setText("交易失败：" + ex.getMessage());
        }
    }

    private static class ResetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            nodeUrlText.setText("");
            fromWalletPrivateKeyText.setText("");
            fromWalletAddrText.setText("");
            toWalletAddrText.setText("");
            amountText.setText("");
            resultTextArea.setText("");
        }
    }
}
