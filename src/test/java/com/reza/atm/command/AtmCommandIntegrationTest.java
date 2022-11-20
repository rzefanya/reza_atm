package com.reza.atm.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.Input;
import org.springframework.shell.Shell;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
public class AtmCommandIntegrationTest {
	@Autowired
	private Shell shell;
	
	@Test
	public void integrationTest() {
		Object result = null;

		result = shell.evaluate(write("login Alice"));
		System.out.println(result + "\n");
		assertEquals("Hello, Alice!\nYour balance is $0", result);

		result = shell.evaluate(write("deposit 100"));
		System.out.println(result + "\n");
		assertEquals("Your balance is $100", result);
		
		result = shell.evaluate(write("logout"));
		System.out.println(result + "\n");
		assertEquals("Goodbye, Alice!", result);
		
		result = shell.evaluate(write("login Bob"));
		System.out.println(result + "\n");
		assertEquals("Hello, Bob!\nYour balance is $0", result);
		
		result = shell.evaluate(write("deposit 80"));
		System.out.println(result + "\n");
		assertEquals("Your balance is $80", result);
		
		result = shell.evaluate(write("transfer Alice 50"));
		System.out.println(result + "\n");
		assertEquals("Transferred $50 to Alice\nYour balance is $30", result);
		
		result = shell.evaluate(write("transfer Alice 100"));
		System.out.println(result + "\n");
		assertEquals("Transferred $30 to Alice\nYour balance is $0\nOwed $70 to Alice", result);
	
		result = shell.evaluate(write("deposit 30"));
		System.out.println(result + "\n");
		assertEquals("Transferred $30 to Alice\nYour balance is $0\nOwed $40 to Alice", result);
	
		result = shell.evaluate(write("logout"));
		System.out.println(result + "\n");
		assertEquals("Goodbye, Bob!", result);
		
		result = shell.evaluate(write("login Alice"));
		System.out.println(result + "\n");
		assertEquals("Hello, Alice!\nYour balance is $210\nOwed $40 from Bob", result);
		
		result = shell.evaluate(write("transfer Bob 30"));
		System.out.println(result + "\n");
		assertEquals("Your balance is $210\nOwed $10 from Bob", result);
		
		result = shell.evaluate(write("logout"));
		System.out.println(result + "\n");
		assertEquals("Goodbye, Alice!", result);
		
		result = shell.evaluate(write("login Bob"));
		System.out.println(result + "\n");
		assertEquals("Hello, Bob!\nYour balance is $0\nOwed $10 to Alice", result);

		result = shell.evaluate(write("deposit 100"));
		System.out.println(result + "\n");
		assertEquals("Transferred $10 to Alice\nYour balance is $90", result);

		result = shell.evaluate(write("logout"));
		System.out.println(result + "\n");
		assertEquals("Goodbye, Bob!", result);
	}

	private Input write(String input) {
		System.out.println("shell:>" + input);
		return new TestInput(input);
	}

	class TestInput implements Input {
		private String input;

		public TestInput(String input) {
			this.input = input;
		}

		@Override
		public String rawText() {
			return input;
		}
	}
}
