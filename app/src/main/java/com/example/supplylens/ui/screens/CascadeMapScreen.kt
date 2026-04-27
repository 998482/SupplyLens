package com.supplylens.app.ui.screens.cascademap

import android.annotation.SuppressLint
import android.webkit.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.supplylens.app.ui.theme.*
import com.supplylens.app.viewmodel.DashboardViewModel
import org.json.JSONArray

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CascadeMapScreen(
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    val dashboardState by dashboardViewModel.uiState.collectAsState()
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    LaunchedEffect(dashboardState.affectedNodeIds) {
        val ids = JSONArray(dashboardState.affectedNodeIds.toList()).toString()
        webViewRef?.evaluateJavascript("updateAffected('$ids')", null)
    }

    Box(modifier = Modifier.fillMaxSize().background(NavyBg)) {
        Column(modifier = Modifier.fillMaxSize()) {

            Column(modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)) {
                Text("🌐 Supply Chain Map", color = CyanAccent,
                    fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    "Global logistics network — ${dashboardState.affectedNodeIds.size} disrupted nodes",
                    color = TextMuted, fontSize = 12.sp
                )
            }

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).also { wv ->
                        wv.settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            allowFileAccessFromFileURLs = true
                            allowUniversalAccessFromFileURLs = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        }
                        wv.setBackgroundColor(0xFF0A0F1E.toInt())
                        wv.webChromeClient = WebChromeClient()
                        wv.webViewClient = WebViewClient()

                        // HTML directly load ho raha hai — no file needed!
                        wv.loadDataWithBaseURL(
                            null,
                            getMapHtml(),
                            "text/html",
                            "UTF-8",
                            null
                        )
                        webViewRef = wv
                    }
                }
            )
        }
    }
}
fun getMapHtml(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no">
<style>
*{margin:0;padding:0;box-sizing:border-box;}
html,body{width:100%;height:100%;background:#0D1117;overflow:hidden;}
#c{position:fixed;top:0;left:0;touch-action:none;}
#cursor{
  width:18px;height:18px;border-radius:50%;
  background:#06B6D4;
  position:fixed;pointer-events:none;z-index:999;
  transition:left 1.8s cubic-bezier(.4,0,.2,1),top 1.8s cubic-bezier(.4,0,.2,1);
}
#cursor::after{
  content:'';position:absolute;
  width:34px;height:34px;border-radius:50%;
  border:2px solid rgba(6,182,212,0.5);
  top:-9px;left:-9px;
  animation:ping 1.8s ease-out infinite;
}
@keyframes ping{0%{transform:scale(1);opacity:.8}100%{transform:scale(2.5);opacity:0}}
#tooltip{
  position:fixed;display:none;
  background:rgba(6,10,20,0.97);
  border:1px solid #06B6D4;
  color:#F1F5F9;font-family:monospace;font-size:11px;
  padding:8px 12px;border-radius:8px;
  pointer-events:none;z-index:1000;
  white-space:nowrap;line-height:1.7;
}
#info{
  position:fixed;bottom:14px;left:12px;right:12px;
  background:rgba(6,10,20,0.92);
  border:1px solid #1F2937;
  color:#06B6D4;font-family:monospace;font-size:11px;
  padding:8px 14px;border-radius:8px;z-index:998;
}
#legend{
  position:fixed;top:8px;right:8px;
  background:rgba(6,10,20,0.92);
  border:1px solid #1F2937;
  padding:10px 14px;border-radius:10px;
  font-family:monospace;font-size:10px;
  color:#94A3B8;line-height:2.2;z-index:998;
}
.ldot{display:inline-block;width:10px;height:10px;border-radius:2px;margin-right:7px;vertical-align:middle;}
#zoombtns{position:fixed;right:12px;bottom:70px;display:flex;flex-direction:column;gap:6px;z-index:998;}
.zbtn{
  width:40px;height:40px;border-radius:8px;
  background:rgba(6,10,20,0.92);border:1px solid #1F2937;
  color:#06B6D4;font-size:22px;font-weight:300;
  display:flex;align-items:center;justify-content:center;cursor:pointer;
}
</style>
</head>
<body>
<canvas id="c"></canvas>
<div id="cursor"></div>
<div id="tooltip" id="tt"></div>
<div id="info">🤖 AI scanning network...</div>
<div id="legend">
  <div><span class="ldot" style="background:#3B82F6"></span>Supplier</div>
  <div><span class="ldot" style="background:#EAB308"></span>Factory</div>
  <div><span class="ldot" style="background:#F97316"></span>Warehouse</div>
  <div><span class="ldot" style="background:#22C55E"></span>Customer</div>
  <div><span class="ldot" style="background:#EF4444"></span>Disrupted</div>
