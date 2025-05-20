export interface Payment {
    paymentid?: number;
    paymentdate?:string;
    paymentamount?: number;
    description?: string;
    verified?:Boolean;
}