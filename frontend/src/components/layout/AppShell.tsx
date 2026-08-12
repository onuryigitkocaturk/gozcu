import { useEffect, useRef, useState } from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

function SidebarLink({ to, children }: { to: string; children: string }) {
  return (
    <NavLink to={to} end={to === "/"} className={({ isActive }) => `sidebar__link${isActive ? " active" : ""}`}>
      {children}
    </NavLink>
  );
}

function UserMenu() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!open) return;
    const handleClickOutside = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [open]);

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <div className="topbar__user" ref={menuRef}>
      <button type="button" className="topbar__user-trigger" onClick={() => setOpen((v) => !v)}>
        {user?.username}
      </button>
      {open && (
        <div className="user-dropdown">
          <button type="button" className="user-dropdown__item" onClick={handleLogout}>
            Çıkış yap
          </button>
        </div>
      )}
    </div>
  );
}

export function AppShell() {
  const { isAdmin } = useAuth();

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="sidebar__brand">gözcü</div>

        <div className="sidebar__section-label">Genel</div>
        <SidebarLink to="/">
          Projelerim
        </SidebarLink>
        <SidebarLink to="/about">
          Sistem Hakkında
        </SidebarLink>

        {isAdmin && (
          <>
            <div className="sidebar__section-label">Yönetim</div>
            <SidebarLink to="/admin/users">
              Kullanıcılar
            </SidebarLink>
            <SidebarLink to="/admin/groups">
              Mail Grupları
            </SidebarLink>
            <SidebarLink to="/admin/connector">
              Bağlantı Testi
            </SidebarLink>
          </>
        )}

        <div className="sidebar__section-label">Hesap</div>
        <SidebarLink to="/account">
          Hesabım
        </SidebarLink>

        <div className="sidebar__footer">
          <img src="/turksat_logotip_cmyk-01.png" alt="Türksat" className="brand-logo" />
        </div>
      </aside>

      <div className="main-area">
        <header className="topbar">
          <UserMenu />
        </header>
        <Outlet />
      </div>
    </div>
  );
}
