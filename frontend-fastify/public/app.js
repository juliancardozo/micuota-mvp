(async function(){
  // Grab elements (may be missing in some variants of the page). Use helpers below to safely attach handlers.
  const statusEl = document.getElementById('status')
  const registerResult = document.getElementById('register-result')
  const loginResult = document.getElementById('login-result')
  const planResult = document.getElementById('plan-result')
  const sessionInfo = document.getElementById('session-info')
  const tokenDisplay = document.getElementById('token-display')

  function el(id){ return document.getElementById(id) }
  function on(id, evt, fn){ const e = el(id); if (e) e.addEventListener(evt, fn); else console.warn(`Element #${id} not found, skipping handler`); }

  // Load configuration (static file or /config)
  let BACKEND = 'http://localhost:8080'
  try {
    const cfg = await fetch('/config').then(r=>r.json()).catch(()=>null)
    if (cfg && cfg.backendUrl) BACKEND = cfg.backendUrl
    else {
      try { const cfg2 = await fetch('./config.json').then(r=>r.json()).catch(()=>null); if (cfg2 && cfg2.backendUrl) BACKEND = cfg2.backendUrl } catch(e){}
    }
  } catch(e){}

  if (statusEl) statusEl.textContent = 'Backend: ' + BACKEND

  function show(el, obj){ if (!el) { console.warn('show() called for missing element', el, obj); return } el.textContent = JSON.stringify(obj, null, 2) }

  function setSession(token){
    if (token) {
      localStorage.setItem('micuota_token', token)
      if (sessionInfo) { sessionInfo.textContent = 'Autenticado'; sessionInfo.classList.add('success') }
      else console.warn('setSession: session-info element not found')
    } else {
      localStorage.removeItem('micuota_token')
      if (sessionInfo) { sessionInfo.textContent = 'No autenticado'; sessionInfo.classList.remove('success') }
      else console.warn('setSession: session-info element not found')
    }
  }

  // restore session
  const existing = localStorage.getItem('micuota_token')
  if (existing) setSession(existing)

  on('btn-register','click', async ()=>{
    const payload = {
      name: (el('reg-name') && el('reg-name').value) || '',
      email: (el('reg-email') && el('reg-email').value) || '',
      password: (el('reg-password') && el('reg-password').value) || '',
      role: (el('reg-role') && el('reg-role').value) || 'ALUMNO'
    }
    if (registerResult) registerResult.textContent = 'Procesando...'
    try{
      const res = await fetch(BACKEND + '/auth/register', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(payload) })
      const json = await res.json()
      show(registerResult, json)
    }catch(err){ show(registerResult, { error: err.message }) }
  })

  on('btn-clear-register','click', ()=>{
    if (el('reg-name')) el('reg-name').value=''
    if (el('reg-email')) el('reg-email').value=''
    if (el('reg-password')) el('reg-password').value=''
  })

  on('btn-login','click', async ()=>{
    const payload = { email: (el('login-email') && el('login-email').value) || '', password: (el('login-password') && el('login-password').value) || '' }
    if (loginResult) loginResult.textContent = 'Procesando...'
    try{
      const res = await fetch(BACKEND + '/auth/login', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(payload) })
      const json = await res.json()
      if (json.token) { setSession(json.token); show(loginResult, { success: true }) }
      else show(loginResult, json)
    }catch(err){ show(loginResult, { error: err.message }) }
  })
  on('btn-logout','click', ()=>{ setSession(null); if (tokenDisplay) tokenDisplay.textContent = '' })

  on('btn-create-plan','click', async ()=>{
    const token = localStorage.getItem('micuota_token')
    if (!token) { planResult.textContent = 'No autenticado - haz login primero'; return }
    const payload = { title: (el('plan-title') && el('plan-title').value) || '', price: (el('plan-price') && el('plan-price').value) || '', frequency: (el('plan-frequency') && el('plan-frequency').value) || 'monthly' }
    if (planResult) planResult.textContent = 'Procesando...'
    try{
      const res = await fetch(BACKEND + '/plans', { method: 'POST', headers: {'Content-Type':'application/json','Authorization':'Bearer ' + token}, body: JSON.stringify(payload) })
      const json = await res.json()
      show(planResult, json)
    }catch(err){ show(planResult, { error: err.message }) }
  })
  on('btn-clear-plan','click', ()=>{ if (el('plan-title')) el('plan-title').value=''; if (el('plan-price')) el('plan-price').value=''; if (el('plan-frequency')) el('plan-frequency').value='monthly' })

  on('btn-show-token','click', ()=>{ if (tokenDisplay) tokenDisplay.textContent = localStorage.getItem('micuota_token') || 'No token' })
  on('btn-copy-token','click', ()=>{ const t = localStorage.getItem('micuota_token'); if (t) navigator.clipboard.writeText(t).then(()=> alert('Token copiado')) })

  // Student quick-pay flow: login as demo student (or use existing session) -> subscribe to first plan -> charge
  on('btn-student-pay','click', async ()=>{
    const log = el('onboard-result') || el('register-result')
    log.textContent = ''
    try{
      // Ensure there is a demo student; call /demo/seed which creates alumno@local.test/alumno
      log.textContent += 'Seed demo data...\n'
      await fetch(BACKEND + '/demo/seed', { method: 'POST' })

      // Login as the demo student
      log.textContent += 'Logging in as alumno@local.test...\n'
      const loginRes = await fetch(BACKEND + '/auth/login', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({ email: 'alumno@local.test', password: 'alumno' }) })
      const loginJson = await loginRes.json()
      if (!loginJson.token) { log.textContent += 'Login failed: ' + JSON.stringify(loginJson) + '\n'; return }
      const token = loginJson.token
      setSession(token)
      log.textContent += 'Login OK\n'

      // Find plans
      log.textContent += 'Fetching plans...\n'
      const plans = await fetch(BACKEND + '/plans').then(r=>r.json())
      if (!Array.isArray(plans) || plans.length === 0) { log.textContent += 'No plans available\n'; return }
      const plan = plans[0]
      log.textContent += 'Using plan id=' + plan.id + '\n'

      // Create subscription
      log.textContent += 'Creating subscription...\n'
      const subRes = await fetch(BACKEND + '/subscriptions', { method: 'POST', headers: {'Content-Type':'application/json','Authorization':'Bearer ' + token}, body: JSON.stringify({ planId: String(plan.id) }) })
      const sub = await subRes.json()
      if (!sub.id) { log.textContent += 'Subscription failed: ' + JSON.stringify(sub) + '\n'; return }
      log.textContent += 'Subscription created id=' + sub.id + '\n'

      // Charge subscription
      log.textContent += 'Charging subscription...\n'
      const chargeRes = await fetch(BACKEND + '/payments/charge', { method: 'POST', headers: {'Content-Type':'application/json','Authorization':'Bearer ' + token}, body: JSON.stringify({ subscriptionId: sub.id }) })
      const payment = await chargeRes.json()
      if (!payment.id) { log.textContent += 'Payment failed: ' + JSON.stringify(payment) + '\n'; return }
      log.textContent += 'Payment successful id=' + payment.id + ' amount=' + payment.amount + '\n'
      showModal('Pago exitoso', 'Pago exitoso id=' + payment.id + '\nMonto: ' + payment.amount)

    } catch(e){ log.textContent += 'Error: ' + e.message + '\n' }
  })

  // Modal helpers
  function showModal(title, body) {
    const modal = document.getElementById('micuota-modal')
    document.getElementById('micuota-modal-title').textContent = title
    document.getElementById('micuota-modal-body').textContent = body
    modal.style.display = 'flex'
  }
  function hideModal(){ document.getElementById('micuota-modal').style.display = 'none' }
  document.getElementById('micuota-modal-close').addEventListener('click', hideModal)

  // Onboarding flows
  const onbResult = document.getElementById('onboard-result')
  function logOnb(msg){ onbResult.textContent = (onbResult.textContent ? onbResult.textContent + '\n' : '') + msg }

  async function registerAndLogin(name,email,password,role){
    logOnb('Registrando...')
    const reg = await fetch(BACKEND + '/auth/register', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({name,email,password,role}) }).then(r=>r.json())
    if (reg.id) logOnb('Registro OK id=' + reg.id)
    else { logOnb('Registro fallo: ' + JSON.stringify(reg)); throw new Error('register failed') }
    logOnb('Autenticando...')
    const login = await fetch(BACKEND + '/auth/login', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({email,password}) }).then(r=>r.json())
    if (login.token) { setSession(login.token); logOnb('Login OK'); return login.token }
    else { logOnb('Login fallo: ' + JSON.stringify(login)); throw new Error('login failed') }
  }

  document.getElementById('btn-onboard-prof').addEventListener('click', async ()=>{
    onbResult.textContent = ''
    const name = document.getElementById('onb-name').value || ('Profesor ' + Date.now())
    const email = document.getElementById('onb-email').value || ('prof+'+Date.now()+'@local.test')
    const password = document.getElementById('onb-password').value || 'test1234'
    try{
      const token = await registerAndLogin(name,email,password,'PROFESOR')
      // create a default plan
      logOnb('Creando plan por defecto...')
      const planPayload = { title: 'Starter E2E', price: '19.99', frequency: 'monthly' }
      const plan = await fetch(BACKEND + '/plans', { method:'POST', headers:{'Content-Type':'application/json','Authorization':'Bearer ' + token}, body: JSON.stringify(planPayload) }).then(r=>r.json())
      if (plan.id) { logOnb('Plan creado id=' + plan.id + ' mpPlanId=' + (plan.mpPlanId||'n/a')) }
      else logOnb('Error creando plan: ' + JSON.stringify(plan))
    }catch(e){ logOnb('Onboarding error: ' + e.message) }
  })

  document.getElementById('btn-onboard-stud').addEventListener('click', async ()=>{
    onbResult.textContent = ''
    const name = document.getElementById('onb-name').value || ('Alumno ' + Date.now())
    const email = document.getElementById('onb-email').value || ('stud+'+Date.now()+'@local.test')
    const password = document.getElementById('onb-password').value || 'test1234'
    try{
      const token = await registerAndLogin(name,email,password,'ALUMNO')
      logOnb('Buscando planes...')
      const plans = await fetch(BACKEND + '/plans').then(r=>r.json())
      if (!Array.isArray(plans) || plans.length === 0) { logOnb('No hay planes disponibles') ; return }
      const plan = plans[0]
      logOnb('Suscribiendo al plan id=' + plan.id)
      const sub = await fetch(BACKEND + '/subscriptions', { method:'POST', headers:{'Content-Type':'application/json','Authorization':'Bearer ' + token}, body: JSON.stringify({ planId: String(plan.id) }) }).then(r=>r.json())
      if (sub.id) { logOnb('Suscripción creada id=' + sub.id + ' mpSubscriptionId=' + (sub.mpSubscriptionId||'n/a')) }
      else logOnb('Error creando suscripción: ' + JSON.stringify(sub))
    }catch(e){ logOnb('Onboarding error: ' + e.message) }
  })

})()
