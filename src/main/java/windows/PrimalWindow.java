package windows;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.sikuli.script.Button;
import org.sikuli.script.FindFailed;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import model.Settings;
import model.UserAmdocs;
import tools.DinamicImg;
 
public class PrimalWindow {
	
	final private Pattern generalWait = new Pattern("src/main/resources/images/generalWait.PNG").similar(0.95f);
	private Screen myScreen;
	private List<Pattern> references;
	private List<Object> data;
	private UserAmdocs userLogged;
	private Settings settings;
	private String repoPath;
	private String windowPath;
	private String sourceAction;
	private Pattern lastPatternConfirmed;
	private Queue<Pattern> nextWindowScript;
	/*	-------------------------------------------------
	 * 					ZONA PRIVADA
	 	-------------------------------------------------*/
	/**
	 * borra el archivo con la ruta del parametro
	 * @param toRemove
	 */
	private void removeFile(String toRemove) {
		File file = new File(toRemove);
		if(file.delete()){
			System.out.println(file.getName() + " is deleted!");
		}else{
			System.out.println("Delete operation is failed.");
		}
	}

	/*	-------------------------------------------------
	 * 					ZONA PROTEGIDA
	 	-------------------------------------------------*/
	/**
	 * Dado un menu desplegable en un formulario selecciona la posicion dada como</br>
	 * parámetro de entrada empezando desde arriba.
	 * @param pos : posicion a seleccionar
	 */
	void selectFromMenuInput(int pos) {
		for(int i = 0;i < pos;i++) {
			waitInMilisecs(300);
			getMyScreen().type(Key.DOWN);
		}
		waitInMilisecs(1000);
		getMyScreen().type(Key.ENTER);
	}
	/**
	 * Inserta texto en una imagen con las propiedades especificadas </br>
	 * <b>Ejemplo:</b>
	 * </br>
	 *	"Buscar","Cambria","#272b2d",
	 *  new int[] {255,255,255},Font.PLAIN,18,
	 *	true
	 * @param path
	 * @param input
	 * @param fontLetter
	 * @param colorLetter
	 * @param colorBackground
	 * @param fontType
	 * @param size
	 * @throws FindFailed
	 * @throws IOException
	 */
	protected void  dinamicClick(
			String input,
			String fontLetter,
			String colorLetter,
			int [] colorBackground,
			int fontType,
			int size,
			boolean clickAll) throws FindFailed, IOException {
		System.out.println("creacion dinamica para "+input);
		Match m = null;
		Pattern p = null;
		DinamicImg DimImg = new DinamicImg();
		DimImg.setPath("blanckTextPrimal.png");
		String modPath = DimImg.insertTextOnBlanck(
				colorLetter,
				colorBackground,
				input,//Inventario de recurso
				new Font(fontLetter,fontType, size)
				);
		if(clickAll) {
			System.out.println("click multiple");			
			p = new Pattern(modPath).similar(0.65f);
			m = this.myScreen.findBest(p);

			System.out.println(m.getScore());
			Iterator<Match> myIt = myScreen.findAll(p);
			while (myIt.hasNext()) {
				myIt.next().click();
			}
			myIt.remove();
		}else {
			System.out.println("click individual");
			p = new Pattern(modPath).similar(0.40f);
			m = this.myScreen.findBest(p);
			System.out.println(m.getScore());
			m.click();
		}
		removeFile(modPath);
		ImagePath.reset();//limpia la cachï¿½ interna de la librerï¿½a
	}
	
