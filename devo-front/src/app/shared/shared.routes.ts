import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { SigninComponent } from './pages/signin/signin.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { MainComponent } from '../Chats/component/pages/main/main.component';
import { AddticketComponent } from '../ticket/componant/addticket/addticket.component';
import { FindTicketsAssignedToUserComponent } from '../ticket/componant/find-tickets-assigned-to-user/find-tickets-assigned-to-user.component';
import { MatirielComponent } from '../matiriel/matiriel.component';
import { CreatePurchaseRequestComponent } from '../purchaseRequest/create-purchase-request/create-purchase-request.component';
import { StockComponent } from '../stock/stock/stock.component';


export const sharedRoutes: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent
  },
  {
    path: 'signin',
    component: SigninComponent
  },
  {
    path: 'sidebar',
    component: SidebarComponent
  },
  {
    path:'chats',
    component: MainComponent
  },
  {
    path:'ticket',
    component: AddticketComponent
  },
  {
    path:'ticketrecived',
    component: FindTicketsAssignedToUserComponent
  },
  {
    path:'MaTirial',
    component: MatirielComponent
  },  
  {
    path:'Purchase',
    component: CreatePurchaseRequestComponent
  },
  {
    path:'stock',
    component: StockComponent
  },

];