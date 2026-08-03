import { useState } from "react";
import { useToast } from "../../context/ToastContext";
import { connectorApi } from "../../api/connector";
import { ApiError } from "../../api/client";
import { Badge, Button, Card, CardHeader, EmptyState, Input, SpinnerCenter } from "../../components/ui";

export function ConnectorPage() {
  const { notifyError } = useToast();
  const [dbHost, setDbHost] = useState("");
  const [dbPort, setDbPort] = useState("5432");
  const [dbName, setDbName] = useState("");
  const [dbUsername, setDbUsername] = useState("");
  const [dbPassword, setDbPassword] = useState("");
  const [testing, setTesting] = useState(false);
  const [tables, setTables] = useState<string[] | null>(null);

  const handleTest = async () => {
    setTesting(true);
    setTables(null);
    try {
      const result = await connectorApi.testConnection({
        dbHost,
        dbPort: Number(dbPort),
        dbName,
        dbUsername,
        dbPassword,
      });
      setTables(result);
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Bağlantı kurulamadı.");
    } finally {
      setTesting(false);
    }
  };

  return (
    <div className="page">
      <div className="page__header">
        <div>
          <h1 className="page__title">Bağlantı Testi</h1>
          <p className="page__subtitle">
            Bir proje oluşturmadan önce, izlemek istediğin veritabanının bilgilerini girip gerçekten
            bağlanılabiliyor mu ve hangi tabloların göründüğünü kontrol et.
          </p>
        </div>
      </div>

      <Card style={{ marginBottom: 20 }}>
        <div className="form-row">
          <Input label="Host" value={dbHost} onChange={(e) => setDbHost(e.target.value)} required />
          <Input label="Port" type="number" value={dbPort} onChange={(e) => setDbPort(e.target.value)} required />
        </div>
        <Input label="Veritabanı adı" value={dbName} onChange={(e) => setDbName(e.target.value)} required />
        <div className="form-row">
          <Input label="Kullanıcı adı" value={dbUsername} onChange={(e) => setDbUsername(e.target.value)} required />
          <Input
            label="Şifre"
            type="password"
            value={dbPassword}
            onChange={(e) => setDbPassword(e.target.value)}
            required
          />
        </div>
        <Button
          variant="primary"
          onClick={handleTest}
          disabled={testing || !dbHost || !dbPort || !dbName || !dbUsername || !dbPassword}
        >
          {testing ? "Test ediliyor…" : "Bağlantıyı Test Et"}
        </Button>
      </Card>

      {testing && <SpinnerCenter />}

      {tables && tables.length === 0 && <EmptyState title="Bu veritabanında hiç tablo yok" />}

      {tables && tables.length > 0 && (
        <div className="grid grid--cols-2">
          {tables.map((name) => (
            <Card key={name}>
              <CardHeader title={name} action={<Badge color="green">bulundu</Badge>} />
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
