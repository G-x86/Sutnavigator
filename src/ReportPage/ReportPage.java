package ReportPage;

import java.awt.*;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import Components.SideBar;
import User.Datauser; // Import Datauser

public class ReportPage {

    private JPanel mainPanel; 
    private Font mainFont = new Font("Prompt Light", Font.PLAIN, 14); 

   
    private Color bgColor = new Color(253, 251, 245);
    private Color activeBrown = new Color(160, 110, 50);  
    private Color bgCream = new Color(253, 251, 245);    
    private Color darkHeader = new Color(30, 30, 30);
    private Color textGray = new Color(100, 100, 100);
    private Color tableHeaderBg = new Color(240, 240, 240); 
    private SideBar sidebar;
    public ReportPage(JPanel cardContainer) {
       
        mainPanel = new JPanel(new BorderLayout());
       
 
        mainPanel.add(new SideBar().createHeader(cardContainer), BorderLayout.NORTH);

 
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
    	
        contentContainer.setBorder(new EmptyBorder(20, 40, 20, 40));
        JPanel c__ = new JPanel();
        
        sidebar = new SideBar("รายงาน");
        c__.add(sidebar.getSideBar(cardContainer));
        
        
        c__.setLayout(new BoxLayout(c__, BoxLayout.Y_AXIS));
 
        c__.setBorder(new EmptyBorder(20, 40, 20, 40));
   
      
        
        contentContainer.add(Box.createVerticalStrut(10));

       
        contentContainer.add(createTopViewsCard());
        contentContainer.add(Box.createVerticalStrut(20));

       
        contentContainer.add(createCategorySummaryCard());

         
        contentContainer.add(Box.createVerticalGlue());

        
        JScrollPane scroll = new JScrollPane(contentContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        c__.add(scroll, BorderLayout.CENTER);
        mainPanel.add(c__ , BorderLayout.CENTER );
       
    }

    public JPanel getPanel() {
        return mainPanel;
    }

 
    private JPanel createTopViewsCard() {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(2000, 350)); 

        JLabel lblHeader = new JLabel(" สถานที่ที่ถูกเปิดดูบ่อยที่สุด (Top 5)");
        lblHeader.setFont(new Font("Prompt Light", Font.BOLD, 16));
        lblHeader.setForeground(activeBrown);
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

         
        JPanel tablePanel = new JPanel(new GridBagLayout());
        tablePanel.setOpaque(false);
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
         
        gbc.gridy = 0;
        tablePanel.add(createTableRow("อันดับ", "อาคาร", "หมวดหมู่", "จำนวนครั้งที่เปิดดู", true), gbc);

        
        Datauser dataUser = new Datauser();
        List<String[]> topBuildings = dataUser.getTopViewedBuildings(5);
        
        if (topBuildings.isEmpty()) {
            gbc.gridy++;
            JLabel noData = new JLabel("ยังไม่มีข้อมูลการเข้าชม");
            noData.setHorizontalAlignment(SwingConstants.CENTER);
            noData.setBorder(new EmptyBorder(20,0,0,0));
            tablePanel.add(noData, gbc);
        } else {
            for (String[] rowData : topBuildings) {
                gbc.gridy++;
                // rowData[0]=rank, [1]=name, [2]=category, [3]=count
                tablePanel.add(createTableRow(rowData[0], rowData[1], rowData[2], rowData[3], false), gbc);
            }
        }
       
        JLabel lblFooter = new JLabel(" ข้อมูลนี้อัปเดตจากการที่ผู้ใช้เปิดดูรายละเอียดอาคารในระบบ");
        lblFooter.setFont(new Font("Prompt Light", Font.PLAIN, 12));
        lblFooter.setForeground(Color.GRAY);
        lblFooter.setBorder(new EmptyBorder(10, 0, 0, 0));
        lblFooter.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblHeader);
        card.add(Box.createVerticalStrut(15));
        card.add(tablePanel);
        card.add(lblFooter);

        return card;
    }

    private JPanel createTableRow(String col1, String col2, String col3, String col4, boolean isHeader) {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setPreferredSize(new Dimension(800, 40));
        row.setBorder(new EmptyBorder(0, 10, 0, 10));

        if (isHeader) {
            row.setBackground(tableHeaderBg);
            row.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, activeBrown),
                new EmptyBorder(0, 10, 0, 10)
            ));
        } else {
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                new EmptyBorder(0, 10, 0, 10)
            ));
        }

        row.add(createCell(col1, isHeader));
        row.add(createCell(col2, isHeader));
        row.add(createCell(col3, isHeader));
        row.add(createCell(col4, isHeader));

        return row;
    }

    private JLabel createCell(String text, boolean isHeader) {
        JLabel lbl = new JLabel(text);
        if (isHeader) {
            lbl.setFont(new Font("Prompt Light", Font.BOLD, 14));
            lbl.setForeground(activeBrown);
        } else {
            lbl.setFont(new Font("Prompt Light", Font.PLAIN, 14));
            lbl.setForeground(Color.BLACK);
        }
        return lbl;
    }

    private JPanel createCategorySummaryCard() {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(2000, 150));

        
        JLabel lblHeader = new JLabel(" สรุปตามหมวดหมู่ (ยอดเข้าชมรวม)");
        lblHeader.setFont(new Font("Prompt Light", Font.BOLD, 16));
        lblHeader.setForeground(activeBrown);
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

       
        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        tagsPanel.setOpaque(false);
        tagsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      
        Datauser dataUser = new Datauser();
        Map<String, Integer> stats = dataUser.getCategoryStats();

        if (stats.isEmpty()) {
             tagsPanel.add(createTag("ยังไม่มีข้อมูล: 0 ครั้ง"));
        } else {
         
            for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                String tagName = entry.getKey();
                int count = entry.getValue();
                tagsPanel.add(createTag(" " + tagName + ": " + count + " ครั้ง"));
            }
        }


        card.add(lblHeader);
        card.add(Box.createVerticalStrut(15));
        card.add(tagsPanel);

        return card;
    }

    private JPanel createTag(String text) {
        RoundedPanel tag = new RoundedPanel(30, new Color(240, 240, 240));
        tag.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 8)); 
        
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Prompt Light", Font.BOLD, 14));
        lbl.setForeground(Color.BLACK);
        
        tag.add(lbl);
        return tag;
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