package Map;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import Components.Components_;
import Home.HomeAdmin;

import java.util.List;
import java.util.function.Consumer;
import java.util.ArrayList;

public class MapPage {

	private JPanel mainPanel, card;

	private String[] labels = { "อาคารเรียน", "โรงอาหาร", "หอพัก", "บรรณสาร", "บริการ/เครื่องมือ", "สนามกีฬา",
			"ยิม/ฟิตเนส", "ตู้ ATM", "หอดูดาว", "อาคารค้นคว้า", "ท่องเที่ยวธรรมชาติ", "อุโมงค์ต้นไม้",
			"เรื่องเล่าเกี่ยวกีบผี" };
	private String[] cat = { "All", "อาคารเรียน", "โรงอาหาร", "หอพัก", "บรรณสาร", "บริการ/เครื่องมือ", "สนามกีฬา",
			"ยิม/ฟิตเนส", "ตู้ ATM", "หอดูดาว", "อาคารค้นคว้า", "ท่องเที่ยวธรรมชาติ", "อุโมงค์ต้นไม้",
			"เรื่องเล่าเกี่ยวกีบผี" };
	private Color bgGray = new Color(243, 244, 246);
	private Color activeBlue = new Color(79, 70, 229);
	private Color textDark = new Color(31, 41, 55);
	private Color textGray = new Color(107, 114, 128);
	private Color mapBgColor = new Color(225, 250, 240);
	private boolean page = false;
	private JPanel sidebar;
	private JPanel Map;
	private java.util.List<JButton> categoryButtons = new java.util.ArrayList<>();
	private boolean d_ = false;

	public MapPage(JPanel cardContainer, boolean d) {

		this.d_ = d;
		card = cardContainer;
 
		mainPanel = new JPanel(new BorderLayout());

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
				MapPage MapPage = new MapPage(cardContainer, d_);
				cardContainer.add(MapPage.getPanel(), "MAP_PAGE");

				CardLayout cl = (CardLayout) cardContainer.getLayout();
				cl.show(cardContainer, "HOME_PAGE");
				System.out.println("Focus on map.. ");

			}
		});
		topPanel.add(btnBack);
		mainPanel.add(topPanel, BorderLayout.NORTH);

		JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
		contentPanel.setOpaque(false);

		sidebar = createLeftSidebar();

		JLayeredPane mapArea = new JLayeredPane();
		mapArea.setPreferredSize(new Dimension(1600, 800));

		Map = new Map().getMap_callback(cardContainer, this.repage());
		JScrollPane a = new JScrollPane(Map);

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

	private JPanel pinbox() {
		String[] iconPaths = { "/Asset/pin/study.png", "/Asset/pin/pin_Dining.png", "/Asset/pin/pin_Housing.png",
				"/Asset/pin/pin_Library.png", "/Asset/pin/pin_Recreation.png", "/Asset/pin/pin_Sport.png",
				"/Asset/pin/pin_Gym.png", "/Asset/pin/yellow_icon.png", "/Asset/pin/pin_Observatory.png",
				"/Asset/pin/pin_Research_building.png" ,"/Asset/pin/pin_Forest_tunnel.png", 
				"/Asset/pin/pin_Natural tourist attractions.png", "/Asset/pin/pin_ghost_Desu.png" };

		List<Image> loadedIcons = new ArrayList<>();
		for (String path : iconPaths) {
			try {

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

				int iconX = 14;
				int drawY = 14;
				int gap = 50;

				for (Image icon : loadedIcons) {
					g2.drawImage(icon, iconX, drawY + 2, null);
					drawY += gap;
				}
			}
		};
		legendPanel.setBackground(Color.WHITE);
		legendPanel.setLayout(null);
		legendPanel.setOpaque(false);
		legendPanel.setBounds(20, 20, 140, 170);

		int lblY = 14;
		int gap = 49;

		for (String text : labels) {
			JLabel lbl = new JLabel(text);
			lbl.setFont(new Font("Prompt Light", Font.PLAIN, 24));
			lbl.setForeground(new Color(60, 60, 60));
			lbl.setBounds(43, lblY, 190, 40);
			legendPanel.add(lbl);
			lblY += gap;
		}
		return legendPanel;

	}

	public Consumer<Integer> repage() {

		return (buildingId) -> {
			System.out.println("Callback triggered for Building ID: " + buildingId);

			ActionListener backAction = e -> {
				CardLayout cl = (CardLayout) card.getLayout();

				cl.show(card, "HOME_PAGE");
			};

			ActionListener locationAction = e -> {
				System.out.println("Focus on map...");
			};

			this.page = true;

			BuildingDetailPanel detailPanel = new BuildingDetailPanel(backAction, buildingId);

			this.sidebar.removeAll();
			this.sidebar.add(detailPanel.getPanel());
			this.sidebar.revalidate();
			this.sidebar.repaint();
		};
	}

	public JPanel getPanel() {
//		if(this.d_) {
//			return new MapAdmin(card).getPanel();
//		}
		return mainPanel;
	}

	private JPanel createLeftSidebar() {

		JPanel sidebar = new JPanel();
		sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
		sidebar.setOpaque(false);
		sidebar.setPreferredSize(new Dimension(450, 0));

		RoundedPanel searchPanel = new RoundedPanel(15, Color.WHITE);
		searchPanel.setLayout(new BorderLayout());
		searchPanel.setMaximumSize(new Dimension(2000, 50));
		searchPanel.setBorder(new EmptyBorder(5, 15, 5, 5));

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
		scrollList.setBorder(BorderFactory.createEmptyBorder());
		scrollList.getVerticalScrollBar().setUnitIncrement(16);

		RoundedPanel listContainer = new RoundedPanel(15, Color.WHITE);
		listContainer.setLayout(new BorderLayout());
		listContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
		listContainer.add(listHeader, BorderLayout.NORTH);
		listContainer.add(scrollList, BorderLayout.CENTER);

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
			int categoryIndex = i;
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
					if (this.Map instanceof GridPanel) {
						((GridPanel) this.Map).setFilter(categoryIndex);
					}
				}
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
		btn.setFont(new Font("Prompt Light", Font.BOLD, 12));
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
}