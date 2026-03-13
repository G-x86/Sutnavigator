package User;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import Components.Components_;

public class Resetpassword {
    private JPanel cardContainer;
    private JPanel mainPanel;

    public Resetpassword(JPanel cardContainer) {
        this.cardContainer = cardContainer;
        mainPanel = new JPanel(new GridBagLayout());

        // --- Card Panel (กรอบขาวกลมตรงกลาง) ---
        JPanel resetCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        resetCard.setOpaque(false);
        resetCard.setBackground(Color.WHITE);
        resetCard.setLayout(new BoxLayout(resetCard, BoxLayout.Y_AXIS));
        resetCard.setBorder(new EmptyBorder(30, 40, 30, 40));
        resetCard.setPreferredSize(new Dimension(500, 650));

        // --- Logo ---
        ImageIcon originalIcon = new ImageIcon(Resetpassword.class.getResource("/Asset/SUT_emblem_standard.png"));
        Image img = originalIcon.getImage();
        Image newImg = img.getScaledInstance(90, 113, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(newImg));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Title ---
        Components_ titleComp = new Components_("ตั้งรหัสผ่านใหม่", "label", 22, 0xB8860B);
        ((JLabel) titleComp.getComponent()).setHorizontalAlignment(SwingConstants.CENTER);

        // --- ช่อง Username หรือ Email ---
        Components_ lblUser = new Components_("ชื่อผู้ใช้หรืออีเมล", "label", 15);
        ((JLabel) lblUser.getComponent()).setHorizontalAlignment(SwingConstants.LEFT);
        Components_ userComp = new Components_("", "textfield", 15, 0x000000, 0xFFFFFF, 0, 40);
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(Color.WHITE);
        userPanel.add(lblUser.getComponent(), BorderLayout.NORTH);
        userPanel.add(userComp.getComponent(), BorderLayout.CENTER);

        // --- ช่อง รหัสผ่านใหม่ ---
        Components_ lblNewPass = new Components_("รหัสผ่านใหม่", "label", 15);
        ((JLabel) lblNewPass.getComponent()).setHorizontalAlignment(SwingConstants.LEFT);
        Components_ newPassComp = new Components_("", "password", 15, 0x000000, 0xFFFFFF, 0, 40);
        JPanel newPassPanel = new JPanel(new BorderLayout());
        newPassPanel.setBackground(Color.WHITE);
        newPassPanel.add(lblNewPass.getComponent(), BorderLayout.NORTH);
        newPassPanel.add(newPassComp.getComponent(), BorderLayout.CENTER);

        // --- ช่อง ยืนยันรหัสผ่าน ---
        Components_ lblConfirmPass = new Components_("ยืนยันรหัสผ่านใหม่", "label", 15);
        ((JLabel) lblConfirmPass.getComponent()).setHorizontalAlignment(SwingConstants.LEFT);
        Components_ confirmPassComp = new Components_("", "password", 15, 0x000000, 0xFFFFFF, 0, 40);
        JPanel confirmPassPanel = new JPanel(new BorderLayout());
        confirmPassPanel.setBackground(Color.WHITE);
        confirmPassPanel.add(lblConfirmPass.getComponent(), BorderLayout.NORTH);
        confirmPassPanel.add(confirmPassComp.getComponent(), BorderLayout.CENTER);

        // --- ปุ่ม Reset Password ---
        Components_ btnResetComp = new Components_("ตั้งรหัสผ่านใหม่", "button", 16, 0xFFFFFF, 0xD25F27, 200, 50);
        JButton btnReset = (JButton) btnResetComp.getComponent();

        // --- ปุ่ม ยกเลิก ---
        String cancelHtml = "<html><center><font color='#888888'>ยกเลิก — กลับไปหน้าเข้าสู่ระบบ</font></center></html>";
        Components_ btnBackComp = new Components_(cancelHtml, "label", 13);
        ((JLabel) btnBackComp.getComponent()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel) btnBackComp.getComponent()).setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- Action: Reset Password ---
        btnReset.addActionListener(e -> {
            String usernameOrEmail = ((JTextField) userComp.getComponent()).getText().trim();
            JPasswordField txtNewPass     = (JPasswordField) newPassComp.getComponent();
            JPasswordField txtConfirmPass = (JPasswordField) confirmPassComp.getComponent();

            String newPassword     = new String(txtNewPass.getPassword());
            String confirmPassword = new String(txtConfirmPass.getPassword());

            // 1. ตรวจว่ากรอกครบ
            if (usernameOrEmail.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(resetCard,
                        "กรุณากรอกข้อมูลให้ครบทุกช่อง",
                        "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. ตรวจรหัสผ่านตรงกัน
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(resetCard,
                        "รหัสผ่านใหม่และยืนยันรหัสผ่านไม่ตรงกัน",
                        "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
                txtNewPass.setText("");
                txtConfirmPass.setText("");
                return;
            }

            // 3. ตรวจความยาวรหัสผ่าน (อย่างน้อย 6 ตัวอักษร)
            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(resetCard,
                        "รหัสผ่านต้องมีอย่างน้อย 6 ตัวอักษร",
                        "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
                txtNewPass.setText("");
                txtConfirmPass.setText("");
                return;
            }

            // 4. ตรวจว่า username/email มีอยู่ในระบบ
            Datauser auth = new Datauser();
            boolean userExists = auth.isUserExistsByUsernameOrEmail(usernameOrEmail);
            if (!userExists) {
                JOptionPane.showMessageDialog(resetCard,
                        "ไม่พบชื่อผู้ใช้หรืออีเมลนี้ในระบบ",
                        "ไม่พบข้อมูล", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 5. อัปเดตรหัสผ่านลง Database
            boolean success = auth.updatePassword(usernameOrEmail, newPassword);
            if (success) {
                JOptionPane.showMessageDialog(resetCard,
                        "เปลี่ยนรหัสผ่านสำเร็จ!\nกรุณาเข้าสู่ระบบด้วยรหัสผ่านใหม่",
                        "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);

                // ล้างทุก field
                ((JTextField) userComp.getComponent()).setText("");
                txtNewPass.setText("");
                txtConfirmPass.setText("");

                // กลับไปหน้า Login
                CardLayout cl = (CardLayout) cardContainer.getLayout();
                cl.show(cardContainer, "LOGIN_PAGE");
            } else {
                JOptionPane.showMessageDialog(resetCard,
                        "เกิดข้อผิดพลาด ไม่สามารถเปลี่ยนรหัสผ่านได้\nกรุณาลองใหม่อีกครั้ง",
                        "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- Action: ยกเลิก ---
        ((JLabel) btnBackComp.getComponent()).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // ล้าง field ก่อนกลับ
                ((JTextField) userComp.getComponent()).setText("");
                ((JPasswordField) newPassComp.getComponent()).setText("");
                ((JPasswordField) confirmPassComp.getComponent()).setText("");

                CardLayout cl = (CardLayout) cardContainer.getLayout();
                cl.show(cardContainer, "LOGIN_PAGE");
            }
        });

        // --- จัดวางองค์ประกอบ ---
        resetCard.add(logo);
        resetCard.add(Box.createVerticalStrut(15));
        resetCard.add(titleComp.getComponent());
        resetCard.add(Box.createVerticalStrut(25));
        resetCard.add(userPanel);
        resetCard.add(Box.createVerticalStrut(15));
        resetCard.add(newPassPanel);
        resetCard.add(Box.createVerticalStrut(15));
        resetCard.add(confirmPassPanel);
        resetCard.add(Box.createVerticalStrut(25));
        resetCard.add(btnResetComp.getComponent());
        resetCard.add(Box.createVerticalStrut(10));
        resetCard.add(btnBackComp.getComponent());

        mainPanel.add(resetCard);
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}
