import React from 'react';

const Footer = () => {
  return (
    <footer className="bg-primary text-white text-center p-3 mt-auto">
      <div className="container">
        <p className="mb-0">&copy; {new Date().getFullYear()} Clínica Salvador. Todos os direitos reservados.</p>
      </div>
    </footer>
  );
};

export default Footer;
