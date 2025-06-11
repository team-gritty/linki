const jsonServer = require('json-server');
const server = jsonServer.create();
const router = jsonServer.router('db.json');
const middlewares = jsonServer.defaults();
// const rewriter = jsonServer.rewriter('routes.json'); // ✅ 요거 복구됨

// 👉 CORS 직접 설정
server.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, PATCH, DELETE, OPTIONS');
  res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Authorization');
  res.header('Access-Control-Allow-Credentials', 'true');
  
  // OPTIONS 요청 처리
  if (req.method === 'OPTIONS') {
    return res.sendStatus(200);
  }
  next();
});

server.use(middlewares);
// server.use(rewriter);  
server.use('/v1/api', router);          // 유저용
server.use('/v1/admin/api', router);    // 관리자용

server.use((req, res, next) => {
  console.log('[요청 받음]', req.method, req.url);
  next();
});


server.listen(3000, '0.0.0.0', () => {
  console.log('JSON Server is running on port 3000');
});