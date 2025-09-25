'use strict';

// Neuer Key + Aufräumen alter Keys -> alte gespeicherte Daten erscheinen nicht mehr
const LS_KEY = 'inventory.items.v3';
const OLD_KEYS = ['inventory.items.v1', 'inventory.items.v2'];

/** @typedef {{ id:string, invnr:string, name:string, category:string, status:'verfügbar'|'verliehen'|'wartung'|'ausgemustert', location?:string, owner?:string, serial?:string, purchased?:string, tags?:string[], notes?:string, deleted?:boolean, createdAt:number, updatedAt:number }} Item */

// --- State ---
let items = load();
let editingId = null;

// --- UI Refs ---
const tbody = document.getElementById('tbody');
const empty = document.getElementById('empty');
const q = document.getElementById('q');
const filterCategory = document.getElementById('filterCategory');
const filterStatus = document.getElementById('filterStatus');
const showDeleted = document.getElementById('showDeleted');
const countAll = document.getElementById('countAll');
const countShown = document.getElementById('countShown');

// Init
refreshCategoryOptions();
render();

// Events
document.getElementById('btnAdd').addEventListener('click', ()=>openModalForCreate());
q.addEventListener('input', render);
filterCategory.addEventListener('change', render);
filterStatus.addEventListener('change', render);
showDeleted.addEventListener('change', render);

// Modal
const dlg = document.getElementById('dlg');
const form = document.getElementById('form');
const dlgTitle = document.getElementById('dlgTitle');
const dlgSub = document.getElementById('dlgSub');
const btnCancel = document.getElementById('btnCancel');

btnCancel.addEventListener('click', ()=>dlg.close());
form.addEventListener('submit', onSubmitForm);

function refreshCategoryOptions(){
  const cats = Array.from(new Set(items.map(i=>i.category))).sort();
  const opts = ['<option value="">Kategorie: alle</option>'].concat(cats.map(c=>`<option value="${escapeHtml(c)}">${escapeHtml(c)}</option>`));
  filterCategory.innerHTML = opts.join('');
}

function openModalForCreate(){
  editingId = null;
  dlgTitle.textContent = 'Inventar anlegen';
  dlgSub.textContent = 'Neuen Gegenstand erfassen';
  form.reset();
  document.getElementById('status').value = 'verfügbar';
  dlg.showModal();
}

function openModalForEdit(id){
  const it = items.find(x=>x.id===id);
  if(!it) return;
  editingId = id;
  dlgTitle.textContent = 'Inventar bearbeiten';
  dlgSub.textContent = it.invnr + ' – ' + it.name;
  form.invnr.value = it.invnr || '';
  form.name.value = it.name || '';
  form.category.value = it.category || 'Sonstiges';
  form.status.value = it.status || 'verfügbar';
  form.location.value = it.location || '';
  form.owner.value = it.owner || '';
  form.serial.value = it.serial || '';
  form.purchased.value = it.purchased || '';
  form.tags.value = (it.tags||[]).join(',');
  form.notes.value = it.notes || '';
  dlg.showModal();
}

function onSubmitForm(e){
  e.preventDefault();
  const data = {
    invnr: form.invnr.value.trim(),
    name: form.name.value.trim(),
    category: form.category.value,
    status: form.status.value,
    location: form.location.value.trim(),
    owner: form.owner.value.trim(),
    serial: form.serial.value.trim(),
    purchased: form.purchased.value,
    tags: form.tags.value.split(',').map(s=>s.trim()).filter(Boolean),
    notes: form.notes.value.trim()
  };

  if(!data.invnr || !data.name){
    alert('Bitte mindestens Inventar-Nr. und Name ausfüllen.');
    return;
  }

  if(editingId){
    const idx = items.findIndex(i=>i.id===editingId);
    if(idx>-1){ items[idx] = {...items[idx], ...data, updatedAt: Date.now()}; }
  } else {
    items.unshift({id:cid(), deleted:false, createdAt:Date.now(), updatedAt:Date.now(), ...data});
  }
  save();
  refreshCategoryOptions();
  render();
  dlg.close();
}

