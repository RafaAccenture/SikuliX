package windows;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;

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
			if (CheckLoadBar()) {
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
				/* para el menu de confirmacion
				if(getMyScreen().exists(getRepoPath()+"")!= null) {
					setLastPatternConfirmed(
							new Pattern(getRepoPath()+"").similar(0.95f)
							);
					getNextWindowScript().add(getLastPatternConfirmed());
				}
				*/
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
	public Pattern waitExitConfirmation() {
		Pattern pat = null;
		final Pattern SelectAnOption = new Pattern(getRepoPath()+"PopUps/SelectAnOption.PNG")
				.similar(0.95f);
		// Emula la barra de carga para chequear si se pide confirmacion
		if (WaitFor("Esperamos por si pide confirmacion de seleccionar una opcion", new HashMap<Pattern, Boolean>() {
			/**
			 * busca si esta el boton de cerrar ventana
			 */
			private static final long serialVersionUID = 1L;
			{
				put(SelectAnOption, true);
			}
		}, 10)) {
			pat = new Pattern(getRepoPath()+"PopUps/SelectAnOption_yes.PNG");
			setLastPatternConfirmed(pat);
			getMyScreen().findBest(getLastPatternConfirmed()).click();
		} else
			System.out.println("Salida sin confirmacion");
		return pat;

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
		System.out.print("accion de ");
		switch (CentralPageInteractionActions.valueOf(ta.toUpperCase())) {
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
