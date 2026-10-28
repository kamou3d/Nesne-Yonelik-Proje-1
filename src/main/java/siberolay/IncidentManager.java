package siberolay;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IncidentManager {
    private final List<SecurityIncident> incidents;
    private final List<IncidentResponder> responders;
    private final List<IncidentObserver> observers;
    private final IIncidentRepository repository;
    private final IncidentPrioritizer prioritizer;

    public IncidentManager(IIncidentRepository repository, IncidentPrioritizer prioritizer) {
        this.repository = repository;
        this.prioritizer = prioritizer;
        this.incidents = new ArrayList<>(repository.loadAll());
        this.responders = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public void addObserver(IncidentObserver observer) {
        observers.add(observer);
    }

    public void registerResponder(IncidentResponder responder) {
        responders.add(responder);
    }

    public void registerIncident(SecurityIncident incident) {
        incidents.add(incident);
        if (incident.determineSeverity() == SeverityLevel.KRITIK) {
            for (IncidentObserver obs : observers) {
                obs.onCriticalIncidentDetected(incident);
            }
        }
        repository.saveAll(incidents);
    }

    public List<SecurityIncident> getPrioritizedQueue() {
        return prioritizer.prioritizeIncidents(incidents);
    }

    public IncidentResponder assignTopPriorityIncident() {
        List<SecurityIncident> queue = getPrioritizedQueue();
        for (SecurityIncident inc : queue) {
            if (inc.getStatus() == IncidentStatus.ACIK) {
                IncidentResponder best = prioritizer.findBestResponder(inc, responders);
                if (best != null) {
                    inc.setStatus(IncidentStatus.INCELENIYOR);
                    inc.setAssignedResponderName(best.getName());
                    best.incrementWorkload();
                    inc.addResponseAction(new ResponseAction(inc.getId(), best.getName(),
                            "Otomatik oncelik kuyrugu ile analiste atandi.", IncidentStatus.INCELENIYOR));
                    repository.saveAll(incidents);
                    return best;
                }
            }
        }
        return null;
    }

    public boolean updateIncidentStatus(String incidentId, IncidentStatus newStatus, String note) {
        Optional<SecurityIncident> opt = incidents.stream()
                .filter(i -> i.getId().equalsIgnoreCase(incidentId))
                .findFirst();
        if (opt.isPresent()) {
            SecurityIncident inc = opt.get();
            inc.setStatus(newStatus);
            inc.addResponseAction(new ResponseAction(inc.getId(), inc.getAssignedResponderName(), note, newStatus));
            if (newStatus == IncidentStatus.COZULDU) {
                responders.stream()
                        .filter(r -> r.getName().equalsIgnoreCase(inc.getAssignedResponderName()))
                        .findFirst()
                        .ifPresent(IncidentResponder::decrementWorkload);
            }
            repository.saveAll(incidents);
            return true;
        }
        return false;
    }

    public List<SecurityIncident> getAllIncidents() { return incidents; }
    public List<IncidentResponder> getResponders() { return responders; }
    public IncidentPrioritizer getPrioritizer() { return prioritizer; }
}