import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useToast } from "../context/ToastContext";
import { useAsync } from "../hooks/useAsync";
import { useMyProjectRole } from "../hooks/useMyProjectRole";
import { queriesApi } from "../api/queries";
import { alertsApi } from "../api/alerts";
import { groupsApi } from "../api/groups";
import { ApiError } from "../api/client";
import {
  Badge,
  Button,
  Card,
  CardHeader,
  DataTable,
  EmptyState,
  Modal,
  Select,
  SpinnerCenter,
} from "../components/ui";
import type { AlertEvaluationResult, AlertLogResponse, ConditionOperator, TableRow } from "../types/api";
import { OPERATOR_LABELS } from "../components/querybuilder/builderTypes";
import { formatDateTime } from "../utils/format";

const ALERT_OPERATORS: ConditionOperator[] = [
  "EQUALS",
  "NOT_EQUALS",
  "GREATER_THAN",
  "GREATER_THAN_OR_EQUAL",
  "LESS_THAN",
  "LESS_THAN_OR_EQUAL",
];

export function QueryDetailPage() {
  const { projectId, tableId, queryId } = useParams();
  const pId = projectId as string;
  const tId = tableId as string;
  const qId = queryId as string;
  const { isAtLeastDeveloper, isAtLeastMaintainer } = useMyProjectRole(pId);
  const { notifySuccess, notifyError } = useToast();
  const navigate = useNavigate();

  const { data: queries } = useAsync(() => queriesApi.list(pId, tId), [pId, tId]);
  const query = queries?.find((q) => q.id === qId);

  const [runResult, setRunResult] = useState<TableRow[] | null>(null);
  const [runningAction, setRunningAction] = useState<"run" | "count" | null>(null);
  const [countResult, setCountResult] = useState<number | null>(null);

  const {
    data: alerts,
    loading: loadingAlerts,
    reload: reloadAlerts,
  } = useAsync(() => alertsApi.list(pId, tId, qId), [pId, tId, qId]);

  const [addAlertOpen, setAddAlertOpen] = useState(false);

  const handleRun = async () => {
    setRunningAction("run");
    setCountResult(null);
    try {
      const rows = await queriesApi.run(pId, tId, qId);
      setRunResult(rows);
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Sorgu çalıştırılamadı.");
    } finally {
      setRunningAction(null);
    }
  };

  const handleCount = async () => {
    setRunningAction("count");
    try {
      const { count } = await queriesApi.count(pId, tId, qId);
      setCountResult(count);
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Sayım yapılamadı.");
    } finally {
      setRunningAction(null);
    }
  };

  const handleDeleteAlert = async (alertId: string) => {
    if (!confirm("Bu alert'i silmek istediğine emin misin?")) return;
    try {
      await alertsApi.remove(pId, tId, qId, alertId);
      notifySuccess("Alert silindi.");
      reloadAlerts();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Alert silinemedi.");
    }
  };

  return (
    <div className="page">
      <div className="breadcrumbs">
        <a
          href={`/projects/${pId}/tables/${tId}`}
          onClick={(e) => (e.preventDefault(), navigate(`/projects/${pId}/tables/${tId}`))}
        >
          {query?.tableName ?? "Tablo"}
        </a>
        <span>/</span>
        <span>{query?.name ?? `Sorgu #${qId}`}</span>
      </div>

      <div className="page__header">
        <div>
          <h1 className="page__title">{query?.name ?? "Sorgu"}</h1>
          <div className="list-row__meta mt-8">
            {query && (
              <>
                <Badge color="blue">{query.frequency === "HOURLY" ? "Saatlik" : "Günlük"}</Badge>
                <Badge color={query.active ? "green" : "neutral"}>{query.active ? "Aktif" : "Pasif"}</Badge>
                <span>Oluşturan: {query.createdByUsername ?? "Bilinmiyor"}</span>
              </>
            )}
          </div>
        </div>
        <div className="flex gap-8">
          <Button variant="secondary" onClick={handleCount} disabled={runningAction !== null}>
            {runningAction === "count" ? "Sayılıyor…" : "Say"}
          </Button>
          <Button variant="primary" onClick={handleRun} disabled={runningAction !== null}>
            {runningAction === "run" ? "Çalıştırılıyor…" : "Çalıştır"}
          </Button>
        </div>
      </div>

      {countResult !== null && (
        <div className="alert-banner alert-banner--info">Eşleşen satır sayısı: <strong>{countResult}</strong></div>
      )}

      {runResult !== null && (
        <Card style={{ marginBottom: 20 }}>
          <CardHeader title={`Sonuçlar (${runResult.length} satır)`} />
          <DataTable rows={runResult} emptyLabel="Bu koşula uyan satır yok" />
        </Card>
      )}

      <Card padded={false}>
        <div style={{ padding: "16px 20px 0" }}>
          <CardHeader
            title="Alertler"
            action={
              isAtLeastDeveloper && (
                <Button size="sm" variant="primary" onClick={() => setAddAlertOpen(true)}>
                  + Alert Ekle
                </Button>
              )
            }
          />
        </div>
        {loadingAlerts && <SpinnerCenter />}
        {!loadingAlerts && alerts && alerts.length === 0 && (
          <EmptyState
            title="Henüz alert yok"
            description="Bu sorgunun sonucunu bir eşiğe göre değerlendirip bir gruba mail atacak bir alert tanımla."
          />
        )}
        {!loadingAlerts &&
          alerts &&
          alerts.map((a) => (
            <AlertRow key={a.id} projectId={pId} tableId={tId} queryId={qId} alert={a} onDelete={handleDeleteAlert} isAdmin={isAtLeastMaintainer} />
          ))}
      </Card>

      {isAtLeastDeveloper && (
        <AddAlertModal
          open={addAlertOpen}
          onClose={() => setAddAlertOpen(false)}
          projectId={pId}
          tableId={tId}
          queryId={qId}
          onAdded={() => {
            setAddAlertOpen(false);
            reloadAlerts();
          }}
        />
      )}
    </div>
  );
}

