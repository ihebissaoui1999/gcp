import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private _keycloak : Keycloak | undefined ;
  constructor() { }
  get keycloak() {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: 'http://34.81.202.204:8080/auth',
        realm: 'devoteam',
        clientId: 'DEVOTEAM'
      });

    }
    return this._keycloak;
  }

  async init() {
    const authenticated = await this.keycloak.init({
      onLoad: 'login-required',

    });
    if (authenticated) {
      localStorage.setItem('keycloak-token', this.keycloak.token || '');
      const tokenParsed: any = this.keycloak.tokenParsed;
      if (tokenParsed && tokenParsed.sub) {
        localStorage.setItem('userId', tokenParsed.sub);
      }
      const roles: string[] = tokenParsed.realm_access?.roles || []; // Déclare le type de roles comme tableau de chaînes
      // Filtrer les rôles pour exclure 'offline_access', 'uma_authorization' et 'default-roles-bnns'
      const filteredRoles = roles.filter((role: string) => !['offline_access', 'uma_authorization', 'default-roles-bnns'].includes(role));

      // Si un rôle est trouvé (comme 'Manager'), le stocker dans le localStorage
      if (filteredRoles.length) {
        localStorage.setItem('userRoles', JSON.stringify(filteredRoles)); // Stocke uniquement le premier rôle trouvé, sans crochets ni guillemets
      }



      setInterval(() => {
        this.refreshToken(30);
      },60000);
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
    return this.keycloak.logout({redirectUri: 'http://35.201.225.147:80'});
  }

  accountManagement() {
    return this.keycloak.accountManagement();
  }

}
