(() => {
  const canvas = document.getElementById("matrix-rain");
  if (!canvas) return;

  const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)");
  if (reduceMotion.matches) {
    canvas.hidden = true;
    return;
  }

  const ctx = canvas.getContext("2d", { alpha: true });
  if (!ctx) return;

  const glyphs = "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝ01アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン";
  const fontSize = 14;
  let columns = [];
  let width = 0;
  let height = 0;
  let last = 0;
  let raf = 0;
  let running = false;

  function resize() {
    const dpr = Math.min(window.devicePixelRatio || 1, 2);
    width = window.innerWidth;
    height = window.innerHeight;
    canvas.width = Math.floor(width * dpr);
    canvas.height = Math.floor(height * dpr);
    canvas.style.width = `${width}px`;
    canvas.style.height = `${height}px`;
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);

    const count = Math.max(8, Math.floor(width / fontSize));
    columns = Array.from({ length: count }, () => ({
      y: Math.random() * -height,
      speed: 3 + Math.random() * 6,
    }));
  }

  function tick(now) {
    if (!running) return;
    const dt = Math.min(0.05, (now - last) / 1000 || 0.016);
    last = now;

    ctx.fillStyle = "rgba(1, 6, 2, 0.06)";
    ctx.fillRect(0, 0, width, height);

    ctx.font = `${fontSize}px "IBM Plex Mono", ui-monospace, monospace`;
    ctx.fillStyle = "rgba(0, 255, 65, 0.45)";

    for (let i = 0; i < columns.length; i++) {
      const col = columns[i];
      const x = i * fontSize;
      const ch = glyphs[(Math.random() * glyphs.length) | 0];
      ctx.fillText(ch, x, col.y);
      col.y += col.speed * dt * 5;
      if (col.y > height + fontSize * 4) {
        col.y = Math.random() * -height * 0.4;
        col.speed = 3 + Math.random() * 6;
      }
    }

    raf = requestAnimationFrame(tick);
  }

  function start() {
    if (running) return;
    running = true;
    canvas.hidden = false;
    cancelAnimationFrame(raf);
    resize();
    ctx.fillStyle = "#010602";
    ctx.fillRect(0, 0, width, height);
    last = performance.now();
    raf = requestAnimationFrame(tick);
  }

  function stop(hide) {
    running = false;
    cancelAnimationFrame(raf);
    if (hide) canvas.hidden = true;
  }

  reduceMotion.addEventListener("change", (event) => {
    if (event.matches) stop(true);
    else start();
  });

  document.addEventListener("visibilitychange", () => {
    if (document.hidden) stop(false);
    else if (!reduceMotion.matches) start();
  });

  window.addEventListener("resize", () => {
    if (running) resize();
  });

  start();
})();
