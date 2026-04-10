import java.util.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.io.*;

//exceptiile
class NotEnoughFunds extends Exception{public NotEnoughFunds(String message){super(message);}}
class NumberException extends Exception{public NumberException(String message){super(message);}}
class InactiveAccountException extends Exception {public InactiveAccountException(String message){super(message);}}

//clasele
class Card
{
    //membrii
    private String owner;
    private String cvv,cardNumber,expireDate;

    //constructor
    public Card(String name, String number, String Cvv, String date)
    {
	owner=name;
	cvv=Cvv;
	cardNumber=number;
	expireDate=date;
    }

    //metode
    public String getOwnerName(){return owner;}
    public String getCVV(){return cvv;}
    public String getCardNumber(){return cardNumber;}
    public String getExpireDate(){return expireDate;}
}


abstract class Account
{
    //membrii
    protected String IBAN;
    protected boolean status;
    protected double balance;
    protected ArrayList <Card> cards;
    protected ArrayList <Tranzaction> tranzactionHistory;

    //constructor
    public Account(String Iban,boolean Status,double Sold){
	IBAN=Iban;
	status=Status;
	balance=Sold;
	cards=new ArrayList <Card>();
	tranzactionHistory=new ArrayList <Tranzaction>();
    }

    //metode
    abstract public void withdraw(double amount) throws NotEnoughFunds,NumberException,InactiveAccountException;
   
    public void deposit(double amount) throws NumberException,InactiveAccountException
    {
	if(!status) throw new InactiveAccountException("Cont inactiv\n");
	
	if(amount < 0) throw new NumberException("Suma depozitata trebuie sa fie pozitiva\n");
	this.balance=this.balance+amount;
	String generatedId = UUID.randomUUID().toString();
        this.tranzactionHistory.add(new Tranzaction(generatedId, amount, TranzactionType.DEPOSIT));
    }

    public void addCard(Card c)
    {
	cards.add(c);
    }
    
    public void displayTranzactionHistory()
    {
	for(Tranzaction t : tranzactionHistory)
	    System.out.println(t.toString());
    }

    public String getIban(){return IBAN;}
    public boolean getStatus(){return status;}
    public double getBalance(){return balance;}
    public ArrayList <Card> getCards(){return cards;}


    public void transfer(Account destinationAccount, double amount) throws NotEnoughFunds, NumberException, InactiveAccountException 
    {
        if (destinationAccount == null) {
            throw new NumberException("Transfer eșuat: Contul destinație nu există.\n");
        }
        if (this.IBAN.equals(destinationAccount.getIban())) {
            throw new NumberException("Transfer eșuat: Nu poți transfera bani în același cont.\n");
        }
        if (!destinationAccount.getStatus()) {
            throw new InactiveAccountException("Transfer respins: Contul destinație este blocat sau închis.\n");
        }

        this.withdraw(amount);
        destinationAccount.deposit(amount);
    }
    
}

class CheckingAccount extends Account
{
    private double overdraftLimit;

    
    public CheckingAccount(String Iban,boolean Status,double Sold,double limit) throws NumberException
    {
	super(Iban,Status,Sold);
	if(limit < 0) throw new NumberException("Limita de descoperire trebuie sa fie pozitiva\n");
	overdraftLimit=limit;
    }

    public void withdraw(double amount) throws NotEnoughFunds,NumberException,InactiveAccountException
    {
	if(!status) throw new InactiveAccountException("Cont inactiv\n");
	
	if(amount < 0) throw new NumberException("Suma depozitata trebuie sa fie pozitiva\n");
	double currentBalance = balance - amount;
	if((currentBalance + overdraftLimit) < 0) throw new NotEnoughFunds("S-a depasit limita de descoperire\n");
	this.balance=currentBalance;
	String generatedId = UUID.randomUUID().toString();
        this.tranzactionHistory.add(new Tranzaction(generatedId, amount, TranzactionType.WITHDRAW));
    }

}

class SavingsAccount extends Account
{
    private double interestRate;


     public SavingsAccount(String Iban,boolean Status,double Sold,double percent) throws NumberException
    {
	super(Iban,Status,Sold);
	if(percent < 0 || percent > 100) throw new NumberException("Dobanda trebuie sa fie intre 0 si 100\n");
	interestRate=percent;
    }

    public void withdraw(double amount) throws NotEnoughFunds,NumberException,InactiveAccountException
    {
	if(!status) throw new InactiveAccountException("Cont inactiv\n");
	
	if(amount < 0) throw new NumberException("Suma retrasa trebuie sa fie pozitiva\n");
	
	double currentBalance = balance - amount;
	if(currentBalance < 0) throw new NotEnoughFunds("Not enough funds\n");
	
	this.balance=currentBalance;
	String generatedId = UUID.randomUUID().toString();
        this.tranzactionHistory.add(new Tranzaction(generatedId, amount, TranzactionType.WITHDRAW));
    }

    public void applyInterest()
    {
	this.balance+=interestRate/100.0*balance;
    }
}


