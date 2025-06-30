import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface Instrument {
  id: number;
  symbol: string;
  name: string;
  currentPrice: number;
  dailyChange: number;
  dailyChangePercent: number;
  weeklyChange?: number | null;
  weeklyChangePercent?: number | null;
  dayHigh: number;
  dayLow: number;
  volume: number;
  marketCap?: number | null;
  lastUpdated: string;
  favorite: boolean;
}

interface InstrumentsState {
  instruments: Instrument[];
  favorites: Instrument[];
  loading: boolean;
  error: string | null;
}

const initialState: InstrumentsState = {
  instruments: [],
  favorites: [],
  loading: false,
  error: null,
};

const instrumentsSlice = createSlice({
  name: 'instruments',
  initialState,
  reducers: {
    fetchInstrumentsStart(state) {
      state.loading = true;
      state.error = null;
    },
    fetchInstrumentsSuccess(state, action: PayloadAction<Instrument[]>) {
      state.loading = false;
      state.instruments = action.payload;
    },
    fetchInstrumentsFailure(state, action: PayloadAction<string>) {
      state.loading = false;
      state.error = action.payload;
    },
    setFavorites(state, action: PayloadAction<Instrument[]>) {
      state.favorites = action.payload;
    },
    toggleFavorite(state, action: PayloadAction<number>) {
      const id = action.payload;
      state.instruments = state.instruments.map(inst =>
        inst.id === id ? { ...inst, favorite: !inst.favorite } : inst
      );
    },
  },
});

export const {
  fetchInstrumentsStart,
  fetchInstrumentsSuccess,
  fetchInstrumentsFailure,
  setFavorites,
  toggleFavorite,
} = instrumentsSlice.actions;
export default instrumentsSlice.reducer; 