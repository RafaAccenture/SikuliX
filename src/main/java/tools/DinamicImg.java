package tools;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;



public class DinamicImg {
	private String path;
	
	
	
    private static BufferedImage resizeImage(BufferedImage originalImage, int type,String content, FontMetrics metrics){
	BufferedImage resizedImage = new BufferedImage(metrics.stringWidth(content)+4, metrics.getAscent() + metrics.getDescent()+4, type);
	Graphics2D g = resizedImage.createGraphics();
	g.drawImage(originalImage, 0, 0, metrics.stringWidth(content)+4,  metrics.getAscent() + metrics.getDescent()+4, null);
	g.dispose();

	return resizedImage;
    }

	
	public DinamicImg(){}
	public DinamicImg(String path){
		this.path=path;
	}
	
	public String activityDetailsMove_CalendarDayBlanck() {
		setPath("activityDetailsMove_CalendarDayBlanck.png");
		File blankFile = new File("images/dinamic/"+this.path);
		BufferedImage image = null;
		try {
		    final Calendar now = GregorianCalendar.getInstance();
		    final int dayNumber = now.get(Calendar.DAY_OF_MONTH);
			image = ImageIO.read(blankFile);
			int w = image.getWidth();
			int h = image.getHeight();
		    Graphics2D g2 = image.createGraphics();
		    Font font= new Font("Arial,Helvetica,sans-serif", Font.BOLD, 18);
		    g2.setColor(Color.decode("#272b2d"));
		    g2.setFont(font);
		    // Get the FontMetrics
		    FontMetrics metrics = g2.getFontMetrics(font);
		    
		    int x = (w - metrics.stringWidth(String.valueOf(dayNumber))) / 2;
		    int y = (metrics.getAscent() + (h - (metrics.getAscent() + metrics.getDescent())) / 2);

		    
		    g2.drawString(String.valueOf(dayNumber), x, y);
		    g2.dispose();
		    
		    //create image with text
		    String exit_path = "images/dinamic/changed_"+path;
		    ImageIO.write(image, "png", new File(exit_path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "images/dinamic/changed_"+path;
	}
	/**
	 * 
	 * @param colorLetter
	 * @param colorBackground
	 * @param text
	 * @param font
	 * @return
	 * @throws IOException
	 */
	public String insertTextOnBlanck(String colorLetter,
			int [] colorBackground,
			String text,
			Font font) throws IOException {
		String exit_path = "src/main/resources/images/dinamic/changed_"+getPath();
		InputStream originalStream = new FileInputStream("src/main/resources/images/dinamic/"+getPath());
		OutputStream outStream = new FileOutputStream(exit_path);
		ImageIO.setUseCache(false);
		BufferedImage image = null;
		try {
			image = ImageIO.read(originalStream);
			int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
			Graphics2D g2 = image.createGraphics();
			g2.setPaint(new Color(colorBackground[0],colorBackground[1],colorBackground[2]));
			g2.fillRect ( 0, 0, image.getWidth(), image.getHeight() );
			FontMetrics metrics = g2.getFontMetrics(font);
			BufferedImage resizeImage = resizeImage(image,type, text,metrics);
			int w = resizeImage.getWidth();
			int h = resizeImage.getHeight();
			g2 = resizeImage.createGraphics();
			g2.setColor(Color.decode(colorLetter));
		    g2.setFont(font);
		    // Get the FontMetrics
		    int x = (w - metrics.stringWidth(text)) / 2;
		    int y = (metrics.getAscent() + (h - (metrics.getAscent() + metrics.getDescent())) / 2);
		    g2.setBackground(Color.decode("#d1e8f8"));
		    g2.drawString(text, x, y);
		    g2.dispose();
		    ImageIO.write(resizeImage, "png", outStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			image.flush();
            outStream.flush();
            outStream.close();
            originalStream.close();
        }
		return exit_path;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
