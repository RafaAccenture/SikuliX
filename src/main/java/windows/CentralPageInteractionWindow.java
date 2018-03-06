package windows;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.sikuli.script.FindFailed;
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
				
			}else {
				exit = false;
				throw new Exception("timeout para cargando ventana de la pagina");//se lanza excepcion por timeout de la espera
			}

			screenShot("__INFO");
			
			getMyScreen().click(getRepoPath()+"gestionComercial.PNG");
			TimeUnit.MILLISECONDS.sleep(10000);
			getMyScreen().click(getRepoPath()+"gestionComercial_ordenTrabajo.PNG");
			exit = true;
		} catch (FindFailed e) {
			exit = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			String MyrepoPath, String sourceAction) {
		super(myScreen, references, data, userLogged, sourceAction);
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
