package ListPage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import Components.Components_;
import Components.SideBar;
import Data.Building;
import Data.DataAll;
import Data.Room;
import User.Datauser;

public class AdminListPage {
    
    private JPanel mainPanel;
    private JScrollPane detailScroll;
    private JScrollPane sidebarScroll; 
    
    // Data
    private List<DataAll> allDataList = new ArrayList<>();
    private List<JButton> categoryButtons = new ArrayList<>();
    
    private boolean isAdmin = true;  
    private int selectedBuildingId = -1; 

    // Colors
    private final int COLOR_BROWN = 0xA06E32;
    private final int COLOR_GOLD = 0xB48C00;
    private final int COLOR_CREAM = 0xFDFBF5;
    private final int COLOR_GRAY_BG = 0xF8F9FA;
    private final int COLOR_WHITE = 0xFFFFFF;
    private final int COLOR_BLACK = 0x000000;
    private final int COLOR_RED = 0xFF0000;
    
    private Color bgCreamColor = new Color(COLOR_CREAM);  

    private Font getPromptFont(int size) {
        return new Font("Prompt Light", Font.PLAIN, size);
    }

    public AdminListPage(JPanel cardContainer) {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgCreamColor);

        // โหลดข้อมูลครั้งแรก
        refreshDataList();

        mainPanel.add(new SideBar().createHeader(cardContainer), BorderLayout.NORTH);

        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setBorder(new EmptyBorder(20, 40, 20, 40));

        contentContainer.add(new SideBar("ลิสต์").getSideBar(cardContainer));
        contentContainer.add(Box.createVerticalStrut(20));

        JPanel splitView = new JPanel(new BorderLayout(20, 0));
        splitView.setOpaque(false);

        // --- Sidebar Scroll ---
        sidebarScroll = new JScrollPane();
        sidebarScroll.setBorder(null);
        sidebarScroll.getViewport().setOpaque(false);
        sidebarScroll.setOpaque(false);
        sidebarScroll.setPreferredSize(new Dimension(280, 0));
        sidebarScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        sidebarScroll.setViewportView(createSidebar()); 

        // --- Detail Scroll ---
        detailScroll = new JScrollPane();
        detailScroll.setBorder(null);
        detailScroll.getViewport().setOpaque(false);
        detailScroll.setOpaque(false);
        detailScroll.getVerticalScrollBar().setUnitIncrement(16);
        detailScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Default View (ใช้ Bubble Sort)
        if (!allDataList.isEmpty()) {
            
            // 1. กรองเฉพาะตึก
            List<DataAll> onlyBuildings = new ArrayList<>();
            for (DataAll d : allDataList) {
                if (d instanceof Building) {
                    onlyBuildings.add(d);
                }
            }
            
            // 2. เรียงลำดับด้วย Bubble Sort
            bubbleSortBuildings(onlyBuildings);

            // 3. เลือกตัวแรก
            DataAll first = onlyBuildings.isEmpty() ? null : onlyBuildings.get(0);
            
            if(first != null) {
                selectedBuildingId = first.getId(); 
                detailScroll.setViewportView(createDetailContent(first.getId(), first.getName()));
                refreshSidebarUI(); 
            } else {
                detailScroll.setViewportView(new JLabel("ไม่พบข้อมูลอาคาร"));
            }
        }

        splitView.add(sidebarScroll, BorderLayout.WEST);
        splitView.add(detailScroll, BorderLayout.CENTER);

