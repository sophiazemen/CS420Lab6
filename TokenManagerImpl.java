import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.PriorityQueue;
import java.util.Queue;
import java.rmi.Naming;
import java.rmi.NotBoundException;

public class TokenManagerImpl extends UnicastRemoteObject implements TokenManager {
    private Integer tokenHolder;
    private Queue<Request> requestQueue;

    public TokenManagerImpl() throws RemoteException {
        super();
        this.tokenHolder = null;
        this.requestQueue = new PriorityQueue<>();
    }

    @Override
    public synchronized void requestEntry(int processId, int sequenceNumber) throws RemoteException {
        System.out.println("Received request from Process " + processId + " with sequence number: " + sequenceNumber);
        requestQueue.add(new Request(processId, sequenceNumber));

        if (tokenHolder == null || tokenHolder == processId) {
            grantToken();
        }
    }

    @Override
    public synchronized void releaseToken(int processId, int sequenceNumber) throws RemoteException {
        System.out.println("Token released by Process " + processId);
        if (!requestQueue.isEmpty()) {
            grantToken();
        }
    }

    private void grantToken() throws RemoteException {
        if (!requestQueue.isEmpty()) {
            Request nextRequest = requestQueue.poll();
            tokenHolder = nextRequest.processId;

            try {
                // Look up the requesting process in the RMI registry
                Process process = (Process) Naming.lookup("rmi://localhost/Process" + nextRequest.processId);
                process.receiveGrant();
                System.out.println("Granted token to Process " + nextRequest.processId);

            } catch (NotBoundException e) {
                // Handle the exception if the process is not bound
                System.err.println("Error: Process " + nextRequest.processId + " not bound in the registry.");
                e.printStackTrace();
            } catch (Exception e) {
                // Handle other exceptions
                System.err.println("Error during RMI lookup or communication:");
                e.printStackTrace();
            }
        }
    }

    private static class Request implements Comparable<Request> {
        int processId;
        int sequenceNumber;

        public Request(int processId, int sequenceNumber) {
            this.processId = processId;
            this.sequenceNumber = sequenceNumber;
        }

        @Override
        public int compareTo(Request o) {
            if (this.sequenceNumber != o.sequenceNumber) {
                return Integer.compare(this.sequenceNumber, o.sequenceNumber);
            }
            return Integer.compare(this.processId, o.processId);
        }
    }
}
