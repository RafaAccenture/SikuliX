package windows;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import model.UserAmdocs;

public class CentralPageInteractionWindow extends PrimalWindow{
	
	/*	-------------------------------------------------
	 * 					ZONA PRIVADA
	 	-------------------------------------------------*/
	private enum CentralPageInteractionActions{
		ACCEDERORDENESTRABAJO("acceder a la ventana de ordenes de trabajo");
		
		private final String text;
	
		 /**
		  * @param text
		  */
		private CentralPageInteractionActions(final String text) {
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
	
	private boolean accederOrdenesTrabajo(Queue<String> tmp) throws  Exception {
		boolean exit = false;
		Location loc;
		try {
			final Pattern menuCheckLoad = new Pattern(getRepoPath()+"menuCheckLoad.PNG").similar(0.95f);
			final Pattern menuCheckLoad2 = new Pattern(getRepoPath()+"menuCheckLoad2.PNG").similar(0.95f);
			
			//emula barra de carga de la ventana en sí
			if(WaitFor("cargando ventana de la pagina principal de interaccion",
				new HashMap<Pattern, Boolean>() {/**
					 * busca si esta el boton de cerrar ventana
					 */
					private static final long serialVersionUID = 1L;

				{
					put(menuCheckLoad,true);
					put(menuCheckLoad2,true);
					put(getGeneralWait(),true);
				}},30)) {
				
				screenShot("__INFO");
				getMyScreen().click(getRepoPath()+"gestionComercial.PNG");
				waitInMilisecs(9000);
				loc = getMyScreen().find(getRepoPath()+"gestionComercial_ordenTrabajo.PNG").getTarget();
				loc .click();
				getNextWindowScript().add(loc);//guardamos en el script de inicio de ventana
				final Pattern SelectAnOption = new Pattern("src/main/resources/images/PopUps/SelectAnOption.PNG").similar(0.95f);
				//Emula la barra de carga para chequear si se pide confirmacion 
				if(WaitFor("Esperamos por si pide confirmacion de seleccionar una opcion",
						new HashMap<Pattern, Boolean>() {/**
							 * busca si esta el boton de cerrar ventana
							 */
							private static final long serialVersionUID = 1L;

						{
							put(SelectAnOption,true);
							put(getGeneralWait(),true);
						}},3)) {
					loc = getMyScreen().find("src/main/resources/images/PopUps/SelectAnOption_yes.PNG").getTarget();
					getNextWindowScript().add(loc);//guardamos en el script de inicio de ventana
					}else
						System.out.println("Salida sin confirmacion");
				exit = true;
			}else {
				exit = false;
				System.out.println("timeout para la carga de la ventana");//se lanza excepcion por timeout de la espera
			}
		} catch (Exception e) {
			exit = false;
		}
		if (!exit)
			this.closeWindow(1, 0);
		return exit;
	}
	/*	-------------------------------------------------
	 * 					ZONA PUBLICA
	 	-------------------------------------------------*/
	public CentralPageInteractionWindow() {
		super();
		setRepoPath(getRepoPath()+"windows/interactionCentralPage/");
	}
	public CentralPageInteractionWindow(Screen myScreen, List<Pattern> references, List<Object> data, UserAmdocs userLogged,
			String MyrepoPath, String sourceAction,Queue<Location> nextWindowScript) {
		super(myScreen, references, data, userLogged, sourceAction, nextWindowScript);
		setRepoPath(getRepoPath()+"windows/interactionCentralPage/");
	}
	/**
	 * Dada una accion selecciona la ejecucion apropiada
	 * @param tmp
	 * @return
	 * @throws Exception 
	 */
	public boolean start(Queue<String> tmp) throws Exception {
		boolean correct = false;
		String ta = tmp.poll();//tipo de acción	
		System.out.print("accion de ");
		switch(CentralPageInteractionActions.valueOf(ta.toUpperCase())) {
			case ACCEDERORDENESTRABAJO:
				setSourceAction("ACCEDERORDENESTRABAJO");
				System.out.println("\tacceder a las ordenes de trabajo");
				System.out.println("\t---------------------------------");	
				correct = accederOrdenesTrabajo(tmp);
				break;
			default: 
				System.out.println("Acción no contemplada");
				correct = true;	
		}
		return correct;
	}
	
}
