package Map; 

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Data.Building;
import User.Datauser;
import User.User;

public class BuildingDetailPanel {

	private JPanel mainPanel;

	private final Color PRIMARY_COLOR = new Color(160, 110, 50);
	private final Color BG_COLOR = new Color(253, 251, 245);
	private final Color TEXT_COLOR = new Color(50, 50, 50);
	private final Color SUBTEXT_COLOR = new Color(120, 120, 120);

	private Building b;
	private JLabel lblImage;
	private JLabel lblTitle;
	private JLabel lblCategory;
	private JTextArea txtDescription;
	private JButton btnBack;
	private JButton btnLocation;

	public BuildingDetailPanel(ActionListener onBackClick, int bid) {

		mainPanel = new RoundedPanel(20, Color.WHITE);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		mainPanel.setPreferredSize(new Dimension(440, 800));
		mainPanel.setMaximumSize(new Dimension(800,960));
		b = new Datauser().getBuildingById(bid);

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		topPanel.setMaximumSize(new Dimension(500, 40));
		topPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		RoundedPanel imageWrapper = new RoundedPanel(20, Color.LIGHT_GRAY);
		imageWrapper.setPreferredSize(new Dimension(180, 460));
		imageWrapper.setMaximumSize(new Dimension(440, 460));
		imageWrapper.setLayout(new BorderLayout());
		imageWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

		lblImage = new JLabel("No Image", SwingConstants.CENTER);
		lblImage.setForeground(Color.WHITE);
		imageWrapper.add(lblImage, BorderLayout.CENTER);

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setOpaque(false);
		infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		lblTitle = new JLabel("ชื่ออาคาร...");
		lblTitle.setFont(new Font("Prompt Light", Font.BOLD, 22));
		lblTitle.setForeground(PRIMARY_COLOR);
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

		lblCategory = new JLabel("• หมวดหมู่...");
		lblCategory.setFont(new Font("Prompt Light", Font.PLAIN, 14));
		lblCategory.setForeground(SUBTEXT_COLOR);
		lblCategory.setAlignmentX(Component.LEFT_ALIGNMENT);

		txtDescription = new JTextArea("รายละเอียด...");
        txtDescription.setFont(new Font("Prompt Light", Font.PLAIN, 16));
        txtDescription.setForeground(TEXT_COLOR);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setEditable(false);
        txtDescription.setOpaque(false);
        
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        
 
        scrollDesc.setOpaque(false);
        scrollDesc.getViewport().setOpaque(false);
        
         
        scrollDesc.setBorder(null);
        
        
        scrollDesc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollDesc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
         
        scrollDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        
        scrollDesc.setPreferredSize(new Dimension(400, 400)); 
        scrollDesc.setMaximumSize(new Dimension(800, 600));  

        if (b != null) {
        
            lblTitle.setText(b.getName());
           
            
            String catName = getCategoryName(Integer.parseInt(b.getCategory()));
            lblCategory.setText("• หมวดหมู่: " + catName);

            String desc = b.getDescription();
            txtDescription.setText((desc == null || desc.isEmpty()) ? "ไม่มีรายละเอียดเพิ่มเติม" : desc);
            
   
            txtDescription.setCaretPosition(0); 
            
        } else {
            lblTitle.setText("ไม่พบข้อมูล (ID: " + b + ")");
            txtDescription.setText("เกิดข้อผิดพลาดในการดึงข้อมูล");
        }

       
      
		if (b != null) {

			lblTitle.setText(b.getName());

			String imgPath = b.getImagePath();

			if (imgPath != null && !imgPath.isEmpty()) {
				try {
					ImageIcon icon = null;

					if (imgPath.toLowerCase().startsWith("http")) {

						icon = new ImageIcon(new java.net.URL(imgPath));
					}

					else {
						java.net.URL imgUrl = getClass().getResource(imgPath);
						if (imgUrl != null) {
							icon = new ImageIcon(imgUrl);
						}
					}

				 
					if (icon != null && icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
						int compW = 440;
						int compH = 460;

						int imgW = icon.getIconWidth();
						int imgH = icon.getIconHeight();

						double scale = Math.min(
						    (double) compW / imgW,
						    (double) compH / imgH
						);

						int newW = (int) (imgW * scale);
						int newH = (int) (imgH * scale);

						Image img = icon.getImage()
						        .getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
						 
						lblImage.setIcon(new ImageIcon(img));
						lblImage.setText("");
					} else {
					 
						lblImage.setText("Image not found");
						lblImage.setIcon(null);
					}

				} catch (Exception e) {
					System.err.println("Error loading image: " + e.getMessage());
					lblImage.setText("Error loading");
					lblImage.setIcon(null);
				}
			} else {
				lblImage.setText("No Image");
				lblImage.setIcon(null);
			}
			String catName = getCategoryName(Integer.parseInt(b.getCategory()));
			lblCategory.setText("• หมวดหมู่: " + catName);

			String desc = b.getDescription();
			txtDescription.setText((desc == null || desc.isEmpty()) ? "ไม่มีรายละเอียดเพิ่มเติม" : desc);

			 
		} else {
			lblTitle.setText("ไม่พบข้อมูล (ID: " + b + ")");
			txtDescription.setText("เกิดข้อผิดพลาดในการดึงข้อมูล");
		}
		infoPanel.add(Box.createVerticalStrut(15));
		infoPanel.add(lblTitle);
		infoPanel.add(Box.createVerticalStrut(5));
		infoPanel.add(lblCategory);
		infoPanel.add(Box.createVerticalStrut(15));
		  infoPanel.add(scrollDesc);

		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		actionPanel.setOpaque(false);
		actionPanel.setMaximumSize(new Dimension(500, 60));
		actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		btnLocation = createPrimaryButton("นำทาง Google Map", e -> {
			  if (b != null) {
			        try {
			         
			            double lat = b.getMapX(); 
			            double lon = b.getMapY(); 
			            
			            String url;

			          
			            if (lat != 0.0 && lon != 0.0) {
			             
			                url = "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lon;
			            } else {
			                
			                String query = java.net.URLEncoder.encode(b.getName(), "UTF-8");
			                url = "https://www.google.com/maps/search/?api=1&query=" + query;
			            }

			         
			            User user = new Datauser().readUserFromFile();
			            if (user != null) {
			                 new Datauser().insertAccessLog(bid, user.userId);
			            }

			 
			            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			                Desktop.getDesktop().browse(new java.net.URI(url));
			            } else {
			                JOptionPane.showMessageDialog(null, "ไม่สามารถเปิด Browser ได้");
			            }

			        } catch (Exception ex) {
			            ex.printStackTrace();
			            JOptionPane.showMessageDialog(null, "เกิดข้อผิดพลาด: " + ex.getMessage());
			        }
			    }
		});

	 
		btnLocation.setPreferredSize(new Dimension(200, 40));

	 
		actionPanel.add(btnLocation);
		mainPanel.add(topPanel);
		mainPanel.add(Box.createVerticalStrut(15));
		mainPanel.add(imageWrapper);
		mainPanel.add(infoPanel);
		mainPanel.add(Box.createVerticalGlue());
		mainPanel.add(Box.createVerticalStrut(20));
		mainPanel.add(actionPanel);
	}

 
	private String getCategoryName(int catId) {
		switch (catId) {
		case 1:
			return "อาคารเรียน";
		case 2:
			return "ร้านค้า/อาหาร";
		case 3:
			return "หอพัก";
		case 4:
			return "บรรณสาร";
		case 5:
			return "บริการ/เครื่องมือ";
		case 6:
			return "สนามกีฬา";
		case 7:
			return "ยิม/ฟิตเนส";
		case 8:
			return "ตู้ ATM";
		case 9:
			return "หอดูดาว";
		case 10:
			return "อาคารค้นคว้า";
		case 11:
			return "ท่องเที่ยวธรรมชาติ";
		case 12:
			return "อุโมงค์ต้นไม้";
		case 13:
			return "เรื่องเล่าผี";
		default:
			return "ทั่วไป";
		}
	}

	public JPanel getPanel() {
		return mainPanel;
	}

	public void setBuildingData(String name, String category, String desc, String imagePath) {
		lblTitle.setText(name);
		lblCategory.setText("• หมวดหมู่: " + category);
		txtDescription.setText((desc == null || desc.isEmpty()) ? "ไม่มีรายละเอียด" : desc);

		// Logic โหลดรูป
		if (imagePath != null && !imagePath.isEmpty()) {
			try {
				// พยายามโหลดรูป
				java.net.URL imgUrl = getClass().getResource(imagePath);
				if (imgUrl != null) {
					ImageIcon icon = new ImageIcon(imgUrl);
					Image img = icon.getImage().getScaledInstance(240, 160, Image.SCALE_SMOOTH);
					lblImage.setIcon(new ImageIcon(img));
					lblImage.setText("");
				} else {
					lblImage.setText("Image not found");
					lblImage.setIcon(null);
				}
			} catch (Exception e) {
				lblImage.setText("Error loading image");
			}
		} else {
			lblImage.setText("No Image");
			lblImage.setIcon(null);
		}
	}

	private JButton createIconButton(String text, ActionListener action) {
		JButton btn = new JButton(text);
		btn.setFont(new Font("Prompt Light", Font.BOLD, 14));
		btn.setForeground(SUBTEXT_COLOR);
		btn.setBackground(Color.WHITE);
		btn.setBorder(null);
		btn.setFocusPainted(false);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setContentAreaFilled(false);

		// Hover Effect
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				btn.setForeground(PRIMARY_COLOR);
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				btn.setForeground(SUBTEXT_COLOR);
			}
		});

		btn.addActionListener(action);
		return btn;
	}

	private JButton createPrimaryButton(String text, ActionListener action) {
		JButton btn = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
				super.paintComponent(g);
			}
		};
		btn.setFont(new Font("Prompt Light", Font.PLAIN, 16));
		btn.setForeground(Color.WHITE);
		btn.setBackground(PRIMARY_COLOR);
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setContentAreaFilled(false);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.addActionListener(action);
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
}