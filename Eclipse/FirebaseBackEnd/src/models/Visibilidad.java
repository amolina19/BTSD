package models;

/**
 * @author Alejandro Molina Louchnikov
 */

public class Visibilidad{

    private Boolean usuario, descripcion, telefono, foto, enLinea;

    public Visibilidad() {
    }

    public Visibilidad(Boolean usuario, Boolean telefono, Boolean descripcion, Boolean foto, Boolean enLinea) {
        this.usuario = true;
        this.telefono = true;
        this.descripcion = true;
        this.foto = true;
        this.enLinea = true;
    }

    public Boolean getUsuario() {
        return usuario;
    }

    public void setUsuario(Boolean usuario) {
        this.usuario = usuario;
    }

    public Boolean getTelefono() {
        return telefono;
    }

    public void setTelefono(Boolean telefono) {
        this.telefono = telefono;
    }

    public Boolean getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(Boolean descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getFoto() {
        return foto;
    }

    public void setFoto(Boolean foto) {
        this.foto = foto;
    }

    public Boolean getEnLinea() {
        return enLinea;
    }

    public void setEnLinea(Boolean enLinea) {
        this.enLinea = enLinea;
    }
    
    public String toString() {
    	return "Usuario "+this.usuario+", Descripcion "+this.descripcion+", Foto "+this.foto+", Telefono "+this.telefono+", En Linea "+this.enLinea;
    }
}
