import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { Button } from "../ui";

function SidebarLink({ to, children }: { to: string; children: string }) {
  return (
    <NavLink to={to} end={to === "/"} className={({ isActive }) => `sidebar__link${isActive ? " active" : ""}`}>
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

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="sidebar__brand">
          gözcü
          <img src="/turksat_logotip_cmyk-01.png" alt="Türksat" className="brand-logo" />
        </div>

        <div className="sidebar__section-label">Genel</div>
        <SidebarLink to="/">
          Projelerim
        </SidebarLink>

        {isAdmin && (
          <>
            <div className="sidebar__section-label">Yönetim</div>
            <SidebarLink to="/admin/users">
              Kullanıcılar
            </SidebarLink>
            <SidebarLink to="/admin/groups">
              Gruplar
            </SidebarLink>
            <SidebarLink to="/admin/connector">
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
          <div className="topbar__user">
            <span>{user?.username}</span>
          </div>
        </header>
        <Outlet />
      </div>
    </div>
  );
}
