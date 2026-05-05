import ',./App.css';
import 'primereact/resources/themes/saga-blue/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';

import { InputText } from 'primereact/inputtext';
import { Button } from 'primereact/button';

function App() {
    const [text, setText] = useState('');

  return (
    <div className="App">
        <header className="App-header">
            <h1>Welcome to React with Spring Boot</h1>
            <p>Get started by editing <code>src/App.js</code></p>
            <InputText value={text} onChange={(e) => setText(e.target.value)} />
            {text}
        </header>
    </div>
  );
}

export default App;