        contentContainer.add(splitView);
        mainPanel.add(contentContainer, BorderLayout.CENTER);
    }

    public JPanel getPanel() { return mainPanel; }

    // --- Data Refresh Methods ---

    private void refreshDataList() {
        allDataList.clear();
        new Datauser().getAllData(allDataList);
    }

    private void refreshSidebarUI() {
        SwingUtilities.invokeLater(() -> {
            sidebarScroll.setViewportView(createSidebar());
            sidebarScroll.revalidate();
            sidebarScroll.repaint();
        });
    }

    private void refreshData() {
        refreshDataList();
        refreshSidebarUI();
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
                
                if (name1.compareToIgnoreCase(name2) > 0) {
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
                    Room temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    // --- Create Sidebar ---

    private JPanel createSidebar() {
        JPanel sidebarPanel = new RoundedPanel(20, Color.WHITE);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setOpaque(false);
        sidebarPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        Components_ headerComp = new Components_("รายการอาคาร", "label", 16, COLOR_GOLD);
        JComponent header = headerComp.getComponent();
        header.setAlignmentX(Component.LEFT_ALIGNMENT);  
        
        sidebarPanel.add(header);
        sidebarPanel.add(Box.createVerticalStrut(15));
        
        categoryButtons.clear(); 

        // ★★★ แก้ไข: เพิ่มให้ครบ 13 หมวดหมู่ ★★★
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
            {"อุโมงค์ต้นไม้", "12"},
            {"เรื่องเล่าผี", "13"}
        };

        for (String[] cat : categories) {
            addSidebarSection(sidebarPanel, cat[0], cat[1]);
            sidebarPanel.add(Box.createVerticalStrut(10));
        }
        // ★★★ จบส่วนแก้ไข ★★★

        if(isAdmin) {
            sidebarPanel.add(Box.createVerticalStrut(20));
            Components_ btnAddComp = new Components_("+ เพิ่มอาคารใหม่", "button", 13, 0x808080, COLOR_WHITE, 180, 35);
            JButton btnAdd = (JButton) btnAddComp.getComponent();
            btnAdd.setBorder(new javax.swing.border.LineBorder(new Color(200, 200, 200), 1, true));
            btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnAdd.addActionListener(e -> openBuildingDialog(-1, "", false));
            
            btnAdd.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btnAdd.setBackground(new Color(240, 240, 240));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btnAdd.setBackground(Color.WHITE);
                }
            });

            sidebarPanel.add(btnAdd);
        }
 
        sidebarPanel.add(Box.createVerticalGlue());
        return sidebarPanel;
    }
    
    private void addSidebarSection(JPanel panel, String title, String categoryId) {
        // เช็คก่อนว่ามีข้อมูลในหมวดนี้ไหม ถ้าไม่มีก็ไม่ต้องโชว์หัวข้อ (Optional: ถ้าอยากโชว์ตลอดให้ลบส่วนนี้ออก)
        boolean hasData = false;
        for (DataAll item : allDataList) {
            if (item instanceof Building && categoryId.equals(item.getCategory())) {
                hasData = true;
                break;
            }
        }
        // ถ้าต้องการโชว์หัวข้อแม้ไม่มีข้อมูล ให้ Comment if (hasData) ออก
        if (!hasData && !isAdmin) return; // Admin เห็นตลอด User เห็นเฉพาะที่มีข้อมูล
        
        Components_ titleComp = new Components_(title, "label", 14, COLOR_BROWN);
        JComponent lblTitle = titleComp.getComponent();
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(5));

        // 1. กรองข้อมูล (Filter) แบบ Manual
        List<DataAll> filteredList = new ArrayList<>();
        for (DataAll item : allDataList) {
            if (item instanceof Building && categoryId.equals(item.getCategory())) {
                filteredList.add(item);
            }
        }

        // 2. เรียงลำดับด้วย Bubble Sort
        bubbleSortBuildings(filteredList);

        // 3. วนลูปสร้างปุ่ม
        for (DataAll item : filteredList) {
            JButton btn = createSidebarItem(item.getDisplayText());
            
            if (item.getId() == selectedBuildingId) {
                btn.setBackground(new Color(COLOR_BROWN));
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(new Color(COLOR_GRAY_BG));
                btn.setForeground(Color.BLACK);
            }

            btn.addActionListener(e -> {
                selectedBuildingId = item.getId();
                for (JButton b : categoryButtons) {
                    if (b == btn) {
                        b.setBackground(new Color(COLOR_BROWN));
                        b.setForeground(Color.WHITE);
                    } else {
                        b.setBackground(new Color(COLOR_GRAY_BG));
                        b.setForeground(Color.BLACK);
                    }
                    b.repaint();
                }
                detailScroll.setViewportView(createDetailContent(item.getId(), item.getName()));
                detailScroll.revalidate();
                detailScroll.repaint();
            });

            categoryButtons.add(btn);
            panel.add(btn);
            panel.add(Box.createVerticalStrut(5));
        }
    }

    private JButton createSidebarItem(String text) {
        Components_ btnComp = new Components_(text, "button", 14, COLOR_BLACK, COLOR_GRAY_BG, 240, 40);
        JButton btn = (JButton) btnComp.getComponent();
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 15, 0, 0));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setVisible(true); 
        return btn;
    }

    private JPanel createDetailContent(int bid, String name) {
        JPanel mainContent = new RoundedPanel(20, Color.WHITE);
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(10, 20, 10, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(2000, 50));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Components_ titleComp = new Components_(name, "label", 24, COLOR_BROWN);
        headerPanel.add(titleComp.getComponent(), BorderLayout.WEST);

        if (isAdmin) {
            JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            adminPanel.setOpaque(false);
            
            Components_ editComp = new Components_("แก้ไข", "button", 12, 0x646464, COLOR_WHITE, 80, 30);
            JButton btnEdit = (JButton) editComp.getComponent();
            btnEdit.setBorder(BorderFactory.createCompoundBorder(
                    new javax.swing.border.LineBorder(new Color(220, 220, 220), 1, true),
                    new EmptyBorder(5, 5, 5, 5)
            ));
            btnEdit.addActionListener(e -> openBuildingDialog(bid, name, true));
            
            Components_ delComp = new Components_("ลบ", "button", 12, COLOR_RED, COLOR_WHITE, 80, 30);
            JButton btnDel = (JButton) delComp.getComponent();
            btnDel.setBorder(BorderFactory.createCompoundBorder(
                    new javax.swing.border.LineBorder(new Color(220, 220, 220), 1, true),
                    new EmptyBorder(5, 5, 5, 5)
            ));
            btnDel.addActionListener(e -> {
                 int confirm = JOptionPane.showConfirmDialog(mainPanel, "ยืนยันการลบอาคาร?", "Confirm", JOptionPane.YES_NO_OPTION);
                 if(confirm == JOptionPane.YES_OPTION && new Datauser().deleteBuilding(bid)) {
                     refreshData();
                 }
            });
            adminPanel.add(btnEdit);
            adminPanel.add(btnDel);
            headerPanel.add(adminPanel, BorderLayout.EAST);
        }
        mainContent.add(headerPanel);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(COLOR_BROWN));
        sep.setMaximumSize(new Dimension(2000, 2));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(Box.createVerticalStrut(10));
        mainContent.add(sep);
        mainContent.add(Box.createVerticalStrut(20));
        
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

        // 2. เรียงลำดับด้วย Bubble Sort
        bubbleSortRooms(filteredRooms);

        // 3. วนลูปสร้างการ์ดห้อง
        for (Room room : filteredRooms) {
            gridPanel.add(createRoomCard(room));
        }

        if (isAdmin) {
            gridPanel.add(createAddRoomCard(bid));
        }

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(gridPanel, BorderLayout.NORTH);
        gridWrapper.setMaximumSize(new Dimension(2000, 10000));
        gridWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        mainContent.add(gridWrapper);
        mainContent.add(Box.createVerticalGlue());

        return mainContent;
    }

    private void openBuildingDialog(int bid, String currentName, boolean isEdit) {
        JDialog d = new JDialog((Frame)SwingUtilities.getWindowAncestor(mainPanel), isEdit ? "แก้ไขอาคาร" : "เพิ่มอาคาร", true);
        d.setSize(500, 500); // ขยายความกว้างหน่อยเผื่อปุ่ม Upload
        d.setLocationRelativeTo(mainPanel);
        d.setLayout(new GridLayout(7, 2, 10, 10));

        // 1. เตรียมตัวแปรเก็บค่าเริ่มต้น
        String initName = isEdit ? currentName : "";
        String initDesc = "";
        String initImg = "/Map/building/default.png";
        String initX = "0.0";
        String initY = "0.0";
        int targetCatId = 1;

        // 2. ดึงข้อมูลเดิมจาก List (ถ้าเป็นการแก้ไข)
        if (isEdit) {
            for (DataAll item : allDataList) {
                if (item instanceof Building && item.getId() == bid) {
                    Building b = (Building) item;
                    initName = b.getName();
                    initDesc = (b.getDescription() != null) ? b.getDescription() : "";
                    initImg = (b.getImagePath() != null && !b.getImagePath().isEmpty()) ? b.getImagePath() : "/Map/building/default.png";
                    initX = String.valueOf(b.getMapX());
                    initY = String.valueOf(b.getMapY());
                    try {
                        targetCatId = Integer.parseInt(b.getCategory());
                    } catch (NumberFormatException e) {
                        targetCatId = 1;
                    }
                    break; 
                }
            }
        }
 
        JTextField txtName = (JTextField) new Components_(initName, "textfield", 14, COLOR_BLACK, COLOR_WHITE).getComponent();
        JTextField txtDesc = (JTextField) new Components_(initDesc, "textfield", 14, COLOR_BLACK, COLOR_WHITE).getComponent();
        JTextField txtImg = (JTextField) new Components_(initImg, "textfield", 14, COLOR_BLACK, COLOR_WHITE).getComponent();
        JTextField txtX = (JTextField) new Components_(initX, "textfield", 14, COLOR_BLACK, COLOR_WHITE).getComponent();
        JTextField txtY = (JTextField) new Components_(initY, "textfield", 14, COLOR_BLACK, COLOR_WHITE).getComponent();
        
  
        JButton btnUpload = new JButton("อัปโหลด .txt");
        btnUpload.setFont(new Font("Tahoma", Font.PLAIN, 12));
        btnUpload.setMargin(new Insets(0, 5, 0, 5));
        btnUpload.setFocusable(false);
        
        btnUpload.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            // กรองให้เลือกได้เฉพาะไฟล์ .txt
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
            fileChooser.setFileFilter(filter);
            
            int result = fileChooser.showOpenDialog(d);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    // อ่านไฟล์ด้วย UTF-8 เพื่อรองรับภาษาไทย
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(selectedFile), "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append(" "); // เอาแต่ละบรรทัดมาต่อกัน (เว้นวรรค)
                    }
                    reader.close();
                    
                    // นำข้อความที่อ่านได้ไปใส่ในช่อง Description
                    txtDesc.setText(sb.toString().trim());
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(d, "อ่านไฟล์ไม่สำเร็จ: " + ex.getMessage());
                }
            }
        });

        // สร้าง Panel เพื่อจัดเรียงช่องกรอก + ปุ่ม Upload ให้อยู่ด้วยกัน
        JPanel descPanel = new JPanel(new BorderLayout(5, 0));
        descPanel.setOpaque(false);
        descPanel.add(txtDesc, BorderLayout.CENTER);
        descPanel.add(btnUpload, BorderLayout.EAST);
        // --- ★★★ จบส่วน Upload ★★★ ---

        
        String[] catItems = {
            "1: อาคารเรียน", "2: ร้านค้า/อาหาร", "3: หอพัก", "4: บรรณสาร", 
            "5: บริการ/เครื่องมือ", "6: สนามกีฬา", "7: ยิม/ฟิตเนส", "8: ตู้ ATM", 
            "9: หอดูดาว", "10: อาคารค้นคว้า", "11: ท่องเที่ยวธรรมชาติ", 
            "12: อุโมงค์ต้นไม้", "13: เรื่องเล่าผี"
        };
        JComboBox<String> cbCat = new JComboBox<>(catItems);
        cbCat.setFont(getPromptFont(14));
        cbCat.setBackground(Color.WHITE);

        if (isEdit) {
            for (int i = 0; i < catItems.length; i++) {
                if (catItems[i].startsWith(String.valueOf(targetCatId) + ":")) {
                    cbCat.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        // เพิ่ม Component ลง Dialog
        d.add(new Components_(" ชื่ออาคาร:", "label", 14).getComponent()); d.add(txtName);
        d.add(new Components_(" หมวดหมู่:", "label", 14).getComponent()); d.add(cbCat);
        
        // ★ เปลี่ยนตรงนี้: ใส่ descPanel แทน txtDesc เพียวๆ
        d.add(new Components_(" คำอธิบาย:", "label", 14).getComponent()); d.add(descPanel);
        
        d.add(new Components_(" รูปภาพ (Path/URL):", "label", 14).getComponent()); d.add(txtImg);
        d.add(new Components_(" Map X:", "label", 14).getComponent()); d.add(txtX);
        d.add(new Components_(" Map Y:", "label", 14).getComponent()); d.add(txtY);

        Components_ btnSaveComp = new Components_("บันทึก", "button", 14, COLOR_BLACK, COLOR_GRAY_BG);
        JButton btnSave = (JButton) btnSaveComp.getComponent();
        btnSave.setBorder(new javax.swing.border.LineBorder(Color.LIGHT_GRAY, 1, true));
        
        btnSave.addActionListener(e -> {
            try {
                String n = txtName.getText();
                String desc = txtDesc.getText(); // ดึงค่าจากช่องที่อาจจะมาจากการ Upload
                String img = txtImg.getText();
                double x = Double.parseDouble(txtX.getText());
                double y = Double.parseDouble(txtY.getText());
                int cat = Integer.parseInt(((String)cbCat.getSelectedItem()).split(":")[0]);
                
                Datauser db = new Datauser();
                boolean success = isEdit ? db.updateBuilding(bid, n, cat, desc, img, x, y) 
                                         : db.addBuilding(n, cat, desc, img, x, y);
                                         
                if(success) {
                    JOptionPane.showMessageDialog(d, "บันทึกสำเร็จ");
                    d.dispose();
                    refreshData(); 
                    
                    if (selectedBuildingId == bid) {
                        detailScroll.setViewportView(createDetailContent(bid, n));
                        detailScroll.revalidate();
                        detailScroll.repaint();
                    }
                } else {
                    JOptionPane.showMessageDialog(d, "บันทึกไม่สำเร็จ");
                }
            } catch(NumberFormatException ex) {
                JOptionPane.showMessageDialog(d, "ข้อมูลไม่ถูกต้อง (Map X/Y ต้องเป็นตัวเลขทศนิยม)", "Error", JOptionPane.ERROR_MESSAGE);
            } catch(Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "เกิดข้อผิดพลาด: " + ex.getMessage());
            }
        });
        
        d.add(new JLabel("")); d.add(btnSave);
        d.setVisible(true);
    }
    private void refreshUIContent(int parentBid) {
        refreshDataList();

        String currentBuildingName = "";
        for (DataAll item : allDataList) {
            if (item instanceof Building && item.getId() == parentBid) {
                currentBuildingName = item.getName();
                break;
            }
        }
        
        String finalName = currentBuildingName;
        SwingUtilities.invokeLater(() -> {
            sidebarScroll.setViewportView(createSidebar());
            detailScroll.setViewportView(createDetailContent(parentBid, finalName));
            detailScroll.revalidate();
            detailScroll.repaint();
        });
    }
    private void openRoomDialog(int parentBid, Room roomToEdit) {
        boolean isEdit = (roomToEdit != null);
        String title = isEdit ? "แก้ไขข้อมูลห้อง" : "เพิ่มห้องใหม่";

        JDialog d = new JDialog((Frame)SwingUtilities.getWindowAncestor(mainPanel), title, true);
        d.setSize(300, 350); 
        d.setLocationRelativeTo(mainPanel);
        d.setLayout(new GridLayout(5, 2, 10, 10)); 

        String initName = isEdit ? roomToEdit.getName() : "";
        String initFloor = isEdit ? roomToEdit.getFloor() : "";
        String initDet = isEdit ? roomToEdit.getDescription() : ""; 

        JTextField txtName = (JTextField) new Components_(initName, "textfield", 14, COLOR_BLACK, COLOR_WHITE).getComponent();
        JTextField txtFloor = (JTextField) new Components_(initFloor, "textfield", 14, COLOR_BLACK, COLOR_WHITE).getComponent();
        JTextField txtDet = (JTextField) new Components_(initDet, "textfield", 14, COLOR_BLACK, COLOR_WHITE).getComponent();

        d.add(new Components_(" ชื่อห้อง:", "label", 14).getComponent()); d.add(txtName);
        d.add(new Components_(" ชั้น:", "label", 14).getComponent()); d.add(txtFloor);
        d.add(new Components_(" รายละเอียด:", "label", 14).getComponent()); d.add(txtDet);

        Components_ btnSaveComp = new Components_("บันทึก", "button", 14, COLOR_BLACK, COLOR_GRAY_BG);
        JButton btnSave = (JButton) btnSaveComp.getComponent();
        btnSave.setBorder(new javax.swing.border.LineBorder(Color.LIGHT_GRAY, 1, true));

        btnSave.addActionListener(e -> {
            Datauser db = new Datauser();
            boolean success = false;
            if (isEdit) {
                success = db.updateRoom(roomToEdit.getId(), txtName.getText(), txtFloor.getText(), txtDet.getText());
            } else {
                success = db.addRoom(txtName.getText(), txtFloor.getText(), txtDet.getText(), parentBid);
            }

            if(success) {
                JOptionPane.showMessageDialog(d, "บันทึกสำเร็จ");
                d.dispose();
                refreshUIContent(parentBid);
            } else {
                JOptionPane.showMessageDialog(d, "เกิดข้อผิดพลาด");
            }
        });
        
        d.add(new JLabel("")); d.add(btnSave);

        if (isEdit) {
            Components_ btnDelComp = new Components_("ลบห้องนี้", "button", 14, COLOR_RED, COLOR_WHITE);
            JButton btnDel = (JButton) btnDelComp.getComponent();
            btnDel.setBorder(new javax.swing.border.LineBorder(Color.RED, 1, true));
            
            btnDel.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(d, "ยืนยันการลบห้องนี้?", "ลบข้อมูล", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (new Datauser().deleteRoom(roomToEdit.getId())) {
                        d.dispose();
                        refreshUIContent(parentBid);
                    }
                }
            });
            d.add(new JLabel("")); d.add(btnDel);
        }

        d.setVisible(true);
    }

    private JPanel createRoomCard(Room data) {
        JPanel card = new RoundedPanel(15, bgCreamColor);
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

        if (isAdmin) {
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    openRoomDialog(data.getBId(), data); 
                }
            });
        }
        return card;
    }
    
    private JPanel createAddRoomCard(int parentBid) {
        RoundedPanel card = new RoundedPanel(15, new Color(230, 230, 230));
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(200, 100));
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblPlus = new JLabel("+");
        lblPlus.setFont(new Font("Prompt Light", Font.BOLD, 40));
        lblPlus.setForeground(Color.GRAY);
        lblPlus.setHorizontalAlignment(SwingConstants.CENTER);
        
        Components_ lblComp = new Components_("เพิ่มห้อง", "label", 12, 0x808080);
        JLabel lblText = (JLabel) lblComp.getComponent();
        
        card.add(lblPlus, BorderLayout.CENTER);
        card.add(lblText, BorderLayout.SOUTH);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openRoomDialog(parentBid, null); 
            }
        });
        return card;    
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}