import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProcessImpl extends UnicastRemoteObject implements Process {
    private int processId;
    private int sequenceNumber;
    private AtomicBoolean inCriticalSection;
    private TokenManager tokenManager;

    public ProcessImpl(int processId, TokenManager tokenManager) throws RemoteException {
        this.processId = processId;
        this.sequenceNumber = 0;
        this.inCriticalSection = new AtomicBoolean(false);
        this.tokenManager = tokenManager;
    }

    @Override
    public void requestCriticalSection() throws RemoteException {
        sequenceNumber++;
        System.out.println("Process " + processId + " requesting critical section with sequence number: " + sequenceNumber);
        tokenManager.requestEntry(processId, sequenceNumber);
    }

    @Override
    public void releaseCriticalSection() throws RemoteException {
        inCriticalSection.set(false);
        System.out.println("Process " + processId + " released critical section.");
        tokenManager.releaseToken(processId, sequenceNumber);
    }

    @Override
    public int getSequenceNumber() throws RemoteException {
        return sequenceNumber;
    }

    @Override
    public void receiveGrant() throws RemoteException {
        System.out.println("Process " + processId + " granted access to critical section.");
        inCriticalSection.set(true);
        try {
            Thread.sleep(2000); // Simulate critical section work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        releaseCriticalSection();
    }
}
