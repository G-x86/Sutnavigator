package Map;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Components.Components_;
import User.Datauser;


public class MapAdmin {

	private JPanel mainPanel;
	// [NEW] เก็บ reference ของ panel แผนที่ไว้ใช้งาน
	private JPanel mapContentPanel; 

	private String[] labels = { "อาคารเรียน", "โรงอาหาร", "หอพัก", "บรรณสาร", "บริการ/เครื่องมือ", "สนามกีฬา",
			"ยิม/ฟิตเนส", "ตู้ ATM", "หอดูดาว", "อาคารค้นคว้า", "ท่องเที่ยวธรรมชาติ", "อุโมงค์ต้นไม้",
			"เรื่องเล่าเกี่ยวกีบผี" };
	private String[] cat = { "All","อาคารเรียน", "โรงอาหาร", "หอพัก", "บรรณสาร", "บริการ/เครื่องมือ", "สนามกีฬา",
			"ยิม/ฟิตเนส", "ตู้ ATM", "หอดูดาว", "อาคารค้นคว้า", "ท่องเที่ยวธรรมชาติ", "อุโมงค์ต้นไม้",
			"เรื่องเล่าเกี่ยวกีบผี"  };
 
	private String[] iconPaths = { 
			"/Asset/pin/blue_icon.png", 
			"/Asset/pin/gray_icon.png", 
			"/Asset/pin/green_icon.png",
			"/Asset/pin/orenge_icon.png", 
			"/Asset/pin/purple_icon.png", 
			"/Asset/pin/red_icon.png",
			"/Asset/pin/yellow_icon.png" 
	};

	private Color bgGray = new Color(243, 244, 246);
	private Color activeBlue = new Color(79, 70, 229);
	private Color textDark = new Color(31, 41, 55);
	// private Color textGray = new Color(107, 114, 128); // Unused
	// private Color mapBgColor = new Color(225, 250, 240); // Unused

	private java.util.List<JButton> categoryButtons = new java.util.ArrayList<>();

