package models;

/**
 * @author Alejandro Molina Louchnikov
 */

public class Usuario {

    private String id, usuario, imagenURL, estado, hora;
	private String fecha;
	private String descripcion;
	private String telefono;
    private Boolean twoAunthenticatorFactor, phoneVerificationOmited, T2Aintroduced;
    private Visibilidad visibilidad;

    public Usuario(){
    }

    public Usuario(String id, String usuario, String imagenURL) {
        this.id = id;
        this.usuario = usuario;
        this.imagenURL = imagenURL;
        this.T2Aintroduced = false;
        this.setVisibilidad(new Visibilidad(true,true,true,true,true));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getTwoAunthenticatorFactor() {
        return twoAunthenticatorFactor;
    }

    public void setTwoAunthenticatorFactor(Boolean twoAunthenticatorFactor) {
        this.twoAunthenticatorFactor = twoAunthenticatorFactor;
    }

    public Boolean getT2Aintroduced() {
        return T2Aintroduced;
    }

    public void setT2Aintroduced(Boolean t2Aintroduced) {
        T2Aintroduced = t2Aintroduced;
    }

    public Boolean getPhoneVerificationOmited() {
        return phoneVerificationOmited;
    }

    public void setPhoneVerificationOmited(Boolean phoneVerificationOmited) {
        this.phoneVerificationOmited = phoneVerificationOmited;
    }

	public Visibilidad getVisibilidad() {
		return visibilidad;
	}

	public void setVisibilidad(Visibilidad visibilidad) {
		this.visibilidad = visibilidad;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
    

}
