package edu.eci.arsw.samples;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class HiloProc extends Thread {
	int waitPeriod = 0;
	int idHilo = 0;
	long resultado = 0L;
	CyclicBarrier barrier;          //nuevo atributo

	public HiloProc(int id, CyclicBarrier barrier) {   // CAMBIO 2: nuevo parámetro
		try {
			Thread.sleep(10L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.waitPeriod = Math.abs((new Random(System.currentTimeMillis())).nextInt() % 5000);
		this.idHilo = id;
		this.barrier = barrier;     //guardar la barrera
	}

	public void run() {
		int numit = 10;
		long startTime = System.currentTimeMillis();

		for(int i = 0; i < numit; ++i) {
			System.out.println("hilo " + this.idHilo + " y va en el "
					+ (float)(i + 1) / (float)numit * 100.0F + "% de la tarea. P:" + this.waitPeriod);

			try {
				Thread.sleep((long)this.waitPeriod);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		this.resultado = System.currentTimeMillis() - startTime;

		// espera en la barrera
		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	public long getResultado() {
		return this.resultado;
	}
}