package siberolay;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class IncidentManagementTest {
    private static final String TEST_DB = "test_incidents.csv";
    private IIncidentRepository repository;
    private IncidentPrioritizer prioritizer;
    private IncidentManager manager;

    @BeforeEach
    void setUp() {
        File file = new File(TEST_DB);
        if (file.exists()) file.delete();
        repository = new FileIncidentRepository(TEST_DB);
        prioritizer = new IncidentPrioritizer();
        manager = new IncidentManager(repository, prioritizer);
    }

    // 1. Birim Testi: Çok biçimli (Polymorphic) DDoS risk skoru hesaplama testi
    @Test
    void testDDoSRiskScoreCalculation() {
        DDoSIncident ddos = new DDoSIncident("INC-1", "192.168.1.1", "Test DDoS", 10, 50000, 10.0);
        assertEquals(90.0, ddos.calculateBaseRiskScore(), 0.01);
        assertEquals(SeverityLevel.KRITIK, ddos.determineSeverity());
    }

    // 2. Birim Testi: Malware (Ransomware) risk skoru ve seviye testi
    @Test
    void testMalwareRiskScoreCalculation() {
        MalwareIncident malware = new MalwareIncident("INC-2", "10.0.0.5", "Fidye yazilimi", 5, "RANSOMWARE", 4);
        assertEquals(85.0, malware.calculateBaseRiskScore(), 0.01);
        assertEquals(SeverityLevel.KRITIK, malware.determineSeverity());
    }

    // 3. Birim Testi: Factory Method tasarım kalıbı doğru nesne üretimi testi
    @Test
    void testIncidentFactoryCreatesCorrectSubclasses() {
        SecurityIncident inc = IncidentFactory.createIncident("PHISHING", "INC-3", "172.16.0.1", "Oltalama", 20, "15", "true");
        assertTrue(inc instanceof PhishingIncident);
        assertEquals("PHISHING", inc.getIncidentType());
    }

    // 4. Birim Testi: Kapsülleme (Encapsulation) geçersiz IP doğrulaması testi
    @Test
    void testEncapsulationInvalidIpThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DDoSIncident("INC-4", "GECERSIZ_IP", "Hatali IP", 10, 1000, 1.0);
        });
    }

    // 5. Birim Testi: Öncelik Kuyruğu (Max-Heap + Aging) sıralama algoritması testi
    @Test
    void testPriorityQueueOrderingWithAging() {
        SecurityIncident lowRisk = new PhishingIncident("INC-LOW", "1.1.1.1", "Dusuk", 5, 2, false);
        SecurityIncident highRisk = new MalwareIncident("INC-HIGH", "2.2.2.2", "Yuksek", 30, "RANSOMWARE", 5);

        manager.registerIncident(lowRisk);
        manager.registerIncident(highRisk);

        List<SecurityIncident> queue = manager.getPrioritizedQueue();
        assertEquals("INC-HIGH", queue.get(0).getId());
    }

    // 6. Birim Testi: Observer tasarım kalıbı kritik olay alarm testi
    @Test
    void testObserverTriggeredOnCriticalIncident() {
        SecurityAlertNotifier notifier = new SecurityAlertNotifier();
        manager.addObserver(notifier);

        SecurityIncident criticalIncident = new MalwareIncident("INC-CRIT", "8.8.8.8", "Kritik Saldiri", 10, "RANSOMWARE", 6);
        manager.registerIncident(criticalIncident);

        assertEquals(1, notifier.getAlertLogs().size());
    }

    // 7. Birim Testi: Uzmanlık alanına göre otomatik analist atama algoritması testi
    @Test
    void testGreedyResponderAssignmentBySpecialization() {
        manager.registerResponder(new IncidentResponder("R1", "Ali", "PHISHING"));
        manager.registerResponder(new IncidentResponder("R2", "Ayse", "DDOS"));

        SecurityIncident ddos = new DDoSIncident("INC-DDOS", "9.9.9.9", "DDoS", 20, 40000, 5.0);
        manager.registerIncident(ddos);

        IncidentResponder assigned = manager.assignTopPriorityIncident();
        assertNotNull(assigned);
        assertEquals("Ayse", assigned.getName());
        assertEquals(IncidentStatus.INCELENIYOR, ddos.getStatus());
    }

    // 8. Birim Testi: Kalıcı veri saklama (Dosyaya kaydetme ve geri yükleme) testi
    @Test
    void testFilePersistenceSaveAndLoad() {
        SecurityIncident inc = new PhishingIncident("INC-SAVE", "4.4.4.4", "Kalici Kayit Testi", 40, 10, true);
        manager.registerIncident(inc);

        List<SecurityIncident> loaded = repository.loadAll();
        assertEquals(1, loaded.size());
        assertEquals("INC-SAVE", loaded.get(0).getId());
        assertEquals("4.4.4.4", loaded.get(0).getSourceIp());
    }
}