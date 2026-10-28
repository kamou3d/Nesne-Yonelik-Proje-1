package siberolay;

public class IncidentResponder {
    private String responderId;
    private String name;
    private String specialization; // DDOS, MALWARE, PHISHING, GENERAL
    private int activeIncidentCount;

    public IncidentResponder(String responderId, String name, String specialization) {
        this.responderId = responderId;
        this.name = name;
        this.specialization = specialization.toUpperCase();
        this.activeIncidentCount = 0;
    }

    public void incrementWorkload() { this.activeIncidentCount++; }
    public void decrementWorkload() {
        if (this.activeIncidentCount > 0) this.activeIncidentCount--;
    }

    public String getResponderId() { return responderId; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public int getActiveIncidentCount() { return activeIncidentCount; }

    @Override
    public String toString() {
        return String.format("%s (%s) - Uzmanlık: %-8s | Aktif Görev: %d", name, responderId, specialization, activeIncidentCount);
    }
}