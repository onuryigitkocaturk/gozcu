import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import { useAsync } from "../hooks/useAsync";
import { projectsApi } from "../api/projects";
import { ApiError } from "../api/client";
import { Button, Card, CardHeader, EmptyState, Input, Modal, SpinnerCenter } from "../components/ui";
import { formatDateTime } from "../utils/format";

export function DashboardPage() {
  const { isAdmin } = useAuth();
  return isAdmin ? <AdminDashboard /> : <MemberDashboard />;
}

function AdminDashboard() {
  const navigate = useNavigate();
  const { notifySuccess, notifyError } = useToast();
  const { data: projects, loading, error, reload } = useAsync(() => projectsApi.list(), []);
  const [modalOpen, setModalOpen] = useState(false);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [saving, setSaving] = useState(false);

  const handleCreate = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await projectsApi.create({ name, description: description || undefined });
      notifySuccess(`"${name}" projesi oluşturuldu.`);
      setModalOpen(false);
      setName("");
      setDescription("");
      reload();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Proje oluşturulamadı.");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: number, projectName: string) => {
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
          <p className="page__subtitle">İzlenen tabloları ve sorguları organize ettiğin çalışma alanları.</p>
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
        </form>
      </Modal>
    </div>
  );
}

function MemberDashboard() {
  const navigate = useNavigate();
  const [projectId, setProjectId] = useState("");

  const handleGo = (e: FormEvent) => {
    e.preventDefault();
    const id = Number(projectId);
    if (id > 0) navigate(`/projects/${id}`);
  };

  return (
    <div className="page">
      <div className="page__header">
        <div>
          <h1 className="page__title">Projelerim</h1>
          <p className="page__subtitle">
            Bir projeye eriştiğinde burada işlem yapabilirsin. Hangi projeye üye olduğunu bilmiyorsan
            yöneticinden proje ID'sini öğrenebilirsin.
          </p>
        </div>
      </div>

      <Card style={{ maxWidth: 420 }}>
        <CardHeader title="Proje ID ile git" />
        <form onSubmit={handleGo} className="flex gap-8">
          <Input
            placeholder="Örn. 1"
            type="number"
            min={1}
            value={projectId}
            onChange={(e) => setProjectId(e.target.value)}
            style={{ marginBottom: 0 }}
          />
          <Button type="submit" variant="primary" disabled={!projectId}>
            Git
          </Button>
        </form>
      </Card>
    </div>
  );
}
