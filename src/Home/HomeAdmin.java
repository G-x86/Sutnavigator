package Home;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import Components.SideBar;
import Map.Map;
import User.Data_user;
import User.Datauser;
import java.util.List; // <--- เพิ่มบรรทัดนี้
import java.util.ArrayList; // <--- เพิ่มบรรทัดนี้ด้วย

public class HomeAdmin {

	private JPanel mainPanel;

	private final Color COLOR_BG = new Color(253, 251, 245);
	private final Color COLOR_PRIMARY = new Color(160, 110, 50);
	private final Color COLOR_WHITE = Color.WHITE;
	private final Color COLOR_TEXT_HEADER = new Color(50, 50, 50);
	private final Color COLOR_TEXT_SUB = new Color(100, 100, 100);

	private final Color COL_CARD_1 = new Color(66, 133, 244);
	private final Color COL_CARD_2 = new Color(52, 168, 83);
	private final Color COL_CARD_3 = new Color(251, 188, 5);
	private final Color COL_CARD_4 = new Color(234, 67, 53);
	private List<Data_user> userList;
	private int totalUsers = 0;
	private int totalAdmins = 0;
	private int newUsersToday = 0;
	private SideBar sidebar;

	public HomeAdmin(JPanel cardContainer) {
		mainPanel = new JPanel(new BorderLayout());
		Datauser db = new Datauser();
		userList = db.select_alluser();
		
		fetchDashboardData();

		mainPanel.add(new SideBar().createHeader(cardContainer), BorderLayout.NORTH);

		JPanel outerContainer = new JPanel(new GridBagLayout());

		JPanel contentPanel = new JPanel();
		sidebar = new SideBar("หน้าหลัก");
		contentPanel.add(sidebar.getSideBar(cardContainer));
		contentPanel.add(Box.createVerticalStrut(20));

		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		contentPanel.setPreferredSize(new Dimension(1700, 1030));

		contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setMaximumSize(new Dimension(1200, 40));
		titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel lblTitle = new JLabel("Dashboard ภาพรวมผู้ใช้งาน");
		lblTitle.setFont(new Font("Prompt Light", Font.PLAIN, 24));
		lblTitle.setForeground(COLOR_PRIMARY);

		JLabel lblDate = new JLabel("ข้อมูล ณ วันที่: " + java.time.LocalDate.now());
		lblDate.setFont(new Font("Prompt Light", Font.PLAIN, 14));
		lblDate.setForeground(COLOR_TEXT_SUB);

		titlePanel.add(lblTitle, BorderLayout.WEST);
		titlePanel.add(lblDate, BorderLayout.EAST);

		contentPanel.add(titlePanel);
		contentPanel.add(Box.createVerticalStrut(20));

		JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
 
		statsPanel.setPreferredSize(new Dimension(1200, 120));
		statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		statsPanel.add(createStatCard("ผู้ใช้งานทั้งหมด", String.valueOf(totalUsers), COL_CARD_1));
		statsPanel.add(createStatCard("ผู้ดูแลระบบ", String.valueOf(totalAdmins), COL_CARD_4));
		statsPanel.add(createStatCard("สมาชิกทั่วไป", String.valueOf(totalUsers - totalAdmins), COL_CARD_2));
		statsPanel.add(createStatCard("สมัครใหม่วันนี้", String.valueOf(newUsersToday), COL_CARD_3));

		contentPanel.add(statsPanel);
		contentPanel.add(Box.createVerticalStrut(30));

		JLabel lblTable = new JLabel("รายชื่อผู้ใช้งานในระบบ");
		lblTable.setFont(new Font("Prompt Light", Font.PLAIN, 18));
		lblTable.setForeground(COLOR_TEXT_HEADER);

		JPanel lblTablePanel = new JPanel(new BorderLayout());
		lblTablePanel.setMaximumSize(new Dimension(1200, 30));
		lblTablePanel.add(lblTable, BorderLayout.WEST);

		contentPanel.add(lblTablePanel);
		contentPanel.add(Box.createVerticalStrut(10));

		JPanel tableContainer = new RoundedPanel(20, COLOR_WHITE);
		tableContainer.setLayout(new BorderLayout());
		tableContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
		tableContainer.setMaximumSize(new Dimension(1200, 400));
		tableContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

		JTable userTable = createUserTable();
		JScrollPane tableScroll = new JScrollPane(userTable);
		tableScroll.setBorder(null);

		tableContainer.add(tableScroll, BorderLayout.CENTER);
		contentPanel.add(tableContainer);
		contentPanel.add(Box.createVerticalStrut(30));

		JPanel chartSection = new JPanel(new GridLayout(1, 2, 20, 0));
		chartSection.setMaximumSize(new Dimension(1200, 250));
		chartSection.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel pieChartPanel = new RoundedPanel(20, COLOR_WHITE);
		pieChartPanel.setLayout(new BorderLayout());
		pieChartPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
		JLabel pieLabel = new JLabel("สัดส่วนผู้ใช้งาน");
		pieLabel.setFont(new Font("Prompt Light", Font.PLAIN, 14));
//		pieChartPanel.add(pieLabel, BorderLayout.NORTH);
		pieChartPanel.add(new PieChartPanel(totalAdmins, totalUsers - totalAdmins), BorderLayout.CENTER);

		JPanel barChartPanel = new RoundedPanel(20, COLOR_WHITE);
		barChartPanel.setLayout(new BorderLayout());
		barChartPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
		JLabel barLabel = new JLabel("สถิติการใช้งาน  ");
		barLabel.setFont(new Font("Prompt Light", Font.PLAIN, 14));
		barChartPanel.add(barLabel, BorderLayout.NORTH);
//		barChartPanel.add(new UserGrowthChart(userList), BorderLayout.CENTER);
		JPanel Map = new Map().getMap(cardContainer);
		JScrollPane a = new JScrollPane(Map);
		a.setBorder(null);
		a.setPreferredSize(new Dimension(1600, 800));

		a.getVerticalScrollBar().setUnitIncrement(30);
		contentPanel.add(a);

		JScrollPane scrollPane = new JScrollPane(contentPanel);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		chartSection.add(pieChartPanel);
		chartSection.add(barChartPanel);

//		contentPanel.add(chartSection);

		outerContainer.add(contentPanel);

		JScrollPane mainScroll = new JScrollPane(outerContainer);
		mainScroll.setBorder(null);
		mainScroll.getVerticalScrollBar().setUnitIncrement(16);

		mainPanel.add(mainScroll, BorderLayout.CENTER);
	}

