import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import { useAsync } from "../hooks/useAsync";
import { usersApi } from "../api/users";
import { ApiError } from "../api/client";
import { Button, Card, CardHeader, EmptyState, SpinnerCenter } from "../components/ui";
import { formatDateTime } from "../utils/format";

export function AccountPage() {
  const { user } = useAuth();
  const { notifySuccess, notifyError } = useToast();
  const { data: account, loading, error, reload } = useAsync(() => usersApi.myAccount(), []);

  const handleRemoveDevice = async (deviceId: string) => {
    if (!confirm("Bu cihazı güvenilir listesinden kaldırmak istediğine emin misin? Bir sonraki girişte tekrar mail doğrulaması istenecek.")) return;
    try {
      await usersApi.removeDevice(deviceId);
      notifySuccess("Cihaz kaldırıldı.");
      reload();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Cihaz kaldırılamadı.");
    }
  };

  return (
    <div className="page">
      <div className="page__header">
        <div>
          <h1 className="page__title">Hesabım</h1>
        </div>
      </div>

      <Card style={{ marginBottom: 20 }}>
        <CardHeader title="Hesap bilgileri" />
        <div className="grid grid--cols-2">
          <div className="stat-card">
            <div className="stat-card__label">Kullanıcı adı</div>
            <div className="stat-card__value">{user?.username}</div>
          </div>
          <div className="stat-card">
            <div className="stat-card__label">E-posta</div>
            <div className="stat-card__value">{user?.email}</div>
          </div>
        </div>
      </Card>

      {loading && <SpinnerCenter />}
      {error && <div className="alert-banner alert-banner--error">{error}</div>}

      {!loading && !error && account && (
        <>
          <Card style={{ marginBottom: 20 }}>
            <CardHeader title="Sorgu istatistiği" />
            <div className="stat-card">
              <div className="stat-card__label">Bugüne kadar yazdığın sorgu sayısı</div>
              <div className="stat-card__value">{account.totalQueriesWritten}</div>
            </div>
          </Card>

          <Card padded={false}>
            <div style={{ padding: "16px 20px 0" }}>
              <div className="card__title">Güvenilir cihazlar</div>
              <p className="text-sm text-muted mt-8">
                Yeni bir cihazdan giriş yaptığında mail ile doğruladıktan sonra o cihaz burada listelenir; sonraki
                girişlerinde tekrar doğrulama istenmez.
              </p>
            </div>

            {account.trustedDevices.length === 0 ? (
              <div style={{ padding: 20 }}>
                <EmptyState title="Henüz güvenilir cihazın yok" description="İlk girişini doğruladığında burada görünecek." />
              </div>
            ) : (
              <div className="grid grid--cols-2" style={{ padding: "16px 20px 20px" }}>
                {account.trustedDevices.map((device) => (
                  <div className="device-tile" key={device.id}>
                    <div className="device-tile__title">{device.browserLabel ?? "Bilinmeyen tarayıcı"}</div>
                    {device.locationLabel && <div className="device-tile__location">{device.locationLabel}</div>}
                    <div className="device-tile__date">Güvenilir hale geldi: {formatDateTime(device.createdAt)}</div>
                    <Button
                      size="sm"
                      variant="danger"
                      className="mt-8"
                      onClick={() => handleRemoveDevice(device.id)}
                      style={{ alignSelf: "flex-start" }}
                    >
                      Kaldır
                    </Button>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </>
      )}
    </div>
  );
}
