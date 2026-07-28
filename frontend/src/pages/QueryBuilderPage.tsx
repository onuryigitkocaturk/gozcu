import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useToast } from "../context/ToastContext";
import { useAsync } from "../hooks/useAsync";
import { connectorApi } from "../api/connector";
import { projectsApi } from "../api/projects";
import { queriesApi } from "../api/queries";
import { ApiError } from "../api/client";
import { Button, Card, CardHeader, Input, Select, SpinnerCenter } from "../components/ui";
import { QueryBuilder } from "../components/querybuilder/QueryBuilder";
import { createEmptyGroup, findEmptyGroup, toQueryNode, type BuilderGroup } from "../components/querybuilder/builderTypes";
import type { Frequency } from "../types/api";

export function QueryBuilderPage() {
  const { projectId, tableId } = useParams();
  const pId = Number(projectId);
  const tId = Number(tableId);
  const navigate = useNavigate();
  const { notifySuccess, notifyError } = useToast();

  const { data: tables, error: tablesError } = useAsync(() => projectsApi.listTables(pId), [pId]);
  const currentTable = tables?.find((t) => t.id === tId);

  const {
    data: columns,
    loading: loadingColumns,
    error: columnsError,
  } = useAsync(
    () => (currentTable ? connectorApi.listColumns(currentTable.tableName) : Promise.resolve([])),
    [currentTable?.tableName],
  );

  const [name, setName] = useState("");
  const [frequency, setFrequency] = useState<Frequency>("DAILY");
  const [tree, setTree] = useState<BuilderGroup>(() => createEmptyGroup());
  const [saving, setSaving] = useState(false);
  const [showJson, setShowJson] = useState(false);

  const queryNodePreview = useMemo(() => toQueryNode(tree), [tree]);
  const isEmpty = findEmptyGroup(tree);

  const handleSubmit = async () => {
    if (!name.trim()) {
      notifyError("Sorguya bir isim ver.");
      return;
    }
    if (isEmpty) {
      notifyError("Her grupta en az bir koşul olmalı.");
      return;
    }
    setSaving(true);
    try {
      const created = await queriesApi.create(pId, tId, {
        name,
        frequency,
        definition: queryNodePreview,
      });
      notifySuccess(`"${name}" sorgusu oluşturuldu.`);
      navigate(`/projects/${pId}/tables/${tId}/queries/${created.id}`);
    } catch (err) {
      notifyError(err instanceof ApiError ? err.message : "Sorgu oluşturulamadı.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="page" style={{ maxWidth: 1180 }}>
      <div className="breadcrumbs">
        <a href={`/projects/${pId}/tables/${tId}`} onClick={(e) => (e.preventDefault(), navigate(`/projects/${pId}/tables/${tId}`))}>
          {currentTable?.tableName ?? "Tablo"}
        </a>
        <span>/</span>
        <span>Yeni Sorgu</span>
      </div>

      <div className="page__header">
        <div>
          <h1 className="page__title">Yeni Sorgu Oluştur</h1>
          <p className="page__subtitle">
            Sol taraftaki kolonları sürükleyerek koşullar oluştur, VE/VEYA ile birleştir.
          </p>
        </div>
        <Button variant="primary" onClick={handleSubmit} disabled={saving}>
          {saving ? "Kaydediliyor…" : "Sorguyu Kaydet"}
        </Button>
      </div>

      <Card style={{ marginBottom: 18 }}>
        <div className="form-row">
          <Input label="Sorgu adı" value={name} onChange={(e) => setName(e.target.value)} autoFocus />
          <Select label="Çalışma sıklığı" value={frequency} onChange={(e) => setFrequency(e.target.value as Frequency)}>
            <option value="HOURLY">Saatlik</option>
            <option value="DAILY">Günlük</option>
          </Select>
        </div>
      </Card>

      {tablesError && <div className="alert-banner alert-banner--error">{tablesError}</div>}
      {columnsError && <div className="alert-banner alert-banner--error">{columnsError}</div>}

      {loadingColumns && <SpinnerCenter />}

      {!loadingColumns && columns && (
        <>
          <QueryBuilder columns={columns} value={tree} onChange={setTree} />

          <Card style={{ marginTop: 18 }}>
            <CardHeader
              title="JSON önizleme"
              action={
                <Button size="sm" variant="ghost" onClick={() => setShowJson((s) => !s)}>
                  {showJson ? "Gizle" : "Göster"}
                </Button>
              }
            />
            {showJson && <pre className="qb-json-preview">{JSON.stringify(queryNodePreview, null, 2)}</pre>}
          </Card>
        </>
      )}
    </div>
  );
}
