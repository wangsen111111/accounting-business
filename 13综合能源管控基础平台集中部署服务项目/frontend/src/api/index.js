import http from './http'

/* ---------- 首页 / 元数据 ---------- */
export const getDashboard = () => http.get('/dashboard')
export const getMeta = () => http.get('/meta')

/* ---------- 能耗 ---------- */
export const getEnergyRecords = (params) => http.get('/energy/records', { params })
export const getEnergySeries = (params) => http.get('/energy/series', { params })
export const getEnergyAnalysis = () => http.get('/energy/analysis')

/* ---------- 碳排放 ---------- */
export const getCarbonFactors = () => http.get('/carbon/factors')
export const getCarbonAccounting = (year = 2025) => http.get('/carbon/accounting', { params: { year } })
export const getCarbonAlerts = () => http.get('/carbon/alerts')
export const getCarbonAsset = () => http.get('/carbon/asset')
export const getFootprint = () => http.get('/carbon/footprint')

/* ---------- 登录 ---------- */
export const login = (payload) => http.post('/auth/login', payload)
