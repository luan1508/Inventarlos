const API = '/items';

document.addEventListener('DOMContentLoaded', () =>
{
    const form = document.getElementById('itemForm');
    const nameInput = document.getElementById('name');
    const beschreibungInput = document.getElementById('beschreibung');
    const standortInput = document.getElementById('locationName');
    const categoryInput = document.getElementById('categoryName');
    const itemsContainer = document.getElementById('itemsContainer');
    const submitBtn=document.getElementById('submitBtn');
    const locationAddBtn = document.getElementById('locationAdd');
    const categoryAddBtn = document.getElementById('categoryAdd');

    const addDialog = createAddDialog();
    document.body.appendChild(addDialog.overlay);

    let editingId = null;
    let sortField = null; // 'name', 'location', 'category'
    let sortDirection = 'asc'; // 'asc' or 'desc'
    const cancelBtn = createCancelButton();
    form.appendChild(cancelBtn);

    const qrModal = createQrModal();
    document.body.appendChild(qrModal.overlay);

    submitBtn.addEventListener('click',
    async (e) =>
    {
        e.preventDefault();

        // Built-in HTML validation first
        if (!form.checkValidity())
        {
            form.reportValidity();
            return; // stop if required fields are not filled
        }

        const payload =
        {
            name: nameInput.value.trim(),
            beschreibung: beschreibungInput.value.trim(),
            locationName: standortInput.value.trim(),
            categoryName: categoryInput.value.trim()
        };

        // Extra trimmed checks to avoid whitespace-only values
        const missing = [];
        if (!payload.name) missing.push('Name');
        if (!payload.locationName) missing.push('Standort');
        if (!payload.categoryName) missing.push('Kategorie');
        if (missing.length)
        {
            alert(`Bitte füllen Sie folgende Felder aus: ${missing.join(', ')}`);
            return; // block request when inputs are invalid
        }

        try
        {
            if (editingId)
            {
                await updateItem(editingId, payload);
            }
            else
            {
                await createItem(payload);
            }
            resetForm();
            await fetchAndRender();
        }
        catch (err)
        {
            console.error(err);
            const suffix = (err.details && Array.isArray(err.details) && err.details.length)
                ? `\nFehlende Felder: ${err.details.join(', ')}`
                : '';
            alert((err.message || 'Fehler beim Speichern des Items.') + suffix);
        }
    });

    cancelBtn.addEventListener('click',
        (e) =>
        {
            e.preventDefault();
            resetForm();
        }
    );

    // initial load
    fetchAndRender();

    fillSelects("location");
    fillSelects("category");

    locationAddBtn?.addEventListener('click', async (e) =>
    {
        e.preventDefault();
        addDialog.open('location');
    });

    categoryAddBtn?.addEventListener('click', async (e) =>
    {
        e.preventDefault();
        addDialog.open('category');
    });

    standortInput.addEventListener('click', function ()
    {
        if (standortInput && !standortInput.options.length > 0)
        {
            fillSelects("location");
        }
    });

    categoryInput.addEventListener('click', function ()
    {
        if (categoryInput && !categoryInput.options.length > 0)
        {
            fillSelects("category");
        }
    });

   let isFilled
   async function fillSelects(input)
   {
       const response = await fetch
       (`${API}/${input}`,
           {
               method: 'GET',
               headers: { 'Content-Type': 'application/json' }
           }
       );
       const data = await response.json();

       //DEBUG HILFE
       console.log(data);

       for(let i in data)
       {
           const option = document.createElement('option');
           option.value = data[i]["name"];
           option.textContent = data[i]["name"];
           if(input==="location")
           {
               standortInput.appendChild(option);
           } else
           {
               categoryInput.appendChild(option);
           }

       }
   }

    function ensureOption(selectEl, value)
    {
        if (!selectEl || !value) return;
        const exists = Array.from(selectEl.options).some(o => o.value === value);
        if (!exists)
        {
            const option = document.createElement('option');
            option.value = value;
            option.textContent = value;
            selectEl.appendChild(option);
        }
        selectEl.value = value;
    }

    function createAddDialog()
    {
        const overlay = document.createElement('div');
        overlay.className = 'dialog-overlay';
        overlay.innerHTML = `
            <div class="dialog">
                <h3 class="dialog-title"></h3>
                <input type="text" class="dialog-input" placeholder="Name" />
                <div class="error-text"></div>
                <div class="dialog-actions">
                    <button type="button" class="btn btn-cancel dialog-cancel">Abbrechen</button>
                    <button type="button" class="btn dialog-save">Hinzufügen</button>
                </div>
            </div>`;

        const titleEl = overlay.querySelector('.dialog-title');
        const inputEl = overlay.querySelector('.dialog-input');
        const errorEl = overlay.querySelector('.error-text');
        const cancelBtn = overlay.querySelector('.dialog-cancel');
        const saveBtn = overlay.querySelector('.dialog-save');

        let currentType = null;

        function close()
        {
            overlay.classList.remove('open');
            inputEl.value = '';
            errorEl.textContent = '';
            currentType = null;
        }

        overlay.addEventListener('click', (e) =>
        {
            if (e.target === overlay) close();
        });
        cancelBtn.addEventListener('click', close);

        saveBtn.addEventListener('click', async () =>
        {
            if (!currentType) return;
            const val = inputEl.value.trim();
            if (!val)
            {
                errorEl.textContent = 'Bitte einen Namen eingeben.';
                inputEl.focus();
                return;
            }

            try
            {
                await createEntity(currentType, val);
                const targetSelect = currentType === 'location' ? standortInput : categoryInput;
                ensureOption(targetSelect, val);
                close();
            }
            catch (err)
            {
                errorEl.textContent = err?.message || 'Fehler beim Anlegen.';
            }
        });

        return {
            overlay,
            open: (type) =>
            {
                currentType = type;
                const label = type === 'location' ? 'Standort' : 'Kategorie';
                titleEl.textContent = `${label} hinzufügen`;
                inputEl.placeholder = `${label}-Name`;
                errorEl.textContent = '';
                inputEl.value = '';
                overlay.classList.add('open');
                setTimeout(() => inputEl.focus(), 10);
            }
        };
    }

    async function createEntity(type, name)
    {
        const payload = { name };
        const res = await fetch(`${API}/${type}`,
        {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json().catch(() => null);
        if (!res.ok)
        {
            const msg = (data && data.message) ? data.message : 'Fehler beim Anlegen.';
            const err = new Error(msg);
            throw err;
        }
        return data;
    }

    // functions
    async function fetchAndRender()
    {
        try
        {
            const res = await fetch(API);
            if (!res.ok) throw new Error('Fehler beim Laden der Items');
            const items = await res.json();
            renderTable(items);
        }
        catch (err)
        {
            console.error(err);
            itemsContainer.innerHTML = '<p class="error">Konnte Items nicht laden.</p>';
        }
    }

    function renderTable(items)
    {
        if (!Array.isArray(items) || items.length === 0)
        {
            itemsContainer.innerHTML = '<p>Keine Items vorhanden.</p>';
            return;
        }

        // Sort items if a field is selected
        if (sortField)
        {
            items = sortItems([...items], sortField, sortDirection);
        }

        const table = document.createElement('table');
        table.className = 'items-table';

        const thead = document.createElement('thead');
        const nameSort = getSortIndicator('name');
        const locationSort = getSortIndicator('location');
        const categorySort = getSortIndicator('category');
        thead.innerHTML = `
            <tr>
                <th>ID</th>
                <th class="sortable" data-field="name"><span">Name</span><span class="sortingArrow">${nameSort}</span></th>
                <th>Beschreibung</th>
                <th class="sortable" data-field="location"><span>Standort</span><span class="sortingArrow">${locationSort}</span></th>
                <th class="sortable" data-field="category"><span>Kategorie</span><span class="sortingArrow">${categorySort}</span></th>
                <th>QR</th>
                <th>Aktionen</th>
            </tr>`;
        table.appendChild(thead);

        // Add click handlers to sortable headers
        thead.querySelectorAll('.sortable').forEach(th =>
        {
            th.style.cursor = 'pointer';
            th.addEventListener('click', () => handleSort(th.dataset.field));
        });

        const tbody = document.createElement('tbody');

        for (const it of items)
        {
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

    function startEdit(item)
    {
        editingId = item.id;
        nameInput.value = item.name || '';
        beschreibungInput.value = item.beschreibung || '';
        standortInput.value = item.location?.name || '';
        categoryInput.value = item.category?.name || '';
        form.querySelector('button[type="submit"]').textContent = 'Speichern';
        cancelBtn.style.display = 'inline-block';
        nameInput.focus();
    }

    function resetForm()
    {
        editingId = null;
        form.reset();
        form.querySelector('button[type="submit"]').textContent = 'Item hinzufügen';
        cancelBtn.style.display = 'none';
    }

    async function createItem(payload)
    {
        const res = await fetch
        (API,
        {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json().catch(() => null);
        if (!res.ok)
        {
            const msg = (data && data.message) ? data.message : 'POST fehlgeschlagen';
            const err = new Error(msg);
            if (data && data.missingFields) err.details = data.missingFields;
            throw err;
        }
        // backend liefert eventuell ItemResponse oder Item
        return data;
    }

    async function updateItem(id, payload)
    {
        const res = await fetch(`${API}/${id}`,
        {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json().catch(() => null);
        if (!res.ok)
        {
            const msg = (data && data.message) ? data.message : 'PUT fehlgeschlagen';
            const err = new Error(msg);
            if (data && data.missingFields) err.details = data.missingFields;
            throw err;
        }
        return data;
    }

    async function deleteItemWithConfirm(id)
    {
        if (!confirm('Item wirklich löschen?')) return;
        try
        {
            const res = await fetch(`${API}/${id}`, { method: 'DELETE' });
            if (res.status === 204)
            {
                await fetchAndRender();
            }
            else
            {
                throw new Error('Löschen fehlgeschlagen');
            }
        }
        catch (err)
        {
            console.error(err);
            alert('Fehler beim Löschen.');
        }
    }

    async function showQr(id)
    {
        try
        {
            const res = await fetch(`${API}/${id}/qrcode`);
            if (!res.ok)
            {
                if (res.status === 404)
                {
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
            qrModal.cleanup = () =>
            {
                URL.revokeObjectURL(url);
            };
        } catch (err) {
            console.error(err);
            alert('Fehler beim Laden des QR-Codes.');
        }
    }

    function createCancelButton()
    {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.textContent = 'Abbrechen';
        btn.className = 'btn btn-cancel';
        btn.style.display = 'none';
        return btn;
    }

    function createQrModal()
    {
        const overlay = document.createElement('div');
        overlay.className = 'qr-overlay';
        overlay.innerHTML = `
            <div class="qr-modal">
                <button class="qr-close" title="Schließen">&times;</button>
                <img class="qr-image" alt="QR-Code">
            </div>`;
        const img = overlay.querySelector('.qr-image');
        const closeBtn = overlay.querySelector('.qr-close');

        overlay.addEventListener('click',
            (e) =>
            {
                if (e.target === overlay) close();
            }
        );
        closeBtn.addEventListener('click', close);

        function close()
        {
            overlay.classList.remove('open');
            if (modal.cleanup) modal.cleanup();
            img.src = '';
        }

        const modal = { overlay, img, close, cleanup: null };
        return modal;
    }

    function escapeHtml(text)
    {
        return String(text)
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#039;');
    }

    function handleSort(field)
    {
        if (sortField === field)
        {
            // Toggle direction
            sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
        }
        else
        {
            sortField = field;
            sortDirection = 'asc';
        }
        fetchAndRender();
    }

    function getSortIndicator(field)
    {
        if (sortField !== field) return '↕';
        return sortDirection === 'asc' ? '↑' : '↓';
    }

    function sortItems(items, field, direction)
    {
        return items.sort((a, b) =>
        {
            let aVal, bVal;
            if (field === 'name')
            {
                aVal = (a.name || '').toLowerCase();
                bVal = (b.name || '').toLowerCase();
            }
            else if (field === 'location')
            {
                aVal = (a.location?.name || a.location?.lname || '').toLowerCase();
                bVal = (b.location?.name || b.location?.lname || '').toLowerCase();
            }
            else if (field === 'category')
            {
                aVal = (a.category?.name || '').toLowerCase();
                bVal = (b.category?.name || '').toLowerCase();
            }
            else return 0;

            if (aVal < bVal) return direction === 'asc' ? -1 : 1;
            if (aVal > bVal) return direction === 'asc' ? 1 : -1;
            return 0;
        });
    }

});
