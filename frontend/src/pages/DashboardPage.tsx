import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import { useAsync } from "../hooks/useAsync";
import { projectsApi } from "../api/projects";
import { connectorApi } from "../api/connector";
import { ApiError } from "../api/client";
import { Badge, Button, Card, CardHeader, EmptyState, Input, Modal, Select, SpinnerCenter } from "../components/ui";
import { formatDateTime } from "../utils/format";
import type { DatabaseType, ProjectRole } from "../types/api";

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

const ROLE_LABELS: Record<ProjectRole, string> = {
  REPORTER: "Reporter",
  DEVELOPER: "Developer",
  MAINTAINER: "Maintainer",
  OWNER: "Owner",
};
const ROLE_COLORS: Record<ProjectRole, "neutral" | "blue" | "green" | "amber" | "red"> = {
  REPORTER: "neutral",
  DEVELOPER: "blue",
  MAINTAINER: "amber",
  OWNER: "red",
};

export function DashboardPage() {
  const { isAdmin } = useAuth();
  return isAdmin ? <AdminDashboard /> : <MemberDashboard />;
}

function ProjectStatsBadges({ projectId }: { projectId: string }) {
  const { data: stats, loading, error } = useAsync(() => projectsApi.getDashboardStats(projectId), [projectId]);

  if (loading || error || !stats) return null;

  return (
    <div className="list-row__stats">
      <Badge color="neutral">{stats.tableCount} tablo</Badge>
      <Badge color="neutral">
        {stats.queryCount} sorgu ({stats.activeQueryCount} aktif)
      </Badge>
      <Badge color="blue">{stats.activeAlertCount} aktif alarm</Badge>
      {stats.triggeredLast7Days > 0 ? (
        <Badge color="amber">Son 7 günde {stats.triggeredLast7Days} kez tetiklendi</Badge>
      ) : (
        <Badge color="green">Son 7 günde tetiklenme yok</Badge>
      )}
    </div>
  );
}

