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

package io.github.msdk.io.mzml2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.MSDKException;
import io.github.msdk.MSDKMethod;
import io.github.msdk.datamodel.chromatograms.Chromatogram;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.io.mzml2.data.MzMLParser;
import io.github.msdk.io.mzml2.data.MzMLRawDataFile;
import io.github.msdk.io.mzml2.util.ByteBufferInputStream;
import io.github.msdk.io.mzml2.util.FileMemoryMapper;
import javolution.text.CharArray;
import javolution.xml.internal.stream.XMLStreamReaderImpl;
import javolution.xml.stream.XMLStreamConstants;
import javolution.xml.stream.XMLStreamException;

/**
 * <p>
 * This class contains methods which parse data in MzML format from {@link java.io.File File},
 * {@link java.nio.file.Path Path} or {@link java.io.InputStream InputStream} <br>
 * {@link io.github.msdk.datamodel.rawdata.MsScan Scan}s and
 * {@link io.github.msdk.datamodel.chromatograms.Chromatogram Chromatogram}s to be parsed can be
 * optionally specified using {@link java.util.function.Predicate Predicate}.
 * </p>
 *
 */
public class MzMLFileImportMethod implements MSDKMethod<RawDataFile> {
  private final File mzMLFile;
  final InputStream inputStream;
  private MzMLRawDataFile newRawFile;
  private volatile boolean canceled;
  private Float progress;
  private int lastLoggedProgress;
  private Logger logger;
  private Predicate<MsScan> msScanPredicate;
  private Predicate<Chromatogram> chromatogramPredicate;

  /**
   * <p>
   * Constructor for MzMLFileImportMethod.
   * </p>
   *
   * @param mzMLFilePath a {@link java.lang.String String} which contains the absolute path to the
   *        MzML File.
   */
  public MzMLFileImportMethod(String mzMLFilePath) {
    this(new File(mzMLFilePath), null, null);
  }

  /**
   * <p>
   * Constructor for MzMLFileImportMethod.
   * </p>
   *
   * @param mzMLFilePath a {@link java.lang.String String} which contains the absolute path to the
   *        MzML File.
   * @param msScanPredicate Only {@link io.github.msdk.datamodel.rawdata.MsScan MsScan}s which pass
   *        this predicate will be parsed by the parser and added to the
   *        {@link io.github.msdk.io.mzml2.data.MzMLRawDataFile RawDataFile} returned by the
   *        {@link #getResult() getResult()} method.
   * @param chromatogramPredicate Only {@link io.github.msdk.datamodel.chromatograms.Chromatogram
   *        Chromatogram}s which pass this predicate will be parsed by the parser and added to the
   *        {@link io.github.msdk.io.mzml2.data.MzMLRawDataFile RawDataFile} returned by the
   *        {@link #getResult() getResult()} method.
   */
  public MzMLFileImportMethod(String mzMLFilePath, Predicate<MsScan> msScanPredicate,
      Predicate<Chromatogram> chromatogramPredicate) {
    this(new File(mzMLFilePath), msScanPredicate, chromatogramPredicate);
  }

  /**
   * <p>
   * Constructor for MzMLFileImportMethod.
   * </p>
   *
   * @param mzMLFilePath a {@link java.nio.file.Path Path} object which contains the path to the
   *        MzML File.
   */
  public MzMLFileImportMethod(Path mzMLFilePath) {
    this(mzMLFilePath.toFile(), null, null);
  }

  /**
   * <p>
   * Constructor for MzMLFileImportMethod.
   * </p>
   *
   * @param mzMLFilePath a {@link java.nio.file.Path Path} object which contains the path to the
   *        MzML File.
   * @param msScanPredicate Only {@link io.github.msdk.datamodel.rawdata.MsScan MsScan}s which pass
   *        this predicate will be parsed by the parser and added to the
   *        {@link io.github.msdk.io.mzml2.data.MzMLRawDataFile RawDataFile} returned by the
   *        {@link #getResult() getResult()} method.
   * @param chromatogramPredicate Only {@link io.github.msdk.datamodel.chromatograms.Chromatogram
   *        Chromatogram}s which pass this predicate will be parsed by the parser and added to the
   *        {@link io.github.msdk.io.mzml2.data.MzMLRawDataFile RawDataFile} returned by the
   *        {@link #getResult() getResult()} method.
   */
  public MzMLFileImportMethod(Path mzMLFilePath, Predicate<MsScan> msScanPredicate,
      Predicate<Chromatogram> chromatogramPredicate) {
    this(mzMLFilePath.toFile(), msScanPredicate, chromatogramPredicate);
  }