function AlertRow({
  projectId,
  tableId,
  queryId,
  alert,
  onDelete,
  isAdmin,
}: {
  projectId: string;
  tableId: string;
  queryId: string;
  alert: { id: string; groupName: string; condition: { metric: string; operator: ConditionOperator; value: number }; active: boolean };
  onDelete: (alertId: string) => void;
  isAdmin: boolean;
}) {
  const { notifyError } = useToast();
  const [evaluating, setEvaluating] = useState(false);
  const [result, setResult] = useState<AlertEvaluationResult | null>(null);
  const [logsOpen, setLogsOpen] = useState(false);
  const [logs, setLogs] = useState<AlertLogResponse[] | null>(null);
  const [loadingLogs, setLoadingLogs] = useState(false);

  const handleEvaluate = async () => {
    setEvaluating(true);
    try {
      const r = await alertsApi.evaluate(projectId, tableId, queryId, alert.id);
      setResult(r);
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Değerlendirme başarısız oldu.");
    } finally {
      setEvaluating(false);
    }
  };

  const handleOpenLogs = async () => {
    setLogsOpen(true);
    setLoadingLogs(true);
    try {
      const l = await alertsApi.logs(projectId, tableId, queryId, alert.id);
      setLogs(l);
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Loglar alınamadı.");
    } finally {
      setLoadingLogs(false);
    }
  };

  return (
    <>
      <div className="list-row">
        <div className="list-row__main">
          <span className="list-row__title">
            {alert.condition.metric === "ROW_COUNT" ? "Eşleşen satır sayısı" : alert.condition.metric}{" "}
            {OPERATOR_LABELS[alert.condition.operator]} {alert.condition.value}
          </span>
          <div className="list-row__meta">
            <span>Bildirim grubu: {alert.groupName}</span>
            {result && (
              <Badge color={result.triggered ? "red" : "green"}>
                {result.triggered ? `Tetiklendi (${result.matchCount})` : `Tetiklenmedi (${result.matchCount})`}
              </Badge>
            )}
          </div>
        </div>
        <div className="list-row__actions">
          <Button size="sm" variant="secondary" onClick={handleEvaluate} disabled={evaluating}>
            {evaluating ? "…" : "Değerlendir"}
          </Button>
          <Button size="sm" variant="ghost" onClick={handleOpenLogs}>
            Loglar
          </Button>
          {isAdmin && (
            <Button size="sm" variant="danger" onClick={() => onDelete(alert.id)}>
              Sil
            </Button>
          )}
        </div>
      </div>

      <Modal open={logsOpen} onClose={() => setLogsOpen(false)} title="Alert Logları">
        {loadingLogs && <SpinnerCenter />}
        {!loadingLogs && logs && logs.length === 0 && <EmptyState title="Henüz log yok" />}
        {!loadingLogs && logs && logs.length > 0 && (
          <div className="flex flex-col gap-8">
            {logs
              .slice()
              .reverse()
              .map((log) => (
                <div key={log.id} className="card card--pad" style={{ padding: 12 }}>
                  <div className="flex justify-between items-center mb-16" style={{ marginBottom: 6 }}>
                    <Badge
                      color={log.status === "TRIGGERED" ? "red" : log.status === "ERROR" ? "amber" : "green"}
                    >
                      {log.status}
                    </Badge>
                    <span className="text-sm text-muted">{formatDateTime(log.executedAt)}</span>
                  </div>
                  <div className="text-sm">{log.message}</div>
                </div>
              ))}
          </div>
        )}
      </Modal>
    </>
  );
}

