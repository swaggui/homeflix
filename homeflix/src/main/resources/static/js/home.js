document.addEventListener('DOMContentLoaded', init);

let allVideos = [];
let allCategories = [];

async function init() {
  setupNavbar();
  setupModal();

  try {
    const [categoriesData, videosData] = await Promise.all([
      CategoriesAPI.findAll(),
      VideosAPI.findAll(0, 100, 'title,asc'),
    ]);

    allCategories = categoriesData;
    allVideos = videosData.content || [];

    updateStats();
    renderCatalog();
  } catch (error) {
    console.error('Erro ao carregar dados:', error);
    document.getElementById('catalog-loading').innerHTML =
      '<div class="empty-state"><div class="empty-state-icon">⚠️</div><p>Erro ao carregar catálogo</p></div>';
  }

  runPreloaderAnimation();
}

function runPreloaderAnimation() {
  if (typeof gsap === 'undefined') {
    const preloader = document.getElementById('preloader');
    preloader.classList.add('hidden');
    setTimeout(() => { preloader.style.display = 'none'; }, 600);
    generateParticles();
    return;
  }

  const tl = gsap.timeline({
    onComplete() {
      gsap.to('#preloader', {
        opacity: 0,
        duration: 0.5,
        onComplete() {
          document.getElementById('preloader').style.display = 'none';
          generateParticles();
          runGSAPAnimations();
        },
      });
    },
  });

  tl.to('#preloader-text', {
    strokeDashoffset: 0,
    duration: 2,
    ease: 'power2.inOut',
  });

  tl.to('#preloader-text', {
    fill: 'url(#logoGradient)',
    duration: 0.6,
    ease: 'power1.in',
  }, '-=0.3');

  tl.to('#preloader-svg', {
    filter: 'drop-shadow(0 0 20px rgba(139, 92, 246, 0.6)) drop-shadow(0 0 60px rgba(139, 92, 246, 0.3))',
    duration: 0.5,
    ease: 'power1.out',
  }, '-=0.3');

  tl.to('.preloader-tagline', {
    opacity: 1,
    y: 0,
    duration: 0.5,
    ease: 'power2.out',
  }, '-=0.2');

  tl.to({}, { duration: 0.6 });
}

function generateParticles() {
  const container = document.getElementById('heroParticles');
  if (!container) return;

  for (let i = 0; i < 20; i++) {
    const particle = document.createElement('div');
    particle.className = 'particle';
    particle.style.left = Math.random() * 100 + '%';
    particle.style.width = (Math.random() * 4 + 2) + 'px';
    particle.style.height = particle.style.width;
    particle.style.animationDuration = (Math.random() * 10 + 8) + 's';
    particle.style.animationDelay = (Math.random() * 8) + 's';
    particle.style.opacity = Math.random() * 0.3 + 0.1;
    container.appendChild(particle);
  }
}

function runGSAPAnimations() {
  if (typeof gsap === 'undefined') return;

  gsap.registerPlugin(ScrollTrigger);

  const heroTL = gsap.timeline({ defaults: { ease: 'power3.out' } });

  heroTL
    .from('.hero-content h1', {
      y: 60,
      opacity: 0,
      duration: 1,
    })
    .from('.hero-content p', {
      y: 40,
      opacity: 0,
      duration: 0.8,
    }, '-=0.5')
    .from('.hero-stat', {
      y: 30,
      opacity: 0,
      duration: 0.6,
      stagger: 0.15,
    }, '-=0.4');

  gsap.from('.navbar', {
    y: -20,
    opacity: 0,
    duration: 0.8,
    ease: 'power2.out',
  });

  const sections = document.querySelectorAll('.section');
  sections.forEach((section) => {
    gsap.from(section.querySelector('.section-header'), {
      x: -40,
      opacity: 0,
      duration: 0.7,
      ease: 'power2.out',
      scrollTrigger: {
        trigger: section,
        start: 'top 85%',
        toggleActions: 'play none none none',
      },
    });

    const cards = section.querySelectorAll('.video-card');
    gsap.from(cards, {
      y: 50,
      opacity: 0,
      duration: 0.5,
      stagger: 0.08,
      ease: 'power2.out',
      scrollTrigger: {
        trigger: section,
        start: 'top 80%',
        toggleActions: 'play none none none',
      },
    });
  });

  gsap.to('.hero-content', {
    y: 80,
    opacity: 0.3,
    ease: 'none',
    scrollTrigger: {
      trigger: '.hero',
      start: 'top top',
      end: 'bottom top',
      scrub: true,
    },
  });

  gsap.to('.hero::before', {
    opacity: 0.8,
    duration: 3,
    repeat: -1,
    yoyo: true,
    ease: 'sine.inOut',
  });
}

