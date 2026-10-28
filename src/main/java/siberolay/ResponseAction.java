package siberolay;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResponseAction {
    private final String incidentId;
    private final String responderName;
    private final String actionNote;
    private final IncidentStatus newStatus;
    private final LocalDateTime actionTime;

    public ResponseAction(String incidentId, String responderName, String actionNote, IncidentStatus newStatus) {
        this.incidentId = incidentId;
        this.responderName = responderName;
        this.actionNote = actionNote;
        this.newStatus = newStatus;
        this.actionTime = LocalDateTime.now();
    }

    public String getIncidentId() { return incidentId; }
    public String getResponderName() { return responderName; }
    public String getActionNote() { return actionNote; }
    public IncidentStatus getNewStatus() { return newStatus; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("[%s] Olay: %s | Uzman: %s | Durum: %s | Not: %s",
                actionTime.format(fmt), incidentId, responderName, newStatus, actionNote);
    }
}