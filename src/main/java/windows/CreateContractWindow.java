package windows;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
		
		VALIDARSIMONLY_PORTABILIDAD_MOVIL("Dado un usuario de tipo presencial se valida una SIM de portabilidad, se rellena el formulario y se lanza la orden"),
		
		VALIDAR_SIM_IMEI_TIENDA("Dado un usuario de tipo presencial se valida el IMEI y la SIM, se compra un terminal en tienda y se valida la orden"),
		
		VALIDAR_IMEI_TIENDA("Dado un usuario de tipo presencial se valida el IMEI, se compra un terminal en tienda y se valida la orden"),
		
		SIMONLY_TELETIENDA("Dado un usuario no presencial, ");
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
	 * función script que selecciona un día de envío en función del día actual
	 */
	private String selectDeliveryDay() {
		Date date=new Date(); 
		String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);
		Calendar c = Calendar.getInstance();
	    c.setTime(date);
	    if(dayOfWeek.equals("Thursday"))
	    	c.add(Calendar.DATE, 4);
	    else if (dayOfWeek.equals("Friday"))
	    	c.add(Calendar.DATE, 3);
	    else if (dayOfWeek.equals("Sunday"))
	    	c.add(Calendar.DATE, 1);
	    else
	    	c.add(Calendar.DATE, 2);
	    	
	    return new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss").format(c);
		 
	}
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
	 * @throws FindFailed 
	 */
	private void continuar_finalizarAction() throws FindFailed {
		getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "continuar_finalizar_button.PNG") ).click();
		waitAndCloseMessagePopUp(2);
		waitInSecs(2);
	}
	/**
	 * Función que abarca toda la lógica que se ejecute en la tienda
	 * @return
	 * @throws FindFailed 
	 */
	private void mobile_portability_form() throws FindFailed {
		// TODO depurar debido a un error seleccionando la nacionalidad
		getMyScreen().find(reescaledImage(getWindowPath()+"mobile_portability/", "to_specify_menu.PNG"));
		getMyScreen().type(Key.ENTER);
		getMyScreen().type(Key.TAB);
		getMyScreen().type("Fakename");
		getMyScreen().type(Key.TAB);
		getMyScreen().type("Fakelastnameone");
		getMyScreen().type("Fakelastnametwo");
		//menu para operador donante
		selectFromMenuInputUp(5);
		getMyScreen().type(Key.TAB);
		getMyScreen().type("678678678");
		//menu para nacionalidad
		selectFromMenuInputUp(2);
	}
	/**
	 * función script encargada de las acciones llevadas a cabo dentro de la ventana de la tienda 
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
				screenShot("metodo_pago");
				getMyScreen().find( reescaledImage(getWindowPath()+"store/", "pay_method_cash.PNG") ).click();
			
			//barra de carga para esperar a los detalles de compra
			if(WaitFor("Esperando a que se carguen los detalles de la compra",
					new HashMap<Pattern, Boolean>() {
				private static final long serialVersionUID = 1L;
				{
					put(new Pattern(reescaledImage(getWindowPath()+"store/","reference_of_load_phase2.PNG")).similar(0.9f),true);
					
				}},15)) {
					screenShot("compra_confirmada");
					getMyScreen().find( reescaledImage(getWindowPath()+"store/", "confirmar_compra_button.PNG") ).click();
					//barra de carga para esperar a la confirmación de la compra
					if(WaitFor("Esperando a que se carguen los detalles de la compra",
							new HashMap<Pattern, Boolean>() {
						private static final long serialVersionUID = 1L;
						{
							put(new Pattern(reescaledImage(getWindowPath()+"store/","reference_of_load_phase3.PNG")).similar(0.9f),true);
							
						}},15)) {
						screenShot("finalizar_compra");
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
	 * Script de validación de un IMEI
	 * @param IMEI
	 */
	private void validate_IMEI(String IMEI) {
		// Busca la fila para añadir IMEI dejando vacía primero la casilla
		Location loc;
		try {
			loc = getMyScreen().find( reescaledImage(getWindowPath()+"phone_equipment/", "add_imei_row.PNG") ).getTarget();
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
		} catch (FindFailed e) {
			e.printStackTrace();
		}
	}
	/**
	 * Script de valicación de una SIM
	 * @param SIM
	 */
	private void validate_SIM(String SIM) {
		Location loc;
		// Busca la fila para añadir SIM dejando vacía primero la casilla
	    try {
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
	    } catch (FindFailed e) {
			e.printStackTrace();
		}
	}