function updateStats() {
  const total = allVideos.length;
  const watched = allVideos.filter(v => v.watched).length;
  const favorites = allVideos.filter(v => v.favorite).length;

  animateNumber('stat-total', total);
  animateNumber('stat-watched', watched);
  animateNumber('stat-favorites', favorites);
}

function animateNumber(elementId, target) {
  const el = document.getElementById(elementId);
  if (typeof gsap !== 'undefined') {
    const obj = { val: 0 };
    gsap.to(obj, {
      val: target,
      duration: 1.5,
      ease: 'power2.out',
      delay: 0.8,
      onUpdate: () => {
        el.textContent = Math.round(obj.val);
      },
    });
  } else {
    el.textContent = target;
  }
}

function renderCatalog() {
  const catalog = document.getElementById('catalog');
  catalog.innerHTML = '';

  if (allVideos.length === 0) {
    catalog.innerHTML = `
      <div class="empty-state">
        <div class="empty-state-icon">🎬</div>
        <p>Nenhum vídeo cadastrado ainda</p>
        <a href="/videos.html" class="btn btn-primary">Adicionar Vídeos</a>
      </div>
    `;
    return;
  }

  renderSection(catalog, 'Todos os Vídeos', allVideos, null);

  const favorites = allVideos.filter(v => v.favorite);
  if (favorites.length > 0) {
    renderSection(catalog, '⭐ Favoritos', favorites, null);
  }

  const unwatched = allVideos.filter(v => !v.watched);
  if (unwatched.length > 0) {
    renderSection(catalog, '🆕 Não Assistidos', unwatched, null);
  }

  allCategories.forEach(category => {
    const categoryVideos = allVideos.filter(v =>
      v.categories && v.categories.some(c => c.id === category.id)
    );
    if (categoryVideos.length > 0) {
      renderSection(catalog, category.name, categoryVideos, category.color);
    }
  });
}

function renderSection(container, title, videos, color) {
  const section = document.createElement('section');
  section.className = 'section';

  const colorDot = color ? `<span class="category-dot" style="background:${color}"></span>` : '';

  section.innerHTML = `
    <div class="section-header">
      <h2 class="section-title">${colorDot} ${title}</h2>
      <span class="section-see-all">${videos.length} vídeos</span>
    </div>
    <div class="carousel-container">
      <button class="carousel-btn left" onclick="scrollCarousel(this, -1)">‹</button>
      <div class="carousel">
        ${videos.map(v => createVideoCard(v)).join('')}
      </div>
      <button class="carousel-btn right" onclick="scrollCarousel(this, 1)">›</button>
    </div>
  `;

  container.appendChild(section);
}

function createVideoCard(video) {
  const poster = video.coverImageUrl
    ? `<img src="${video.coverImageUrl}" alt="${video.title}" loading="lazy">`
    : `<div class="placeholder-poster">${getPosterEmoji(video.title)}</div>`;

  const badges = [];
  if (video.watched) badges.push('<span class="badge badge-watched">Assistido</span>');
  if (video.favorite) badges.push('<span class="badge badge-favorite">★</span>');

  const meta = [];
  if (video.releaseYear) meta.push(video.releaseYear);
  if (video.durationMinutes) meta.push(formatDuration(video.durationMinutes));
  if (video.rating) meta.push(`★ ${video.rating}`);

  return `
    <div class="video-card" onclick="openVideoModal(${video.id})">
      <div class="video-card-poster">
        ${poster}
        <div class="video-card-badges">${badges.join('')}</div>
        <div class="video-card-overlay">
          <div class="overlay-actions">
            <button class="overlay-btn ${video.watched ? 'active' : ''}" onclick="event.stopPropagation(); toggleWatched(${video.id})" title="Marcar como assistido">✓</button>
            <button class="overlay-btn ${video.favorite ? 'active' : ''}" onclick="event.stopPropagation(); toggleFavorite(${video.id})" title="Favoritar">★</button>
          </div>
        </div>
      </div>
      <div class="video-card-info">
        <div class="video-card-title">${video.title}</div>
        <div class="video-card-meta">${meta.join(' · ')}</div>
      </div>
    </div>
  `;
}

function scrollCarousel(btn, direction) {
  const carousel = btn.parentElement.querySelector('.carousel');
  const scrollAmount = 600;
  carousel.scrollBy({ left: direction * scrollAmount, behavior: 'smooth' });
}

async function toggleWatched(id) {
  try {
    const updated = await VideosAPI.toggleWatched(id);
    const idx = allVideos.findIndex(v => v.id === id);
    if (idx !== -1) allVideos[idx] = updated;
    updateStats();
    renderCatalog();
    showToast(updated.watched ? 'Marcado como assistido' : 'Desmarcado como assistido');
  } catch (e) {
    showToast('Erro ao atualizar', 'error');
  }
}

