package ar.edu.unlu.fat;

import java.util.ArrayList;
import java.util.List;

public class Disco {
    public static final int TOTAL_SECTORES = 100;
    public static final int TAMANIO_SECTOR = 10;
    private Sector[] sectores = new Sector[100];
    private boolean[] tablaBits = new boolean[100];

    public Disco() {
        for(int i = 0; i < 100; ++i) {
            this.sectores[i] = new Sector();
            this.tablaBits[i] = false;
        }
    }

    public List<Integer> buscarSectoresLibres(int cantidad) {
        List<Integer> posiciones = new ArrayList();

        for(int i = 0; i < 100; ++i) {
            if (!this.tablaBits[i]) {
                posiciones.add(i);
            }
            if (posiciones.size() == cantidad) {break;}
        }
        return posiciones.size() < cantidad ? null : posiciones;
    }

    public void guardarContenido(String contenido, List<Integer> posiciones) {
        for(int i = 0; i < posiciones.size(); ++i) {
            int posicion = (Integer)posiciones.get(i);
            int inicio = i * 10;
            int fin = Math.min(inicio + 10, contenido.length());

            String parte = contenido.substring(inicio, fin);
            this.sectores[posicion].setContenido(parte);
            this.tablaBits[posicion] = true;

            if (i < posiciones.size() - 1) {
                int siguiente = (Integer)posiciones.get(i + 1);
                this.sectores[posicion].setSiguiente(siguiente);
            } else {
                this.sectores[posicion].setSiguiente(-1);
            }
        }

    }

    public String leerContenido(int bloqueInicial) {
        StringBuilder contenido = new StringBuilder();
        int actual = bloqueInicial;
        int contador = 0;

        while(true) {
            if (actual != -1 && contador < 100) {
                if (actual >= 0 && actual < 100) {
                    if (!this.tablaBits[actual]) {return null;}

                    contenido.append(this.sectores[actual].getContenido());
                    actual = this.sectores[actual].getSiguiente();
                    ++contador;
                    continue;
                }
                return null;
            }
            if (actual != -1) {return null;}

            return contenido.toString();
        }
    }

    public void liberarSectores(int bloqueInicial) {
        int actual = bloqueInicial;

        for(int contador = 0; actual != -1 && contador < 100 && actual >= 0 && actual < 100 && this.tablaBits[actual]; ++contador) {
            int siguiente = this.sectores[actual].getSiguiente();
            this.sectores[actual].limpiar();
            this.tablaBits[actual] = false;
            actual = siguiente;
        }

    }

    public void mostrarFAT() {
        System.out.println("\n========== TABLA FAT ==========");
        System.out.printf("%-12s %-15s %-12s%n", "Sector", "Contenido", "Siguiente");

        for(int i = 0; i < 100; ++i) {
            if (this.tablaBits[i]) {
                System.out.printf("%-12d %-15s %-12s%n", i + 1, this.sectores[i].getContenido(), this.sectores[i].getSiguiente() == -1 ? "FIN" : this.sectores[i].getSiguiente() + 1);
            }
        }

    }

    public void mostrarTablaBits() {
        System.out.println("\n========== TABLA DE BITS ==========");

        for(int i = 0; i < 100; ++i) {
            System.out.print(this.tablaBits[i] ? "1 " : "0 ");
            if ((i + 1) % 10 == 0) {
                System.out.println();
            }
        }

    }

    public void mostrarMapaDisco() {
        System.out.println("\n========== MAPA DEL DISCO ==========");

        for(int i = 0; i < 100; ++i) {
            if (this.tablaBits[i]) {
                System.out.print("[ X ] ");
            } else {
                System.out.print("[   ] ");
            }

            if ((i + 1) % 10 == 0) {
                System.out.println();
            }
        }
        System.out.println("\nX = ocupado | vacío = libre");
    }

    public int contarSectoresLibres() {
        int contador = 0;

        for(int i = 0; i < 100; ++i) {
            if (!this.tablaBits[i]) {
                ++contador;
            }
        }
        return contador;
    }

    public boolean estaOcupado(int posicion) {
        return posicion >= 0 && posicion < 100 ? this.tablaBits[posicion] : false;
    }

    public String obtenerContenidoSector(int posicion) {
        return posicion >= 0 && posicion < 100 ? this.sectores[posicion].getContenido() : "";
    }

    public int obtenerSiguienteSector(int posicion) {
        return posicion >= 0 && posicion < 100 ? this.sectores[posicion].getSiguiente() : -1;
    }

    public int getTotalSectores() {return 100;}

    public int getTamanioSector() {return 10;}
}
