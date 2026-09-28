import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteDirectorio {

    static final String IP_SERVIDOR = "192.168.100.10"; // IP de NFSv4-Server
    static final int PUERTO = 5000;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(IP_SERVIDOR, PUERTO);
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            Scanner teclado = new Scanner(System.in)
        ) {
            System.out.println("Conectado al servidor " + IP_SERVIDOR + ":" + PUERTO);

            int consultas = 0;
            while (consultas < 2) {
                System.out.print("Ingrese el número de teléfono a consultar: ");
                String telefono = teclado.nextLine();

                salida.println(telefono);
                String respuesta = entrada.readLine();

                System.out.println("Respuesta del servidor: " + respuesta);
                consultas++;
            }

            salida.println("SALIR");
            System.out.println("Transacción finalizada. El servidor sigue escuchando otras solicitudes.");

        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}