  /**
   * <p>
   * Constructor for MzMLFileImportMethod.
   * </p>
   *
   * @param mzMLFile a {@link java.io.File File} object instance of the MzML File.
   */
  public MzMLFileImportMethod(File mzMLFile) {
    this(mzMLFile, null, null, null);
  }

  /**
   * <p>
   * Constructor for MzMLFileImportMethod.
   * </p>
   *
   * @param mzMLFile a {@link java.io.File File} object instance of the MzML File.
   * 
   * @param msScanPredicate Only {@link io.github.msdk.datamodel.rawdata.MsScan MsScan}s which pass
   *        this predicate will be parsed by the parser and added to the
   *        {@link io.github.msdk.io.mzml2.data.MzMLRawDataFile RawDataFile} returned by the
   *        {@link #getResult() getResult()} method.
   * @param chromatogramPredicate Only {@link io.github.msdk.datamodel.chromatograms.Chromatogram
   *        Chromatogram}s which pass this predicate will be parsed by the parser and added to the
   *        {@link io.github.msdk.io.mzml2.data.MzMLRawDataFile RawDataFile} returned by the
   *        {@link #getResult() getResult()} method.
   */
  public MzMLFileImportMethod(File mzMLFile, Predicate<MsScan> msScanPredicate,
      Predicate<Chromatogram> chromatogramPredicate) {
    this(mzMLFile, null, msScanPredicate, chromatogramPredicate);
  }

  /**
   * <p>
   * Constructor for MzMLFileImportMethod.
   * </p>
   *
   * @param inputStream an {@link java.io.InputStream InputStream} which contains data in MzML
   *        format.
   */
  public MzMLFileImportMethod(InputStream inputStream) {
    this(null, inputStream, null, null);
  }

  /**
   * <p>
   * Constructor for MzMLFileImportMethod.
   * </p>
   *
   * @param inputStream an {@link java.io.InputStream InputStream} which contains data in MzML
   *        format.
   * @param msScanPredicate Only {@link io.github.msdk.datamodel.rawdata.MsScan MsScan}s which pass
   *        this predicate will be parsed by the parser and added to the
   *        {@link io.github.msdk.io.mzml2.data.MzMLRawDataFile RawDataFile} returned by the
   *        {@link #getResult() getResult()} method.
   * @param chromatogramPredicate Only {@link io.github.msdk.datamodel.chromatograms.Chromatogram
   *        Chromatogram}s which pass this predicate will be parsed by the parser and added to the
   *        {@link io.github.msdk.io.mzml2.data.MzMLRawDataFile RawDataFile} returned by the
   *        {@link #getResult() getResult()} method.
   */
  public MzMLFileImportMethod(InputStream inputStream, Predicate<MsScan> msScanPredicate,
      Predicate<Chromatogram> chromatogramPredicate) {
    this(null, inputStream, msScanPredicate, chromatogramPredicate);
  }

  /**
   * <p>
   * Internal constructor used to initialize instances of this object using other constructors.
   * </p>
   */
  private MzMLFileImportMethod(File mzMLFile, InputStream inputStream,
      Predicate<MsScan> msScanPredicate, Predicate<Chromatogram> chromatogramPredicate) {
    this.mzMLFile = mzMLFile;
    this.inputStream = inputStream;
    this.canceled = false;
    this.progress = 0f;
    this.lastLoggedProgress = 0;
    this.logger = LoggerFactory.getLogger(this.getClass());
    this.msScanPredicate = msScanPredicate != null ? msScanPredicate : s -> true;
    this.chromatogramPredicate = chromatogramPredicate != null ? chromatogramPredicate : c -> true;
  }

