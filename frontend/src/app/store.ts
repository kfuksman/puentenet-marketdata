import { configureStore } from '@reduxjs/toolkit';
import authReducer from '../features/auth/authSlice';
import instrumentsReducer from '../features/instruments/instrumentsSlice';
import adminReducer from '../features/admin/adminSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    instruments: instrumentsReducer,
    admin: adminReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch; 