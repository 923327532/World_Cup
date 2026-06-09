import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';

// SockJS expects a Node-like global object in some bundles.
if (!(window as any).global) {
  (window as any).global = window;
}

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch((err: any) => console.error(err));
