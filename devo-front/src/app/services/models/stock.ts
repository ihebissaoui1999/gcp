import { Matiriel } from "./matiriel";

export interface Stock {
    stockid: number;
    stockdescription: string;
    stocknumbMatiriel: number;
    stockename: 'Souris' | 'Clavier' | 'Ecran' | 'Disquedur' | 'Imprimante' | 'Microphone' | 'Webcam' | 'Casque' | 'USB' | 'Bureau' | 'Chaise';
    image: string;
    stockMatiriel: 'ITEquipment' | 'CommunicationEquipment' | 'Accessories';
    stockType: 'ENSTOCK' | 'ENPANNE' | 'CLEAR';
    matiriels: Matiriel[];
  }