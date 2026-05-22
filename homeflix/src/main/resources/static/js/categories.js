
document.addEventListener('DOMContentLoaded', init);

let categories = [];

async function init() {
  await loadCategories();

  document.getElementById('inputColor').addEventListener('input', (e) => {
    document.getElementById('inputColorText').value = e.target.value;
  });
}

async function loadCategories() {
  try {
    categories = await CategoriesAPI.findAll();
    renderCategoryList();
  } catch (e) {
    console.error('Erro ao carregar categorias:', e);
    document.getElementById('categoryList').innerHTML =
      '<div class="empty-state"><div class="empty-state-icon">⚠️</div><p>Erro ao carregar categorias</p></div>';
  }
}

function renderCategoryList() {
  const container = document.getElementById('categoryList');

  if (categories.length === 0) {
    container.innerHTML = `
      <div class="empty-state">
        <div class="empty-state-icon">📂</div>
        <p>Nenhuma categoria cadastrada</p>
      </div>`;
    return;
  }

  container.innerHTML = `
    <div class="table-container">
      <table class="table">
        <thead>
          <tr>
            <th>Cor</th>
            <th>Nome</th>
            <th>Descrição</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          ${categories.map(c => `
            <tr>
              <td><span class="color-dot" style="background:${c.color || 'var(--primary)'}"></span></td>
              <td style="font-weight:500">${c.name}</td>
              <td style="color:var(--text-secondary)">${c.description || '—'}</td>
              <td>
                <div class="table-actions">
                  <button class="btn btn-secondary btn-icon btn-sm" onclick="editCategory(${c.id})" title="Editar">✏️</button>
                  <button class="btn btn-danger btn-icon btn-sm" onclick="deleteCategory(${c.id}, '${c.name.replace(/'/g, "\\'")}')" title="Excluir">🗑️</button>
                </div>
              </td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    </div>
  `;
}



function openForm(categoryId = null) {
  const modal = document.getElementById('formModal');
  const title = document.getElementById('formTitle');
  const form = document.getElementById('categoryForm');

  form.reset();
  document.getElementById('categoryId').value = '';
  document.getElementById('inputColor').value = '#8B5CF6';
  document.getElementById('inputColorText').value = '#8B5CF6';

  if (categoryId) {
    title.textContent = 'Editar Categoria';
    const category = categories.find(c => c.id === categoryId);
    if (category) {
      document.getElementById('categoryId').value = category.id;
      document.getElementById('inputName').value = category.name || '';
      document.getElementById('inputDescription').value = category.description || '';
      document.getElementById('inputColor').value = category.color || '#8B5CF6';
      document.getElementById('inputColorText').value = category.color || '#8B5CF6';
    }
  } else {
    title.textContent = 'Nova Categoria';
  }

  modal.classList.add('active');
  document.body.style.overflow = 'hidden';
}

function closeForm() {
  document.getElementById('formModal').classList.remove('active');
  document.body.style.overflow = '';
}

async function handleSubmit(event) {
  event.preventDefault();

  const id = document.getElementById('categoryId').value;
  const categoryData = {
    name: document.getElementById('inputName').value.trim(),
    description: document.getElementById('inputDescription').value.trim() || null,
    color: document.getElementById('inputColorText').value.trim() || '#8B5CF6',
  };

  try {
    if (id) {
      await CategoriesAPI.update(parseInt(id), categoryData);
      showToast('Categoria atualizada com sucesso');
    } else {
      await CategoriesAPI.create(categoryData);
      showToast('Categoria criada com sucesso');
    }
    closeForm();
    await loadCategories();
  } catch (e) {
    const msg = e.data?.messages
      ? Object.values(e.data.messages).join(', ')
      : e.data?.message || 'Erro ao salvar categoria';
    showToast(msg, 'error');
  }
}

function editCategory(id) {
  openForm(id);
}

async function deleteCategory(id, name) {
  if (!confirm(`Tem certeza que deseja excluir a categoria "${name}"?`)) return;

  try {
    await CategoriesAPI.delete(id);
    showToast('Categoria excluída com sucesso');
    await loadCategories();
  } catch (e) {
    showToast('Erro ao excluir categoria', 'error');
  }
}
