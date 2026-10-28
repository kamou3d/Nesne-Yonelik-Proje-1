package siberolay;

public class PhishingIncident extends SecurityIncident {
    private int targetedUsersCount;
    private boolean credentialsCompromised;

    public PhishingIncident(String id, String sourceIp, String description, int waitingMinutes, int targetedUsersCount, boolean credentialsCompromised) {
        super(id, sourceIp, description, waitingMinutes);
        if (targetedUsersCount <= 0) {
            throw new IllegalArgumentException("Hedeflenen kullanici sayisi pozitif olmalidir!");
        }
        this.targetedUsersCount = targetedUsersCount;
        this.credentialsCompromised = credentialsCompromised;
    }

    @Override
    public double calculateBaseRiskScore() {
        double score = Math.min(50.0, targetedUsersCount * 2.0);
        if (credentialsCompromised) {
            score += 40.0;
        }
        return Math.min(100.0, score);
    }

    @Override
    public String getIncidentType() { return "PHISHING"; }

    @Override
    public String getResponsePlaybook() {
        return "1. Oltalama alan adini engelle | 2. Etkilenen kullanicilarin parolalarini sifirla | 3. MFA zorunlulugunu aktif et.";
    }

    @Override
    public String toCsvRow() {
        return String.join(";", "PHISHING", getId(), getSourceIp(), getDescription(),
                String.valueOf(getWaitingMinutes()), getStatus().name(), getAssignedResponderName(),
                String.valueOf(targetedUsersCount), String.valueOf(credentialsCompromised));
    }
}