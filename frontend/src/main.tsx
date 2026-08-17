// Uygulamanın DOM'a bağlandığı, 
// routing/auth/toast context'lerini ve global stilleri doğru sırayla sarmalayıp App'i render eden giriş noktası.

// DOM: Tarayıcının bir HTML sayfasını bellekte JavaScript'ten değiştirilebilir bir nesne ağacı olarak tuttuğu temsili.

// DOM ve React'a bağlanma API'leri
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";

// react-router-dom'un url tabanlı routing altyapısı
import { BrowserRouter } from "react-router-dom";

// css dosyaları
import "./styles/theme.css";
import "./styles/components.css";
import "./styles/querybuilder.css";

// asıl route tanımlarını içeren bileşen
import { App } from "./App";
import { AuthProvider } from "./context/AuthContext";
import { ToastProvider } from "./context/ToastContext";

// "root" DOM node'unu bulup React'a devrederek, içindeki context sarmalayıcılarıyla
// (StrictMode, routing, toast, auth) birlikte App'i o node'un içine render ediyor.
createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <BrowserRouter>
      <ToastProvider>
        <AuthProvider>
          <App />
        </AuthProvider>
      </ToastProvider>
    </BrowserRouter>
  </StrictMode>,
);
