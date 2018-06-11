package windows;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import model.Settings;
import model.UserAmdocs;

public class CentralPageInteractionWindow extends PrimalWindow {

	/*
	 * ZONA PRIVADA
	 * -------------------------------------------------
	 */
	private enum CentralPageInteractionActions {
		ACCEDERORDENESTRABAJO("acceder a la ventana de ordenes de trabajo");

		private final String text;

		/**
		 * @param text
		 */
		private CentralPageInteractionActions(final String text) {
			this.text = text;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return text;
		}
	};

	private boolean accederOrdenesTrabajo(Queue<String> tmp) throws Exception {
		boolean exit = false;
		try {

			// emula barra de carga de la ventana en sí
			if(
					CheckLoadBar() && 
					WaitFor("Esperando a que se cargue el título de la ventana de interacción",
					new HashMap<Pattern, Boolean>() {/**
					* busca si esta el boton de cerrar ventana
					*/
					private static final long serialVersionUID = 1L;
					{
						put(new Pattern(reescaledImage("windowTittle.PNG")).similar(0.9f),true);
					}},15) 
			){
				mover_ventana("ABAJO");
				setLastPatternConfirmed(
						new Pattern(reescaledImage("gestionComercial.PNG")).similar(0.65f)
						);
				
				waitInMilisecs(2000);
				
				getNextWindowScript().add(getLastPatternConfirmed());
				setLastPatternConfirmed(
						new Pattern(reescaledImage("gestionComercial_ordenTrabajo.PNG")).similar(0.65f)
						);
				getNextWindowScript().add(getLastPatternConfirmed());
				
				waitInMilisecs(2000);
				screenShot("SIN ERRORES");
				exit = true;
			} else {
				exit = false;
				System.err.println("timeout para la carga de la ventana");// se lanza excepcion por timeout de la espera
			}
		} catch (Exception e) {
			exit = false;
			this.closeWindow(1, 0);
		}

		return exit;
	}

	/*
	 * ZONA PUBLICA
	 * -------------------------------------------------
	 */
	public boolean waitExitConfirmation() throws FindFailed {
		boolean exit = false;
		if(getMyScreen().exists(reescaledImage(getRepoPath()+"PopUps/", "SelectAnOption.PNG"))!= null) {
			setLastPatternConfirmed(
					new Pattern(reescaledImage(getRepoPath()+"PopUps/", "SelectAnOption_yes.PNG")).similar(0.85f)
					);
			getNextWindowScript().add(getLastPatternConfirmed());
			exit = true;
			getMyScreen().click(reescaledImage(getRepoPath()+"PopUps/", "SelectAnOption_yes.PNG"));
		}
		return exit;
	}

	public CentralPageInteractionWindow(UserAmdocs userAmdocs, Settings settings) {
		super(userAmdocs,settings);
		setWindowPath(getRepoPath() + "windows/interactionCentralPage/");
	}

	public CentralPageInteractionWindow(Screen myScreen, List<Pattern> references, List<Object> data,
			UserAmdocs userLogged, String MyrepoPath, String sourceAction, Queue<Pattern> nextWindowScript,Settings settings) {
		super(myScreen, references, data, userLogged, settings, sourceAction, sourceAction, sourceAction, nextWindowScript);
		setWindowPath(getRepoPath() + "windows/interactionCentralPage/");
	}

	/**
	 * Dada una accion selecciona la ejecucion apropiada
	 * 
	 * @param tmp
	 * @return
	 * @throws Exception
	 */
	public boolean start(Queue<String> tmp) throws Exception {
		boolean correct = false;
		String ta = tmp.poll();// tipo de acción
		System.out.print("Descripción de la acción: ");
		String action = ta.toUpperCase();
		System.out.println(CentralPageInteractionActions.valueOf(action).toString());
		System.out.println("---------------------------------");
		setSourceAction(action);
		switch (CentralPageInteractionActions.valueOf(action)) {
		case ACCEDERORDENESTRABAJO:
			setSourceAction("ACCEDERORDENESTRABAJO");
			System.out.println("acceder a las ordenes de trabajo");
			System.out.println("---------------------------------");
			correct = accederOrdenesTrabajo(tmp);
			break;
		default:
			System.out.println("Acción no contemplada");
			correct = true;
		}
		return correct;
	}

}
