package controller;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;

import model.UserAmdocs;
import tools.ExcelReader;
import tools.SubThread;
import tools.objects.MyRow;
import windows.SearchClientWindow;

public class ManagerScheduler {
	private enum Actions{
		GETUSER("inicializo usuario"),
		SEARCHCLIENT("SearchClient"),
		CLOSEWINDOW("cierra ventanas"),
		CENTRALPAGEINTERACTION("pagina central iteraccion(menu para gestionar un cliente)"),
		WORKORDERSONE("Ordenes de trabajo 1 de 2"),
		PAUSE("Paramos la lectura");
		
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
	private SubThread subThread;
	private List<MyRow> myRows;
	private UserAmdocs myUser;

	

	
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
		boolean correct = false;
		Queue<Queue<String>> q = current.getInput();
		int pos = 0,cont = 0;
		while(!q.isEmpty()){
			Queue<String> Original = new LinkedList<String>(q.poll());			
			Queue<String> tmp = new LinkedList<String>(Original);
			do {//mientras se descubra un error se sigue ejecutando hasta 3 veces
				try {
					if(!this.subThread.isAlive()) {
						this.subThread = new SubThread();
						this.subThread.start();//empieza el hilo de comprobación de popUps
					}else {
						if(this.subThread.isError())
							this.subThread.setError(false);
					}
					System.out.print("ventana de ");
					switch(Actions.valueOf(current.getAction(pos).toUpperCase())) {
						case GETUSER:
							System.out.println("Inicializacion de usuario");
							System.out.println("---------------------------------");
							setMyUser(tmp);
							correct = true;
							break;
						case SEARCHCLIENT:
							System.out.println("Busqueda de cliente");
							System.out.println("---------------------------------");	
							SearchClientWindow scw = new SearchClientWindow();
							correct = scw.start(tmp);
							break;
						case CENTRALPAGEINTERACTION:
							System.out.println("Pagina central de iteraccion");	
							System.out.println("---------------------------------");
							//correct = centralPageIteration(tmp);
							break;
						case WORKORDERSONE:
							System.out.println("Orden de trabajo 1 de 2");
							System.out.println("---------------------------------");
							//correct = workOrderOne(tmp);
							break;

						default: 
							System.out.println("Acción no contemplada");
							correct = true;	
					}
					
				    if (this.subThread.isError())
				        throw new Exception("se detectó un error interno durante la ejecucion");
				} catch (FindFailed e) {
					cont++;
					System.out.println("Fallo en la tarea "+current.getAction(pos)+"\tcontador="+cont);
				} catch (Exception e) {
					this.subThread.setError(false);
					System.err.println(e+" durante la tarea "+current.getAction(pos));
				}
			}while( (!correct) && cont < 3);

			if(cont >= 3)
				System.err.println("La instrucción '"+current.getAction(pos)+"' ha fallado demasiadas veces y queda descartada.");
			cont = 0;
			pos++;
			
			this.subThread.setEnd(true);//finalizamos el hilo que controla los errores
		}
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
		this.myRows = er.readBooksFromExcelFile("files/pruebas.xlsx");
		this.subThread = new SubThread();
 	}
 	public void ShowExcelContent() {
 		System.out.println(this.myRows);
 	}
 	public void start() throws FindFailed, InterruptedException, HeadlessException, IOException, AWTException {
 		ListIterator<MyRow> litr = myRows.listIterator();
 		this.s.click("src/main/resources/images/logoHoverVodafone.PNG");
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
	public void setMyUser(Queue<String> tmp) {
		String name = tmp.poll();
		String rol = tmp.poll();
		UserAmdocs ua = new UserAmdocs(name,rol);
		this.myUser = ua;
		System.out.println("Login con "+name+" tuyo rol es "+rol);
	}
}
