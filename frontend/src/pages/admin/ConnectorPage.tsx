import { useState } from "react";
import { useAsync } from "../../hooks/useAsync";
import { connectorApi } from "../../api/connector";
import { Badge, Card, CardHeader, EmptyState, SpinnerCenter } from "../../components/ui";

export function ConnectorPage() {
  const { data: tables, loading, error } = useAsync(() => connectorApi.listTables(), []);
  const [selected, setSelected] = useState<string | null>(null);

  const {
    data: columns,
    loading: loadingColumns,
  } = useAsync(() => (selected ? connectorApi.listColumns(selected) : Promise.resolve([])), [selected]);

  return (
    <div className="page">
      <div className="page__header">
        <div>
          <h1 className="page__title">İzlenen Veritabanı</h1>
          <p className="page__subtitle">
            Mock veritabanındaki tüm tablolar — bunları bir projeye bağlamak için proje sayfasındaki
            "Tablo Ekle" özelliğini kullan.
          </p>
        </div>
      </div>

      {loading && <SpinnerCenter />}
      {error && <div className="alert-banner alert-banner--error">{error}</div>}

      {!loading && !error && tables && tables.length === 0 && (
        <EmptyState title="İzlenen veritabanında hiç tablo yok" />
      )}

      {!loading && !error && tables && tables.length > 0 && (
        <div className="grid grid--cols-2">
          {tables.map((name) => (
            <Card key={name} onClick={() => setSelected(name)} style={{ cursor: "pointer" }}>
              <CardHeader title={name} action={selected === name && <Badge color="blue">seçili</Badge>} />
              {selected === name && (
                <>
                  {loadingColumns && <SpinnerCenter />}
                  {!loadingColumns && columns && (
                    <div className="flex flex-col gap-6">
                      {columns.map((col) => (
                        <span key={col} className="mono text-sm text-muted">
                          {col}
                        </span>
                      ))}
                    </div>
                  )}
                </>
              )}
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
