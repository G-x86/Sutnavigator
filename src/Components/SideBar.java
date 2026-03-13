package Components;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import Components.Components_;
 
import User.Datauser;
import User.Login;
import User.Register;
import User.Resetpassword;
 

public class SideBar {
	private Font mainFont = new Font("Prompt Light", Font.PLAIN, 14);

	private String type;

	public SideBar(String type) {
		this.type = type;
	}

	public SideBar() {
		 
	}

	public JPanel createHeader(JPanel cardContainer) {
		JPanel header = new RoundedPanel(25, new Color(30, 30, 30));
	 
		header.setPreferredSize(new Dimension(100, 50));
		header.setBorder(new EmptyBorder(5, 20, 0, 20));

		JLabel title = new JLabel("SUT-NAVIGATOR");
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Prompt Light", Font.BOLD, 16));

		JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		userPanel.setOpaque(false);

		JLabel userLabel = new JLabel(Datauser.username);
		userLabel.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent event) {
			 
				if (Datauser.username != null) {
					userLabel.setText(Datauser.username);
				} else {
					userLabel.setText("ผู้เยี่ยมชม");
				}
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
				// TODO Auto-generated method stub

			}

		});
		userLabel.setForeground(Color.WHITE);
		userLabel.setFont(mainFont);

		JButton logoutBtn = new JButton("logout");
		logoutBtn.setBackground(new Color(230, 80, 50));
		logoutBtn.setForeground(Color.WHITE);
		logoutBtn.setFocusPainted(false);
		logoutBtn.setBorderPainted(false);
		logoutBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Object[] options = { "ออกจากโปรแกรม", "ออกไปหน้าเข้าสู่ระบบ" };
				if (cardContainer == null) {
		        
		            return;
		        }
				int result = JOptionPane.showOptionDialog(cardContainer,  														 
						"คุณต้องการออกจากระบบใช่หรือไม่?",  
						"ยืนยันการออกจากระบบ",  
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.WARNING_MESSAGE, 
						null, 
						options, 
						options[1] 
				);

				if (result == 1) {

					Datauser.username = null;
				 
					    File file = new File("user.txt");
					    if (file.exists()) file.delete();

					    cardContainer.removeAll();
						Register register = new Register(cardContainer);
					 
						cardContainer.add(register.getPanel(), "REGISTER_PAGE");
						 
					    Login login = new Login(cardContainer);
					    cardContainer.add(login.getPanel(), "LOGIN_PAGE");
					    Resetpassword resetpassword = new Resetpassword(cardContainer);
					    cardContainer.add(resetpassword.getPanel(), "RESET_PAGE");
					CardLayout cl = (CardLayout) cardContainer.getLayout();
					cl.show(cardContainer, "LOGIN_PAGE");
					cardContainer.revalidate();
				    cardContainer.repaint();
					System.out.println("User logged out.");
				}else {
					System.exit(0);
				}
			}
		});
		userPanel.add(userLabel);
		userPanel.add(logoutBtn);

		header.add(title, BorderLayout.WEST);
		header.add(userPanel, BorderLayout.EAST);

		return header;
	}

	public JPanel getSideBar(JPanel c_) {
		JPanel menuBar = new JPanel(new GridLayout(1, 4, 10, 0));
		menuBar.setOpaque(false);
		menuBar.setMaximumSize(new Dimension(2000, 50));
		menuBar.setPreferredSize(new Dimension(800, 50));

		menuBar.add(createBtn(c_, "หน้าหลัก", "HOME_PAGE"));
		menuBar.add(createBtn(c_, "ค้นหา", "SEARCH_PAGE"));
		menuBar.add(createBtn(c_, "ลิสต์", "LIST_PAGE"));
		menuBar.add(createBtn(c_, "รายงาน", "REPORT_PAGE"));

		return menuBar;
	}

	private JComponent createBtn(JPanel c_, String name, String pageName) {
		boolean isActive = type.equalsIgnoreCase(name);
		int fg = isActive ? 0xFFFFFF : 0x000000;
		int bg = isActive ? 0xA06E32 : 0xFFFFFF;
 
		Components_ btn = new Components_(name, "button", 16, fg, bg, 0, 50);

		btn.getComponent().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CardLayout cl = (CardLayout) c_.getLayout();
			 
				cl.show(c_, pageName);
			}
		});

		return btn.getComponent();
	}
	class RoundedPanel extends JPanel {
		private int radius;
		private Color bgColor;

		public RoundedPanel(int radius, Color bgColor) {
			this.radius = radius;
			this.bgColor = bgColor;
			setOpaque(false);
			setLayout(new BorderLayout());
		}

		@Override
		protected void paintComponent(Graphics g) {
		    super.paintComponent(g); 
		    Graphics2D g2 = (Graphics2D) g;
		    
		    
		    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    g2.setColor(bgColor); 

		    int w = getWidth();
		    int h = getHeight();
		    g2.fillRoundRect(0, 0, w, h, radius, radius);
		    g2.fillRect(0, 0, w, h - radius); 
		}
	}
}