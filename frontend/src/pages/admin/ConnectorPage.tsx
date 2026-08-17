import { useState } from "react";
import { useToast } from "../../context/ToastContext";
import { connectorApi } from "../../api/connector";
import { ApiError } from "../../api/client";
import { Badge, Button, Card, CardHeader, EmptyState, Input, Select, SpinnerCenter } from "../../components/ui";
import type { DatabaseType } from "../../types/api";

const DB_TYPE_LABELS: Record<DatabaseType, string> = {
  POSTGRESQL: "PostgreSQL",
  MYSQL: "MySQL / MariaDB",
  MSSQL: "Microsoft SQL Server",
};
const DEFAULT_PORTS: Record<DatabaseType, string> = {
  POSTGRESQL: "5432",
  MYSQL: "3306",
  MSSQL: "1433",
};

export function ConnectorPage() {
  const { notifyError } = useToast();
  const [dbType, setDbType] = useState<DatabaseType>("POSTGRESQL");
  const [dbHost, setDbHost] = useState("");
  const [dbPort, setDbPort] = useState(DEFAULT_PORTS.POSTGRESQL);
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
        dbType,
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
        </div>
      </div>

      <Card style={{ marginBottom: 20 }}>
        <Select
          label="Veritabanı tipi"
          value={dbType}
          onChange={(e) => {
            const type = e.target.value as DatabaseType;
            setDbType(type);
            setDbPort(DEFAULT_PORTS[type]);
          }}
        >
          {Object.entries(DB_TYPE_LABELS).map(([value, label]) => (
            <option key={value} value={value}>
              {label}
            </option>
          ))}
        </Select>
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