</div>
<div id="zoombtns">
  <div class="zbtn" id="zin">+</div>
  <div class="zbtn" id="zout">−</div>
</div>

<script>
const canvas = document.getElementById('c');
const ctx    = canvas.getContext('2d');
const cursorEl  = document.getElementById('cursor');
const infoEl    = document.getElementById('info');
const ttEl      = document.getElementById('tooltip');

// ── Size ──
let W, H;
function resize(){
  W = canvas.width  = window.innerWidth;
  H = canvas.height = window.innerHeight;
}
resize();

// ── Camera state ──
let camX = 0.5;   // centre longitude fraction (0-1)
let camY = 0.42;  // centre latitude fraction
let zoom = 1.8;   // pixels per world-unit (world is 1x1)
const MINZ = 0.9, MAXZ = 18;

// Mercator: lat/lng → world coords (0-1, 0-1)
function toWorld(lat, lng){
  const x = (lng + 180) / 360;
  const φ = lat * Math.PI / 180;
  const y = 0.5 - Math.log(Math.tan(Math.PI/4 + φ/2)) / (2*Math.PI);
  return {x, y};
}

// World → screen
function toScreen(wx, wy){
  return {
    x: (wx - camX) * W * zoom + W/2,
    y: (wy - camY) * H * zoom + H/2
  };
}

function proj(lat, lng){
  const w = toWorld(lat, lng);
  return toScreen(w.x, w.y);
}

// ── Continent outlines (lat,lng pairs) ──
const LAND = [
  // North America
  [[72,-168],[72,-50],[60,-52],[50,-55],[45,-65],[44,-66],[40,-74],
   [25,-80],[20,-87],[15,-85],[9,-80],[8,-77],[9,-79],[15,-92],
   [20,-105],[23,-110],[32,-117],[38,-122],[48,-124],[55,-130],
   [60,-142],[65,-168],[72,-168]],
  // Greenland
  [[83,-45],[83,-12],[76,-18],[70,-24],[65,-40],[66,-54],[72,-54],[78,-70],[83,-45]],
  // South America
  [[12,-72],[10,-62],[8,-60],[4,-52],[0,-50],[-5,-35],[-10,-37],
   [-15,-39],[-23,-43],[-33,-53],[-42,-65],[-55,-68],[-56,-68],
   [-42,-63],[-34,-58],[-20,-40],[-5,-35],[4,-52],[8,-60],[12,-72]],
  // Europe
  [[71,26],[68,16],[58,5],[51,2],[44,0],[36,-6],[36,10],[38,16],
   [42,14],[46,13],[48,18],[52,14],[54,18],[60,25],[65,24],[70,26],[71,26]],
  // Africa
  [[37,10],[37,37],[22,37],[15,42],[12,44],[10,42],[0,42],
   [-10,40],[-20,35],[-35,20],[-35,18],[-26,15],[-10,14],
   [0,9],[5,2],[10,15],[15,16],[20,15],[25,33],[30,32],[35,36],[37,10]],
  // Asia main
  [[72,26],[72,140],[60,140],[50,140],[40,122],[35,120],[25,120],
   [22,114],[10,104],[1,104],[5,100],[10,98],[20,92],[25,90],
   [28,86],[28,72],[20,60],[12,44],[22,37],[37,37],[37,26],
   [50,28],[60,30],[72,26]],
  // Japan (Honshu)
  [[45,142],[43,145],[36,141],[34,136],[35,132],[38,140],[45,142]],
  // Sri Lanka
  [[10,80],[6,80],[6,82],[8,82],[10,80]],
  // Australia
  [[-15,130],[-12,136],[-14,142],[-18,147],[-28,154],[-38,146],
   [-38,140],[-32,116],[-22,114],[-15,130]],
  // New Zealand
  [[-34,172],[-46,168],[-46,170],[-40,176],[-34,172]],
  // UK
  [[58,-5],[58,0],[52,2],[50,0],[52,-5],[58,-5]],
  // Iceland
  [[66,-24],[64,-14],[63,-18],[64,-22],[66,-24]],
  // Borneo
  [[7,116],[4,118],[1,110],[4,108],[7,116]],
  // Sumatra
  [[5,96],[0,104],[-4,105],[1,98],[5,96]],
];

