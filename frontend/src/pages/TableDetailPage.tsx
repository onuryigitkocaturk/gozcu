import { useNavigate, useParams } from "react-router-dom";
import { useToast } from "../context/ToastContext";
import { useAsync } from "../hooks/useAsync";
import { useMyProjectRole } from "../hooks/useMyProjectRole";
import { projectsApi } from "../api/projects";
import { queriesApi } from "../api/queries";
import { ApiError } from "../api/client";
import { Badge, Button, Card, CardHeader, DataTable, EmptyState, SpinnerCenter } from "../components/ui";

export function TableDetailPage() {
  const { projectId, tableId } = useParams();
  const pId = projectId as string;
  const tId = tableId as string;
  const { isAtLeastDeveloper, isAtLeastMaintainer } = useMyProjectRole(pId);
  const { notifySuccess, notifyError } = useToast();
  const navigate = useNavigate();

  const { data: tables } = useAsync(() => projectsApi.listTables(pId), [pId]);
  const currentTable = tables?.find((t) => t.id === tId);

  const {
    data: rows,
    loading: loadingRows,
    error: rowsError,
  } = useAsync(() => (currentTable ? projectsApi.getTableData(pId, currentTable.tableName) : Promise.resolve([])), [
    pId,
    currentTable?.tableName,
  ]);

  const {
    data: queries,
    loading: loadingQueries,
    error: queriesError,
    reload: reloadQueries,
  } = useAsync(() => queriesApi.list(pId, tId), [pId, tId]);

  const handleDeleteQuery = async (queryId: string, name: string) => {
    if (!confirm(`"${name}" sorgusunu silmek istediğine emin misin?`)) return;
    try {
      await queriesApi.remove(pId, tId, queryId);
      notifySuccess("Sorgu silindi.");
      reloadQueries();
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Sorgu silinemedi.");
    }
  };

  return (
    <div className="page">
      <div className="breadcrumbs">
        <a href="/" onClick={(e) => (e.preventDefault(), navigate("/"))}>
          Projeler
        </a>
        <span>/</span>
        <a href={`/projects/${pId}`} onClick={(e) => (e.preventDefault(), navigate(`/projects/${pId}`))}>
          {currentTable?.projectName ?? "Proje"}
        </a>
        <span>/</span>
        <span>{currentTable?.tableName ?? `Tablo #${tId}`}</span>
      </div>

      <div className="page__header">
        <div>
          <h1 className="page__title">{currentTable?.tableName ?? "Tablo"}</h1>
        </div>
      </div>

      <Card style={{ marginBottom: 20 }}>
        <CardHeader title="Veri Önizleme" />
        {loadingRows && <SpinnerCenter />}
        {rowsError && <div className="alert-banner alert-banner--error">{rowsError}</div>}
        {!loadingRows && !rowsError && rows && <DataTable rows={rows} emptyLabel="Bu tabloda satır yok" />}
      </Card>

      <Card padded={false}>
        <div style={{ padding: "16px 20px 0" }}>
          <CardHeader
            title="Sorgular"
            action={
              isAtLeastDeveloper && (
                <Button size="sm" variant="primary" onClick={() => navigate(`/projects/${pId}/tables/${tId}/queries/new`)}>
                  + Yeni Sorgu
                </Button>
              )
            }
          />
        </div>
        {loadingQueries && <SpinnerCenter />}
        {queriesError && <div className="alert-banner alert-banner--error">{queriesError}</div>}
        {!loadingQueries && !queriesError && queries && queries.length === 0 && (
          <EmptyState
            title="Henüz sorgu yok"
            description="Sürükle-bırak sorgu oluşturucuyla bu tablo için bir kontrol sorgusu tanımla."
          />
        )}
        {!loadingQueries &&
          queries &&
          queries.map((q) => (
            <div className="list-row" key={q.id}>
              <div className="list-row__main">
                <a
                  className="list-row__title"
                  href={`/projects/${pId}/tables/${tId}/queries/${q.id}`}
                  onClick={(e) => (e.preventDefault(), navigate(`/projects/${pId}/tables/${tId}/queries/${q.id}`))}
                >
                  {q.name}
                </a>
                <div className="list-row__meta">
                  <Badge color="blue">{q.frequency === "HOURLY" ? "Saatlik" : "Günlük"}</Badge>
                  <Badge color={q.active ? "green" : "neutral"}>{q.active ? "Aktif" : "Pasif"}</Badge>
                  <span>Oluşturan: {q.createdByUsername ?? "Hesabı silinmiş"}</span>
                </div>
              </div>
              <div className="list-row__actions">
                <Button
                  size="sm"
                  variant="secondary"
                  onClick={() => navigate(`/projects/${pId}/tables/${tId}/queries/${q.id}`)}
                >
                  Aç
                </Button>
                {isAtLeastMaintainer && (
                  <Button size="sm" variant="danger" onClick={() => handleDeleteQuery(q.id, q.name)}>
                    Sil
                  </Button>
                )}
              </div>
            </div>
          ))}
      </Card>
    </div>
  );
}
