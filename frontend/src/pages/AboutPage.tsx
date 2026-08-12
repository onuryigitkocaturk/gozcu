import { Badge, Card, CardHeader } from "../components/ui";
import { useAuth } from "../context/AuthContext";

export function AboutPage() {
  const { isAdmin } = useAuth();

  return (
    <div className="page">
      <div className="page__header">
        <div>
          <h1 className="page__title">Sistem Hakkında</h1>
        </div>
      </div>

      <Card style={{ marginBottom: 20 }}>
        <CardHeader title="Bu sistem ne yapar?" />
        <p className="text-sm">
          gözcü, farklı veritabanlarını izlemek için kullanılan bir sistemdir. Bir <strong>proje</strong> oluşturulur,
          o projeye izlenecek veritabanı bağlantısı ve tablolar eklenir. Sürükle-bırak ile SQL bilmeden{" "}
          <strong>sorgular (query)</strong> tanımlanır, bu sorgulara <strong>alarm (alert)</strong> koşulları bağlanır
          (örn. "sonuç sayısı 0'dan büyükse"). Sorgular saatlik/günlük periyotlarla otomatik çalışır; bir alarm
          tetiklendiğinde ilgili mail grubuna bildirim gönderilir.
        </p>
      </Card>

      <Card style={{ marginBottom: 20 }}>
        <CardHeader title="İki farklı yetki katmanı" />
        <p className="text-sm mb-16">
          Sistemde birbirinden bağımsız iki ayrı rol kavramı vardır — biri "sisteme genel erişimin" ne olduğunu, diğeri
          "belirli bir projede ne yapabildiğini" belirler.
        </p>

        <div className="grid grid--cols-2">
          <div>
            <div className="card__title" style={{ marginBottom: 8 }}>
              Genel rol (sistem geneli)
            </div>
            <ul className="text-sm" style={{ paddingLeft: 18 }}>
              <li>
                <Badge color="red">ADMIN</Badge> — tüm projeleri görebilir, kullanıcı/mail grubu yönetimi yapabilir,
                bağlantı testi araçlarına erişebilir.
              </li>
              <li className="mt-8">
                <Badge color="neutral">USER</Badge> — sadece üye olduğu projeleri görür; yönetim ekranlarına erişemez.
              </li>
            </ul>
          </div>

          <div>
            <div className="card__title" style={{ marginBottom: 8 }}>
              Proje rolü (proje bazlı)
            </div>
            <ul className="text-sm" style={{ paddingLeft: 18 }}>
              <li>
                <Badge color="neutral">Reporter</Badge> — projeyi ve verileri görüntüleyebilir.
              </li>
              <li className="mt-8">
                <Badge color="blue">Developer</Badge> — Reporter yetkisine ek olarak sorgu/alarm oluşturabilir.
              </li>
              <li className="mt-8">
                <Badge color="amber">Maintainer</Badge> — Developer yetkisine ek olarak projeye tablo/üye ekleyip
                çıkarabilir.
              </li>
              <li className="mt-8">
                <Badge color="red">Owner</Badge> — projenin tam yetkilisi, başka birini Owner yapabilen tek roldür.
              </li>
            </ul>
          </div>
        </div>

        <p className="text-sm text-muted mt-16">
          Bir kullanıcının aynı anda birden fazla projede farklı rolleri olabilir — örneğin bir projede Developer,
          başka bir projede Reporter olabilir. Kendi projelerindeki rolünü "Projelerim" listesinde her projenin
          yanındaki rozetten görebilirsin.
        </p>
      </Card>

      <Card style={{ marginBottom: 20 }}>
        <CardHeader title="Mail grubu ile proje farkı" />
        <p className="text-sm">
          <strong>Grup</strong> ve <strong>proje</strong> birbirinden bağımsız iki farklı kavramdır. Grup, bir alarm
          tetiklendiğinde mailin kime gideceğini belirler (bildirim hedefi). Proje ise sorguların hangi kapsamda/hangi
          tablolarda çalıştığını ve kimin o projede çalışabildiğini belirler. Bir alarm oluşturulurken, tetiklendiğinde
          hangi gruba mail gideceği ayrıca seçilir.
        </p>
      </Card>

      {isAdmin && (
        <Card style={{ marginBottom: 20 }}>
          <CardHeader title="Yönetici notu" />
          <p className="text-sm">
            ADMIN rolündeki kullanıcılar sol menüdeki <strong>Yönetim</strong> bölümünden tüm kullanıcıları, mail
            gruplarını yönetebilir ve herhangi bir veritabanı bağlantısını test edebilir. Bir projeye üye eklerken
            atanan proje rolü, o kullanıcının o projede neler yapabileceğini belirler — global ADMIN rolünden
            bağımsızdır.
          </p>
        </Card>
      )}

      <Card>
        <CardHeader title="Sorularınız ve görüşleriniz için" />
        <p className="text-sm">
          Sorularınız, hata bildirimleriniz veya önerileriniz için:{" "}
          <a href="mailto:onuryigitkocaturk@gmail.com">onuryigitkocaturk@gmail.com</a>
        </p>
      </Card>
    </div>
  );
}
