package com.bsn;


import com.bsn.utils.ResourcesUtil;
import com.bsn.wallet.*;
import org.bouncycastle.util.test.Test;
//import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * @author wxq
 * @create 2023/2/9 11:14
 * @description
 */
public class WalletTool extends JPanel {


    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        // 使用Swing窗体描述
        // JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            //org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
//            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
//            BeautyEyeLNFHelper.launchBeautyEyeLNF();
            // 设置右上角的 设置按钮
            UIManager.put("RootPane.setupButtonVisible", false);


            // 设置BeantuEye外观下JTabbedPane的左缩进
            UIManager.put("TabbedPane.tabAreaInsets", new javax.swing.plaf.InsetsUIResource(3, 10, 2, 20));

        } catch (Exception e) {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }

        // 创建窗体
        JFrame frame = new JFrame("Wallet tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new WalletTool(), BorderLayout.CENTER);

        Image icon = Toolkit.getDefaultToolkit().getImage(WalletTool.class.getResource("/images/main.png"));
        frame.setIconImage(icon);


        // 显示窗体
        //调整窗口大小
        frame.setSize(1075, 650);
        frame.setResizable(false);
        setLocation(frame);

        frame.setVisible(true);

    }


    public WalletTool() {

        super(new GridLayout(1, 1));


        ImageIcon icon = createImageIcon("images/wallet.png");

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        tabbedPane.setFont(new Font("宋体", Font.PLAIN, 14));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);


        // 1. 钱包生成面板
        JSplitPane walletPanel = WalletPanel.walletPanel();
        tabbedPane.addTab("Wallet", icon, walletPanel);

        // 2. 公私钥转地址面板
        JSplitPane swapPanel = SwapPanel.swapPanel();
        ImageIcon icon2 = createImageIcon("images/trans.png");
        tabbedPane.addTab("公私钥转地址", icon2, swapPanel, "wang xiao qiang！");

        // 3. Keystore 面板
        Component panel3 = KeystorePanel.swapPanel();
        ImageIcon icon3 = createImageIcon("images/key.png");
        tabbedPane.addTab("Keystore", icon3, panel3, "wang xiao qiang！");

        // 4. Keystore 解密面板
        Component panel31 = KeystoreDecryptPanel.swapPanel();
        ImageIcon icon31 = createImageIcon("images/key.png");
        tabbedPane.addTab("Keystore Decrypt", icon31, panel31, "wang xiao qiang！");

        // 5. AES 解密面板
        Component panel32 = AesDecryptPanel.swapPanel();
        ImageIcon icon32 = createImageIcon("images/key.png");
        tabbedPane.addTab("Aes", icon32, panel32, "默认三组代码里的加密 KEY！");

        // 6. PEM 转 HEX 面板
        Component panel33 = PemToHexPanel.swapPanel();
        ImageIcon icon33 = createImageIcon("images/change.png");
        tabbedPane.addTab("PemToHex", icon33, panel33, "wang xiao qiang！");

        // 8. 转账面板（新增）
        JSplitPane transferPanel = TransferPanel.transferPanel();
        ImageIcon iconTransfer = createImageIcon("images/transfer.png");
        tabbedPane.addTab("Transfer", iconTransfer, transferPanel, "转账功能！");


        // 9. 时间戳面板
        Component panel4 = TimePanel.timePanel();
        ImageIcon icon4 = createImageIcon("images/time.png");
        tabbedPane.addTab("时间戳", icon4, panel4, "wang xiao qiang！");

        // 将选项卡添加到 panl 中
        // 在 WalletTool 构造函数末尾添加
        tabbedPane.setSelectedIndex(0); // 默认选中第一个 tab
        tabbedPane.revalidate();
        tabbedPane.repaint();
        add(tabbedPane);
    }


    /**
     * <br>
     * 方法说明：获得图片 <br>
     * 输入参数：String path 图片的路径 <br>
     * 返回类型：ImageIcon 图片对象
     */
    protected static ImageIcon createImageIcon(String fileName) {
        if (fileName != null) {
            URL urL = WalletTool.class.getResource("/" + fileName);
            return new ImageIcon(urL);
        } else {
            System.out.println("Couldn't find file: " + fileName);
            return null;
        }

    }


    /**
     * setLocation
     *
     * @param frame
     */
    private static void setLocation(JFrame frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }


}
