/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.core.resources;

import org.eclipse.core.internal.resources.InternalWorkspaceJob;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * A job that makes an atomic modification to the workspace.  Clients must
 * implement the abstract method <code>runInWorkspace</code> instead
 * of the usual <code>Job.run</code> method.
 * <p>
 * After running a method that modifies resources in the workspace,
 * registered listeners receive after-the-fact notification of
 * what just transpired, in the form of a resource change event.
 * This method allows clients to call a number of
 * methods that modify resources and only have resource
 * change event notifications reported at the end of the entire
 * batch.
 * </p>
 * <p>
 * A WorkspaceJob is the asynchronous equivalent of IWorkspaceRunnable
 * </p>
 * <p>
 * Note that the workspace is not locked against other threads during the execution 
 * of a workspace job. Other threads can be modifying the workspace concurrently 
 * with a workspace job.  To obtain exclusive access to a portion of the workspace, 
 * set the scheduling rule on the job to be a resource scheduling rule.  The factory 
 * method <code>newSchedulingRule</code> is used to create a scheduling rule 
 * based on a particular resource.
 * </p>
 * @see IWorkspaceRunnable
 */
public abstract class WorkspaceJob extends InternalWorkspaceJob {
	/**
	 * Returns a new scheduling rule on a resource.  Two resource scheduling rules
	 * will be conflicting if and only if the resource of one rule is a child of, or equal to,
	 *  the resource of the other rule.
	 * 
	 * @return a resource scheduling rule
	 */
	public static final ISchedulingRule newSchedulingRule(IResource resource) {
		return InternalWorkspaceJob.newSchedulingRule(resource);
	}
	/**
	 * Creates a new workspace job.
	 * @param name the name of the job
	 */
	public WorkspaceJob(String name) {
		super(name);
	}
	/**
	 * Runs the operation, reporting progress to and accepting
	 * cancelation requests from the given progress monitor.
	 * <p>
	 * Implementors of this method should check the progress monitor
	 * for cancelation when it is safe and appropriate to do so.  The cancelation
	 * request should be propagated to the caller by throwing 
	 * <code>OperationCanceledException</code>.
	 * </p>
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancelation are not desired
	 * @exception CoreException if this operation fails.
	 */
	public abstract IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException;
}