const NODES=[
  {id:'shanghai_supplier',  name:'Shanghai',   type:'supplier',  lat:31.2, lng:121.5},
  {id:'shenzhen_supplier',  name:'Shenzhen',   type:'supplier',  lat:22.5, lng:114.1},
  {id:'taipei_supplier',    name:'Taipei',     type:'supplier',  lat:25.0, lng:121.5},
  {id:'mumbai_supplier',    name:'Mumbai',     type:'supplier',  lat:19.1, lng:72.9},
  {id:'chongqing_factory',  name:'Chongqing',  type:'factory',   lat:29.6, lng:106.6},
  {id:'tijuana_factory',    name:'Tijuana',    type:'factory',   lat:32.5, lng:-117.0},
  {id:'bratislava_factory', name:'Bratislava', type:'factory',   lat:48.1, lng:17.1},
  {id:'la_port',            name:'LA Port',    type:'warehouse', lat:33.7, lng:-118.2},
  {id:'rotterdam_port',     name:'Rotterdam',  type:'warehouse', lat:51.9, lng:4.5},
  {id:'singapore_port',     name:'Singapore',  type:'warehouse', lat:1.3,  lng:103.8},
  {id:'newark_port',        name:'Newark',     type:'warehouse', lat:40.7, lng:-74.2},
  {id:'chicago_customer',   name:'Chicago',    type:'customer',  lat:41.9, lng:-87.6},
  {id:'london_customer',    name:'London',     type:'customer',  lat:51.5, lng:-0.1},
  {id:'tokyo_customer',     name:'Tokyo',      type:'customer',  lat:35.7, lng:139.7},
  {id:'sydney_customer',    name:'Sydney',     type:'customer',  lat:-33.9,lng:151.2}
];

const LINKS=[
  ['shanghai_supplier','la_port'],['shenzhen_supplier','la_port'],
  ['taipei_supplier','la_port'],['mumbai_supplier','rotterdam_port'],
  ['chongqing_factory','la_port'],['tijuana_factory','la_port'],
  ['bratislava_factory','rotterdam_port'],['la_port','chicago_customer'],
  ['rotterdam_port','london_customer'],['singapore_port','tokyo_customer'],
  ['singapore_port','sydney_customer'],['la_port','newark_port'],
  ['newark_port','chicago_customer'],['rotterdam_port','newark_port']
];

const COL={
  supplier:'#3B82F6',factory:'#EAB308',
  warehouse:'#F97316',customer:'#22C55E',affected:'#EF4444'
};

let affectedIds = new Set();
let dash = 0;
let animFrame = 0;

function getNode(id){ return NODES.find(n=>n.id===id); }

