const API = '/items';

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('itemForm');
    const nameInput = document.getElementById('name');
    const beschreibungInput = document.getElementById('beschreibung');
    const standortInput = document.getElementById('locationName');
    const categoryInput = document.getElementById('categoryName');
    const itemsContainer = document.getElementById('itemsContainer');

    let editingId = null;
    const cancelBtn = createCancelButton();
    form.appendChild(cancelBtn);

    const qrModal = createQrModal();
    document.body.appendChild(qrModal.overlay);

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = {
            name: nameInput.value.trim(),
            beschreibung: beschreibungInput.value.trim(),
            locationName: standortInput.value.trim(),
            categoryName: categoryInput.value.trim()
        };
        try {
            if (editingId) {
                await updateItem(editingId, payload);
            } else {
                await createItem(payload);
            }
            resetForm();
            await fetchAndRender();
        } catch (err) {
            console.error(err);
            alert('Fehler beim Speichern des Items.');
        }
    });

    cancelBtn.addEventListener('click', (e) => {
        e.preventDefault();
        resetForm();
    });

    // initial load
    fetchAndRender();

    // functions
    async function fetchAndRender() {
        try {
            const res = await fetch(API);
            if (!res.ok) throw new Error('Fehler beim Laden der Items');
            const items = await res.json();
            renderTable(items);
        } catch (err) {
            console.error(err);
            itemsContainer.innerHTML = '<p class="error">Konnte Items nicht laden.</p>';
        }
    }

    function renderTable(items) {
        if (!Array.isArray(items) || items.length === 0) {
            itemsContainer.innerHTML = '<p>Keine Items vorhanden.</p>';
            return;
        }

        const table = document.createElement('table');
        table.className = 'items-table';

        const thead = document.createElement('thead');
        thead.innerHTML = `
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Beschreibung</th>
                <th>Standort</th>
                <th>Kategorie</th>
                <th>QR</th>
                <th>Aktionen</th>
            </tr>`;
        table.appendChild(thead);

        const tbody = document.createElement('tbody');
        for (const it of items) {
            console.log(it)
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${escapeHtml(it.id)}</td>
                <td>${escapeHtml(it.name || '')}</td>
                <td>${escapeHtml(it.beschreibung || '')}</td>
                <td>${escapeHtml(it.location?.lname || it.location?.name || it.location || '')}</td>
                <td>${escapeHtml(it.category?.name || '')}</td>
                <td class="qr-cell"></td>
                <td class="actions-cell"></td>
            `;

            // QR-Button
            const qrCell = tr.querySelector('.qr-cell');
            const qrBtn = document.createElement('button');
            qrBtn.textContent = 'QR anzeigen';
            qrBtn.className = 'btn';
            qrBtn.addEventListener('click', () => showQr(it.id));
            qrCell.appendChild(qrBtn);

            // Actions: Edit, Delete
            const actionsCell = tr.querySelector('.actions-cell');

            const editBtn = document.createElement('button');
            editBtn.textContent = 'Bearbeiten';
            editBtn.className = 'btn btn-edit';
            editBtn.addEventListener('click', () => startEdit(it));
            actionsCell.appendChild(editBtn);

            const delBtn = document.createElement('button');
            delBtn.textContent = 'Löschen';
            delBtn.className = 'btn btn-delete';
            delBtn.addEventListener('click', () => deleteItemWithConfirm(it.id));
            actionsCell.appendChild(delBtn);

            tbody.appendChild(tr);
        }
        table.appendChild(tbody);
        itemsContainer.innerHTML = '';
        itemsContainer.appendChild(table);
    }

    function startEdit(item) {
        editingId = item.id;
        nameInput.value = item.name || '';
        beschreibungInput.value = item.beschreibung || '';
        standortInput.value = item.location || '';
        form.querySelector('button[type="submit"]').textContent = 'Speichern';
        cancelBtn.style.display = 'inline-block';
        nameInput.focus();
    }

    function resetForm() {
        editingId = null;
        form.reset();
        form.querySelector('button[type="submit"]').textContent = 'Item hinzufügen';
        cancelBtn.style.display = 'none';
    }

    async function createItem(payload) {
        const res = await fetch(API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('POST fehlgeschlagen');
        // backend liefert eventuell ItemResponse oder Item; we ignore payload return and reload list
        return res.json().catch(() => null);
    }

    async function updateItem(id, payload) {
        const res = await fetch(`${API}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('PUT fehlgeschlagen');
        return res.json().catch(() => null);
    }

    async function deleteItemWithConfirm(id) {
        if (!confirm('Item wirklich löschen?')) return;
        try {
            const res = await fetch(`${API}/${id}`, { method: 'DELETE' });
            if (res.status === 204) {
                await fetchAndRender();
            } else {
                throw new Error('Löschen fehlgeschlagen');
            }
        } catch (err) {
            console.error(err);
            alert('Fehler beim Löschen.');
        }
    }

    async function showQr(id) {
        try {
            const res = await fetch(`${API}/${id}/qrcode`);
            if (!res.ok) {
                if (res.status === 404) {
                    alert('Kein QR-Code vorhanden.');
                    return;
                }
                throw new Error('QR laden fehlgeschlagen');
            }
            const blob = await res.blob();
            const url = URL.createObjectURL(blob);
            qrModal.img.src = url;
            qrModal.overlay.classList.add('open');
            // revoke URL when modal closes
            qrModal.cleanup = () => {
                URL.revokeObjectURL(url);
            };
        } catch (err) {
            console.error(err);
            alert('Fehler beim Laden des QR-Codes.');
        }
    }

    function createCancelButton() {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.textContent = 'Abbrechen';
        btn.className = 'btn btn-cancel';
        btn.style.display = 'none';
        return btn;
    }

    function createQrModal() {
        const overlay = document.createElement('div');
        overlay.className = 'qr-overlay';
        overlay.innerHTML = `
            <div class="qr-modal">
                <button class="qr-close" title="Schließen">&times;</button>
                <img class="qr-image" alt="QR-Code">
            </div>`;
        const img = overlay.querySelector('.qr-image');
        const closeBtn = overlay.querySelector('.qr-close');

        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) close();
        });
        closeBtn.addEventListener('click', close);

        function close() {
            overlay.classList.remove('open');
            if (modal.cleanup) modal.cleanup();
            img.src = '';
        }

        const modal = { overlay, img, close, cleanup: null };
        return modal;
    }

    function escapeHtml(text) {
        return String(text)
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#039;');
    }
});
