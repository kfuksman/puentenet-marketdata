import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../../app/hooks';
import { setFavorites, toggleFavorite } from './instrumentsSlice';
import api from '../../services/api';
import { StarIcon } from '@heroicons/react/24/solid';

const FavoritesPage: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { favorites } = useAppSelector((state) => state.instruments);
  const [loading, setLoading] = useState(true);

  const fetchFavorites = useCallback(async () => {
    try {
      const response = await api.get('/instruments/favorites');
      dispatch(setFavorites(response.data));
    } catch (err: any) {
      console.error('Error al cargar favoritos:', err);
    } finally {
      setLoading(false);
    }
  }, [dispatch]);

  useEffect(() => {
    fetchFavorites();
  }, [fetchFavorites]);

  const handleRemoveFavorite = async (e: React.MouseEvent, instrumentId: number) => {
    e.stopPropagation(); // Evitar que el clic se propague a la tarjeta
    try {
      await api.delete(`/instruments/${instrumentId}/favorite`);
      dispatch(toggleFavorite(instrumentId));
      // Refetch favorites to update the list
      fetchFavorites();
    } catch (err: any) {
      console.error('Error al remover favorito:', err);
    }
  };

  const handleInstrumentClick = (instrumentId: number) => {
    navigate(`/instrument/${instrumentId}`);
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(price);
  };

  const formatVolume = (volume: number) => {
    return new Intl.NumberFormat('en-US').format(volume);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-xl">Cargando favoritos...</div>
      </div>
    );
  }

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-3xl font-bold mb-4">Mis Favoritos</h1>
        {favorites.length === 0 && (
          <p className="text-gray-600">No tienes instrumentos favoritos. Ve a la página principal para agregar algunos.</p>
        )}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {favorites.map((instrument) => (
          <div 
            key={instrument.id} 
            className="bg-white p-4 rounded shadow border cursor-pointer hover:shadow-lg transition-shadow"
            onClick={() => handleInstrumentClick(instrument.id)}
          >
            <div className="flex justify-between items-start mb-2">
              <div>
                <h3 className="font-bold text-lg">{instrument.symbol}</h3>
                <p className="text-gray-600 text-sm">{instrument.name}</p>
              </div>
              <button
                onClick={(e) => handleRemoveFavorite(e, instrument.id)}
                className="text-yellow-500 hover:text-yellow-600"
                title="Remover de favoritos"
              >
                <StarIcon className="h-6 w-6" />
              </button>
            </div>
            
            <div className="space-y-2">
              <div className="text-2xl font-bold">
                {formatPrice(instrument.currentPrice)}
              </div>
              
              <div className={`text-sm ${instrument.dailyChange >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                {instrument.dailyChange >= 0 ? '+' : ''}{formatPrice(instrument.dailyChange)} 
                ({instrument.dailyChangePercent >= 0 ? '+' : ''}{instrument.dailyChangePercent.toFixed(2)}%)
              </div>
              
              <div className="text-xs text-gray-500">
                <div>Volumen: {formatVolume(instrument.volume)}</div>
                <div>Alto: {formatPrice(instrument.dayHigh)} | Bajo: {formatPrice(instrument.dayLow)}</div>
                <div>Actualizado: {new Date(instrument.lastUpdated).toLocaleTimeString()}</div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default FavoritesPage; 