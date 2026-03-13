package Home;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Components.Components_;
import Components.SideBar;
import ListPage.AdminListPage;
import Map.Map;
import User.Datauser;
import User.User;

public class Home {

	private JPanel mainPanel, card = null;
	private Font mainFont = new Font("Prompt Light", Font.PLAIN, 14);
	private Font titleFont = new Font("Prompt Light", Font.BOLD, 18);
	private Color primaryColor = new Color(160, 110, 50);
	private Color bgColor = new Color(253, 251, 245);
	private boolean d_;
	 private SideBar sidebar;
	public Home(JPanel cardContainer, boolean d) {

		this.d_ = d;
		card = cardContainer;

		mainPanel = new JPanel(new BorderLayout());

		mainPanel.add(new SideBar().createHeader(cardContainer), BorderLayout.NORTH);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		contentPanel.setPreferredSize(new Dimension(1700, 1030));

		contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

		JPanel menuBar = createMenuBar(cardContainer);

		JLabel section1Title = new JLabel("หน้าแรก");
		section1Title.setFont(new Font("Prompt Light", Font.BOLD, 14));
		section1Title.setForeground(new Color(180, 100, 50));
		section1Title.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel quickAccessPanel = new JPanel(new GridLayout(1, 3, 20, 0));
		quickAccessPanel.setOpaque(false);
		quickAccessPanel.setMaximumSize(new Dimension(2000, 120));

		quickAccessPanel.add(createInfo("ค้นหา", "ค้นหาอาคารและห้องเรียน", "🔍", cardContainer));
		quickAccessPanel.add(createInfo("รายการ", "ดูรายการอาคารทั้งหมด", "📋", cardContainer));
		quickAccessPanel.add(createInfo("รายงาน", "สถิติการใช้งาน", "📊", cardContainer));

		JLabel section2Title = new JLabel("recommend");
		section2Title.setAlignmentX(Component.LEFT_ALIGNMENT);
		section2Title.setFont(new Font("Prompt Light", Font.PLAIN, 16));
		section2Title.setForeground(new Color(180, 140, 0));
		Datauser db = new Datauser();
		JPanel recContainer = new JPanel(new GridLayout(1, 3, 20, 0)); 
        recContainer.setOpaque(false); 
        recContainer.setMaximumSize(new Dimension(2000, 140));  
        recContainer.setAlignmentX(Component.CENTER_ALIGNMENT);  
        List<String[]> topList = db.getTopViewedBuildings(3); 

        
        if (topList.isEmpty()) {
           
            recContainer.add(createRec(0,"ไม่มีข้อมูลยอดนิยม", "" , 0 , 0));
        } else {
            for (String[] item : topList) {
                String name = item[1];       
                String imagePath = item[4]; 
                int bid = 0;
                double lat = Double.parseDouble(item[6]); // map_y
                double lon = Double.parseDouble(item[5]); // map_x
                if (item.length > 7 && item[7] != null) {
                    bid = Integer.parseInt(item[7]);
                }
                recContainer.add(createRec(bid , name, imagePath , lat , lon));
            }
        }

		RoundedPanel mapPanel = new RoundedPanel(20, Color.WHITE);
		mapPanel.setPreferredSize(new Dimension(800, 200));
		mapPanel.setMaximumSize(new Dimension(2000, 200));
		mapPanel.setLayout(new GridBagLayout());
		 sidebar = new SideBar("หน้าหลัก");
		 contentPanel.add(sidebar.getSideBar(cardContainer));
		 

		contentPanel.add(Box.createVerticalStrut(20));
		contentPanel.add(section1Title);
		contentPanel.add(Box.createVerticalStrut(10));
		contentPanel.add(quickAccessPanel);
		contentPanel.add(Box.createVerticalStrut(20));
		contentPanel.add(section2Title);
		contentPanel.add(Box.createVerticalStrut(10));
		
 
       

     
        contentPanel.add(recContainer);
        contentPanel.add(Box.createVerticalStrut(20));
		JPanel Map = new Map().getMap(cardContainer);
		JScrollPane a = new JScrollPane(Map);
		a.setBorder(null);
		a.setPreferredSize(new Dimension(1600, 800));

		a.getVerticalScrollBar().setUnitIncrement(30);
		contentPanel.add(a);

		JScrollPane scrollPane = new JScrollPane(contentPanel);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		mainPanel.add(scrollPane, BorderLayout.CENTER);
	}

	public JPanel getPanel() {
		if (this.d_) {
			return new HomeAdmin(card).getPanel();
		}
		return mainPanel;
	}

