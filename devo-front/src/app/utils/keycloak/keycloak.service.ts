import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import Keycloak from 'keycloak-js';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private _keycloak: Keycloak | undefined;
  private initialized = false; 

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

  get keycloak() {
    if (!this._keycloak && isPlatformBrowser(this.platformId)) {
      this._keycloak = new Keycloak({
        url: 'https://35.229.216.62:8443/',
        realm: 'devoteam',
        clientId: 'devoteam',
      });
    }
    if (!this._keycloak) {
      throw new Error('Keycloak is not initialized – this may be running on the server.');
    }
    return this._keycloak;
    
  }

  async init() {
    if (this.initialized) {
      // Already initialized, do nothing
      return;
    }

    if (!isPlatformBrowser(this.platformId)) {
      return;
    }
    this.initialized = true;

    const isSecure = window.location.protocol === 'https:' || window.location.hostname === 'localhost';

    try {
      const authenticated = await this.keycloak.init({
        onLoad: 'login-required',
        redirectUri: window.location.origin,
        flow: isSecure ? 'standard' : 'implicit',
        //checkLoginIframe: false
      });

      if (authenticated) {
        localStorage.setItem('keycloak-token', this.keycloak.token || '');
        const tokenParsed: any = this.keycloak.tokenParsed;
        if (tokenParsed?.sub) {
          localStorage.setItem('userId', tokenParsed.sub);
        }

        const roles: string[] = tokenParsed.realm_access?.roles || [];
        const filteredRoles = roles.filter(role => !['offline_access', 'uma_authorization', 'default-roles-bnns'].includes(role));
        if (filteredRoles.length) {
          localStorage.setItem('userRoles', JSON.stringify(filteredRoles));
        }

        setInterval(() => this.refreshToken(30), 60000);
      }
    } catch (error) {
      console.error('Keycloak init failed', error);
    }
  }
async refreshToken(minValidity = 30): Promise<boolean> {
  if (!this.keycloak) return false;
  try {
    const refreshed = await this.keycloak.updateToken(minValidity);
    if (refreshed) {
      console.log('Token rafraîchi avec succès');
      localStorage.setItem('keycloak-token', this.keycloak.token || '');
      localStorage.setItem('keycloak-refresh-token', this.keycloak.refreshToken || ''); // Met à jour le refresh token

    }
    return true;
  } catch (error) {
    console.error('Erreur lors du refresh token', error);
    this.logout();
    return false;
  }
}

  async login() {
    await this.keycloak.login();
  }

  get userId(): string {
    return this.keycloak?.tokenParsed?.sub as string;
  }

  get isTokenValid(): boolean {
    return !this.keycloak.isTokenExpired();
  }

  get fullName(): string {
    return this.keycloak.tokenParsed?.['name'] as string;
  }

  logout() {
    return this.keycloak.logout({redirectUri: window.location.origin});
  }

  accountManagement() {
    return this.keycloak.accountManagement();
  }

}
