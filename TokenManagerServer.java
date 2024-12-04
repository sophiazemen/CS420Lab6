import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;

public class TokenManagerServer {
    public static void main(String[] args) {
        try {
            // Start the RMI registry on port 5000
            LocateRegistry.createRegistry(5000);
            System.out.println("RMI Registry started on port 5000");

            // Create the TokenManager instance (make sure TokenManagerImpl is correct)
            TokenManager tokenManager = new TokenManagerImpl(); // Correct initialization

            // Bind the TokenManager object to the registry
            System.out.println("Binding TokenManager to RMI registry...");
            Naming.rebind("rmi://localhost:5000/TokenManager", tokenManager);

            // Confirm the binding
            System.out.println("Token Manager is ready and bound to RMI registry.");

        } catch (Exception e) {
            System.err.println("Exception in TokenManagerServer:");
            e.printStackTrace();
        }
    }
}
