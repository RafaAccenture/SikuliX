package windows;

import java.awt.Font;
import java.io.IOException;
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

public class SearchClientWindow extends PrimalWindow{
	
	/*	-------------------------------------------------
	 * 					ZONA PRIVADA
	 	-------------------------------------------------*/
	private enum SearchClientActions{
		SELECTCLIENT("seleccionar cliente");
		
		private final String text;
	
		 /**
		  * @param text
		  */
		private SearchClientActions(final String text) {
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
	
	private void openWindow() throws FindFailed, IOException {
		dinamicClick(
				"Buscar",
				"Cambria",
				"#272b2d",
				new int[] {255,255,255},
				Font.PLAIN,
				18,
				true);

		waitInMilisecs(1000);

		
		getMyScreen().type(Key.DOWN);
		waitInMilisecs(1000);
		getMyScreen().type(Key.ENTER);
		/*la busqueda por imagen no es fiable
		 * ---------------------------------
		dinamicClick(
				"Búsqueda Clientes",
				"Cambria",
				"#272b2d",
				new int[] {255,255,255},
				Font.PLAIN,
				18,
				false);
		Pattern busquedaClientesPattern = new Pattern(getRepoPath()+"Menus/Buscar_busqueda_clientes.png").similar(0.96f);
		getMyScreen().find(busquedaClientesPattern).click();
		 */
		waitInMilisecs(500);
	}
	private boolean existUser() {
		final Pattern MessageError = new Pattern(getWindowPath()+"popups/MessageError.PNG").similar(0.95f);
		//se comprueba durante 5 segundos
		return !(WaitFor("Comprobando si existe cliente bajo el criterio de busqueda designado",
				new HashMap<Pattern, Boolean>() {/**
			 * busca si esta el boton de cerrar ventana
			 */
			private static final long serialVersionUID = 1L;

		{
			put(MessageError,true);
		}},3));
	}
	private boolean findClient(Queue<String> tmp) throws Exception {
		boolean exit = false;
		Location loc = null;
		final Pattern errorFinderButtons = new Pattern(getWindowPath()+"errorFinderButtons.PNG").similar(0.95f);
		//emula barra de carga para el formulario de busqueda
		if(WaitFor("cargando ventana",
			new HashMap<Pattern, Boolean>() {/**
				 * busca si esta el boton de cerrar ventana
				 */
				private static final long serialVersionUID = 1L;

			{
				put(errorFinderButtons,false);
			}},20)) {
			loc = getMyScreen().findBest(getWindowPath()+"clearButton.png").getTarget();
			getMyScreen().click(loc);


			getMyScreen().keyDown(Key.SHIFT);
			//nos situamos en el principio del formulario
			for(int i = 0;i<18;i++)
				getMyScreen().type(Key.TAB);
			getMyScreen().keyUp(Key.SHIFT);
			//descomponemos el string con los campos del formulario de búsqueda
			while(!tmp.isEmpty()) {
				String s = tmp.poll();
				String[] parts = s.split("\\$");
				if(parts.length > 1)
					getMyScreen().paste(parts[1]);
				getMyScreen().type(Key.TAB);
			}
			mover_ventana("DERECHA");
			loc = getMyScreen().find(getWindowPath()+"FindButton.png").getTarget();
			
			getMyScreen().click(loc);
			loc.x-=200;
			getMyScreen().hover(loc);//desplazo a la derecha el raton apara quitar la vista hover en el boton de busqueda
			waitInMilisecs(500);
			
			//emula barra de carga para la busqueda en la base de datos de un cliente
			final Pattern findButtonLoading = new Pattern(getWindowPath()+"findButtonLoading.png").similar(0.95f);

			if(existUser()) {
				//si existe se espera a que se busque
				exit = true;
				if(!WaitFor("Buscando en la base de datos",
						new HashMap<Pattern, Boolean>() {/**
							 * busca si esta el boton de cerrar ventana
							 */
							private static final long serialVersionUID = 1L;
		
						{
							put(findButtonLoading,false);
						}},10)) 
					throw new Exception("timeout para busqueda en la base de datos");//se lanza excepcion por timeout de la espera
				else
					mover_ventana("IZQUIERDA");
			}	
			
		}else
			throw new Exception("timeout para errorFinderButtons");//se lanza excepcion por timeout de la espera

		return exit;
	}
	/**
	 * Busca un cliente en el formulario y lo selecciona para pasar a su pagina principal de iteraccion
	 * @param tmp
	 * @return
	 * @throws FindFailed 
	 */
	private boolean selectClient(Queue<String> tmp) throws FindFailed {
		boolean exit = false;

		try {
			openWindow();	
			if(!findClient(tmp)) {
				screenShot("__NO__ENCONTRADO");
				//en caso de no existir cliente
				waitInMilisecs(1000);
				getMyScreen().type(Key.ENTER);
				System.out.println("\n\n¡Cliente no encontrado! fin de la ejecucion\n");
				exit = false;
				
			}else {
				//si existe comprobamos errores inesperados
				mover_ventana("ABAJO");
				new Region(346,699,698,257).click(getRepoPath()+"CheckCircle.PNG");
				mover_ventana("DERECHA");
				setLastPatternConfirmed(new Pattern(getWindowPath()+"selectClientButton.png").similar(0.95f));
				//guardamos el 'script' para abrir la siguiente ventana en caso de error
				getNextWindowScript().add(getLastPatternConfirmed());
				exit = true;
			}
		} catch (FindFailed e) {
			throw e;//salimos de la ventana
		} catch (Exception e) {
			exit = false;
			System.err.println(e);
		}
		if (!exit)
			this.closeWindow(1, 0);
		return exit;
	}
	/*	-------------------------------------------------
	 * 					ZONA PUBLICA
	 	-------------------------------------------------*/
	public SearchClientWindow() {
		super();
		setWindowPath(getRepoPath()+"windows/findClient/");
	}
	public SearchClientWindow(Screen myScreen, List<Pattern> references, List<Object> data, UserAmdocs userLogged,
			String MyrepoPath, String sourceAction,Queue<Pattern> nextWindowScript) {
		super(myScreen, references, data, userLogged, sourceAction, nextWindowScript);
		setRepoPath(getRepoPath()+"windows/findClient/");
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
		switch(SearchClientActions.valueOf(ta.toUpperCase())) {
			case SELECTCLIENT:
				setSourceAction("SELECTCLIENT");
				System.out.println("\tselecionar cliente");
				System.out.println("\t---------------------------------");	
				correct = selectClient(tmp);
				break;
			default: 
				System.out.println("Acción no contemplada");
				correct = true;	
		}
		return correct;
	}
	
}
