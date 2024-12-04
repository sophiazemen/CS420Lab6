import java.rmi.Naming;

public class ProcessClient {
    public static void main(String[] args) {
        try {
            TokenManager tokenManager = (TokenManager) Naming.lookup("rmi://localhost:5000/TokenManager");

            // Create and bind Process instances to RMI registry
            Process process1 = new ProcessImpl(1, tokenManager);
            Naming.rebind("rmi://localhost/Process1", process1);

            Process process2 = new ProcessImpl(2, tokenManager);
            Naming.rebind("rmi://localhost/Process2", process2);

            // Simulate requests to the critical section
            process1.requestCriticalSection();
            process2.requestCriticalSection();
        } catch (Exception e) {
            System.err.println("Error in ProcessClient:");
            e.printStackTrace();
        }
    }
}
