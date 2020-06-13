package models;
import com.google.firebase.internal.NonNull;

/**
 * @author Alejandro Molina Louchnikov
 */
public class Fecha  {

    public String milisegundos;
    public String segundos;
    public String minutos;
    public String hora;
    public String dia;
    public String mes;
    public String anno;

    /**
     * Constructor sin parámetros, al instanciarse llamada los métodos para que devuelvan la fecha individual y se los asigna a los atributos.
     */

    public Fecha(){
        this.milisegundos = this.obtenerMilisegundos();
        this.segundos = this.obtenerSegundos();
        this.minutos = this.obtenerMinutos();
        this.hora = this.obtenerHora();
        this.dia = this.obtenerDia();
        this.mes = this.obtenerMes();
        this.anno = this.obtenerAnno();
    }
    
    public Fecha(String Anno, String mes, String dia,String hora, String minutos, String segundos, String milisegundos){
        this.milisegundos = milisegundos;
        this.segundos = segundos;
        this.minutos = minutos;
        this.hora = hora;
        this.dia = dia;
        this.mes = mes;
        this.anno = Anno;
    }

    public int getMilisegundosInteger() {
        return Integer.parseInt(milisegundos);
    }

    public void setMilisegundosInteger(int milisegundos) {
        this.milisegundos = String.valueOf(milisegundos);
    }

    public int getSegundosInteger() {
        return Integer.parseInt(segundos);
    }

    public void setSegundosInteger(int segundos) {
        this.segundos = String.valueOf(segundos);
    }

    public int getMinutosInteger() {
        return Integer.parseInt(minutos);
    }

    public void setMinutosInteger(int minutos) {
        this.minutos = String.valueOf(minutos);
    }

    public int getHoraInteger() {
        return Integer.parseInt(hora);
    }

    public void setHoraInteger(int hora) {
        this.hora = String.valueOf(hora);
    }

    public int getDiaInteger() {
        return Integer.parseInt(dia);
    }

    public void setDiaInteger(int dia) {
        this.dia = String.valueOf(dia);
    }

    public int getMesInteger() {
        return Integer.parseInt(mes);
    }

    public void setMesInteger(int mes) {
        this.mes = String.valueOf(mes);
    }

    public int getAnnoInteger() {
        return Integer.parseInt(anno);
    }

    public void setAnnoInteger(int anno) {
        this.anno = String.valueOf(anno);
    }

    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve milisengudos en String
     */

    private String obtenerMilisegundos(){
    
    	
        String fecha = java.time.LocalDateTime.now().toString();
        int longitudPunto = fecha.lastIndexOf(".");
        try{
            this.milisegundos = java.time.LocalDateTime.now().toString().substring((longitudPunto+1),fecha.length());
        }catch (StringIndexOutOfBoundsException sioobe){
            this.milisegundos = "000";
        }

        return milisegundos;
    }

    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve segundos en String
     */
    private String obtenerSegundos(){
    	
    	String fecha = java.time.LocalDateTime.now().toString();
    	
        try{
            this.minutos = fecha.toString().substring(17,19);
        }catch (StringIndexOutOfBoundsException sioobe){
            this.segundos = "00";
        }
        return this.minutos;
    }

    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve minutos en String
     */

    public String obtenerMinutos(){
    	String fecha = java.time.LocalDateTime.now().toString();
    	
        this.minutos = fecha.toString().substring(14,16);
        return this.minutos;
    }

    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve la hora en String
     */

    public String obtenerHora(){
    	String fecha = java.time.LocalDateTime.now().toString();
    	
        this.hora = fecha.toString().substring(11,13);
        return this.hora;
    }

    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve el día en String
     */

    public String  obtenerDia(){
    	String fecha = java.time.LocalDateTime.now().toString();
    	
        this.dia = fecha.toString().substring(8,10);
        return this.dia;
    }

    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve el mes en String
     */

    public String obtenerMes(){
    	String fecha = java.time.LocalDateTime.now().toString();
    	
        this.mes = fecha.toString().substring(5,7);
        return this.mes;

    }
    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve el año en String
     */

    public String obtenerAnno(){
        //posicion = posicion-3;
    	String fecha = java.time.LocalDateTime.now().toString();
    	
        this.anno = fecha.toString().substring(0,4);
        return this.anno;
    }

    /**
     * Devuelve la fecha entera actual del sistema concatenada
     * @return Devuelve toda la fecha concatenada en String mediante los atributos.
     */

    @NonNull
    public String toString(){
        return this.hora+":"+this.minutos+" "+this.segundos+" Sec "+this.dia+"/"+this.mes+"/"+this.anno;
    }

    /**
     * Devuelve la fecha entera actual del sistema concatenada
     * @return Devuelve toda la fecha empezando por el año concatenada en String mediante los metodos.
     */

    public String obtenerFechaTotal(){
        return this.obtenerAnno()+""+this.obtenerMes()+""+this.obtenerDia()+""+this.obtenerHora()+""+this.obtenerMinutos()+""+this.obtenerSegundos()+""+this.obtenerMilisegundos();
    }
    
   

}
