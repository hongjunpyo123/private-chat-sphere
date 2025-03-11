const CACHE_NAME = '채팅앱-v1';
const urlsToCache = [
  '/',
  '/css/style.css',
  '/js/main.js',
  '/index.html',
  // 추가적인 리소스 파일들
];

// 서비스 워커 설치 및 캐시
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => {
        return cache.addAll(urlsToCache);
      })
  );
});

// 네트워크 요청 가로채기
self.addEventListener('fetch', (event) => {
  event.respondWith(
    caches.match(event.request)
      .then((response) => {
        // 캐시에서 찾으면 반환, 아니면 네트워크 요청
        return response || fetch(event.request)
          .then((response) => {
            // 중요한 요청은 캐시에 추가
            if (event.request.url.indexOf('http') === 0) {
              let responseClone = response.clone();
              caches.open(CACHE_NAME)
                .then((cache) => {
                  cache.put(event.request, responseClone);
                });
            }
            return response;
          });
      })
  );
});

// 이전 캐시 정리
self.addEventListener('activate', (event) => {
  const cacheWhitelist = [CACHE_NAME];
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cacheName) => {
          if (cacheWhitelist.indexOf(cacheName) === -1) {
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
});