import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Components.Components_;
import Home.Home;
import ListPage.ListPage;
import Map.MapPage;
import ReportPage.ReportPage;
import Search.Search;
import User.Datauser;
import User.Login;
import User.Register;
import User.Resetpassword;

public class MainFrame {

	public static void main(String[] args) {
		UIManager.put("OptionPane.messageFont", new Font("Prompt Light", Font.PLAIN, 14));
		UIManager.put("OptionPane.buttonFont", new Font("Prompt Light", Font.BOLD, 12));
		UIManager.put("OptionPane.messageForeground", Color.BLACK);
		System.setProperty("sun.java2d.d3d", "false");

		JFrame frame = new JFrame("SUT Navigator");
 
		String targetFontName = "Prompt Light";  

       
        boolean fontExists = false;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        
        for (String name : fontNames) {
            if (name.equalsIgnoreCase(targetFontName)) {
                fontExists = true;
                break;
            }
        }

 
        if (!fontExists) {
            System.out.println("ไม่พบฟอนต์ กำลังดาวน์โหลด...");
            try {
           
                File downloadedFile = downloadFontFromWeb("https://files.litlelab.site/uploads/uploads/6962a03b0c1ef-Prompt-Light.ttf");
                
              
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, downloadedFile);
                ge.registerFont(customFont);
                
                System.out.println("โหลดฟอนต์เสร็จสิ้น พร้อมใช้งาน");
                
            } catch (Exception e) {
                e.printStackTrace();
                
            }
        }
	 
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		 
		frame.setLayout(new BorderLayout()); 
		 

		CardLayout cardLayout = new CardLayout();
		JPanel mainContainer = new JPanel(cardLayout);
		java.net.URL iconURL = MainFrame.class.getResource("/Asset/SUT_NAVIGATOR_ICON.png");
		if (iconURL != null) {
			ImageIcon icon = new ImageIcon(iconURL);
			frame.setIconImage(icon.getImage());
		} else {
			System.err.println("หาไฟล์ icon ไม่เจอ!");
		}

		Datauser d = new Datauser();
		String username = d.autoLoginIfPossible();
		if (username != null) {
			JOptionPane.showMessageDialog(mainContainer, "ยินดีต้อนรับคุณ " + username, "สำเร็จ",
					JOptionPane.INFORMATION_MESSAGE);

			Datauser.username = username;
			boolean isAdmin = "admin".equals(username);

			Register register = new Register(mainContainer);
			Home Home = new Home(mainContainer, isAdmin);
			Search Search = new Search(mainContainer);
			ListPage listPage = new ListPage(mainContainer, isAdmin);
			ReportPage report = new ReportPage(mainContainer);
			MapPage MapPage = new MapPage(mainContainer, isAdmin);

			mainContainer.removeAll();

			mainContainer.add(MapPage.getPanel(), "MAP_PAGE");
			mainContainer.add(report.getPanel(), "REPORT_PAGE");
			mainContainer.add(register.getPanel(), "REGISTER_PAGE");
			mainContainer.add(Home.getPanel(), "HOME_PAGE");
			mainContainer.add(Search.getPanel(), "SEARCH_PAGE");
			mainContainer.add(listPage.getPanel(), "LIST_PAGE");

			CardLayout cl = (CardLayout) mainContainer.getLayout();
			cl.show(mainContainer, "HOME_PAGE");

		} else {
			Login login = new Login(mainContainer);
			Register register = new Register(mainContainer);
			Resetpassword Resetpassword = new Resetpassword(mainContainer);
		 
			 
 
		 
			mainContainer.add(login.getPanel(), "LOGIN_PAGE");
			mainContainer.add(register.getPanel(), "REGISTER_PAGE");
			mainContainer.add(Resetpassword.getPanel(), "RESET_PAGE");
 
			cardLayout.show(mainContainer, "LOGIN_PAGE");
		}

		 
		frame.add(mainContainer, BorderLayout.CENTER);
		
		frame.setVisible(true);

		KeyStroke keyStroke = KeyStroke.getKeyStroke("Q");
		mainContainer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "quitFunction");
		mainContainer.getActionMap().put("quitFunction", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}
	private static File downloadFontFromWeb(String urlStr) throws IOException {
        URL url = new URL(urlStr);
       
        File tempFile = File.createTempFile("app_font_", ".ttf");

        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
            
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            
             
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
        return tempFile;
    }
}