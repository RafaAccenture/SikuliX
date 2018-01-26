package windows;

import java.util.List;

import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

public class SearchClientWindow extends PrimalWindow{

	public SearchClientWindow() {
		super();
	}

	public SearchClientWindow(Screen myScreen, List<Pattern> references, List<Object> data, String repoPath,
			String sourceAction) {
		super(myScreen, references, data, repoPath, sourceAction);
	}
	
}
