package windows;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.Location;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
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
	
	private boolean altaMovil(Queue<String> tmp) throws FindFailed {
		boolean exit = false;
		try {
			//emula barra de carga de la ventana en sí
			if(CheckLoadBar()) {
				Region menu = new Region(326,224,522,790);
				menu.click(getWindowPath()+"Services_MovilOption.PNG");
				waitInMilisecs(500);
				menu.click(getWindowPath()+"addButton.PNG");
				if(CheckLoadBar()) {
					Location formsLocation;
					Region forms = new Region(769,228,1151,791);
					//input de elección de línea de teléfono
					formsLocation = forms.find(getWindowPath()+"movil/PhoneLineMenu.PNG").getTarget();
					formsLocation.x+=20;
					forms.click(formsLocation);
					waitInMilisecs(1500);
					selectFromMenuInput(1);
					waitInMilisecs(1000);
					//input de elección de tarifa de llamadas	 
					formsLocation = forms.find(getWindowPath()+"movil/callCostMenu.PNG").getTarget();
					formsLocation.x+=20;
					forms.click(formsLocation);
					selectFromMenuInput(9);
					waitInMilisecs(1000);
					//input de elección de SIM
					formsLocation = forms.find(getWindowPath()+"movil/simMenu.PNG").getTarget();
					formsLocation.x+=20;
					forms.click(formsLocation);
					selectFromMenuInput(2);
					waitInMilisecs(1000);
					
					mover_ventana("ABAJO");
					forms.click(getWindowPath()+"movil/AcceptButton.PNG");
				}
				exit = true;
			}else {
					throw new Exception("timeout para cargando ventana de la pagina Orden de trabajo 1 de 2");
			}
		} catch (Exception e) {
			exit = false;
		}

		return exit;
	}
	/*	-------------------------------------------------
	 * 					ZONA PUBLICA
	 	-------------------------------------------------*/
	public WorkOrdenOneWindow() {
		super();
		setWindowPath(getRepoPath()+"windows/workOrdersOne/");
	}
	public WorkOrdenOneWindow(Screen myScreen, List<Pattern> references, List<Object> data, UserAmdocs userLogged,
			String MyrepoPath, String sourceAction,Queue<Pattern> nextWindowScript) {
		super(myScreen, references, data, userLogged, sourceAction, nextWindowScript);
		setWindowPath(getRepoPath()+"windows/workOrdersOne/");
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
				System.out.println("acceder a las ordenes de trabajo");
				System.out.println("---------------------------------");	
				correct = altaMovil(tmp);
				break;
			default: 
				System.out.println("Acción no contemplada");
				correct = true;	
		}
		return correct;
	}
	
}
