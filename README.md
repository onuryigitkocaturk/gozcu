# gözcü

Kullanıcıların izlemek istedikleri veritabanlarına bağlanıp, SQL bilmeden
sürükle-bırak ile kontrol sorguları kurabildiği, bu sorguları saatlik/günlük
periyotlarla otomatik çalıştırıp bir koşul sağlandığında ilgili gruba mail
gönderen bir izleme (monitoring) uygulamasıdır.

---

## 1. Neden var ?

Fikir basit: "şu tablodaki şu koşulu sağlayan satır sayısı şu eşiği geçerse
bana haber ver." Bunu SQL yazmadan, tekrar tekrar elle kontrol etmeden,
arka planda kendiliğinden çalışan bir sisteme devretmek. Proje sabit,
önceden bilinen bir veritabanı için değil, herhangi bir veritabanı için
çalışıyor. Hangi veritabanına bağlanılırsa
bağlanılsın, tablo/kolon yapısı önceden bilinmeden keşfedilip süreç ona
göre dinamik şekilde işliyor.

Örnek senaryolar:

- **Araç filosu (Postgres):** "garantisi 30 gün içinde dolacak VE 2020
  modelden daha yeni VE son 6 ayda bakıma girmemiş" araçları her gün
  kontrol edip filo ekibine mail at.
- **E-ticaret siparişleri (MySQL):** "ödemesi 'başarılı' VE kargo durumu
  hâlâ 'hazırlanıyor' VE sipariş tarihi 3 günden eski" olan (yani kargoya
  verilmesi gecikmiş) siparişleri saatte bir kontrol edip operasyon
  ekibine bildir.
- **Sunucu/log tablosu (Postgres):** "hata seviyesi 'CRITICAL' VE son 1
  saat içinde oluşmuş VE ilgili servis 'ödeme-servisi'" satır sayısı 5'i
  geçerse on-call ekibine anında haber ver.

Dördü de aynı sürükle-bırak koşul ağacı ile, aynı whitelist + parametreli
JDBC mekanizmasıyla, farklı veritabanı tipleri ve farklı iç içe VE/VEYA
gruplamalarıyla kuruluyor. Sistem, tablonun ya da veritabanının ne
olduğunu önceden bilmek zorunda değil.

## 2. Mimari — büyük resim

```
Kullanıcı (React frontend)
        │  JWT ile kimlik doğrulama
        ▼
   Spring Boot backend
        │
        ├── Kendi veritabanı (Postgres)     → kullanıcılar, projeler, sorgular,
        │                                       alertler — JPA ile yönetilir
        │
        └── İzlenen veritabanı(lar)          → her PROJE kendi bağlantı bilgisini
             (Postgres / MySQL / MSSQL)         taşır, JPA KULLANILMAZ, ham JDBC
                                                 ile (connector/ paketi) sorgulanır
```

İki farklı veritabanı katmanı **bilinçli olarak** birbirinden ayrı tutuluyor:
uygulamanın kendi verisi (kullanıcı, proje, sorgu tanımı) JPA/Entity ile
yönetilirken, izlenen veritabanları yapısı önceden bilinmeyen, generic JDBC
(`JdbcTemplate`) ile sorgulanan bağımsız bir katman. Bir tabloyu "entity"
olarak modellemek mümkün değil çünkü her kullanıcının izlediği tablo farklı
olabilir.

**Katman akışı tek yönlü:** Controller → Service → Repository. Bir katman
sadece bir alttakini çağırır, atlama yapılmaz. Entity'ler controller'dan
dışarı hiç sızmaz, her zaman DTO döner.

## 3. Neden bu teknik kararlar alındı

| Karar | Neden |
|---|---|
| Refresh token yok, sadece access token (1 saat) | Bilinçli sadelik tercihi — projenin kapsamı için fazladan karmaşıklık istemedim |
| MapStruct yok, mapper'lar elle yazılıyor | Öğrenme amaçlı: entity→DTO dönüşümünün her adımı görünür kalsın istedim |
| İzlenen tablolarda Criteria API/QueryDSL yok, whitelist + parametreli JDBC var | İzlenen tablolar JPA entity'si değil, yapıları derleme zamanında bilinmiyor — kolon adı whitelist ile, değer hep `?` ile bağlanıyor (SQL injection'a karşı) |
| Her proje kendi DB bağlantı bilgisini taşıyor, `DbConnection` diye ayrı bir entity yok | Bağlantı bilgisi zaten `Project`'in bir parçası; ayrı bir tabloya gerek kalmıyor. |
| Bağlantı şifreleri DB'de şifreli (AES, `CONNECTION_ENCRYPTION_KEY`) tutuluyor | Kullanıcı, izlediği veritabanının gerçek şifresini paylaşıyor — düz metin saklanamaz |
| Login'de yeni cihaz tespit edilirse mail ile 6 haneli kod doğrulaması isteniyor | Ekstra bir güvenlik katmanı — bilinmeyen cihazdan giriş kontrolü |
| Bağlantı testi (`/api/connector/test-connection`) proje kaydından ayrı bir endpoint | Kullanıcı, proje oluşturmadan önce girdiği host/port/şifrenin gerçekten çalıştığını görebilsin istedim. |

