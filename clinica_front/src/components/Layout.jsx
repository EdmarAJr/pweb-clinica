import Navbar from "./Navbar";
import Footer from "./Footer";

// Layout com Navbar, conteúdo e Footer.
// A estrutura com d-flex e flex-column garante que o Footer fique no final da página.
const Layout = ({ children }) => (
  <div className="d-flex flex-column min-vh-100 bg-primary-subtle">
    <Navbar />
    <main className="flex-grow-1">
      {children}
    </main>
    <Footer />
  </div>
);

export default Layout;