package Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorListener;  
import javax.swing.event.AncestorEvent;    

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionEvent;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.ImageIcon;

public class Map {
    private Consumer<Integer> callback;

    public Map() {
    }

    public JPanel getMap(JPanel c_) {
        int rows = 1000;
        int cols = 2000;
        GridPanel cardPanel = new GridPanel(rows, cols, 1400, 800, "/Asset/Map.png");
        cardPanel.setOpaque(true);

       
        cardPanel.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                System.out.println("Opening Map: Refreshing Data...");
                cardPanel.refreshData(); // โหลดข้อมูลใหม่ทุกครั้งที่เปิด
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });

        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CardLayout cl = (CardLayout) c_.getLayout();
                cl.show(c_, "MAP_PAGE");
            }
        });

        return cardPanel;
    }

    public JPanel getMap_callback(JPanel c_, Consumer<Integer> c) {
        int rows = 1000;
        int cols = 2000;
        GridPanel cardPanel = new GridPanel(rows, cols, 1600, 800, "/Asset/Map.png");
        cardPanel.setcall = c;
        cardPanel.setOpaque(true);

       
        cardPanel.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                System.out.println("Opening Map (Callback): Refreshing Data...");
                cardPanel.refreshData();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });

        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CardLayout cl = (CardLayout) c_.getLayout();
                cl.show(c_, "MAP_PAGE");
            }
        });

        return cardPanel;
    }
}

class GridPanel extends JPanel {
    public Consumer<Integer> setcall = null;
    private int rows;
    private int cols;
    private int width;
    private int height;
    private Image bgImage;
    private int originalWidth;
    private int originalHeight;
    private double zoomScale = 1.3;
    private Image pinImage;
    private List<Image> scaledPinImage = new ArrayList<>();
    private List<PinHitbox> pinHitboxes = new ArrayList<>();
    private int currentCategoryFilter = 0;
    private double[][] coordinates;

    public GridPanel(int rows, int cols, int width, int height, String imagePath) {
        this.rows = rows;
        this.cols = cols;
        this.width = width;
        this.height = height;
        this.bgImage = new ImageIcon(imagePath).getImage();
        this.originalWidth = width;
        this.originalHeight = height;
        
     
        coordinates = new CoordinateBuilding().get();

        java.net.URL imgUrl = getClass().getResource(imagePath);
        if (imgUrl != null) {
            this.bgImage = new ImageIcon(imgUrl).getImage();
        } else {
            System.err.println("หาไฟล์รูปไม่เจอ: " + imagePath);
        }
        
        String[] iconPaths = { 
            "/Asset/pin/study.png", "/Asset/pin/pin_Dining.png", "/Asset/pin/pin_Housing.png",
            "/Asset/pin/pin_Library.png", "/Asset/pin/pin_Recreation.png", "/Asset/pin/pin_Sport.png",
            "/Asset/pin/pin_Gym.png", "/Asset/pin/yellow_icon.png", "/Asset/pin/pin_Observatory.png",
            "/Asset/pin/pin_Research_building.png", "/Asset/pin/pin_Forest_tunnel.png", 
            "/Asset/pin/pin_Natural tourist attractions.png", "/Asset/pin/pin_ghost_Desu.png" 
        };

        for (String url : iconPaths) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(url));
                Image originalImg = icon.getImage();

                int targetWidth = 64;
                int targetHeight = (originalImg.getHeight(null) * targetWidth) / originalImg.getWidth(null);

                java.awt.image.BufferedImage resizedImg = new java.awt.image.BufferedImage(targetWidth, targetHeight,
                        java.awt.image.BufferedImage.TYPE_INT_ARGB);

                Graphics2D g2 = resizedImg.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(originalImg, 0, 0, targetWidth, targetHeight, null);
                g2.dispose();

