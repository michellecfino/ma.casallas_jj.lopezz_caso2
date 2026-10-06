import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CrearArchivo {

    /**
     * Algoritmo Hill de cifrado proporcionado en el enunciado.
     * Mantenido tal como lo exige la actividad.
     */
    public static void cifrar(byte[][] m, byte[] v, int numPasadas) {
        int filas = m.length;
        int columnas = m[0].length;

        for (int pasada = 0; pasada < numPasadas; pasada++) {
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    m[i][j] = (byte) ((m[i][j] + v[j % v.length]) & 0xFF);
                }
            }
        }

        for (int j = 0; j < columnas; j++) {
            for (int i = 0; i < filas; i++) {
                m[i][j] = (byte) ((m[i][j] ^ v[i % v.length]) & 0xFF);
            }
        }
    }

    /**
     * Simula la ejecución de la función cifrar() y escribe las referencias a memoria
     * virtual en el archivo de salida con el formato exacto del Anexo A.
     */
    public static void generarReferencias(int filas, int columnas, int tamVector, 
                                           int tamPag, int numPasadas, String nombreArchivo) {
        
        // 1. Definición de tamaños en bytes (byte = 1 B)
        int bytesMatriz = filas * columnas;
        int totalBytes = bytesMatriz + tamVector;
        
        // Offset donde comienza el vector en la memoria virtual del proceso
        int offsetVector = bytesMatriz;
        
        // Número de páginas virtuales requeridas (NP = ceil(totalBytes / tamPag))
        int np = (int) Math.ceil((double) totalBytes / tamPag);
        
        // Número total de referencias a memoria (NR)
        // Cada celda realiza 3 accesos: Lectura M, Lectura V, Escritura M
        long nr = (long) (numPasadas + 1) * filas * columnas * 3;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {
            
            // --- Encabezado según Anexo A ---
            bw.write("TP=" + tamPag + "\n");
            bw.write("NF1=" + filas + "\n");
            bw.write("NC1=" + columnas + "\n");
            bw.write("Tamaño vector clave=" + tamVector + "\n");
            bw.write("numPasadas=" + numPasadas + "\n");
            bw.write("NR=" + nr + "\n");
            bw.write("NP=" + np + "\n");

            // --- BUCLE 1: Cifrado por filas (numPasadas veces) ---
            for (int pasada = 0; pasada < numPasadas; pasada++) {
                for (int i = 0; i < filas; i++) {
                    for (int j = 0; j < columnas; j++) {

                        // Dirección virtual de m[i][j] en Row-Major
                        int dirM = (i * columnas) + j;
                        int pagM = dirM / tamPag;
                        int offM = dirM % tamPag;

                        // Dirección virtual de v[j % tamVector]
                        int idxV = j % tamVector;
                        int dirV = offsetVector + idxV;
                        int pagV = dirV / tamPag;
                        int offV = dirV % tamPag;

                        // 1. Lectura m[i][j]
                        bw.write(String.format("[mat1-%d-%d],%d,%d\n", i, j, pagM, offM));
                        // 2. Lectura v[j % v.length]
                        bw.write(String.format("[v-0-%d],%d,%d\n", idxV, pagV, offV));
                        // 3. Escritura m[i][j]
                        bw.write(String.format("[mat1-%d-%d],%d,%d\n", i, j, pagM, offM));
                    }
                }
            }

            // --- BUCLE 2: Recorrido por columnas (1 vez) ---
            for (int j = 0; j < columnas; j++) {
                for (int i = 0; i < filas; i++) {

                    // Dirección virtual de m[i][j]
                    int dirM = (i * columnas) + j;
                    int pagM = dirM / tamPag;
                    int offM = dirM % tamPag;

                    // Dirección virtual de v[i % tamVector]
                    int idxV = i % tamVector;
                    int dirV = offsetVector + idxV;
                    int pagV = dirV / tamPag;
                    int offV = dirV % tamPag;

                    // 1. Lectura m[i][j]
                    bw.write(String.format("[mat1-%d-%d],%d,%d\n", i, j, pagM, offM));
                    // 2. Lectura v[i % v.length]
                    bw.write(String.format("[v-0-%d],%d,%d\n", idxV, pagV, offV));
                    // 3. Escritura m[i][j]
                    bw.write(String.format("[mat1-%d-%d],%d,%d\n", i, j, pagM, offM));
                }
            }

            System.out.println("\n-> Archivo '" + nombreArchivo + "' generado exitosamente.");
            System.out.println("   Referencias escritas (NR): " + nr);
            System.out.println("   Páginas virtuales (NP): " + np);

        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner lector = new Scanner(System.in);

        System.out.println("==================================================");
        System.out.println(" GENERADOR DE TRAZA DE MEMORIA VIRTUAL - CASO 2  ");
        System.out.println("==================================================");

        System.out.print("Número de filas (NF1): ");
        int filas = lector.nextInt();

        System.out.print("Número de columnas (NC1): ");
        int columnas = lector.nextInt();

        System.out.print("Tamaño del vector (NV): ");
        int tamVector = lector.nextInt();

        System.out.print("Tamaño de página en bytes (TP): ");
        int tamPag = lector.nextInt();

        System.out.print("Número de pasadas: ");
        int numPasadas = lector.nextInt();

        lector.nextLine(); // Limpiar el buffer de entrada
        System.out.print("Nombre o ruta del archivo de salida (ej. direcciones.txt): ");
        String nombreArchivo = lector.nextLine().trim();

        if (nombreArchivo.isEmpty()) {
            nombreArchivo = "direcciones.txt";
        }

        generarReferencias(filas, columnas, tamVector, tamPag, numPasadas, nombreArchivo);

        lector.close();
    }
}