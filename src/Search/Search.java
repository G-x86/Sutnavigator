package Search;

import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
// import javax.swing.event.DocumentEvent;    <-- ไม่ต้องใช้แล้ว
// import javax.swing.event.DocumentListener; <-- ไม่ต้องใช้แล้ว

import Components.Components_;
import Components.SideBar;
import User.Datauser;
import User.SearchResult;

public class Search {

    private JPanel mainPanel;
    private JPanel resultCard;
    private Color bgColor = new Color(253, 251, 245);
    private Color whiteColor = Color.WHITE;

    private List<SearchResult> currentResults = new ArrayList<>();
    private JComboBox<String> sortDropdown;
    private JTextField txtSearch;
    private List<JCheckBox> categoryCheckBoxes = new ArrayList<>();

    public Search(JPanel cardContainer) {
        
        resultCard = new JPanel();
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));
        resultCard.setOpaque(false);  

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(whiteColor);

        mainPanel.add(new SideBar().createHeader(cardContainer), BorderLayout.NORTH);

        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        
        contentContainer.setBorder(new EmptyBorder(20, 40, 20, 40));
        contentContainer.setPreferredSize(new Dimension(1700, 1030));

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setOpaque(false);
        labelPanel.setMaximumSize(new Dimension(2000, 30));

        JPanel searchInterfaceCard = createSearchInterface();
        contentContainer.add(new SideBar("ค้นหา").getSideBar(cardContainer));

        contentContainer.add(Box.createVerticalStrut(15));
        contentContainer.add(labelPanel);
        contentContainer.add(Box.createVerticalStrut(5));
        contentContainer.add(searchInterfaceCard);
        contentContainer.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(contentContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scroll, BorderLayout.CENTER);
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    private JPanel createSearchInterface() {

        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout());  
        card.setBorder(new EmptyBorder(20, 30, 40, 30));
        card.setMaximumSize(new Dimension(2000, 2450)); 

        JPanel headerContainer = new JPanel();
        headerContainer.setLayout(new BoxLayout(headerContainer, BoxLayout.Y_AXIS));
        headerContainer.setOpaque(false);

        // --- ส่วนหัว Title และ Dropdown ---
        JPanel topControl = new JPanel(new BorderLayout());
        topControl.setOpaque(false);
        topControl.setMaximumSize(new Dimension(2000, 40));

        Components_ titleSearch = new Components_("ค้นหา", "label", 18, 0xA06E32);
        JLabel lblTitle = (JLabel) titleSearch.getComponent();
        lblTitle.setFont(new Font("Prompt Light", Font.BOLD, 18));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // Dropdown เรียงลำดับ (ให้เรียงทันทีที่เลือก ยังคงไว้เหมือนเดิมได้ หรือจะเอาออกก็ได้ถ้าอยากให้กดปุ่มก่อน)
        String[] sortOptions = { 
            "None (ไม่เลือก)", 
            "เรียงตามตัวอักษร (A-Z)", 
            "เรียงตามตัวอักษร (Z-A)", 
            "หมายเลขห้อง (น้อย -> มาก)", 
            "หมายเลขห้อง (มาก -> น้อย)" 
        };
        sortDropdown = new JComboBox<>(sortOptions);
        sortDropdown.setFont(new Font("Prompt Light", Font.PLAIN, 14));
        sortDropdown.setBackground(Color.WHITE);
        sortDropdown.setFocusable(false);
        sortDropdown.setPreferredSize(new Dimension(220, 30));
        
        sortDropdown.addActionListener(e -> updateResultPanel());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        JLabel i = new JLabel("เรียงลำดับ: ");
        i.setFont(new Font("Prompt Light", Font.PLAIN, 14));
        rightPanel.add(i);
        rightPanel.add(sortDropdown);

        topControl.add(lblTitle, BorderLayout.CENTER);
        topControl.add(rightPanel, BorderLayout.EAST); 
      
        // --- Search Bar ---
        JPanel searchBarPanel = new RoundedPanel(30, new Color(240, 240, 245));
        searchBarPanel.setLayout(new BorderLayout());
        searchBarPanel.setMaximumSize(new Dimension(2000, 50));
        searchBarPanel.setBorder(new EmptyBorder(5, 15, 5, 5));

        JLabel iconSearchLeft = new JLabel("🔍 ");
        iconSearchLeft.setForeground(Color.GRAY);

        txtSearch = new JTextField("ค้นหาสถานที่...");
        txtSearch.setBorder(null);
        txtSearch.setOpaque(false);
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setFont(new Font("Prompt Light", Font.PLAIN, 14));
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().equals("ค้นหาสถานที่...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText("ค้นหาสถานที่...");
                }
            }
        });
        
        // ★★★ แก้ไข 1: ลบ DocumentListener ออก (ไม่ค้นหาตอนพิมพ์) ★★★
        // txtSearch.getDocument().addDocumentListener(...) << ลบทิ้งไปเลย

        // ★★★ แก้ไข 2: เพิ่ม ActionListener แทน (ให้กด Enter แล้วค้นหา) ★★★
        txtSearch.addActionListener(e -> performSearch());

        JButton btnSearchAction = new JButton("ค้นหา"){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSearchAction.setContentAreaFilled(false);
        btnSearchAction.setFont(new Font("Prompt Light", Font.PLAIN, 16));
        btnSearchAction.setPreferredSize(new Dimension(60, 40));
        btnSearchAction.setBackground(new Color(100, 149, 237));
        btnSearchAction.setForeground(Color.WHITE);
        btnSearchAction.setBorderPainted(false);
        btnSearchAction.setFocusPainted(false);
        btnSearchAction.setBorder(new EmptyBorder(0, 0, 0, 0));
        btnSearchAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // ★★★ แก้ไข 3: ปุ่มค้นหาทำงานเมื่อกดคลิก ★★★
        btnSearchAction.addActionListener(e -> performSearch());
        
        searchBarPanel.add(iconSearchLeft, BorderLayout.WEST);
        searchBarPanel.add(txtSearch, BorderLayout.CENTER);
        searchBarPanel.add(btnSearchAction, BorderLayout.EAST);

        // --- Filter Panel ---
        JPanel filterPanel = createFilterPanel();

        JLabel notFound = new JLabel("กรอกข้อมูลที่ต้องการค้นหา");
        notFound.setFont(new Font("Prompt Light", Font.PLAIN, 14));
        notFound.setForeground(Color.GRAY);
        notFound.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultCard.add(notFound);

        headerContainer.add(topControl);
        headerContainer.add(Box.createVerticalStrut(20));
        headerContainer.add(searchBarPanel);
        headerContainer.add(Box.createVerticalStrut(10));
        headerContainer.add(filterPanel); 
        headerContainer.add(Box.createVerticalStrut(10));

        JScrollPane a = new JScrollPane(resultCard);
        a.setPreferredSize(new Dimension(1000, 900));
        a.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        a.getVerticalScrollBar().setUnitIncrement(20);
        a.setBorder(null);
        a.setOpaque(false);
        a.getViewport().setOpaque(false);

        card.add(headerContainer, BorderLayout.NORTH);  
        card.add(a, BorderLayout.CENTER);             

        return card;
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        // ถ้าเป็นค่าว่าง หรือ เป็นข้อความ Placeholder ไม่ต้องทำอะไร (หรือจะให้เคลียร์ผลลัพธ์ก็ได้)
        if (keyword.isEmpty() || keyword.equals("ค้นหาสถานที่...")) {
            // ถ้าอยากให้กดค้นหาเปล่าๆ แล้วโชว์ทั้งหมด ให้ลบ if นี้ออก
            // แต่ถ้าอยากให้เคลียร์ผลลัพธ์:
             resultCard.removeAll();
             resultCard.revalidate();
             resultCard.repaint();
            return;
        }
        
        // 1. ค้นหาทั้งหมดจาก DB/Cache
        List<SearchResult> allResults = new Datauser().searchFromDatabase(keyword);
        
        // 2. กรองตาม Category Checkbox (อ่านค่า checkbox ตอนกดปุ่ม)
        List<Integer> selectedIds = new ArrayList<>();
        for (JCheckBox chk : categoryCheckBoxes) {
            if (chk.isSelected()) {
                selectedIds.add((Integer) chk.getClientProperty("categoryId"));
            }
        }

        currentResults.clear();
        for (SearchResult result : allResults) {
            // ถ้าไม่มีการเลือก Filter เลย หรือ ID ตรงกับที่เลือก -> เก็บไว้แสดงผล
            if (selectedIds.isEmpty() || selectedIds.contains(result.categoryId)) {
                currentResults.add(result);
            }
        }
        
        updateResultPanel();
    }

    private void updateResultPanel() {
        resultCard.removeAll();
        
        if (currentResults == null || currentResults.isEmpty()) {
            JLabel notFound = new JLabel("ไม่พบข้อมูล");
            notFound.setFont(new Font("Prompt Light", Font.PLAIN, 14));
            notFound.setForeground(Color.GRAY);
            notFound.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultCard.add(notFound);
            resultCard.revalidate();
            resultCard.repaint();
            return;
        }

        List<SearchResult> displayList = new ArrayList<>(currentResults);
        String sortMode = (String) sortDropdown.getSelectedItem();

        // Bubble Sort logic (คงเดิม)
        if (sortMode != null && !sortMode.startsWith("None")) {
            int n = displayList.size();
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    SearchResult p1 = displayList.get(j);
                    SearchResult p2 = displayList.get(j + 1);
                    boolean swap = false;

                    switch (sortMode) {
                        case "เรียงตามตัวอักษร (A-Z)":
                            if (p1.text.compareToIgnoreCase(p2.text) > 0) swap = true;
                            break;
                        case "เรียงตามตัวอักษร (Z-A)":
                            if (p1.text.compareToIgnoreCase(p2.text) < 0) swap = true;
                            break;
                        case "หมายเลขห้อง (น้อย -> มาก)":
                            if (extractRoomNumber(p1.text) > extractRoomNumber(p2.text)) swap = true;
                            break;
                        case "หมายเลขห้อง (มาก -> น้อย)":
                            if (extractRoomNumber(p1.text) < extractRoomNumber(p2.text)) swap = true;
                            break;
                    }

                    if (swap) {
                        displayList.set(j, p2);
                        displayList.set(j + 1, p1);
                    }
                }
            }
        }

        for (SearchResult result : displayList) {
            resultCard.add(createResultItem(result));
            resultCard.add(Box.createVerticalStrut(10));
        }

        resultCard.revalidate();
        resultCard.repaint();
    }

    private int extractRoomNumber(String text) {
        try {
            String numberOnly = text.replaceAll("[^0-9]", ""); 
            if (numberOnly.isEmpty()) return 0;
            return Integer.parseInt(numberOnly);
        } catch (Exception e) {
            return 0;
        }
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(2000, 40));

        Object[][] filters = {
            {"อาคารเรียน", 1}, {"ร้านอาหาร", 2}, {"ที่พัก", 3}, {"ห้องสมุด", 4},        
            {"บริการ", 5}, {"สนามกีฬา", 6}, {"ยิม", 7}, {"ตู้เอทีเอ็ม", 8},
            {"หอดูดาว", 9}, {"อาคารค้นคว้า", 10}, {"แหล่งท่องเที่ยวเชิงธรรมชาติ", 11},
            {"อุโมงค์ต้นไม้", 12}, {"เรื่องเล่าผี", 13}
        };

        for (Object[] filter : filters) {
            String label = (String) filter[0];
            int id = (Integer) filter[1];

            JCheckBox chk = new JCheckBox(label);
            chk.setOpaque(false);
            chk.setFont(new Font("Tahoma", Font.PLAIN, 14));
            chk.setForeground(Color.DARK_GRAY);
            chk.setFocusPainted(false);
            chk.putClientProperty("categoryId", id);
            
            // ★★★ แก้ไข 4: เอา Listener ของ Checkbox ออก ★★★
            // ไม่ค้นหาทันทีที่ติ๊ก ต้องรอ User กดปุ่มค้นหาเอง
            // chk.addItemListener(e -> performSearch()); << ลบบรรทัดนี้ออก
            
            categoryCheckBoxes.add(chk);
            panel.add(chk);
        }
        return panel;
    }

    private JPanel createResultItem(SearchResult result) {
        // ... (ส่วนนี้เหมือนเดิม ไม่ต้องแก้) ...
        String title = result.text;
        String path = result.imagePath;
        
        RoundedPanel itemPanel = new RoundedPanel(15, Color.WHITE);
        itemPanel.setLayout(new BorderLayout(15, 0));
        itemPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        itemPanel.setMaximumSize(new Dimension(2000, 100));

        JPanel imgPlaceholder = new JPanel(new BorderLayout());
        imgPlaceholder.setPreferredSize(new Dimension(80, 80));
        imgPlaceholder.setOpaque(false);

        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(JLabel.CENTER);

        if (path != null && !path.isEmpty()) {
            try {
                ImageIcon icon = null;
                if (path.toLowerCase().startsWith("http")) {
                    icon = new ImageIcon(new java.net.URL(path));
                } else {
                    java.net.URL imgURL = getClass().getResource(path);
                    if (imgURL != null) {
                        icon = new ImageIcon(imgURL);
                    }
                }

                if (icon != null && icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    imgLabel.setIcon(new ImageIcon(img));
                    imgLabel.setText(""); 
                } else {
                    imgLabel.setText("No Img");
                    imgPlaceholder.setBackground(Color.LIGHT_GRAY);
                    imgPlaceholder.setOpaque(true);
                }
            } catch (Exception e) {
                imgLabel.setText("Error");
            }
        } else {
            imgLabel.setText("No Img");
            imgPlaceholder.setBackground(Color.LIGHT_GRAY);
            imgPlaceholder.setOpaque(true);
        }

        imgPlaceholder.add(imgLabel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        Components_ titleComp = new Components_(title, "label", 16, 0x000000);
        ((JLabel) titleComp.getComponent()).setFont(new Font("Prompt Light", Font.BOLD, 16));
        ((JLabel) titleComp.getComponent()).setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnGroup.setOpaque(false);
        btnGroup.setAlignmentX(Component.LEFT_ALIGNMENT);

        Components_ btnInfoComp = new Components_("แผนที่", "button", 12, 0xFFFFFF, 0xA06E32, 80, 30);
        JButton btnInfo = (JButton) btnInfoComp.getComponent();  

        btnInfo.addActionListener(e -> {
            int currentUserId = 1; 
            new Datauser().insertAccessLog(result.buildingId, currentUserId);

            try {
                String mapUrl = "https://www.google.com/maps/place/" + result.lat  + "," + result.lon+ "";
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(mapUrl));
                } else {
                    JOptionPane.showMessageDialog(null, "ไม่สามารถเปิด Web Browser ได้");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "เกิดข้อผิดพลาด: " + ex.getMessage());
            }
        });

        btnGroup.add(btnInfo);
        btnGroup.add(Box.createHorizontalStrut(10));

        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(titleComp.getComponent());
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(btnGroup);

        itemPanel.add(imgPlaceholder, BorderLayout.WEST);
        itemPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel shadowWrap = new JPanel(new BorderLayout());
        shadowWrap.setBackground(new Color(250, 250, 250));
        shadowWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1), new EmptyBorder(1, 1, 1, 1)));
        shadowWrap.setMaximumSize(new Dimension(2000, 105));
        shadowWrap.add(itemPanel);

        return shadowWrap;
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
            g2.setColor(new Color(230, 230, 230)); 
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
    }
}