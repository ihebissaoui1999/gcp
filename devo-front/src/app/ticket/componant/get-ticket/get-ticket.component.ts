import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ticketService } from '../../../services/services/ticket.service';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { FullCalendarModule } from '@fullcalendar/angular';
import { CalendarOptions } from '@fullcalendar/core/index.js';
import dayGridPlugin from '@fullcalendar/daygrid';
@Component({
  selector: 'app-get-ticket',
  imports: [CommonModule,SidebarComponent,FullCalendarModule],
  templateUrl: './get-ticket.component.html',
  styleUrl: './get-ticket.component.css'
})
export class GetTicketComponent implements OnInit {
  tickets: any[] = [];
  iduser: string | null = localStorage.getItem('userId');
  errorMessage: string | null = null;
  
  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin],
    initialView: 'dayGridMonth',
    weekends: true,
    events: []  // To be populated with tickets
  };


constructor(private service :ticketService,private cdr :ChangeDetectorRef){}
  ngOnInit(): void {
    this.getTicketbyuser();
    throw new Error('Method not implemented.');
  }

getTicketbyuser(){
  if (!this.iduser) {
    this.errorMessage = 'Utilisateur non connecté';
    return;
  }
  console.log('ID utilisateur:', this.iduser);

  this.service.getticketbyuser(this.iduser).subscribe(
    (response ) => {
      this.tickets = response ;
      console.log('Tickets reçus:', response );
      console.log(this.tickets); 
      this.cdr.detectChanges();
      this.calendarOptions.events = this.tickets.map(ticket => ({
        title: ticket.title,
        start: new Date(ticket.creationdate), // Assuming 'creationdate' is a valid date string
        description: ticket.description
      }));
    },
    (error) => {
      console.error('Erreur lors de la récupération des tickets:', error);
    }
  );
}

}
