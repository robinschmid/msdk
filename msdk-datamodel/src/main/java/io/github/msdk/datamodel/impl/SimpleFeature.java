/*
 * (C) Copyright 2015-2017 by MSDK Development Team
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

package io.github.msdk.datamodel.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.msdk.datamodel.chromatograms.Chromatogram;
import io.github.msdk.datamodel.features.Feature;
import io.github.msdk.datamodel.ionannotations.IonAnnotation;
import io.github.msdk.datamodel.rawdata.MsScan;

/**
 * Implementation of the Feature interface.
 *
 * @author plusik
 * @version $Id: $Id
 * @since 0.0.8
 */
public class SimpleFeature implements Feature {

  private @Nonnull final Double mz;
  private @Nonnull final Float retentionTime;
  private @Nullable Float area, height, snRatio, score;
  private @Nullable Chromatogram chromatogram;
  private @Nullable List<MsScan> msmsSpectra;
  private @Nullable IonAnnotation ionAnnotation;

  /**
   * <p>Constructor for SimpleFeature.</p>
   *
   * @param mz a {@link java.lang.Double} object.
   * @param retentionTime a {@link java.lang.Float} object.
   */
  public SimpleFeature(@Nonnull Double mz, @Nonnull Float retentionTime) {
    this.mz = mz;
    this.retentionTime = retentionTime;
  }


  /** {@inheritDoc} */
  @Override
  public Double getMz() {
    return mz;
  }

  /** {@inheritDoc} */
  @Override
  public Float getRetentionTime() {
    return retentionTime;
  }

  /** {@inheritDoc} */
  @Override
  public Float getArea() {
    return area;
  }

  /** {@inheritDoc} */
  @Override
  public Float getHeight() {
    return height;
  }

  /** {@inheritDoc} */
  @Override
  public Float getSNRatio() {
    return snRatio;
  }

  /** {@inheritDoc} */
  @Override
  public Float getScore() {
    return score;
  }

  /** {@inheritDoc} */
  @Override
  public Chromatogram getChromatogram() {
    return chromatogram;
  }

  /** {@inheritDoc} */
  @Override
  public List<MsScan> getMSMSSpectra() {
    return msmsSpectra;
  }

  /** {@inheritDoc} */
  @Override
  public IonAnnotation getIonAnnotation() {
    return ionAnnotation;
  }


  /**
   * <p>Setter for the field <code>area</code>.</p>
   *
   * @param area a {@link java.lang.Float} object.
   */
  public void setArea(Float area) {
    this.area = area;
  }

  /**
   * <p>Setter for the field <code>height</code>.</p>
   *
   * @param height a {@link java.lang.Float} object.
   */
  public void setHeight(Float height) {
    this.height = height;
  }

  /**
   * <p>setSNRatio.</p>
   *
   * @param snRatio a {@link java.lang.Float} object.
   */
  public void setSNRatio(Float snRatio) {
    this.snRatio = snRatio;
  }

  /**
   * <p>Setter for the field <code>score</code>.</p>
   *
   * @param score a {@link java.lang.Float} object.
   */
  public void setScore(Float score) {
    this.score = score;
  }

  /**
   * <p>Setter for the field <code>chromatogram</code>.</p>
   *
   * @param chromatogram a {@link io.github.msdk.datamodel.chromatograms.Chromatogram} object.
   */
  public void setChromatogram(Chromatogram chromatogram) {
    this.chromatogram = chromatogram;
  }

  /**
   * <p>setMSMSSpectra.</p>
   *
   * @param msmsSpectra a {@link java.util.List} object.
   */
  public void setMSMSSpectra(List<MsScan> msmsSpectra) {
    this.msmsSpectra = msmsSpectra;
  }

  /**
   * <p>Setter for the field <code>ionAnnotation</code>.</p>
   *
   * @param ionAnnotation a {@link io.github.msdk.datamodel.ionannotations.IonAnnotation} object.
   */
  public void setIonAnnotation(IonAnnotation ionAnnotation) {
    this.ionAnnotation = ionAnnotation;
  }
  
}