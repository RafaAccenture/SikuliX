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
	/********************************************************************************************************
	 *  			FUNCIONES DE APOYO
	 ********************************************************************************************************/
	/**
	 * se lleva a cabo la lógica de firma
	 */
	private void signatureAcion() {
		Location loc;
		try {
			loc = getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "signature_menu.PNG") ).getTarget();
			getMyScreen().click(loc);
			selectFromMenuInputDown(1);
			loc.x+=70;
			waitInSecs(5);
			getMyScreen().click(loc);
		} catch (FindFailed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * se pulsa el scoring, cerrando popUps y esperando su carga
	 */
	private void scoringAction() {
		try {
			getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "scoring_button.PNG") ).click();
			waitAndCloseMessagePopUp(1);
			waitInSecs(12);
		} catch (FindFailed e) {
			e.printStackTrace();
		}
	}
	/**
	 * se pulsa el boton de continuar finalizar y se esperan los popups
	 */
	private void continuar_finalizarAction() {
		try {
			getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "continuar_finalizar_button.PNG") ).click();
			waitAndCloseMessagePopUp(2);
			waitInSecs(2);
		} catch (FindFailed e) {
			e.printStackTrace();
		}		
	}
	/**
	 * Función que abarca toda la lógica que se ejecute en la tienda
	 * @return
	 */
	private boolean StoreProcess() {
		boolean exit = false;
		try {
		if(WaitFor("Esperando a que se carge la tienda",
				new HashMap<Pattern, Boolean>() {
			private static final long serialVersionUID = 1L;
			{
				put(new Pattern(reescaledImage(getWindowPath()+"store/","tittle_window_store.PNG")).similar(0.9f),true);
				put(new Pattern(reescaledImage(getWindowPath()+"store/","reference_of_load_phase1.PNG")).similar(0.9f),true);
				
			}},15)) {
			
				getMyScreen().find( reescaledImage(getWindowPath()+"store/", "pay_method_cash.PNG") ).click();
			
			//barra de carga para esperar a los detalles de compra
			if(WaitFor("Esperando a que se carguen los detalles de la compra",
					new HashMap<Pattern, Boolean>() {
				private static final long serialVersionUID = 1L;
				{
					put(new Pattern(reescaledImage(getWindowPath()+"store/","reference_of_load_phase2.PNG")).similar(0.9f),true);
					
				}},15)) {
					getMyScreen().find( reescaledImage(getWindowPath()+"store/", "confirmar_compra_button.PNG") ).click();
					//barra de carga para esperar a la confirmación de la compra
					if(WaitFor("Esperando a que se carguen los detalles de la compra",
							new HashMap<Pattern, Boolean>() {
						private static final long serialVersionUID = 1L;
						{
							put(new Pattern(reescaledImage(getWindowPath()+"store/","reference_of_load_phase3.PNG")).similar(0.9f),true);
							
						}},15)) {
						getMyScreen().find( reescaledImage(getWindowPath()+"store/", "close_store_button.PNG") ).click();
						
						exit = true;
					}
				}
			}
		} catch (FindFailed e) {
			e.printStackTrace();
		}
		return exit;
	}
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
			// Mueve a la derecha la localización para hacer click en agregar IMEI y cierra los PopUps generados
			loc.x+=100;
			waitInSecs(3);
			getMyScreen().click(loc);
			waitAndCloseMessagePopUp(2);
			
			// Busca la fila para añadir SIM dejando vacía primero la casilla
		    loc = getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "add_sim_row.PNG") ).getTarget();
			getMyScreen().doubleClick(loc);
			for(int i = 0;i<10;i++) {
				getMyScreen().type(Key.BACKSPACE);
				waitInMilisecs(300);
			}
			getMyScreen().paste(SIM);
			// Mueve a la derecha la localización para hacer click en agregar SIM y cierra los PopUps generados
			loc.x+=100;
			waitInSecs(3);
			getMyScreen().click(loc);
			waitAndCloseMessagePopUp(2);
			mover_ventana("ABAJO");
			getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "acceso_tienda_button.PNG") ).click();
	
			if(StoreProcess()) {
				scoringAction();
				continuar_finalizarAction();
				CheckLoadBar();
				waitInSecs(5);
				signatureAcion();
				waitAndClosePDF(1);
				//se finaliza la orden
				continuar_finalizarAction();			
				exit = true;
				CheckLoadBar();//para estabilizar el programa antes de cerrar las ventanas
			}		
		} catch (FindFailed e) {
			
			System.out.println("no se ha encontrado una imagen en creación de contrato");
			e.printStackTrace();
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
			scoringAction();
			continuar_finalizarAction();
			CheckLoadBar();
			waitInSecs(5);
			signatureAcion();
			waitAndClosePDF(1);
			continuar_finalizarAction();				
			exit = true;
		} catch (FindFailed e) {
			System.out.println("no se ha encontrado una imagen en creación de contrato");
			e.printStackTrace();
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
		// se espera a la cargad e la ventana
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
		}
		return correct;
	}

	
}
