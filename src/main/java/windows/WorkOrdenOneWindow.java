package windows;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import model.UserAmdocs;

public class WorkOrdenOneWindow extends PrimalWindow{
	
	/*	-------------------------------------------------
	 * 					ZONA PRIVADA
	 	-------------------------------------------------*/
	private enum CentralPageInteractionActions{
		ALTAMOVIL("dar de alta un movil para el cliente");
		
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
	
	private boolean altaMovil(Queue<String> tmp) throws Exception {
		boolean exit = false;
		final Pattern checkLoad1 = new Pattern(getRepoPath()+"checkLoad1.png").similar(0.95f);
		final Pattern checkLoad2 = new Pattern(getRepoPath()+"checkLoad2.png").similar(0.95f);
		System.out.print("Cargando las ordenes de trabajo 1 de 2");
		//emula barra de carga de la ventana en sí
		if(WaitFor("cargando ventana de la pagina principal de interaccion",
			new HashMap<Pattern, Boolean>() {/**
				 * Comprueba si ha cargado ya toda la interfaz de la ventana
				 */
				private static final long serialVersionUID = 1L;

			{
				put(checkLoad1,true);
				put(checkLoad2,true);
				put(getGeneralWait(),true);
			}},40)) {
			getMyScreen().find(getRepoPath()+"addHalfButton.png").click();
			screenShot("__INFO");
			exit = true;
		}else {
			throw new Exception("timeout para cargando ventana de la pagina Orden de trabajo 1 de 2");//se lanza excepcion por timeout de la espera
		}
		

		
		

		return exit;
	}
	/*	-------------------------------------------------
	 * 					ZONA PUBLICA
	 	-------------------------------------------------*/
	public WorkOrdenOneWindow() {
		super();
		setRepoPath(getRepoPath()+"windows/workOrdersOne/");
	}
	public WorkOrdenOneWindow(Screen myScreen, List<Pattern> references, List<Object> data, UserAmdocs userLogged,
			String MyrepoPath, String sourceAction) {
		super(myScreen, references, data, userLogged, sourceAction);
		setRepoPath(getRepoPath()+"windows/workOrdersOne/");
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
			case ALTAMOVIL:
				setSourceAction("ALTAMOVIL");
				System.out.println("\tacceder a las ordenes de trabajo");
				System.out.println("\t---------------------------------");	
				correct = altaMovil(tmp);
				break;
			default: 
				System.out.println("Acción no contemplada");
				correct = true;	
		}
		return correct;
	}
	
}
