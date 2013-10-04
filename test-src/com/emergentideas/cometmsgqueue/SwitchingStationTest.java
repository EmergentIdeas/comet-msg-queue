package com.emergentideas.cometmsgqueue;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

public class SwitchingStationTest {

	protected int idNum = 1;
	
	@Test
	public void testSwitchStation() throws Exception {
		
		SwitchingStation switchingStation = new SwitchingStation();
		
		ReaderEndpoint one = new ReaderEndpoint(switchingStation, new MessageEndpoint(id(), "c1"), "one");
		one.start();
		
		ReaderEndpoint two = new ReaderEndpoint(switchingStation, new MessageEndpoint(id(), "c2"), "two");
		two.start();
		
		ReaderEndpoint three = new ReaderEndpoint(switchingStation, new MessageEndpoint(id(), "c1"), "three");
		three.start();
		
		ReaderEndpoint four = new ReaderEndpoint(switchingStation, new MessageEndpoint(id(), "c3"), "four");
		four.start();
		
		ReaderEndpoint five = new ReaderEndpoint(switchingStation, new MessageEndpoint(id(), "c3", "c2"), "five");
		five.start();

		
		System.out.println("Threads are started but main thread is still running. Going to wait now.");
		
		Thread.sleep(1000);
		
		switchingStation.sendMessage("c1", "q1", "message 1");
		System.out.println("First message is sent");
		
		Thread.sleep(1000);
		
		assertTrue(one.isPrintedMessage());
		assertTrue(three.isPrintedMessage());
		assertFalse(two.isPrintedMessage());
		assertFalse(four.isPrintedMessage());
		assertFalse(five.isPrintedMessage());
		
		switchingStation.sendMessage("c2", "q2", "message 2");
		System.out.println("Second message is sent");
		
		Thread.sleep(1000);
		
		assertTrue(two.isPrintedMessage());
		assertTrue(five.isPrintedMessage());
		assertFalse(four.isPrintedMessage());
	}
	
	@Test
	public void testMultipleMessagesBehavior() throws Exception {
		String clientId = "id1";
		
		SwitchingStation switchingStation = new SwitchingStation();
		switchingStation.setMaxWaitingTime(120000);
		
		ReaderEndpoint one = new ReaderEndpoint(switchingStation, new MessageEndpoint(clientId, "c1"), "one");
		one.start();
		
		Thread.sleep(1000);
		
		switchingStation.sendMessage("c1", "q1", "message 1");
		switchingStation.sendMessage("c1", "q1", "message 2");
		
		Thread.sleep(1000);
		
		assertTrue(one.isPrintedMessage());
		
		one = new ReaderEndpoint(switchingStation, new MessageEndpoint(clientId, "c1"), "one");
		one.start();
		
		Thread.sleep(1000);
		
		assertTrue(one.isPrintedMessage());

	}
	
	protected String id() {
		return "id" + idNum++;
	}
}