// ── Draw ──
function draw(){
  ctx.clearRect(0,0,W,H);

  // Ocean
  ctx.fillStyle = '#0D1117';
  ctx.fillRect(0,0,W,H);

  // Lat/lng grid
  ctx.strokeStyle = 'rgba(30,40,60,0.8)';
  ctx.lineWidth = 0.5;
  for(let lat=-60;lat<=80;lat+=30){
    const p1=proj(lat,-180), p2=proj(lat,180);
    if(p1.x>W+500||p2.x<-500)continue;
    ctx.beginPath();ctx.moveTo(p1.x,p1.y);ctx.lineTo(p2.x,p2.y);ctx.stroke();
  }
  for(let lng=-180;lng<=180;lng+=30){
    const p1=proj(85,lng), p2=proj(-85,lng);
    ctx.beginPath();ctx.moveTo(p1.x,p1.y);ctx.lineTo(p2.x,p2.y);ctx.stroke();
  }

  // Equator
  ctx.strokeStyle='rgba(6,182,212,0.15)';
  ctx.lineWidth=0.8;
  const eq1=proj(0,-180),eq2=proj(0,180);
  ctx.beginPath();ctx.moveTo(eq1.x,eq1.y);ctx.lineTo(eq2.x,eq2.y);ctx.stroke();

  // Land
  LAND.forEach(poly=>{
    ctx.beginPath();
    poly.forEach(([lat,lng],i)=>{
      const p=proj(lat,lng);
      i===0?ctx.moveTo(p.x,p.y):ctx.lineTo(p.x,p.y);
    });
    ctx.closePath();
    ctx.fillStyle='#1C2A3A';
    ctx.fill();
    ctx.strokeStyle='rgba(6,182,212,0.2)';
    ctx.lineWidth=0.6;
    ctx.stroke();
  });

  // Routes
  LINKS.forEach(([a,b])=>{
    const sn=getNode(a),tn=getNode(b);
    if(!sn||!tn)return;
    const sp=proj(sn.lat,sn.lng);
    const tp=proj(tn.lat,tn.lng);
    const isAff=affectedIds.has(a)||affectedIds.has(b);

    ctx.save();
    ctx.setLineDash([6,9]);
    ctx.lineDashOffset = -dash;
    ctx.strokeStyle = isAff?'rgba(239,68,68,0.9)':'rgba(6,182,212,0.35)';
    ctx.lineWidth   = isAff?2:1;

    const mx=(sp.x+tp.x)/2;
    const dy=Math.abs(tp.x-sp.x)*0.2+30;
    const my=Math.min(sp.y,tp.y)-dy;

    ctx.beginPath();
    ctx.moveTo(sp.x,sp.y);
    ctx.quadraticCurveTo(mx,my,tp.x,tp.y);
    ctx.stroke();
    ctx.restore();
  });

  // Nodes
  const dotR = Math.max(5, Math.min(12, zoom*2.5));
  NODES.forEach(node=>{
    const p=proj(node.lat,node.lng);
    if(p.x<-50||p.x>W+50||p.y<-50||p.y>H+50)return;
    const isAff=affectedIds.has(node.id);
    const col=isAff?COL.affected:COL[node.type];

    // Pulse ring for affected
    if(isAff){
      const pulse=0.5+0.5*Math.sin(animFrame*0.08);
      ctx.strokeStyle='rgba(239,68,68,'+(.3+.3*pulse)+')';
      ctx.lineWidth=1.5;
      ctx.beginPath();
      ctx.arc(p.x,p.y,dotR+4+pulse*4,0,Math.PI*2);
      ctx.stroke();
    }

    // Main dot
    ctx.fillStyle=col;
    ctx.beginPath();
    ctx.arc(p.x,p.y,dotR,0,Math.PI*2);
    ctx.fill();

    // White ring
    ctx.strokeStyle='rgba(255,255,255,0.25)';
    ctx.lineWidth=0.8;
    ctx.beginPath();
    ctx.arc(p.x,p.y,dotR+1.5,0,Math.PI*2);
    ctx.stroke();

    // Label
    ctx.fillStyle=isAff?'#EF4444':'#94A3B8';
    ctx.font=(Math.max(9,Math.min(13,zoom*2.2)))+'px monospace';
    ctx.fillText(node.name, p.x+dotR+4, p.y+4);
  });

  dash += 0.3;
  animFrame++;
  requestAnimationFrame(draw);
}
draw();

// ── Touch: pan + pinch zoom ──
let pt1=null, pt2=null, lastMid=null, lastPinchDist=null;

canvas.addEventListener('touchstart', e=>{
  e.preventDefault();
  if(e.touches.length===1){
    pt1={x:e.touches[0].clientX, y:e.touches[0].clientY};
    pt2=null; lastMid=null; lastPinchDist=null;
  } else if(e.touches.length===2){
    pt1={x:e.touches[0].clientX,y:e.touches[0].clientY};
    pt2={x:e.touches[1].clientX,y:e.touches[1].clientY};
    lastPinchDist=Math.hypot(pt2.x-pt1.x,pt2.y-pt1.y);
    lastMid={x:(pt1.x+pt2.x)/2,y:(pt1.y+pt2.y)/2};
  }
},{passive:false});

