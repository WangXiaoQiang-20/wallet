package com.bsn.wallet;

import com.bsn.utils.FileUtil;
import com.google.common.collect.ImmutableList;
import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.*;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple5;
import sun.security.provider.SecureRandom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author wxq
 * @create 2023/2/9 13:43
 * @description walletFile panel
 */
public class WalletPanel {
    /**
     * path路径
     */
    private final static ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =

            ImmutableList.of(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);
    static Font font = new Font("宋体", Font.PLAIN, 16);
    static JTextArea textArea = new JTextArea();

    public static JSplitPane walletPanel() {


        JPanel leftPanel = new JPanel();
        //添加按钮
        JButton createAccountButton = new JButton("生成账户地址");
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountButton.addActionListener(new CreateAccountButtonListener());
        setupButtonStyle(createAccountButton, new Color(0, 150, 0), new Color(0, 180, 0));
        createAccountButton.setPreferredSize(new Dimension(120, 35));
        leftPanel.add(createAccountButton);

        JPanel rightPanel = new JPanel();
        textArea.setRows(50);
        textArea.setColumns(102);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(null);
        textArea.setFont(font);
        textArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(textArea, BorderLayout.CENTER);

        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftPanel, rightPanel);
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

    private static class CreateAccountButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Tuple5<String, String, String, String, String> account = createAccount2();
            if (Objects.nonNull(account)) {
                StringBuilder address = new StringBuilder();
                address.append("Your account:：\n\n");
                address.append(account.component1() + "\n\n");
                address.append(account.component2() + "\n\n");
                address.append(account.component3() + "\n\n");
                address.append(account.component4() + "\n\n");
                address.append(account.component5() + "\n\n\n\n");
                textArea.setText(address + "\n\n");
            } else {
                textArea.setText("不好意思 生成失败");
            }

            textArea.setVisible(true);
        }
    }

    public static Tuple4<String, String, String, String> createAccount() {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] entropy = new byte[DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS / 8];
            secureRandom.engineNextBytes(entropy);

            List<String> str = MnemonicCode.INSTANCE.toMnemonic(entropy);

            byte[] seed = MnemonicCode.toSeed(str, "");

            DeterministicKey masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
            DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(masterPrivateKey);

            DeterministicKey deterministicKey = deterministicHierarchy.deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(0));
            byte[] bytes = deterministicKey.getPrivKeyBytes();
            ECKeyPair keyPair = ECKeyPair.create(bytes);

            String address = "Address: 0x" + Keys.getAddress(keyPair.getPublicKey());
            String privateKey = "PrivateKey: 0x" + keyPair.getPrivateKey().toString(16);
            String publicKey = "PublicKey: 0x" + keyPair.getPublicKey().toString(16);
            String mnemonicCode = "MnemonicCode: " + str.stream().collect(Collectors.joining(","));
            return new Tuple4<>(address, privateKey, publicKey, mnemonicCode);
        } catch (MnemonicException.MnemonicLengthException e) {
            e.printStackTrace();
        } catch (HDDerivationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Tuple5<String, String, String, String, String> createAccount2() {
        try {
            String filePath = "C:\\wallet\\" + LocalDate.now();
            FileUtil.createOrExistsDir(new File(filePath));
            Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet("", new File(filePath));

            Credentials credentials = WalletUtils.loadBip39Credentials("", bip39Wallet.getMnemonic());
            String address = "Address: " + credentials.getAddress();
            String privateKey = "PrivateKey: 0x" + credentials.getEcKeyPair().getPrivateKey().toString(16);
            String publicKey = "PublicKey: 0x" + credentials.getEcKeyPair().getPublicKey().toString(16);
            String mnemonicCode = "MnemonicCode: " + bip39Wallet.getMnemonic();
            String walletFilePath = "FilePath: " + filePath + "\\" + bip39Wallet.getFilename();

            return new Tuple5<>(address, privateKey, publicKey, mnemonicCode, walletFilePath);
        } catch (Exception e) {
            textArea.setText("不好意思 生成失败:" + e.getMessage());
        }

        return null;
    }

}