class Customer {
    // Membrii
    private String customerId; 
    private String firstName;
    private String lastName;
    private ArrayList<Account> accounts; 

    // Constructor
    public Customer(String customerId, String firstName, String lastName) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accounts = new ArrayList<>(); 
    }

    // Metode
    public void openAccount(Account account) {
        this.accounts.add(account);
    }

    public String getCustomerDetails() {
        return firstName + " " + lastName + " (ID: " + customerId + ")";
    }


    public double getTotalBalance() {
        double sum=0.0;
	for(Account acc : accounts)
	    sum+=acc.getBalance();
        return sum;
    }
}

enum TranzactionType
{
    DEPOSIT,
    WITHDRAW
}

class Tranzaction
{
    private TranzactionType type;
    private double amount;
    private String id;
    private LocalDateTime time;

    public Tranzaction(String id,double amount,TranzactionType type)
    {
	this.type=type;
	this.amount=amount;
	this.id=id;
	this.time=LocalDateTime.now();
    }

    public String getType(){
	if(type == TranzactionType.DEPOSIT)
	    return "DEPOSIT";
        return "WITHDRAW";
    }
    public double getAmount(){return amount;}
    public String getId(){return id;}
    public LocalDateTime getTime(){return time;}

    public String toString()
    {
	return "" + type + ": " + amount + ", date : " + time;
    }
}


class BankSystem
{
    public static void main(String[] args) throws IOException
    { 
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        Customer customer = new Customer("123456789", "Razvan", "Singiorzan");
        CheckingAccount contCurent = null;
        SavingsAccount contEconomii = null; 
        
        try {
            contCurent = new CheckingAccount("RO12BTRL...", true, 0.0, 500.0);
            contEconomii = new SavingsAccount("RO99INGB...", true, 0.0, 5.0); 
            
            customer.openAccount(contCurent);
            customer.openAccount(contEconomii);
        } catch (NumberException e) {
            System.err.println("Eroare la inițializarea sistemului: " + e.getMessage());
            return; 
        }

        System.out.println("=== Sistem Bancar Inițializat pentru: " + customer.getCustomerDetails() + " ===");

        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n--- MENIU PRINCIPAL ---");
            System.out.println("Cont Curent: " + contCurent.getBalance() + " RON | Cont Economii: " + contEconomii.getBalance() + " RON");
            System.out.println("1. Depunere numerar (Cont Curent)");
            System.out.println("2. Retragere numerar (Cont Curent)");
            System.out.println("3. Afișare istoric tranzacții (Cont Curent)");
            System.out.println("4. Transferă bani către Contul de Economii");
            System.out.println("5. Ieșire");
            System.out.print("Alege o opțiune: ");
            
            int option = 0;
            try {
                option = Integer.parseInt(reader.readLine()); 
            } catch (NumberFormatException e) {
                System.out.println("[ATENȚIE] Te rog să introduci o cifră validă!");
                continue; 
            }
            
            switch (option) {
                case 1:
                    System.out.print("Introdu suma de depus: ");
                    try {
                        double sumaDepunere = Double.parseDouble(reader.readLine());
                        contCurent.deposit(sumaDepunere);
                        System.out.println("[SUCCES] Depunere realizată.");
                    } catch (NumberFormatException e) {
                        System.err.println("[EROARE INPUT] Suma introdusă nu este un număr valid.");
                    } catch (Exception e) { // Prindem orice altă excepție custom
                        System.err.print("[EROARE] " + e.getMessage()); 
                    }
                    break;
                    
                case 2:
                    System.out.print("Introdu suma de retras: ");
                    try {
                        double sumaRetragere = Double.parseDouble(reader.readLine());
                        contCurent.withdraw(sumaRetragere);
                        System.out.println("[SUCCES] Retragere realizată.");
                    } catch (NumberFormatException e) {
                        System.err.println("[EROARE INPUT] Suma introdusă nu este un număr valid.");
                    } catch (Exception e) {
                        System.err.print("[TRANZACȚIE RESPINSĂ] " + e.getMessage());
                    } 
                    break;
                    
                case 3:
                    System.out.println("\n--- ISTORIC TRANZACȚII CONT CURENT ---");
                    contCurent.displayTranzactionHistory();
                    break;
                    
                case 4:
                    System.out.print("Introdu suma de transferat: ");
                    try {
                        double sumaTransfer = Double.parseDouble(reader.readLine());
                        contCurent.transfer(contEconomii, sumaTransfer);
                        System.out.println("[SUCCES] Ai transferat " + sumaTransfer + " RON în contul de economii.");
                    } catch (NumberFormatException e) {
                        System.err.println("[EROARE INPUT] Format numeric invalid.");
                    } catch (Exception e) {
                        System.err.print("[TRANSFER EȘUAT] " + e.getMessage());
                    }
                    break;

                case 5:
                    System.out.println("Se închide aplicația...");
                    isRunning = false;
                    break;
                    
                default:
                    System.out.println("Opțiune invalidă. Te rog să alegi un număr de la 1 la 5.");
            }
        }
    }
}
