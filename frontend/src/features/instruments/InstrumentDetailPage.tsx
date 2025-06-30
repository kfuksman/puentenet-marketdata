import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../../app/hooks';
import { toggleFavorite } from './instrumentsSlice';
import api from '../../services/api';
import { StarIcon } from '@heroicons/react/24/solid';
import { StarIcon as StarOutlineIcon } from '@heroicons/react/24/outline';
import { ArrowLeftIcon } from '@heroicons/react/24/outline';

interface Instrument {
  id: number;
  symbol: string;
  name: string;
  currentPrice: number;
  dailyChange: number;
  dailyChangePercent: number;
  weeklyChange: number | null;
  weeklyChangePercent: number | null;
  dayHigh: number | null;
  dayLow: number | null;
  volume: number;
  marketCap: number | null;
  lastUpdated: string;
  favorite: boolean;
}

const InstrumentDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const [instrument, setInstrument] = useState<Instrument | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchInstrument = async () => {
      if (!id) return;
      
      setLoading(true);
      try {
        const response = await api.get(`/instruments/${id}`);
        setInstrument(response.data);
        setError(null);
      } catch (err: any) {
        setError('Error al cargar el instrumento');
        console.error('Error fetching instrument:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchInstrument();
  }, [id]);

  const handleToggleFavorite = async () => {
    if (!instrument) return;
    
    try {
      if (instrument.favorite) {
        await api.delete(`/instruments/${instrument.id}/favorite`);
      } else {
        await api.post(`/instruments/${instrument.id}/favorite`);
      }
      dispatch(toggleFavorite(instrument.id));
      setInstrument(prev => prev ? { ...prev, favorite: !prev.favorite } : null);
    } catch (err: any) {
      console.error('Error al actualizar favorito:', err);
    }
  };

  const formatPrice = (price: number | null) => {
    if (price === null) return 'N/A';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(price);
  };

  const formatVolume = (volume: number) => {
    return new Intl.NumberFormat('en-US').format(volume);
  };

  const formatMarketCap = (marketCap: number | null) => {
    if (marketCap === null) return 'N/A';
    if (marketCap >= 1e12) {
      return `$${(marketCap / 1e12).toFixed(2)}T`;
    } else if (marketCap >= 1e9) {
      return `$${(marketCap / 1e9).toFixed(2)}B`;
    } else if (marketCap >= 1e6) {
      return `$${(marketCap / 1e6).toFixed(2)}M`;
    } else {
      return formatPrice(marketCap);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-xl">Cargando instrumento...</div>
      </div>
    );
  }

  if (error || !instrument) {
    return (
      <div className="text-center text-red-600">
        <p>{error || 'Instrumento no encontrado'}</p>
        <button
          onClick={() => navigate('/')}
          className="mt-4 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          Volver a Instrumentos
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto">
      {/* Header */}
      <div className="mb-6">
        <button
          onClick={() => navigate('/')}
          className="flex items-center text-blue-600 hover:text-blue-800 mb-4"
        >
          <ArrowLeftIcon className="h-5 w-5 mr-2" />
          Volver a Instrumentos
        </button>
        
        <div className="flex justify-between items-start">
          <div>
            <h1 className="text-4xl font-bold">{instrument.symbol}</h1>
            <p className="text-xl text-gray-600">{instrument.name}</p>
          </div>
          <button
            onClick={handleToggleFavorite}
            className="text-yellow-500 hover:text-yellow-600 p-2"
          >
            {instrument.favorite ? (
              <StarIcon className="h-8 w-8" />
            ) : (
              <StarOutlineIcon className="h-8 w-8" />
            )}
          </button>
        </div>
      </div>

      {/* Main Price Section */}
      <div className="bg-white rounded-lg shadow-lg p-6 mb-6">
        <div className="text-center mb-6">
          <div className="text-5xl font-bold mb-2">
            {formatPrice(instrument.currentPrice)}
          </div>
          <div className={`text-2xl ${instrument.dailyChange >= 0 ? 'text-green-600' : 'text-red-600'}`}>
            {instrument.dailyChange >= 0 ? '+' : ''}{formatPrice(instrument.dailyChange)} 
            ({instrument.dailyChangePercent >= 0 ? '+' : ''}{instrument.dailyChangePercent.toFixed(2)}%)
          </div>
        </div>
      </div>

      {/* Key Metrics Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-6">
        {/* Day High/Low */}
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold mb-4 text-gray-800">Rango del Día</h3>
          <div className="space-y-3">
            <div className="flex justify-between">
              <span className="text-gray-600">Máximo:</span>
              <span className="font-semibold text-green-600">
                {formatPrice(instrument.dayHigh)}
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Mínimo:</span>
              <span className="font-semibold text-red-600">
                {formatPrice(instrument.dayLow)}
              </span>
            </div>
          </div>
        </div>

        {/* Volume */}
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold mb-4 text-gray-800">Volumen</h3>
          <div className="text-2xl font-bold text-blue-600">
            {formatVolume(instrument.volume)}
          </div>
          <p className="text-sm text-gray-500 mt-1">Acciones negociadas</p>
        </div>

        {/* Market Cap */}
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold mb-4 text-gray-800">Capitalización</h3>
          <div className="text-2xl font-bold text-purple-600">
            {formatMarketCap(instrument.marketCap)}
          </div>
          <p className="text-sm text-gray-500 mt-1">Valor de mercado</p>
        </div>
      </div>

      {/* Weekly Performance */}
      {instrument.weeklyChange !== null && (
        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <h3 className="text-lg font-semibold mb-4 text-gray-800">Rendimiento Semanal</h3>
          <div className={`text-2xl ${instrument.weeklyChange >= 0 ? 'text-green-600' : 'text-red-600'}`}>
            {instrument.weeklyChange >= 0 ? '+' : ''}{formatPrice(instrument.weeklyChange)} 
            ({instrument.weeklyChangePercent && instrument.weeklyChangePercent >= 0 ? '+' : ''}{instrument.weeklyChangePercent?.toFixed(2)}%)
          </div>
        </div>
      )}

      {/* Last Updated */}
      <div className="bg-gray-50 rounded-lg p-4">
        <p className="text-sm text-gray-600">
          <strong>Última actualización:</strong> {formatDate(instrument.lastUpdated)}
        </p>
      </div>
    </div>
  );
};

export default InstrumentDetailPage; 