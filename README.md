# Barrier Synchronization Pattern — ARSW Lab

**Escuela Colombiana de Ingeniería Julio Garavito**
Arquitecturas de Software (ARSW)

---

## Description

This project demonstrates the **Barrier Synchronization Pattern** in Java. It creates 20 threads that each perform the same task at different speeds. The goal is to calculate the average execution time of all threads **only after the last thread has finished**.

Without synchronization, the main thread calculates the average before any thread finishes, always returning `0 ms`. With a `CyclicBarrier`, the main thread sleeps at the barrier and only wakes up when every worker thread has arrived — giving a correct result.

---

## Project Structure

```
BarrierSyncProblem/
└── src/
    └── edu/
        └── eci/
            └── arsw/
                └── samples/
                    ├── HiloProc.java   ← Worker thread
                    └── Main.java       ← Main program (entry point)
```

---

## How It Works

### The Problem (without synchronization)

```
main: hilos[0].start()  → thread launches and returns immediately
main: hilos[1].start()  → thread launches and returns immediately
...
main: getResultado()    → resultado is still 0! threads haven't finished yet
main: prints average = 0 ms  
```

The main thread calls `getResultado()` right after `start()`. Since `start()` returns immediately without waiting, all `resultado` values are still `0`.

### The Solution (CyclicBarrier)

```
main creates CyclicBarrier(21)   ← 20 threads + 1 main

hilos[0].start() ... hilos[19].start()   ← all threads launch

main: barrier.await()   ← main SLEEPS here

  Thread 7  finishes → barrier.await() → waiting: 19 left
  Thread 8  finishes → barrier.await() → waiting: 18 left
  ...
  Thread 17 finishes → barrier.await() → counter reaches 0!

← ALL 21 participants reached the barrier → everyone is released

main wakes up → getResultado() → all values are real → correct average 
```

---

## Key Classes

### `HiloProc.java`

Each thread receives a `CyclicBarrier` reference, generates a random `waitPeriod` (0–5000 ms), performs 10 iterations sleeping that many ms each time, records its total execution time, and **calls `barrier.await()` when finished**.

```java
package edu.eci.arsw.samples;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class HiloProc extends Thread {
    int waitPeriod = 0;
    int idHilo = 0;
    long resultado = 0L;
    CyclicBarrier barrier;          // new attribute

    public HiloProc(int id, CyclicBarrier barrier) {
       try {
          Thread.sleep(10L);
       } catch (InterruptedException e) {
          e.printStackTrace();
       }

       this.waitPeriod = Math.abs((new Random(System.currentTimeMillis())).nextInt() % 5000);
       this.idHilo = id;
       this.barrier = barrier;     // save the barrier
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
```

### `Main.java`

Creates a `CyclicBarrier` with `numHilos + 1` participants, starts all threads, calls `barrier.await()` to sleep, and only then calculates the average.

```java
package edu.eci.arsw.samples;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Main {
    public static void main(String[] args) {
        int numHilos = 20;

        // 20 threads + 1 main = 21 participants
        CyclicBarrier barrier = new CyclicBarrier(numHilos + 1);

        HiloProc[] hilos = new HiloProc[numHilos];

        for(int i = 0; i < numHilos; ++i) {
            hilos[i] = new HiloProc(i, barrier);
        }

        for(int i = 0; i < numHilos; ++i) {
            hilos[i].start();
        }

        // main sleeps here until all 20 threads arrive
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        // only reaches here when ALL threads finished
        long tiempoPromedio = 0L;
        for(int i = 0; i < numHilos; ++i) {
            tiempoPromedio += hilos[i].getResultado();
        }

        System.out.println("El tiempo promedio de la ejecución fue de: "
                + tiempoPromedio / (long)numHilos + " ms");
    }
}
```

---

## Comparison: Before vs After

| | Without synchronization | With CyclicBarrier |
|---|---|---|
| Main thread behavior | Continues immediately after `start()` | Sleeps at `barrier.await()` |
| When average is calculated | Before threads finish | After last thread finishes |
| Result | `0 ms`  | Correct value  |

---

## Why `CyclicBarrier` and not other solutions?

| Solution | How it works | Reusable? | Best for |
|---|---|---|---|
| `Thread.join()` | Main waits for each thread one by one | ✅ | Simple sequential waiting |
| `wait()/notifyAll()` | Manual monitor with counter | ✅ | Low-level understanding |
| `CountDownLatch` | Countdown from N to 0 | ❌ | One-shot waiting |
| **`CyclicBarrier`** | **All participants wait together** | **✅** | **Multi-thread rendezvous** |

`CyclicBarrier` is the best fit because its name matches the pattern exactly, all participants meet at the same point, and it resets automatically after release.

---

## Sample Output

<img width="520" height="332" alt="image" src="https://github.com/user-attachments/assets/11612414-6776-4a74-bb60-3a549e5cfdc6" />

---

## How to Run

1. Open the project in IntelliJ IDEA
2. Navigate to `src/edu/eci/arsw/samples/Main.java`
3. Right-click `Main.java` → **Run 'Main.main()'**
4. Observe the threads running in the console
5. Wait for all 20 threads to reach 100% — the average prints last

---

## Dependencies

- Java 8 or higher (uses `java.util.concurrent.CyclicBarrier`)
- No external libraries required

---

## Author

Hildebrando — Escuela Colombiana de Ingeniería, ARSW 2026
