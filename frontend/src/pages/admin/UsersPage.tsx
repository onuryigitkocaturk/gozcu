import { useState } from "react";
import { useAuth } from "../../context/AuthContext";
import { useToast } from "../../context/ToastContext";
import { useAsync } from "../../hooks/useAsync";
import { usersApi } from "../../api/users";
import { ApiError } from "../../api/client";
import { Badge, Button, Card, SpinnerCenter } from "../../components/ui";
import { InlineSelect } from "../../components/ui/Field";
import type { Role } from "../../types/api";

export function UsersPage() {
  const { user: currentUser } = useAuth();
  const { notifySuccess, notifyError } = useToast();
  const { data: users, loading, error, reload } = useAsync(() => usersApi.list(), []);

  const [pendingRoles, setPendingRoles] = useState<Record<string, Role>>({});
  const [savingRoles, setSavingRoles] = useState(false);

  const handleRoleSelect = (id: string, role: Role) => {
    setPendingRoles((prev) => ({ ...prev, [id]: role }));
  };

  const handleSaveRoles = async () => {
    setSavingRoles(true);
    try {
      await Promise.all(
        Object.entries(pendingRoles).map(([id, role]) => usersApi.changeRole(id, { role })),
      );
      notifySuccess("Değişiklikler kaydedildi.");
      setPendingRoles({});
      reload();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Değişiklikler kaydedilemedi.");
    } finally {
      setSavingRoles(false);
    }
  };

  const [confirmingId, setConfirmingId] = useState<string | null>(null);

  const handleDelete = async (id: string, username: string) => {
    if (!confirm(`${username} kullanıcısını silmek istediğine emin misin?`)) return;
    setConfirmingId(id);
    try {
      await usersApi.remove(id);
      notifySuccess("Kullanıcı silindi.");
      reload();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Kullanıcı silinemedi.");
    } finally {
      setConfirmingId(null);
    }
  };

  return (
    <div className="page">
      <div className="page__header">
        <div>
          <h1 className="page__title">Kullanıcılar</h1>
          <p className="page__subtitle">Sistemdeki tüm kullanıcılar ve rolleri.</p>
        </div>
      </div>

      {loading && <SpinnerCenter />}
      {error && <div className="alert-banner alert-banner--error">{error}</div>}

      {!loading && !error && users && (
        <Card padded={false}>
          {users.map((u) => (
            <div className="list-row" key={u.id}>
              <div className="list-row__main">
                <span className="list-row__title">{u.username}</span>
                <div className="list-row__meta">
                  <span>{u.email}</span>
                  <Badge color={u.role === "ADMIN" ? "blue" : "neutral"}>{u.role}</Badge>
                </div>
              </div>
              <div className="list-row__actions">
                <InlineSelect
                  value={pendingRoles[u.id] ?? u.role}
                  onChange={(e) => handleRoleSelect(u.id, e.target.value as Role)}
                  style={{ minWidth: 110 }}
                  disabled={u.id === currentUser?.id}
                >
                  <option value="USER">USER</option>
                  <option value="ADMIN">ADMIN</option>
                </InlineSelect>
                <Button
                  size="sm"
                  variant="danger"
                  onClick={() => handleDelete(u.id, u.username)}
                  disabled={confirmingId === u.id}
                >
                  Sil
                </Button>
              </div>
            </div>
          ))}
        </Card>
      )}

      {!loading && !error && users && Object.keys(pendingRoles).length > 0 && (
        <div className="page__footer-actions">
          <Button variant="primary" onClick={handleSaveRoles} disabled={savingRoles}>
            {savingRoles ? "Kaydediliyor…" : "Değişiklikleri Kaydet"}
          </Button>
        </div>
      )}
    </div>
  );
}