	/**
	 * Dado el nombre de una imagen se usa la ruta por defecto de la ventana y el porcentaje de escala de las configuraciones
	 * para escalar la imagen devolviendo la ruta donde se ha creado
	 * @param imageName
	 * @return
	 */
	protected String reescaledImage(String imageName) {
		DinamicImg DimImg = new DinamicImg(imageName);
		String exit = DimImg.obtainScaledImage(getWindowPath(), getSettings().getScaledImgRatio());
		return exit;
	}
	/**
	 * Dado el nombre de una imagen  y su ruta se usa el porcentaje de escala de las configuraciones
	 * para escalar la imagen devolviendo la ruta donde se ha creado
	 * @param path
	 * @param imageName
	 * @return
	 */
	protected String reescaledImage(String path, String imageName) {
		DinamicImg DimImg = new DinamicImg(imageName);
		String exit = DimImg.obtainScaledImage(path, getSettings().getScaledImgRatio());
		return exit;
	}
	/**
	 * Usa los scrolls de la ventana para moverse
	 * <h1>opciones parametro entrada</h1>
	 * <ul>
	 * <li>ABAJO</li>
	 * <li>ARRIBA por hacer</li>
	 * <li>DERECHA</li>
	 * <li>IZQUIERDA</li>
	 * </ul>
	 * @param direccion
	 * @throws FindFailed
	 */
	protected void mover_ventana(String direccion) {
		Region tmpReg;
		try {
			if(direccion.equals("ABAJO")) {
				tmpReg = new Region(1476,148,123,713);//seccion de la derecha del todo
				tmpReg.hover(getRepoPath()+"vertical_scroll_down.png");
	
			}else if(direccion.equals("DERECHA")) {
				tmpReg = new Region(0,794,1600,106);//seccion abajo de la ventana
				tmpReg.hover(getRepoPath()+"scroll_horizontal_right.png");
				
			}else if(direccion.equals("IZQUIERDA")) {
				tmpReg = new Region(0,794,1600,106);//seccion abajo de la ventana
				tmpReg.hover(getRepoPath()+"horizontal_scroll_left.png");
				
			}
			
			waitInMilisecs(500);
			getMyScreen().mouseDown(Button.LEFT);
			waitInMilisecs(3000);
			getMyScreen().mouseUp(Button.LEFT);
		} catch (FindFailed e) {
			System.err.println("No se encuentra barra de scroll");
		}
	}
	/**
	 * Hace una captura de pantalla en la carpeta log con el nombre de la accion seguido de --
	 *  y la fecha actual en el formato  yyyy__MM__dd HH_mm_ss.
	 * @param note : String 
	 * @throws IOException
	 * @throws HeadlessException
	 * @throws AWTException
	 */
	protected void screenShot(String note) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy__MM__dd HH_mm_ss");
		Date date = new Date();
		BufferedImage image;
		try {
			image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		ImageIO.write(image, "png", new File("logs/"+getSourceAction()+note+"--"+dateFormat.format(date)+".png"));
		} catch (HeadlessException e) {
			System.err.println("\n ï¿½Teclado,raton o monitor no soportado!:\n");
			e.printStackTrace();
		} catch (AWTException e) {
			System.err.println("\n ï¿½Fallo en windows Toolkit!\n");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("\n ï¿½Fallo de entrada/salida!\n");
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @return
	 */
	protected boolean CheckLoadBar() {
		int cont = 40;
		Pattern generalWait = new Pattern(getRepoPath()+"generalWait.PNG").similar(0.9f);
		Region tmpReg = new Region(1420,827,180,73);
		while(tmpReg.exists(generalWait) != null && cont > 0) {
			System.out.print(". ");
			waitInMilisecs(500);
			cont--;
		}
		return cont > 0;	
	}
	/**
	 * Busca sobre los patrones deseados para respetar los tiempos de carga
	 * @param label : etiqueta a mostrar por la carga
	 * @param references : patrones de imagen referencia a buscar </br>
	 * para el valor en el Map: <i>True si se ejecuta mientras no exista (== null) y False mientras que si (!= null)</i>
	 * @param timeMax : especifica el tiempo de espera para cada pattern * 2. Ej: 40 = 20 segundos
	 * @return false cuando ha tardado demasidado tiempo </br>true cuando no hay fallos
	 */
	protected boolean WaitFor(String label,Map<Pattern,Boolean> references,int timeMax) {
		int cont = 0,max = timeMax*references.size(); //40 segundos por cada imagen a buscar maximo
		System.out.println(label);
		for (Map.Entry<Pattern,Boolean> entry : references.entrySet())
		{
			System.out.println("\nchequeando pattern de "+entry.getKey().getFilename()+"\n");
			if(entry.getValue()) {
				System.out.print("Mientras no exista espero");
				while(this.myScreen.exists(entry.getKey()) == null && cont < max) {
					System.out.print(".");
					cont++;
					waitInMilisecs(500);
				}
			}else {
				System.out.print("Mientras exista espero");
				while(this.myScreen.exists(entry.getKey()) != null && cont < max) {
					System.out.print(".");
					cont++;
					waitInMilisecs(500);
				}				
			}
		}
		System.out.println();
		return cont < max;
	}
	
	/**
	 * Metodo de espera en milisegundos
	 * @param milisecs : cantidad entera en milisegundos 
	 */
	protected void waitInMilisecs(int milisecs) {
		try {
			TimeUnit.MILLISECONDS.sleep(milisecs);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*	-------------------------------------------------
	 * 					ZONA PUBLICA
	 	-------------------------------------------------*/
	
	/**
	 * Busca el icono de cierre y cierra la ventana n veces segï¿½n el nï¿½mero de iteraciones especificadas
	 * @param iterations
	 * @param timeBetween
	 * @throws FindFailed
	 */
	public void closeWindow(int iterations, int timeBetween) throws FindFailed {
		final Pattern closeWindow = new Pattern(getRepoPath()+"closeWindowButton.PNG").similar(0.95f);
		while(iterations>0) {
			waitInMilisecs(timeBetween);
			if(WaitFor("Cerrando la ventana numero "+iterations,
				new HashMap<Pattern, Boolean>() {/**
					 * busca si esta el boton de cerrar ventana
					 */
					private static final long serialVersionUID = 1L;

				{
					put(closeWindow,true);
				}},80)) {
				getMyScreen().click(closeWindow);
				iterations--;
			}else {
				System.err.println("no se encuentra el botï¿½n de cerrar ventana");
				break;
			}
				
		
		}
	}
	/**
	 * Contructor simple
	 * @param settings2 
	 * @param userAmdocs 
	 */
	public PrimalWindow(UserAmdocs userAmdocs, Settings settings) {
		this.myScreen = new Screen();
		this.repoPath = "src/main/resources/images/";
		this.nextWindowScript= new LinkedList<Pattern>();
		this.userLogged = userAmdocs;
		this.settings = settings;
	}
	/**
	 * constructor completo
	 * @param myScreen
	 * @param references
	 * @param data
	 * @param userLogged
	 * @param settings
	 * @param repoPath
	 * @param windowPath
	 * @param sourceAction
	 * @param nextWindowScript
	 */
	public PrimalWindow(Screen myScreen, List<Pattern> references, List<Object> data, UserAmdocs userLogged,
			Settings settings, String repoPath, String windowPath, String sourceAction, Queue<Pattern> nextWindowScript) {
		super();
		this.myScreen = myScreen;
		this.references = references;
		this.data = data;
		this.userLogged = userLogged;
		this.settings = settings;
		this.repoPath = repoPath;
		this.windowPath = windowPath;
		this.sourceAction = sourceAction;
		this.nextWindowScript = nextWindowScript;
	}
/*
 * ----------------------------------------------------------------------
 * 		Getters y Setters de la clase
 * ----------------------------------------------------------------------
 */
	


	public Screen getMyScreen() {
		return myScreen;
	}

	public void setMyScreen(Screen myScreen) {
		this.myScreen = myScreen;
	}

	public List<Pattern> getReferences() {
		return references;
	}

	public void setReferences(List<Pattern> references) {
		this.references = references;
	}

	public List<Object> getData() {
		return data;
	}

	public void setData(List<Object> data) {
		this.data = data;
	}

	public String getRepoPath() {
		return repoPath;
	}

	public void setRepoPath(String repoPath) {
		this.repoPath = repoPath;
	}

	public String getSourceAction() {
		return sourceAction;
	}

	public void setSourceAction(String sourceAction) {
		this.sourceAction = sourceAction;
	}

	public UserAmdocs getUserLogged() {
		return userLogged;
	}

	public void setUserLogged(UserAmdocs userLogged) {
		this.userLogged = userLogged;
	}
	public Pattern getGeneralWait() {
		return generalWait;
	}
	public Queue<Pattern> getNextWindowScript() {
		return nextWindowScript;
	}
	public void setNextWindowScript(Queue<Pattern> nextWindowScript) {
		this.nextWindowScript = nextWindowScript;
	}

	public Pattern getLastPatternConfirmed() {
		return lastPatternConfirmed;
	}

	public void setLastPatternConfirmed(Pattern lastPatternConfirmed) {
		this.lastPatternConfirmed = lastPatternConfirmed;
	}

	public String getWindowPath() {
		return windowPath;
	}

	public void setWindowPath(String windowPath) {
		this.windowPath = windowPath;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}
}
