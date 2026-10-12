(() => {
  const dialog = document.getElementById("signin-dialog");

  document.querySelectorAll("[data-close-dialog]").forEach((btn) => {
    btn.addEventListener("click", () => dialog?.close());
  });

  document.querySelectorAll("[data-vote]").forEach((button) => {
    button.addEventListener("click", async (event) => {
      event.preventDefault();
      event.stopPropagation();
      const projectId = button.getAttribute("data-vote");
      const signedIn = button.getAttribute("data-signed-in") === "true";
      if (!signedIn) {
        dialog?.showModal();
        return;
      }
      const csrf = document.querySelector('meta[name="_csrf"]');
      const csrfHeader = document.querySelector('meta[name="_csrf_header"]');
      const headers = { Accept: "application/json" };
      if (csrf && csrfHeader) {
        headers[csrfHeader.content] = csrf.content;
      }
      try {
        const response = await fetch(`/projects/${encodeURIComponent(projectId)}/votes`, {
          method: "POST",
          headers,
          credentials: "same-origin",
        });
        if (response.status === 401) {
          dialog?.showModal();
          return;
        }
        if (!response.ok) {
          return;
        }
        const data = await response.json();
        button.classList.toggle("voted", !!data.voted);
        const count = button.querySelector("[data-vote-count]");
        if (count) {
          count.textContent = String(data.voteCount);
        }
      } catch (_) {
        /* ignore network errors for optimistic UI */
      }
    });
  });

  document.querySelectorAll("[data-share]").forEach((button) => {
    button.addEventListener("click", async () => {
      const url = button.getAttribute("data-share") || window.location.href;
      const title = button.getAttribute("data-share-title") || document.title;
      try {
        if (navigator.share) {
          await navigator.share({ title, url });
        } else if (navigator.clipboard) {
          await navigator.clipboard.writeText(url);
          button.textContent = "copied";
          setTimeout(() => (button.textContent = "share"), 1500);
        }
      } catch (_) {
        /* user cancelled */
      }
    });
  });

  const lightbox = document.getElementById("screenshot-lightbox");
  const lightboxImg = lightbox?.querySelector("img");
  document.querySelectorAll("[data-lightbox]").forEach((thumb) => {
    thumb.addEventListener("click", () => {
      if (!lightbox || !lightboxImg) return;
      lightboxImg.src = thumb.getAttribute("data-lightbox");
      lightboxImg.alt = thumb.getAttribute("alt") || "Screenshot";
      lightbox.showModal();
    });
  });
  lightbox?.querySelector("[data-close-dialog]")?.addEventListener("click", () => lightbox.close());
})();
