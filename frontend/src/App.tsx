import { useEffect, useState } from 'react'

function App() {
    const [health, setHealth] = useState<any>(null)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        fetch('/api/health')
            .then(r => r.json())
            .then(setHealth)
            .catch(e => setError(String(e)))
    }, [])

    return (
        <div style={{padding: 24, fontFamily: 'system-ui, sans-serif'}}>
            <h1>Heat Selector MVP</h1>
            <p>Статус бэкенда:</p>
            <pre>{error ? error : JSON.stringify(health, null, 2)}</pre>
        </div>
    )
}

export default App
