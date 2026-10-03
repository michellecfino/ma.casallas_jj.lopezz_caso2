//Programa para hacer uso del código de hill
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import static System.out;
import java.util.*;


public class CrearArchivo{
    public static void cifrar (byte[][] n, byte[] v, int numPasadas){
        int filas = m.lenght;
        int columnas = m[0].lenght;

        for (int pasada = 0; pasada < numPasadas; pasada++){
            for(int i=0; i<filas; i++){
                for(int j = 0; j < columnas; j++){
                    m[i][j]=(byte) ((m[i][j]+v[j%v.lenght])&0xFF);
                }
            }
            for(int j=0; j<columnas; j++){
                for(int i=0; i <filas;i++){
                    m[i][j]=(byte) ((m[i][j])^ v[i%v.lenght] & 0xFF);

                }
            }
        }

    }

    //Este método es para crear el archivo directamente
    public static void main (String [] args){
        try {
            FileWriter archivo = new FileWriter("C:\\Users\\David.DESKTOP-A6NC9IE\\caso2\\direcciones.txt", true);
            archivo.write("Esto es una prueba");
            archivo.close();

            System.out.println("Creado");
        }
        catch (IOException e){
            System.out.println("Error :c");
            e.printStackTrace();
        }    
        
    }

}
