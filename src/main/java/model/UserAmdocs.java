package model;

public class UserAmdocs {
	private String name;
	private String rol;
	
	public UserAmdocs(String name, String rol) {

		this.name = name;
		this.rol = rol;
	}

/*
 * ----------------------------------------------------------------------
 * 		Getters y Setters de la clase
 * ----------------------------------------------------------------------
 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}
}
