// app.js - simple SPA to view all tables, sort and filter
// Assumptions: backend exposes endpoints under /api/<resource>
// resources: employees, inventory, suppliers, orders, withdrawals, documents, supplier-feedback, notifications

const RESOURCES = [
  { key: 'employees', label: 'Employees' },
  { key: 'inventory', label: 'Inventory' },
  { key: 'suppliers', label: 'Suppliers' },
  { key: 'orders', label: 'Orders' },
  { key: 'withdrawals', label: 'Withdrawals' },
  { key: 'documents', label: 'Documents' },
  { key: 'supplier-feedback', label: 'Supplier Feedback' },
  { key: 'notifications', label: 'Notifications' }
];

const state = {
  resource: null,
  data: [],
  sort: { field: null, asc: true },
  filter: ''
};

function initNav() {
  const nav = document.getElementById('nav-tables');
  RESOURCES.forEach(r => {
    const li = document.createElement('li'); li.className = 'nav-item';
    const a = document.createElement('a'); a.className = 'nav-link'; a.href = '#'; a.textContent = r.label;
    a.onclick = (e) => { e.preventDefault(); loadResource(r.key); document.querySelectorAll('#nav-tables .nav-link').forEach(n => n.classList.remove('active')); a.classList.add('active'); };
    li.appendChild(a); nav.appendChild(li);
  });
}

async function loadResource(key) {
  state.resource = key;
  state.sort = { field: null, asc: true };
  state.filter = '';
  document.getElementById('content').innerHTML = `<div class="d-flex justify-content-between align-items-center"><h3 class="mb-3">${key}</h3><div class="input-group search-input"><input id="filterInput" class="form-control" placeholder="Filter..."/></div></div><div id="tableArea" class="table-container"></div>`;
  document.getElementById('filterInput').addEventListener('input', (e) => { state.filter = e.target.value; renderTable(); });

  try {
    const res = await fetch(`/api/${key}`);
    if (!res.ok) throw new Error(await res.text());
    const data = await res.json();
    state.data = data;
    renderTable();
  } catch (err) {
    document.getElementById('tableArea').innerHTML = `<div class="alert alert-danger">Fehler beim Laden: ${err}</div>`;
  }
}

function getColumns(items) {
  if (!items || items.length === 0) return [];
  const keys = new Set();
  items.forEach(it => Object.keys(flattenObject(it)).forEach(k => keys.add(k)));
  return Array.from(keys);
}

function flattenObject(obj, prefix = '') {
  // flatten nested simple objects to dot-notation (one level deep)
  const out = {};
  for (const k of Object.keys(obj)) {
    const val = obj[k];
    if (val && typeof val === 'object' && !Array.isArray(val)) {
      // include id and a short text field if present
      if (val.id !== undefined) out[`${k}.id`] = val.id;
      if (val.name !== undefined) out[`${k}.name`] = val.name;
      if (val.username !== undefined) out[`${k}.username`] = val.username;
      if (val.email !== undefined) out[`${k}.email`] = val.email;
    } else {
      out[k] = val;
    }
  }
  return out;
}

function renderTable() {
  const area = document.getElementById('tableArea');
  const items = state.data || [];
  const filter = state.filter.toLowerCase();
  const filtered = items.filter(it => JSON.stringify(it).toLowerCase().includes(filter));
  const cols = getColumns(filtered);
  if (cols.length === 0) { area.innerHTML = '<div class="alert alert-info">Keine Einträge.</div>'; return; }

  let html = '<div class="card table-card"><div class="card-body p-2"><div class="table-responsive"><table class="table table-striped table-sm mb-0"><thead><tr>';
  cols.forEach(c => { html += `<th data-field="${c}">${c} <span class="sort-indicator"></span></th>`; });
  html += '</tr></thead><tbody>';

  // prepare flattened rows
  const rows = filtered.map(it => flattenObject(it));

  // sort
  if (state.sort.field) {
    rows.sort((a,b) => {
      const af = a[state.sort.field]; const bf = b[state.sort.field];
      if (af === undefined) return 1; if (bf === undefined) return -1;
      if (af === bf) return 0;
      if (state.sort.asc) return (af > bf) ? 1 : -1; else return (af < bf) ? 1 : -1;
    });
  }

  rows.forEach(r => {
    html += '<tr>';
    cols.forEach(c => { html += `<td>${escapeHtml(r[c]) ?? ''}</td>` });
    html += '</tr>';
  });

  html += '</tbody></table></div></div></div>';
  area.innerHTML = html;

  // attach sort handlers
  document.querySelectorAll('th[data-field]').forEach(th => {
    th.onclick = () => {
      const field = th.getAttribute('data-field');
      if (state.sort.field === field) state.sort.asc = !state.sort.asc; else { state.sort.field = field; state.sort.asc = true; }
      renderTable();
    }
  });
}

function escapeHtml(val) {
  if (val === null || val === undefined) return '';
  return String(val)
    .replaceAll('&','&amp;')
    .replaceAll('<','&lt;')
    .replaceAll('>','&gt;')
    .replaceAll('"','&quot;')
    .replaceAll("'","&#039;");
}

// init
window.addEventListener('load', () => { initNav(); loadResource('employees'); });

