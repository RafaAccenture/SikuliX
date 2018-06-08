package windows;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import model.Settings;
import model.UserAmdocs;

public class WorkOrdenOneWindow extends PrimalWindow{
	
	/*	-------------------------------------------------
	 * 					ZONA PRIVADA
	 	-------------------------------------------------*/
	private enum CentralPageInteractionActions{
		ALTAMOVIL("dar de alta un movil para el cliente"),
		MODIFICARMOVIL("modificar un movil ya existente");
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
	/********************************************************************************************************
	 *  			FUNCIONES DE APOYO
	 ********************************************************************************************************/
	/**
	 * Selecciona un elemento dentro del árbol de opciones en función de la posición pasada como parámetro
	 * numérico empezando desde arriba
	 * @param pos
	 */
	private void treeMenuSelect(int pos) {
		try {
		//para entrar en el menu
			for(int i = 0;i< 2 ;i++) {
				getMyScreen().type(Key.TAB);
				waitInSecs(2);
			}
			//para ir al principio del menu
			for(int i = 0;i< 12 ;i++) {
				getMyScreen().type(Key.UP);
				waitInMilisecs(300);
			}
			//para ir a la tarifa a cambiar del cliente
			for(int i = 0;i< pos ;i++) {
				getMyScreen().type(Key.DOWN);
				waitInMilisecs(300);
			}
			//busca dentro de la ventana con el arbol de opciones un hueco en blanco donde pinchar
		
			new Region(317,174,441,470).find(reescaledImage(getRepoPath(), "colorOfFocused.PNG")).click();
		} catch (FindFailed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/********************************************************************************************************
	 *  			FUNCIONES PRINCIPALES
	 ********************************************************************************************************/
	private boolean modificarMovil(Queue<String> tmp) {
		boolean exit = false;
		try {
			waitInSecs(5);
			Region menu = new Region(329,247,309,598);
			treeMenuSelect(6);
			menu.find(reescaledImage("modifyButton.PNG")).click();

			waitInSecs(15);// para asegurar la carga del menú
			Region forms = new Region(629,151,971,686);
			//input radiobuttons de línea de teléfono
			if(!tmp.isEmpty())
				selectRadioButtons(tmp.poll(),"movil");				
			mover_ventana("ABAJO");
			forms.findBest(reescaledImage(getWindowPath()+"movil/","AcceptButton.PNG")).click();
			CheckLoadBar();
			waitInSecs(12);
			screenShot("SIN ERRORES");
			forms.findBest(reescaledImage(getWindowPath()+"movil/","processOrder.PNG")).click();
			
			exit = true;

		} catch (Exception e) {
			e.printStackTrace();
			exit = false;
		}
		return exit;
	}
	/**
	 * Se selecciona la opción de añadir un móvil en el CATALOG, y en función de la cola de entrada, se rellenan campos extra
	 * (radioButtons, menús, etc..)
	 * @param tmp
	 * @return
	 * @throws FindFailed
	 */
	private boolean altaMovil(Queue<String> tmp) throws FindFailed {
		boolean exit = false;
		try {
			//emula barra de carga de la ventana en sí
			
			waitInSecs(5);
			Region menu = new Region(329,247,309,598);
			treeMenuSelect(5);				
			menu.find(reescaledImage("addButton.PNG")).click();

			waitInSecs(15);// para asegurar la carga del menú
			Location formsLocation;
			Region forms = new Region(629,151,971,686);
			//input radiobuttons de línea de teléfono
			if(!tmp.isEmpty())
				selectRadioButtons(tmp.poll(),"movil");
			//input de elección de línea de teléfono
			formsLocation = forms.find(reescaledImage(getWindowPath()+"movil/","PhoneLineMenu.PNG")).getTarget();
			formsLocation.x+=20;
			forms.click(formsLocation);
			waitInMilisecs(1500);
			selectFromMenuInputUp(1);
			waitInMilisecs(1000);
			//input de elección de tarifa de llamadas	 
			formsLocation = forms.find(reescaledImage(getWindowPath()+"movil/","callCostMenu.PNG")).getTarget();
			formsLocation.x+=20;
			forms.click(formsLocation);
			selectFromMenuInputUp(10);
			waitInMilisecs(1000);
			//input de elección de SIM
			formsLocation = forms.find(reescaledImage(getWindowPath()+"movil/","simMenu.PNG")).getTarget();
			formsLocation.x+=20;
			forms.click(formsLocation);
			selectFromMenuInputUp(2);
			waitInMilisecs(1000);
			
			mover_ventana("ABAJO");
			forms.findBest(reescaledImage(getWindowPath()+"movil/","AcceptButton.PNG")).click();
			CheckLoadBar();
			waitInSecs(12);
			screenShot("ANTES_PROCESSORDER");
			forms.findBest(reescaledImage(getWindowPath()+"movil/","processOrder.PNG")).click();
			screenShot("DESPUES_PROCESSORDER");
			exit = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			exit = false;
		}

		return exit;
	}
	/*	-------------------------------------------------
	 * 					ZONA PUBLICA
	 	-------------------------------------------------*/
	public WorkOrdenOneWindow(UserAmdocs userAmdocs, Settings settings) {
		super(userAmdocs,settings);
		setWindowPath(getRepoPath()+"windows/workOrdersOne/");
	}
	public WorkOrdenOneWindow(Screen myScreen, List<Pattern> references, List<Object> data, UserAmdocs userLogged,
			String MyrepoPath, String sourceAction,Queue<Pattern> nextWindowScript,Settings settings) {
		super(myScreen, references, data, userLogged, settings, sourceAction, sourceAction, sourceAction, nextWindowScript);
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
		if(
				CheckLoadBar() && 
				WaitFor("Esperando a que se cargue el título de la ventana ordenes de trabajo",
						new HashMap<Pattern, Boolean>() {/**
						* busca si esta el boton de cerrar ventana
						*/
						private static final long serialVersionUID = 1L;
						{
							put(new Pattern(reescaledImage("windowTittle.PNG")).similar(0.9f),true);
						}},15) 
		 ) {
			String ta = tmp.poll();//tipo de acción	
			System.out.print("accion de ");
			switch(CentralPageInteractionActions.valueOf(ta.toUpperCase())) {
				case ALTAMOVIL:
					setSourceAction("ALTAMOVIL");
					System.out.println("dar de alta un móvil");
					System.out.println("---------------------------------");	
					correct = altaMovil(tmp);
					break;
				case MODIFICARMOVIL:
					setSourceAction("MODIFICARMOVIL");
					System.out.println("modificar un móvil");
					System.out.println("---------------------------------");
					correct = modificarMovil(tmp);
					
				default: 
					System.out.println("Acción no contemplada");
					correct = true;	
			}
		}else {
			throw new Exception("timeout para cargando ventana de la pagina Orden de trabajo 1 de 2");
	}
		return correct;
	}

	
}
