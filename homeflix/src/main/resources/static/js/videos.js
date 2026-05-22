
document.addEventListener('DOMContentLoaded', init);

let videos = [];
let categories = [];
let searchTimeout;

async function init() {
  await loadCategories();
  await loadVideos();
}

async function loadCategories() {
  try {
    categories = await CategoriesAPI.findAll();
    renderCategoryFilter();
    renderCategoryCheckboxes();
  } catch (e) {
    console.error('Erro ao carregar categorias:', e);
  }
}

async function loadVideos() {
  try {
    const data = await VideosAPI.findAll(0, 100, 'title,asc');
    videos = data.content || [];
    renderVideoList(videos);
  } catch (e) {
    console.error('Erro ao carregar vídeos:', e);
    document.getElementById('videoList').innerHTML =
      '<div class="empty-state"><div class="empty-state-icon">⚠️</div><p>Erro ao carregar vídeos</p></div>';
  }
}

function renderCategoryFilter() {
  const select = document.getElementById('filterCategory');
  categories.forEach(c => {
    const option = document.createElement('option');
    option.value = c.id;
    option.textContent = c.name;
    select.appendChild(option);
  });
}

function renderCategoryCheckboxes() {
  const container = document.getElementById('categoryCheckboxes');
  container.innerHTML = categories.map(c => `
    <div class="form-check">
      <input type="checkbox" id="cat-${c.id}" value="${c.id}">
      <label for="cat-${c.id}" style="margin:0; font-size:0.85rem; color:var(--text-secondary);">
        <span class="color-dot" style="background:${c.color || 'var(--primary)'}"></span> ${c.name}
      </label>
    </div>
  `).join('');
}

function renderVideoList(videoList) {
  const container = document.getElementById('videoList');

  if (videoList.length === 0) {
    container.innerHTML = `
      <div class="empty-state">
        <div class="empty-state-icon">🎬</div>
        <p>Nenhum vídeo encontrado</p>
      </div>`;
    return;
  }

  container.innerHTML = `
    <div class="table-container">
      <table class="table">
        <thead>
          <tr>
            <th>Título</th>
            <th>Categorias</th>
            <th>Ano</th>
            <th>Duração</th>
            <th>Nota</th>
            <th>Status</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          ${videoList.map(v => renderVideoRow(v)).join('')}
        </tbody>
      </table>
    </div>
  `;
}

function renderVideoRow(video) {
  const cats = video.categories
    ? video.categories.map(c => `<span class="modal-category-tag" style="background:${c.color || 'var(--primary-deep)'}; font-size:0.65rem; padding:0.15rem 0.5rem;">${c.name}</span>`).join(' ')
    : '—';

  const status = [];
  if (video.watched) status.push('<span class="badge badge-watched">Assistido</span>');
  if (video.favorite) status.push('<span class="badge badge-favorite">★</span>');

  return `
    <tr>
      <td style="font-weight:500">${video.title}</td>
      <td>${cats}</td>
      <td>${video.releaseYear || '—'}</td>
      <td>${video.durationMinutes ? formatDuration(video.durationMinutes) : '—'}</td>
      <td>${video.rating ? `★ ${video.rating}` : '—'}</td>
      <td>${status.join(' ') || '—'}</td>
      <td>
        <div class="table-actions">
          <button class="btn btn-secondary btn-icon btn-sm" onclick="editVideo(${video.id})" title="Editar">✏️</button>
          <button class="btn btn-danger btn-icon btn-sm" onclick="deleteVideo(${video.id}, '${video.title.replace(/'/g, "\\'")}')" title="Excluir">🗑️</button>
        </div>
      </td>
    </tr>
  `;
}



function handleSearch() {
  clearTimeout(searchTimeout);
  searchTimeout = setTimeout(async () => {
    const query = document.getElementById('searchInput').value.trim();
    if (query.length > 0) {
      try {
        const data = await VideosAPI.search(query);
        renderVideoList(data.content || []);
      } catch (e) {
        showToast('Erro na busca', 'error');
      }
    } else {
      renderVideoList(videos);
    }
  }, 300);
}

