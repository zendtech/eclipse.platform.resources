/*******************************************************************************
 *  Copyright (c) 2000, 2012 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.resources;

import java.util.Random;
import junit.framework.Assert;
import org.eclipse.core.tests.harness.FussyProgressMonitor;

/**
 * Abstract superclass of inner classes used for black-box testing
 * For debugging certain failure cases, insert a breakpoint in performTestRecursiveLoop
 * according to the comment in that method.
 */
abstract public class TestPerformer {
	private int count = 0;
	String name = null;

	/**
	 * TestPerformer constructor comment.
	 */
	public TestPerformer(String name) {
		super();
		this.name = name;
	}

	public void cleanUp(Object[] args, int count) {
		// do nothing
	}

	public Object[] interestingOldState(Object[] args) throws Exception {
		//subclasses should override to hold onto interesting old state
		return null;
	}

	abstract public Object invokeMethod(Object[] args, int count) throws Exception;

	final public void performTest(Object[][] inputs) {
		// call helper method
		int permutations = 1;
		for (int i = 0; i < inputs.length; i++) {
			permutations = permutations * inputs[i].length;
			if (inputs[i].length > 2) {
				scramble(inputs[i]);
			}
		}
		//	System.out.println("\nTesting " + permutations + " permutations of " + name);
		performTestRecursiveLoop(inputs, new Object[inputs.length], 0);
	}

	/**
	 * Loop through imaginary (nth) index variable, setting args[nth] to inputs[nth][i].
	 * Then invoke method if nth==inputs.length-1, otherwise do recursive call
	 * with incremented nth.
	 */
	private void performTestRecursiveLoop(Object[][] inputs, Object[] args, int nth) {
		for (int i = 0; i < inputs[nth].length; i++) {
			args[nth] = inputs[nth][i];
			if (nth == inputs.length - 1) {
				// breakpoint goes here, may be conditional on name and count, e.g.:
				// name.equals("IResourceTest.testMove") && count==2886
				if (shouldFail(args, count)) {
					try {
						invokeMethod(args, count);
						StringBuffer buffer = new StringBuffer();
						buffer.append("invocation " + count + " should fail, but it doesn't [");
						for (int j = 0; j < args.length; j++) {
							buffer.append(args[j]);
							buffer.append(',');
						}
						buffer.deleteCharAt(buffer.length() - 1);
						buffer.append(']');
						Assert.assertTrue(buffer.toString(), false);
					} catch (Exception ex) {
					}
				} else {
					Object[] oldState = null;
					try {
						oldState = interestingOldState(args);
					} catch (Exception ex) {
						ex.printStackTrace();
						throw new RuntimeException("call to interestingOldState failed");
					}
					Object result = null;
					try {
						result = invokeMethod(args, count);
					} catch (FussyProgressMonitor.FussyProgressAssertionFailed fussyEx) {
						Assert.assertTrue("invocation " + count + ": " + fussyEx.getMessage(), false);
					} catch (Exception ex) {
						ex.printStackTrace();
						Assert.assertTrue("invocation " + count + " failed with " + ex, false);
					}
					boolean success = false;
					try {
						success = wasSuccess(args, result, oldState);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					Assert.assertTrue("invocation " + count + " did not produce desired result", success);
				}
				cleanUp(args, count);
				count++;
			} else {
				performTestRecursiveLoop(inputs, args, nth + 1);
			}
		}
	}

	/**
	 * scrambles an array in a deterministic manner (note the constant seed...).
	 */
	static protected void scramble(Object[] first) {
		Random random = new Random(4711);

		final int len = first.length;
		for (int i = 0; i < len * 100; i++) {
			/* get any array offset */
			int off1 = (int) (random.nextFloat() * len);
			if (off1 == len)
				continue;

			/* get another array offset */
			int off2 = (int) (random.nextFloat() * len);
			if (off2 == len)
				continue;

			/* switch */
			Object temp = first[off1];
			first[off1] = first[off2];
			first[off2] = temp;
		}
	}

	abstract public boolean shouldFail(Object[] args, int count);

	abstract public boolean wasSuccess(Object[] args, Object result, Object[] oldState) throws Exception;
}
