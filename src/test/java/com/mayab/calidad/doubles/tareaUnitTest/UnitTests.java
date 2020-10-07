package com.mayab.calidad.doubles.tareaUnitTest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import java.util.HashMap; 

@RunWith(Parameterized.class)
public class UnitTests {

	private static Alert alertas;
	private static DbManager dbs;
	
	@Before
	public void setupMocks() {
		alertas = mock(Alert.class);
		dbs = mock(DbManager.class);
	}
	@Parameters
	public static Iterable data() {
		return Arrays.asList(new Object[][] {
			{"Ernesto",5000,1,alertas,dbs},{"Leonel",6000,2,alertas,dbs},{"Patricia",7000,3,alertas,dbs}
		});
	}
	
	private Cuenta cuenta;
	int balance;
    String holder;
    int zone;
    int index = 1;
    HashMap<Integer,Integer> dbFake = new HashMap<Integer,Integer>();
	
	public UnitTests(String holder, int initialBalance, int zone, Alert alertas, DbManager dbs) {
		 cuenta = new Cuenta(holder, initialBalance, zone, alertas, dbs);
		 this.holder = holder;
	     this.balance = initialBalance;
	     this.zone = zone;
	}

	@Test
	public void comprobacionCuentas() {
		assertThat(holder, is(cuenta.getHolder()));
		assertThat(balance, is(cuenta.getBalance()));
		assertThat(zone, is(cuenta.getZone()));
	}
	
	@Test
	public void comprobacionMensaje() {
		when(alertas.sendAlert(anyString())).thenAnswer(new Answer<String> () {
			public String answer(InvocationOnMock invocation) throws Throwable{
				String msg = (String) invocation.getArguments()[0];
				return msg;
			}
		});
		cuenta = new Cuenta(holder, balance, zone, alertas,dbs);
		cuenta.debit(cuenta.getBalance()-99);
		verify(alertas).sendAlert(holder + ", your account balance is below 100");
	}
	
	@Test
	public void pruebasAnswer() {
		when(dbs.guardarTransaccion(anyInt())).thenAnswer(new Answer<Integer> () {
			public Integer answer(InvocationOnMock invocation) throws Throwable{
				int cantidadDeTransaccion = (int) invocation.getArguments()[0];
				dbFake.put(index, cantidadDeTransaccion);
				index++;
				return cantidadDeTransaccion;
			}
		});
		when(dbs.calcularComisiones()).thenAnswer(new Answer<Double> () {
			public Double answer(InvocationOnMock invocation) throws Throwable{
				double totalComisiones = 0;
				for(int i : dbFake.values()) {
					totalComisiones += cuenta.getZone()*i*.01;
				}
				return totalComisiones;
			}
		});
		cuenta = new Cuenta(holder, balance, zone, alertas,dbs);
		cuenta.debit(5000);
		cuenta.debit(500);
		cuenta.credit(700);
		double expected = cuenta.getZone()*(5000+500+700)*.01;
		assertThat(expected, is(cuenta.comisionesTotales()));
	}

}