// --- Render ---
function render(){
  const term = q.value.toLowerCase().trim();
  const cat = filterCategory.value;
  const status = filterStatus.value;
  const incDeleted = showDeleted.checked;

  const filtered = items.filter(it=>{
    if(!incDeleted && it.deleted) return false;
    if(cat && it.category!==cat) return false;
    if(status && it.status!==status) return false;
    if(term){
      const blob = [it.invnr,it.name,it.category,it.location,it.serial,it.owner,(it.tags||[]).join(' ')].join(' ').toLowerCase();
      if(!blob.includes(term)) return false;
    }
    return true;
  });

  countAll.textContent = items.length;
  countShown.textContent = filtered.length;

  if(filtered.length===0){
    tbody.innerHTML='';
    empty.hidden=false;
    return;
  }
  empty.hidden=true;

  tbody.innerHTML = filtered.map(rowTemplate).join('');

  tbody.querySelectorAll('[data-edit]').forEach(btn=>btn.addEventListener('click', e=>{
    openModalForEdit(e.currentTarget.getAttribute('data-edit'))
  }));
  tbody.querySelectorAll('[data-del]').forEach(btn=>btn.addEventListener('click', e=>{
    toggleDelete(e.currentTarget.getAttribute('data-del'), true)
  }));
  tbody.querySelectorAll('[data-restore]').forEach(btn=>btn.addEventListener('click', e=>{
    toggleDelete(e.currentTarget.getAttribute('data-restore'), false)
  }));
}

function rowTemplate(it){
  const statusChip = chipForStatus(it.status, it.deleted);
  const actions = it.deleted
    ? `<button class="btn" data-restore="${it.id}">Wiederherstellen</button>`
    : `<button class="btn" data-edit="${it.id}">Bearbeiten</button> <button class="btn danger" data-del="${it.id}">Löschen</button>`;

  return `<tr ${it.deleted? 'style="opacity:.6"':''}>
    <td><code class="kbd">${escapeHtml(it.invnr)}</code></td>
    <td>
      <div style="font-weight:600">${escapeHtml(it.name)}</div>
      <div class="badge">${(it.tags||[]).map(t=>`<span class="tag">${escapeHtml(t)}</span>`).join(' ')}</div>
    </td>
    <td>${escapeHtml(it.category)}</td>
    <td>${statusChip}</td>
    <td>${escapeHtml(it.location||'-')}</td>
    <td>${escapeHtml(it.serial||'-')}</td>
    <td class="row-actions">${actions}</td>
  </tr>`;
}

function chipForStatus(status, deleted){
  if(deleted) return `<span class="chip del">gelöscht</span>`;
  let cls='';
  if(status==='verfügbar') cls='ok';
  else if(status==='wartung') cls='warn';
  else if(status==='verliehen') cls='out';
  else if(status==='ausgemustert') cls='del';
  return `<span class="chip ${cls}">${escapeHtml(status)}</span>`;
}

function toggleDelete(id, del){
  const it = items.find(x=>x.id===id);
  if(!it) return;
  it.deleted = del;
  it.updatedAt = Date.now();
  save();
  render();
}

// --- Storage ---
function load(){
  try{
    cleanupOldKeys();
    const raw = localStorage.getItem(LS_KEY);
    if(raw){
      const arr = JSON.parse(raw);
      if(Array.isArray(arr)) return arr;
    }
  }catch(e){ console.warn('load failed', e); }
  return [];
}
function save(){ persist(items); }
function persist(arr){ localStorage.setItem(LS_KEY, JSON.stringify(arr)); }
function cleanupOldKeys(){
  try{ (OLD_KEYS||[]).forEach(k=>localStorage.removeItem(k)); }catch(e){}
}

// --- Utils ---
function cid(){ return 'id-' + Math.random().toString(36).slice(2,9) + Date.now().toString(36).slice(-4) }
function escapeHtml(s=''){ return s.replace(/[&<>"']/g, c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;','\'':'&#39;'}[c])) }

// Keyboard UX
window.addEventListener('keydown', (e)=>{
  if(e.key === 'n' && !dlg.open){ e.preventDefault(); openModalForCreate(); }
  if(e.key === '/' && !dlg.open){ e.preventDefault(); q.focus(); }
  if(e.key === 'Escape' && dlg.open){ dlg.close(); }
});
