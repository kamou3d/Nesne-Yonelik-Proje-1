package siberolay;

public interface IncidentObserver {
    void onCriticalIncidentDetected(SecurityIncident incident);
}