	public MapAdmin(JPanel cardContainer) {

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(bgGray);
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		topPanel.setOpaque(false);
		topPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

		Components_ btnBackComp = new Components_("← ย้อนกลับ", "label", 18, 0x666666);
		JLabel btnBack = (JLabel) btnBackComp.getComponent();
		btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnBack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CardLayout cl = (CardLayout) cardContainer.getLayout();
				cl.show(cardContainer, "HOME_PAGE");
			}
		});
		topPanel.add(btnBack);
		mainPanel.add(topPanel, BorderLayout.NORTH);

		JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
		contentPanel.setOpaque(false);

		JPanel sidebar = createLeftSidebar();

		JLayeredPane mapArea = new JLayeredPane();
		mapArea.setPreferredSize(new Dimension(1600, 800));
 
		mapContentPanel = new Map().getMap(cardContainer);
		
	 
		mapContentPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				 
				showAddBuildingDialog(e.getX(), e.getY());
			}
		});
		
	 
		if (mapContentPanel.getLayout() != null) {
			mapContentPanel.setLayout(null);
		}

		JScrollPane a = new JScrollPane(mapContentPanel);

		mapArea.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent evt) {
				a.setSize(mapArea.getSize());
				a.setBounds(0, 0, mapArea.getWidth(), mapArea.getHeight());
				a.revalidate();
			}
		});

		a.getVerticalScrollBar().setUnitIncrement(30);
		a.setBorder(null);

		mapArea.add(a, JLayeredPane.DEFAULT_LAYER);

		contentPanel.add(sidebar, BorderLayout.WEST);

		JPanel mapContainer = new JPanel(new BorderLayout());

		mapContainer.add(mapArea, BorderLayout.CENTER);

		contentPanel.add(mapContainer, BorderLayout.CENTER);

		mainPanel.add(contentPanel, BorderLayout.CENTER);

	}

	// [NEW] Method สำหรับแสดง Pop-up Dialog รับข้อมูลตึก
	private void showAddBuildingDialog(int x, int y) {
		JTextField nameField = new JTextField(20);
		JComboBox<String> categoryBox = new JComboBox<>(labels);

		// --- ส่วนแก้ปัญหาฟอนต์ภาษาไทย (สี่เหลี่ยม) ---
		// ถ้าเครื่องยังไม่มี Prompt Light ให้เปลี่ยนเป็น "Prompt Light" แทน
		Font thaiFont = new Font("Prompt Light", Font.PLAIN, 14); 
		
		JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
		
		JLabel lblName = new JLabel("ชื่อตึก:");
		lblName.setFont(thaiFont); 
		
		JLabel lblCat = new JLabel("ประเภท:");
		lblCat.setFont(thaiFont);  

		// ตั้งค่าฟอนต์ให้ Input Field และ Dropdown ด้วย (เผื่อพิมพ์ไทย)
		nameField.setFont(thaiFont);
		categoryBox.setFont(thaiFont);

		panel.add(lblName);
		panel.add(nameField);
		panel.add(lblCat);
		panel.add(categoryBox);

		// ปรับปุ่ม OK/Cancel ให้รองรับภาษาไทย
		UIManager.put("OptionPane.messageFont", thaiFont);
		UIManager.put("OptionPane.buttonFont", thaiFont);

		int result = JOptionPane.showConfirmDialog(mainPanel, panel, 
				"เพิ่มสถานที่ใหม่", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			String name = nameField.getText().trim();
			String category = (String) categoryBox.getSelectedItem();

			if (!name.isEmpty() && category != null) {
				
				// --- ส่วนเชื่อมต่อฐานข้อมูล (Database) ---
				try {
					Datauser db = new Datauser(); 
					int catId = getCategoryId(category); 
					
				 
					boolean isSuccess = db.addBuilding(name, catId, "-", "", (double)x, (double)y);

					if (isSuccess) {
					 
						addPinToMap(x, y, name, category);
						JOptionPane.showMessageDialog(mainPanel, "บันทึกข้อมูลเรียบร้อย!");
					} else {
						JOptionPane.showMessageDialog(mainPanel, "บันทึกไม่สำเร็จ (Database Error)");
					}
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(mainPanel, "เกิดข้อผิดพลาด: " + e.getMessage());
				}

			} else {
				JOptionPane.showMessageDialog(mainPanel, "กรุณาใส่ชื่อสถานที่");
			}
		}
	}
	// [NEW] Method สำหรับวาด Pin ลงบนแผนที่
	private void addPinToMap(int x, int y, String name, String category) {
		 
		String iconPath = "/Asset/pin/red_icon.png"; // Default
		switch (category) {
			case "Academic": iconPath = "/Asset/pin/blue_icon.png"; break;
			case "Dining": iconPath = "/Asset/pin/orenge_icon.png"; break;
			case "Housing": iconPath = "/Asset/pin/green_icon.png"; break;
			case "Library": iconPath = "/Asset/pin/purple_icon.png"; break;
			case "Recreation": iconPath = "/Asset/pin/yellow_icon.png"; break;
			
			
		}

		// 2. โหลดและย่อขนาดไอคอน
		ImageIcon pinIcon = null;
		try {
			java.net.URL imgUrl = getClass().getResource(iconPath);
			if (imgUrl != null) {
				ImageIcon original = new ImageIcon(imgUrl);
				// ย่อขนาดให้เหมาะสม (เช่น 32x32 pixel)
				Image scaled = original.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
				pinIcon = new ImageIcon(scaled);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not load icon: " + iconPath);
		}

		// 3. สร้าง JLabel เพื่อแสดงไอคอน
		JLabel pinLabel = new JLabel();
		if (pinIcon != null) {
			pinLabel.setIcon(pinIcon);
			// ปรับตำแหน่งให้จุดชี้ของ Pin (ด้านล่างตรงกลาง) ตรงกับจุดที่คลิก (x,y)
			// สมมติไอคอนขนาด 32x32, จุดชี้อยู่ตรงกลางล่าง
			int iconWidth = pinIcon.getIconWidth();
			int iconHeight = pinIcon.getIconHeight();
			pinLabel.setBounds(x - (iconWidth / 2), y - iconHeight, iconWidth, iconHeight);
		} else {
			// Fallback ถ้าโหลดรูปไม่ได้
			pinLabel.setText("📍");
			pinLabel.setBounds(x, y - 20, 20, 20);
			pinLabel.setForeground(Color.RED);
		}

		// เพิ่ม Tooltip ให้แสดงชื่อเมื่อเอาเมาส์ไปชี้
		pinLabel.setToolTipText("<html><b>" + name + "</b><br>(" + category + ")</html>");
		pinLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// 4. เพิ่ม JLabel ลงใน mapContentPanel
		mapContentPanel.add(pinLabel);
		// สั่งให้วาดใหม่เพื่อให้ Pin ปรากฏขึ้นมา
		mapContentPanel.revalidate();
		mapContentPanel.repaint();
	}


	private JPanel pinbox() {
		// [MODIFIED] ใช้ iconPaths ที่เป็น class member แทน local variable
		List<Image> loadedIcons = new ArrayList<>();
		for (String path : iconPaths) {
			try {
				// System.out.println(getClass().getResource(path)); // Comment out debug print
				java.net.URL imgUrl = getClass().getResource(path);
				if (imgUrl != null) {

					ImageIcon icon = new ImageIcon(imgUrl);

					BufferedImage scaled = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = scaled.createGraphics();
					g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g2.drawImage(icon.getImage(), 0, 0, 24, 24, null);
					g2.dispose();

					loadedIcons.add(scaled);
				} else {
					java.awt.image.BufferedImage fallback = new java.awt.image.BufferedImage(24, 24,
							java.awt.image.BufferedImage.TYPE_INT_ARGB);
					Graphics g = fallback.getGraphics();

					g.fillOval(0, 0, 24, 24);
					g.dispose();
					loadedIcons.add(fallback);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		JPanel legendPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setColor(Color.WHITE);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

				int iconX = 15;
				int drawY = 15;
				int gap = 50;
				
				// [MODIFIED] ป้องกัน index out of bounds ถ้าจำนวนรูปกับ label ไม่เท่ากัน
				int limit = Math.min(loadedIcons.size(), labels.length);
				for (int i = 0; i < limit; i++) {
					g2.drawImage(loadedIcons.get(i), iconX, drawY + 2, null);
					drawY += gap;
				}
			}
		};
		legendPanel.setBackground(Color.WHITE);
		legendPanel.setLayout(null);
		legendPanel.setOpaque(false);
		legendPanel.setBounds(20, 20, 140, 170);

		int lblY = 15;
		int gap = 49;

		for (String text : labels) {
			JLabel lbl = new JLabel(text);
			lbl.setFont(new Font("Prompt Light", Font.PLAIN, 24));
			lbl.setForeground(new Color(60, 60, 60));
			lbl.setBounds(43, lblY, 130, 40);
			legendPanel.add(lbl);
			lblY += gap;
		}
		return legendPanel;

	}

	public JPanel getPanel() {
		return mainPanel;
	}

	private JPanel createLeftSidebar() {
		JPanel sidebar = new JPanel();
		sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
		sidebar.setOpaque(false);
		sidebar.setPreferredSize(new Dimension(320, 0));

		RoundedPanel searchPanel = new RoundedPanel(15, Color.WHITE);
		searchPanel.setLayout(new BorderLayout());
		searchPanel.setMaximumSize(new Dimension(2000, 50));
		searchPanel.setBorder(new EmptyBorder(5, 15, 5, 5));
		// [TODO] Add search functionality here if needed

		JPanel catPanel = createCategories();

		JPanel listHeader = new JPanel(new BorderLayout());
		listHeader.setOpaque(false);
		listHeader.setMaximumSize(new Dimension(2000, 30));
		JLabel lblListTitle = new JLabel("category");
		lblListTitle.setFont(new Font("Prompt Light", Font.PLAIN, 18));
		lblListTitle.setForeground(textDark);
		listHeader.add(lblListTitle, BorderLayout.WEST);

		JPanel listItemsObj = new JPanel();
		listItemsObj.setLayout(new BoxLayout(listItemsObj, BoxLayout.Y_AXIS));
		listItemsObj.setBackground(Color.WHITE);

		listItemsObj.add(pinbox());

		JScrollPane scrollList = new JScrollPane(listItemsObj);
		scrollList.setBorder(BorderFactory.createEmptyBorder()); // ไร้ขอบ
		scrollList.getVerticalScrollBar().setUnitIncrement(16);

		RoundedPanel listContainer = new RoundedPanel(15, Color.WHITE);
		listContainer.setLayout(new BorderLayout());
		listContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
		listContainer.add(listHeader, BorderLayout.NORTH);
		listContainer.add(scrollList, BorderLayout.CENTER);

		// sidebar.add(searchPanel); // Uncomment if you use searchPanel
		// sidebar.add(Box.createVerticalStrut(15));
		sidebar.add(catPanel);
		sidebar.add(Box.createVerticalStrut(15));
		sidebar.add(listContainer);

		return sidebar;
	}

	private JPanel createCategories() {
		RoundedPanel panel = new RoundedPanel(15, Color.WHITE);
		panel.setLayout(new GridLayout(3, 2, 10, 10)); // 3 แถว 2 คอลัมน์
		panel.setBorder(new EmptyBorder(15, 15, 15, 15));
		panel.setMaximumSize(new Dimension(2000, 140));
		categoryButtons.clear();
		for (int i = 0; i < cat.length; i++) {
			String text = cat[i];

			boolean isActive = (i == 0);

			JButton btn = createCatBtn(text, isActive);
			btn.setContentAreaFilled(false);

			btn.addActionListener(e -> {
				for (JButton b : categoryButtons) {
					if (b == btn) {
						b.setBackground(activeBlue);
						b.setForeground(Color.WHITE);
					} else {
						b.setBackground(bgGray);
						b.setForeground(textDark);
					}
				}
				// [TODO] Add filtering logic here based on 'text' (category name)
			});

			categoryButtons.add(btn);
			panel.add(btn);
		}

		return panel;
	}

	private JButton createCatBtn(String text, boolean isActive) {
	    JButton btn = new JButton(text) {
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

	    btn.setFocusPainted(false);
	    btn.setBorderPainted(false);
	    
	    // [แก้ไขตรงนี้] เปลี่ยนเป็น Prompt Light, PLAIN, 12
	    btn.setFont(new Font("Prompt Light", Font.PLAIN, 12));
	    
	    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

	    if (isActive) {
	        btn.setBackground(activeBlue);
	        btn.setForeground(Color.WHITE);
	    } else {
	        btn.setBackground(new Color(243, 244, 246));
	        btn.setForeground(textDark);
	    }
	    return btn;
	}

	class RoundedPanel extends JPanel {
		private int radius;
		private Color bgColor;

		public RoundedPanel(int radius, Color bgColor) {
			this.radius = radius;
			this.bgColor = bgColor;
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(bgColor);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
		}
	}
    // RoundedLabel class wasn't used in the provided code snippet, but I kept it just in case.
	class RoundedLabel extends JLabel {
		private Color bgColor;

		public RoundedLabel(String text, Color bgColor, Color fgColor) {
			super(text);
			this.bgColor = bgColor;
			setForeground(fgColor);
			setFont(new Font("Prompt Light", Font.BOLD, 10));
			setBorder(new EmptyBorder(2, 8, 2, 8));
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(bgColor);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
			super.paintComponent(g);
		}
	}
	
	// ฟังก์ชันช่วยแปลงชื่อหมวดหมู่ เป็นรหัสตัวเลข (ต้องตรงกับในตาราง Categories ของคุณถ้ามี)
    private int getCategoryId(String categoryName) {
        switch (categoryName) {
            case "Academic": return 1;
            case "Dining": return 2;
            case "Housing": return 3;
            case "Library": return 4;
            case "Recreation": return 5;
            default: return 1; // ค่าเริ่มต้น
        }
    }
}
 