	private void fetchDashboardData() {
	    totalUsers = userList.size();
	    totalAdmins = 0;
	    newUsersToday = 0;

	    // 1. หาวันที่ปัจจุบันของเครื่องเรา (เช่น 2026-01-13)
	    LocalDate today = LocalDate.now(); 
	    
	    // กำหนด Timezone
	    ZoneId dbZone = ZoneId.of("UTC");      // เวลาของ Database (ปกติเป็น UTC)
	    ZoneId myZone = ZoneId.systemDefault(); // เวลาเครื่องเรา (Asia/Bangkok)

	    for (Data_user user : userList) {

	        // เช็ค Admin
	        if (user.getRole() != null && user.getRole().equalsIgnoreCase("admin")) {
	            totalAdmins++;
	        }

	        // เช็ค User ใหม่
	        if (user.getRegisteredDate() != null) {
	            try {
	                // 2. แปลง String จาก DB เป็น LocalDateTime ก่อน
	                // รูปแบบ: "2026-01-12T19:09:21"
	                String regDateStr = user.getRegisteredDate().toString();
	                LocalDateTime dbTime = LocalDateTime.parse(regDateStr);

	                // 3. ระบุว่าเวลานี้คือ UTC นะ
	                ZonedDateTime utcZoned = dbTime.atZone(dbZone);

	                // 4. แปลงร่าง! ให้เป็นเวลาเครื่องเรา (บวก 7 ชม. อัตโนมัติ)
	                // จาก 12 ม.ค. 19:00 -> จะกลายเป็น 13 ม.ค. 02:00
	                ZonedDateTime localZoned = utcZoned.withZoneSameInstant(myZone);

	                // 5. ตัดเอาแค่ "วันที่" มาเทียบกัน
	                if (localZoned.toLocalDate().equals(today)) {
	                    newUsersToday++;
	                }

	            } catch (Exception e) {
	                e.printStackTrace(); // เผื่อ Format เวลาผิด
	            }
	        }
	    }
	}

	public JPanel getPanel() {
		return mainPanel;
	}

	private JPanel createStatCard(String title, String value, Color accentColor) {
		RoundedPanel card = new RoundedPanel(20, COLOR_WHITE);
		card.setLayout(new BorderLayout());
		card.setBorder(new EmptyBorder(15, 20, 15, 20));

		JPanel textPanel = new JPanel();
		textPanel.setOpaque(false);
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("Prompt Light", Font.PLAIN, 14));
		lblTitle.setForeground(COLOR_TEXT_SUB);

		JLabel lblValue = new JLabel(value);
		lblValue.setFont(new Font("Prompt Light", Font.PLAIN, 32));
		lblValue.setForeground(COLOR_TEXT_HEADER);

