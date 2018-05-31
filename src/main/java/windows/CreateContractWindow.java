package windows;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import model.Settings;
import model.UserAmdocs;

public class CreateContractWindow extends PrimalWindow{
	
	/*	-------------------------------------------------
	 * 					ZONA PRIVADA
	 	-------------------------------------------------*/
	private enum ContractActions{
		VALIDARSIMPRESENCIAL("Dado un usuario de tipo presencial se valida la SIM  se lanza la orden"),
		VALIDARSIMCONTERMINAL("");
		
		private final String text;
	
		 /**
		  * @param text
		  */
		private ContractActions(final String text) {
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
	/**
	 * validar numero IMEI
	 * validar numero SIM
	 * pulsar boton acceso tienda
	 * esperar a que se abra la tienda
	 * seleccionar metodo de pago, en este caso al contado
	 * confirmamos la compra
	 * vemos que se ha realizado bien y cerramos la ventana
	 * pulsamos el boton de scoring cerrando los popups habituales
	 * pulsamos el boton de continuar finalizar + sus popups
	 * introducimos la firma manual
	 * volvemos a pulsar el boton de continuar finalizar y sus popups
	 * @param tmp
	 * @return
	 */
	private boolean validarSIMConTerminal(Queue<String> tmp) {
		boolean exit = false;
		return exit;
	}
	/**
	 * 
	 * @param tmp
	 * @return
	 */
	private boolean validarSIMPresencial(Queue<String> tmp) {
		boolean exit = false;
		String SIM = tmp.poll();
		SIM = SIM.split("\\$")[1];
		if(
				CheckLoadBar() && 
				WaitFor("Esperando a que se cargue el título de la ventana de creación de contrato",
				new HashMap<Pattern, Boolean>() {/**
				* busca si esta el boton de cerrar ventana
				*/
				private static final long serialVersionUID = 1L;
				{
					put(new Pattern(reescaledImage("windowTittle.PNG")).similar(0.9f),true);
				}},15) 
		) {
			waitInSecs(15);
			try {
				// Busca la fila para añadir SIM y deja vacía primero la casilla
				Location loc = getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "add_sim_row.PNG") ).getTarget();
				getMyScreen().doubleClick(loc);
				for(int i = 0;i<10;i++) {
					getMyScreen().type(Key.BACKSPACE);
					waitInMilisecs(300);
				}
				getMyScreen().paste(SIM);
				// Mueve a la derecha la localización para hacer click en agregar SIM
				loc.x+=100;
				waitInSecs(3);
				getMyScreen().click(loc);
				// Espera popUps y va al botón de scoring
				waitAndCloseMessagePopUp(2);
				mover_ventana("ABAJO");
				getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "scoring_button.PNG") ).click();
				// Espera popUps , da un tiempo de carga y va al botón de continuar_finalizar
				waitAndCloseMessagePopUp(1);
				waitInSecs(12);
				getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "continuar_finalizar_button.PNG") ).click();
				// Espera popUps, espera la carga de los contratos en formato PDF y va al botón de continuar_finalizar
				waitAndCloseMessagePopUp(2);
				loc = getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "signature_menu.PNG") ).getTarget();
				getMyScreen().click(loc);
				selectFromMenuInputDown(1);
				loc.x+=70;
				waitInSecs(5);
				getMyScreen().click(loc);
				waitAndClosePDF(1);
				getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "continuar_finalizar_button.PNG") ).click();
				waitAndCloseMessagePopUp(2);				
				exit = true;
			} catch (FindFailed e) {
				System.out.println("no se ha encontrado una imagen en creación de contrato");
				e.printStackTrace();
			}
		}
		return exit;
	}
	/*	-------------------------------------------------
	 * 					ZONA PUBLICA
	 	-------------------------------------------------*/
	public CreateContractWindow(UserAmdocs userAmdocs, Settings settings) {
		super(userAmdocs,settings);
		setWindowPath(getRepoPath()+"windows/createContract/");
	}
	public CreateContractWindow(Screen myScreen, List<Pattern> references, List<Object> data, UserAmdocs userLogged,
			String MyrepoPath, String sourceAction,Queue<Pattern> nextWindowScript,Settings settings) {
		super(myScreen, references, data, userLogged, settings, sourceAction, sourceAction, sourceAction, nextWindowScript);
		setWindowPath(getRepoPath()+"windows/createContract/");
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
		System.out.println("Descripción de la acción:");
		switch(ContractActions.valueOf(ta.toUpperCase())) {
			case VALIDARSIMPRESENCIAL:
				setSourceAction("VALIDARSIMPRESENCIAL");
				System.out.println("se introduce una SIM y se valida");
				System.out.println("---------------------------------");	
				correct = validarSIMPresencial(tmp);
				break;
			case VALIDARSIMCONTERMINAL:
				setSourceAction("VALIDARSIMCONTERMINAL");
				System.out.println("se compra una terminal  y se validan los datos");
				System.out.println("---------------------------------");	
				correct = validarSIMConTerminal(tmp);
				break;
			default: 
				System.out.println("Acción no contemplada");
				correct = true;	
		}
		return correct;
	}

	
}
