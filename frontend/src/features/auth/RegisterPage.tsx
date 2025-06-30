import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useAppDispatch, useAppSelector } from '../../app/hooks';
import { loginStart, loginSuccess, loginFailure } from './authSlice';
import api from '../../services/api';
import { useNavigate } from 'react-router-dom';

interface RegisterFormInputs {
  name: string;
  email: string;
  password: string;
}

const schema = yup.object().shape({
  name: yup.string().min(2, 'Mínimo 2 caracteres').required('Nombre requerido'),
  email: yup.string().email('Email inválido').required('Email requerido'),
  password: yup.string().min(6, 'Mínimo 6 caracteres').required('Contraseña requerida'),
});

const RegisterPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { loading, error } = useAppSelector((state) => state.auth);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormInputs>({
    resolver: yupResolver(schema),
  });

  const onSubmit = async (data: RegisterFormInputs) => {
    dispatch(loginStart());
    try {
      const response = await api.post('/auth/register', data);
      dispatch(
        loginSuccess({
          token: response.data.token,
          user: {
            id: response.data.id,
            name: response.data.name,
            email: response.data.email,
            role: response.data.role,
          },
        })
      );
      navigate('/');
    } catch (err: any) {
      dispatch(loginFailure('Error al registrar usuario'));
    }
  };

  return (
    <div className="max-w-md mx-auto bg-white p-8 rounded shadow">
      <h2 className="text-2xl font-bold mb-6 text-center">Registrarse</h2>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="block mb-1">Nombre</label>
          <input
            type="text"
            {...register('name')}
            className="w-full border rounded px-3 py-2"
          />
          {errors.name && <p className="text-red-500 text-sm">{errors.name.message}</p>}
        </div>
        <div>
          <label className="block mb-1">Email</label>
          <input
            type="email"
            {...register('email')}
            className="w-full border rounded px-3 py-2"
          />
          {errors.email && <p className="text-red-500 text-sm">{errors.email.message}</p>}
        </div>
        <div>
          <label className="block mb-1">Contraseña</label>
          <input
            type="password"
            {...register('password')}
            className="w-full border rounded px-3 py-2"
          />
          {errors.password && <p className="text-red-500 text-sm">{errors.password.message}</p>}
        </div>
        {error && <p className="text-red-500 text-sm">{error}</p>}
        <button
          type="submit"
          className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700"
          disabled={loading}
        >
          {loading ? 'Registrando...' : 'Registrarse'}
        </button>
      </form>
      <div className="mt-4 text-center">
        <a href="/login" className="text-blue-600 hover:underline">¿Ya tienes cuenta? Inicia sesión</a>
      </div>
    </div>
  );
};

export default RegisterPage; 