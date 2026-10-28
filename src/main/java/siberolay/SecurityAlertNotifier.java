package siberolay;

import java.util.ArrayList;
import java.util.List;

public class SecurityAlertNotifier implements IncidentObserver {
    private final List<String> alertLogs = new ArrayList<>();

    @Override
    public void onCriticalIncidentDetected(SecurityIncident incident) {
        String alertMsg = "[ALARM - SOC] Kritik Olay Tespit Edildi! ID: " + incident.getId()
                + " | Tur: " + incident.getIncidentType()
                + " | Risk: " + incident.calculateBaseRiskScore();
        alertLogs.add(alertMsg);
        System.out.println("\n>>> " + alertMsg + " <<<");
    }

    public List<String> getAlertLogs() {
        return alertLogs;
    }
}