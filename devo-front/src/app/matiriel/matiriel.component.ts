import { Component, OnInit } from '@angular/core';
import { MatirielService } from '../services/services/matiriel.service';
import { Matiriel } from '../services/models';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from "../shared/components/sidebar/sidebar.component";
import { ChangeDetectorRef } from '@angular/core';
@Component({
  selector: 'app-matiriel',
  imports: [CommonModule, SidebarComponent],
  templateUrl: './matiriel.component.html',
  styleUrl: './matiriel.component.css'
})
export class MatirielComponent implements OnInit {
  notificationsSubscription :any;
  unreadNotificationsSubscription =0;
  notificationss :Array<Notification> =[];
  matiriel : Matiriel [] =[];
  matirielData: string = '';
  selectedItems: Matiriel[] = [];
  showPopup: boolean = false;

constructor( private service  :MatirielService,private cdr: ChangeDetectorRef){
}
  ngOnInit(): void {
this.getMatiriel();  }



getMatiriel() {
  this.service.getMatiriel().subscribe(      
    (response ) => {
    this.matiriel = response ;
    console.log('materiel  recived:', response );
  },
  (error) => {
    console.error('Erreur lors de la récupération des materiel:', error);
  }
);
}
onCheckboxChange(item: Matiriel) {
    const index = this.selectedItems.indexOf(item);
    if (index === -1) {
      if (this.selectedItems.length < 3) {
        this.selectedItems.push(item);
      }
    } else {
      this.selectedItems.splice(index, 1);
    }
  }

  openPopup() {
    if (this.selectedItems.length === 1 || this.selectedItems.length === 3) {
      this.showPopup = true;
    }
  }

  closePopup() {
    this.showPopup = false;
  }

  sendToEmployee() {
    const ids: number[] = this.selectedItems
      .map(item => item.matirielid)
      .filter((id): id is number => id !== undefined); // Ensure id is a number and not undefined
  
    if (ids.length === 0) {
      console.error('No valid material IDs selected');
      return;
    }
  
    this.service.decreaseStock(ids).subscribe(
      (response) => {
        console.log('Stock decreased:', response);
        this.getMatiriel(); // Refresh the list
        this.selectedItems = []; // Clear selections
        this.closePopup();
      },
      (error) => {
        console.error('Error decreasing stock:', error);
      }
    );
  }
}