function AdminDashboard() {
  const navigate = useNavigate();
  const { notifySuccess, notifyError } = useToast();
  const { data: projects, loading, error, reload } = useAsync(() => projectsApi.list(), []);
  const [modalOpen, setModalOpen] = useState(false);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [dbType, setDbType] = useState<DatabaseType>("POSTGRESQL");
  const [dbHost, setDbHost] = useState("");
  const [dbPort, setDbPort] = useState(DEFAULT_PORTS.POSTGRESQL);
  const [dbName, setDbName] = useState("");
  const [dbUsername, setDbUsername] = useState("");
  const [dbPassword, setDbPassword] = useState("");
  const [saving, setSaving] = useState(false);
  const [testing, setTesting] = useState(false);

  const resetForm = () => {
    setName("");
    setDescription("");
    setDbType("POSTGRESQL");
    setDbHost("");
    setDbPort(DEFAULT_PORTS.POSTGRESQL);
    setDbName("");
    setDbUsername("");
    setDbPassword("");
  };

  const handleDbTypeChange = (type: DatabaseType) => {
    setDbType(type);
    setDbPort(DEFAULT_PORTS[type]);
  };

  const handleTestConnection = async () => {
    setTesting(true);
    try {
      const tables = await connectorApi.testConnection({
        dbType,
        dbHost,
        dbPort: Number(dbPort),
        dbName,
        dbUsername,
        dbPassword,
      });
      notifySuccess(`Bağlantı başarılı, ${tables.length} tablo bulundu.`);
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Bağlantı kurulamadı.");
    } finally {
      setTesting(false);
    }
  };

  const handleCreate = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await projectsApi.create({
        name,
        description: description || undefined,
        dbType,
        dbHost,
        dbPort: Number(dbPort),
        dbName,
        dbUsername,
        dbPassword,
      });
      notifySuccess(`"${name}" projesi oluşturuldu.`);
      setModalOpen(false);
      resetForm();
      reload();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Proje oluşturulamadı.");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: string, projectName: string) => {
    if (!confirm(`"${projectName}" projesini silmek istediğine emin misin? Bağlı tüm tablolar da silinecek.`)) return;
    try {
      await projectsApi.remove(id);
      notifySuccess("Proje silindi.");
      reload();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Proje silinemedi.");
    }
  };

  return (
    <div className="page">
      <div className="page__header">
        <div>
          <h1 className="page__title">Projeler</h1>
        </div>
        <Button variant="primary" onClick={() => setModalOpen(true)}>
          + Yeni Proje
        </Button>
      </div>

      {loading && <SpinnerCenter />}
      {error && <div className="alert-banner alert-banner--error">{error}</div>}

      {!loading && !error && projects && projects.length === 0 && (
        <EmptyState
          title="Henüz proje yok"
          description="İlk projeni oluşturarak izlenen tabloları ve sorguları burada organize etmeye başla."
          action={
            <Button variant="primary" onClick={() => setModalOpen(true)} className="mt-8">
              + Yeni Proje
            </Button>
          }
        />
      )}

      {!loading && !error && projects && projects.length > 0 && (
        <Card padded={false}>
          {projects.map((project) => (
            <div className="list-row" key={project.id}>
              <div className="list-row__main">
                <a
                  className="list-row__title"
                  href={`/projects/${project.id}`}
                  onClick={(e) => {
                    e.preventDefault();
                    navigate(`/projects/${project.id}`);
                  }}
                >
                  {project.name}
                </a>
                <div className="list-row__meta">
                  {project.description && <span>{project.description}</span>}
                  <span>· {formatDateTime(project.createdAt)}</span>
                </div>
                <ProjectStatsBadges projectId={project.id} />
              </div>
              <div className="list-row__actions">
                <Button size="sm" variant="secondary" onClick={() => navigate(`/projects/${project.id}`)}>
                  Aç
                </Button>
                <Button size="sm" variant="danger" onClick={() => handleDelete(project.id, project.name)}>
                  Sil
                </Button>
              </div>
            </div>
          ))}
        </Card>
      )}

      <Modal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        title="Yeni Proje"
        footer={
          <>
            <Button variant="ghost" onClick={() => setModalOpen(false)}>
              Vazgeç
            </Button>
            <Button variant="primary" onClick={handleCreate} disabled={saving || !name.trim()}>
              {saving ? "Oluşturuluyor…" : "Oluştur"}
            </Button>
          </>
        }
      >
        <form onSubmit={handleCreate}>
          <Input label="Proje adı" value={name} onChange={(e) => setName(e.target.value)} autoFocus required />
          <Input
            label="Açıklama (opsiyonel)"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
          <Select
            label="Veritabanı tipi"
            value={dbType}
            onChange={(e) => handleDbTypeChange(e.target.value as DatabaseType)}
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
            <Input
              label="Kullanıcı adı"
              value={dbUsername}
              onChange={(e) => setDbUsername(e.target.value)}
              required
            />
            <Input
              label="Şifre"
              type="password"
              value={dbPassword}
              onChange={(e) => setDbPassword(e.target.value)}
              required
            />
          </div>
          <Button
            type="button"
            variant="secondary"
            size="sm"
            onClick={handleTestConnection}
            disabled={testing || !dbHost || !dbPort || !dbName || !dbUsername || !dbPassword}
          >
            {testing ? "Test ediliyor…" : "Bağlantıyı Test Et"}
          </Button>
        </form>
      </Modal>
    </div>
  );
}

function MemberDashboard() {
  const navigate = useNavigate();
  const { data: projects, loading, error } = useAsync(() => projectsApi.listMine(), []);

  return (
    <div className="page">
      <div className="page__header">
        <div>
          <h1 className="page__title">Projelerim</h1>
        </div>
      </div>

      {loading && <SpinnerCenter />}
      {error && <div className="alert-banner alert-banner--error">{error}</div>}

      {!loading && !error && projects && projects.length === 0 && (
        <EmptyState
          title="Henüz bir projeye üye değilsin"
          description="Bir yöneticiden seni bir projeye eklemesini iste."
        />
      )}

      {!loading && !error && projects && projects.length > 0 && (
        <Card padded={false}>
          {projects.map((project) => (
            <div className="list-row" key={project.id}>
              <div className="list-row__main">
                <a
                  className="list-row__title"
                  href={`/projects/${project.id}`}
                  onClick={(e) => {
                    e.preventDefault();
                    navigate(`/projects/${project.id}`);
                  }}
                >
                  {project.name}
                </a>
                <div className="list-row__meta">
                  <Badge color={ROLE_COLORS[project.role]}>{ROLE_LABELS[project.role]}</Badge>
                  {project.description && <span>{project.description}</span>}
                  <span>· {formatDateTime(project.createdAt)}</span>
                </div>
                <ProjectStatsBadges projectId={project.id} />
              </div>
              <div className="list-row__actions">
                <Button size="sm" variant="secondary" onClick={() => navigate(`/projects/${project.id}`)}>
                  Aç
                </Button>
              </div>
            </div>
          ))}
        </Card>
      )}
    </div>
  );
}
