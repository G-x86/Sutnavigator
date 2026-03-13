package Components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Components_ {
	private JComponent component;

	public Components_(String text, String type, int... args) {
		int fontSize = (args.length > 0) ? args[0] : 14;
		int fgColor = (args.length > 1) ? args[1] : 0x000000;
		int bgColor = (args.length > 2) ? args[2] : 0xFFFFFF;
		int width = (args.length > 3) ? args[3] : 0;
		int height = (args.length > 4) ? args[4] : 40;
		int radius = 30;
		switch (type.toLowerCase()) {
	 
		case "button":
			component = new JButton(text) {
				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					g2.setColor(getBackground());
					g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

					g2.dispose();
					super.paintComponent(g);
				}
			};

			((JButton) component).setContentAreaFilled(false);
			((JButton) component).setFocusPainted(false);
			((JButton) component).setBorderPainted(false);
			component.setCursor(new Cursor(Cursor.HAND_CURSOR));
			break;
		case "textfield":

		case "password":

			if (type.equalsIgnoreCase("password")) {
				component = new JPasswordField(text);
			} else {
				component = new JTextField(text);
			}

			component.setOpaque(false);

			component = new JTextField(text) {
				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setColor(getBackground());
					g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
					super.paintComponent(g);
				}

				@Override
				protected void paintBorder(Graphics g) {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setColor(Color.LIGHT_GRAY); 
					g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
				}
			};

			if (type.equalsIgnoreCase("password")) {
				component = new JPasswordField(text) {
					@Override
					protected void paintComponent(Graphics g) {
						Graphics2D g2 = (Graphics2D) g.create();
						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						g2.setColor(getBackground());
						g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
						super.paintComponent(g);
					}

					@Override
					protected void paintBorder(Graphics g) {
						Graphics2D g2 = (Graphics2D) g.create();
						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						g2.setColor(Color.LIGHT_GRAY);
						g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
					}
				};
			}

			((JTextField) component).setBorder(new EmptyBorder(0, 15, 0, 15));
			break;

		default:
			component = new JLabel(text);
			break;
		}

		component.setFont(new Font("Prompt Light", Font.PLAIN, fontSize));
		component.setForeground(new Color(fgColor));

		if (!type.equals("label")) {
			component.setBackground(new Color(bgColor));
		}

		component.setAlignmentX(Component.CENTER_ALIGNMENT);
		int finalWidth = (width == 0) ? Integer.MAX_VALUE : width;
		component.setMaximumSize(new Dimension(finalWidth, height));
		component.setPreferredSize(new Dimension(finalWidth, height));
	}

	public JComponent getComponent() {
		return component;
	}
}