canvas.addEventListener('touchmove', e=>{
  e.preventDefault();
  if(e.touches.length===1 && pt1 && !pt2){
    const nx=e.touches[0].clientX, ny=e.touches[0].clientY;
    const dx=nx-pt1.x, dy=ny-pt1.y;
    camX -= dx/(W*zoom);
    camY -= dy/(H*zoom);
    pt1={x:nx,y:ny};
  } else if(e.touches.length===2){
    const t1={x:e.touches[0].clientX,y:e.touches[0].clientY};
    const t2={x:e.touches[1].clientX,y:e.touches[1].clientY};
    const dist=Math.hypot(t2.x-t1.x,t2.y-t1.y);
    const mid={x:(t1.x+t2.x)/2,y:(t1.y+t2.y)/2};

    if(lastPinchDist){
      const ratio=dist/lastPinchDist;
      const wBefore={
        x:(mid.x-W/2)/(W*zoom)+camX,
        y:(mid.y-H/2)/(H*zoom)+camY
      };
      zoom=Math.max(MINZ,Math.min(MAXZ,zoom*ratio));
      camX=wBefore.x-(mid.x-W/2)/(W*zoom);
      camY=wBefore.y-(mid.y-H/2)/(H*zoom);
    }
    if(lastMid){
      camX-=(mid.x-lastMid.x)/(W*zoom);
      camY-=(mid.y-lastMid.y)/(H*zoom);
    }
    lastPinchDist=dist;
    lastMid=mid;
    pt1=t1; pt2=t2;
  }
},{passive:false});

canvas.addEventListener('touchend', e=>{
  e.preventDefault();
  if(e.touches.length===0){
    // Tap detection
    if(e.changedTouches.length===1 && !pt2){
      const tx=e.changedTouches[0].clientX;
      const ty=e.changedTouches[0].clientY;
      let found=null, minD=999;
      NODES.forEach(node=>{
        const p=proj(node.lat,node.lng);
        const d=Math.hypot(p.x-tx,p.y-ty);
        if(d<30&&d<minD){minD=d;found=node;}
      });
      if(found){
        const isAff=affectedIds.has(found.id);
        ttEl.style.display='block';
        ttEl.style.left=Math.min(tx+12,W-160)+'px';
        ttEl.style.top=Math.max(ty-60,10)+'px';
        ttEl.style.borderColor=isAff?'#EF4444':'#06B6D4';
        const routes=LINKS.filter(([a,b])=>a===found.id||b===found.id).length;
        ttEl.innerHTML=
          '<b style="color:'+(isAff?'#EF4444':'#06B6D4')+'">'+found.name+'</b><br>'+
          found.type.toUpperCase()+'<br>'+
          'Routes: '+routes+
          (isAff?'<br><span style="color:#EF4444">⚠ DISRUPTED</span>':'');
        setTimeout(()=>ttEl.style.display='none',3000);
      }
    }
    pt1=null; pt2=null; lastMid=null; lastPinchDist=null;
  } else if(e.touches.length===1){
    pt1={x:e.touches[0].clientX,y:e.touches[0].clientY};
    pt2=null; lastMid=null; lastPinchDist=null;
  }
},{passive:false});

// ── Zoom buttons ──
function zoomAt(factor){
  zoom=Math.max(MINZ,Math.min(MAXZ,zoom*factor));
}
document.getElementById('zin').onclick=()=>zoomAt(1.5);
document.getElementById('zout').onclick=()=>zoomAt(1/1.5);

// ── AI Cursor ──
let cIdx=0, scanNodes=[...NODES];

function moveCursor(){
  if(scanNodes.length===0)scanNodes=[...NODES];
  const t=scanNodes[cIdx%scanNodes.length];
  const p=proj(t.lat,t.lng);
  cursorEl.style.left=(p.x-9)+'px';
  cursorEl.style.top=(p.y-9)+'px';
  const isAff=affectedIds.has(t.id);
  cursorEl.style.background=isAff?'#EF4444':'#06B6D4';
  cursorEl.style.boxShadow=isAff
    ?'0 0 0 4px rgba(239,68,68,0.3),0 0 16px #EF4444'
    :'0 0 0 4px rgba(6,182,212,0.3),0 0 16px #06B6D4';
  const routes=LINKS.filter(([a,b])=>a===t.id||b===t.id).length;
  infoEl.innerHTML='🤖 <b style="color:#F1F5F9">'+t.name+'</b>'
    +' · '+t.type+' · '+routes+' routes'
    +(isAff?' · <span style="color:#EF4444">⚠ DISRUPTED</span>':'');
  cIdx++;
}
setTimeout(()=>{ moveCursor(); setInterval(moveCursor,2200); },400);

// ── Android bridge ──
function updateAffected(idsJson){
  try{
    affectedIds=new Set(JSON.parse(idsJson));
    scanNodes=affectedIds.size>0
      ?NODES.filter(n=>affectedIds.has(n.id)):[...NODES];
    cIdx=0;
  }catch(e){}
}

window.addEventListener('resize',()=>{ resize(); });
</script>
</body>
</html>
""".trimIndent()