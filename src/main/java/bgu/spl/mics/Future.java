package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */

 /* ====================================
 To Do:
 check if get() needs to be synchronized
 check if resolve() needs to be synched and if needs to check for null - exception?
	**probably doesnt need to throw exception - see page 4
 implement timeService for the second get(time,unit) function
 ====================================*/
public class Future<T> {
	
	private T result;
	private boolean isDone;
	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		result = null;
		isDone = false;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	public synchronized T get() {
		while (!isDone) {
			try{
				wait();
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
				return null;
			}
		}
		return result;
	}
	
	/**
     * Resolves the result of this Future object.
     */
	public synchronized void resolve (T result) {
		this.result = result;
		isDone = true;
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public synchronized boolean isDone() {
		return isDone;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timeout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public synchronized T get(long timeout, TimeUnit unit) {
		long timeOut = unit.toNanos(timeout); // Convert timeout to nanoseconds
		long endTime = System.nanoTime() + timeOut; // Calculate the time when the timeout will occur
		while (!isDone()) {
			try {
				long remainingTime = endTime - System.nanoTime(); // Calculate remaining time
				if (remainingTime <= 0) {
					// If the timeout has expired, break out of the loop and return null
					return null;
				}
				this.wait(remainingTime / 1000000, (int) (remainingTime % 1000000)); // Wait for the remaining time in milliseconds and nanoseconds
				
			} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Restore the interrupt status
			return null; // Return null if interrupted
			}
		}
		return result; // Return the result if it's available
	}
}