async function handleFilter() {
  const categoryId = document.getElementById('filterCategory').value;
  const searchQuery = document.getElementById('searchInput').value.trim();

  if (categoryId) {
    try {
      const data = await VideosAPI.filter({ categoryId }, 0, 100);
      renderVideoList(data.content || []);
    } catch (e) {
      showToast('Erro ao filtrar', 'error');
    }
  } else if (searchQuery) {
    handleSearch();
  } else {
    renderVideoList(videos);
  }
}



function openForm(videoId = null) {
  const modal = document.getElementById('formModal');
  const title = document.getElementById('formTitle');
  const form = document.getElementById('videoForm');

  form.reset();
  document.getElementById('videoId').value = '';
  document.querySelectorAll('#categoryCheckboxes input[type="checkbox"]').forEach(cb => cb.checked = false);

  if (videoId) {
    title.textContent = 'Editar Vídeo';
    const video = videos.find(v => v.id === videoId);
    if (video) {
      document.getElementById('videoId').value = video.id;
      document.getElementById('inputTitle').value = video.title || '';
      document.getElementById('inputDescription').value = video.description || '';
      document.getElementById('inputFilePath').value = video.filePath || '';
      document.getElementById('inputCoverUrl').value = video.coverImageUrl || '';
      document.getElementById('inputYear').value = video.releaseYear || '';
      document.getElementById('inputRating').value = video.rating || '';
      document.getElementById('inputDuration').value = video.durationMinutes || '';
      document.getElementById('inputWatched').checked = video.watched || false;
      document.getElementById('inputFavorite').checked = video.favorite || false;

      if (video.categories) {
        video.categories.forEach(c => {
          const cb = document.getElementById(`cat-${c.id}`);
          if (cb) cb.checked = true;
        });
      }
    }
  } else {
    title.textContent = 'Novo Vídeo';
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

  const id = document.getElementById('videoId').value;
  const categoryIds = [];
  document.querySelectorAll('#categoryCheckboxes input[type="checkbox"]:checked').forEach(cb => {
    categoryIds.push(parseInt(cb.value));
  });

  const videoData = {
    title: document.getElementById('inputTitle').value.trim(),
    description: document.getElementById('inputDescription').value.trim() || null,
    filePath: document.getElementById('inputFilePath').value.trim() || null,
    coverImageUrl: document.getElementById('inputCoverUrl').value.trim() || null,
    releaseYear: document.getElementById('inputYear').value ? parseInt(document.getElementById('inputYear').value) : null,
    rating: document.getElementById('inputRating').value ? parseFloat(document.getElementById('inputRating').value) : null,
    durationMinutes: document.getElementById('inputDuration').value ? parseInt(document.getElementById('inputDuration').value) : null,
    watched: document.getElementById('inputWatched').checked,
    favorite: document.getElementById('inputFavorite').checked,
    categoryIds: categoryIds.length > 0 ? categoryIds : null,
  };

  try {
    if (id) {
      await VideosAPI.update(parseInt(id), videoData);
      showToast('Vídeo atualizado com sucesso');
    } else {
      await VideosAPI.create(videoData);
      showToast('Vídeo cadastrado com sucesso');
    }
    closeForm();
    await loadVideos();
  } catch (e) {
    const msg = e.data?.messages
      ? Object.values(e.data.messages).join(', ')
      : e.data?.message || 'Erro ao salvar vídeo';
    showToast(msg, 'error');
  }
}

function editVideo(id) {
  openForm(id);
}

async function deleteVideo(id, title) {
  if (!confirm(`Tem certeza que deseja excluir "${title}"?`)) return;

  try {
    await VideosAPI.delete(id);
    showToast('Vídeo excluído com sucesso');
    await loadVideos();
  } catch (e) {
    showToast('Erro ao excluir vídeo', 'error');
  }
}
