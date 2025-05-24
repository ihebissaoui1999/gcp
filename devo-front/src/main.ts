import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { KeycloakService } from './app/utils/keycloak/keycloak.service';
import { ɵPLATFORM_BROWSER_ID as PLATFORM_BROWSER_ID } from '@angular/common'; // Angular internal constant

// Provide PLATFORM_ID manually
const keycloakService = new KeycloakService(PLATFORM_BROWSER_ID);

async function main() {
  if (typeof window !== 'undefined' && window.crypto?.subtle) {
    try {
      await keycloakService.init();
      console.log('Keycloak initialized successfully');
    } catch (err) {
      console.error('Keycloak init failed', err);
    }
  } else {
    console.warn('Not in browser environment - skipping Keycloak init');
  }

  const providers = [
    ...(appConfig.providers || []),
    { provide: KeycloakService, useValue: keycloakService }
  ];

  bootstrapApplication(AppComponent, {
    ...appConfig,
    providers
  }).catch((err) => console.error(err));
}

main();
