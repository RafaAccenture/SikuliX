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
		VALIDARSIMONLY("Dado un usuario de tipo presencial se valida la SIM y se lanza la orden"),
		VALIDARSIMCONTERMINAL("Dado un usuario de tipo presencial se valida el IMEI y la SIM, se compra un terminal en tienda y se valida la orden");
		
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
	 * Función script para tramitar la orden de <i>SIM con terminal</i> en la ventana de creación de contratos. <br>
	 * validar numero IMEI<br>
	 * validar numero SIM<br>
	 * pulsar boton acceso tienda<br>
	 * esperar a que se abra la tienda<br>
	 * seleccionar metodo de pago, en este caso al contado<br>
	 * confirmamos la compra<br>
	 * vemos que se ha realizado bien y cerramos la ventana<br>
	 * pulsamos el boton de scoring cerrando los popups habituales<br>
	 * pulsamos el boton de continuar finalizar + sus popups<br>
	 * introducimos la firma manual<br>
	 * volvemos a pulsar el boton de continuar finalizar y sus popups<br>
	 * @param tmp
	 * @return
	 */
	private boolean validarSIMConTerminal(Queue<String> tmp) {
		boolean exit = false;
		String IMEI = tmp.poll();
		IMEI = IMEI.split("\\$")[1];
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
				// Busca la fila para añadir IMEI dejando vacía primero la casilla
				Location loc = getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "add_imei_row.PNG") ).getTarget();
				getMyScreen().doubleClick(loc);
				for(int i = 0;i<10;i++) {
					getMyScreen().type(Key.BACKSPACE);
					waitInMilisecs(300);
				}
				getMyScreen().paste(IMEI);
				// Busca la fila para añadir SIM dejando vacía primero la casilla
			    loc = getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "add_sim_row.PNG") ).getTarget();
				getMyScreen().doubleClick(loc);
				for(int i = 0;i<10;i++) {
					getMyScreen().type(Key.BACKSPACE);
					waitInMilisecs(300);
				}
				getMyScreen().paste(SIM);
				exit = true;
			} catch (FindFailed e) {
				
				System.out.println("no se ha encontrado una imagen en creación de contrato");
				e.printStackTrace();
			}
		}
		return exit;
	}
	/**
	 * Función script para tramitar la orden de <i>SIM only</i> en la ventana de creación de contratos.
	 * @param tmp
	 * @return
	 */
	private boolean validarSIMOnly(Queue<String> tmp) {
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
			case VALIDARSIMONLY:
				setSourceAction("VALIDARSIMPRESENCIAL");
				System.out.println("se introduce una SIM y se valida");
				System.out.println("---------------------------------");	
				correct = validarSIMOnly(tmp);
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
