import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { Button } from "../ui";

function SidebarLink({ to, icon, children }: { to: string; icon: string; children: string }) {
  return (
    <NavLink to={to} end={to === "/"} className={({ isActive }) => `sidebar__link${isActive ? " active" : ""}`}>
      <span aria-hidden>{icon}</span>
      {children}
    </NavLink>
  );
}

export function AppShell() {
  const { user, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  const initials = user?.username ? user.username.slice(0, 2).toUpperCase() : "?";

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="sidebar__brand">
          <span className="sidebar__brand-mark">Q</span>
          Query Monitor
        </div>

        <div className="sidebar__section-label">Genel</div>
        <SidebarLink to="/" icon="📁">
          Projelerim
        </SidebarLink>

        {isAdmin && (
          <>
            <div className="sidebar__section-label">Yönetim</div>
            <SidebarLink to="/admin/users" icon="👤">
              Kullanıcılar
            </SidebarLink>
            <SidebarLink to="/admin/groups" icon="👥">
              Gruplar
            </SidebarLink>
            <SidebarLink to="/admin/connector" icon="🗄️">
              İzlenen Veritabanı
            </SidebarLink>
          </>
        )}

        <div className="sidebar__footer">
          <Button variant="ghost" size="sm" onClick={handleLogout} style={{ width: "100%" }}>
            Çıkış yap
          </Button>
        </div>
      </aside>

      <div className="main-area">
        <header className="topbar">
          <div className="topbar__title">Query Monitor</div>
          <div className="topbar__user">
            <span>{user?.username}</span>
            <span className="avatar">{initials}</span>
          </div>
        </header>
        <Outlet />
      </div>
    </div>
  );
}