                scaledPinImage.add(resizedImg);
            } catch (Exception e) {
                System.err.println("หาไฟล์รูปหมุดไม่เจอ: " + e.getMessage());
            }
        }

        this.setPreferredSize(new Dimension(width, height));

        int nw = (int) (originalWidth * zoomScale);
        int nh = (int) (originalHeight * zoomScale);
        setPreferredSize(new Dimension(nw, nh));
        revalidate();
        repaint();

        this.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                if (e.getWheelRotation() < 0) {
                    if (zoomScale >= 1) {
                        zoomScale *= 1.02;
                    }
                } else {
                    if (zoomScale > 1.4) {
                        zoomScale /= 1.02;
                    }
                }
                JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, this);

                if (scrollPane == null)
                    return;

                JScrollBar vBar = scrollPane.getVerticalScrollBar();

                int newWidth = (int) (originalWidth * zoomScale);
                int newHeight = (int) (originalHeight * zoomScale);

                setPreferredSize(new Dimension(newWidth, newHeight));
                revalidate();
                repaint();
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (PinHitbox hitbox : pinHitboxes) {
                    if (hitbox.area.contains(e.getPoint())) {
                        if (setcall != null) {
                            setcall.accept(hitbox.buildingId);
                        } else {
                            System.out.println("Error: setcall ยังเป็น null อยู่");
                        }
                        return;
                    }
                }
            }
        });
    }

   
    public void refreshData() {
        this.coordinates = new CoordinateBuilding().get();
        repaint();
    }

    public void setFilter(int categoryId) {
        this.coordinates = new CoordinateBuilding().get();
        this.currentCategoryFilter = categoryId;
        repaint();
    }

    public void paintCell(int x, int y) {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
     
        pinHitboxes.clear();

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
        
        int currentWidth = getWidth();
        int currentHeight = getHeight();

        if (currentWidth == 0 || currentHeight == 0) {
            currentWidth = getPreferredSize().width;
            currentHeight = getPreferredSize().height;
        }

        // --- ส่วนวาดเส้น Grid (ถ้าต้องการ) ---
        // g.setColor(new Color(0, 0, 0, 0));
        // double cellWidth = (double) currentWidth / cols;
        // double cellHeight = (double) currentHeight / rows;
        // for (int r = 0; r <= rows; r++) { ... }
        // for (int c = 0; c <= cols; c++) { ... }

        // --- วนลูปวาด Pin ตาม coordinates ที่โหลดมา ---
        for (double[] node : coordinates) {
            int categoryId = (int) node[3];
            if (currentCategoryFilter == 0 || currentCategoryFilter == categoryId) {
                drawPin(g2d, node[1], node[2], currentWidth + 35, currentHeight + 35, categoryId, (int) node[0]);
            }
        }
    }

    private void drawPin(Graphics2D g2d, double latVal, double lonVal, double mapWidth, double mapHeight, int t,
            int buildingId) {

        double lat = latVal;
        double lon = lonVal;
        
        // พิกัดขอบเขตแผนที่ (ปรับตามความเหมาะสม)
        double latMax = 14.90272 - 0.011;
        double latMin = 14.86147 + 0.011;
        double lonMin = 101.99737 + 0.00;
        double lonMax = 102.03376 - 0.00;

        double rawX = ((lon - lonMin) / (lonMax - lonMin) * (mapWidth));
        double rawY = ((latMax - lat) / (latMax - latMin) * (mapHeight));

        double pivotX = getWidth() / 2.0;
        double pivotY = getHeight() / 2.0;

        double rad = Math.toRadians(304.5);

        double dx = rawX - pivotX;
        double dy = rawY - pivotY;

        int centerX = (int) (pivotX + (dx * Math.cos(rad) - dy * Math.sin(rad)));
        int centerY = (int) (pivotY + (dx * Math.sin(rad) + dy * Math.cos(rad)));

        int iconIndex = t - 1;
        boolean hasImage = false;

        g2d.setFont(new Font("Prompt Light", Font.PLAIN, 12));
        
        if (scaledPinImage != null && iconIndex >= 0 && iconIndex < scaledPinImage.size()) {
            Image img = scaledPinImage.get(iconIndex);
            if (img != null && img.getWidth(null) > 0) {
                hasImage = true;
                int pinWidth = 32;

                int pinHeight = (pinWidth * img.getHeight(null)) / img.getWidth(null);

                int drawX = centerX - (pinWidth / 2);
                int drawY = centerY - pinHeight;

                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillOval(centerX - (pinWidth / 4), centerY - 3, pinWidth / 2, 6);

                Rectangle rect = new Rectangle(drawX, drawY, pinWidth, pinHeight);

                // Add hitbox เพื่อให้คลิกได้
                pinHitboxes.add(new PinHitbox(rect, buildingId));
                
                g2d.drawImage(img, drawX, drawY, pinWidth, pinHeight, null);
                g2d.drawString(getCategoryName(iconIndex + 1), drawX - 2, drawY - 8);
            }
        }

        if (!hasImage) {
            g2d.setColor(Color.RED);
            g2d.fillOval(centerX - 5, centerY - 5, 10, 10);
        }
    }

    private String getCategoryName(int catId) {
        switch (catId) {
            case 1: return "อาคารเรียน";
            case 2: return "ร้านค้า/อาหาร";
            case 3: return "หอพัก";
            case 4: return "บรรณสาร";
            case 5: return "บริการ/เครื่องมือ";
            case 6: return "สนามกีฬา";
            case 7: return "ยิม/ฟิตเนส";
            case 8: return "ตู้ ATM";
            case 9: return "หอดูดาว";
            case 10: return "อาคารค้นคว้า";
            case 11: return "ท่องเที่ยวธรรมชาติ";
            case 12: return "อุโมงค์ต้นไม้";
            case 13: return "เรื่องเล่าผี";
            default: return "ทั่วไป";
        }
    }

    class PinHitbox {
        Rectangle area;
        int buildingId;

        public PinHitbox(Rectangle area, int buildingId) {
            this.area = area;
            this.buildingId = buildingId;
        }
    }
}