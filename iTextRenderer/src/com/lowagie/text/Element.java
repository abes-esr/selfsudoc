/*
 * $Id: Element.java 3672 2009-02-01 15:32:09Z blowagie $
 *
 * Copyright 1999, 2000, 2001, 2002 by Bruno Lowagie.
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * LGPL license (the "GNU LIBRARY GENERAL PUBLIC LICENSE"), in which case the
 * provisions of LGPL are applicable instead of those above.  If you wish to
 * allow use of your version of this file only under the terms of the LGPL
 * License and not to allow others to use your version of this file under
 * the MPL, indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by the LGPL.
 * If you do not delete the provisions above, a recipient may use your version
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.lowagie.com/iText/
 */

package com.lowagie.text;

/**
 * Interface for a text element.
 * <P>
 * Remark: I looked at the interface javax.swing.text.Element, but I decided to
 * write my own text-classes for two reasons:
 * <OL>
 * <LI>The javax.swing.text-classes may be very generic, I think they are
 * overkill: they are to heavy for what they have to do.
 * <LI>A lot of people using iText (formerly known as rugPdf), still use
 * JDK1.1.x. I try to keep the Java2 requirements limited to the Collection
 * classes (I think they're really great). However, if I use the
 * javax.swing.text classes, it will become very difficult to downgrade rugPdf.
 * </OL>
 * 
 * @see Anchor
 * @see Cell
 * @see Chapter
 * @see Chunk
 * @see Header
 * @see Image
 * @see Jpeg
 * @see List
 * @see ListItem
 * @see Meta
 * @see Paragraph
 * @see Phrase
 * @see Rectangle
 * @see Row
 * @see Section
 * @see Table
 */

public interface Element {

// static membervariables (tables)

	/** This is a possible type of <CODE>Element</CODE>. */
	public static final int CELL = 20;

	/** This is a possible type of <CODE>Element</CODE>. */
	public static final int ROW = 21;

	/** This is a possible type of <CODE>Element</CODE>. */
	public static final int TABLE = 22;

}