  /**
   * <p>
   * Parse the MzML data and return the parsed data
   * </p>
   *
   * @return a {@link io.github.msdk.io.mzml2.data.MzMLRawDataFile MzMLRawDataFile} object
   *         containing the parsed data
   */
  @Override
  public MzMLRawDataFile execute() throws MSDKException {

    try {

      InputStream is = null;

      if (mzMLFile != null) {
        logger.info("Began parsing file: " + mzMLFile.getAbsolutePath());
        is = FileMemoryMapper.mapToMemory(mzMLFile);
      } else if (inputStream != null) {
        logger.info("Began parsing file from stream");
        is = inputStream;
      } else {
        throw new MSDKException("Invalid input");
      }
      // It's ok to directly create this particular reader, this class is `public final`
      // and we precisely want that fast UFT-8 reader implementation
      final XMLStreamReaderImpl xmlStreamReader = new XMLStreamReaderImpl();
      xmlStreamReader.setInput(is, "UTF-8");

      MzMLParser parser = new MzMLParser(this);
      this.newRawFile = parser.getMzMLRawFile();

      lastLoggedProgress = 0;

      int eventType;
      try {
        do {
          // check if parsing has been cancelled?
          if (canceled)
            return null;

          eventType = xmlStreamReader.next();

          // XXX Can't track progress this way now, switched to using the primitive InputStream
          // without the length() function
          // Update: We can track progress if source is a file
          if (mzMLFile != null)
            progress = ((float) (xmlStreamReader.getLocation().getCharacterOffset())
                / ((ByteBufferInputStream) is).length());

          // Log progress after every 10% completion
          if ((int) (progress * 100) >= lastLoggedProgress + 10) {
            lastLoggedProgress = (int) (progress * 10) * 10;
            logger.debug("Parsing in progress... " + lastLoggedProgress + "% completed");
          }

          switch (eventType) {
            case XMLStreamConstants.START_ELEMENT:
              final CharArray openingTagName = xmlStreamReader.getLocalName();
              parser.processOpeningTag(xmlStreamReader, is, openingTagName);
              break;

            case XMLStreamConstants.END_ELEMENT:
              final CharArray closingTagName = xmlStreamReader.getLocalName();
              parser.processClosingTag(xmlStreamReader, closingTagName);
              break;

            case XMLStreamConstants.CHARACTERS:
              parser.processCharacters(xmlStreamReader);
              break;
          }

        } while (eventType != XMLStreamConstants.END_DOCUMENT);

      } finally {
        if (xmlStreamReader != null)
          xmlStreamReader.close();
      }
      progress = 1f;
      logger.info("Parsing Complete");
    } catch (IOException | XMLStreamException e) {
      throw (new MSDKException(e));
    }

    progress = 1f;
    return newRawFile;
  }


  /** {@inheritDoc} */
  @Override
  public Float getFinishedPercentage() {
    return progress;
  }

  /** {@inheritDoc} */
  @Override
  public RawDataFile getResult() {
    return newRawFile;
  }

  /** {@inheritDoc} */
  @Override
  public void cancel() {
    this.canceled = true;
  }

  /**
   * @return {@link java.util.function.Predicate Predicate} specified for
   *         {@link io.github.msdk.datamodel.rawdata.MsScan MsScan}s <br>
   *         The {@link java.util.function.Predicate Predicate} evaluates to true always, if it
   *         wasn't specified on initialization
   */
  public Predicate<MsScan> getMsScanPredicate() {
    return msScanPredicate;
  }

  /**
   * @return {@link java.util.function.Predicate Predicate} specified for
   *         {@link io.github.msdk.datamodel.chromatograms.Chromatogram Chromatogram}s <br>
   *         The {@link java.util.function.Predicate Predicate} evaluates to true always, if it
   *         wasn't specified on initialization
   */
  public Predicate<Chromatogram> getChromatogramPredicate() {
    return chromatogramPredicate;
  }

  /**
   * 
   * @return a {@link java.io.File File} instance of the MzML source if being read from a file <br>
   *         null if the MzML source is an {@link java.io.InputStream InputStream}
   */
  public File getMzMLFile() {
    return mzMLFile;
  }

}