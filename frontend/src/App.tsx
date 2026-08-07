import { Navigate, Route, Routes } from "react-router-dom";
import { AppShell } from "./components/layout/AppShell";
import { AdminRoute, ProtectedRoute } from "./components/layout/ProtectedRoute";
import { LoginPage } from "./pages/auth/LoginPage";
import { RegisterPage } from "./pages/auth/RegisterPage";
import { DashboardPage } from "./pages/DashboardPage";
import { ProjectDetailPage } from "./pages/ProjectDetailPage";
import { TableDetailPage } from "./pages/TableDetailPage";
import { QueryBuilderPage } from "./pages/QueryBuilderPage";
import { QueryDetailPage } from "./pages/QueryDetailPage";
import { UsersPage } from "./pages/admin/UsersPage";
import { GroupsPage } from "./pages/admin/GroupsPage";
import { ConnectorPage } from "./pages/admin/ConnectorPage";

export function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<AppShell />}>
          <Route path="/" element={<DashboardPage />} />
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
