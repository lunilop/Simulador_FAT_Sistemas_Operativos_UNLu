package ar.edu.unlu.fat;

import java.util.ArrayList;
import java.util.List;

public class SimuladorFAT {
    private Archivo[] directorio = new Archivo[100];
    private Disco disco = new Disco();
    private static final int TOTAL_ENTRADAS = 100;

    private int buscarArchivo(String nombre) {
        for(int i = 0; i < this.directorio.length; ++i) {
            Archivo archivo = this.directorio[i];

            if (archivo != null && !archivo.estaBorrado() && archivo.getNombre().equalsIgnoreCase(nombre)) {
                return i;
            }
        }
        return -1;
    }

    private int buscarEntradaLibre() {
        for(int i = 0; i < this.directorio.length; ++i) {

            if (this.directorio[i] == null || this.directorio[i].estaBorrado()) {
                return i;
            }
        }
        return -1;
    }

    public String agregarArchivo(String nombre, String contenido) {
        if (nombre != null && contenido != null) {
            nombre = nombre.trim();

            if (!nombre.isEmpty() && !contenido.isEmpty()) {

                if (nombre.length() > 25) {
                    return "Error: el nombre no puede superar los 25 caracteres.";
                } else if (this.buscarArchivo(nombre) != -1) {
                    return "Error: ya existe un archivo con ese nombre.";
                } else {
                    int entradaLibre = this.buscarEntradaLibre();

                    if (entradaLibre == -1) {
                        return "Error: el directorio está lleno.";
                    } else {
                        int cantidadBloques = (contenido.length() + 10 - 1) / 10;
                        List<Integer> posiciones = this.disco.buscarSectoresLibres(cantidadBloques);

                        if (posiciones == null) {
                            return "Error: no hay espacio suficiente en el disco.";
                        } else {
                            int bloqueInicial = (Integer)posiciones.get(0);
                            this.disco.guardarContenido(contenido, posiciones);

                            Archivo nuevo = new Archivo(nombre, bloqueInicial, cantidadBloques, contenido.length());

                            this.directorio[entradaLibre] = nuevo;
                            return "Archivo guardado correctamente.\nNombre: " + nombre + "\nTamaño real: " + contenido.length() + " caracteres\nSectores ocupados: " + cantidadBloques + "\nEspacio reservado: " + cantidadBloques * 10 + " caracteres.";
                        }
                    }
                }
            } else {
                return "Error: el nombre y el contenido son obligatorios.";
            }
        } else {
            return "Error: los datos no pueden ser nulos.";
        }
    }

    public String leerArchivo(String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            int posicion = this.buscarArchivo(nombre.trim());

            if (posicion == -1) {
                return "Error: el archivo no existe.";
            } else {
                Archivo archivo = this.directorio[posicion];
                String contenido = this.disco.leerContenido(archivo.getBloqueInicial());

                if (contenido == null) {
                    return "Error: no se pudo leer el archivo. La cadena de sectores puede estar dañada.";
                } else {
                    String var10000 = archivo.getNombre();
                    return "Archivo: " + var10000 + "\nTamaño: " + archivo.getTamanoContenido() + " caracteres\n\n" + contenido;
                }
            }
        } else {
            return "Error: debe ingresar un nombre.";
        }
    }

    public String listarArchivos() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("DIRECTORIO DE ARCHIVOS\n\n");
        boolean hayArchivos = false;

        for(Archivo archivo : this.directorio) {
            if (archivo != null && !archivo.estaBorrado()) {
                hayArchivos = true;
                resultado.append("Nombre: ").append(archivo.getNombre()).append("\n");
                resultado.append("Tamaño real: ").append(archivo.getTamanoContenido()).append(" caracteres\n");
                resultado.append("Sectores ocupados: ").append(archivo.getCantidadBloques()).append("\n");
                resultado.append("Espacio reservado: ").append(archivo.getCantidadBloques() * 10).append(" caracteres\n");
                resultado.append("Bloque inicial: ").append(archivo.getBloqueInicial() + 1).append("\n");
                resultado.append("--------------------------\n");
            }
        }
        if (!hayArchivos) {
            resultado.append("No hay archivos almacenados.");
        }
        return resultado.toString();
    }

    public String borrarArchivo(String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            int posicion = this.buscarArchivo(nombre.trim());

            if (posicion == -1) {
                return "Error: el archivo no existe.";
            } else {
                Archivo archivo = this.directorio[posicion];
                this.disco.liberarSectores(archivo.getBloqueInicial());
                archivo.setBorrado(true);
                return "Archivo eliminado correctamente: " + archivo.getNombre();
            }
        } else {
            return "Error: debe ingresar un nombre.";
        }
    }

    public List<Archivo> obtenerArchivosActivos() {
        List<Archivo> archivos = new ArrayList();

        for(Archivo archivo : this.directorio) {
            if (archivo != null && !archivo.estaBorrado()) {
                archivos.add(archivo);
            }
        }
        return archivos;
    }

    public Disco getDisco() {return this.disco;}

    public int contarSectoresLibres() {return this.disco.contarSectoresLibres();}

    public int getTotalSectores() {return 100;}

    public int getTamanioSector() {return 10;}
}