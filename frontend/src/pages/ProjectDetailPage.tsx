import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import { useAsync } from "../hooks/useAsync";
import { projectsApi } from "../api/projects";
import { connectorApi } from "../api/connector";
import { usersApi } from "../api/users";
import { ApiError } from "../api/client";
import { Badge, Button, Card, CardHeader, EmptyState, Modal, Select, SpinnerCenter } from "../components/ui";
import { formatDateTime } from "../utils/format";

export function ProjectDetailPage() {
  const { projectId } = useParams();
  const id = Number(projectId);
  const { isAdmin } = useAuth();
  const { notifySuccess, notifyError } = useToast();
  const navigate = useNavigate();

  const [tab, setTab] = useState<"tables" | "members">("tables");

  const {
    data: tables,
    loading: loadingTables,
    error: tablesError,
    reload: reloadTables,
  } = useAsync(() => projectsApi.listTables(id), [id]);

  const {
    data: members,
    loading: loadingMembers,
    error: membersError,
    reload: reloadMembers,
  } = useAsync(() => projectsApi.listUsers(id), [id]);

  const [addTableOpen, setAddTableOpen] = useState(false);
  const [addMemberOpen, setAddMemberOpen] = useState(false);

  const handleRemoveTable = async (tableName: string) => {
    notifyError("Tablo çıkarma henüz desteklenmiyor — bunu yakında ekleyeceğiz.");
    void tableName;
  };

  const handleRemoveMember = async (userId: number, username: string) => {
    if (!confirm(`${username} kullanıcısını bu projeden çıkarmak istediğine emin misin?`)) return;
    try {
      await projectsApi.removeUser(id, userId);
      notifySuccess(`${username} projeden çıkarıldı.`);
      reloadMembers();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Kullanıcı çıkarılamadı.");
    }
  };

  return (
    <div className="page">
      <div className="breadcrumbs">
        <a
          href="/"
          onClick={(e) => {
            e.preventDefault();
            navigate("/");
          }}
        >
          Projeler
        </a>
        <span>/</span>
        <span>Proje #{id}</span>
      </div>

      <div className="page__header">
        <div>
          <h1 className="page__title">Proje #{id}</h1>
          <p className="page__subtitle">Bu projeye bağlı tablolar, sorgular ve üyeler.</p>
        </div>
      </div>

      <div className="tabs">
        <button className={`tab ${tab === "tables" ? "active" : ""}`} onClick={() => setTab("tables")}>
          Tablolar
        </button>
        <button className={`tab ${tab === "members" ? "active" : ""}`} onClick={() => setTab("members")}>
          Üyeler
        </button>
      </div>

      {tab === "tables" && (
        <Card padded={false}>
          <div style={{ padding: "16px 20px 0" }}>
            <CardHeader
              title="Bağlı Tablolar"
              action={
                isAdmin && (
                  <Button size="sm" variant="primary" onClick={() => setAddTableOpen(true)}>
                    + Tablo Ekle
                  </Button>
                )
              }
            />
          </div>
          {loadingTables && <SpinnerCenter />}
          {tablesError && <div className="alert-banner alert-banner--error">{tablesError}</div>}
          {!loadingTables && !tablesError && tables && tables.length === 0 && (
            <EmptyState
              title="Henüz tablo bağlanmadı"
              description="İzlenen veritabanından bir tablo seçip bu projeye bağlayarak sorgu oluşturmaya başlayabilirsin."
            />
          )}
          {!loadingTables &&
            tables &&
            tables.map((t) => (
              <div className="list-row" key={t.id}>
                <div className="list-row__main">
                  <a
                    className="list-row__title"
                    href={`/projects/${id}/tables/${t.id}`}
                    onClick={(e) => {
                      e.preventDefault();
                      navigate(`/projects/${id}/tables/${t.id}`);
                    }}
                  >
                    {t.tableName}
                  </a>
                  <div className="list-row__meta">
                    <span>Bağlandı: {formatDateTime(t.createdAt)}</span>
                  </div>
                </div>
                <div className="list-row__actions">
                  <Button size="sm" variant="secondary" onClick={() => navigate(`/projects/${id}/tables/${t.id}`)}>
                    Aç
                  </Button>
                  {isAdmin && (
                    <Button size="sm" variant="danger" onClick={() => handleRemoveTable(t.tableName)}>
                      Çıkar
                    </Button>
                  )}
                </div>
              </div>
            ))}
        </Card>
      )}

      {tab === "members" && (
        <Card padded={false}>
          <div style={{ padding: "16px 20px 0" }}>
            <CardHeader
              title="Proje Üyeleri"
              action={
                isAdmin && (
                  <Button size="sm" variant="primary" onClick={() => setAddMemberOpen(true)}>
                    + Üye Ekle
                  </Button>
                )
              }
            />
          </div>
          {loadingMembers && <SpinnerCenter />}
          {membersError && <div className="alert-banner alert-banner--error">{membersError}</div>}
          {!loadingMembers && !membersError && members && members.length === 0 && (
            <EmptyState title="Henüz üye eklenmedi" />
          )}
          {!loadingMembers &&
            members &&
            members.map((u) => (
              <div className="list-row" key={u.id}>
                <div className="list-row__main">
                  <span className="list-row__title">{u.username}</span>
                  <div className="list-row__meta">
                    <span>{u.email}</span>
                    <Badge color={u.role === "ADMIN" ? "blue" : "neutral"}>{u.role}</Badge>
                  </div>
                </div>
                {isAdmin && (
                  <div className="list-row__actions">
                    <Button size="sm" variant="danger" onClick={() => handleRemoveMember(u.id, u.username)}>
                      Çıkar
                    </Button>
                  </div>
                )}
              </div>
            ))}
        </Card>
      )}

      {isAdmin && (
        <AddTableModal
          open={addTableOpen}
          onClose={() => setAddTableOpen(false)}
          projectId={id}
          onAdded={() => {
            setAddTableOpen(false);
            reloadTables();
          }}
        />
      )}
      {isAdmin && (
        <AddMemberModal
          open={addMemberOpen}
          onClose={() => setAddMemberOpen(false)}
          projectId={id}
          existingMemberIds={members?.map((m) => m.id) ?? []}
          onAdded={() => {
            setAddMemberOpen(false);
            reloadMembers();
          }}
        />
      )}
    </div>
  );
}

