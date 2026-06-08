package edu.eci.arsw.samples;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Main {
	public static void main(String[] args) {
		int numHilos = 20;

		CyclicBarrier barrier = new CyclicBarrier(numHilos + 1);

		HiloProc[] hilos = new HiloProc[numHilos];

		for(int i = 0; i < numHilos; ++i) {
			hilos[i] = new HiloProc(i, barrier);  // CAMBIO 2: pasar la barrera
		}

		for(int i = 0; i < numHilos; ++i) {
			hilos[i].start();
		}

		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}

		// Solo llega aquí cuando TODOS terminaron
		long tiempoPromedio = 0L;

		for(int i = 0; i < numHilos; ++i) {
			tiempoPromedio += hilos[i].getResultado();
		}

		System.out.println("tiempo promedio de ejecucion fue de: "
				+ tiempoPromedio / (long)numHilos + " ms");
	}
}