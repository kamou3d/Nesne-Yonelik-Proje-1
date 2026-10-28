package siberolay;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class SecurityIncident {
    private String id;
    private String sourceIp;
    private String description;
    private IncidentStatus status;
    private LocalDateTime timestamp;
    private int waitingMinutes;
    private String assignedResponderName;
    private final List<ResponseAction> responseHistory;

    public SecurityIncident(String id, String sourceIp, String description, int waitingMinutes) {
        setId(id);
        setSourceIp(sourceIp);
        setDescription(description);
        setWaitingMinutes(waitingMinutes);
        this.status = IncidentStatus.ACIK;
        this.timestamp = LocalDateTime.now();
        this.assignedResponderName = "Atanmadi";
        this.responseHistory = new ArrayList<>();
    }

    // Soyut metotlar (Abstraction & Polymorphism)
    public abstract double calculateBaseRiskScore();
    public abstract String getIncidentType();
    public abstract String getResponsePlaybook();
    public abstract String toCsvRow();

    public SeverityLevel determineSeverity() {
        double score = calculateBaseRiskScore();
        if (score >= 80) return SeverityLevel.KRITIK;
        if (score >= 60) return SeverityLevel.YUKSEK;
        if (score >= 35) return SeverityLevel.ORTA;
        return SeverityLevel.DUSUK;
    }

    public void addResponseAction(ResponseAction action) {
        this.responseHistory.add(action);
    }

    // Kapsülleme (Encapsulation) ve Doğrulama
    public String getId() { return id; }
    public void setId(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Olay ID bos olamaz!");
        this.id = id;
    }

    public String getSourceIp() { return sourceIp; }
    public void setSourceIp(String sourceIp) {
        if (sourceIp == null || !sourceIp.matches("^[0-9.]+$")) {
            throw new IllegalArgumentException("Gecersiz IP adresi formati!");
        }
        this.sourceIp = sourceIp;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = (description == null) ? "" : description;
    }

    public IncidentStatus getStatus() { return status; }
    public void setStatus(IncidentStatus status) { this.status = status; }

    public int getWaitingMinutes() { return waitingMinutes; }
    public void setWaitingMinutes(int waitingMinutes) {
        if (waitingMinutes < 0) throw new IllegalArgumentException("Bekleme suresi negatif olamaz!");
        this.waitingMinutes = waitingMinutes;
    }

    public String getAssignedResponderName() { return assignedResponderName; }
    public void setAssignedResponderName(String assignedResponderName) {
        this.assignedResponderName = assignedResponderName;
    }

    public List<ResponseAction> getResponseHistory() { return responseHistory; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] Tür: %-8s | IP: %-13s | Seviye: %-6s | Baz Risk: %5.1f | Bekleme: %3d dk | Durum: %-11s | Analist: %s",
                id, getIncidentType(), sourceIp, determineSeverity(), calculateBaseRiskScore(), waitingMinutes, status, assignedResponderName);
    }
}