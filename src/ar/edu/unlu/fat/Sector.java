package ar.edu.unlu.fat;

public class Sector {
    private String contenido = "";
    private int siguiente = -1;

    public String getContenido() {
        return this.contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public int getSiguiente() {
        return this.siguiente;
    }

    public void setSiguiente(int siguiente) {
        this.siguiente = siguiente;
    }

    public void limpiar() {
        this.contenido = "";
        this.siguiente = -1;
    }
}