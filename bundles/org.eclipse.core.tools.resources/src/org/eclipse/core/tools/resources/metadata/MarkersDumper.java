/*******************************************************************************
 * Copyright (c) 2002, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tools.resources.metadata;

import java.io.DataInputStream;
import org.eclipse.core.tools.metadata.*;

/**
 * A dumper for .markers files.
 * 
 * @see org.eclipse.core.tools.resources.metadata.AbstractDumper
 * @see org.eclipse.core.tools.resources.metadata.MarkersDumpingStrategy_1 
 * @see org.eclipse.core.tools.resources.metadata.MarkersDumpingStrategy_2
 * @see org.eclipse.core.tools.resources.metadata.MarkersDumpingStrategy_3  
 */
public class MarkersDumper extends MultiStrategyDumper {

	// type constants
	static final byte INDEX = 1;
	static final byte QNAME = 2;

	// marker attribute types
	static final byte ATTRIBUTE_NULL = 0;
	static final byte ATTRIBUTE_BOOLEAN = 1;
	static final byte ATTRIBUTE_INTEGER = 2;
	static final byte ATTRIBUTE_STRING = 3;

	/**
	 * @see org.eclipse.core.tools.resources.metadata.AbstractDumper#getStringDumpingStrategy(java.io.DataInputStream)
	 */
	protected IStringDumpingStrategy getStringDumpingStrategy(DataInputStream dataInput) throws Exception {
		int versionId = dataInput.readInt();
		IStringDumpingStrategy strategy;
		switch (versionId) {
			case 1 :
				strategy = new MarkersDumpingStrategy_1();
				break;
			case 2 :
				strategy = new MarkersDumpingStrategy_2();
				break;
			case 3 :
				strategy = new MarkersDumpingStrategy_3();
				break;
			default :
				throw new DumpException("Unknown markers file version: " + versionId); //$NON-NLS-1$
		}
		return strategy;
	}
}
