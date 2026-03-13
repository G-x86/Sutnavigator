package ListPage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Components.Components_;
import Components.SideBar;
import Data.Building;
import Data.DataAll;
import Data.Room;
import User.Datauser;

public class ListPage {
    private AtomicInteger i = new AtomicInteger(0);
    private JPanel mainPanel;
    JScrollPane detailScroll = null;
    private JPanel detailCard = null, card = null;

    private Color activeBrown = new Color(160, 110, 50);
    private Color bgCream = new Color(253, 251, 245);
    private List<DataAll> allDataList = new ArrayList<>();
    private java.util.List<JButton> categoryButtons = new java.util.ArrayList<>();
    private boolean isAdmin = true;

    public ListPage(JPanel cardContainer, boolean iam) {
        this.isAdmin = iam;
        card = cardContainer;
        mainPanel = new JPanel(new BorderLayout());

        new Datauser().getAllData(allDataList);

        mainPanel.add(new SideBar().createHeader(cardContainer), BorderLayout.NORTH);

        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setBorder(new EmptyBorder(20, 40, 20, 40));

        contentContainer.add(new SideBar("ลิสต์").getSideBar(cardContainer));
        contentContainer.add(Box.createVerticalStrut(20));

        JPanel splitView = new JPanel(new BorderLayout(20, 0));
        splitView.setOpaque(false);

        // 1. หาอาคารแรกสุด (หลังจากเรียงแล้ว) เพื่อแสดงผลเป็น Default
        int firstId = -1;
        String firstName = "Loading...";

        // กรองเฉพาะ Building
        List<DataAll> onlyBuildings = new ArrayList<>();
        for (DataAll d : allDataList) {
            if (d instanceof Building) {
                // กรองไม่เอาตึกผี (เผื่อมีหลุดมา)
                if (!"13".equals(d.getCategory())) {
                    onlyBuildings.add(d);
                }
            }
        }

        // เรียงลำดับด้วย Bubble Sort (เขียนเอง)
        bubbleSortBuildings(onlyBuildings);

        // เลือกตัวแรก
        if (!onlyBuildings.isEmpty()) {
            DataAll first = onlyBuildings.get(0);
            firstId = first.getId();
            firstName = first.getName();
        }

        // สร้างเนื้อหาเริ่มต้น
        detailCard = createDetailContent(firstId, firstName);
        
        JPanel sidebar = createSidebar();
        JScrollPane sidebarScroll = new JScrollPane(sidebar);
        sidebarScroll.setBorder(null);
        sidebarScroll.getViewport().setOpaque(false);
        sidebarScroll.setOpaque(false);
        sidebarScroll.setPreferredSize(new Dimension(280, 0));
        sidebarScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        detailScroll = new JScrollPane(detailCard);
        detailScroll.setBorder(null);
        detailScroll.getViewport().setOpaque(false);
        detailScroll.setOpaque(false);
        detailScroll.getVerticalScrollBar().setUnitIncrement(16);
        detailScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        splitView.add(sidebarScroll, BorderLayout.WEST);
        splitView.add(detailScroll, BorderLayout.CENTER);

        contentContainer.add(splitView);
        mainPanel.add(contentContainer, BorderLayout.CENTER);
    }

    public JPanel getPanel() {
        if (this.isAdmin) {
            return new AdminListPage(card).getPanel();
        }
        return mainPanel;
    }

