// App.tsx, uygulamanın route (sayfa) haritasını tanımlıyor — hangi URL'de hangi bileşenin gösterileceğini söylüyor.
import { Navigate, Route, Routes } from "react-router-dom";
import { AppShell } from "./components/layout/AppShell";
import { AdminRoute, ProtectedRoute } from "./components/layout/ProtectedRoute";
import { LoginPage } from "./pages/auth/LoginPage";
import { RegisterPage } from "./pages/auth/RegisterPage";
import { DashboardPage } from "./pages/DashboardPage";
import { AboutPage } from "./pages/AboutPage";
import { AccountPage } from "./pages/AccountPage";
import { ProjectDetailPage } from "./pages/ProjectDetailPage";
import { TableDetailPage } from "./pages/TableDetailPage";
import { QueryBuilderPage } from "./pages/QueryBuilderPage";
import { QueryDetailPage } from "./pages/QueryDetailPage";
import { UsersPage } from "./pages/admin/UsersPage";
import { GroupsPage } from "./pages/admin/GroupsPage";
import { ConnectorPage } from "./pages/admin/ConnectorPage";

// App, bu uygulamanın tüm root ağacını döndüren ana component. main.tsx bunu route içine koyup render ediyor.
export function App() {
  return (
    // routes bir konteyner, route "şu path için şu componenti göster kuralı"
    // navigate, redirect için component
    // Outlet, o katmanın "asıl içerik buraya gelsin" dediği yer tutucu — yani AppShell header'ı basar,
    // <Outlet /> yazdığı yere de o an eşleşen gerçek sayfa (DashboardPage vb.) render edilir.
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<AppShell />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/about" element={<AboutPage />} />
          <Route path="/account" element={<AccountPage />} />
          <Route path="/projects/:projectId" element={<ProjectDetailPage />} />
          <Route path="/projects/:projectId/tables/:tableId" element={<TableDetailPage />} />
          <Route path="/projects/:projectId/tables/:tableId/queries/new" element={<QueryBuilderPage />} />
          <Route path="/projects/:projectId/tables/:tableId/queries/:queryId/edit" element={<QueryBuilderPage />} />
          <Route path="/projects/:projectId/tables/:tableId/queries/:queryId" element={<QueryDetailPage />} />

          <Route element={<AdminRoute />}>
            <Route path="/admin/users" element={<UsersPage />} />
            <Route path="/admin/groups" element={<GroupsPage />} />
            <Route path="/admin/connector" element={<ConnectorPage />} />
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