async function toggleFavorite(id) {
  try {
    const updated = await VideosAPI.toggleFavorite(id);
    const idx = allVideos.findIndex(v => v.id === id);
    if (idx !== -1) allVideos[idx] = updated;
    updateStats();
    renderCatalog();
    showToast(updated.favorite ? 'Adicionado aos favoritos' : 'Removido dos favoritos');
  } catch (e) {
    showToast('Erro ao atualizar', 'error');
  }
}

function setupModal() {
  const backdrop = document.getElementById('videoModal');
  const closeBtn = document.getElementById('modalClose');

  closeBtn.addEventListener('click', closeModal);
  backdrop.addEventListener('click', (e) => {
    if (e.target === backdrop) closeModal();
  });
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') closeModal();
  });
}

async function openVideoModal(id) {
  const video = allVideos.find(v => v.id === id);
  if (!video) return;

  const backdrop = document.getElementById('videoModal');
  const playerContainer = document.getElementById('modalPlayer');
  const titleEl = document.getElementById('modalTitle');
  const metaEl = document.getElementById('modalMeta');
  const descEl = document.getElementById('modalDescription');
  const categoriesEl = document.getElementById('modalCategories');
  const actionsEl = document.getElementById('modalActions');

  if (video.filePath) {
    if (isDriveUrl(video.filePath)) {
      const embedUrl = getDriveEmbedUrl(video.filePath);
      playerContainer.innerHTML = `<iframe src="${embedUrl}" allow="autoplay; encrypted-media" allowfullscreen></iframe>`;
    } else {
      playerContainer.innerHTML = `<video src="${video.filePath}" controls></video>`;
    }
  } else {
    playerContainer.innerHTML = `<div class="no-video"><span style="font-size:3rem">🎬</span><span>Nenhum arquivo de vídeo vinculado</span></div>`;
  }

  titleEl.textContent = video.title;
  descEl.textContent = video.description || 'Sem descrição';

  const metaItems = [];
  if (video.releaseYear) metaItems.push(`📅 ${video.releaseYear}`);
  if (video.durationMinutes) metaItems.push(`⏱️ ${formatDuration(video.durationMinutes)}`);
  if (video.rating) metaItems.push(`⭐ ${video.rating}/10`);
  metaEl.innerHTML = metaItems.map(m => `<span class="modal-meta-item">${m}</span>`).join('');

  if (video.categories && video.categories.length > 0) {
    categoriesEl.innerHTML = video.categories.map(c =>
      `<span class="modal-category-tag" style="background:${c.color || 'var(--primary-deep)'}">${c.name}</span>`
    ).join('');
  } else {
    categoriesEl.innerHTML = '<span class="modal-meta-item" style="color:var(--text-muted)">Sem categoria</span>';
  }

  actionsEl.innerHTML = `
    <button class="btn ${video.watched ? 'btn-primary' : 'btn-secondary'}" onclick="toggleWatchedModal(${video.id})">
      ${video.watched ? '✓ Assistido' : '○ Marcar como assistido'}
    </button>
    <button class="btn ${video.favorite ? 'btn-primary' : 'btn-secondary'}" onclick="toggleFavoriteModal(${video.id})">
      ${video.favorite ? '★ Favorito' : '☆ Favoritar'}
    </button>
  `;

  backdrop.classList.add('active');
  document.body.style.overflow = 'hidden';

  if (typeof gsap !== 'undefined') {
    gsap.from('.modal', {
      scale: 0.9,
      opacity: 0,
      duration: 0.4,
      ease: 'back.out(1.7)',
    });
  }
}

function closeModal() {
  const backdrop = document.getElementById('videoModal');

  if (typeof gsap !== 'undefined') {
    gsap.to('.modal', {
      scale: 0.9,
      opacity: 0,
      duration: 0.25,
      ease: 'power2.in',
      onComplete: () => {
        backdrop.classList.remove('active');
        document.body.style.overflow = '';
        gsap.set('.modal', { clearProps: 'all' });
        const player = document.getElementById('modalPlayer');
        player.innerHTML = '<div class="no-video"><span>🎬</span></div>';
      },
    });
  } else {
    backdrop.classList.remove('active');
    document.body.style.overflow = '';
    const player = document.getElementById('modalPlayer');
    player.innerHTML = '<div class="no-video"><span>🎬</span></div>';
  }
}

async function toggleWatchedModal(id) {
  await toggleWatched(id);
  openVideoModal(id);
}

async function toggleFavoriteModal(id) {
  await toggleFavorite(id);
  openVideoModal(id);
}

function setupNavbar() {
  window.addEventListener('scroll', () => {
    const navbar = document.getElementById('navbar');
    if (window.scrollY > 50) {
      navbar.classList.add('scrolled');
    } else {
      navbar.classList.remove('scrolled');
    }
  });
}
