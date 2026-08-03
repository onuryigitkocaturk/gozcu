import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import { useAsync } from "../hooks/useAsync";
import { projectsApi } from "../api/projects";
import { usersApi } from "../api/users";
import { queriesApi } from "../api/queries";
import { ApiError } from "../api/client";
import { Badge, Button, Card, CardHeader, EmptyState, Modal, Select, SpinnerCenter } from "../components/ui";
import { formatDateTime } from "../utils/format";
import type { ProjectTableResponse } from "../types/api";

export function ProjectDetailPage() {
  const { projectId } = useParams();
  const id = projectId as string;
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

  const handleRemoveMember = async (userId: string, username: string) => {
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
              <TableAccordionRow
                key={t.id}
                projectId={id}
                table={t}
                isAdmin={isAdmin}
                onOpen={() => navigate(`/projects/${id}/tables/${t.id}`)}
                onRemove={() => handleRemoveTable(t.tableName)}
              />
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

function TableAccordionRow({
  projectId,
  table,
  isAdmin,
  onOpen,
  onRemove,
}: {
  projectId: string;
  table: ProjectTableResponse;
  isAdmin: boolean;
  onOpen: () => void;
  onRemove: () => void;
}) {
  const navigate = useNavigate();
  const [expanded, setExpanded] = useState(false);

  const {
    data: queries,
    loading: loadingQueries,
    error: queriesError,
  } = useAsync(() => (expanded ? queriesApi.list(projectId, table.id) : Promise.resolve([])), [
    expanded,
    projectId,
    table.id,
  ]);

  return (
    <div className="list-row list-row--stacked">
      <div className="list-row">
        <div className="list-row__main">
          <button
            className="list-row__title"
            style={{ background: "none", border: "none", padding: 0, cursor: "pointer", textAlign: "left" }}
            onClick={() => setExpanded((v) => !v)}
          >
            {expanded ? "▾" : "▸"} {table.tableName}
          </button>
          <div className="list-row__meta">
            <span>Bağlandı: {formatDateTime(table.createdAt)}</span>
          </div>
        </div>
        <div className="list-row__actions">
          <Button size="sm" variant="secondary" onClick={onOpen}>
            Aç
          </Button>
          {isAdmin && (
            <Button size="sm" variant="danger" onClick={onRemove}>
              Çıkar
            </Button>
          )}
        </div>
      </div>

      {expanded && (
        <div style={{ paddingLeft: 24, paddingBottom: 12 }}>
          {loadingQueries && <SpinnerCenter />}
          {queriesError && <div className="alert-banner alert-banner--error">{queriesError}</div>}
          {!loadingQueries && !queriesError && queries && queries.length === 0 && (
            <EmptyState title="Bu tabloya bağlı sorgu yok" />
          )}
          {!loadingQueries &&
            queries &&
            queries.map((q) => (
              <div className="list-row" key={q.id}>
                <div className="list-row__main">
                  <a
                    className="list-row__title"
                    href={`/projects/${projectId}/tables/${table.id}/queries/${q.id}`}
                    onClick={(e) => {
                      e.preventDefault();
                      navigate(`/projects/${projectId}/tables/${table.id}/queries/${q.id}`);
                    }}
                  >
                    {q.name}
                  </a>
                  <div className="list-row__meta">
                    <Badge color="blue">{q.frequency === "HOURLY" ? "Saatlik" : "Günlük"}</Badge>
                    <Badge color={q.active ? "green" : "neutral"}>{q.active ? "Aktif" : "Pasif"}</Badge>
                    <span>Oluşturan: {q.createdByUsername ?? "Bilinmiyor"}</span>
                  </div>
                </div>
              </div>
            ))}
        </div>
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
  projectId: string;
  onAdded: () => void;
}) {
  const { notifySuccess, notifyError } = useToast();
  const { data: allTables, loading } = useAsync(
    () => (open ? projectsApi.discoverTables(projectId) : Promise.resolve([])),
    [open, projectId],
  );
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
  projectId: string;
  existingMemberIds: string[];
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
      await projectsApi.addUser(projectId, selected);
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
