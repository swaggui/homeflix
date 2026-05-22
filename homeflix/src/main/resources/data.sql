
INSERT INTO categories (name, description, color)
SELECT 'Ação', 'Filmes e vídeos de ação', '#E53935'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Ação');

INSERT INTO categories (name, description, color)
SELECT 'Comédia', 'Filmes e vídeos de comédia', '#FDD835'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Comédia');

INSERT INTO categories (name, description, color)
SELECT 'Drama', 'Filmes e vídeos de drama', '#1E88E5'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Drama');

INSERT INTO categories (name, description, color)
SELECT 'Documentário', 'Documentários diversos', '#43A047'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Documentário');

INSERT INTO categories (name, description, color)
SELECT 'Terror', 'Filmes e vídeos de terror', '#6A1B9A'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Terror');

INSERT INTO categories (name, description, color)
SELECT 'Tutorial', 'Tutoriais e aulas', '#FF8F00'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Tutorial');

INSERT INTO categories (name, description, color)
SELECT 'Anime', 'Animes e animações japonesas', '#E91E63'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Anime');

INSERT INTO categories (name, description, color)
SELECT 'Série', 'Séries de TV', '#00ACC1'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Série');
