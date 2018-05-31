package controller;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import model.Settings;
import model.UserAmdocs;
import tools.Consumer;
import tools.ExcelReader;
import tools.Producer;
import tools.objects.MyRow;
import windows.CentralPageInteractionWindow;
import windows.CreateContractWindow;
import windows.PrimalWindow;
import windows.SearchClientWindow;
import windows.WorkOrdenOneWindow;

public class ManagerScheduler {
	private enum Actions{
		GETUSER("inicializar un usuario"),
		GETSETTINGS("inicializar la configuración"),
		TESTCASENAME("indicar el nombre de la prueba para almacenar las evidencias obtenidas"),
		SEARCHCLIENT("Busqueda de cliente"),
		CLOSEWINDOW("cerrar ventanas"),
		CENTRALPAGEINTERACTION("pagina central iteraccion(menu para gestionar un cliente)"),
		CATALOG("Ordenes de trabajo 1 de 2"),
		CREATECONTRACT("Core de amdocs para crear un contrato y/o gestionarlo"),
		PAUSE("Parar la lectura");
		
		private final String text;
	
		 /**
		  * @param text
		  */
		private Actions(final String text) {
		    this.text = text;
		}
	
		 /* (non-Javadoc)
		  * @see java.lang.Enum#toString()
		  */
		@Override
		public String toString() {
		    return text;
		}
	};
	private String currectAction;
	private Screen s;
	private List<MyRow> myRows;
	private UserAmdocs myUser;//información del usuario logueado
	private Settings conf; //clase para configuración de la aplicación
	private Queue<Pattern> openWindowScript;
	
    static private Semaphore semaphore = new Semaphore(0);
    static private Semaphore mutex = new Semaphore(1);
    private Producer ThreadProducer;
	
    /**
     * Guarda el fragmento del script que abre la siguiente ventana
     * @throws InterruptedException
     * @throws FindFailed
     */
    private void runWindowScript() throws InterruptedException, FindFailed {
    	Queue<Pattern> tmp = new LinkedList<Pattern>(openWindowScript);
    	for(Pattern pat : tmp) {
    		System.out.println("mejor porcentaje de similitud : "+this.s.findBest(pat).getScore()*100+"%");
    		this.s.findBest(pat).click();
    		TimeUnit.MILLISECONDS.sleep(1500);
    	}
    }
 	