	private JPanel createMenuBar(JPanel c_) {
		JPanel menuBar = new JPanel(new GridLayout(1, 4, 10, 0));
		menuBar.setOpaque(false);
		menuBar.setMaximumSize(new Dimension(2000, 50));
		menuBar.setPreferredSize(new Dimension(800, 50));

		menuBar.add(new Components_("home", "button", 16, 0xFFFFFF, 0xA06E32, 0, 50).getComponent());

		Components_ searchBtn = new Components_("search", "button", 16, 0x000000, 0xF5F5F5, 0, 50);
		searchBtn.getComponent().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CardLayout cl = (CardLayout) c_.getLayout();
				cl.show(c_, "SEARCH_PAGE");
			}
		});
		menuBar.add(searchBtn.getComponent());

		menuBar.add(new Components_("list", "button", 16, 0x000000, 0xF5F5F5, 0, 50).getComponent());
		menuBar.add(new Components_("report", "button", 16, 0x000000, 0xF5F5F5, 0, 50).getComponent());

		return menuBar;
	}

	private JPanel createInfo(String title, String subtitle, String icon, JPanel c_) {
		RoundedPanel panel = new RoundedPanel(20, Color.WHITE);
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(15, 15, 15, 15));
		if (title == "ค้นหา") {
			panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					CardLayout cl = (CardLayout) c_.getLayout();
					cl.show(c_, "SEARCH_PAGE");
				}

			});
		}
		if (title == "รายการ") {
			panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					CardLayout cl = (CardLayout) c_.getLayout();
					cl.show(c_, "LIST_PAGE");
				}

			});
		}
		if (title == "รายงาน") {
			panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					CardLayout cl = (CardLayout) c_.getLayout();
					cl.show(c_, "REPORT_PAGE");
				}

			});
		}

		JLabel lbl = new JLabel("<html><font size='5'>" + icon + " " + title + "</font><br><font size='3' color='#777'>"
				+ subtitle + "</font></html>");
		lbl.setFont(mainFont);

		panel.add(lbl, BorderLayout.CENTER);

		return panel;
	}
	private JPanel createRec(int bid,String name, String imagePathInput , double lat , double lon) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(30, 40, 30, 40)); 
        card.setMaximumSize(new Dimension(2000, 120)); 

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setPreferredSize(new Dimension(140, 90)); 
        imgPanel.setBackground(Color.WHITE);
        imgPanel.setOpaque(false);

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        
    
        if (imagePathInput != null && !imagePathInput.isEmpty()) {
            try {
                ImageIcon icon = null;
                if (imagePathInput.toLowerCase().startsWith("http")) {
                    icon = new ImageIcon(new java.net.URL(imagePathInput));
                } else {
                    java.net.URL imgUrl = getClass().getResource(imagePathInput);
                    if (imgUrl != null) icon = new ImageIcon(imgUrl);
                }

                if (icon != null) {
                    Image scaledImg = icon.getImage().getScaledInstance(140, 90, Image.SCALE_SMOOTH);
                    imgLabel.setIcon(new ImageIcon(scaledImg));
                } else {
                    imgLabel.setText("No Image");
                }
            } catch (Exception e) {
                // e.printStackTrace();
                imgLabel.setText("Error");
            }
        } else {
            imgLabel.setText("No Data");
        }
        imgPanel.add(imgLabel, BorderLayout.CENTER);

      
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.weightx = 0;  
        gbc.anchor = GridBagConstraints.CENTER;  
        gbc.insets = new Insets(0, 0, 0, 15);  
        card.add(imgPanel, gbc);

        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(name, SwingConstants.RIGHT);
        titleLabel.setFont(new Font("Prompt Light", Font.BOLD, 15));  
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnGroup.setOpaque(false);

        JButton locBtn = new JButton("ตำแหน่ง");
        locBtn.setBorderPainted(false);
        locBtn.setFocusPainted(false);
        locBtn.setBackground(primaryColor);
        locBtn.setForeground(Color.WHITE);
        locBtn.setFont(new Font("Prompt Light", Font.BOLD, 12));
        locBtn.setPreferredSize(new Dimension(80, 30));
        
        
        locBtn.addActionListener(e -> {
            try {
              
                String query = java.net.URLEncoder.encode(name, "UTF-8");
                String url  ;  
                if (lat != 0.0 && lon != 0.0) {
		             
                	url = "https://www.google.com/maps/search/?api=1&query=" + lon + "," + lat;	        
                	
                } else {
	                
	              
	                url = "https://www.google.com/maps/search/?api=1&query=" + query;
	            }
                User user = new Datauser().readUserFromFile();
	            if (user != null) {
	                 new Datauser().insertAccessLog(bid, user.userId);
	            }
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new java.net.URI(url));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnGroup.add(locBtn);

        rightPanel.add(titleLabel, BorderLayout.CENTER);  
        rightPanel.add(btnGroup, BorderLayout.SOUTH);

       
        gbc.gridx = 1; 
        gbc.gridy = 0;
        gbc.weightx = 1.0;  
        gbc.fill = GridBagConstraints.HORIZONTAL;  
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(rightPanel, gbc);
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        return card;
    }
	private void getimage(String path) {
		String imgPath = path;

	}

	class RoundedPanel extends JPanel {
		private int radius;
		private Color color;

		public RoundedPanel(int radius, Color color) {
			this.radius = radius;
			this.color = color;
			setOpaque(false);
		}

		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(color);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
		}
	}
}
