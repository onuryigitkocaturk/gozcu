import { useState } from "react";
import { useToast } from "../../context/ToastContext";
import { useAsync } from "../../hooks/useAsync";
import { groupsApi } from "../../api/groups";
import { usersApi } from "../../api/users";
import { ApiError } from "../../api/client";
import { Button, Card, CardHeader, EmptyState, Input, Modal, SpinnerCenter } from "../../components/ui";
import { InlineSelect } from "../../components/ui/Field";
import { formatDateTime } from "../../utils/format";

export function GroupsPage() {
  const { notifySuccess, notifyError } = useToast();
  const { data: groups, loading, error, reload } = useAsync(() => groupsApi.list(), []);

  const [createOpen, setCreateOpen] = useState(false);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [saving, setSaving] = useState(false);

  const [membersOpenFor, setMembersOpenFor] = useState<number | null>(null);

  const handleCreate = async () => {
    if (!name.trim()) return;
    setSaving(true);
    try {
      await groupsApi.create({ name, description: description || undefined });
      notifySuccess(`"${name}" grubu oluşturuldu.`);
      setCreateOpen(false);
      setName("");
      setDescription("");
      reload();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Grup oluşturulamadı.");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: number, groupName: string) => {
    if (!confirm(`"${groupName}" grubunu silmek istediğine emin misin?`)) return;
    try {
      await groupsApi.remove(id);
      notifySuccess("Grup silindi.");
      reload();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Grup silinemedi.");
    }
  };

  return (
    <div className="page">
      <div className="page__header">
        <div>
          <h1 className="page__title">Gruplar</h1>
          <p className="page__subtitle">Alert tetiklendiğinde mail bildirimi alacak kullanıcı grupları.</p>
        </div>
        <Button variant="primary" onClick={() => setCreateOpen(true)}>
          + Yeni Grup
        </Button>
      </div>

      {loading && <SpinnerCenter />}
      {error && <div className="alert-banner alert-banner--error">{error}</div>}

      {!loading && !error && groups && groups.length === 0 && (
        <EmptyState title="Henüz grup yok" description="İlk bildirim grubunu oluştur." />
      )}

      {!loading && !error && groups && groups.length > 0 && (
        <Card padded={false}>
          {groups.map((g) => (
            <div className="list-row" key={g.id}>
              <div className="list-row__main">
                <span className="list-row__title">{g.name}</span>
                <div className="list-row__meta">
                  {g.description && <span>{g.description}</span>}
                  <span>· {formatDateTime(g.createdAt)}</span>
                </div>
              </div>
              <div className="list-row__actions">
                <Button size="sm" variant="secondary" onClick={() => setMembersOpenFor(g.id)}>
                  Üyeler
                </Button>
                <Button size="sm" variant="danger" onClick={() => handleDelete(g.id, g.name)}>
                  Sil
                </Button>
              </div>
            </div>
          ))}
        </Card>
      )}

      <Modal
        open={createOpen}
        onClose={() => setCreateOpen(false)}
        title="Yeni Grup"
        footer={
          <>
            <Button variant="ghost" onClick={() => setCreateOpen(false)}>
              Vazgeç
            </Button>
            <Button variant="primary" onClick={handleCreate} disabled={saving || !name.trim()}>
              {saving ? "Oluşturuluyor…" : "Oluştur"}
            </Button>
          </>
        }
      >
        <Input label="Grup adı" value={name} onChange={(e) => setName(e.target.value)} autoFocus />
        <Input label="Açıklama (opsiyonel)" value={description} onChange={(e) => setDescription(e.target.value)} />
      </Modal>

      {membersOpenFor !== null && (
        <GroupMembersModal groupId={membersOpenFor} onClose={() => setMembersOpenFor(null)} />
      )}
    </div>
  );
}

function GroupMembersModal({ groupId, onClose }: { groupId: number; onClose: () => void }) {
  const { notifySuccess, notifyError } = useToast();
  const {
    data: members,
    loading,
    reload,
  } = useAsync(() => groupsApi.listUsers(groupId), [groupId]);
  const { data: allUsers } = useAsync(() => usersApi.list(), []);
  const [selected, setSelected] = useState("");
  const [adding, setAdding] = useState(false);

  const candidates = allUsers?.filter((u) => !members?.some((m) => m.id === u.id)) ?? [];

  const handleAdd = async () => {
    if (!selected) return;
    setAdding(true);
    try {
      await groupsApi.addUser(groupId, Number(selected));
      notifySuccess("Kullanıcı gruba eklendi.");
      setSelected("");
      reload();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Kullanıcı eklenemedi.");
    } finally {
      setAdding(false);
    }
  };

  const handleRemove = async (userId: number, username: string) => {
    if (!confirm(`${username} kullanıcısını gruptan çıkarmak istediğine emin misin?`)) return;
    try {
      await groupsApi.removeUser(groupId, userId);
      notifySuccess("Kullanıcı gruptan çıkarıldı.");
      reload();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Kullanıcı çıkarılamadı.");
    }
  };

  return (
    <Modal open onClose={onClose} title="Grup Üyeleri">
      <CardHeader title="Yeni üye ekle" />
      <div className="flex gap-8 mb-16">
        <InlineSelect value={selected} onChange={(e) => setSelected(e.target.value)}>
          <option value="">Kullanıcı seç…</option>
          {candidates.map((u) => (
            <option key={u.id} value={u.id}>
              {u.username}
            </option>
          ))}
        </InlineSelect>
        <Button variant="primary" size="sm" onClick={handleAdd} disabled={!selected || adding}>
          Ekle
        </Button>
      </div>

      <div className="divider" />

      {loading && <SpinnerCenter />}
      {!loading && members && members.length === 0 && <EmptyState title="Üye yok" />}
      {!loading &&
        members &&
        members.map((m) => (
          <div className="list-row" key={m.id} style={{ padding: "10px 0" }}>
            <div className="list-row__main">
              <span className="list-row__title">{m.username}</span>
              <div className="list-row__meta">
                <span>{m.email}</span>
              </div>
            </div>
            <Button size="sm" variant="danger" onClick={() => handleRemove(m.id, m.username)}>
              Çıkar
            </Button>
          </div>
        ))}
    </Modal>
  );
}
