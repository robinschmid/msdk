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

package io.github.msdk.featuredetection.adap3d.algorithms;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.github.msdk.MSDKException;
import io.github.msdk.datamodel.RawDataFile;
import io.github.msdk.featuredetection.adap3d.algorithms.CurveTool;
import io.github.msdk.featuredetection.adap3d.algorithms.SliceSparseMatrix;
import io.github.msdk.io.mzxml.MzXMLFileImportMethod;

public class EstimateFwhmMsTest {

  private static RawDataFile rawFile;
  private static CurveTool objCurveTool;

  private static Path getResourcePath(String resource) throws MSDKException {
    final URL url = EstimateFwhmMsTest.class.getClassLoader().getResource(resource);
    try {
      return Paths.get(url.toURI()).toAbsolutePath();
    } catch (URISyntaxException e) {
      throw new MSDKException(e);
    }
  }

  @BeforeClass
  public static void loadData() throws MSDKException {

    // Import the file
    String file = "tiny.mzXML";
    Path path = getResourcePath(file);
    File inputFile = path.toFile();
    Assert.assertTrue("Cannot read test data", inputFile.canRead());
    MzXMLFileImportMethod importer = new MzXMLFileImportMethod(inputFile);
    rawFile = importer.execute();
    SliceSparseMatrix objSliceSparseMatrix = new SliceSparseMatrix(rawFile);
    objCurveTool = new CurveTool(objSliceSparseMatrix);
    Assert.assertNotNull(rawFile);
  }

  @Test
  public void testestimateFwhmMs() throws MSDKException {
    double fwhm = objCurveTool.estimateFwhmMs();
    Assert.assertEquals(0.0027, fwhm, 0.00005);
  }
}
