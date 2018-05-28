package windows;

import java.util.List;
import java.util.Queue;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import model.Settings;
import model.UserAmdocs;

public class CreateContractWindow extends PrimalWindow{
	
	/*	-------------------------------------------------
	 * 					ZONA PRIVADA
	 	-------------------------------------------------*/
	private enum ContractActions{
		VALIDARSIMPRESENCIAL("Dado un usuario de tipo presencial se valida la SIM  se lanza la orden");
		
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
	 * 
	 * @param tmp
	 * @return
	 */
	private boolean validarSIMPresencial(Queue<String> tmp) {
		// TODO Auto-generated method stub
		return false;
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
		System.out.print("accion de ");
		switch(ContractActions.valueOf(ta.toUpperCase())) {
			case VALIDARSIMPRESENCIAL:
				setSourceAction("VALIDARSIMPRESENCIAL");
				System.out.println("acceder a las ordenes de trabajo");
				System.out.println("---------------------------------");	
				correct = validarSIMPresencial(tmp);
				break;
			default: 
				System.out.println("Acción no contemplada");
				correct = true;	
		}
		return correct;
	}

	
}
