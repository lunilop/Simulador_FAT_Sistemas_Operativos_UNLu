package ar.edu.unlu.fat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VentanaPrincipal extends JFrame {

    private final SimuladorFAT simulador;

    private JTextField campoNombre;
    private JTextArea campoContenido;
    private JTextArea areaResultado;

    private JTable tablaDirectorio;
    private DefaultTableModel modeloDirectorio;

    private JTable tablaFAT;
    private DefaultTableModel modeloFAT;

    private PanelDisco panelDisco;
    private JLabel etiquetaEspacio;

    public VentanaPrincipal(SimuladorFAT simulador) {
        this.simulador = simulador;

        setTitle("Simulador de administración FAT");
        setSize(1050, 750);
        setMinimumSize(new Dimension(850, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        construirInterfaz();
        actualizarVista();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(8, 8));

        // PANEL SUPERIOR: FORMULARIO Y OPERACIONES
        JPanel panelSuperior = new JPanel(new BorderLayout(8, 6));

        panelSuperior.setBorder(BorderFactory.createEmptyBorder(6, 10, 3, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(1, 2, 10, 5));

        // Nombre del archivo
        JPanel panelNombre = new JPanel(new BorderLayout(5, 5));

        panelNombre.add(new JLabel("Nombre del archivo:"), BorderLayout.NORTH);

        campoNombre = new JTextField(20);
        panelNombre.add(campoNombre, BorderLayout.CENTER);

        // Contenido del archivo
        JPanel panelContenido = new JPanel(new BorderLayout(5, 5));

        panelContenido.add(new JLabel("Contenido:"), BorderLayout.NORTH);

        campoContenido = new JTextArea(2, 25);
        campoContenido.setLineWrap(true);
        campoContenido.setWrapStyleWord(true);

        panelContenido.add(new JScrollPane(campoContenido), BorderLayout.CENTER);
        panelFormulario.add(panelNombre);
        panelFormulario.add(panelContenido);

        // BOTONES

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));

        JButton botonAgregar = new JButton("Agregar archivo");
        JButton botonLeer = new JButton("Leer archivo");
        JButton botonEliminar = new JButton("Eliminar archivo");
        JButton botonListar = new JButton("Listar directorio");

        botonAgregar.addActionListener(e -> agregarArchivo());
        botonLeer.addActionListener(e -> leerArchivo());
        botonEliminar.addActionListener(e -> eliminarArchivo());
        botonListar.addActionListener(e -> listarArchivos());

        panelBotones.add(botonAgregar);
        panelBotones.add(botonLeer);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonListar);

        panelSuperior.add(panelFormulario, BorderLayout.CENTER);

        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        // PANEL CENTRAL: DISCO Y TABLAS
        panelDisco = new PanelDisco();

        JPanel panelMapa = new JPanel(new BorderLayout(5, 5));
        panelMapa.setBorder(BorderFactory.createTitledBorder("Mapa de sectores"));

        etiquetaEspacio = new JLabel();
        panelMapa.add(etiquetaEspacio, BorderLayout.NORTH);

        JScrollPane scrollMapa = new JScrollPane(panelDisco);

        scrollMapa.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollMapa.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panelMapa.add(scrollMapa, BorderLayout.CENTER);

        // TABLAS
        crearTablaDirectorio();
        crearTablaFAT();

        // Seleccionar archivo desde el directorio
        tablaDirectorio.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        String nombre = obtenerNombreSeleccionado();

                        if (nombre != null) {campoNombre.setText(nombre);}
                    }
                });

        JPanel panelTablas = new JPanel(new GridLayout(1, 2, 10, 0));

        JScrollPane scrollDirectorio = new JScrollPane(tablaDirectorio);

        scrollDirectorio.setBorder(BorderFactory.createTitledBorder("Directorio"));

        JScrollPane scrollFAT = new JScrollPane(tablaFAT);

        scrollFAT.setBorder(BorderFactory.createTitledBorder("Tabla de datos / FAT"));

        panelTablas.add(scrollDirectorio);
        panelTablas.add(scrollFAT);

        // DISTRIBUCIÓN VERTICAL
        JSplitPane separador = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                panelMapa,
                panelTablas
        );
        separador.setResizeWeight(0.48);
        add(separador, BorderLayout.CENTER);

        // PANEL INFERIOR: RESULTADOS
        areaResultado = new JTextArea(4, 30);
        areaResultado.setEditable(false);
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true);

        JScrollPane scrollResultado = new JScrollPane(areaResultado);

        scrollResultado.setBorder(BorderFactory.createTitledBorder("Resultado de la operación"));
        add(scrollResultado, BorderLayout.SOUTH);
    }

    // CREAR TABLA DEL DIRECTORIO
    private void crearTablaDirectorio() {
        String[] columnas = {
                "Nombre",
                "Tamaño real",
                "Sectores",
                "Espacio ocupado",
                "Bloque inicial"
        };
        modeloDirectorio = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDirectorio = new JTable(modeloDirectorio);
        tablaDirectorio.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDirectorio.setRowHeight(24);
    }

    // CREAR TABLA DE DATOS / FAT
    private void crearTablaFAT() {
        String[] columnas = {
                "Sector",
                "Contenido",
                "Siguiente",
                "Estado"
        };

        modeloFAT = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaFAT = new JTable(modeloFAT);
        tablaFAT.setRowHeight(24);
    }

    // OPERACIÓN: AGREGAR
    private void agregarArchivo() {
        String nombre = campoNombre.getText();
        String contenido = campoContenido.getText();
        String resultado = simulador.agregarArchivo(nombre, contenido);

        mostrarResultado(resultado);
        actualizarVista();
    }

    // OPERACIÓN: LEER
    private void leerArchivo() {
        String nombre = obtenerNombreSeleccionado();

        if (nombre == null) { nombre = campoNombre.getText().trim();}

        if (nombre.isEmpty()) {
            mostrarResultado("Seleccioná un archivo o ingresá su nombre.");
            return;
        }
        String resultado = simulador.leerArchivo(nombre);
        mostrarResultado(resultado);
    }

    // OPERACIÓN: ELIMINAR
    private void eliminarArchivo() {
        String nombre = obtenerNombreSeleccionado();

        if (nombre == null) {nombre = campoNombre.getText().trim(); }

        if (nombre.isEmpty()) {
            mostrarResultado("Seleccioná un archivo o ingresá su nombre.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseás eliminar el archivo " + nombre + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (confirmacion != JOptionPane.YES_OPTION) {return;}

        String resultado = simulador.borrarArchivo(nombre);
        mostrarResultado(resultado);
        actualizarVista();
    }

    // OPERACIÓN: LISTAR
    private void listarArchivos() {
        String resultado = simulador.listarArchivos();
        mostrarResultado(resultado);
        actualizarVista();
    }

    // OBTENER NOMBRE SELECCIONADO
    private String obtenerNombreSeleccionado() {
        int fila = tablaDirectorio.getSelectedRow();

        if (fila == -1) {return null;}

        return modeloDirectorio.getValueAt(
                fila, 0
        ).toString();
    }

    // MOSTRAR RESULTADO Y LIMPIAR CAMPOS
    private void mostrarResultado(String resultado) {
        areaResultado.setText(resultado);
        areaResultado.setCaretPosition(0);

        if (!resultado.startsWith("Error:")) {
            campoNombre.setText("");
            campoContenido.setText("");
            tablaDirectorio.clearSelection();
        }
    }

    // ACTUALIZAR TODA LA INFORMACIÓN
    private void actualizarVista() {
        actualizarDirectorio();
        actualizarTablaFAT();

        etiquetaEspacio.setText(
                "Sectores libres: "
                        + simulador.contarSectoresLibres()
                        + " / "
                        + simulador.getTotalSectores()
                        + "    |    Capacidad total: "
                        + (simulador.getTotalSectores()
                        * simulador.getTamanioSector())
                        + " caracteres"
        );
        panelDisco.repaint();
    }

    // ACTUALIZAR DIRECTORIO
    private void actualizarDirectorio() {
        modeloDirectorio.setRowCount(0);

        List<Archivo> archivos = simulador.obtenerArchivosActivos();

        for (Archivo archivo : archivos) {
            int espacioOcupado = archivo.getCantidadBloques() * simulador.getTamanioSector();

            modeloDirectorio.addRow(new Object[] {
                    archivo.getNombre(),
                    archivo.getTamanoContenido()
                            + " caracteres",
                    archivo.getCantidadBloques(),
                    espacioOcupado + " caracteres",
                    archivo.getBloqueInicial() + 1
            });
        }
    }

    // ACTUALIZAR TABLA FAT
    private void actualizarTablaFAT() {
        modeloFAT.setRowCount(0);
        Disco disco = simulador.getDisco();

        for (int i = 0; i < disco.getTotalSectores(); i++) {
            String estado = disco.estaOcupado(i)
                    ? "Ocupado"
                    : "Libre";

            int siguiente = disco.obtenerSiguienteSector(i);

            String siguienteTexto = siguiente == -1
                    ? "-"
                    : String.valueOf(siguiente + 1);

            modeloFAT.addRow(new Object[] {
                    i + 1,
                    disco.obtenerContenidoSector(i),
                    siguienteTexto,
                    estado
            });
        }
    }

    // PANEL PERSONALIZADO DEL DISCO
    private class PanelDisco extends JPanel {

        private final Color[] colores = {
                new Color(52, 120, 190),
                new Color(44, 145, 105),
                new Color(145, 95, 180),
                new Color(205, 135, 55),
                new Color(190, 85, 85),
                new Color(55, 145, 155),
                new Color(125, 125, 65)
        };

        public PanelDisco() {
            int cantidadFilas = (simulador.getTotalSectores() + 9) / 10;

            int altoCelda = 48;
            int espacio = 5;
            int margen = 20;

            int altoTotal = cantidadFilas * (altoCelda + espacio) + margen;

            setPreferredSize(new Dimension(850, altoTotal));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            Disco disco = simulador.getDisco();

            Map<Integer, String> propietarios = obtenerPropietarios();

            int columnas = 10;
            int anchoCelda = 75;
            int altoCelda = 48;
            int espacio = 5;
            int inicioX = 15;
            int inicioY = 10;

            for (int i = 0; i < disco.getTotalSectores(); i++) {
                int fila = i / columnas;
                int columna = i % columnas;

                int x = inicioX + columna * (anchoCelda + espacio);
                int y = inicioY + fila * (altoCelda + espacio);

                String propietario = propietarios.get(i);

                Color color = new Color(225, 225, 225);

                if (propietario != null) {
                    int indice = Math.abs(propietario.hashCode() % colores.length);
                    color = colores[indice];
                }

                // FONDO DEL SECTOR
                g2.setColor(color);

                g2.fillRoundRect(x, y,
                        anchoCelda,
                        altoCelda,
                        10,
                        10);

                // BORDE
                g2.setColor(Color.DARK_GRAY);

                g2.drawRoundRect(x, y,
                        anchoCelda,
                        altoCelda,
                        10,
                        10);

                // NÚMERO DEL SECTOR
                g2.setColor(propietario == null
                                ? Color.DARK_GRAY
                                : Color.WHITE);

                g2.setFont(new Font("Arial", Font.BOLD, 13));

                g2.drawString("Sector " + (i + 1),
                        x + 7,
                        y + 19);

                // NOMBRE DEL ARCHIVO O ESTADO
                g2.setFont(new Font("Arial", Font.PLAIN, 11));

                String texto = propietario == null
                        ? "Libre"
                        : propietario;

                if (texto.length() > 11) { texto = texto.substring(0, 11);}

                g2.drawString(texto,
                        x + 7,
                        y + 37);
            }
            g2.dispose();
        }

        // OBTENER ARCHIVO ASOCIADO A CADA SECTOR
        private Map<Integer, String> obtenerPropietarios() {

            Map<Integer, String> propietarios = new HashMap<>();
            Disco disco = simulador.getDisco();

            for (Archivo archivo : simulador.obtenerArchivosActivos()) {

                int actual = archivo.getBloqueInicial();
                int contador = 0;

                while (actual != -1 && actual >= 0
                        && actual < disco.getTotalSectores()
                        && contador < disco.getTotalSectores()) {

                    if (!disco.estaOcupado(actual)) {break;}

                    propietarios.put(actual,
                            archivo.getNombre()
                    );
                    actual = disco.obtenerSiguienteSector(actual);
                    contador++;
                }
            }
            return propietarios;
        }
    }
}