		textPanel.add(lblTitle);
		textPanel.add(Box.createVerticalStrut(5));
		textPanel.add(lblValue);

		card.add(textPanel, BorderLayout.CENTER);

		JPanel bottomLine = new JPanel() {
			protected void paintComponent(Graphics g) {
				g.setColor(accentColor);
				g.fillRect(0, 0, getWidth(), 4);
			}
		};
		bottomLine.setPreferredSize(new Dimension(0, 4));
		card.add(bottomLine, BorderLayout.SOUTH);

		return card;
	}

	private JTable createUserTable() {
		String[] columnNames = { "ID", "Username", "Email", "Role", "Registered Date" };

		Object[][] data = new Object[userList.size()][5];

		for (int i = 0; i < userList.size(); i++) {
			Data_user u = userList.get(i);

			data[i][0] = u.getId(); // user_id
			data[i][1] = u.getUsername(); // username
			data[i][2] = u.getEmail(); // email
			data[i][3] = u.getRole(); // password (ไม่ควรโชว์จริง)
			data[i][4] = u.getRegisteredDate(); // role
		}

		DefaultTableModel model = new DefaultTableModel(data, columnNames) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		JTable table = new JTable(model);
		table.setRowHeight(40);
		table.setFont(new Font("Prompt Light", Font.PLAIN, 14));
		table.setShowVerticalLines(false);

		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Prompt Light", Font.PLAIN, 14));
		header.setPreferredSize(new Dimension(0, 40));
		((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

		return table;
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

	class PieChartPanel extends JPanel {
		int v1, v2;

		public PieChartPanel(int v1, int v2) {
			this.v1 = v1;
			this.v2 = v2;
			setOpaque(false);
		}

		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			int size = Math.min(getWidth(), getHeight()) - 40;
			int x = (getWidth() - size) / 2;
			int y = (getHeight() - size) / 2;
			int total = v1 + v2;
			if (total == 0)
				return;

			int angle1 = (int) (360.0 * v1 / total);
			g2.setColor(COL_CARD_4);
			g2.fillArc(x, y, size, size, 90, angle1);
			g2.setColor(COL_CARD_2);
			g2.fillArc(x, y, size, size, 90 + angle1, 360 - angle1);
		}
	}

//	class UserGrowthChart extends JPanel {
//
//	    private int[] monthlyData = new int[12];
//	    private int maxCount = 1;
//
//	    public UserGrowthChart(List<Data_user> userList) {
//	        setOpaque(false);
//	        calculateMonthlyData(userList);
//	    }
//
//	    private void calculateMonthlyData(List<Data_user> userList) {
//	        int currentYear = java.time.LocalDate.now().getYear();
//
//	        for (Data_user u : userList) {
//	            LocalDate regDate = u.getRegisteredDate();
//	            if (regDate != null && regDate.getYear() == currentYear) {
//	                int monthIndex = regDate.getMonthValue() - 1;
//	                monthlyData[monthIndex]++;
//	                maxCount = Math.max(maxCount, monthlyData[monthIndex]);
//	            }
//	        }
//	    }
//
//	    @Override
//	    protected void paintComponent(Graphics g) {
//	        super.paintComponent(g);
//	        Graphics2D g2 = (Graphics2D) g;
//	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//	        int w = getWidth();
//	        int h = getHeight();
//	        int padding = 30;
//	        int graphHeight = h - padding * 2;
//	        int barWidth = (w - padding * 2) / 12;
//
//	        // baseline
//	        g2.setColor(Color.LIGHT_GRAY);
//	        g2.drawLine(padding, h - padding, w - padding, h - padding);
//
//	        String[] months = {"J","F","M","A","M","J","J","A","S","O","N","D"};
//
//	        for (int i = 0; i < 12; i++) {
//	            int value = monthlyData[i];
//	            int barHeight = (int) ((value / (double) maxCount) * graphHeight);
//
//	            int x = padding + i * barWidth + barWidth / 4;
//	            int y = h - padding - barHeight;
//
//	            if (value > 0) {
//	                g2.setColor(i % 2 == 0 ? COL_CARD_1 : COL_CARD_3);
//	                g2.fillRoundRect(x, y, barWidth / 2, barHeight, 8, 8);
//
//	                // number
//	                g2.setColor(Color.BLACK);
//	                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
//	                g2.drawString(String.valueOf(value), x + 5, y - 5);
//	            }
//
//	            // month label
//	            g2.setColor(Color.GRAY);
//	            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
//	            g2.drawString(months[i], x + 6, h - 10);
//	        }
//	    }
//	}

}
