package tools;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;



public class DinamicImg {
	private String path;
	
	
	
    private static BufferedImage resizeDinamicImage(BufferedImage originalImage, int type,String content, FontMetrics metrics){
	BufferedImage resizedImage = new BufferedImage(metrics.stringWidth(content)+4, metrics.getAscent() + metrics.getDescent()+4, type);
	Graphics2D g = resizedImage.createGraphics();
	g.drawImage(originalImage, 0, 0, metrics.stringWidth(content)+4,  metrics.getAscent() + metrics.getDescent()+4, null);
	g.dispose();

	return resizedImage;
    }

	/**
	 * Constructor básico
	 */
	public DinamicImg(){}
	
	/**
	 * Constructor completo
	 * @param path
	 */
	public DinamicImg(String path){
		this.path=path;
	}
	
	/**
	 * Dada una fuente, tamaño y colores inserta texto en una foto en blanco y la escala ajustándose a 
	 * las características.
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
		String exit_path = "src/main/resources/images/Dinamic/changed_"+getPath();
		InputStream originalStream = new FileInputStream("src/main/resources/images/Dinamic/"+getPath());
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
			BufferedImage resizeImage = resizeDinamicImage(image,type, text,metrics);
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
	
	public String obtainScaledImage(String currentFolderPath,float ratioScale) throws IOException {
		InputStream originalStream = null;
		 OutputStream outStream = null;
		BufferedImage  original_image = null,scaled_image = null;
		String exit_path = "";
		try {
			  originalStream = new FileInputStream(currentFolderPath+getPath());
			  
			  original_image = ImageIO.read(originalStream);
			  int scaledWidth = (int) (ratioScale* original_image.getWidth());
			  int scaledHeight = (int) (ratioScale* original_image.getHeight());
			  scaled_image = new BufferedImage( scaledWidth, scaledHeight, original_image.getType() );
			  Graphics2D g2 = scaled_image.createGraphics(); //crea un objecto Graphics para manipular la imagen
			  g2.drawImage( original_image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH), 0, 0, scaledWidth, scaledHeight, null ); //dibuja la imagen escalada
			  g2.dispose();
			  exit_path = currentFolderPath+"scaled_"+getPath();
			  outStream = new FileOutputStream(exit_path);
			  ImageIO.write( scaled_image, "PNG", outStream ); //escribe la imagen en el archivo determinado por la salida outStream
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			original_image.flush();
			scaled_image.flush();
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
