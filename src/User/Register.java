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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import Components.Components_;

public class Register  {

	private JPanel cardContainer;
	private JPanel mainPanel;
	public Register(JPanel cardContainer) {
		mainPanel = new JPanel(new GridBagLayout());
		this.cardContainer = cardContainer;

		JPanel cardPanel = new JPanel() {
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
		cardPanel.setOpaque(false);
		cardPanel.setBackground(Color.WHITE);
		cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
		cardPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
		cardPanel.setPreferredSize(new Dimension(500, 750));

		ImageIcon originalIcon = new ImageIcon(Register.class.getResource("/Asset/SUT_emblem_standard.png"));

		Image img = originalIcon.getImage();
		Image newImg = img.getScaledInstance(110, 138, Image.SCALE_SMOOTH);

		JLabel logo = new JLabel(new ImageIcon(newImg));
		logo.setAlignmentX(Component.CENTER_ALIGNMENT);
		cardPanel.add(logo);

		Components_ title = new Components_("SUT Navigator Register", "label", 24, 0xB8860B);
		((JLabel) title.getComponent()).setHorizontalAlignment(SwingConstants.CENTER);
		
		Components_ userLbl = new Components_("ชื่อผู้ใช้", "label", 16);
		((JLabel) userLbl.getComponent()).setHorizontalAlignment(SwingConstants.LEFT);
		Components_ userFld = new Components_("", "textfield", 16, 0x000000, 0xFFFFFF, 0, 40);
		JPanel userPanel = new JPanel(new BorderLayout());
		userPanel.setBackground(Color.WHITE);
		userPanel.add(userLbl.getComponent(), BorderLayout.NORTH);
		userPanel.add(userFld.getComponent(), BorderLayout.CENTER);
		
		Components_ emailLbl = new Components_("อีเมล", "label", 16);
		((JLabel) emailLbl.getComponent()).setHorizontalAlignment(SwingConstants.LEFT);
		Components_ emailFld = new Components_("", "textfield", 16, 0x000000, 0xFFFFFF, 0, 40);
		JPanel emailPanel = new JPanel(new BorderLayout());
		emailPanel.setBackground(Color.WHITE);
		emailPanel.add(emailLbl.getComponent(), BorderLayout.NORTH);
		emailPanel.add(emailFld.getComponent(), BorderLayout.CENTER);
		
		
		Components_ passLbl = new Components_("รหัสผ่าน", "label", 16);
		((JLabel) passLbl.getComponent()).setHorizontalAlignment(SwingConstants.LEFT);
		Components_ passFld = new Components_("", "password", 16, 0x000000, 0xFFFFFF, 0, 40);
		JPanel passPanel = new JPanel(new BorderLayout());
		passPanel.setBackground(Color.WHITE);
		passPanel.add(passLbl.getComponent(), BorderLayout.NORTH);
		passPanel.add(passFld.getComponent(), BorderLayout.CENTER);

		
		
		Components_ btnLogin = new Components_("ลงทะเบียน", "button", 18, 0xFFFFFF, 0xD25F27, 200, 50);

		String htmlText = "<html><center><font color='#D25F27'>มีบัญชีแล้ว? กลับไปเข้าสู่ระบบ</font></center></html>";

		Components_ linkReg = new Components_(htmlText, "label", 12);
		((JLabel) linkReg.getComponent()).setHorizontalAlignment(SwingConstants.CENTER);

		((JButton) btnLogin.getComponent()).addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        
		        String username = ((JTextField) userFld.getComponent()).getText();
		        String email = ((JTextField) emailFld.getComponent()).getText();
		       
		        String pass = new String(((JPasswordField) passFld.getComponent()).getPassword());
 
		        
		        if (username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
		            JOptionPane.showMessageDialog(null, "กรุณากรอกข้อมูลให้ครบถ้วน", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
		            return;
		        }
 
		        Datauser newUser = new Datauser(username, email, pass);

		      
		        boolean isSuccess = newUser.registerUser();

		        if (isSuccess) {
		            JOptionPane.showMessageDialog(null, "สมัครสมาชิกสำเร็จ!", "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
		            
		          
		            CardLayout cl = (CardLayout) cardContainer.getLayout();
		            cl.show(cardContainer, "LOGIN_PAGE");
		            
		            
		            ((JTextField) userFld.getComponent()).setText("");
		            ((JTextField) emailFld.getComponent()).setText("");
		            ((JPasswordField) passFld.getComponent()).setText("");
		          
		            
		        } else {
		            
		            JOptionPane.showMessageDialog(null, "สมัครไม่สำเร็จ: ชื่อผู้ใช้หรืออีเมลอาจซ้ำ", "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
		        }
		    }
		});

		((JLabel) linkReg.getComponent()).setCursor(new Cursor(Cursor.HAND_CURSOR));

		cardPanel.add(Box.createVerticalStrut(20));
		cardPanel.add(title.getComponent());
		cardPanel.add(Box.createVerticalStrut(30));
		cardPanel.add(userPanel);
		cardPanel.add(Box.createVerticalStrut(15));
		cardPanel.add(emailPanel);
		cardPanel.add(Box.createVerticalStrut(15));
		cardPanel.add(passPanel);
		cardPanel.add(Box.createVerticalStrut(20));
		cardPanel.add(btnLogin.getComponent());
		cardPanel.add(linkReg.getComponent());
		
		((JLabel) linkReg.getComponent()).addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				CardLayout cl = (CardLayout) cardContainer.getLayout();
				cl.show(cardContainer, "LOGIN_PAGE");
			}

		});
		mainPanel.add(cardPanel);

	}
	public JPanel getPanel() {
		return mainPanel;
	}

}
