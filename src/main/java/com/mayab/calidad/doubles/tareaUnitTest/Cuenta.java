package com.mayab.calidad.doubles.tareaUnitTest;
public class Cuenta {
    
    int balance;
    String holder;
    int zone;
    Alert alerts;
    DbManager db;

    public Cuenta(String holder, int initialBalance, int zone, Alert alerts, DbManager db){
        this.holder = holder;
        this.balance = initialBalance;
        this.zone = zone;
        this.alerts = alerts;
        this.db = db;
    }
    
    public int getBalance() {
        return this.balance;
    }
    
    public String getHolder(){
        return this.holder;
    }

    public int getZone() {
		return zone;
	}

	void debit(int balance) {
        this.balance -= balance;
        db.guardarTransaccion(balance);
        if(this.balance < 100){
            this.alerts.sendAlert(this.holder + ", your account balance is below 100");
        }
    }
	
	public double comisionesTotales() {
		return db.calcularComisiones();
	}

    void credit(int balance) {
        this.balance += balance;
        db.guardarTransaccion(balance);
    }
    
    void setAlertListener(Alert listener){
        this.alerts = listener;
    }
    
}