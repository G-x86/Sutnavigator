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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import Home.Home;
import ListPage.ListPage;
import Map.MapPage;
import ReportPage.ReportPage;
import Search.Search;

public class Login {
	private JPanel cardContainer;
	private JPanel mainPanel;

	public Login(JPanel cardContainer) {
		this.cardContainer = cardContainer;
		mainPanel = new JPanel(new GridBagLayout());
		
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
		cardPanel.setPreferredSize(new Dimension(500, 650));

		ImageIcon originalIcon = new ImageIcon(Login.class.getResource("/Asset/SUT_emblem_standard.png"));

		Image img = originalIcon.getImage();
		Image newImg = img.getScaledInstance(110, 138, Image.SCALE_SMOOTH);

		JLabel logo = new JLabel(new ImageIcon(newImg));
		logo.setAlignmentX(Component.CENTER_ALIGNMENT);
		cardPanel.add(logo);

		Components_ userLbl = new Components_("ชื่อผู้ใช้หรืออีเมล", "label", 16);

		((JLabel) userLbl.getComponent()).setHorizontalAlignment(SwingConstants.LEFT);

		Components_ userFld = new Components_("", "textfield", 16, 0x000000, 0xFFFFFF, 0, 40);

		JPanel userPanel = new JPanel(new BorderLayout());
		userPanel.setBackground(Color.WHITE);
		userPanel.add(userLbl.getComponent(), BorderLayout.NORTH);
		userPanel.add(userFld.getComponent(), BorderLayout.CENTER);

		Components_ passLbl = new Components_("รหัสผ่าน", "label", 16);
		((JLabel) passLbl.getComponent()).setHorizontalAlignment(SwingConstants.LEFT);

		Components_ passFld = new Components_("", "password", 16, 0x000000, 0xFFFFFF, 0, 40);

		JPanel passPanel = new JPanel(new BorderLayout());
		passPanel.setBackground(Color.WHITE);
		passPanel.add(passLbl.getComponent(), BorderLayout.NORTH);
		passPanel.add(passFld.getComponent(), BorderLayout.CENTER);

		Components_ title = new Components_("SUT Navigator", "label", 24, 0xB8860B);
		((JLabel) title.getComponent()).setHorizontalAlignment(SwingConstants.CENTER);

		Components_ btnLogin = new Components_("เข้าสู่ระบบ", "button", 18, 0xFFFFFF, 0xD25F27, 200, 50);

		String htmlText = "<html><center><font color='#D25F27'>ยังไม่มีบัญชี? สมัครสมาชิก</font></center></html>";

		Components_ linkReg = new Components_(htmlText, "label", 12);
		((JLabel) linkReg.getComponent()).setHorizontalAlignment(SwingConstants.CENTER);

		htmlText = "<html><center><font color='#888888'>ลืมรหัสผ่าน? คลิกที่นี่</font></center></html>";
		Components_ linkReg_ = new Components_(htmlText, "label", 12);
		((JLabel) linkReg_.getComponent()).setHorizontalAlignment(SwingConstants.CENTER);
		((JLabel) linkReg_.getComponent()).setCursor(new Cursor(Cursor.HAND_CURSOR));

		
		
		htmlText = "<html><center>สามารถเข้าสู่ระบบแบบทดลองได้</center></html>";
		Components_ guest = new Components_(htmlText, "label", 12);
		((JLabel) guest.getComponent()).setHorizontalAlignment(SwingConstants.CENTER);
		((JLabel) guest.getComponent()).setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		
		((JLabel) linkReg.getComponent()).setCursor(new Cursor(Cursor.HAND_CURSOR));

		cardPanel.add(Box.createVerticalStrut(20));
		cardPanel.add(title.getComponent());
		cardPanel.add(Box.createVerticalStrut(30));
		cardPanel.add(userPanel);
		cardPanel.add(Box.createVerticalStrut(15));
		cardPanel.add(passPanel);
		cardPanel.add(Box.createVerticalStrut(20));
		cardPanel.add(btnLogin.getComponent());
		cardPanel.add(linkReg.getComponent());
		cardPanel.add(linkReg_.getComponent());
		cardPanel.add(guest.getComponent());
		((JButton) btnLogin.getComponent()).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String username = ((JTextField) userFld.getComponent()).getText();
				String password = new String(((JPasswordField) passFld.getComponent()).getPassword());

				if (username.isEmpty() || password.isEmpty()) {
					JOptionPane.showMessageDialog(mainPanel, "กรุณากรอกชื่อผู้ใช้และรหัสผ่าน", "แจ้งเตือน",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				Datauser auth = new Datauser();
				boolean isLoginSuccess = auth.checkLogin(username, password);
				if (isLoginSuccess) {
					JOptionPane.showMessageDialog(mainPanel, "ยินดีต้อนรับคุณ " + username, "สำเร็จ",
							JOptionPane.INFORMATION_MESSAGE);
					Datauser.username = username; 
					boolean isAdmin = false;
					if (username.equals("admin")) { 
						isAdmin = true;
					}
					 
					Register register = new Register(cardContainer);
				 
					Home Home = new Home(cardContainer , isAdmin);
					Search Search = new Search(cardContainer);
				  
					ListPage listPage = new ListPage(cardContainer, isAdmin); 
					 
					ReportPage report = new ReportPage(cardContainer);
					MapPage MapPage = new MapPage(cardContainer , isAdmin);
					
				 
					cardContainer.removeAll();  

					cardContainer.add(MapPage.getPanel(), "MAP_PAGE");
					cardContainer.add(report.getPanel(), "REPORT_PAGE");
					 
					cardContainer.add(register.getPanel(), "REGISTER_PAGE");
				 
					cardContainer.add(Home.getPanel(), "HOME_PAGE");
					cardContainer.add(Search.getPanel(), "SEARCH_PAGE");
					
					 
					cardContainer.add(listPage.getPanel(), "LIST_PAGE");

					 
					CardLayout cl = (CardLayout) cardContainer.getLayout();
					cl.show(cardContainer, "HOME_PAGE");

					 
					((JPasswordField) passFld.getComponent()).setText("");

				} else {
					JOptionPane.showMessageDialog(mainPanel, "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง", "เข้าสู่ระบบล้มเหลว",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		((JLabel) linkReg_.getComponent()).addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				CardLayout cl = (CardLayout) cardContainer.getLayout();
				cl.show(cardContainer, "RESET_PAGE");
			}

		});
		((JLabel) guest.getComponent()).addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			 
				boolean isAdmin = false;
				Login login = new Login(cardContainer);
				Register register = new Register(cardContainer);
			 
				Home Home = new Home(cardContainer , isAdmin);
				Search Search = new Search(cardContainer);
			  
				ListPage listPage = new ListPage(cardContainer, isAdmin); 
				 
				ReportPage report = new ReportPage(cardContainer);
				MapPage MapPage = new MapPage(cardContainer , isAdmin);
				
			 
				cardContainer.removeAll();  
				cardContainer.add(listPage.getPanel(), "LIST_PAGE");
				cardContainer.add(MapPage.getPanel(), "MAP_PAGE");
				cardContainer.add(report.getPanel(), "REPORT_PAGE");
				cardContainer.add(login.getPanel(), "LOGIN_PAGE");
				cardContainer.add(register.getPanel(), "REGISTER_PAGE");
			 
				cardContainer.add(Home.getPanel(), "HOME_PAGE");
				cardContainer.add(Search.getPanel(), "SEARCH_PAGE");
				
				CardLayout cl = (CardLayout) cardContainer.getLayout();
				cl.show(cardContainer, "HOME_PAGE");
			}

		});
		((JLabel) linkReg.getComponent()).addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CardLayout cl = (CardLayout) cardContainer.getLayout();
				cl.show(cardContainer, "REGISTER_PAGE");
			}

		});
		mainPanel.add(cardPanel);
	}
	

	public JPanel getPanel() {
		
		return mainPanel;
	}
}
