export interface Transaction {
    id?: string;
    clientProductId: string;
    clientId: string;  
    transactionType: string;
    amount: string;
    transactionDate: string;
}
 
export interface TransactionResponse {
   transactions: Transaction[];
}