	/**
	 * Para la ejecución hasta recibir una entrada de teclado
	 */
	@SuppressWarnings("resource")
	private void pause() {
		 System.out.println("Pulsa cualquier tecla para continuar...");
         new java.util.Scanner(System.in).nextLine();
	}
	/**
	 * Manejador de opciones usando un switch
	 * @param current
	 * @throws FindFailed
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws HeadlessException
	 * @throws AWTException
	 */
	private void throwRow(MyRow current) throws InterruptedException, IOException, HeadlessException, AWTException {
		TimeUnit.MILLISECONDS.sleep(1000);
		
		Queue<Queue<String>> q = current.getInput();
		int pos = 0, numErr = 0;
		//tratamiento de errores
		this.ThreadProducer = new Producer(semaphore, mutex);
		this.ThreadProducer.start();
		Consumer consumer = new Consumer(semaphore, mutex);
		consumer.start();
		//----------------------
		while(!q.isEmpty()){
			boolean correct = true;
			Queue<String> Original = new LinkedList<String>(q.poll());

			String action = current.getAction(pos).toUpperCase();
			do {//mientras se descubra un error se sigue ejecutando hasta 3 veces
				try {
					if(!correct) {
						System.out.println("Repetimos la interacción con la ventana");
						runWindowScript();
					}
					Queue<String> tmp = new LinkedList<String>(Original);
					System.out.print("Accion de " +Actions.valueOf(action).toString());
					System.out.println("\n---------------------------------");
					switch(Actions.valueOf(action)) {
						case GETUSER:							
							correct = setMyUser(tmp);
							break;
						case GETSETTINGS:							
							correct = setConf(tmp);
							break;
						case TESTCASENAME:
							getConf().setCurrentTestCaseName(tmp.poll());
							PrimalWindow pw_testcasename = new PrimalWindow(getMyUser(),getConf());
							pw_testcasename.createTestFolder();
							break;
						case SEARCHCLIENT:	
							SearchClientWindow scw = new SearchClientWindow(getMyUser(),getConf());
							correct = scw.start(tmp);
							if(correct) setOpenWindowScript(scw.getNextWindowScript());
							runWindowScript();
							break;
						case CENTRALPAGEINTERACTION:
							CentralPageInteractionWindow cpw = new CentralPageInteractionWindow(getMyUser(),getConf());
							correct = cpw.start(tmp);
							if(correct) {
								setOpenWindowScript(cpw.getNextWindowScript());
								runWindowScript();
								if(cpw.waitExitConfirmation())
									setOpenWindowScript(cpw.getNextWindowScript());
							}else runWindowScript();
							break;
						case CATALOG:
							WorkOrdenOneWindow wow = new WorkOrdenOneWindow(getMyUser(),getConf());
							correct = wow.start(tmp);
							if(correct) setOpenWindowScript(wow.getNextWindowScript());
							runWindowScript();
							break;
						case CREATECONTRACT:
							CreateContractWindow cnt = new CreateContractWindow(getMyUser(),getConf());
							correct = cnt.start(tmp);
							if(correct) setOpenWindowScript(cnt.getNextWindowScript());
							runWindowScript();
							break;
						case CLOSEWINDOW:
							PrimalWindow pw_closewindow = new PrimalWindow(getMyUser(),getConf());

							int iterations = Integer.parseInt(tmp.poll());
							int timeBetween = Integer.parseInt(tmp.poll());
							pw_closewindow.closeWindow(iterations, timeBetween);
							break;
						case PAUSE:
							pause();
							correct = true;
							break;
						default: 
							System.err.println("Acción no contemplada");
							correct = true;	
					}
					
				} catch (FindFailed e) {
					numErr++;
					correct = false;
					System.err.println("Fallo de busqeda de recurso en "+current.getAction(pos)+"\tErrores registrados: "+numErr);
					System.err.println(e);
				} catch (Exception e) {
					numErr++;
					correct = false;
					System.err.println("Fallo inesperado en la tarea "+current.getAction(pos)+"\tErrores registrados: "+numErr);
					System.err.println(e);
				}
			}while( (!correct) && numErr < 3);
			if(numErr >= 3)
				System.err.println("La instrucción '"+current.getAction(pos)+"' ha fallado demasiadas veces y queda descartada.");
			numErr = 0;
			pos++;
			
		}
		ThreadProducer.setEnd(true);//terminamos el producer
		consumer.setOnOff(false);
		System.out.println("El caso de uso ha reportado "+ThreadProducer.getCounter()+" errores");
	}
	
	/****************************************************************
	 * 						Parte pública
	 ****************************************************************/
 	/**
 	 * inicializa sólo la pantalla
 	 */	
 	public ManagerScheduler() throws IOException {
 		//Inicializamos la vista
 		this.s = new Screen();
 		ExcelReader er = new ExcelReader();
 		//volcamos el excel
		this.myRows = er.readBooksFromExcelFile("files/devmode.xlsx");//pruebas.xlsx
		this.openWindowScript = new LinkedList<Pattern>();
 	}

	public void ShowExcelContent() {
 		System.out.println(this.myRows);
 	}
 	public void start() throws FindFailed, InterruptedException, HeadlessException, IOException, AWTException {
 		ListIterator<MyRow> litr = myRows.listIterator();
	
 		while(litr.hasNext()){
 			System.out.println("---------------------------\nNueva fila\n---------------------------");
 			throwRow(litr.next());
 			TimeUnit.MILLISECONDS.sleep(500);//damos un margen de tiempo a la herramienta
 		}
 	}
	public String getCurrectAction() {
		return currectAction;
	}

	public void setCurrectAction(String currectAction) {
		this.currectAction = currectAction;

	}
	public UserAmdocs getMyUser() {
		return myUser;
	}
	
	public boolean setMyUser(Queue<String> tmp) {
		String name = tmp.poll();
		String rol = tmp.poll();
		UserAmdocs ua = new UserAmdocs(name,rol);
		this.myUser = ua;
		System.out.println("Login de "+name+" con rol '"+rol+"'");
		return ( this.myUser != null );
	}
	public Queue<Pattern> getOpenWindowScript() {
		return openWindowScript;
	}
	public void setOpenWindowScript(Queue<Pattern> queue) {
		this.openWindowScript = queue;
	}
	public Settings getConf() {
		return conf;
	}
	public boolean setConf(Queue<String> tmp) {
		Settings sts = new Settings();
		sts.setEnvironment(tmp.poll());
		sts.setMAX_NUMBER_ERRORS(Integer.parseInt(tmp.poll()));
		sts.setMAX_TIMEWAIT(Integer.parseInt(tmp.poll()));
		sts.setScaledImgRatio(Float.valueOf(tmp.poll()));
		this.conf = sts;
		return this.conf != null;
	}
}
