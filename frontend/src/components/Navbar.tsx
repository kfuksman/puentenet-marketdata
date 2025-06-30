import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../app/hooks';
import { logout } from '../features/auth/authSlice';

const Navbar: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { token, user } = useAppSelector((state) => state.auth);

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  if (!token) {
    return null;
  }

  return (
    <nav className="bg-gray-800 text-white p-4">
      <div className="container mx-auto flex justify-between items-center">
        <div className="flex items-center space-x-4">
          <Link to="/" className="text-xl font-bold">
            Market Monitor
          </Link>
          <Link to="/" className="hover:text-gray-300">
            Instrumentos
          </Link>
          <Link to="/favorites" className="hover:text-gray-300">
            Favoritos
          </Link>
          {user?.role === 'ADMIN' && (
            <Link to="/admin/users" className="hover:text-gray-300">
              Usuarios
            </Link>
          )}
        </div>
        <div className="flex items-center space-x-4">
          <span className="text-sm">Hola, {user?.name}</span>
          <button
            onClick={handleLogout}
            className="bg-red-600 px-4 py-2 rounded hover:bg-red-700"
          >
            Cerrar sesión
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar; 