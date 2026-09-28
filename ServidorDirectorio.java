import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.concurrent.*;

public class ServidorDirectorio {

    static final int NUMERO_HILOS = 10;
    static final int PUERTO = 5000;
    static final String DB_URL = "jdbc:mariadb://localhost:3306/directorio";
    static final String DB_USER = "appuser";
    static final String DB_PASS = "contra123";

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(NUMERO_HILOS); // Pool de hilos para manejar múltiples clientes

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor escuchando en el puerto " + PUERTO + "...");

            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("Cliente conectado: " + socketCliente.getInetAddress());
                pool.execute(() -> manejarCliente(socketCliente)); // Manejar cada cliente en un hilo separado
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void manejarCliente(Socket socketCliente) {
        try (
            BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socketCliente.getInputStream()));
            PrintWriter salida = new PrintWriter(socketCliente.getOutputStream(), true)
        ) {
            String telefono;
            while ((telefono = entrada.readLine()) != null) {
                if (telefono.equalsIgnoreCase("SALIR")) {
                    System.out.println("Cliente finalizó la sesión.");
                    break;
                }
                String respuesta = consultarPersona(telefono);
                salida.println(respuesta);
            }
        } catch (IOException e) {
            System.err.println("Error manejando cliente: " + e.getMessage());
        } finally {
            try {
                socketCliente.close();
            } catch (IOException e) {
                System.err.println("Error cerrando socket del cliente: " + e.getMessage());
            }
        }
    }

    private static String consultarPersona(String telefono) {
        String sql = "SELECT p.dir_tel, p.dir_nombre, p.dir_direccion, c.ciudad_nombre " +
                     "FROM personas p JOIN ciudades c ON p.dir_ciudad_id = c.ciudad_id " +
                     "WHERE p.dir_tel = ?";

        try (
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, telefono);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return "Teléfono: " + rs.getString("dir_tel") +
                           " | Nombre: " + rs.getString("dir_nombre") +
                           " | Dirección: " + rs.getString("dir_direccion") +
                           " | Ciudad: " + rs.getString("ciudad_nombre");
                } else {
                    return "La persona dueña de ese número telefónico no existe en el directorio.";
                }
            }
        } catch (SQLException e) {
            return "Error consultando la base de datos: " + e.getMessage();
        }
    }
}