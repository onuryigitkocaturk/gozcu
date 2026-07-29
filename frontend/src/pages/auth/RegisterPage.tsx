import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { ApiError } from "../../api/client";
import { Button, Card, Input } from "../../components/ui";

export function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await register({ username, email, password });
      navigate("/login", { replace: true });
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Kayıt başarısız oldu.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <Card className="auth-card">
        <div className="auth-card__brand">
          <strong className="auth-card__brand-text">gözcü</strong>
        </div>
        <h1 className="auth-card__title">Hesap oluşturun</h1>

        {error && <div className="alert-banner alert-banner--error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <Input
            label="Kullanıcı adı"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoFocus
            required
          />
          <Input
            label="E-posta"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <Input
            label="Şifre"
            type="password"
            minLength={8}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <Button type="submit" variant="primary" disabled={loading} style={{ width: "100%" }}>
            {loading ? "Kayıt olunuyor…" : "Kayıt olun"}
          </Button>
        </form>

        <div className="auth-card__footer">
          Zaten hesabınız var mı? <Link to="/login">Giriş yapın</Link>
        </div>

        <div className="auth-card__footer-logo">
          <img src="/turksat_logotip_cmyk-01.png" alt="Türksat" />
        </div>
      </Card>
    </div>
  );
}
