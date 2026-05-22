const API_BASE = '/api';

async function apiRequest(endpoint, options = {}) {
  const url = `${API_BASE}${endpoint}`;
  const config = {
    headers: {
      'Content-Type': 'application/json',
    },
    ...options,
  };

  try {
    const response = await fetch(url, config);

    if (response.status === 204) {
      return null;
    }

    const data = await response.json();

    if (!response.ok) {
      throw { status: response.status, data };
    }

    return data;
  } catch (error) {
    if (error.status) throw error;
    console.error('Erro de rede:', error);
    throw { status: 0, data: { message: 'Erro de conexão com o servidor' } };
  }
}

const VideosAPI = {
  async findAll(page = 0, size = 20, sort = 'title,asc') {
    return apiRequest(`/videos?page=${page}&size=${size}&sort=${sort}`);
  },

  async findById(id) {
    return apiRequest(`/videos/${id}`);
  },

  async search(title, page = 0, size = 20) {
    return apiRequest(`/videos/search?title=${encodeURIComponent(title)}&page=${page}&size=${size}`);
  },

  async filter(params = {}, page = 0, size = 20) {
    let query = `page=${page}&size=${size}`;
    if (params.watched !== undefined && params.watched !== null) query += `&watched=${params.watched}`;
    if (params.favorite !== undefined && params.favorite !== null) query += `&favorite=${params.favorite}`;
    if (params.categoryId) query += `&categoryId=${params.categoryId}`;
    return apiRequest(`/videos/filter?${query}`);
  },

  async create(videoData) {
    return apiRequest('/videos', {
      method: 'POST',
      body: JSON.stringify(videoData),
    });
  },

  async update(id, videoData) {
    return apiRequest(`/videos/${id}`, {
      method: 'PUT',
      body: JSON.stringify(videoData),
    });
  },

  async toggleWatched(id) {
    return apiRequest(`/videos/${id}/watched`, { method: 'PATCH' });
  },

  async toggleFavorite(id) {
    return apiRequest(`/videos/${id}/favorite`, { method: 'PATCH' });
  },

  async delete(id) {
    return apiRequest(`/videos/${id}`, { method: 'DELETE' });
  },
};

const CategoriesAPI = {
  async findAll() {
    return apiRequest('/categories');
  },

  async findById(id) {
    return apiRequest(`/categories/${id}`);
  },

  async findVideos(id, page = 0, size = 20) {
    return apiRequest(`/categories/${id}/videos?page=${page}&size=${size}`);
  },

  async create(categoryData) {
    return apiRequest('/categories', {
      method: 'POST',
      body: JSON.stringify(categoryData),
    });
  },

  async update(id, categoryData) {
    return apiRequest(`/categories/${id}`, {
      method: 'PUT',
      body: JSON.stringify(categoryData),
    });
  },

  async delete(id) {
    return apiRequest(`/categories/${id}`, { method: 'DELETE' });
  },
};

function getDriveEmbedUrl(url) {
  if (!url) return null;

  let match = url.match(/\/file\/d\/([a-zA-Z0-9_-]+)/);
  if (match) {
    return `https://drive.google.com/file/d/${match[1]}/preview`;
  }

  match = url.match(/[?&]id=([a-zA-Z0-9_-]+)/);
  if (match) {
    return `https://drive.google.com/file/d/${match[1]}/preview`;
  }

  return null;
}

function isDriveUrl(url) {
  return url && url.includes('drive.google.com');
}

function formatDuration(minutes) {
  if (!minutes) return '';
  if (minutes < 60) return `${minutes}min`;
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return m > 0 ? `${h}h ${m}min` : `${h}h`;
}

function showToast(message, type = 'success') {
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  const icon = type === 'success' ? '✓' : '✕';
  toast.innerHTML = `<span>${icon}</span> ${message}`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    setTimeout(() => toast.remove(), 300);
  }, 3000);
}

function getPosterEmoji(title) {
  const emojis = ['🎬', '🎥', '📽️', '🎞️', '🎭', '🎪', '🎯', '🎮', '🎵', '🎸'];
  const index = title ? title.charCodeAt(0) % emojis.length : 0;
  return emojis[index];
}
