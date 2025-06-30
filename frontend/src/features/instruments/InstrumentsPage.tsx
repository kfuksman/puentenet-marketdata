import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../../app/hooks';
import { fetchInstrumentsStart, fetchInstrumentsSuccess, fetchInstrumentsFailure, toggleFavorite } from './instrumentsSlice';
import api from '../../services/api';
import { StarIcon } from '@heroicons/react/24/solid';
import { StarIcon as StarOutlineIcon } from '@heroicons/react/24/outline';

const InstrumentsPage: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { instruments, loading, error } = useAppSelector((state) => state.instruments);
  const [searchTerm, setSearchTerm] = useState('');

  const fetchInstruments = useCallback(async () => {
    dispatch(fetchInstrumentsStart());
    try {
      const response = await api.get('/instruments');
      dispatch(fetchInstrumentsSuccess(response.data));
    } catch (err: any) {
      dispatch(fetchInstrumentsFailure('Error al cargar instrumentos'));
    }
  }, [dispatch]);

  useEffect(() => {
    fetchInstruments();
  }, [fetchInstruments]);

  const handleToggleFavorite = async (e: React.MouseEvent, instrumentId: number) => {
    e.stopPropagation(); // Evitar que el clic se propague a la tarjeta
    try {
      const instrument = instruments.find(inst => inst.id === instrumentId);
      if (instrument?.favorite) {
        await api.delete(`/instruments/${instrumentId}/favorite`);
      } else {
        await api.post(`/instruments/${instrumentId}/favorite`);
      }
      dispatch(toggleFavorite(instrumentId));
    } catch (err: any) {
      console.error('Error al actualizar favorito:', err);
    }
  };

  const handleInstrumentClick = (instrumentId: number) => {
    navigate(`/instrument/${instrumentId}`);
  };

  const filteredInstruments = instruments.filter(instrument =>
    instrument.symbol.toLowerCase().includes(searchTerm.toLowerCase()) ||
    instrument.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

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
        <div className="text-xl">Cargando instrumentos...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center text-red-600">
        <p>{error}</p>
        <button
          onClick={fetchInstruments}
          className="mt-4 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          Reintentar
        </button>
      </div>
    );
  }

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-3xl font-bold mb-4">Instrumentos de Mercado</h1>
        <input
          type="text"
          placeholder="Buscar por símbolo o nombre..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full max-w-md border rounded px-3 py-2"
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filteredInstruments.map((instrument) => (
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
                onClick={(e) => handleToggleFavorite(e, instrument.id)}
                className="text-yellow-500 hover:text-yellow-600"
              >
                {instrument.favorite ? (
                  <StarIcon className="h-6 w-6" />
                ) : (
                  <StarOutlineIcon className="h-6 w-6" />
                )}
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

export default InstrumentsPage; 