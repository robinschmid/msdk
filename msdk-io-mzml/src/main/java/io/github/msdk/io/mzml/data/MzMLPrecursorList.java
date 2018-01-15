/*
 * (C) Copyright 2015-2016 by MSDK Development Team
 *
 * This software is dual-licensed under either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1 as published by the Free
 * Software Foundation
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by the Eclipse Foundation.
 */

package io.github.msdk.io.mzml.data;

import java.util.ArrayList;


/**
 * <p>MzMLPrecursorList class.</p>
 *
 */
public class MzMLPrecursorList {

  private ArrayList<MzMLPrecursorElement> precursorElements;

  /**
   * <p>Constructor for MzMLPrecursorList.</p>
   */
  public MzMLPrecursorList() {
    this.precursorElements = new ArrayList<>();
  }

  /**
   * <p>Getter for the field <code>precursorElements</code>.</p>
   *
   * @return a {@link java.util.ArrayList} object.
   */
  public ArrayList<MzMLPrecursorElement> getPrecursorElements() {
    return precursorElements;
  }

  /**
   * <p>addPrecursor.</p>
   *
   * @param precursor a {@link io.github.msdk.io.mzml.data.MzMLPrecursorElement} object.
   */
  public void addPrecursor(MzMLPrecursorElement precursor) {
    precursorElements.add(precursor);
  }
}