function AddTableModal({
  open,
  onClose,
  projectId,
  onAdded,
}: {
  open: boolean;
  onClose: () => void;
  projectId: number;
  onAdded: () => void;
}) {
  const { notifySuccess, notifyError } = useToast();
  const { data: allTables, loading } = useAsync(() => (open ? connectorApi.listTables() : Promise.resolve([])), [
    open,
  ]);
  const [selected, setSelected] = useState("");
  const [saving, setSaving] = useState(false);

  const handleAdd = async () => {
    if (!selected) return;
    setSaving(true);
    try {
      await projectsApi.addTable(projectId, { tableName: selected });
      notifySuccess(`"${selected}" tablosu projeye bağlandı.`);
      setSelected("");
      onAdded();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Tablo eklenemedi.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Tablo Ekle"
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>
            Vazgeç
          </Button>
          <Button variant="primary" onClick={handleAdd} disabled={!selected || saving}>
            {saving ? "Ekleniyor…" : "Ekle"}
          </Button>
        </>
      }
    >
      {loading ? (
        <SpinnerCenter />
      ) : (
        <Select label="İzlenen veritabanındaki tablolar" value={selected} onChange={(e) => setSelected(e.target.value)}>
          <option value="">Bir tablo seç…</option>
          {allTables?.map((name) => (
            <option key={name} value={name}>
              {name}
            </option>
          ))}
        </Select>
      )}
    </Modal>
  );
}

function AddMemberModal({
  open,
  onClose,
  projectId,
  existingMemberIds,
  onAdded,
}: {
  open: boolean;
  onClose: () => void;
  projectId: number;
  existingMemberIds: number[];
  onAdded: () => void;
}) {
  const { notifySuccess, notifyError } = useToast();
  const { data: allUsers, loading } = useAsync(() => (open ? usersApi.list() : Promise.resolve([])), [open]);
  const [selected, setSelected] = useState("");
  const [saving, setSaving] = useState(false);

  const candidates = allUsers?.filter((u) => !existingMemberIds.includes(u.id)) ?? [];

  const handleAdd = async () => {
    if (!selected) return;
    setSaving(true);
    try {
      await projectsApi.addUser(projectId, Number(selected));
      notifySuccess("Kullanıcı projeye eklendi.");
      setSelected("");
      onAdded();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Kullanıcı eklenemedi.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Üye Ekle"
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>
            Vazgeç
          </Button>
          <Button variant="primary" onClick={handleAdd} disabled={!selected || saving}>
            {saving ? "Ekleniyor…" : "Ekle"}
          </Button>
        </>
      }
    >
      {loading ? (
        <SpinnerCenter />
      ) : (
        <Select label="Kullanıcı" value={selected} onChange={(e) => setSelected(e.target.value)}>
          <option value="">Bir kullanıcı seç…</option>
          {candidates.map((u) => (
            <option key={u.id} value={u.id}>
              {u.username} ({u.email})
            </option>
          ))}
        </Select>
      )}
    </Modal>
  );
}
