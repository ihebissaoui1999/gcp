import { Routes } from '@angular/router';
import { MainComponent } from './Chats/component/pages/main/main.component';

export const routes: Routes = [
    {
        path:'',
        redirectTo: 'dashboard',
        pathMatch: 'full'
    },
    
    
    {
        path: '',
        loadChildren: () => import('./shared/shared.routes').then((m) => m.sharedRoutes),
    },
    {
        path: '',
        loadChildren: () => import('./consultant/consultant.routes').then((m) => m.consultantsRoutes),
    },
    {
        path: '',
        loadChildren: () => import('./manager/manager.routes').then((m) => m.managersRoutes),
    },
    {
        path: '',
        loadChildren: () => import('./salesman/salesman.routes').then((m) => m.salesmanRoutes),
    },

    // ...sharedRoutes,
];


