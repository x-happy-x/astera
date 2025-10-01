import { AuthProvider } from './context/AuthContext'
import AppRouter from './routing/components/AppRouter'
import './App.scss'

function App() {
    return (
        <AuthProvider>
            <AppRouter />
        </AuthProvider>
    )
}

export default App
