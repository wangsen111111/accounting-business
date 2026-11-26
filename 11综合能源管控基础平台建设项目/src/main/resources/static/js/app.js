(function(){function tick(){var e=document.getElementById('clock');if(e){var d=new Date();e.textContent=d.toLocaleTimeString('zh-CN',{hour12:false});}}tick();setInterval(tick,1000);})();
