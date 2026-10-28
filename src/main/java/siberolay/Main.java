package siberolay;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        IIncidentRepository repository = new FileIncidentRepository("incidents_db.csv");
        IncidentPrioritizer prioritizer = new IncidentPrioritizer();
        IncidentManager manager = new IncidentManager(repository, prioritizer);
        manager.addObserver(new SecurityAlertNotifier());

        // Varsayilan Analistler
        manager.registerResponder(new IncidentResponder("A-01", "Ahmet Yilmaz", "DDOS"));
        manager.registerResponder(new IncidentResponder("A-02", "Zeynep Kaya", "MALWARE"));
        manager.registerResponder(new IncidentResponder("A-03", "Mehmet Demir", "PHISHING"));

        // Eger veritabani bossa ornek olaylar ekle
        if (manager.getAllIncidents().isEmpty()) {
            manager.registerIncident(IncidentFactory.createIncident("DDOS", "INC-101", "185.22.14.9", "Ana sunucu SYN Flood", 45, "45000", "8.5"));
            manager.registerIncident(IncidentFactory.createIncident("MALWARE", "INC-102", "10.0.4.21", "Muhasebe PC sifreleme", 15, "RANSOMWARE", "6"));
            manager.registerIncident(IncidentFactory.createIncident("PHISHING", "INC-103", "91.14.2.88", "Sahte IK e-postasi", 90, "18", "true"));
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== SİBER GÜVENLİK OLAY YÖNETİM SİSTEMİ ===");
            System.out.println("1. Tüm Güvenlik Olaylarını Listele");
            System.out.println("2. Önceliklendirilmiş Müdahale Kuyruğunu Göster (Max-Heap + Aging)");
            System.out.println("3. Yeni Güvenlik Olayı Kaydet");
            System.out.println("4. En Yüksek Öncelikli Olayı Uygun Analiste Ata");
            System.out.println("5. Olay Durumu Güncelle / Çözüldü İşaretle");
            System.out.println("6. Müdahale Ekibi (Analist) Durumunu Göster");
            System.out.println("0. Çıkış (Veriler Otomatik Kaydedilir)");
            System.out.print("Seçiminiz: ");

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1" -> {
                        System.out.println("\n--- TÜM GÜVENLİK OLAYLARI ---");
                        for (SecurityIncident inc : manager.getAllIncidents()) {
                            System.out.println(inc);
                        }
                    }
                    case "2" -> {
                        System.out.println("\n--- ÖNCELİKLENDİRİLMİŞ MÜDAHALE KUYRUĞU ---");
                        List<SecurityIncident> prioritized = manager.getPrioritizedQueue();
                        int rank = 1;
                        for (SecurityIncident inc : prioritized) {
                            double dynScore = prioritizer.calculateDynamicPriorityScore(inc);
                            System.out.printf("%d. [Dinamik Öncelik Puanı: %6.2f] %s%n   -> Önerilen Müdahale: %s%n",
                                    rank++, dynScore, inc, inc.getResponsePlaybook());
                        }
                    }
                    case "3" -> {
                        System.out.print("Olay Türü (DDOS / MALWARE / PHISHING): ");
                        String type = scanner.nextLine().trim().toUpperCase();
                        System.out.print("Olay ID (Örn: INC-104): ");
                        String id = scanner.nextLine().trim();
                        System.out.print("Kaynak IP (Örn: 192.168.1.50): ");
                        String ip = scanner.nextLine().trim();
                        System.out.print("Açıklama: ");
                        String desc = scanner.nextLine().trim();
                        System.out.print("Bekleme Süresi (Dakika): ");
                        int wait = Integer.parseInt(scanner.nextLine().trim());

                        String p1, p2;
                        if (type.equals("DDOS")) {
                            System.out.print("Saniyedeki İstek Sayısı (RPS): ");
                            p1 = scanner.nextLine().trim();
                            System.out.print("Bant Genişliği (Gbps): ");
                            p2 = scanner.nextLine().trim();
                        } else if (type.equals("MALWARE")) {
                            System.out.print("Zararlı Türü (RANSOMWARE / TROJAN / SPYWARE): ");
                            p1 = scanner.nextLine().trim();
                            System.out.print("Etkilenen Cihaz Sayısı: ");
                            p2 = scanner.nextLine().trim();
                        } else {
                            System.out.print("Hedeflenen Kullanıcı Sayısı: ");
                            p1 = scanner.nextLine().trim();
                            System.out.print("Parola Sızıntısı Var mı? (true / false): ");
                            p2 = scanner.nextLine().trim();
                        }

                        SecurityIncident newInc = IncidentFactory.createIncident(type, id, ip, desc, wait, p1, p2);
                        manager.registerIncident(newInc);
                        System.out.println("Olay başarıyla kaydedildi: " + newInc.getId());
                    }
                    case "4" -> {
                        IncidentResponder assigned = manager.assignTopPriorityIncident();
                        if (assigned != null) {
                            System.out.println("En yüksek öncelikli açık olay " + assigned.getName() + " adlı uzmana atandı.");
                        } else {
                            System.out.println("Atanacak açık olay veya uygun analist bulunamadı.");
                        }
                    }
                    case "5" -> {
                        System.out.print("Güncellenecek Olay ID: ");
                        String id = scanner.nextLine().trim();
                        System.out.print("Yeni Durum (ACIK / INCELENIYOR / COZULDU): ");
                        IncidentStatus status = IncidentStatus.valueOf(scanner.nextLine().trim().toUpperCase());
                        System.out.print("Müdahale Notu: ");
                        String note = scanner.nextLine().trim();

                        if (manager.updateIncidentStatus(id, status, note)) {
                            System.out.println("Olay durumu güncellendi ve kalıcı dosyaya kaydedildi.");
                        } else {
                            System.out.println("Olay bulunamadı!");
                        }
                    }
                    case "6" -> {
                        System.out.println("\n--- SİBER GÜVENLİK MÜDAHALE EKİBİ ---");
                        for (IncidentResponder r : manager.getResponders()) {
                            System.out.println(r);
                        }
                    }
                    case "0" -> {
                        running = false;
                        System.out.println("Sistem kapatılıyor...");
                    }
                    default -> System.out.println("Geçersiz seçim!");
                }
            } catch (Exception e) {
                System.out.println("Hata oluştu: " + e.getMessage());
            }
        }
        scanner.close();
    }
}