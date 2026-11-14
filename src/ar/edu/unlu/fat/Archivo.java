package ar.edu.unlu.fat;

public class Archivo {
    private String nombre;
    private int bloqueInicial;
    private int cantidadBloques;
    private boolean borrado;
    private int tamanoContenido;

    public Archivo(String nombre, int bloqueInicial, int cantidadBloques, int tamanoContenido) {
        this.nombre = nombre;
        this.bloqueInicial = bloqueInicial;
        this.cantidadBloques = cantidadBloques;
        this.tamanoContenido = tamanoContenido;
        this.borrado = false;
    }

    public String getNombre() {
        return this.nombre;
    }

    public int getBloqueInicial() {
        return this.bloqueInicial;
    }

    public int getCantidadBloques() {
        return this.cantidadBloques;
    }

    public boolean estaBorrado() {
        return this.borrado;
    }

    public int getTamanoContenido() {
        return this.tamanoContenido;
    }

    public void setBorrado(boolean borrado) {
        this.borrado = borrado;
    }
}