## 4. Kod yapısı (backend)

```
model/         gerçek @Entity sınıfları (User, Project, ProjectMembership,
                Query, Alert, AlertLog, Group, TrustedDevice, LoginVerification)
enums/         Role, ProjectRole, Frequency, ConditionOperator gibi enum'lar
dto/           API'de dışarı/içeri giden veri şekilleri
repository/    Spring Data JPA repository'leri
service/       iş mantığı — interface + impl/ altında Impl sınıfı deseni
mapper/        entity <-> dto dönüşümü (elle yazılan mapper'lar)
controller/    REST endpoint'leri
security/      JWT + Spring Security entegrasyonu, login doğrulama
connector/     izlenen veritabanına ham JDBC bağlantısı, tablo keşfi,
                query çalıştırma
querybuilder/  sürükle-bırak JSON ağacının parametreli SQL'e çevrilmesi
                + whitelist doğrulaması
alerting/      bir alert'in şu an tetiklenip tetiklenmeyeceğini hesaplayan
                servis (mail atmaz, sadece hesaplar)
scheduler/     @Scheduled görevler — periyodik sorgu/alert kontrolü
notification/  mail gönderimi (MailHog ile geliştirme ortamında mock)
geocoding/     giriş denemesinin yaklaşık konumunu (IP/koordinat) mail
                bildirimine eklemek için
config/        CORS, DataSource, Jackson gibi genel bean tanımları
```

## 5. Uçtan uca akış — bir sorgunun hayat döngüsü

1. **Proje oluşturulur** — host/port/db tipi/kullanıcı/şifre girilir, kaydedilmeden
   önce gerçekten bağlanılabiliyor mu test edilir, şifre şifrelenerek saklanır.
2. **Tablo keşfedilir** — o projeye bağlı veritabanındaki tablolar listelenir,
   izlemek istediğin tablo projeye eklenir.
3. **Sorgu kurulur** — sürükle-bırak ekranında kolonlar/işleçler/değerlerle bir
   koşul ağacı kurulur, kaydedilirken kolon isimleri gerçek şema ile
   (whitelist) doğrulanır, JSON olarak saklanır.
4. **Alert bağlanır** — bu sorgunun kaç satır eşleştirdiğine göre ("eşik
   değeri" + karşılaştırma) tetiklenme koşulu tanımlanır, hangi gruba mail
   gideceği seçilir.
5. **Scheduler periyodik çalışır** — aktif her sorgunun frequency'sine
   (saatlik/günlük) göre alert'leri değerlendirir, sonucu loglar, tetiklenmişse
   mail gönderir.
6. **Mail gider** — grubun tüm üyelerine, eşleşen satırların bir özetiyle
   birlikte HTML mail atılır.

## 6. Yetkilendirme modeli

İki katmanlı rol sistemi var:

- **Global rol** (`Role`: `ADMIN` / `USER`) — sistem genelinde kullanıcı
  yönetimi, grup yönetimi gibi işler için.
- **Proje rolü** (`ProjectRole`: `REPORTER` < `DEVELOPER` < `MAINTAINER` <
  `OWNER`) — bir kullanıcının **belirli bir projedeki** yetkisi, global
  rolden bağımsız. "En az şu rol" kontrolü hiyerarşik yapılıyor
  (`ProjectAuthorizationService`).

Bir ADMIN her projeye her zaman erişebiliyor; proje bazlı roller ise sadece
o projenin üyeleri için geçerli.

## 7. Kurulum

### Gereksinim: Docker servisleri

Proje kökünden, kendi Postgres'i (uygulama verisi) ve MailHog (sahte SMTP)
ayağa kaldırılır:

```bash
docker compose up -d
```

Kontrol: `docker ps` çıktısında `query-monitor-postgres` ve
`query-monitor-mailhog` görünmeli.

### Backend

`backend/` dizininde, `JWT_SECRET` ve `CONNECTION_ENCRYPTION_KEY` env
değişkenleri set edilmeden uygulama **açılmaz** (bilinçli tercih):

```bash
cd backend
export JWT_SECRET=$(openssl rand -base64 48)
export CONNECTION_ENCRYPTION_KEY=$(openssl rand -base64 32)
./mvnw spring-boot:run
```

Açıldığında: `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install   # sadece ilk seferde
npm run dev
```

Açıldığında: `http://localhost:5173`

### Diğer arayüzler

- **MailHog** (gönderilen mailleri görmek için): `http://localhost:8025`
- Sabit/tek bir izlenen veritabanı yok — her **proje** kendi bağlantı
  bilgisini (host/port/db/kullanıcı/şifre) proje oluşturulurken taşır.
  `CONNECTION_ENCRYPTION_KEY`, bu saklanan şifreleri şifrelemek için
  kullanılan AES anahtarı.

### İlk admin kullanıcı

Frontend'den ("Kayıt olun") kayıt olan herkes `USER` rolüyle başlar. İlk
admin'i SQL ile elle atamak gerekiyor:

```sql
UPDATE users SET role='ADMIN' WHERE username='<kullanici_adi>';
```
