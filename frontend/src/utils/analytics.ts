export const GA_MEASUREMENT_ID = import.meta.env.VITE_GA_MEASUREMENT_ID;

let initialized = false;

function initializeGtag() {
  if (initialized || !GA_MEASUREMENT_ID) return;

  // Create script element
  const script = document.createElement('script');
  script.async = true;
  script.src = `https://www.googletagmanager.com/gtag/js?id=${GA_MEASUREMENT_ID}`;
  document.head.appendChild(script);

  // Initialize dataLayer and gtag function
  window.dataLayer = window.dataLayer || [];
  window.gtag = function gtag(...args: unknown[]) {
    window.dataLayer.push(args);
  };
  window.gtag('js', new Date());
  window.gtag('config', GA_MEASUREMENT_ID);

  initialized = true;
}

export const pageview = (url: string) => {
  initializeGtag();
  if (GA_MEASUREMENT_ID && window.gtag) {
    window.gtag('config', GA_MEASUREMENT_ID, { page_path: url });
  }
};

export const event = (action: string, params?: Record<string, unknown>) => {
  initializeGtag();
  if (GA_MEASUREMENT_ID && window.gtag) {
    window.gtag('event', action, params);
  }
};