function AddAlertModal({
  open,
  onClose,
  projectId,
  tableId,
  queryId,
  onAdded,
}: {
  open: boolean;
  onClose: () => void;
  projectId: string;
  tableId: string;
  queryId: string;
  onAdded: () => void;
}) {
  const { notifySuccess, notifyError } = useToast();
  const { data: groups, loading } = useAsync(() => (open ? groupsApi.list() : Promise.resolve([])), [open]);
  const [groupId, setGroupId] = useState("");
  const [operator, setOperator] = useState<ConditionOperator>("GREATER_THAN");
  const [value, setValue] = useState("0");
  const [saving, setSaving] = useState(false);

  const handleAdd = async () => {
    if (!groupId) return;
    setSaving(true);
    try {
      await alertsApi.create(projectId, tableId, queryId, {
        groupId,
        condition: { metric: "ROW_COUNT", operator, value: Number(value) },
      });
      notifySuccess("Alert oluşturuldu.");
      setGroupId("");
      onAdded();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Alert oluşturulamadı.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Alert Ekle"
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>
            Vazgeç
          </Button>
          <Button variant="primary" onClick={handleAdd} disabled={!groupId || saving}>
            {saving ? "Ekleniyor…" : "Ekle"}
          </Button>
        </>
      }
    >
      {loading ? (
        <SpinnerCenter />
      ) : (
        <>
          <Select label="Bildirim grubu" value={groupId} onChange={(e) => setGroupId(e.target.value)}>
            <option value="">Bir grup seç…</option>
            {groups?.map((g) => (
              <option key={g.id} value={g.id}>
                {g.name}
              </option>
            ))}
          </Select>
          <p className="text-sm text-muted mb-16">Koşul: eşleşen satır sayısı…</p>
          <div className="form-row">
            <Select label="İşleç" value={operator} onChange={(e) => setOperator(e.target.value as ConditionOperator)}>
              {ALERT_OPERATORS.map((op) => (
                <option key={op} value={op}>
                  {OPERATOR_LABELS[op]}
                </option>
              ))}
            </Select>
            <div className="field">
              <label className="field__label">Eşik değer</label>
              <input className="input" type="number" value={value} onChange={(e) => setValue(e.target.value)} />
            </div>
          </div>
        </>
      )}
    </Modal>
  );
}