    // ==========================================
    // ★ HELPER: Manual Bubble Sort (Building)
    // ==========================================
    private void bubbleSortBuildings(List<DataAll> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                String name1 = list.get(j).getDisplayText();
                String name2 = list.get(j + 1).getDisplayText();
                
                // เปรียบเทียบ String (A-Z)
                if (name1.compareToIgnoreCase(name2) > 0) {
                    // Swap
                    DataAll temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    // ==========================================
    // ★ HELPER: Manual Bubble Sort (Room)
    // ==========================================
    private void bubbleSortRooms(List<Room> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                String name1 = list.get(j).getName();
                String name2 = list.get(j + 1).getName();
                
                if (name1.compareToIgnoreCase(name2) > 0) {
                    // Swap
                    Room temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    private JPanel createSidebar() {
        RoundedPanel sidebarPanel = new RoundedPanel(20, Color.WHITE);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        Components_ headerLabel = new Components_("รายการอาคาร", "label", 16, 0xB48C00);
        JLabel header = (JLabel) headerLabel.getComponent();
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setFont(new Font("Prompt Light", Font.BOLD, 16));

        sidebarPanel.add(header);
        sidebarPanel.add(Box.createVerticalStrut(15));
        
        // Reset ตัวนับก่อนสร้าง Sidebar ใหม่
        i.set(0); 

        // ★★★ แก้ไข: เพิ่มหมวดหมู่ครบ (ยกเว้นเรื่องผี) ★★★
        String[][] categories = {
            {"อาคารเรียน", "1"},
            {"ร้านค้า/อาหาร", "2"},
            {"หอพัก", "3"},
            {"บรรณสาร", "4"},
            {"บริการ/เครื่องมือ", "5"},
            {"สนามกีฬา", "6"},
            {"ยิม/ฟิตเนส", "7"},
            {"ตู้ ATM", "8"},
            {"หอดูดาว", "9"},
            {"อาคารค้นคว้า", "10"},
            {"ท่องเที่ยวธรรมชาติ", "11"},
            {"อุโมงค์ต้นไม้", "12"}
            // ตัด {"เรื่องเล่าผี", "13"} ออก
        };

        for (String[] cat : categories) {
            addSidebarSection(sidebarPanel, cat[0], cat[1]);
        }
        // ★★★ จบส่วนแก้ไข ★★★

        sidebarPanel.add(Box.createVerticalGlue());
        return sidebarPanel;
    }

    private void addSidebarSection(JPanel panel, String title, String categoryId) {
        // 1. กรองข้อมูล (Manual Filter)
        List<DataAll> filteredList = new ArrayList<>();
        for (DataAll item : allDataList) {
            if (item instanceof Building && categoryId.equals(item.getCategory())) {
                filteredList.add(item);
            }
        }

        // ★ เพิ่ม: ถ้าหมวดไหนไม่มีข้อมูล ไม่ต้องแสดงหัวข้อ (เพื่อให้หน้า User ดูสะอาดตา)
        if (filteredList.isEmpty()) {
            return;
        }

        panel.add(createSidebarSectionHeader(title));
        panel.add(Box.createVerticalStrut(5));

        // 2. เรียงลำดับ (Manual Bubble Sort)
        bubbleSortBuildings(filteredList);

        // 3. วนลูปสร้างปุ่ม
        for (DataAll item : filteredList) {

            // i.get() == 0 คือให้ปุ่มแรกสุด Active เสมอ
            JButton btn = createSidebarItem(item.getDisplayText(), i.get() == 0);
            btn.setContentAreaFilled(false);
            
            if(i.get() == 0) {
                // Logic เพิ่มเติม (ถ้ามี)
            }

            btn.addActionListener(e -> {
                Color activeBg = new Color(0xA06E32);
                Color activeFg = Color.WHITE;
                Color normalBg = new Color(0xF8F9FA); // สีเทาอ่อน
                Color normalFg = Color.BLACK;

                // 1. วนลูปเปลี่ยนสีปุ่มทั้งหมด
                for (JButton b : categoryButtons) {
                    if (b == btn) {
                        b.setBackground(activeBg);
                        b.setForeground(activeFg);
                    } else {
                        b.setBackground(normalBg);
                        b.setForeground(normalFg);
                    }
                }

                JPanel newContent = createDetailContent(item.getId(), item.getName());

                // สั่ง JScrollPane ให้เปลี่ยนไส้ใน
                detailScroll.setViewportView(newContent);
                detailScroll.revalidate();
                detailScroll.repaint();
            });

            categoryButtons.add(btn); // เก็บเข้า List ไว้เปลี่ยนสี
            panel.add(btn);
            panel.add(Box.createVerticalStrut(5)); // ระยะห่างระหว่างปุ่ม

            i.incrementAndGet();
        }
        
        // เพิ่มระยะห่างระหว่างหมวด
        panel.add(Box.createVerticalStrut(10));
    }

    private JComponent createSidebarSectionHeader(String text) {
        Components_ comp = new Components_(text, "label", 14, 0xA06E32);
        JLabel lbl = (JLabel) comp.getComponent();
        lbl.setFont(new Font("Prompt Light", Font.BOLD, 14));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton createSidebarItem(String text, boolean isActive) {
        int bgColor = isActive ? 0xA06E32 : 0xF8F9FA;
        int fgColor = isActive ? 0xFFFFFF : 0x000000;

        Components_ btnComp = new Components_(text, "button", 14, fgColor, bgColor, 240, 40);
        JButton btn = (JButton) btnComp.getComponent();
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 15, 0, 0));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        return btn;
    }

    private JPanel createDetailContent(int bid, String name) {
        RoundedPanel contentCard = new RoundedPanel(20, Color.WHITE);
        contentCard.setLayout(new BoxLayout(contentCard, BoxLayout.Y_AXIS));
        contentCard.setBorder(new EmptyBorder(30, 40, 30, 40));

        Components_ titleComp = new Components_(name, "label", 22, 0xA06E32);
        JLabel title = (JLabel) titleComp.getComponent();
        title.setFont(new Font("Prompt Light", Font.BOLD, 22));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(activeBrown);
        sep.setMaximumSize(new Dimension(1000, 2));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel gridPanel = new JPanel(new GridLayout(0, 5, 20, 20));
        gridPanel.setOpaque(false);

        // 1. กรองเฉพาะห้อง (Manual Filter)
        List<Room> filteredRooms = new ArrayList<>();
        for (DataAll item : allDataList) {
            if (item instanceof Room) {
                Room r = (Room) item;
                if (r.getBId() == bid) {
                    filteredRooms.add(r);
                }
            }
        }

        // 2. เรียงลำดับ (Manual Bubble Sort)
        bubbleSortRooms(filteredRooms);

        // 3. วนลูปสร้างการ์ด
        for (Room room : filteredRooms) {
            gridPanel.add(createRoomCard(room));
        }

        JPanel gridWrapper = new JPanel(new BorderLayout());

        gridWrapper.setOpaque(false);
        gridWrapper.add(gridPanel, BorderLayout.NORTH);
        gridWrapper.setMaximumSize(new Dimension(900, 800));
        gridWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridWrapper.setForeground(Color.blue);
        
        contentCard.add(title);
        contentCard.add(Box.createVerticalStrut(10));
        contentCard.add(sep);
        contentCard.add(Box.createVerticalStrut(20));
        contentCard.add(gridWrapper);
        contentCard.add(Box.createVerticalGlue());

        return contentCard;
    }

    private JPanel createRoomCard(Room data) {
        // ใช้ JPanel ธรรมดา หรือ RoundedPanel ก็ได้ (ตามโค้ดเดิมใช้ RoundedPanel)
        JPanel card = new RoundedPanel(15, bgCream);
        
        card.setPreferredSize(new Dimension(250, 100));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblName = new JLabel(data.getName());
        lblName.setFont(new Font("Prompt Light", Font.BOLD, 14));
        lblName.setHorizontalAlignment(SwingConstants.CENTER);

        String bottomText = data.getDisplayText() != null ? data.getDisplayText() : "";
        if (data.getFloor() != null && !data.getFloor().isEmpty()) {
             bottomText += " (Fl." + data.getFloor() + ")";
        }

        JLabel lblDetail = new JLabel(bottomText);
        lblDetail.setFont(new Font("Prompt Light", Font.PLAIN, 12));
        lblDetail.setForeground(Color.GRAY);
        lblDetail.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(lblName, BorderLayout.CENTER);
        card.add(lblDetail, BorderLayout.SOUTH);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return card;
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