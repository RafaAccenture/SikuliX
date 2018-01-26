package windows;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

public class PrimalWindow {
	private Screen myScreen;
	private List<Pattern> references;
	private List<Object> data;
	private String repoPath;
	private String sourceAction;
	/**
	 * Contructor de PrimalWindow vacío
	 */
	public PrimalWindow() {}
	
	/**
	 * Contructor de PrimalWindow
	 * @param myScreen
	 * @param references
	 * @param data
	 * @param repoPath
	 * @param sourceAction
	 */
	public PrimalWindow(Screen myScreen, List<Pattern> references, List<Object> data, String repoPath, String sourceAction) {
		this.myScreen = myScreen;
		this.references = references;
		this.data = data;
		this.repoPath = repoPath;
		this.sourceAction = sourceAction;
	}
	
	/**
	 * Busca sobre los patrones deseados para respetar los tiempos de carga
	 * @param label : etiqueta a mostrar por la carga
	 * @param references : patrones de imagen referencia a buscar
	 * @return false cuando ha tardado demasidado tiempo </br>true cuando no hay fallos
	 */
	public boolean WaitFor(String label,List<Pattern> references) {
		int cont = 0;
		System.out.println(label);
		Iterator<Pattern> it = references.iterator();
		while(it.hasNext() || cont < 60) {
			Pattern src = it.next();
			while(this.myScreen.exists(src) != null) {
				System.out.print(".");
				cont++;
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.err.println("----\nPara "+label+"en "+this.sourceAction+"\n----");
					e.printStackTrace();
				}
			}
		}
		return cont < 60;
	}

/*
 * ----------------------------------------------------------------------
 * 		Getters y Setters de la clase
 * ----------------------------------------------------------------------
 */
	
	public Screen getMyScreen() {
		return myScreen;
	}

	public void setMyScreen(Screen myScreen) {
		this.myScreen = myScreen;
	}

	public List<Pattern> getReferences() {
		return references;
	}

	public void setReferences(List<Pattern> references) {
		this.references = references;
	}

	public List<Object> getData() {
		return data;
	}

	public void setData(List<Object> data) {
		this.data = data;
	}

	public String getRepoPath() {
		return repoPath;
	}

	public void setRepoPath(String repoPath) {
		this.repoPath = repoPath;
	}

	public String getSourceAction() {
		return sourceAction;
	}

	public void setSourceAction(String sourceAction) {
		this.sourceAction = sourceAction;
	}
}