/********************************************************************************************************
 *  			FUNCIONES PRINCIPALES
 ********************************************************************************************************/
	/** función script para el alta de sim por televenta <br><br>
	 * <ul>
	 * <li>añadir direccion de envío (pestañita arriba)</li>
	 * <li>añadir fecha del envío</li>
	 * <li>elegir envío a tienda o a domicilio(en caso de ser a domicilio se necesita el numero de operador logistico random , es un movil random)</li>
	 * <li>pulsar el boton de guardar</li>
	 * <li>introducir método de pago ( preferiblemente contrarrembolso )</li>
	 * <li>pulsar boton de pagar y salen pop ups de confirmación</li>
	 * <li>pulsamos cerrar</li>
	 * <li>pulsar el boton de scoring y cerrar sus pop ups</li>
	 * <li>pulsar boton de continuar / finalizar y cerrar sus popups</li>
	 * <li>saldrá un pop up de eleccion de calidad de la venta ( pulsar OK OK preferentemente)</li>
	 * <li>pulsar de nuevo continuar / finalizar para que se guarden los datos y se levantan los procesos</li>
	 * </ul>
	 * @param tmp
	 * @return
	 */
	private boolean SIMOnly_teletienda(Queue<String> tmp) {
		// TODO por completar
		boolean exit = false;
		try {
			Location loc = getMyScreen().find(reescaledImage(getWindowPath()+"delivery_address/", "delivery_date.PNG")).getTarget();
			loc.x-= 30;
			getMyScreen().click(loc);
			for (int i = 0; i<40;i++)
				getMyScreen().type(Key.BACKSPACE);
			getMyScreen().type(selectDeliveryDay());
			continuar_finalizarAction();
			exit = true;
		} catch (FindFailed e) {
			//se finaliza la orden
			System.err.println("fallo por búsqueda de imagen en simonly_teletienda");
			e.printStackTrace();
		}
		return exit;
	}	
	
	/**
	 * Función script para tramitar la orden de <i>SIM con terminal</i> en la ventana de creación de contratos. <br><br>
	 * pasos a realizar
	 * <ul>
	 * <li>validar numero IMEI</li>
	 * <li>validar numero SIM<br>
	 * <li>pulsar boton acceso tienda</li>
	 * <li>esperar a que se abra la tienda</li>
	 * <li>seleccionar metodo de pago, en este caso al contado</li>
	 * <li>confirmamos la compra</li>
	 * <li>vemos que se ha realizado bien y cerramos la ventana</li>
	 * <li>pulsamos el boton de scoring cerrando los popups habituales</li>
	 * <li>pulsamos el boton de continuar finalizar + sus popups</li>
	 * <li>introducimos la firma manual</li>
	 * <li>volvemos a pulsar el boton de continuar finalizar y sus popups</li>
	 * </ul>
	 * @param tmp
	 * @return
	 */
	private boolean validar_SIM_IMEI_ConTiendaEVA(Queue<String> tmp) {
		boolean exit = false;
		String IMEI = tmp.poll();
		IMEI = IMEI.split("\\$")[1];
		String SIM = tmp.poll();
		SIM = SIM.split("\\$")[1];
		waitInSecs(15);
		try {
			validate_IMEI(IMEI);
			validate_SIM(SIM);
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
	 * Función script para tramitar la orden de <i>IMEI con terminal</i> en la ventana de creación de contratos. <br><br>
	 * pasos a realizar
	 * <ul>
	 * <li>validar numero IMEI</li>
	 * <li>pulsar boton acceso tienda</li>
	 * <li>esperar a que se abra la tienda</li>
	 * <li>seleccionar metodo de pago, en este caso al contado</li>
	 * <li>confirmamos la compra</li>
	 * <li>vemos que se ha realizado bien y cerramos la ventana</li>
	 * <li>pulsamos el boton de scoring cerrando los popups habituales</li>
	 * <li>pulsamos el boton de continuar finalizar + sus popups</li>
	 * <li>introducimos la firma manual</li>
	 * <li>volvemos a pulsar el boton de continuar finalizar y sus popups</li>
	 * </ul>
	 * @param tmp
	 * @return
	 */
	private boolean validar_IMEI_ConTiendaEVA(Queue<String> tmp) {
		boolean exit = false;
		String IMEI = tmp.poll();
		IMEI = IMEI.split("\\$")[1];
		waitInSecs(15);
		try {
			validate_IMEI(IMEI);
			// Bajamos con el scroll de la ventana y accedemos a la tienda
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
		try {
			String SIM = tmp.poll();
			SIM = SIM.split("\\$")[1];
			waitInSecs(15);
			validate_SIM(SIM);
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
			e.printStackTrace();
		}
		return exit;
	}
	/**
	 * Función script para tramitar la validación de una orden SIMonly con portabilidad. <br>
	 * <ul>
	 * <li> validar numero SIM de portabilidad </li>
	 * <li> accedemos al tab de portabilidad movil </li>
	 * <li> rellenamos el formulario correctamente </li>
	 * <li> guardamos el formulario rellenado </li>
	 * <li> pulsamos el boton de scoring cerrando los popups habituales </li>
	 * <li> pulsamos el boton de continuar finalizar + sus popups </li>
	 * <li> introducimos la firma manual </li>
	 * <li> volvemos a pulsar el boton de continuar finalizar y sus popups </li>
	 * </ul>
	 * 
	 * @param tmp
	 * @return
	 */
	private boolean validarSIMOnly_portabilidad_movil(Queue<String> tmp) {
		boolean exit = false;
		try {
			String SIM = tmp.poll();
			SIM = SIM.split("\\$")[1];
			waitInSecs(15);
			validate_SIM(SIM);
			// TODO debido a un error en el formulario queda sin probar
			mobile_portability_form();
			getMyScreen().find(reescaledImage("save_button.PNG")).click();
			scoringAction();
			continuar_finalizarAction();
			CheckLoadBar();
			waitInSecs(5);
			signatureAcion();
			waitAndClosePDF(1);
			continuar_finalizarAction();	
		} catch (FindFailed e) {
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
		// se espera a la carga de la ventana
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
			String action = ta.toUpperCase();
			System.out.print("Accion de " +ContractActions.valueOf(action).toString());
			System.out.println("---------------------------------");
			setSourceAction(action);
			switch(ContractActions.valueOf(action)) {
				case VALIDARSIMONLY:
					correct = validarSIMOnly(tmp);
					break;
				case VALIDAR_SIM_IMEI_TIENDA:
					correct = validar_SIM_IMEI_ConTiendaEVA(tmp);
					break;
				case VALIDAR_IMEI_TIENDA:
					correct = validar_IMEI_ConTiendaEVA(tmp);
					break;
				case VALIDARSIMONLY_PORTABILIDAD_MOVIL:
					correct = validarSIMOnly_portabilidad_movil(tmp);
					break;
				case SIMONLY_TELETIENDA:
					//TODO por acabar
					correct = SIMOnly_teletienda(tmp);
				default: 
					System.out.println("Acción no contemplada");
					correct = true;	
			}
		}
		return correct;													  	}

	
}
